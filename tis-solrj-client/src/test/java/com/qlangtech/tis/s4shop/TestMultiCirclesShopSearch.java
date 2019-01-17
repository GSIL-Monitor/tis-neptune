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

import com.qlangtech.tis.solrj.extend.BasicTestCase;
import com.qlangtech.tis.solrj.extend.TisCloudSolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import java.io.IOException;

/*
 * Created by . on 2018/4/20.
 *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class TestMultiCirclesShopSearch extends BasicTestCase {

    private static final String COLLECTION_SHOP = "search4shop";

    @SuppressWarnings("all")
    public void testQuery() throws Exception {
        SolrQuery query = new SolrQuery();
        String frompt = "30.29592,120.13432";
        query.setParam("pt", frompt);
        query.setParam("sfield", "coordinate");
        query.setRows(20);
        query.setFields("*", "dist:geodist()");
        // query.setFilterQueries(COORDINATE_FILTER_QUERY);
        // query.set("d",Integer.MAX_VALUE);
        // query.set("__val__","{!func}distScore()");
        // query.setParam("defType","func");
        // query.setParam("sort","{!func}distScore() desc");
        // query.setParam("bf","{!func}distScore()");
        // query.setParam("bf","distScore()");
        // query.setQuery("*:*");
        // query.setFilterQueries("__val","")
        query.setQuery("_query_:\"*:*\" _val_:\"{!func}distScore()\"");
        try {
            TisCloudSolrClient.SimpleQueryResult<Shop> result = this.client.query(COLLECTION_SHOP, "0", /* 因为现在shop数量比较小直接設置一個默認值就行了 */
            query, Shop.class);
            System.out.println("hits:" + result.getResponse().getResults().getNumFound());
            for (Shop s : result.getResult()) {
                System.out.println(s.getEntityId() + "," + s.getDist());
            }
        } catch (SolrServerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
