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
package com.qlangtech.tis.solrextend.queryparse.s4presellStock;

import java.io.IOException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.LeafCollector;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.search.TISHitQueue;
import org.apache.lucene.search.TopDocsCollector;
import org.apache.lucene.search.Weight;
import org.apache.lucene.util.PriorityQueue;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.handler.component.MergeStrategy;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.search.QParser;
import org.apache.solr.search.QParserPlugin;
import org.apache.solr.search.QueryCommand;
import org.apache.solr.search.RankQuery;
import org.apache.solr.search.SyntaxError;

/*
 * 需要将结果集打散,现有预购项目中單個品牌的
 *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class ShuffleQParserPlugin extends QParserPlugin {

    @Override
    public QParser createParser(String qstr, SolrParams localParams, SolrParams params, SolrQueryRequest req) {
        return new ShuffleQParser(qstr, localParams, params, req);
    }

    private class ShuffleQParser extends QParser {

        public ShuffleQParser(String query, SolrParams localParams, SolrParams params, SolrQueryRequest req) {
            super(query, localParams, params, req);
        }

        @Override
        public Query parse() throws SyntaxError {
            int mod = this.localParams.getInt("mod", 1);
            float multi = this.localParams.getFloat("multi", 1.0f);
            return new ShuffleQuery(mod, multi);
        }
    }

    private static Query defaultQuery = new MatchAllDocsQuery();

    private final class ShuffleQuery extends RankQuery {

        private Query mainQuery;

        // private Query originQuery;
        // private Map<BytesRef, Integer> boostedPriority;
        public int hashCode() {
            return 31 * super.hashCode();
        }

        public Weight createWeight(IndexSearcher searcher, boolean needsScores) throws IOException {
            return mainQuery.createWeight(searcher, needsScores);
        }

        // public boolean equals(Object o) {
        // if (super.equals(o) == false) {
        // return false;
        // }
        // ShuffleQuery rrq = (ShuffleQuery) o;
        // return originQuery.equals(rrq.originQuery);
        // }
        private final int mod;

        private final float multi;

        public ShuffleQuery(int mod, float multi) {
            // this.originQuery = originQuery;
            this.mod = mod;
            this.multi = multi;
        }

        public RankQuery wrap(Query _mainQuery) {
            if (_mainQuery != null) {
                this.mainQuery = _mainQuery;
            }
            return this;
        }

        @Override
        public Query rewrite(IndexReader reader) throws IOException {
            return super.rewrite(reader);
        }

        public MergeStrategy getMergeStrategy() {
            return null;
        }

        public TopDocsCollector<ScoreDoc> getTopDocsCollector(int len, QueryCommand cmd, IndexSearcher searcher) throws IOException {
            // FieldValueHitQueue<FieldValueHitQueue.Entry> queue = //
            // FieldValueHitQueue.create(cmd.getSort().getSort(), len);
            PriorityQueue<ScoreDoc> queue = TISHitQueue.create(len);
            // Float.NEGATIVE_INFINITY));
            return new ShufferCollector(queue, cmd, searcher);
        }

        private class ShufferCollector extends TopDocsCollector<ScoreDoc> {

            // float maxScore = Float.NaN;
            // 
            // final int numHits;
            // FieldValueHitQueue.Entry bottom = null;
            // boolean queueFull;
            // int docBase;
            // final boolean needsScores;
            ScoreDoc pqTop;

            // private Query originQuery;
            private IndexSearcher searcher;

            public ShufferCollector(// Query reRankQuery,
            PriorityQueue<ScoreDoc> queue, QueryCommand cmd, IndexSearcher searcher) throws IOException {
                super(queue);
                // this.originQuery = reRankQuery;
                this.searcher = searcher;
                this.pqTop = pq.top();
            }

            @Override
            public LeafCollector getLeafCollector(LeafReaderContext context) throws IOException {
                final int docBase = context.docBase;
                return new ScorerLeafCollector() {

                    @Override
                    public void collect(int doc) throws IOException {
                        // 这样可以让score产生随机性，理论上来说 可以打乱结果
                        float score = scorer.score() + (doc % mod) * multi;
                        // This collector cannot handle these scores:
                        assert score != Float.NEGATIVE_INFINITY;
                        assert !Float.isNaN(score);
                        totalHits++;
                        if (score <= pqTop.score) {
                            // documents with lower doc Ids. Therefore reject those docs too.
                            return;
                        }
                        pqTop.doc = doc + docBase;
                        pqTop.score = score;
                        pqTop = pq.updateTop();
                    }
                };
            }

            @Override
            public boolean needsScores() {
                return true;
            }
            // final void add(int slot, int doc, float score) {
            // bottom = pq.add(new Entry(slot, docBase + doc, score));
            // queueFull = totalHits == numHits;
            // }
            // 
            // final void updateBottom(int doc) {
            // // bottom.score is already set to Float.NaN in add().
            // bottom.doc = docBase + doc;
            // bottom = pq.updateTop();
            // }
            // 
            // final void updateBottom(int doc, float score) {
            // bottom.doc = docBase + doc;
            // bottom.score = score;
            // bottom = pq.updateTop();
            // }
            // 
            // @Override
            // public boolean needsScores() {
            // 
            // return false;
            // }
        }
        // @Override
        // public String toString(String s) {
        // final StringBuilder sb = new StringBuilder(100); // default initialCapacity
        // of 16 won't be enough
        // sb.append("{!").append(NAME);
        // sb.append(" mainQuery='").append(mainQuery.toString()).append("' ");
        // sb.append(RERANK_QUERY).append("='").append(originQuery.toString()).append("'
        // ");
        // sb.append(RERANK_DOCS).append('=').append(reRankDocs).append(' ');
        // sb.append(RERANK_WEIGHT).append('=').append(reRankWeight).append('}');
        // return sb.toString();
        // }
        // public Query rewrite(IndexReader reader) throws IOException {
        // return super.rewrite(reader);
        // }
        // public Weight createWeight(IndexSearcher searcher, boolean needsScores)
        // throws IOException {
        // return new ReRankWeight(mainQuery, originQuery, reRankWeight, searcher,
        // needsScores);
        // }
    }

    abstract static class ScorerLeafCollector implements LeafCollector {

        Scorer scorer;

        @Override
        public void setScorer(Scorer scorer) throws IOException {
            this.scorer = scorer;
        }
    }
    // private class ReRankWeight extends Weight {
    // private Query reRankQuery;
    // private IndexSearcher searcher;
    // private Weight mainWeight;
    // private double reRankWeight;
    // 
    // public ReRankWeight(Query mainQuery, Query reRankQuery, double reRankWeight,
    // IndexSearcher searcher,
    // boolean needsScores) throws IOException {
    // super(mainQuery);
    // this.reRankQuery = reRankQuery;
    // this.searcher = searcher;
    // this.reRankWeight = reRankWeight;
    // this.mainWeight = mainQuery.createWeight(searcher, needsScores);
    // }
    // 
    // @Override
    // public void extractTerms(Set<Term> terms) {
    // this.mainWeight.extractTerms(terms);
    // 
    // }
    // 
    // // public float getValueForNormalization() throws IOException {
    // // return mainWeight.getValueForNormalization();
    // // }
    // 
    // // public Scorer scorer(LeafReaderContext context) throws IOException {
    // // return mainWeight.scorer(context);
    // // }
    // 
    // // public void normalize(float norm, float topLevelBoost) {
    // // mainWeight.normalize(norm, topLevelBoost);
    // // }
    // 
    // // public Explanation explain(LeafReaderContext context, int doc) throws
    // // IOException {
    // // Explanation mainExplain = mainWeight.explain(context, doc);
    // // return new QueryRescorer(reRankQuery) {
    // // @Override
    // // protected float combine(float firstPassScore, boolean secondPassMatches,
    // // float secondPassScore) {
    // // float score = firstPassScore;
    // // if (secondPassMatches) {
    // // score += reRankWeight * secondPassScore;
    // // }
    // // return score;
    // // }
    // // }.explain(searcher, mainExplain, context.docBase + doc);
    // // }
    // }
}
