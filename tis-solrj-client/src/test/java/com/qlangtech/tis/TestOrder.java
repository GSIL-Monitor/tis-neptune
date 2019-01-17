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
package com.qlangtech.tis;

import com.qlangtech.tis.solrj.extend.TisCloudSolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.common.params.UpdateParams;

/* *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class TestOrder {

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        TisCloudSolrClient client = new TisCloudSolrClient("zk1.2dfire-inc.com:2181,zk2.2dfire-inc.com:2181,zk3.2dfire-inc.com:2181/tis/cloud");
        while (true) {
            SolrQuery query = new SolrQuery();
            query.setRows(0);
            query.set("isShard", "false");
            query.setQuery("entity_id:00034093 AND curr_date:[20151001 TO 20160115]");
            query.set(UpdateParams.COLLECTION, "search4totalpay");
            query.setDistrib(false);
            query.set("mr", "true");
            query.set("all_consume_dim_statis_component", "true");
            System.out.println("query:" + query.toString());
            // query.addSort(new SortClause("ddd",ORDER.asc));
            // query.addSort(new SortClause("ddd",ORDER.asc));
            TisCloudSolrClient.SimpleQueryResult<Object> result = client.query("search4totalpay", "00034093", query, Object.class);
            System.out.println(result.getNumberFound());
            System.out.println("================================================");
            System.out.println(result.getResponse().getResponse().toString());
            System.out.println("================================================");
            System.out.println(result.getResponse().getResponse().get("all_dim_statis"));
            Thread.sleep(500);
        }
    }
}
