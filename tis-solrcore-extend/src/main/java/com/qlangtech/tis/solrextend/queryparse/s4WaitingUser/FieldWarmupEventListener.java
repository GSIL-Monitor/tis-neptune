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

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.lucene.index.DocValues;
import org.apache.solr.core.AbstractSolrEventListener;
import org.apache.solr.core.SolrCore;
import org.apache.solr.search.SolrIndexSearcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * 为了防止查询的时候的顿挫感（每隔一个soft commit的周期就有一次RT非常高的查詢）<br>
 * 所以要在这里先作一次预热 <br>
 *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class FieldWarmupEventListener extends AbstractSolrEventListener {

    private static final Logger logger = LoggerFactory.getLogger(FieldWarmupEventListener.class);

    private static final ExecutorService executorService = Executors.newCachedThreadPool();

    public FieldWarmupEventListener(SolrCore core) {
        super(core);
    }

    @Override
    public void newSearcher(SolrIndexSearcher newSearcher, SolrIndexSearcher currentSearcher) {
        executorService.execute(new Runnable() {

            @Override
            public void run() {
                long start = System.currentTimeMillis();
                try {
                    // 这个调用在大数据量的时候非常耗时
                    // SortedDocValues orderidVals =
                    DocValues.getSorted(newSearcher.getLeafReader(), DistinctOrderQparserPlugin.order_id);
                    // NumericDocValues createtimeVals =
                    DocValues.getNumeric(newSearcher.getLeafReader(), DistinctOrderQparserPlugin.create_time);
                    // SortedDocValues pkVals =
                    DocValues.getSorted(newSearcher.getLeafReader(), DistinctOrderQparserPlugin.waitingorder_id);
                } catch (IOException e) {
                    throw new IllegalStateException(e);
                } finally {
                    logger.info("consume:" + (System.currentTimeMillis() - start) + "ms");
                }
            }
        });
    }
}
