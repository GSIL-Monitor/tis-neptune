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
package com.qlangtech.tis.s4product;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import com.qlangtech.tis.solrj.extend.BasicTestCase;

/* *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class Test4Product extends BasicTestCase {

    public void testGetSpu() throws Exception {
        SolrQuery query = new SolrQuery();
        query.setQuery("id:0");
        query.setFields("*", "[child parentFilter=type:p childFilter=\"type:c\" limit=100]");
        query.set("id", "000995585a8dff68015a9e5216917fda");
        query.set("nestget", true);
        QueryResponse response = this.client.query("search4product", "0", query);
        System.out.println("docList.size:" + response.getResults().getNumFound());
    }
    // public void testSpuWithSKu() throws Exception {
    // 
    // SolrQuery query = new SolrQuery();
    // // query.setQuery("{!parent which=\"type:p\"}pp_size:xx");
    // query.setQuery("{!parent which=\"type:p\"}pp_size:xxl");
    // // query.setFields("id", "entity_id", "name", "[child parentFilter=type:p]");
    // query.setFields("id", "name", "props", "[child parentFilter=type:p]");
    // 
    // QueryResponse response = this.client.query("search4product", "0", query);
    // 
    // SolrDocumentList docList = response.getResults();
    // 
    // System.out.println("docList.size:" + docList.getNumFound());
    // 
    // for (SolrDocument doc : docList) {
    // System.out.println("id:" + doc.getFieldValue("id") + ",name:" + doc.getFieldValue("name"));
    // 
    // if (doc.hasChildDocuments()) {
    // for (SolrDocument c : doc.getChildDocuments()) {
    // System.out.println("child,props_index:" + c.getFieldValue("props_index"));
    // }
    // }
    // 
    // System.out.println("---------------------------------------");
    // 
    // }
    // }
}
