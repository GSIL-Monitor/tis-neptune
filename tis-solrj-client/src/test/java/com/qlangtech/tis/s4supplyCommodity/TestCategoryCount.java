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
package com.qlangtech.tis.s4supplyCommodity;

import org.apache.solr.client.solrj.io.Tuple;
import com.qlangtech.tis.solrj.extend.BasicTestCase;
import com.qlangtech.tis.solrj.extend.AbstractTisCloudSolrClient.ResponseCallback;

/* *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class TestCategoryCount extends BasicTestCase {

    public void testCategoryCount() throws Exception {
        String query = "innerJoin(" + "    search(search4supplyGoods, qt=/select, _route_=99928370, q=_query_:\"entity_id:99928370\", fl=\"id, category_id\" , sort=\"id asc\")," + "    unique(search(search4supplyUnionTabs, qt=/export, _route_=99928370, q=\"entity_id:99928370 AND table_name:stock_change_log\", fl=\"goods_id\" , sort=\"goods_id asc\") , over=goods_id)," + " on=\"id=goods_id\" )";
        this.client.queryAndStreamResponse(query, Tuple.class, new ResponseCallback<Tuple>() {

            @Override
            public void process(Tuple pojo) {
                System.out.println(pojo.getString("category_id"));
            }

            @Override
            public void lististInfo(long numFound, long start) {
                System.out.println("numFound:" + numFound);
            }
        });
    }
}
