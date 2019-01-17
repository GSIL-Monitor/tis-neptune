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
import com.qlangtech.tis.solrj.extend.BasicTestCase;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.solr.client.solrj.io.Tuple;
import org.apache.solr.client.solrj.io.stream.CloudSolrStream;
import org.apache.solr.client.solrj.io.stream.InnerJoinStream;
import org.apache.solr.client.solrj.io.stream.ParallelStream;
import org.apache.solr.client.solrj.io.stream.RankStream;
import org.apache.solr.client.solrj.io.stream.ReducerStream;
import org.apache.solr.client.solrj.io.stream.UniqueStream;
import org.apache.solr.client.solrj.io.stream.expr.StreamFactory;
import java.util.concurrent.atomic.AtomicInteger;

/* *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class TestTransformFactory extends BasicTestCase {

    public void testTransformFactory() throws Exception {
        CloudSolrClient solrClient = new CloudSolrClient("10.1.6.65:2181,10.1.6.67:2181,10.1.6.80:2181/tis/cloud");
        long time1 = System.currentTimeMillis();
        // 1. stream response
        // solrClient.connect();
        // long time1 = System.currentTimeMillis();
        // AtomicInteger count = new AtomicInteger();
        // SolrQuery query = new SolrQuery();
        // query.setQuery("type:i");
        // query.setFields("id", "i_name", "i_kindmenu_id", "i_num", "i_fee",
        // "i_ratio_fee", "i_unit", "i_account_num",
        // "i_account_unit", "curr_date", "[parent parentFilter=type:t f=curr_date]");
        // query.setRows(1000);
        // query.addSort("id", SolrQuery.ORDER.asc);
        // solrClient.queryAndStreamResponse("search4totalpay6", query, new
        // StreamingResponseCallback() {
        // @Override
        // public void streamSolrDocument(SolrDocument doc) {
        // count.incrementAndGet();
        // // if (count.get() == 1) {
        // // for (String field : doc.getFieldNames()) {
        // // String field = "i_name";
        // // }
        // // System.out.println(field + ":" + doc.getFieldValue(field)); }
        // // System.out.println("id:" + doc.getFieldValue("id"));
        // System.out.println(doc.getFieldValue("id"));
        // // }
        // }
        // @Override
        // public void streamDocListInfo(long numFound, long start, Float
        // maxScore) {
        // }
        // });
        // SolrQuery q = new SolrQuery();
        // q.setQuery("entity_id:[* TO *]");
        // q.add("qt", "/select");
        // q.setFields("entity_id", "shop_kind", "coordinate");
        // QueryResponse response = solrClient.query("search4shop", q);
        // SolrDocumentList docs = response.getResults();
        // System.out.println("NumFound:" + docs.getNumFound());
        // for (SolrDocument doc : docs) {
        // System.out.println();
        // for (String field : doc.getFieldNames()) {
        // System.out.println(field + ":" + doc.getFieldValue(field));
        // }
        // System.out.println();
        // }
        // delete
        // solrClient.deleteById("search4totalpay6", "0000112553125c3f01531276577c08a6");
        // solrClient.deleteById("search4totalpay6", "d0689b8e5a5947d8ae10909440bf8ed6 ");
        // add
        /*
        SolrInputDocument d = new SolrInputDocument();
        d.addField("id", "00000793517c358601519e6f528201d3");
        d.addField("type", "t");
        d.addField("curr_date", "115");
        d.addField("entity_id", "99001125");
        d.addField("_version_", "30161025001000");
        d.addField("_root_", "00000793517c358601519e6f528201d3");
        d.addField("i_name", "wohenhao112");
        SolrInputDocument c = new SolrInputDocument();
        c.addField("id", "131");
        c.addField("_root_", "00000793517abebd01517b4434db0184");
        c.addField("i_name", "hongshaorou");
        c.addField("type", "i");
        d.addChildDocument(c);
        solrClient.add("search4totalpay6", d);
        solrClient.close();
        */
        // Query q = new MatchAllDocsQuery();
        // System.out.println(q.toString());
        // System.out.println(Sort.RELEVANCE.toString());
        // System.out.println(StringUtils.rightPad("title", 30));
        // System.out.println(StringUtils.center("pubmonth", 10));
        // SolrInputDocument d = new SolrInputDocument();
        // d.addField("id", "00000793517c358601519e6f528201d3");
        // d.addField("type", "p");
        // d.addField("entity_id", "99001125");
        // d.addField("_version_", "30161025001002");
        // d.addField("last_ver", 1);
        // d.addField("name", "wohenhao112");
        // SolrInputDocument c = new SolrInputDocument();
        // c.addField("id", "131");
        // c.addField("sku_spec_name", "hongshaorou");
        // c.addField("type", "c");
        // d.addChildDocument(c);
        // solrClient.add("search4supplyCommodity", d);
        // solrClient.deleteById("search4supplyCommodity", "00000793517c358601519e6f528201d3");
        // solrClient.close();
        // 2. Streaming Expressions
        String zkHost = solrClient.getZkHost();
        StreamFactory streamFactory = new StreamFactory().withCollectionZkHost("search4totalpay6", zkHost).withFunctionName("search", CloudSolrStream.class).withFunctionName("unique", UniqueStream.class).withFunctionName("top", RankStream.class).withFunctionName("group", ReducerStream.class).withFunctionName("parallel", ParallelStream.class).withFunctionName("innerJoin", InnerJoinStream.class);
        String expr = "search(search4totalpay6, qt=\"/export\", q=\"type:i\", fl=\"id, i_name [parent parentFilter=type:t extraFields=discount_amount]\", sort=\"id asc\")";
        // String expr = "search(search4totalpay6, qt=\"/select\", q=\"id:0000084250eba09a0150ebc4262800bf\", fl=\"id, i_name, discount_amount, seat_id [parentWithRoot parentFilter=type:t extraFields=discount_amount,seat_id]\", sort=\"id desc\")";
        // String expr = "search(search4totalpay6, qt=\"/export\", q=\"type:i\", fl=\"id, i_name, _root_\", sort=\"id asc\")";
        // String search1 = "search(search4totalpay, qt=\"/export\", q=\"type:t\", fl=\"id, discount_amount, _root_\", sort=\"_root_ asc\")";
        // String search2 = "search(search4totalpay, qt=\"/export\", q=\"type:i\", fl=\"id, i_name, _root_\", sort=\"_root_ asc\")";
        // String expr = String.format("innerJoin(%s,%s,on=\"_root_\")", search1, search2);
        System.out.println(expr);
        CloudSolrStream joinStream = (CloudSolrStream) streamFactory.constructStream(expr);
        joinStream.open();
        AtomicInteger cnt = new AtomicInteger(0);
        while (true) {
            Tuple tuple = joinStream.read();
            if (tuple.EOF) {
                break;
            } else {
                // System.out.println(tuple.toString());
                if (cnt.incrementAndGet() < 1000000) {
                    System.out.println("-----------timer is " + cnt.get());
                    for (Object key : tuple.getMap().keySet()) {
                        System.out.println(String.valueOf(key) + ":" + String.valueOf(tuple.getString(key)));
                    }
                    System.out.println();
                }
            }
        }
        joinStream.close();
        // 3. childDocTransformer
        // SolrQuery query = new SolrQuery();
        // query.setQuery("id:00000793517abebd01517bb89b0301bf");
        // query.set("qt", "/export");
        // query.setFields("id", "curr_date", "[parent parentFilter=type:t
        // f=curr_date]");
        // query.addSort("id", SolrQuery.ORDER.desc);
        // query.add("rows", "100");
        // solrClient.connect();
        // AtomicInteger count = new AtomicInteger();
        // solrClient.queryAndStreamResponse("search4totalpay6", query, new
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
        // public void streamDocListInfo(long numFound, long start, Float
        // maxScore) {
        // }
        // });
        // solrClient.close();
        long time2 = System.currentTimeMillis();
        System.out.println("cost time " + (time2 - time1) + " ms");
    }

    public static void main(String[] args) {
    // SolrQuery q = new SolrQuery();
    // q.setQuery("title:[* TO *]");
    // q.setFields("id", "title", "content_type", "[child
    // parentFilter=content_type:parentDocument]");
    // 
    // QueryResponse response = server.query(q);
    // 
    // SolrDocumentList docs = response.getResults();
    // 
    // System.out.println("NumFound:" + docs.getNumFound());
    // 
    // for (SolrDocument doc : docs) {
    // System.out.println("id:" + val(doc.getFirstValue("id")) + ",title:" +
    // val(doc.getFirstValue("title"))
    // + ",type:" + val(doc.getFirstValue("content_type")));
    // 
    // for (SolrDocument c : doc.getChildDocuments()) {
    // System.out.println(">id:" + val(c.getFirstValue("id")) + ",title:" +
    // val(c.getFirstValue("title"))
    // + ",type:" + val(c.getFirstValue("content_type")));
    // }
    // }
    }
}
