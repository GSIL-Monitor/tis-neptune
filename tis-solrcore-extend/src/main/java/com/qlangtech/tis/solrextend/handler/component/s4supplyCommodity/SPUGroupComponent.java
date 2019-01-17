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
package com.qlangtech.tis.solrextend.handler.component.s4supplyCommodity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.index.NumericDocValues;
import org.apache.lucene.queries.function.FunctionValues;
import org.apache.lucene.queries.function.ValueSource;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.SimpleCollector;
import org.apache.lucene.search.join.BitSetProducer;
import org.apache.lucene.search.join.QueryBitSetProducer;
import org.apache.lucene.util.BitSet;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.PriorityQueue;
import org.apache.lucene.util.mutable.MutableValue;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrException;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.handler.component.ResponseBuilder;
import org.apache.solr.handler.component.SearchComponent;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.response.DocsStreamer;
import org.apache.solr.schema.IndexSchema;
import org.apache.solr.schema.SchemaField;
import org.apache.solr.search.DocListAndSet;
import org.apache.solr.search.QParser;
import org.apache.solr.search.QueryWrapperFilter;
import org.apache.solr.search.SolrIndexSearcher;
import org.apache.solr.search.SyntaxError;
import org.slf4j.Logger;

/*
 * 选取七个品类，每个品类中靠前的4个SPU，排序规则是“默认按照销量降序展示全部商品，当销量相同时按照商品新建时间降序排序” Created by
 * Qinjiu on 2016/12/21.
 *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class SPUGroupComponent extends SearchComponent {

    private static final String COMPONENT_NAME = "SPUGroup";

    private static final String SKU_STANDARD_INNER_CODE = "sku_standard_inner_code";

    // private static final String SALE_TIME = "sale_time";
    private static final String SALES_NUM = "sales_num";

    private static final String CREATE_TIME = "create_time";

    private static final String[] SORT_COLUMN_NAMES;

    private static final Logger log = org.slf4j.LoggerFactory.getLogger(SPUGroupComponent.class);

    static {
        SORT_COLUMN_NAMES = new String[] { SALES_NUM, CREATE_TIME };
    }

    public static String val(Object o) {
        if (o == null) {
            return "";
        }
        if (!(o instanceof Field)) {
            return String.valueOf(o);
        }
        Field f = (Field) o;
        return f.stringValue();
    }

    static long getLongValue(FieldColumn field) {
        if (!field.val.exists) {
            return 0L;
        }
        try {
            return Long.parseLong(field.val.toString());
        } catch (Throwable e) {
            return 0L;
        }
    }

    static String getStringValue(FieldColumn field) {
        if (!field.val.exists) {
            return null;
        }
        try {
            return field.val.toString();
        } catch (Throwable e) {
            return null;
        }
    }

    @Override
    public String getDescription() {
        return COMPONENT_NAME;
    }

    @Override
    public void prepare(ResponseBuilder responseBuilder) throws IOException {
        if (responseBuilder.req.getParams().getBool(COMPONENT_NAME, false)) {
            responseBuilder.setNeedDocSet(true);
        }
    }

    @Override
    public void process(ResponseBuilder responseBuilder) throws IOException {
        SolrQueryRequest req = responseBuilder.req;
        SolrParams params = req.getParams();
        if (!params.getBool(COMPONENT_NAME, false)) {
            return;
        }
        IndexSchema schema = req.getSchema();
        SolrIndexSearcher searcher = req.getSearcher();
        Map<String, PriorityQueue<SpuWithSkuIdAndSortFields>> resultMap = new HashMap<>();
        FieldColumn[] fieldColumns = new FieldColumn[SORT_COLUMN_NAMES.length];
        for (int i = 0; i < SORT_COLUMN_NAMES.length; i++) {
            fieldColumns[i] = new FieldColumn(schema.getField(SORT_COLUMN_NAMES[i]));
        }
        DocValues childDocValues = new DocValues(new FieldColumn(schema.getField(SKU_STANDARD_INNER_CODE)));
        SPUCollector collector = new SPUCollector(req, resultMap, fieldColumns, childDocValues);
        DocListAndSet results = responseBuilder.getResults();
        // 搜索出7个豆腐块和每个父doc所对应的子doc
        searcher.search(results.docSet.getTopFilter(), collector);
        // 把结果doc取出来
        Map<String, ArrayList<SolrDocument>> resMap = new HashMap<>();
        for (String key : resultMap.keySet()) {
            // heap按照升序排序，需要重新排
            PriorityQueue<SpuWithSkuIdAndSortFields> spuHeap = resultMap.get(key);
            // 降序排序
            List<SpuWithSkuIdAndSortFields> heapList = new ArrayList<>(spuHeap.size());
            while (spuHeap.size() > 0) {
                heapList.add(spuHeap.pop());
            }
            Collections.reverse(heapList);
            ArrayList<SolrDocument> list = new ArrayList<>();
            for (SpuWithSkuIdAndSortFields aSpu : heapList) {
                Document parentDoc = searcher.doc(aSpu.getSpuId());
                SolrDocument solrParentDoc = DocsStreamer.getDoc(parentDoc, schema);
                List<Integer> skuIdList = aSpu.getSkuIdList();
                for (int childDocId : skuIdList) {
                    Document childDoc = searcher.doc(childDocId);
                    SolrDocument solrChildDoc = DocsStreamer.getDoc(childDoc, schema);
                    solrParentDoc.addChildDocument(solrChildDoc);
                }
                list.add(solrParentDoc);
            }
            resMap.put(key, list);
        }
        responseBuilder.rsp.add(COMPONENT_NAME, resMap);
    }

    private static class SPUCollector extends SimpleCollector {

        private static final String PARENT_FILTER = "type:p";

        private static final int HEAP_SIZE = 4;

        private final Map<String, PriorityQueue<SpuWithSkuIdAndSortFields>> resultMap;

        private final SolrQueryRequest req;

        private final DocValues childDocValues;

        private final BitSetProducer parentsFilter;

        FieldColumn[] fieldColumns;

        private BitSet parentBits = null;

        private int docBase;

        private NumericDocValues skuIsValid = null;

        SPUCollector(SolrQueryRequest req, Map<String, PriorityQueue<SpuWithSkuIdAndSortFields>> resultMap, FieldColumn[] fieldColumns, DocValues childDocValues) {
            this.req = req;
            this.resultMap = resultMap;
            this.fieldColumns = fieldColumns;
            this.childDocValues = childDocValues;
            try {
                Query parentFilterQuery = QParser.getParser(PARENT_FILTER, null, this.req).getQuery();
                parentsFilter = new QueryBitSetProducer(new QueryWrapperFilter(parentFilterQuery));
            } catch (SyntaxError syntaxError) {
                throw new SolrException(SolrException.ErrorCode.BAD_REQUEST, "Failed to create correct parent filter " + "query");
            }
        }

        @Override
        public boolean needsScores() {
            return false;
        }

        @Override
        protected void doSetNextReader(LeafReaderContext context) throws IOException {
            Arrays.stream(this.fieldColumns).forEach(fieldColumn -> fieldColumn.doSetNextReader(context));
            childDocValues.doSetNextReader(context);
            parentBits = parentsFilter.getBitSet(context);
            docBase = context.docBase;
            // 
            this.skuIsValid = org.apache.lucene.index.DocValues.getNumeric(context.reader(), "sku_is_valid");
        }

        @Override
        public void collect(int docId) throws IOException {
            Arrays.stream(this.fieldColumns).forEach(fieldColumn -> fieldColumn.fillValue(docId));
            if (parentBits == null) {
                // no parents
                throw new NullPointerException("parentBits is null");
            }
            if (docId < 1) {
                // log.info("parent docId = " + docId + " can not find any child");
                return;
            }
            int childDocId = 1 + parentBits.prevSetBit(docId - 1);
            if (childDocId == docId) {
                // log.info("parent docId = " + docId + " can not find any child");
                return;
            }
            List<Integer> skuIdList = new ArrayList<>(docId - childDocId);
            for (int i = childDocId + docBase; i < docId + docBase; i++) {
                skuIdList.add(i);
            }
            long skuIsValidValue;
            Set<String> keySet = new HashSet<>();
            // 每一个子 都要加入对象的类目当中
            while (childDocId < docId) {
                childDocValues.fillValue(childDocId);
                skuIsValidValue = skuIsValid.get(childDocId);
                ++childDocId;
                String sku_standard_inner_code = childDocValues.getSkuStandardInnerCode();
                if (!(skuIsValidValue == 1) || sku_standard_inner_code == null || sku_standard_inner_code.length() < 2) {
                    // 1));
                    continue;
                }
                String key = sku_standard_inner_code.substring(0, 2);
                if (keySet.contains(key)) {
                    // spu已经被加入到当前类目就不需要重新加入了
                    continue;
                } else {
                    keySet.add(key);
                }
                PriorityQueue<SpuWithSkuIdAndSortFields> heap = resultMap.computeIfAbsent(key, k -> new PriorityQueue<SpuWithSkuIdAndSortFields>(HEAP_SIZE) {

                    // 返回最大的N个数，但是pop出来的顺序是从小到大的
                    // insertWithOverflow()方法会把比较小的给剔除
                    @Override
                    protected boolean lessThan(SpuWithSkuIdAndSortFields a, SpuWithSkuIdAndSortFields b) {
                        if (a.getSalesNum() == b.getSalesNum()) {
                            return a.getCreateTime() < b.getCreateTime();
                        } else {
                            return a.getSalesNum() < b.getSalesNum();
                        }
                    }
                });
                // lambda expression
                SpuWithSkuIdAndSortFields aSpu = new SpuWithSkuIdAndSortFields(docId + docBase, skuIdList, this.fieldColumns);
                heap.insertWithOverflow(aSpu);
            }
        }
    }

    private static class SpuWithSkuIdAndSortFields {

        private int spuId;

        private List<Integer> skuIdList;

        private long createTime;

        private long salesNum;

        SpuWithSkuIdAndSortFields(int spuId, List<Integer> skuIdList, FieldColumn[] fieldColumns) {
            this.spuId = spuId;
            this.skuIdList = skuIdList;
            this.salesNum = getLongValue(fieldColumns[0]);
            this.createTime = getLongValue(fieldColumns[1]);
        }

        List<Integer> getSkuIdList() {
            return skuIdList;
        }

        int getSpuId() {
            return spuId;
        }

        long getCreateTime() {
            return createTime;
        }

        long getSalesNum() {
            return salesNum;
        }
    }

    private static class FieldColumn {

        @SuppressWarnings("unused")
        private final SchemaField field;

        private FunctionValues.ValueFiller filler;

        private MutableValue val;

        private ValueSource valueSource;

        public FieldColumn(SchemaField field) {
            if (field == null) {
                throw new IllegalArgumentException("field can not be null");
            }
            this.field = field;
            this.valueSource = field.getType().getValueSource(field, null);
        }

        public void doSetNextReader(LeafReaderContext context) {
            try {
                FunctionValues funcValues = valueSource.getValues(Collections.emptyMap(), context);
                filler = funcValues.getValueFiller();
                val = filler.getValue();
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        }

        public MutableValue getValue(int docId) {
            this.filler.fillValue(docId);
            return this.val;
        }

        public void fillValue(int docId) {
            this.filler.fillValue(docId);
        }
    }

    private static class DocValues {

        private FieldColumn skuStandardInnerCodeColumn;

        public DocValues(FieldColumn skuStandardInnerCodeColumn) {
            this.skuStandardInnerCodeColumn = skuStandardInnerCodeColumn;
        }

        private void doSetNextReader(LeafReaderContext context) {
            skuStandardInnerCodeColumn.doSetNextReader(context);
        }

        private void fillValue(int doc) {
            skuStandardInnerCodeColumn.fillValue(doc);
        }

        String getSkuStandardInnerCode() {
            return getStringValue(skuStandardInnerCodeColumn);
        }
    }

    public static void main(String[] args) {
        PriorityQueue<Integer> queue = new PriorityQueue<Integer>(4) {

            @Override
            protected boolean lessThan(Integer a, Integer b) {
                return a < b;
            }
        };
        queue.insertWithOverflow(1);
        queue.insertWithOverflow(3);
        queue.insertWithOverflow(5);
        queue.insertWithOverflow(2);
        queue.insertWithOverflow(4);
        queue.insertWithOverflow(0);
        while (queue.size() > 0) {
            System.out.println(queue.pop());
        }
    }
}
