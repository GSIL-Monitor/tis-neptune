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

import com.qlangtech.tis.solrj.extend.BasicTestCase;
import org.apache.solr.common.SolrInputDocument;

/*
 * Created by Qinjiu(Qinjiu@2dfire.com) on 3/2/2017.
 *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class TestSolrUpdate extends BasicTestCase {

    public void testUpdateCommodity() throws Exception {
        long version = 20170302001080L;
        int i = 0;
        for (; i < 9; i++) {
            SolrInputDocument parent = new SolrInputDocument();
            parent.addField("store_code", "00001");
            parent.addField("store_id", "576843c5b8959c245870cd28e473a9d7");
            parent.addField("is_sale", 1);
            parent.addField("spell", "CSSPU4");
            parent.addField("op_user_id", "niurou");
            parent.addField("create_time", 20161223143951000L);
            parent.addField("origin", "浙江杭州");
            parent.addField("op_time", 20161223143951100L);
            parent.addField("spec_type", 1);
            parent.addField("entity_id", "99926300");
            parent.addField("priority", 4);
            parent.addField("op_user_name", "牛肉");
            parent.addField("last_ver", 1);
            parent.addField("admin_freeze", 0);
            parent.addField("is_valid", 0);
            parent.addField("name", "测试SPU4");
            parent.addField("sales_num", 400);
            parent.addField("system_priority", 1);
            parent.addField("id", "4038ed4feebdd7e4dbed5d3dc88ea759");
            parent.addField("tag", "无公害");
            parent.addField("brand", "二维火");
            parent.addField("sale_time", version + i);
            parent.addField("status", 1);
            parent.addField("_version_", version);
            parent.addField("_root_", "4038ed4feebdd7e4dbed5d3dc88ea759");
            parent.addField("type", "p");
            SolrInputDocument child1 = new SolrInputDocument();
            child1.addField("id", "b932c4fadfbb44869e907f550719c92a");
            child1.addField("sku_goods_id", "9992654855589238015559099ad802fd");
            child1.addField("sku_standard_goods_id", "standardgoods0000000000000000007");
            child1.addField("sku_standard_category_id", "standardcategory0000000000000007");
            child1.addField("sku_standard_inner_code", "010007");
            child1.addField("sku_sort_code", 0);
            child1.addField("sku_spec_name", "规格7");
            child1.addField("sku_package_unit", "packageunit000000000000000000007");
            child1.addField("sku_package_num", 1.0);
            child1.addField("sku_order_min_num", 1);
            child1.addField("sku_order_max_num", 50);
            child1.addField("sku_status", 1);
            child1.addField("sku_create_time", 20161223144325000L);
            child1.addField("sku_bar_code", "3301000212164");
            child1.addField("sku_is_valid", 0);
            child1.addField("sku_last_ver", 0);
            child1.addField("type", "c");
            child1.addField("_root_", "4038ed4feebdd7e4dbed5d3dc88ea759");
            SolrInputDocument child2 = new SolrInputDocument();
            child2.addField("id", "b932c4fadfbb44869e907f550719c930");
            child2.addField("sku_goods_id", "9992654855589238015559099ad802fd");
            child2.addField("sku_standard_goods_id", "standardgoods0000000000000000008");
            child2.addField("sku_standard_category_id", "standardcategory0000000000000008");
            child2.addField("sku_standard_inner_code", "010008");
            child2.addField("sku_sort_code", 1);
            child2.addField("sku_spec_name", "规格88");
            child2.addField("sku_package_unit", "packageunit000000000000000000008");
            child2.addField("sku_package_num", 5.0);
            child2.addField("sku_order_min_num", 5);
            child2.addField("sku_order_max_num", 15);
            child2.addField("sku_status", 1);
            child2.addField("sku_create_time", 20161223144552001L);
            child2.addField("sku_bar_code", "3301000212164");
            child2.addField("sku_is_valid", 0);
            child2.addField("sku_last_ver", 8);
            child2.addField("type", "c");
            child2.addField("_root_", "4038ed4feebdd7e4dbed5d3dc88ea759");
            parent.addChildDocument(child1);
            parent.addChildDocument(child2);
            client.add("search4supplyCommodity", parent, version + i);
            Thread.sleep(1000);
            SolrInputDocument parent1 = new SolrInputDocument();
            parent1.addField("store_code", "00001");
            parent1.addField("store_id", "576843c5b8959c245870cd28e473a9d7");
            parent1.addField("is_sale", 1);
            parent1.addField("spell", "CSSPU4");
            parent1.addField("op_user_id", "niurou");
            parent1.addField("create_time", 20161223143951000L);
            parent1.addField("origin", "浙江杭州");
            parent1.addField("op_time", 20161223143951100L);
            parent1.addField("spec_type", 1);
            parent1.addField("entity_id", "99926300");
            parent1.addField("priority", 4);
            parent1.addField("op_user_name", "牛肉");
            parent1.addField("last_ver", 1);
            parent1.addField("admin_freeze", 0);
            parent1.addField("is_valid", 0);
            parent1.addField("name", "测试SPU4");
            parent1.addField("sales_num", 400);
            parent1.addField("system_priority", 1);
            parent1.addField("id", "990008985997109301599bc3de71002d");
            parent1.addField("tag", "无公害");
            parent1.addField("brand", "二维火");
            parent1.addField("sale_time", version + i);
            parent1.addField("status", 1);
            parent1.addField("_version_", version);
            parent1.addField("_root_", "990008985997109301599bc3de71002d");
            parent1.addField("type", "p");
            SolrInputDocument child1_1 = new SolrInputDocument();
            child1_1.addField("id", "990008985997109301599bc3de85002e");
            child1_1.addField("sku_goods_id", "9992654855589238015559099ad802fd");
            child1_1.addField("sku_standard_goods_id", "standardgoods0000000000000000007");
            child1_1.addField("sku_standard_category_id", "standardcategory0000000000000007");
            child1_1.addField("sku_standard_inner_code", "010007");
            child1_1.addField("sku_sort_code", 0);
            child1_1.addField("sku_spec_name", "规格7");
            child1_1.addField("sku_package_unit", "packageunit000000000000000000007");
            child1_1.addField("sku_package_num", 1.0);
            child1_1.addField("sku_order_min_num", 1);
            child1_1.addField("sku_order_max_num", 50);
            child1_1.addField("sku_status", 1);
            child1_1.addField("sku_create_time", 20161223144325000L);
            child1_1.addField("sku_bar_code", "3301000212164");
            child1_1.addField("sku_is_valid", 0);
            child1_1.addField("sku_last_ver", 0);
            child1_1.addField("type", "c");
            child1_1.addField("_root_", "990008985997109301599bc3de71002d");
            parent1.addChildDocument(child1_1);
            client.add("search4supplyCommodity", parent1, version + i);
            Thread.sleep(1000);
            System.out.println("time" + i);
        }
    }
}
