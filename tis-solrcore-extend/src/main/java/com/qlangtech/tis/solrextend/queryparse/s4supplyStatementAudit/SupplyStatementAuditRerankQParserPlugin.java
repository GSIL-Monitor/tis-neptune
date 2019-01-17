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
package com.qlangtech.tis.solrextend.queryparse.s4supplyStatementAudit;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.index.SortedDocValues;
import org.apache.lucene.search.DocIdSetIterator;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.LeafCollector;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.search.TopDocsCollector;
import org.apache.lucene.search.Weight;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.PriorityQueue;
import org.apache.solr.common.StringUtils;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.handler.component.MergeStrategy;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.request.SolrRequestInfo;
import org.apache.solr.search.QParser;
import org.apache.solr.search.QParserPlugin;
import org.apache.solr.search.QueryCommand;
import org.apache.solr.search.RankQuery;
import org.apache.solr.search.SyntaxError;
import com.qlangtech.tis.solrextend.queryparse.s4supplyStatementAudit.SupplyStatementAuditQuery.Accumulator;

/*
 * 将SupplyStatementAuditQuery中的查询结果重新排序
 *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class SupplyStatementAuditRerankQParserPlugin extends QParserPlugin {

    @Override
    public QParser createParser(String qstr, SolrParams localParams, SolrParams params, SolrQueryRequest req) {
        return new AmountResortQParser(qstr, localParams, params, req);
    }

    private class AmountResortQParser extends QParser {

        private final String groupKey;

        public AmountResortQParser(String query, SolrParams localParams, SolrParams params, SolrQueryRequest req) {
            super(query, localParams, params, req);
            // localParams.get("groupby");
            this.groupKey = SupplyStatementAuditGroupByTradeIdQPlugin.getGroupKey(localParams);
        }

        @Override
        public Query parse() throws SyntaxError {
            return new ExportQuery();
        }

        public class ExportQuery extends RankQuery {

            private Query mainQuery;

            private Object id;

            public RankQuery clone() {
                ExportQuery clone = new ExportQuery();
                clone.id = this.id;
                return clone;
            }

            @Override
            public RankQuery wrap(Query mainQuery) {
                this.mainQuery = mainQuery;
                return this;
            }

            public MergeStrategy getMergeStrategy() {
                return null;
            }

            public Weight createWeight(IndexSearcher searcher, boolean needsScores) throws IOException {
                return mainQuery.createWeight(searcher, true);
            }

            public Query rewrite(IndexReader reader) throws IOException {
                Query q = mainQuery.rewrite(reader);
                if (q == mainQuery) {
                    return super.rewrite(reader);
                } else {
                    return clone().wrap(q);
                }
            }

            public TopDocsCollector<ScoreDoc> getTopDocsCollector(int len, QueryCommand cmd, IndexSearcher searcher) throws IOException {
                Doc2TradeMap sets = new Doc2TradeMap();
                setDoc2TradeMap(sets);
                AggregationPriorityQueue pq = new AggregationPriorityQueue(len);
                return new ExportCollector(pq, sets);
            }

            public int hashCode() {
                return // 
                31 * // 
                super.hashCode() + id.hashCode();
            }

            public boolean equals(Object o) {
                if (super.equals(o) == false) {
                    return false;
                }
                ExportQuery q = (ExportQuery) o;
                return id == q.id;
            }

            public String toString(String s) {
                return s;
            }

            public ExportQuery() {
                // this.req = req;
                this.id = new Object();
            }
            // public ExportQuery( //
            // SolrParams localParams, SolrParams params) throws IOException {
            // this();
            // }
        }

        private class ExportCollector extends TopDocsCollector<ScoreDoc> {

            private final Doc2TradeMap sets;

            private ScoreDoc topDoc;

            public ExportCollector(PriorityQueue<ScoreDoc> pq, Doc2TradeMap sets) {
                super(pq);
                this.topDoc = pq.top();
                this.sets = sets;
            }

            @Override
            public LeafCollector getLeafCollector(LeafReaderContext context) throws IOException {
                // final Doc2TradeMap set = new Doc2TradeMap();
                // this.sets[context.ord] = set;
                final int docBase = context.docBase;
                final SortedDocValues groupKeyDV = SupplyStatementAuditQuery.getGroupColDV(groupKey, context);
                return new LeafCollector() {

                    @Override
                    public void setScorer(Scorer scorer) throws IOException {
                    }

                    @Override
                    public void collect(int docId) throws IOException {
                        BytesRef groupkey = groupKeyDV.get(docId);
                        if (groupkey != null) {
                            ++totalHits;
                            sets.put(docId + docBase, groupkey.utf8ToString());
                        }
                    }
                };
            }

            @Override
            public int topDocsSize() {
                SolrRequestInfo info = SolrRequestInfo.getRequestInfo();
                // 
                Map<String, Accumulator> /* tradeid */
                tradeAmountAccumulator = SupplyStatementAuditGroupByTradeIdQPlugin.getAmountAccumulator(info.getReq());
                // Doc2TradeMap bitset = null;
                int docId;
                String tradeId = null;
                Accumulator accumulator = null;
                // boolean firstPushed = false;
                for (Map.Entry<Integer, String> /* tradeId */
                e : sets.entrySet()) {
                    docId = e.getKey();
                    tradeId = e.getValue();
                    accumulator = tradeAmountAccumulator.get(tradeId);
                    long diff = processDiff(accumulator.getDifference());
                    if (accumulator != null) {
                        if (diff > this.topDoc.score) {
                            this.topDoc.score = diff;
                            this.topDoc.doc = docId;
                            this.topDoc = this.pq.updateTop();
                        }
                    }
                }
                return super.topDocsSize();
            }

            // public TopDocs topDocs(int start, int howMany) {
            // 
            // assert (sets != null);
            // 
            // 
            // 
            // // SolrQueryRequest req = null;
            // // if (info != null && ((req = info.getReq()) != null)) {
            // // Map context = req.getContext();
            // // context.put("export", sets);
            // // context.put("totalHits", totalHits);
            // // }
            // 
            // ScoreDoc[] scoreDocs = getScoreDocs(howMany);
            // assert scoreDocs.length <= totalHits;
            // return new TopDocs(totalHits, scoreDocs, 0.0f);
            // }
            @Override
            public boolean needsScores() {
                // TODO: is this the case?
                return true;
            }
        }
    }

    private long processDiff(long val) {
        if (val <= Integer.MIN_VALUE) {
            val -= Integer.MIN_VALUE;
            return processDiff(val);
        } else {
            return val;
        }
    }

    public static Doc2TradeMap getFromRequestContext() {
        SolrRequestInfo requestInfo = SolrRequestInfo.getRequestInfo();
        Doc2TradeMap resut = (Doc2TradeMap) requestInfo.getReq().getContext().get("Key_Doc2TradeMap");
        if (resut == null) {
            throw new IllegalStateException("Key_Doc2TradeMap relevant can not be null");
        }
        return resut;
    }

    private void setDoc2TradeMap(Doc2TradeMap val) {
        SolrRequestInfo requestInfo = SolrRequestInfo.getRequestInfo();
        requestInfo.getReq().getContext().put("Key_Doc2TradeMap", val);
    }

    private static class AggregationPriorityQueue extends PriorityQueue<ScoreDoc> {

        public AggregationPriorityQueue(int maxSize) {
            super(maxSize);
        }

        @Override
        protected ScoreDoc getSentinelObject() {
            return new ScoreDoc(DocIdSetIterator.NO_MORE_DOCS, Integer.MIN_VALUE);
        }

        @Override
        protected boolean lessThan(ScoreDoc a, ScoreDoc b) {
            return a.score < b.score;
        }
    }

    public static class Doc2TradeMap extends HashMap<Integer, /* docid */
    String> {

        private static final long serialVersionUID = 1L;
    }
}
