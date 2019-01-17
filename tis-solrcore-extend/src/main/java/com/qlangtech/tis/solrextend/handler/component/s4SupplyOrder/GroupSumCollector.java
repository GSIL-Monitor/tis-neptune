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
package com.qlangtech.tis.solrextend.handler.component.s4SupplyOrder;

import com.qlangtech.tis.solrextend.handler.component.s4SupplyOrder.docValueGetter.IDocValueGetter;
import org.apache.lucene.index.LeafReader;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.search.SimpleCollector;
import org.apache.solr.request.SolrQueryRequest;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/*
 * GroupSumComponent收集器，根据一个字段统计总和
 * Created by Qinjiu on 6/28/2017.
 *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class GroupSumCollector extends SimpleCollector {

    private final SolrQueryRequest req;

    private final Map<String, Number> resultMap;

    private IDocValueGetter groupDocValues;

    private IDocValueGetter sumDocValues;

    private final AtomicInteger cnt;

    private static final int COLLECT_MAX = 20000;

    public GroupSumCollector(SolrQueryRequest req, Map<String, Number> resultMap, IDocValueGetter groupDocValues, IDocValueGetter sumDocValues) {
        this.req = req;
        this.resultMap = resultMap;
        this.groupDocValues = groupDocValues;
        this.sumDocValues = sumDocValues;
        this.cnt = new AtomicInteger(0);
    }

    @Override
    protected void doSetNextReader(LeafReaderContext context) throws IOException {
        super.doSetNextReader(context);
        LeafReader reader = context.reader();
        this.groupDocValues.setDocValues(reader);
        this.sumDocValues.setDocValues(reader);
    }

    @Override
    public void collect(int doc) throws IOException {
        // 统计结果不超过2w个
        if (cnt.incrementAndGet() > COLLECT_MAX) {
            throw new IllegalStateException("collect cnt can not over " + COLLECT_MAX);
        }
        String key = this.groupDocValues.getString(doc);
        Number value = this.sumDocValues.getNumber(doc);
        resultMap.merge(key, value, (a, b) -> b.doubleValue() + a.doubleValue());
    }

    @Override
    public boolean needsScores() {
        return false;
    }
}
