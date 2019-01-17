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
package com.qlangtech.tis.solrextend.queryparse.s4menuNameSuggestion;

import org.apache.lucene.index.DocValues;
import org.apache.lucene.index.SortedDocValues;
import org.apache.lucene.search.Query;
import org.apache.lucene.util.FixedBitSet;
import org.apache.solr.common.SolrException;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.search.LuceneQParserPlugin;
import org.apache.solr.search.QParser;
import org.apache.solr.search.SyntaxError;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/* *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class MenuNameSuggestionQParserPlugin extends LuceneQParserPlugin {

    private int DEFAULT_SUGGESTIONS_NUM = 10;

    private static final String name_origin = "name_origin";

    private int maxDocsToCollect = 5000;

    @Override
    public QParser createParser(String qstr, SolrParams localParams, SolrParams params, SolrQueryRequest req) {
        try {
            QParser parser = super.createParser(qstr, localParams, params, req);
            Query sortedQuery = parser.parse();
            final FixedBitSet bitSet = new FixedBitSet(req.getSearcher().getIndexReader().maxDoc());
            SortedDocValues orderidVals = null;
            orderidVals = DocValues.getSorted(req.getSearcher().getLeafReader(), name_origin);
            TopNSuggestionCollector collector = new TopNSuggestionCollector(orderidVals, maxDocsToCollect);
            req.getSearcher().search(sortedQuery, collector);
            Map<Order2Doc, AtomicInteger> orderIdCount = collector.getOrderIdCount();
            Map<Integer, Integer> /*doc_id,count*/
            doc2freq = new HashMap();
            MenuNamePriorityQueue menuNamePriorityQueue = new MenuNamePriorityQueue(DEFAULT_SUGGESTIONS_NUM);
            for (Map.Entry<Order2Doc, AtomicInteger> entry : orderIdCount.entrySet()) {
                menuNamePriorityQueue.insertWithOverflow(entry);
            }
            while (menuNamePriorityQueue.size() > 0) {
                Map.Entry<Order2Doc, AtomicInteger> entry = menuNamePriorityQueue.pop();
                Order2Doc order2Doc = entry.getKey();
                int docId = order2Doc.getDocId();
                doc2freq.put(docId, entry.getValue().get());
                bitSet.set(docId);
            }
            final FreqBitQuery freqBitQuery = new FreqBitQuery(bitSet, doc2freq, qstr.hashCode());
            QParser qParser = new QParser(qstr, localParams, params, req) {

                @Override
                public Query parse() throws SyntaxError {
                    return freqBitQuery;
                }
            };
            return qParser;
        } catch (Exception e) {
            throw new SolrException(SolrException.ErrorCode.SERVER_ERROR, e);
        }
    }
}
