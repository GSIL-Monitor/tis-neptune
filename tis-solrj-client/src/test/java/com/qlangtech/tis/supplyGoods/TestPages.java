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

import org.apache.solr.client.solrj.SolrQuery;
import com.qlangtech.tis.solrj.extend.BasicTestCase;
import com.qlangtech.tis.solrj.extend.AbstractTisCloudSolrClient.SimpleQueryResult;

/* *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class TestPages extends BasicTestCase {

    public void testPages() throws Exception {
        // String collection, SolrQuery query, Class<T> clazz
        int pageSize = 11;
        SolrQuery query = new SolrQuery();
        query.setQuery("*:*");
        query.setRows(pageSize);
        query.setStart(0);
        SimpleQueryResult<SupplyGoods> response = this.client.mergeQuery("search4supplyGoods", query, SupplyGoods.class);
        final Long allrow = response.getNumberFound();
        query.setRows(pageSize);
        int page = 0;
        int start = 0;
        while (true) {
            start = (page++) * pageSize;
            if (start > (allrow - 1)) {
                break;
            }
            System.out.println("page:" + page + "======================");
            query.setStart(start);
            response = this.client.mergeQuery("search4supplyGoods", query, SupplyGoods.class);
            for (SupplyGoods goods : response.getResult()) {
                System.out.println("id:" + goods.getId());
            }
        }
    }
}
