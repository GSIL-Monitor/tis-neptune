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
package com.qlangtech.tis.solrextend.queryparse.s4shop;

import java.io.IOException;
import java.util.Set;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.index.Term;
import org.apache.lucene.queries.CustomScoreQuery;
import org.apache.lucene.queries.function.FunctionQuery;
import org.apache.lucene.queries.function.valuesource.ConstValueSource;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.DocIdSetIterator;
import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.FilteredDocIdSetIterator;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.search.Weight;
import org.apache.lucene.util.BitSet;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.search.LuceneQParserPlugin;
import org.apache.solr.search.QParser;
import org.apache.solr.search.QParserPlugin;
import org.apache.solr.search.SyntaxError;

/*
 * 影响结果的Score，并不能影响结果的命中数目<br>
 * http://k.2dfire.net/pages/viewpage.action?pageId=518455605<br>
 *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class InServiceQparserPlugin extends QParserPlugin {

    private static final String LOCAL_PARAM_CURR_POS = "curr_pos";

    @Override
    public QParser createParser(String qstr, SolrParams localParams, SolrParams params, SolrQueryRequest req) {
        return new QParser(qstr, localParams, params, req) {

            @Override
            public Query parse() throws SyntaxError {
                String point = localParams.get(LOCAL_PARAM_CURR_POS);
                String[] latd = StringUtils.split(point, "_");
                if (point == null || latd.length != 2) {
                    throw new IllegalArgumentException("local param curr_pos is illegal ,point:" + point);
                }
                // .final String pointContain = "Contains(POINT(120.1596 30.2547))";
                final String pointContain = "Contains(POINT(" + latd[1] + " " + latd[0] + "))";
                StringBuffer q = new StringBuffer();
                q.append("service_flag:1 OR");
                q.append(" ( service_flag:8 AND pickup_range:\"").append(pointContain).append("\")");
                q.append(" OR ( service_flag:4 AND delivery_range:\"").append(pointContain).append("\")");
                q.append(" OR ( service_flag:2 AND gps_data:\"").append(pointContain).append("\")");
                // 影响查询得分
                QParser inserviceParser = getParser(q.toString(), LuceneQParserPlugin.NAME, getReq());
                Query inserviceQuery = inserviceParser.parse();
                // 命中记录查询Query
                QParser subQueryParser = getParser(qstr, null, getReq());
                Query subQuery = subQueryParser.parse();
                final BooleanQuery.Builder qbuilder = new BooleanQuery.Builder();
                qbuilder.add(inserviceQuery, Occur.MUST);
                qbuilder.add(subQuery, Occur.FILTER);
                return new InServiceScoreQuery(subQuery, new InServiceFunctionQuery(qbuilder.build()));
            }
        };
    }

    private static class InServiceScoreQuery extends CustomScoreQuery {

        private final InServiceFunctionQuery funcQuery;

        public InServiceScoreQuery(Query subQuery, InServiceFunctionQuery funcQuery) {
            super(subQuery, funcQuery);
            this.funcQuery = funcQuery;
        }

        // 需要覆写这个方法，不然坐标点即使变化之后Query会被缓存起来
        @Override
        public int hashCode() {
            return super.hashCode() + funcQuery.scoreQuery.hashCode();
        }

        @Override
        public Weight createWeight(IndexSearcher searcher, boolean needsScores) throws IOException {
            Weight funcWeight = funcQuery.createWeight(searcher, true);
            Weight subWeight = this.getSubQuery().createWeight(searcher, false);
            return new BitQueryWeight(this.getSubQuery(), subWeight, funcWeight);
        }
        // @Override
        // protected CustomScoreProvider getCustomScoreProvider(LeafReaderContext
        // context) throws IOException {
        // 
        // return new CustomScoreProvider(context) {
        // 
        // @Override
        // public float customScore(int doc, float subQueryScore, float valSrcScore)
        // throws IOException {
        // 
        // System.out.println("docid:"+ doc +",subQueryScore:" + subQueryScore +
        // "valSrcScore:" + valSrcScore);
        // 
        // return super.customScore(doc, subQueryScore, valSrcScore);
        // }
        // 
        // };
        // }
    }

    private static class InServiceFunctionQuery extends FunctionQuery {

        private Query scoreQuery;

        public InServiceFunctionQuery(Query scoreQuery) {
            super(new ConstValueSource(0));
            this.scoreQuery = scoreQuery;
        }

        @Override
        public Weight createWeight(IndexSearcher searcher, boolean needsScores) throws IOException {
            return scoreQuery.createWeight(searcher, true);
        }
    }

    static final class BitQueryWeight extends Weight {

        private final Weight funcWeight;

        private final Weight subWeight;

        private BitQueryWeight(Query query, Weight subWeight, Weight funcWeight) {
            super(query);
            this.funcWeight = funcWeight;
            this.subWeight = subWeight;
            if (this.subWeight == null) {
                throw new IllegalArgumentException("param subWeight can not be null");
            }
        }

        @Override
        public void extractTerms(Set<Term> terms) {
        }

        @Override
        public Explanation explain(LeafReaderContext context, int doc) throws IOException {
            return Explanation.noMatch("no avalible");
        }

        @Override
        public float getValueForNormalization() throws IOException {
            return 0;
        }

        @Override
        public void normalize(float norm, float topLevelBoost) {
        }

        @Override
        public Scorer scorer(LeafReaderContext context) throws IOException {
            Scorer funcScore = funcWeight.scorer(context);
            return new BitScorer(subWeight, context, funcScore);
        }

        @Override
        public String toString() {
            return "weight(" + BitQueryWeight.this + ")";
        }
    }

    static final class BitScorer extends Scorer {

        // 对应该段的maxDoc
        final int maxDoc;

        // final final int docBase = 0;
        // private final Scorer funcScore;
        private final BitSet funcBitset;

        // private final LeafReaderContext context;
        private final DocIdSetIterator docIdSetIterator;

        @Override
        public DocIdSetIterator iterator() {
            return new FilteredDocIdSetIterator(docIdSetIterator) {

                @Override
                protected boolean match(int doc) {
                    return setDoc(doc);
                }
            };
        // return new DocIdSetIterator() {
        // @Override
        // public int docID() {
        // return doc;
        // }
        // 
        // @Override
        // public int nextDoc() throws IOException {
        // doc = advance(doc + 1);
        // return doc;
        // }
        // 
        // /**
        // * 返回第一个大于等于target的docId
        // */
        // @Override
        // public int advance(int target) throws IOException {
        // if (target >= maxDoc) {
        // doc = NO_MORE_DOCS;
        // return NO_MORE_DOCS;
        // }
        // doc = docIdSetIterator.advance(target);
        // if (doc >= maxDoc) {
        // doc = NO_MORE_DOCS;
        // return NO_MORE_DOCS;
        // } else {
        // return doc;
        // }
        // }
        // 
        // @Override
        // public long cost() {
        // return 0;
        // }
        // };
        }

        BitScorer(Weight weight, LeafReaderContext context, Scorer funcScore) {
            super(weight);
            try {
                Scorer socre = weight.scorer(context);
                this.docIdSetIterator = socre == null ? DocIdSetIterator.empty() : socre.iterator();
                // this.context = context;
                // docBase = context.docBase;
                maxDoc = context.reader().maxDoc();
                this.funcBitset = (funcScore == null) ? BitSet.of(DocIdSetIterator.empty(), maxDoc) : BitSet.of(funcScore.iterator(), maxDoc);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        private int doc = -1;

        private boolean setDoc(int doc) {
            this.doc = doc;
            return true;
        }

        @Override
        public int docID() {
            return doc;
        }

        @Override
        public int freq() throws IOException {
            return 0;
        }

        @Override
        public float score() {
            return this.funcBitset.get(this.doc) ? 1 : 0;
        }

        /**
         * Returns a string representation of this <code>TermScorer</code>.
         */
        @Override
        public String toString() {
            return "scorer(" + weight + ")";
        }
    }
}
