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
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.queries.function.FunctionValues;
import org.apache.lucene.queries.function.ValueSource;
import org.apache.lucene.search.SimpleCollector;
import org.apache.lucene.util.mutable.MutableValue;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.handler.component.ResponseBuilder;
import org.apache.solr.handler.component.SearchComponent;
import org.apache.solr.schema.IndexSchema;
import org.apache.solr.schema.SchemaField;
import org.apache.solr.search.DocListAndSet;

/*
 * 按照店铺区域来统计消费<br>
 * http://k.2dfire.net/pages/viewpage.action?pageId=70090916
 *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class AreaConsumeStatisComponent extends SearchComponent {

    public static final String COMPONENT_NAME = "area_consume_statis_component";

    @Override
    public void prepare(ResponseBuilder rb) throws IOException {
        if (rb.req.getParams().getBool(PairValSummary.FIELD_MAP_REDUCE, false)) {
            rb.setNeedDocSet(true);
        }
    }

    private static final String GROUP_BY_KEY = "group_by";

    private static final String GROUP_BY_AREA_ID = "area_id";

    private static final String GROUP_BY_SEAT_ID = "seat_id";

    @Override
    public void process(ResponseBuilder rb) throws IOException {
        if (!rb.req.getParams().getBool(COMPONENT_NAME, false)) {
            return;
        }
        String groupByKey = getGroupByKey(rb.req.getParams());
        IndexSchema schema = rb.req.getSchema();
        DocValues docValues = new DocValues(groupByKey);
        // docValues.groupByKey = new FieldColumn(schema.getField(groupByKey));
        docValues.areaId = new FieldColumn(schema.getField(GROUP_BY_AREA_ID));
        docValues.seatId = new FieldColumn(schema.getField(GROUP_BY_SEAT_ID));
        docValues.peopleCount = new FieldColumn(schema.getField("people_count"));
        docValues.receiveAmount = new FieldColumn(schema.getField("recieve_amount"));
        docValues.resultAmount = new FieldColumn(schema.getField("result_amount"));
        docValues.sourceAmount = new FieldColumn(schema.getField("source_amount"));
        DocListAndSet results = rb.getResults();
        JSONFloatPairCollector collector = new JSONFloatPairCollector(docValues);
        rb.req.getSearcher().search(results.docSet.getTopFilter(), collector);
        rb.rsp.add("all_area_statis", ResultUtils.writeMap(docValues.summaryStatis));
    }

    private String getGroupByKey(SolrParams params) {
        String groupBy = params.get(GROUP_BY_KEY);
        if (GROUP_BY_AREA_ID.equals(groupBy) || GROUP_BY_SEAT_ID.equals(groupBy)) {
            return groupBy;
        }
        throw new IllegalArgumentException(GROUP_BY_KEY + ":" + groupBy + " is not illegal");
    }

    private static class JSONFloatPairCollector extends SimpleCollector {

        private final DocValues docValues;

        public JSONFloatPairCollector(DocValues docValues) {
            super();
            this.docValues = docValues;
        }

        @Override
        public boolean needsScores() {
            return false;
        }

        protected void doSetNextReader(LeafReaderContext context) throws IOException {
            docValues.doSetNextReader(context);
        }

        @Override
        public void collect(int doc) throws IOException {
            docValues.fillValue(doc);
            docValues.addIncrease();
        }
    }

    private static class SummaryStatis {

        private final String groupKey;

        // 总单数
        private int allPayCount;

        // 客流量,总人数
        private int peopleCount;

        // 应收总额
        private float allResultAmount;

        // 实收总额
        private float allReceiveAmount;

        private float allSourceAmount;

        /**
         * @param groupKey
         */
        public SummaryStatis(String groupKey) {
            super();
            this.groupKey = groupKey;
        }

        @Override
        public String toString() {
            return this.write();
        }

        public String write() {
            return ("groupKey:\"" + groupKey + "\",allPayCount:" + allPayCount + ",peopleCount:" + peopleCount + ",allResultAmount:" + allResultAmount + ",allReceiveAmount:" + allReceiveAmount + ",sourceAmount:" + this.allSourceAmount);
        }

        private void addIncrease(DocValues val) {
            this.allResultAmount += val.getResultAmount();
            this.allReceiveAmount += val.getReceiveAmount();
            allPayCount++;
            peopleCount += val.getPeopleCount();
            this.allSourceAmount += val.getSourceAmount();
        }
    }

    public static class FieldColumn {

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

        public FieldColumn(SchemaField field) {
            if (field == null) {
                throw new IllegalArgumentException("field can not be null");
            }
            // this.field = field;
            this.valueSource = field.getType().getValueSource(field, null);
        }

        /**
         * @return
         */
        public int getInt(int docid) {
            String val = getString(docid);
            if (StringUtils.isBlank(val)) {
                return 0;
            }
            return Integer.parseInt(val);
        }

        /**
         * @return
         */
        public float getFloatValue(int docid) {
            String val = getString(docid);
            if (StringUtils.isBlank(val)) {
                return 0;
            }
            return Float.parseFloat(val);
        }

        /**
         * @return
         */
        public String getString(int docId) {
            this.filler.fillValue(docId);
            if (!this.val.exists) {
                return null;
            }
            return this.val.toString();
        }
    }

    private static class DocValues {

        // private FieldColumn groupByKey;
        private final String groupKey;

        public DocValues(String groupKey) {
            super();
            this.groupKey = groupKey;
        }

        private FieldColumn areaId;

        private FieldColumn seatId;

        private FieldColumn resultAmount;

        private FieldColumn receiveAmount;

        private FieldColumn peopleCount;

        private FieldColumn sourceAmount;

        private final Map<String, SummaryStatis> /* group key */
        summaryStatis = new HashMap<String, /*
																									 * group
																									 * key
																									 */
        SummaryStatis>();

        private void addIncrease() {
            String groupKey = this.getGroupByKey();
            if (StringUtils.isBlank(groupKey)) {
                return;
            }
            SummaryStatis statis = summaryStatis.get(groupKey);
            if (statis == null) {
                statis = new SummaryStatis(groupKey);
                summaryStatis.put(groupKey, statis);
            }
            statis.addIncrease(this);
        }

        private void doSetNextReader(LeafReaderContext context) {
            resultAmount.doSetNextReader(context);
            receiveAmount.doSetNextReader(context);
            peopleCount.doSetNextReader(context);
            // groupByKey.doSetNextReader(context);
            areaId.doSetNextReader(context);
            seatId.doSetNextReader(context);
            sourceAmount.doSetNextReader(context);
        }

        private void fillValue(int doc) {
            resultAmount.filler.fillValue(doc);
            receiveAmount.filler.fillValue(doc);
            peopleCount.filler.fillValue(doc);
            // groupByKey.filler.fillValue(doc);
            areaId.filler.fillValue(doc);
            seatId.filler.fillValue(doc);
            sourceAmount.filler.fillValue(doc);
        }

        // 如果是零售的在数据库表上是没有seatid的 ，这里自己定义一个默认码标示 seatid 或者 areaid为空
        private static final String LING_SHOU_KEY = "9999";

        private String getGroupByKey() {
            String areaid = this.getString(this.areaId);
            if (areaid == null) {
                return LING_SHOU_KEY;
            }
            String seatid = this.getString(this.seatId);
            if (seatid == null) {
                return LING_SHOU_KEY;
            }
            if (GROUP_BY_AREA_ID.equals(this.groupKey)) {
                return areaid;
            } else if (GROUP_BY_SEAT_ID.equals(this.groupKey)) {
                return seatid;
            }
            throw new IllegalStateException("groupKey:" + groupKey + " is not illegal");
        }

        public float getSourceAmount() {
            return this.getFloatValue(this.sourceAmount);
        }

        public float getResultAmount() {
            return getFloatValue(this.resultAmount);
        }

        public float getReceiveAmount() {
            return getFloatValue(this.receiveAmount);
        }

        public int getPeopleCount() {
            return (int) getFloatValue(this.peopleCount);
        }

        /**
         * @return
         */
        private float getFloatValue(FieldColumn field) {
            if (!field.val.exists) {
                return 0;
            }
            return Float.parseFloat(field.val.toString());
        }

        /**
         * @return
         */
        private String getString(FieldColumn field) {
            if (!field.val.exists) {
                return null;
            }
            return field.val.toString();
        }
    }

    @Override
    public String getDescription() {
        return COMPONENT_NAME;
    }
}
