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
package com.qlangtech.tis.supplyGoods;

import java.util.concurrent.atomic.AtomicInteger;
import org.apache.solr.client.solrj.io.Tuple;
import org.apache.solr.client.solrj.io.stream.CloudSolrStream;
import org.apache.solr.client.solrj.io.stream.ComplementStream;
import org.apache.solr.client.solrj.io.stream.InnerJoinStream;
import org.apache.solr.client.solrj.io.stream.LeftOuterJoinStream;
import org.apache.solr.client.solrj.io.stream.RankStream;
import org.apache.solr.client.solrj.io.stream.ReducerStream;
import org.apache.solr.client.solrj.io.stream.UniqueStream;
import org.apache.solr.client.solrj.io.stream.expr.StreamFactory;
import com.qlangtech.tis.solrj.extend.BasicTestCase;
import com.qlangtech.tis.solrj.extend.CountStream;
import com.qlangtech.tis.solrj.io.stream.ExtendCloudSolrStream;
import com.qlangtech.tis.solrj.io.stream.NotExistStream;
import com.qlangtech.tis.solrj.io.stream.expr.StreamFactoryWithClient;

/*
 * Created by Qinjiu(Qinjiu@2dfire.com) on 2/24/2017.
 *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class TestStreamExpression extends BasicTestCase {

    private static StreamFactory streamFactory;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        streamFactory = new StreamFactoryWithClient().withExtendClient(this.client.getExtendSolrClient()).withFunctionName("searchExtend", ExtendCloudSolrStream.class).withFunctionName("search", // 
        CloudSolrStream.class).withFunctionName("unique", UniqueStream.class).withFunctionName("top", RankStream.class).withFunctionName("group", ReducerStream.class).withFunctionName("innerJoin", InnerJoinStream.class).withFunctionName("leftOuterJoin", LeftOuterJoinStream.class).withFunctionName("complement", ComplementStream.class).withFunctionName("notExist", NotExistStream.class).withFunctionName("count", CountStream.class).withFunctionName("unique", UniqueStream.class);
    }

    public void testStream() throws Exception {
        String expr = "search(search4supplyGoods, qt=\"/export\", q=\"entity_id:99933218\", fl=\"id,create_time,docid:[docid f=docid]\", sort=\"create_time asc\")";
        System.out.println(expr);
        CloudSolrStream joinStream = (CloudSolrStream) streamFactory.constructStream(expr);
        joinStream.open();
        AtomicInteger cnt = new AtomicInteger(0);
        while (true) {
            System.out.println("read line");
            Tuple tuple = joinStream.read();
            if (tuple.EOF) {
                break;
            } else {
                if (cnt.incrementAndGet() < 100) {
                    System.out.println("-----------timer is " + cnt.get());
                    for (Object key : tuple.getMap().keySet()) {
                        System.out.println(String.valueOf(key) + ":" + String.valueOf(tuple.getString(key)));
                    }
                    System.out.println();
                }
            }
        }
        joinStream.close();
    }

    public void testClient() throws Exception {
    // ExtendCloudSolrClient solrClient = new
    // ExtendCloudSolrClient("10.1.6.65:2181,10.1.6.67:2181,10.1.6.80:2181/tis/cloud");
    // SolrQuery query = new SolrQuery();
    // query.setQuery("entity_id:99928910");
    // query.set("qt", "/export");
    // query.setSort("create_time", SolrQuery.ORDER.asc);
    // query.setFields("id,create_time,docid:[docid f=docid]");
    // query.set(ExtendCloudSolrClient.SINGLE_SLICE_QUERY, true);
    // query.set(ShardParams._ROUTE_, "99928910");
    // AtomicInteger count = new AtomicInteger();
    // solrClient.queryAndStreamResponse("search4supplyGoods", query, new
    // StreamingResponseCallback() {
    // @Override
    // public void streamSolrDocument(SolrDocument doc) {
    // count.incrementAndGet();
    // System.out.println();
    // System.out.println("---------------" + count.get());
    // for (String field : doc.getFieldNames()) {
    // System.out.println("field:" + field + ":" +
    // doc.getFieldValue(field));
    // }
    // System.out.println("---------------");
    // System.out.println();
    // }
    // 
    // @Override
    // public void streamDocListInfo(long numFound, long start, Float maxScore) {
    // 
    // }
    // }
    // );
    }
    // public void testClient1() throws Exception {
    // String expr = "top(n=3, leftOuterJoin(\n" +
    // " searchExtend(search4supplyGoods, qt=\"/export\", _route_=99928370,
    // q=_query_:\"{!topNField rowCount=3 sort=create_time
    // order=asc}entity_id:99926350 AND is_valid:1\",
    // fl=\"id,entity_id,create_time\", sort=\"id asc\"),\n" +
    // " unique(searchExtend(search4supplyUnionTabs, qt=\"/export\",
    // _route_=99928370, q=entity_id:99928370, fl=\"goods_id\", sort=\"goods_id
    // asc\"), over=\"goods_id\"),\n" +
    // " on=\"id=goods_id\"\n" +
    // " ), sort=\"create_time asc\")";
    // String expr = "top(n=3, leftOuterJoin(\n" +
    // " searchExtend(search4supplyGoods, qt=\"/export\", _route_=99928370,
    // q=_query_:\"{!topNField rowCount=3 sort=create_time order=asc afterId=55984
    // afterValue=20160830111700948}entity_id:99928370\",
    // fl=\"id,entity_id,create_time\", sort=\"id asc\"),\n" +
    // " unique(searchExtend(search4supplyUnionTabs, qt=\"/export\",
    // _route_=99928370, q=entity_id:99928370, fl=\"goods_id\", sort=\"goods_id
    // asc\"), over=\"goods_id\"),\n" +
    // " on=\"id=goods_id\"\n" +
    // " ), sort=\"create_time asc\")";
    // String expr = "search(search4supplyGoods, qt=\"/export\",
    // q=\"entity_id:99928910\", fl=\"id,create_time,docid:[docid f=docid]\",
    // sort=\"create_time asc\")";
    // System.out.println(expr);
    // 
    // 
    // RankStream joinStream = (RankStream) streamFactory.constructStream(expr);
    // CloudSolrStream joinStream = (CloudSolrStream)
    // streamFactory.constructStream(expr);
    // joinStream.open();
    // AtomicInteger cnt = new AtomicInteger(0);
    // while (true) {
    // Tuple tuple = joinStream.read();
    // if (tuple.EOF) {
    // break;
    // } else {
    // if (cnt.incrementAndGet() < 100) {
    // System.out.println("-----------timer is " + cnt.get());
    // for (Object key : tuple.getMap().keySet()) {
    // System.out.println(String.valueOf(key) + ":" +
    // String.valueOf(tuple.getString(key)));
    // }
    // System.out.println();
    // }
    // }
    // }
    // joinStream.close();
    // }
    // 
    // public void testParse() throws Exception {
    // DocumentBuilderFactory schemaDocumentBuilderFactory =
    // DocumentBuilderFactory.newInstance();
    // schemaDocumentBuilderFactory.setValidating(true);
    // }
}
