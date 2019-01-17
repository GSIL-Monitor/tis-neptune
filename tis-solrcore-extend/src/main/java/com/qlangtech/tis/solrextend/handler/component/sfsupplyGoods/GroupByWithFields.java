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
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DocValues;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.index.SortedDocValues;
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

/* *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class GroupByWithFields extends SearchComponent {

    private static final String NAME = "groupby";

    private static final String[] GROUP_FIELD_KEY = new String[] { "supply_code", "self_entity_id", "category_id", "goods_id", "op_kind" };

    // 最终只显示值的Field ，這個應該由聚合的最後一行的行顯示
    private static final String[] GROUP_FIELD_KEY_SHOW = new String[] { "shop_spell", "goods_spell", "goods_name", "category_name", "supply_name", "shop_name", "new_price_unit_name", "weight_unit_name" };

    private static final String FILED_KEY_OLD_GOODS_NUM = "old_goods_num";

    private static final String FILED_KEY_NEW_GOODS_NUM = "new_goods_num";

    private static final String FILED_KEY_GOODS_WEIGHT = "goods_weight";

    private static final String FILED_KEY_GOODS_AMOUNT = "goods_amount";

    // 合计数据集合
    private static final String[] SUM_FIELDS = new String[] { FILED_KEY_OLD_GOODS_NUM, FILED_KEY_NEW_GOODS_NUM, FILED_KEY_GOODS_WEIGHT, FILED_KEY_GOODS_AMOUNT };

    @Override
    public void prepare(ResponseBuilder rb) throws IOException {
        SolrQueryRequest req = rb.req;
        SolrParams params = req.getParams();
        if (!params.getBool(NAME, false)) {
            return;
        }
        rb.setNeedDocSet(true);
    }

    @Override
    public void process(ResponseBuilder rb) throws IOException {
        SolrQueryRequest req = rb.req;
        SolrParams params = req.getParams();
        if (!params.getBool(NAME, false)) {
            return;
        }
        SolrIndexSearcher searcher = req.getSearcher();
        final IndexReader reader = searcher.getIndexReader();
        DocListAndSet results = rb.getResults();
        final Map<String, AllConsumeDimStatisComponent.FieldColumn> fileCols = new HashMap<>();
        for (String f : SUM_FIELDS) {
            fileCols.put(f, new AllConsumeDimStatisComponent.FieldColumn(req.getSchema().getField(f)));
        }
        GroupByCollector collector = new GroupByCollector(fileCols);
        rb.req.getSearcher().search(results.docSet.getTopFilter(), collector);
        List<SimpleOrderedMap<String>> result = new ArrayList<>();
        SimpleOrderedMap<String> row = null;
        GroupKey gkey = null;
        Document docment = null;
        for (Map.Entry<GroupKey, List<Sum>> entry : collector.grouByFields.entrySet()) {
            gkey = entry.getKey();
            row = new SimpleOrderedMap<>();
            // 生成group的kv
            for (int i = 0; i < GROUP_FIELD_KEY.length; i++) {
                row.add(GROUP_FIELD_KEY[i], entry.getKey().keys.get(i));
            }
            docment = reader.document(gkey.firstDocId);
            for (String field : GROUP_FIELD_KEY_SHOW) {
                row.add(field, StringUtils.trimToEmpty(docment.get(field)));
            }
            for (Sum sum : entry.getValue()) {
                row.add(sum.field, String.valueOf(sum.value));
            }
            result.add(row);
        }
        rb.rsp.add(NAME, result);
    }

    private static class GroupByCollector extends SimpleCollector {

        private final Map<GroupKey, List<Sum>> grouByFields = new TreeMap<>();

        private final Map<String, SortedDocValues> /* field name */
        groupValues = new HashMap<>();

        // private final Map<String/* field name */, NumericDocValues> sumFields
        // = new HashMap<>();
        // private SortedNumericDocValues recommendLevel;
        private int docBase;

        // private final BytesRefBuilder bytes = new BytesRefBuilder();
        private final Map<String, AllConsumeDimStatisComponent.FieldColumn> fileCols;

        public GroupByCollector(final Map<String, AllConsumeDimStatisComponent.FieldColumn> fileCols) {
            this.fileCols = fileCols;
        }

        @Override
        public boolean needsScores() {
            return false;
        }

        protected void doSetNextReader(LeafReaderContext context) throws IOException {
            this.docBase = context.docBase;
            for (String f : GROUP_FIELD_KEY) {
                groupValues.put(f, DocValues.getSorted(context.reader(), f));
            }
            for (AllConsumeDimStatisComponent.FieldColumn sumField : fileCols.values()) {
                sumField.doSetNextReader(context);
            }
        // for (String f : SUM_FIELDS) {
        // sumFields.put(f, DocValues.getNumeric(context.reader(), f));
        // }
        }

        @Override
        public void collect(int doc) throws IOException {
            SortedDocValues docVal = null;
            AllConsumeDimStatisComponent.FieldColumn sumFieldVal = null;
            BytesRef groupValue = null;
            MutableValue sumValue = null;
            List<String> groupField = new ArrayList<>(GROUP_FIELD_KEY.length);
            for (String f : GROUP_FIELD_KEY) {
                docVal = groupValues.get(f);
                groupValue = docVal.get(doc);
                if (groupValue == null) {
                    groupField.add(StringUtils.EMPTY);
                } else {
                    // groupField.add(StringUtils.lowerCase(groupValue.utf8ToString()));
                    groupField.add(groupValue.utf8ToString());
                }
            }
            final GroupKey groupKey = new GroupKey(groupField);
            List<Sum> sum = grouByFields.get(groupKey);
            if (sum == null) {
                sum = new ArrayList<>(SUM_FIELDS.length);
                for (String key : SUM_FIELDS) {
                    sum.add(new Sum(key));
                }
                groupKey.firstDocId = docBase + doc;
                grouByFields.put(groupKey, sum);
            }
            for (Sum ss : sum) {
                fileCols.get(ss.field);
                // sumFields.get(ss.field);
                sumFieldVal = fileCols.get(ss.field);
                sumValue = sumFieldVal.getValue(doc);
                if (!sumValue.exists) {
                    continue;
                }
                Double value = (Double) sumValue.toObject();
                ss.add(value);
            }
        }
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
            this.key = StringUtils.lowerCase(keyBuffer.toString());
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
}
