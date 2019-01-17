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
package com.qlangtech.tis.solrextend.queryparse.s4userRecommend;

import com.qlangtech.tis.solrextend.queryparse.BitQuery;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.*;
import org.apache.lucene.util.BitDocIdSet;
import org.apache.lucene.util.BitSet;
import org.apache.solr.core.SolrCore;
import org.slf4j.Logger;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

/* *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class UserRecommendBitQuery extends BitQuery {

    private int hashcode;

    private Map<Long, Double> id2Score;

    private static final Logger log = SolrCore.requestLog;

    private final DocIdSetIterator docIdSetIterator;

    public UserRecommendBitQuery(BitSet bitSet, int hashcode, Map<Long, Double> id2Score) {
        super(bitSet);
        BitDocIdSet docIdSet = new BitDocIdSet(bitSet);
        this.docIdSetIterator = docIdSet.iterator();
        this.hashcode = hashcode;
        this.id2Score = id2Score;
    }

    @Override
    protected float getScore(int docid) {
        return 1.0f;
    // if (id2Score.keySet().contains((long) docid)) {
    // log.info("docId:[" + docid + "] get a score:[" + id2Score.get((long) docid) + "]");
    // double score = id2Score.get((long) docid);
    // return (float) score;
    // } else {
    // log.info("docId:[" + docid + "] can not get a score...");
    // return 0.0f;
    // }
    }

    @Override
    public Weight createWeight(IndexSearcher searcher, boolean needsScores) throws IOException {
        // log.info("docBase:[" + docBase + "]");
        return new UserRecommendBitQueryWeight(this);
    }

    @Override
    public String toString(String field) {
        return super.toString(field);
    }

    @Override
    public int hashCode() {
        return this.hashcode;
    }

    final class UserRecommendBitQueryWeight extends Weight {

        private UserRecommendBitQueryWeight(Query query) {
            super(query);
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
            return new UserRecommendBitScorer(this, context);
        }

        @Override
        public String toString() {
            return "weight(" + UserRecommendBitQueryWeight.this + ")";
        }
    }

    final class UserRecommendBitScorer extends Scorer {

        // 对应该段的maxDoc
        final int maxDoc;

        final int docBase;

        @Override
        public DocIdSetIterator iterator() {
            return new DocIdSetIterator() {

                @Override
                public int docID() {
                    return doc;
                }

                @Override
                public int nextDoc() throws IOException {
                    doc = advance(doc + 1);
                    return doc;
                }

                /**
                 * 返回第一个大于等于target的docId
                 */
                @Override
                public int advance(int target) throws IOException {
                    if (target >= maxDoc) {
                        doc = NO_MORE_DOCS;
                        return NO_MORE_DOCS;
                    }
                    doc = docIdSetIterator.advance(target + docBase) - docBase;
                    if (doc >= maxDoc) {
                        doc = NO_MORE_DOCS;
                        return NO_MORE_DOCS;
                    } else {
                        return doc;
                    }
                }

                @Override
                public long cost() {
                    return 0;
                }
            };
        }

        UserRecommendBitScorer(Weight weight, LeafReaderContext context) {
            super(weight);
            docBase = context.docBase;
            log.info("docBase:[" + docBase + "]");
            maxDoc = context.reader().maxDoc();
        }

        private int doc = -1;

        // @Override
        // public void score(Collector c) throws IOException {
        // log.warn("score(Collector c) invoke!");
        // score(c, Integer.MAX_VALUE, nextDoc());
        // }
        // @Override
        // protected boolean score(Collector c, int end, int firstDocID)
        // throws IOException {
        // log.warn("score(Collector c, int end, int firstDocID) invoke!");
        // c.setScorer(this);
        // if (firstDocID == NO_MORE_DOCS || doc == NO_MORE_DOCS)
        // return true;
        // while (doc < end) { // for docs in window
        // // log.warn("doc:"+doc+",docBase:"+docBase);
        // 
        // c.collect(doc); // collect score
        // int tdoc = nextDoc();
        // if (tdoc == NO_MORE_DOCS) {
        // break;
        // } else {
        // doc = tdoc;
        // }
        // 
        // }
        // return true;
        // }
        @Override
        public int docID() {
            return doc == -1 ? doc : (doc + docBase);
        }

        @Override
        public int freq() throws IOException {
            return 0;
        }

        @Override
        public float score() {
            return getScore(docID());
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
