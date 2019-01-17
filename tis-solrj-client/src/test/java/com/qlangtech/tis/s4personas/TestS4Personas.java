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
package com.qlangtech.tis.s4personas;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import com.qlangtech.tis.solrj.extend.BasicTestCase;

/* *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class TestS4Personas extends BasicTestCase {

    public void testQueryParent() throws Exception {
        SolrQuery query = new SolrQuery();
        query.setQuery("id:3 AND _query_:\"{!child of=type:p}id:00005273c46440bb9684e698b87c6bc0\"");
        query.setFields("id", "monetary", "card_degree", "[parent parentFilter=type:p f=nick_name,customer_register_id,sex,city]");
        QueryResponse response = this.client.query("search4personas", "0", query);
        SolrDocumentList docList = response.getResults();
        System.out.println("numFound:" + docList.getNumFound());
        for (SolrDocument doc : docList) {
            System.out.println(doc.toString());
        // System.out.println("nick_name:"
        // + doc.getFieldValue("nick_name") + ",city:" +
        // doc.getFieldValue("city")+",customerid:"+doc.getFieldValue("customer_register_id")
        // );
        }
    }
    // public void testQueryParent() throws Exception {
    // SolrQuery query = new SolrQuery();
    // query.setQuery(
    // "months:11 AND openid_wx6b0a76e23e5ec2e5:T AND _query_:\"{!parent
    // which=type:p}monetary:[1 TO *]\"");
    // query.setFields("id", "openid_wx6b0a76e23e5ec2e5", "nick_name",
    // "customer_register_id", "sex");
    // 
    // QueryResponse response = this.client.query("search4personas", "0", query);
    // 
    // SolrDocumentList docList = response.getResults();
    // System.out.println("numFound:" + docList.getNumFound());
    // for (SolrDocument doc : docList) {
    // System.out.println("openid:" + doc.getFieldValue("openid_wx6b0a76e23e5ec2e5")
    // + ",nick_name:"
    // + doc.getFieldValue("nick_name"));
    // }
    // }
    // public void test() throws Exception {
    // SolrQuery query = new SolrQuery();
    // query.setQuery("monetary:[1 TO *] AND _query_:\"{!child of=type:p}(sex:2 AND
    // months:11)\"");
    // query.setFields("id", "monetary", "card_degree", "nick_name",
    // "customer_register_id", "sex",
    // "[parent parentFilter=type:p f=nick_name,customer_register_id,sex]");
    // 
    // QueryResponse response = this.client.query("search4personas", "0", query);
    // 
    // SolrDocumentList docList = response.getResults();
    // 
    // System.out.println("docList.size:" + docList.getNumFound());
    // 
    // for (SolrDocument doc : docList) {
    // System.out.println("id:" + doc.getFieldValue("id") + ",monetary:" +
    // doc.getFieldValue("monetary")
    // + ",card_degree:" + doc.getFieldValue("card_degree") + ",sex:" +
    // doc.getFieldValue("sex")
    // + ",nick_name:" + doc.getFieldValue("nick_name"));
    // 
    // // if (doc.hasChildDocuments()) {
    // // for (SolrDocument c : doc.getChildDocuments()) {
    // // System.out.println("child,props_index:" + c.getFieldValue("props_index"));
    // // }
    // // }
    // 
    // System.out.println("---------------------------------------");
    // 
    // }
    // }
}
