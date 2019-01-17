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
package com.qlangtech.tis.s4supplier;

import java.util.List;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.util.SimpleOrderedMap;
import com.qlangtech.tis.solrj.extend.BasicTestCase;

/*
 * 使用facet实现groupby功能
 *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class TestSupplyQuery2 extends BasicTestCase {

    private static final String[] GROUP_FIELD_KEY = new String[] { "supply_code", "self_entity_id", "category_id", "goods_id", "op_kind" };

    // 最终只显示值的Field ，這個應該由聚合的最後一行的行顯示
    private static final String[] GROUP_FIELD_KEY_SHOW = new String[] { "shop_spell", "goods_spell", "goods_name", "category_name", "supply_name", "shop_name", "new_price_unit_name", "weight_unit_name" };

    private static final String FILED_KEY_OLD_GOODS_NUM = "old_goods_num";

    private static final String FILED_KEY_NEW_GOODS_NUM = "new_goods_num";

    private static final String FILED_KEY_GOODS_WEIGHT = "goods_weight";

    private static final String FILED_KEY_GOODS_AMOUNT = "goods_amount";

    // 合计数据集合
    private static final String[] SUM_FIELDS = new String[] { FILED_KEY_OLD_GOODS_NUM, FILED_KEY_NEW_GOODS_NUM, FILED_KEY_GOODS_WEIGHT, FILED_KEY_GOODS_AMOUNT };

    @SuppressWarnings("all")
    public void testQuery() throws Exception {
        SolrQuery query = new SolrQuery();
        query.setQuery("self_entity_id:99061304 AND operate_time:[20150109142233 TO 20151109142233]");
        query.setRows(0);
        query.set("groupby", true);
        System.out.println(query.toString());
        QueryResponse response = this.client.query("search4Supplier", "10086", query);
        // SimpleOrderedMap<String> result =
        // (SimpleOrderedMap<String>)response.getResponse().get("groupby");
        // System.out.println(.getClass());
        // System.out.println(pfield);
        List<SimpleOrderedMap<String>> result = (List<SimpleOrderedMap<String>>) response.getResponse().get("groupby");
        for (String f : GROUP_FIELD_KEY) {
            System.out.print(String.format("%1$20s", f));
        }
        for (String f : GROUP_FIELD_KEY_SHOW) {
            System.out.print(String.format("%1$20s", f));
        }
        for (String f : SUM_FIELDS) {
            System.out.print(String.format("%1$20s", f));
        }
        System.out.println();
        for (SimpleOrderedMap<String> row : result) {
            for (String f : GROUP_FIELD_KEY) {
                System.out.print(String.format("%1$20s", row.get(f)));
            }
            for (String f : GROUP_FIELD_KEY_SHOW) {
                System.out.print(String.format("%1$20s", row.get(f)));
            }
            for (String f : SUM_FIELDS) {
                System.out.print(String.format("%1$20s", row.get(f)));
            }
            System.out.println();
        }
    }
}
