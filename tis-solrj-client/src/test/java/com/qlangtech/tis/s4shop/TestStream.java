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

import java.util.Map;
import org.apache.solr.client.solrj.io.Tuple;
import org.apache.solr.client.solrj.io.stream.CloudSolrStream;
import org.apache.solr.client.solrj.io.stream.ParallelStream;
import org.apache.solr.client.solrj.io.stream.RankStream;
import org.apache.solr.client.solrj.io.stream.ReducerStream;
import org.apache.solr.client.solrj.io.stream.UniqueStream;
import org.apache.solr.client.solrj.io.stream.expr.StreamFactory;
import junit.framework.TestCase;

/* *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class TestStream extends TestCase {

    public void testSearchStream() throws Exception {
        String zkHost = "10.1.6.65:2181,10.1.6.67:2181,10.1.6.80:2181/tis/cloud";
        StreamFactory streamFactory = new StreamFactory().withCollectionZkHost("search4_fat_instance", zkHost).withFunctionName("search", CloudSolrStream.class).withFunctionName("unique", UniqueStream.class).withFunctionName("top", RankStream.class).withFunctionName("group", ReducerStream.class).withFunctionName("parallel", ParallelStream.class);
        String searchCommand = // /////////////////////////
        "search(search4_fat_instance," + "zkHost=\"" + zkHost + // ///////////////////////////////////////
        "\"," + // ////////////////////////
        "qt=\"/export\"," + // ////////////////////////////////////////
        "q=\"*:*\"," + // /////////////////////////////
        "fl=\"account_num,in_fee,entity_id,in_status,od_curr_date,wh_name\"," + // ///////////////////////////
        "sort=\"entity_id asc\")";
        CloudSolrStream pstream = (CloudSolrStream) streamFactory.constructStream(searchCommand);
        Tuple tuple = null;
        while (true) {
            tuple = pstream.read();
            if (tuple.EOF) {
                break;
            } else {
                Map fields = tuple.getMap();
                for (Object key : fields.keySet()) {
                    System.out.print(key + ":" + fields.get(key) + ",");
                }
                System.out.println();
            }
        }
        pstream.close();
    }
}
