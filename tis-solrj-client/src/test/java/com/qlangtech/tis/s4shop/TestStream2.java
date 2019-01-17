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
package com.qlangtech.tis.s4shop;

import java.util.HashMap;
import java.util.Map;
import org.apache.solr.client.solrj.io.SolrClientCache;
import org.apache.solr.client.solrj.io.Tuple;
import org.apache.solr.client.solrj.io.stream.DaemonStream;
import org.apache.solr.client.solrj.io.stream.StreamContext;
import org.apache.solr.client.solrj.io.stream.TopicStream;
import junit.framework.TestCase;

/* *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class TestStream2 extends TestCase {

    public void testStream() throws Exception {
        StreamContext context = new StreamContext();
        SolrClientCache cache = new SolrClientCache();
        context.setSolrClientCache(cache);
        Map topicQueryParams = new HashMap();
        // The query for the topic
        topicQueryParams.put("q", "hello");
        // How many rows to fetch during
        topicQueryParams.put("rows", "500");
        // each run
        // The field list to return with
        topicQueryParams.put("fl", "id,title");
        // the documents
        String zkHost = "";
        TopicStream topicStream = new TopicStream(zkHost, "checkpoints", "topicData", "topicId", -1, topicQueryParams);
        DaemonStream daemonStream = new DaemonStream(topicStream, "daemonId", 1000, 500);
        daemonStream.setStreamContext(context);
        daemonStream.open();
        // shutdown criteria.
        while (!shutdown()) {
            Tuple tuple = daemonStream.read();
        }
        daemonStream.shutdown();
        while (true) {
            Tuple tuple = daemonStream.read();
            if (tuple.EOF) {
                break;
            } else {
            }
        }
        // Finally close the stream
        daemonStream.close();
    }

    private static boolean shutdown() {
        return false;
    }
}
