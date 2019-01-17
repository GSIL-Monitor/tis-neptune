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
package com.qlangtech.tis.s4supplyGoods;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.io.Tuple;
import com.qlangtech.tis.solrj.extend.BasicTestCase;
import com.qlangtech.tis.solrj.extend.AbstractTisCloudSolrClient.ResponseCallback;

/* *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class TestGoodsExport extends BasicTestCase {

    public void testExport() throws Exception {
        // _query_:\"{!topNField sort=spell,asc,string}entity_id:99933218 AND is_valid:1
        // AND ((goods_type:1 AND self_entity_ids:99933220) OR (goods_type:2 AND
        // self_entity_id:99933220))\"
        SolrQuery query = new SolrQuery();
        query.setQuery("_query_:\\\"{!topNField sort=spell,asc,string}entity_id:99933218 AND (bar_code_like:\\\\^ OR name:\\\\^)\\\"");
        query.setSort("id", ORDER.asc);
        query.setFields("id,spell,docid:[docid f=docid]");
        client.queryAndStreamResponse("search4supplyGoods", query, "99933218", new ResponseCallback<Tuple>() {

            @Override
            public void process(Tuple pojo) {
                System.out.println(pojo.getString("id"));
            }

            @Override
            public void lististInfo(long numFound, long start) {
                System.err.println("all find:" + numFound);
            }
        }, Tuple.class);
    }
}
