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
package com.qlangtech.tis.solrextend.handler.component.s4supplyStockChangeLog;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.lucene.index.DocValues;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.index.NumericDocValues;
import org.apache.lucene.index.SortedDocValues;
import org.apache.lucene.queries.function.FunctionValues;
import org.apache.lucene.queries.function.ValueSource;
import org.apache.lucene.search.SimpleCollector;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.handler.component.ResponseBuilder;
import org.apache.solr.handler.component.SearchComponent;
import org.apache.solr.schema.IndexSchema;
import org.apache.solr.schema.SchemaField;
import com.qlangtech.tis.solrextend.handler.component.TisMoney;

/*
 * 供应链的一个统计需求
 *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class WarehouseStatComponent extends SearchComponent {

    public static final String COMPONENT_NAME = "warehouse-stat";

    private static final String SELF_ENTITY_ID = "self_entity_id";

    private static final String WAREHOUSE_ID = "warehouse_id";

    private static final String GOODS_ID = "goods_id";

    private static final String CHANGE_NUM = "change_num";

    private static final String UNIT_CONVERSION = "unit_conversion";

    @Override
    public void prepare(ResponseBuilder responseBuilder) throws IOException {
        if (responseBuilder.req.getParams().getBool(COMPONENT_NAME, false)) {
            responseBuilder.setNeedDocSet(true);
        }
    }

    @Override
    public void process(ResponseBuilder responseBuilder) throws IOException {
        if (!responseBuilder.req.getParams().getBool(COMPONENT_NAME, false)) {
            return;
        }
        IndexSchema schema = responseBuilder.req.getSchema();
        SchemaField changeNumField = schema.getField(CHANGE_NUM);
        ValueSource changeNumValueSource = changeNumField.getType().getValueSource(changeNumField, null);
        TimeIntervalStatisCollector collector = new TimeIntervalStatisCollector(changeNumValueSource);
        responseBuilder.req.getSearcher().search(responseBuilder.getResults().docSet.getTopFilter(), collector);
        responseBuilder.rsp.add("change_num_statis_sum", collector.write());
    }

    /**
     * Created by lingxiao on 2016/7/19.
     */
    private class TimeIntervalStatisCollector extends SimpleCollector {

        private SortedDocValues selfEntityIdDV;

        private SortedDocValues warehouseIdDV;

        private SortedDocValues goodsIdDV;

        private final ValueSource changeNumber;

        private NumericDocValues unitConversion;

        private FunctionValues changeNumberFuncVal;

        private final Map<String, BigDecimal> statisticResult = new HashMap<>();

        public static final int MAX_COLLECT_COUNT = 30000;

        private final AtomicInteger collectCount = new AtomicInteger();

        private TimeIntervalStatisCollector(ValueSource changeNumValueSource) {
            this.changeNumber = changeNumValueSource;
        }

        @Override
        protected void doSetNextReader(LeafReaderContext context) {
            try {
                this.selfEntityIdDV = DocValues.getSorted(context.reader(), SELF_ENTITY_ID);
                this.warehouseIdDV = DocValues.getSorted(context.reader(), WAREHOUSE_ID);
                this.goodsIdDV = DocValues.getSorted(context.reader(), GOODS_ID);
                this.unitConversion = DocValues.getNumeric(context.reader(), UNIT_CONVERSION);
                this.changeNumberFuncVal = this.changeNumber.getValues(Collections.emptyMap(), context);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public boolean needsScores() {
            return false;
        }

        private String getKey(int doc) throws IOException {
            return (new StringBuffer(this.selfEntityIdDV.get(doc).utf8ToString())).append("_").append(this.warehouseIdDV.get(doc).utf8ToString()).append("_").append(this.goodsIdDV.get(doc).utf8ToString()).toString();
        }

        @Override
        public void collect(int doc) throws IOException {
            if (collectCount.incrementAndGet() > MAX_COLLECT_COUNT) {
                throw new IllegalStateException("you have exceed maxCollectCount:" + MAX_COLLECT_COUNT);
            }
            float changeNumber = this.changeNumberFuncVal.floatVal(doc);
            String key = getKey(doc);
            BigDecimal sum = new BigDecimal(String.valueOf(changeNumber));
            long uc = unitConversion.get(doc);
            if (uc != 0) {
                sum = sum.multiply(new BigDecimal(String.valueOf(Float.intBitsToFloat((int) uc))));
            }
            // 
            // if (sum == null) {
            // sum = ;
            // statisticResult.put(key, sum);
            // }
            BigDecimal preSum = statisticResult.get(key);
            if (preSum != null) {
                sum = sum.add(preSum);
            }
            statisticResult.put(key, sum);
        }

        public NamedList<String> write() {
            NamedList<String> result = new NamedList<String>();
            for (Map.Entry<String, BigDecimal> entry : statisticResult.entrySet()) {
                result.add(entry.getKey(), String.valueOf(entry.getValue().floatValue()));
            }
            return result;
        }
    }

    @Override
    public String getDescription() {
        return COMPONENT_NAME;
    }
}
