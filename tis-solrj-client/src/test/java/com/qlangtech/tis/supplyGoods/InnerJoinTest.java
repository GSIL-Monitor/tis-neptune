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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.solr.client.solrj.io.Tuple;
import com.qlangtech.tis.solrj.extend.BasicTestCase;
import com.qlangtech.tis.solrj.extend.TisCloudSolrClient;

/* *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class InnerJoinTest extends BasicTestCase {

    public void test1() throws Exception {
        String query_more = "innerJoin(" + "    search(search4supplyGoods, qt=/select, _route_=99928370, q=_query_:\"entity_id:99928370\", fl=\"id, category_id\" , sort=\"id asc\")," + "    unique(search(search4supplyUnionTabs, qt=/export, _route_=99928370, q=\"entity_id:99928370 AND table_name:supplier_goods\", fl=\"goods_id\" , sort=\"goods_id asc\") , over=goods_id)," + " on=\"id=goods_id\" )";
        String query_search4supplyUnionTabs = "search(search4supplyGoods, qt=/select, _route_=99928370, q=_query_:\"entity_id:99928370\", fl=\"id\" , sort=\"id asc\")";
        String query_search4supplyGoods = "    unique(search(search4supplyUnionTabs, qt=/export, _route_=99928370, q=\"entity_id:99928370 AND table_name:supplier_goods\", fl=\"goods_id\" , sort=\"goods_id asc\") , over=goods_id)";
        Set<String> categoryIdSet = new HashSet<>();
        AtomicInteger count = new AtomicInteger();
        try {
            client.queryAndStreamResponse(query_search4supplyUnionTabs, Tuple.class, new TisCloudSolrClient.ResponseCallback<Tuple>() {

                @Override
                public void process(Tuple pojo) {
                    count.incrementAndGet();
                    String categoryId = pojo.getString("id");
                    System.out.println("categoryId:" + categoryId);
                // if (!StringUtils.equals("null", categoryId)) {
                // categoryIdSet.add(categoryId);
                // }
                }

                @Override
                public void lististInfo(long numFound, long start) {
                    System.out.println("numFound:" + numFound);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error");
        }
        List<String> categoryIdList = new ArrayList<>(categoryIdSet);
        System.out.println("finish,count:" + count);
        Thread.sleep(4000);
    }
}
