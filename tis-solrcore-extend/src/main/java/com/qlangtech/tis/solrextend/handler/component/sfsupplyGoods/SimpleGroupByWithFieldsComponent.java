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
package com.qlangtech.tis.solrextend.handler.component.sfsupplyGoods;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DocValues;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.index.NumericDocValues;
import org.apache.lucene.index.SortedDocValues;
import org.apache.lucene.index.SortedNumericDocValues;
import org.apache.lucene.search.SimpleCollector;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.mutable.MutableValue;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.common.util.SimpleOrderedMap;
import org.apache.solr.handler.component.ResponseBuilder;
import org.apache.solr.handler.component.SearchComponent;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.search.DocListAndSet;
import org.apache.solr.search.SolrIndexSearcher;
import com.qlangtech.tis.solrextend.handler.component.AllConsumeDimStatisComponent;

/*
 * 通用goup查询模块
 *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class SimpleGroupByWithFieldsComponent extends SearchComponent {

    private static final String NAME = "commonGroupby";

    // kind_createtime(sort_num)
    private static final Pattern FIELD_PATTERN = Pattern.compile("([^,\\(]+)(\\(([^,\\(]+?)\\))?");

    public static void main(String[] args) {
        Matcher m = FIELD_PATTERN.matcher("kind_createtime(num_sort),menu_createtime(num),kind_id,menu_id");
        while (m.find()) {
            System.out.println(m.groupCount());
            System.out.println(m.group(1));
            System.out.println(m.group(3));
            System.out.println("===========================");
        }
    }

    @Override
    public void prepare(ResponseBuilder rb) throws IOException {
        SolrQueryRequest req = rb.req;
        SolrParams params = req.getParams();
        if (!params.getBool(NAME, false)) {
            return;
        }
        rb.setNeedDocSet(true);
    }

    private static final String[] emptyArray = new String[0];

    @Override
    public void process(ResponseBuilder rb) throws IOException {
        SolrQueryRequest req = rb.req;
        SolrParams params = req.getParams();
        if (!params.getBool(NAME, false)) {
            return;
        }
        // 需要用它来分组
        final List<GField> groupFieldKey = getGroupField(params);
        String[] groupShowFieldKey = StringUtils.split(params.get("tisgroup.group.show.field"), ",");
        if (groupShowFieldKey == null) {
            groupShowFieldKey = emptyArray;
        }
        // group之后需要求和的字段
        final String[] groupSumField = StringUtils.split(params.get("tisgroup.group.sum.field"), ",");
        SolrIndexSearcher searcher = req.getSearcher();
        final IndexReader reader = searcher.getIndexReader();
        DocListAndSet results = rb.getResults();
        final Map<String, AllConsumeDimStatisComponent.FieldColumn> sumCols = new HashMap<>();
        for (String f : groupSumField) {
            sumCols.put(f, new AllConsumeDimStatisComponent.FieldColumn(req.getSchema().getField(f)));
        }
        GroupByCollector collector = new GroupByCollector(groupFieldKey, groupShowFieldKey, sumCols);
        rb.req.getSearcher().search(results.docSet.getTopFilter(), collector);
        List<SimpleOrderedMap<String>> result = new ArrayList<>();
        SimpleOrderedMap<String> row = null;
        GroupKey gkey = null;
        Document docment = null;
        for (Map.Entry<GroupKey, List<Sum>> entry : collector.grouByFields.entrySet()) {
            gkey = entry.getKey();
            row = new SimpleOrderedMap<>();
            // 生成group的kv
            for (int i = 0; i < groupFieldKey.size(); i++) {
                row.add(groupFieldKey.get(i).key, entry.getKey().keys.get(i));
            }
            if (groupShowFieldKey.length > 1) {
                docment = reader.document(gkey.firstDocId);
                for (String field : groupShowFieldKey) {
                    row.add(field, StringUtils.trimToEmpty(docment.get(field)));
                }
            }
            for (Sum sum : entry.getValue()) {
                row.add(sum.field, String.valueOf(sum.value));
            }
            result.add(row);
        }
        rb.rsp.add(NAME, result);
    }

    private List<GField> getGroupField(SolrParams params) {
        final List<GField> groupFieldKey = new ArrayList<>();
        GField gf = null;
        String groupField = StringUtils.trimToEmpty(params.get("tisgroup.group.field"));
        Matcher m = FIELD_PATTERN.matcher(groupField);
        while (m.find()) {
            gf = new GField(m.group(1), DocValueType.parse(m.group(3)));
            groupFieldKey.add(gf);
        }
        if (groupFieldKey.size() < 1) {
            throw new IllegalArgumentException("param “tisgroup.group.field” can not be null");
        }
        return groupFieldKey;
    }

    private static class GField {

        private final String key;

        private final DocValueType valueType;

        private Object docValues;

        public GField(String key, DocValueType valueType) {
            super();
            this.key = key;
            this.valueType = valueType;
        }

        public void createDocValues(LeafReaderContext context) {
            try {
                this.docValues = this.valueType.process.getDocValue(context, key);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        public Object getValue(int docid) {
            try {
                return this.valueType.process.getValue(this.docValues, docid);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    private enum DocValueType {

        NUM("num", new DocValueProcess() {

            public Object getDocValue(LeafReaderContext context, String field) throws Exception {
                return /* NumericDocValues */
                DocValues.getNumeric(context.reader(), field);
            }

            public Object getValue(Object docValue, int docid) throws Exception {
                NumericDocValues values = (NumericDocValues) docValue;
                // NumericUtils.sortableLongToDouble();
                return /* long */
                values.get(docid);
            }
        }), NUM_SORT("num_sort", new DocValueProcess() {

            public Object getDocValue(LeafReaderContext context, String field) throws Exception {
                return /* SortedNumericDocValues */
                DocValues.getSortedNumeric(context.reader(), field);
            }

            public Object getValue(Object docValue, int docid) throws Exception {
                SortedNumericDocValues values = (SortedNumericDocValues) docValue;
                values.setDocument(docid);
                return // NumericUtils.sortableLongToDouble()
                values.count() > 0 ? // NumericUtils.sortableLongToDouble()
                values.valueAt(0) : 0;
            }
        }), STR("str", new DocValueProcess() {

            public Object getDocValue(LeafReaderContext context, String field) throws Exception {
                return /* SortedDocValues */
                DocValues.getSorted(context.reader(), field);
            }

            public Object getValue(Object docValue, int docid) throws Exception {
                SortedDocValues values = (SortedDocValues) docValue;
                BytesRef value = values.get(docid);
                return value != null ? value.utf8ToString() : StringUtils.EMPTY;
            }
        });

        private final String name;

        private final DocValueProcess process;

        private DocValueType(String name, DocValueProcess process) {
            this.name = name;
            this.process = process;
        }

        private static DocValueType parse(String name) {
            if (name == null) {
                return STR;
            }
            if (NUM.name.equals(name)) {
                return NUM;
            }
            if (NUM_SORT.name.equals(name)) {
                return NUM_SORT;
            }
            if (STR.name.equals(name)) {
                return STR;
            }
            throw new IllegalArgumentException("name:" + name + " is illegal");
        }
    }

    private interface DocValueProcess {

        public Object getDocValue(LeafReaderContext context, String field) throws Exception;

        public Object getValue(Object docValue, int docid) throws Exception;
    }

    private class GroupByCollector extends SimpleCollector {

        private final List<GField> groupFieldKey;

        // private final String[] groupShowFieldKey;
        // group之后需要求和的字段
        private final Map<String, AllConsumeDimStatisComponent.FieldColumn> sumCols;

        private final Map<GroupKey, List<Sum>> grouByFields = new TreeMap<>();

        // private final Map<String/* field name */, Object> groupValues = new
        // HashMap<>();
        // private final Map<String/* field name */, AllConsumeDimStatisComponent.FieldColumn> sumFields ;
        // private SortedNumericDocValues recommendLevel;
        private int docBase;

        public GroupByCollector(List<GField> groupFieldKe, String[] groupShowFieldKey, Map<String, AllConsumeDimStatisComponent.FieldColumn> sumCols) {
            this.groupFieldKey = groupFieldKe;
            // group之后需要求和的字段
            this.sumCols = sumCols;
        }

        @Override
        public boolean needsScores() {
            return false;
        }

        protected void doSetNextReader(LeafReaderContext context) throws IOException {
            this.docBase = context.docBase;
            for (GField f : groupFieldKey) {
                f.createDocValues(context);
            }
            for (AllConsumeDimStatisComponent.FieldColumn f : sumCols.values()) {
                f.doSetNextReader(context);
            }
        // for (String f : groupSumField) {
        // sumFields.put(f, DocValues.getNumeric(context.reader(), f));
        // }
        }

        @Override
        public void collect(int doc) throws IOException {
            AllConsumeDimStatisComponent.FieldColumn sumFieldVal = null;
            Double fieldValue = null;
            MutableValue mvalue = null;
            List<String> groupField = new ArrayList<>(groupFieldKey.size());
            for (GField f : groupFieldKey) {
                groupField.add(StringUtils.lowerCase(String.valueOf(f.getValue(doc))));
            }
            final GroupKey groupKey = new GroupKey(groupField);
            List<Sum> sum = grouByFields.get(groupKey);
            if (sum == null) {
                sum = new ArrayList<>(sumCols.size());
                for (String key : sumCols.keySet()) {
                    sum.add(new Sum(key));
                }
                groupKey.firstDocId = docBase + doc;
                grouByFields.put(groupKey, sum);
            }
            for (Sum ss : sum) {
                sumFieldVal = sumCols.get(ss.field);
                mvalue = sumFieldVal.getValue(doc);
                if (!mvalue.exists) {
                    continue;
                }
                fieldValue = (Double) mvalue.toObject();
                ss.add(fieldValue);
            }
        }
        // private String[] getSumFields() {
        // return SUM_FIELDS;
        // }
        // 
        // private String[] getGroupKey() {
        // return GROUP_FIELD_KEY;
        // }
        // 
        // private String[] getGroupFieldKeyShow() {
        // return this.groupShowFieldKey;
        // }
    }

    private static final class GroupKey implements Comparable<GroupKey> {

        private int firstDocId;

        private final List<String> keys;

        private final String key;

        public GroupKey(List<String> keys) {
            super();
            this.keys = keys;
            StringBuffer keyBuffer = new StringBuffer();
            for (String k : keys) {
                keyBuffer.append(k);
            }
            this.key = keyBuffer.toString();
        }

        @Override
        public int compareTo(GroupKey o) {
            return key.compareTo(o.key);
        }

        @Override
        public int hashCode() {
            return this.key.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            return StringUtils.equals(key, ((GroupKey) obj).key);
        }

        @Override
        public String toString() {
            return this.key;
        }
    }

    private static class Sum {

        private final String field;

        public Sum(String field) {
            super();
            this.field = field;
        }

        private double value = 0;

        public void add(double d) {
            this.value += d;
        }
        // public double getValue() {
        // return this.value;
        // }
    }

    @Override
    public String getDescription() {
        return NAME;
    }
    // private String[] getSumFields() {
    // return SUM_FIELDS;
    // }
    // 
    // private String[] getGroupKey() {
    // return GROUP_FIELD_KEY;
    // }
    // 
    // private String[] getGroupFieldKeyShow() {
    // return GROUP_FIELD_KEY_SHOW;
    // }
}
