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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.FieldStatsInfo;
import org.apache.solr.client.solrj.response.PivotField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.params.FacetParams;
import org.apache.solr.common.params.StatsParams;
import org.apache.solr.common.util.NamedList;
import com.qlangtech.tis.solrj.extend.BasicTestCase;

/*
 * 使用facet实现groupby功能
 *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class TestSupplyQuery extends BasicTestCase {

    private static final String FILED_KEY_OLD_GOODS_NUM = "old_goods_num";

    private static final String FILED_KEY_NEW_GOODS_NUM = "new_goods_num";

    private static final String FILED_KEY_GOODS_WEIGHT = "goods_weight";

    private static final String FILED_KEY_GOODS_AMOUNT = "goods_amount";

    // 合计数据集合
    private static final String[] SUM_FIELDS = new String[] { FILED_KEY_OLD_GOODS_NUM, FILED_KEY_NEW_GOODS_NUM, FILED_KEY_GOODS_WEIGHT, FILED_KEY_GOODS_AMOUNT };

    private static final Set<String> AGG_FIELDS;

    static {
        // 设置排序
        AGG_FIELDS = new HashSet<>();
        // 供应商编号
        AGG_FIELDS.add("supply_code");
        // 门店拼写
        AGG_FIELDS.add("shop_spell");
        // 门店ID
        AGG_FIELDS.add("self_entity_id");
        // 原料拼写
        AGG_FIELDS.add("goods_spell");
        // 原料ID
        AGG_FIELDS.add("goods_id");
    }

    public void testQuery() throws Exception {
        SolrQuery query = new SolrQuery();
        query.setQuery("self_entity_id:99061304 AND operate_time:[20150109142233 TO 20151109142233]");
        query.setRows(0);
        query.set(StatsParams.STATS, true);
        for (String f : SUM_FIELDS) {
            query.add(StatsParams.STATS_FIELD, "{!tag=piv1 sum=true}" + f);
        }
        query.set(FacetParams.FACET, true);
        // query.set(FacetParams.FACET_PIVOT,
        // "{!stats=piv1}supply_code,self_entity_id,category_id,goods_id,operate_kind");
        query.set(FacetParams.FACET_PIVOT, "{!stats=piv1}supply_code,shop_spell,self_entity_id,goods_spell,goods_id,goods_name,category_id,category_name,operate_kind,supply_name,shop_name,new_price_unit_name,weight_unit_name");
        System.out.println(query.toString());
        QueryResponse response = this.client.query("search4Supplier", "10086", query);
        NamedList<List<PivotField>> pfield = response.getFacetPivot();
        // System.out.println(pfield);
        List<PivotField> fields = null;
        List<List<Pair>> groupBy = new ArrayList<>();
        for (int i = 0; i < pfield.size(); i++) {
            System.out.println("pfield:" + pfield.getName(i));
            fields = pfield.getVal(i);
            System.out.println("List<PivotField> size:" + fields.size());
            for (PivotField f : fields) {
                System.out.println("name:" + f.getField());
                System.out.println("value:" + f.getValue());
                addTreeNode(groupBy, new ArrayList<Pair>(), f);
            // System.out.println(f);
            }
        }
        Collections.sort(groupBy, new Comparator<List<Pair>>() {

            @Override
            public int compare(List<Pair> o1, List<Pair> o2) {
                StringBuffer o1buf = new StringBuffer();
                for (Pair p : o1) {
                    if (AGG_FIELDS.contains(p.key)) {
                        o1buf.append(p.key);
                    }
                }
                StringBuffer o2buf = new StringBuffer();
                for (Pair p : o2) {
                    if (AGG_FIELDS.contains(p.key)) {
                        o2buf.append(p.key);
                    }
                }
                return o1buf.toString().compareTo(o2buf.toString());
            }
        });
        for (List<Pair> group : groupBy) {
            for (Pair node : group) {
                if (node.value == null) {
                    System.out.print("(sum ");
                    for (Pair sum : node.sums) {
                        System.out.print(sum.key + ":" + sum.value + ",");
                    }
                    System.out.print(") ");
                } else {
                    System.out.print(node.key + "(" + node.value + ")-");
                }
            }
            System.out.println();
        }
    }

    private void addTreeNode(List<List<Pair>> groupBy, List<Pair> pairs, PivotField f) {
        pairs.add(new Pair(f.getField(), String.valueOf(f.getValue())));
        List<PivotField> childs = f.getPivot();
        if (childs == null || childs.size() < 1) {
            Pair sum = new Pair(null, null);
            sum.sums = new ArrayList<>();
            for (String field : SUM_FIELDS) {
                FieldStatsInfo stats = f.getFieldStatsInfo().get(field);
                sum.sums.add(new Pair(field, String.valueOf(stats.getSum())));
            }
            pairs.add(sum);
            groupBy.add(pairs);
            return;
        } else {
            for (PivotField ff : childs) {
                addTreeNode(groupBy, new ArrayList<Pair>(pairs), ff);
            }
        }
    }

    private static class Pair {

        private final String key;

        private final String value;

        private List<Pair> sums;

        /**
         * @param key
         * @param value
         */
        public Pair(String key, String value) {
            super();
            this.key = key;
            this.value = value;
        }
    }
}
