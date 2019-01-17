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
import com.qlangtech.tis.solrj.extend.BasicTestCase;

/* *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class TestRQuery extends BasicTestCase {

    public void testRquery() throws Exception {
        SolrQuery solrQuery = new SolrQuery();
        StringBuilder idStr = new StringBuilder("is_valid:1 AND {!terms f=id}");
        idStr.append("000000006492817901649281d0380004,9992654455288a2b0155438eb12f00da,9992654461dbc2ad0161df5d152300fe,9992654355288a2b01553eaee9e400cb,9993236363d8ecba01641bf7d08c095f");
        solrQuery.setQuery(idStr.toString());
        solrQuery.setRows(5);
        solrQuery.setRequestHandler("/rquery");
        System.out.println("start query");
        QueryResponse response = client.mergeQuery("search4product", solrQuery, false);
        for (SolrDocument doc : response.getResults()) {
            System.out.println(doc.getFieldValue("id"));
        }
    }
}
