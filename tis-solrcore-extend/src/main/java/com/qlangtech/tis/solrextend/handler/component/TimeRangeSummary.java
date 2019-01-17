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
import java.net.URLEncoder;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
 * 根据传入的参数将，时段内的值进行聚合
 *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class TimeRangeSummary extends SearchComponent {

    public static final String COMPONENT_NAME = "timeRangeSummary";

    // exp:0600-0900,0900-1200,1200-1400
    public static final String FIELD_TIME_RANGE_MAP_REDUCE = "timeRange";

    @Override
    public void prepare(ResponseBuilder rb) throws IOException {
        if (rb.req.getParams().getBool(COMPONENT_NAME, false)) {
            // rb.setNeedDocList(true);
            rb.setNeedDocSet(true);
        }
    }

    @Override
    public void process(ResponseBuilder rb) throws IOException {
        if (!rb.req.getParams().getBool(COMPONENT_NAME, false)) {
            return;
        }
        SolrParams params = rb.req.getParams();
        IndexSchema schema = rb.req.getSchema();
        String fieldName = params.get(FIELD_TIME_RANGE_MAP_REDUCE);
        if (StringUtils.isEmpty(fieldName)) {
            throw new IllegalStateException("param fieldName can not be null");
        }
        // 初始化统计的筛子
        TimeRanageSummary timeRangeSummary = new TimeRanageSummary();
        String[] timeRangeAry = StringUtils.split(fieldName, ",");
        for (String range : timeRangeAry) {
            timeRangeSummary.put(new TimeRange(range), new AggreValue());
        }
        // ==============================================================
        SchemaField timeRangefield = schema.getField("operate_date");
        SchemaField peopleCountFiled = schema.getField("people_count");
        // 应收
        SchemaField resultAmountField = schema.getField("result_amount");
        // 实收
        SchemaField recieveAmountField = schema.getField("recieve_amount");
        SchemaField sourceAmountField = schema.getField("source_amount");
        ValueSource timeRangeVS = timeRangefield.getType().getValueSource(timeRangefield, null);
        ValueSource peopCount = peopleCountFiled.getType().getValueSource(peopleCountFiled, null);
        // 应收账款
        ValueSource resultAmount = resultAmountField.getType().getValueSource(resultAmountField, null);
        // 实收账款
        ValueSource reciveAmount = recieveAmountField.getType().getValueSource(recieveAmountField, null);
        ValueSource sourceAmount = sourceAmountField.getType().getValueSource(sourceAmountField, null);
        DocListAndSet results = rb.getResults();
        JSONFloatPairCollector collector = new JSONFloatPairCollector(timeRangeVS, peopCount, resultAmount, reciveAmount, sourceAmount, timeRangeSummary, Collections.emptyMap());
        rb.req.getSearcher().search(results.docSet.getTopFilter(), collector);
        // for (Map.Entry<String, AtomicDouble> entry : collector.summary
        // .entrySet()) {
        // System.out.println(entry.getKey() + ":" + entry.getValue());
        // }
        // Map<String, Double> result = new HashMap<>();
        rb.rsp.add("time_range_mr", ResultUtils.writeMap(collector.timeRangeSummary));
    }

    private class TimeRanageSummary extends HashMap<TimeRange, AggreValue> {

        private static final long serialVersionUID = 1L;

        public AggreValue findAggreValue(String hourMin) {
            for (Map.Entry<TimeRange, AggreValue> timeRange : this.entrySet()) {
                if (timeRange.getKey().isContain(hourMin)) {
                    return timeRange.getValue();
                }
            }
            return null;
        }
    }

    // 聚合之后的值
    private static class AggreValue {

        private int peopleCount = 0;

        private float receiveAmount = 0;

        private float resultAmount = 0;

        // 单数
        private int allPayCount;

        private float sourceAmount = 0;

        public void addSourceAmount(float sourceAmount) {
            this.sourceAmount += sourceAmount;
        }

        public void addPeopleCount(int peopleCount) {
            allPayCount++;
            this.peopleCount += peopleCount;
        }

        public void addReceiveAmount(float receiveAmount) {
            this.receiveAmount += receiveAmount;
        }

        public void addResultAmount(float resultAmount) {
            this.resultAmount += resultAmount;
        }

        @Override
        public String toString() {
            return peopleCount + "_" + allPayCount + "_" + resultAmount + "_" + receiveAmount + "_" + sourceAmount;
        }
    }

    private static class JSONFloatPairCollector extends SimpleCollector {

        private final ValueSource timeRage;

        private final ValueSource peopCount;

        private final ValueSource resultAmount;

        private final ValueSource reciveAmount;

        private final ValueSource sourceAmount;

        private final Map<?, ?> vsContext;

        private DocValues docValues;

        // Map<String, AtomicDouble> summary = new HashMap<String,
        // AtomicDouble>();
        private final TimeRanageSummary timeRangeSummary;

        public JSONFloatPairCollector(ValueSource timeRanageVS, ValueSource peopCount, ValueSource resultAmount, ValueSource reciveAmount, ValueSource sourceAmount, TimeRanageSummary timeRangeSummary, Map<?, ?> vsContext) {
            super();
            this.sourceAmount = sourceAmount;
            this.timeRage = timeRanageVS;
            this.peopCount = peopCount;
            this.resultAmount = resultAmount;
            this.reciveAmount = reciveAmount;
            this.timeRangeSummary = timeRangeSummary;
            this.vsContext = vsContext;
        }

        @Override
        public boolean needsScores() {
            return false;
        }

        protected void doSetNextReader(LeafReaderContext context) throws IOException {
            this.docValues = new DocValues();
            // time range
            FunctionValues timeRageValues = timeRage.getValues(vsContext, context);
            this.docValues.setTimeRagefiller(timeRageValues.getValueFiller());
            // people count
            FunctionValues peopleCountValues = peopCount.getValues(vsContext, context);
            this.docValues.setPeopleCountfiller(peopleCountValues.getValueFiller());
            // result amount
            FunctionValues resultAmountValues = resultAmount.getValues(vsContext, context);
            this.docValues.setResultAmountfiller(resultAmountValues.getValueFiller());
            // receive amount
            FunctionValues reciveAmountValues = reciveAmount.getValues(vsContext, context);
            this.docValues.setReceiveAmountfiller(reciveAmountValues.getValueFiller());
            FunctionValues sourceAmountValues = this.sourceAmount.getValues(vsContext, context);
            this.docValues.setSourceAmountfiller(sourceAmountValues.getValueFiller());
        }

        @Override
        public void collect(int doc) throws IOException {
            this.docValues.fillValue(doc);
            if (!this.docValues.timeRagemval.exists) {
                return;
            }
            // example:20150612165710 结账时间
            String operatorDate = docValues.getOperateDate();
            String hourMin = null;
            Matcher optimeMatcher = OPERATOR_DATE_PATTERN.matcher(operatorDate);
            if (optimeMatcher.matches()) {
                hourMin = optimeMatcher.group(1);
            } else {
                return;
            }
            AggreValue aggreValue = timeRangeSummary.findAggreValue(hourMin);
            if (aggreValue == null) {
                return;
            }
            aggreValue.addPeopleCount(docValues.getPeopleCount());
            aggreValue.addReceiveAmount(docValues.getReceiveAmount());
            aggreValue.addResultAmount(docValues.getResultAmount());
            aggreValue.addSourceAmount(docValues.getSourceAmount());
        // String value = String.valueOf(timeRagemval.toObject());
        // 
        // String[] args = StringUtils.split(value, ";");
        // String[] pair = null;
        // String key = null;
        // AtomicDouble v = null;
        // for (int i = 0; i < args.length; i++) {
        // pair = StringUtils.split(args[i], "_");
        // if (pair.length < 2) {
        // continue;
        // }
        // key = pair[0];
        // 
        // if ((v = summary.get(key)) == null) {
        // summary.put(key,
        // new AtomicDouble(Double.parseDouble(pair[1])));
        // } else {
        // v.addAndGet(Double.parseDouble(pair[1]));
        // }
        // }
        }
    }

    private static final Pattern TIMERANGE_PATTERN = Pattern.compile("(\\d{4})_(\\d{4})");

    private static final String TIME_PREFIX = "1";

    // 20150612(1657)10
    private static final Pattern OPERATOR_DATE_PATTERN = Pattern.compile("2\\d{7}(\\d{4})\\d{2}");

    public static void main(String[] args) {
        System.out.println(URLEncoder.encode("0600-0900,0900-1200,1200-1400,1400-2300"));
    // Matcher m = OPERATOR_DATE_PATTERN.matcher("20150612165710");
    // if (m.matches()) {
    // System.out.println(m.group(1));
    // }
    }

    private static class TimeRange {

        private final int start;

        private final int end;

        /**
         * example:0600-0900
         *
         * @param timeRange
         */
        public TimeRange(String timeRange) {
            Matcher matcher = TIMERANGE_PATTERN.matcher(timeRange);
            if (matcher.matches()) {
                this.start = Integer.parseInt(TIME_PREFIX + matcher.group(1));
                this.end = Integer.parseInt(TIME_PREFIX + matcher.group(2));
            } else {
                throw new IllegalArgumentException("timeRange:" + timeRange + " is not illegal");
            }
        }

        @Override
        public String toString() {
            return "r" + StringUtils.substringAfter(String.valueOf(start), TIME_PREFIX) + "_" + StringUtils.substringAfter(String.valueOf(end), TIME_PREFIX);
        }

        /**
         * 时间是否包括在这个区间之内
         *
         * @param hourmin
         * @return
         */
        public boolean isContain(String hourmin) {
            int time = Integer.valueOf(TIME_PREFIX + hourmin);
            return time >= start && time < end;
        }
    }

    private static class DocValues {

        private FunctionValues.ValueFiller timeRagefiller;

        private MutableValue timeRagemval;

        private FunctionValues.ValueFiller peopleCountfiller;

        private MutableValue peopleCountmval;

        private FunctionValues.ValueFiller resultAmountfiller;

        private MutableValue resultAmountmval;

        private FunctionValues.ValueFiller receiveAmountfiller;

        private MutableValue receiveAmountmval;

        private FunctionValues.ValueFiller sourceAmountfiller;

        private MutableValue sourceAmountmval;

        public void fillValue(int doc) {
            getTimeRagefiller().fillValue(doc);
            getPeopleCountfiller().fillValue(doc);
            getResultAmountfiller().fillValue(doc);
            getReceiveAmountfiller().fillValue(doc);
            this.sourceAmountfiller.fillValue(doc);
        }

        public float getSourceAmount() {
            if (!sourceAmountmval.exists) {
                return 0;
            }
            return Float.valueOf(sourceAmountmval.toString());
        }

        public String getOperateDate() {
            if (!timeRagemval.exists) {
                throw new IllegalStateException("timeRagemval is not exist");
            }
            return timeRagemval.toString();
        }

        public int getPeopleCount() {
            if (!peopleCountmval.exists) {
                return 0;
            }
            return Integer.valueOf(peopleCountmval.toString());
        }

        /**
         * 应收账款
         *
         * @return
         */
        public float getResultAmount() {
            if (!resultAmountmval.exists) {
                return 0;
            }
            return Float.valueOf(resultAmountmval.toString());
        }

        /**
         * 实收账款
         *
         * @return
         */
        public float getReceiveAmount() {
            if (!receiveAmountmval.exists) {
                return 0;
            }
            return Float.valueOf(receiveAmountmval.toString());
        }

        private FunctionValues.ValueFiller getPeopleCountfiller() {
            return peopleCountfiller;
        }

        private void setPeopleCountfiller(FunctionValues.ValueFiller peopleCountfiller) {
            this.peopleCountfiller = peopleCountfiller;
            this.peopleCountmval = peopleCountfiller.getValue();
        }

        private FunctionValues.ValueFiller getResultAmountfiller() {
            return resultAmountfiller;
        }

        private void setResultAmountfiller(FunctionValues.ValueFiller resultAmountfiller) {
            this.resultAmountfiller = resultAmountfiller;
            this.resultAmountmval = resultAmountfiller.getValue();
        }

        private FunctionValues.ValueFiller getReceiveAmountfiller() {
            return receiveAmountfiller;
        }

        private void setReceiveAmountfiller(FunctionValues.ValueFiller receiveAmountfiller) {
            this.receiveAmountfiller = receiveAmountfiller;
            this.receiveAmountmval = receiveAmountfiller.getValue();
        }

        private FunctionValues.ValueFiller getTimeRagefiller() {
            return timeRagefiller;
        }

        private void setTimeRagefiller(FunctionValues.ValueFiller timeRagefiller) {
            this.timeRagefiller = timeRagefiller;
            this.timeRagemval = timeRagefiller.getValue();
        }

        private void setSourceAmountfiller(FunctionValues.ValueFiller sourceAmountfiller) {
            this.sourceAmountfiller = sourceAmountfiller;
            this.sourceAmountmval = sourceAmountfiller.getValue();
        }
    }

    @Override
    public String getDescription() {
        return COMPONENT_NAME;
    }
}
