/* 
 * The MIT License
 *
 * Copyright (c) 2018-2022, qinglangtech Ltd
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.qlangtech.tis.solrextend.handler.component;

import java.io.IOException;
import java.util.Collections;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.queries.function.FunctionValues;
import org.apache.lucene.queries.function.ValueSource;
import org.apache.lucene.search.SimpleCollector;
import org.apache.lucene.util.mutable.MutableValue;
import org.apache.solr.handler.component.ResponseBuilder;
import org.apache.solr.handler.component.SearchComponent;
import org.apache.solr.schema.IndexSchema;
import org.apache.solr.schema.SchemaField;
import org.apache.solr.search.DocListAndSet;

/*
 * 营业汇总表统计<br>
 * http://k.2dfire.net/pages/viewpage.action?pageId=70090916
 *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class AllConsumeDimStatisComponent extends SearchComponent {

    public static final String COMPONENT_NAME = "all_consume_dim_statis_component";

    @Override
    public void prepare(ResponseBuilder rb) throws IOException {
        if (rb.req.getParams().getBool(PairValSummary.FIELD_MAP_REDUCE, false)) {
            rb.setNeedDocSet(true);
        }
    }

    @Override
    public void process(ResponseBuilder rb) throws IOException {
        if (!rb.req.getParams().getBool(COMPONENT_NAME, false)) {
            return;
        }
        IndexSchema schema = rb.req.getSchema();
        DocValues docValues = new DocValues();
        docValues.resultAmountColumn = new FieldColumn(schema.getField("result_amount"));
        docValues.sourceAmountColumn = new FieldColumn(schema.getField("source_amount"));
        docValues.discountAmount = new FieldColumn(schema.getField("discount_amount"));
        docValues.specialFeeSummary = new FieldColumn(schema.getField("special_fee_summary"));
        docValues.receiveAmount = new FieldColumn(schema.getField("recieve_amount"));
        docValues.peopleCount = new FieldColumn(schema.getField("people_count"));
        DocListAndSet results = rb.getResults();
        JSONFloatPairCollector collector = new JSONFloatPairCollector(docValues);
        rb.req.getSearcher().search(results.docSet.getTopFilter(), collector);
        rb.rsp.add("all_dim_statis", collector.summary.write());
    }

    private static class JSONFloatPairCollector extends SimpleCollector {

        private final DocValues docValues;

        private final SummaryStatis summary;

        public JSONFloatPairCollector(DocValues docValues) {
            super();
            this.docValues = docValues;
            this.summary = new SummaryStatis();
        }

        public boolean needsScores() {
            return false;
        }

        protected void doSetNextReader(LeafReaderContext context) throws IOException {
            docValues.doSetNextReader(context);
        }

        @Override
        public void collect(int doc) throws IOException {
            docValues.fillValue(doc);
            this.summary.addIncrease(docValues);
        }
    }

    // 损益额
    private static final String SPECIAL_FEE_TYPE_LOSS = "3";

    // 服务费
    private static final String SPECIAL_FEE_SErVICE = "1";

    private static class SummaryStatis {

        // 总营业额
        private final TisMoney sourceAmount = TisMoney.create();

        private final TisMoney resultAmount = TisMoney.create();

        // 总折扣额度
        private final TisMoney discountAmount = TisMoney.create();

        // 损益额度 specialfee.fee & kind=3
        private final TisMoney loss = TisMoney.create();

        // 实收额
        private final TisMoney receiveAmount = TisMoney.create();

        // specialfee.fee & kind=1
        private final TisMoney serviceFee = TisMoney.create();

        // 总单数
        private int allPayCount;

        // 客流量
        private int peopleCount;

        // 优惠总额
        private final TisMoney couponDiscount = TisMoney.create();

        // @Override
        public String write() throws IOException {
            return "sourceAmount:" + sourceAmount + ",discountAmount:" + discountAmount + ",loss:" + loss + ",receiveAmount:" + receiveAmount + ",serviceFee:" + serviceFee + ",allPayCount:" + allPayCount + ",peopleCount:" + peopleCount + ",resultAmount:" + resultAmount + ",couponDiscount:" + couponDiscount;
        }

        private void addIncrease(DocValues val) {
            // =
            sourceAmount.add(val.getSourceAmountColumn());
            // sourceAmount.add();
            // =
            discountAmount.add(val.getDiscountAmount());
            // discountAmount.add();
            // = receiveAmount.add();
            receiveAmount.add(val.getReceiveAmount());
            allPayCount++;
            peopleCount += val.getPeopleCount();
            // = resultAmount.add();
            resultAmount.add(val.getResultAmount());
            if (StringUtils.isNotBlank(val.getSpecialFeeSummary())) {
                String[] kindFeeArry = StringUtils.split(val.getSpecialFeeSummary(), ";");
                String[] findFee = null;
                String kind = null;
                TisMoney fee;
                for (String kindFee : kindFeeArry) {
                    findFee = StringUtils.split(kindFee, "_");
                    if (findFee.length < 2) {
                        continue;
                    }
                    kind = findFee[0];
                    // new BigDecimal(,
                    fee = TisMoney.create(findFee[1]);
                    if (SPECIAL_FEE_TYPE_LOSS.equals(kind)) {
                        // = this.loss.add(fee);
                        this.loss.add(fee);
                    } else if (SPECIAL_FEE_SErVICE.equals(kind)) {
                        // = serviceFee.add(fee);
                        this.serviceFee.add(fee);
                    }
                }
            }
        }
    }

    public static class FieldColumn {

        @SuppressWarnings("unused")
        private final SchemaField field;

        private FunctionValues.ValueFiller filler;

        private MutableValue val;

        private ValueSource valueSource;

        public void doSetNextReader(LeafReaderContext context) {
            try {
                FunctionValues funcValues = valueSource.getValues(Collections.emptyMap(), context);
                filler = funcValues.getValueFiller();
                val = filler.getValue();
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        }

        public MutableValue getValue(int docid) {
            this.filler.fillValue(docid);
            return this.val;
        }

        public FieldColumn(SchemaField field) {
            if (field == null) {
                throw new IllegalArgumentException("field can not be null");
            }
            this.field = field;
            this.valueSource = field.getType().getValueSource(field, null);
        }

        public float getFloat(int docid) {
            String val = getString(docid);
            if (StringUtils.isBlank(val)) {
                return 0;
            }
            return Float.parseFloat(val);
        }

        public int getInt(int docid) {
            String val = getString(docid);
            if (StringUtils.isBlank(val)) {
                return 0;
            }
            return Integer.parseInt(val);
        }

        public String getString(int docId) {
            this.filler.fillValue(docId);
            if (!this.val.exists) {
                return null;
            }
            return this.val.toString();
        }
    }

    private static class DocValues {

        private FieldColumn resultAmountColumn;

        private FieldColumn sourceAmountColumn;

        private FieldColumn discountAmount;

        private FieldColumn specialFeeSummary;

        private FieldColumn receiveAmount;

        private FieldColumn peopleCount;

        private void doSetNextReader(LeafReaderContext context) {
            sourceAmountColumn.doSetNextReader(context);
            discountAmount.doSetNextReader(context);
            specialFeeSummary.doSetNextReader(context);
            receiveAmount.doSetNextReader(context);
            peopleCount.doSetNextReader(context);
            resultAmountColumn.doSetNextReader(context);
        }

        private void fillValue(int doc) {
            sourceAmountColumn.filler.fillValue(doc);
            discountAmount.filler.fillValue(doc);
            specialFeeSummary.filler.fillValue(doc);
            receiveAmount.filler.fillValue(doc);
            peopleCount.filler.fillValue(doc);
            this.resultAmountColumn.filler.fillValue(doc);
        }

        public TisMoney getResultAmount() {
            return getFloatValue(this.resultAmountColumn);
        }

        public TisMoney getSourceAmountColumn() {
            return getFloatValue(this.sourceAmountColumn);
        }

        public TisMoney getDiscountAmount() {
            return getFloatValue(this.discountAmount);
        }

        // kind1_fee1;kind2_fee2
        public String getSpecialFeeSummary() {
            if (!this.specialFeeSummary.val.exists) {
                return null;
            }
            return specialFeeSummary.val.toString();
        }

        public TisMoney getReceiveAmount() {
            return getFloatValue(this.receiveAmount);
        }

        public int getPeopleCount() {
            return geIntValue(this.peopleCount);
        }

        private int geIntValue(FieldColumn field) {
            if (!field.val.exists) {
                return 0;
            }
            try {
                // new
                return Integer.parseInt(field.val.toString());
            // BigDecimal(field.val.toString(),
            // BigDecimalContext);
            } catch (Throwable e) {
                return 0;
            }
        // Float.parseFloat();
        }

        /**
         * @return
         */
        private TisMoney getFloatValue(FieldColumn field) {
            if (!field.val.exists) {
                return TisMoney.create();
            }
            try {
                // new
                return TisMoney.create(field.val.toString());
            // BigDecimal(field.val.toString(),
            // BigDecimalContext);
            } catch (Throwable e) {
                return TisMoney.create();
            }
        // Float.parseFloat();
        }
    }

    @Override
    public String getDescription() {
        return COMPONENT_NAME;
    }
}
