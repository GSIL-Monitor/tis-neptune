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

import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.index.SortedDocValues;
import org.apache.lucene.search.SimpleCollector;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/* *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class TopNSuggestionCollector extends SimpleCollector {

    private Map<Order2Doc, AtomicInteger> /*order_id&doc_id,count*/
    orderIdCount = new HashMap();

    private SortedDocValues orderidVals = null;

    private final int maxDocsToCollect;

    private AtomicInteger numCollected = new AtomicInteger(0);

    public TopNSuggestionCollector(SortedDocValues orderidVals, int maxDocsToCollect) {
        this.orderidVals = orderidVals;
        this.maxDocsToCollect = maxDocsToCollect;
    }

    @Override
    public void collect(int doc) throws IOException {
        this.numCollected.incrementAndGet();
        if (this.maxDocsToCollect <= this.numCollected.get()) {
            int orderIdOrd = orderidVals.getOrd(doc);
            Order2Doc order2doc = new Order2Doc(orderIdOrd, doc);
            if (orderIdCount.get(order2doc) == null)
                orderIdCount.put(order2doc, new AtomicInteger(1));
            else {
                orderIdCount.get(order2doc).incrementAndGet();
            }
        }
    }

    public Map<Order2Doc, /*order_id&doc_id,count*/
    AtomicInteger> getOrderIdCount() {
        return this.orderIdCount;
    }

    @Override
    public boolean needsScores() {
        return false;
    }
}
