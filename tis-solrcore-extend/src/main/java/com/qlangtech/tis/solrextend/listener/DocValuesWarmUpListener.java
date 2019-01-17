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
package com.qlangtech.tis.solrextend.listener;

import org.apache.lucene.index.DocValues;
import org.apache.solr.common.SolrException;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.core.AbstractSolrEventListener;
import org.apache.solr.core.SolrCore;
import org.apache.solr.search.SolrIndexSearcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/* *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class DocValuesWarmUpListener extends AbstractSolrEventListener {

    private static final Logger logger = LoggerFactory.getLogger(DocValuesWarmUpListener.class);

    private List<String> fields;

    private ExecutorService executorService;

    public DocValuesWarmUpListener(SolrCore core) {
        super(core);
    }

    @Override
    public void init(NamedList args) {
        super.init(args);
        fields = (List) (args.get("fields"));
        executorService = Executors.newFixedThreadPool(fields.size() * 3);
    }

    @Override
    public void newSearcher(SolrIndexSearcher newSearcher, SolrIndexSearcher currentSearcher) {
        long start = System.currentTimeMillis();
        CountDownLatch countDownLatch = new CountDownLatch(fields.size());
        try {
            // 排序所需要用到的字段,用一个线程池多线程预热
            for (int i = 0; i < fields.size(); i++) {
                WarmUpDocValueThread warmUpDocValueThread = new WarmUpDocValueThread(fields.get(i), newSearcher, countDownLatch);
                executorService.execute(warmUpDocValueThread);
            }
            countDownLatch.await();
        } catch (Exception e) {
            throw new SolrException(SolrException.ErrorCode.INVALID_STATE, e);
        }
    // finally {
    // logger.info("warming up consumes ["+(System.currentTimeMillis()-start)+"]
    // Millis");
    // }
    }

    class WarmUpDocValueThread implements Runnable {

        private String field;

        private SolrIndexSearcher newSearcher;

        private CountDownLatch countDownLatch;

        public WarmUpDocValueThread(String field, SolrIndexSearcher newSearcher, CountDownLatch countDownLatch) {
            this.field = field;
            this.newSearcher = newSearcher;
            this.countDownLatch = countDownLatch;
        }

        @Override
        public void run() {
            try {
                DocValues.getSorted(this.newSearcher.getLeafReader(), this.field);
            } catch (IOException e) {
                logger.error("warm up thread catch a exception", e);
            } finally {
                this.countDownLatch.countDown();
            }
        }
    }
}
