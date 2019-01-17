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
package com.qlangtech.tis.solrextend.queryparse.s4WaitingUser;

import java.util.HashMap;
import java.util.Map;
import org.apache.lucene.index.DocValues;
import org.apache.lucene.index.NumericDocValues;
import org.apache.lucene.index.SortedDocValues;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopFieldCollector;
import org.apache.lucene.search.TopFieldDocs;
import org.apache.lucene.util.FixedBitSet;
import org.apache.solr.common.SolrException;
import org.apache.solr.common.params.CommonParams;
import org.apache.solr.common.params.CursorMarkParams;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.schema.IndexSchema;
import org.apache.solr.search.CursorMark;
import org.apache.solr.search.LuceneQParserPlugin;
import org.apache.solr.search.QParser;
import org.apache.solr.search.SortSpec;
import org.apache.solr.search.SortSpecParsing;
import org.apache.solr.search.SyntaxError;
import com.qlangtech.tis.solrextend.queryparse.BitQuery;

/*
 * 生抽的需求，waiting记录中会有多个相同的orderid出现，需要对这些orderid进行去重<br>
 * 如果两个时间相同的话就取最小的那一个
 *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class DistinctOrderQparserPlugin extends LuceneQParserPlugin {

    // private static final Logger logger = LoggerFactory.getLogger(DistinctOrderQparserPlugin.class);
    // 第一次命中结果集取的条数
    private static final int DEFAULT_ROW_COUNT = 300;

    public static final String waitingorder_id = "waitingorder_id";

    public static final String create_time = "create_time";

    public static final String order_id = "order_id";

    @Override
    public QParser createParser(String qstr, SolrParams localParams, SolrParams params, SolrQueryRequest req) {
        // final long allstart = System.currentTimeMillis();
        try {
            Map<Integer, OrderCompare> /* orderid order */
            orderReducer = new HashMap<>();
            IndexSchema schema = req.getSchema();
            SortSpec sortSpec = SortSpecParsing.parseSortSpec(params.get(CommonParams.SORT), req);
            CursorMark mark = new CursorMark(schema, sortSpec);
            mark.parseSerializedTotem(params.get(CursorMarkParams.CURSOR_MARK_PARAM, CursorMarkParams.CURSOR_MARK_START));
            QParser parser = super.createParser(qstr, localParams, params, req);
            Query sortedQuery = parser.parse();
            // long start = System.currentTimeMillis();
            TopFieldCollector collector = null;
            try {
                collector = TopFieldCollector.create(sortSpec.getSort(), DEFAULT_ROW_COUNT, mark.getSearchAfterFieldDoc(), false, false, false);
                req.getSearcher().search(sortedQuery, collector);
            } finally {
            // logger.info("search consume:" + (System.currentTimeMillis() - start) + "ms,sort:" + sortSpec.getSort());
            }
            final TopFieldDocs sortedDocs = collector.topDocs();
            // 这个调用在大数据量的时候非常耗时
            SortedDocValues orderidVals = null;
            NumericDocValues createtimeVals = null;
            SortedDocValues pkVals = null;
            // start = System.currentTimeMillis();
            try {
                orderidVals = DocValues.getSorted(req.getSearcher().getLeafReader(), order_id);
                createtimeVals = DocValues.getNumeric(req.getSearcher().getLeafReader(), create_time);
                pkVals = DocValues.getSorted(req.getSearcher().getLeafReader(), waitingorder_id);
            } finally {
            // logger.info("consume:" + (System.currentTimeMillis() - start) + "ms");
            }
            int orderIdOrd;
            long createTimeVal;
            int pkOrd;
            OrderCompare ordCompare = null;
            // bitSet用来快速排序
            final FixedBitSet bitSet = new FixedBitSet(req.getSearcher().getIndexReader().maxDoc());
            for (ScoreDoc hitDoc : sortedDocs.scoreDocs) {
                orderIdOrd = orderidVals.getOrd(hitDoc.doc);
                // 这样可以控制
                if (orderIdOrd < 0) {
                    continue;
                }
                createTimeVal = createtimeVals.get(hitDoc.doc);
                pkOrd = pkVals.getOrd(hitDoc.doc);
                ordCompare = orderReducer.get(orderIdOrd);
                if (ordCompare == null) {
                    ordCompare = new OrderCompare();
                    ordCompare.docid = hitDoc.doc;
                    ordCompare.createTimeVal = createTimeVal;
                    ordCompare.pkOrd = pkOrd;
                    orderReducer.put(orderIdOrd, ordCompare);
                } else {
                    // 比较是否要取代之前已经存在的
                    if ((createTimeVal < ordCompare.createTimeVal) || (createTimeVal == ordCompare.createTimeVal && pkOrd < ordCompare.pkOrd)) {
                        ordCompare.docid = hitDoc.doc;
                        ordCompare.createTimeVal = createTimeVal;
                        ordCompare.pkOrd = pkOrd;
                    }
                }
            }
            // 去重之后灌装
            for (OrderCompare o : orderReducer.values()) {
                bitSet.set(o.docid);
            }
            final BitQuery bitQuery = new BitQuery(bitSet);
            return new QParser(qstr, localParams, params, req) {

                @Override
                public Query parse() throws SyntaxError {
                    return bitQuery;
                }
            };
        } catch (Exception e) {
            throw new SolrException(SolrException.ErrorCode.SERVER_ERROR, e);
        } finally {
        // logger.info("allconsume:" + (System.currentTimeMillis() - allstart));
        }
    }

    private static class OrderCompare {

        private int docid;

        private long createTimeVal;

        private int pkOrd;
    }
}
