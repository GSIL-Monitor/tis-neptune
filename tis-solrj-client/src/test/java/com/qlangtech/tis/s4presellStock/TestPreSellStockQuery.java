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
package com.qlangtech.tis.s4presellStock;

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
public class TestPreSellStockQuery extends BasicTestCase {

    // 合计数据集合
    private static final String[] SUM_FIELDS = new String[] { "remain_volume" };

    public void testQuery() throws Exception {
        SolrQuery query = new SolrQuery();
        query.setQuery("{!shop_mindiscount}entity_id:99001331");
        query.setRows(0);
        query.set(StatsParams.STATS, true);
        query.add(StatsParams.STATS_FIELD, "{!tag=piv1 sum=true}remain_volume");
        query.set(FacetParams.FACET, true);
        query.set(FacetParams.FACET_PIVOT, "{!stats=piv1}entity_id,discount");
        System.out.println(query.toString());
        QueryResponse response = this.client.query("search4presellStock", "99001331", query);
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
