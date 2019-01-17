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

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import com.qlangtech.tis.solrj.extend.AbstractTisCloudSolrClient.SimpleQueryResult;
import com.qlangtech.tis.solrj.extend.BasicTestCase;

/* *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class TestShopSearch extends BasicTestCase {

    private static final String FIELD_COORDINATE = "coordinate";

    private static final String COORDINATE_FILTER_QUERY = "{!geofilt sfield=coordinate}";

    private static final String COLLECTION_SHOP = "search4shop";

    @SuppressWarnings("all")
    public void testQuery() throws Exception {
        // System.out.println(
        // "action=add_app_action&projectName=ddd&dptId=356&recept=&event_submit_do_add_app=%E6%8F%90+++++%E4%BA%A4"
        // .length());
        // &d=400
        // &pt=87.74267,38.8956
        // &sfield=coordinate
        // &q={!func}geodist()
        // &fq={!geofilt%20sfield=coordinate}
        // &fl=*,score
        // &sort=score+asc
        // String fromPt = "87.74267,38.8956";
        // String fromPt = "19.224862,10.383899";
        String fromPt = "10.21,120.33";
        // String cursorStart = CursorMarkParams.CURSOR_MARK_START;
        // while (true) {
        SolrQuery query = new SolrQuery();
        // 后面还可以
        // query.setQuery("city_id:20");
        // query.setQuery("city_id:[* TO *] _val_:\"{!func}geodist()\"");
        query.setQuery("_query_:\"{!terms f=entity_id}99001530,99000907,99000938\" _val_:\"{!func}geodist()\"");
        // query.setQuery("{!terms f=entity_id}99001530,99000907,99000938 ");
        query.setFilterQueries(COORDINATE_FILTER_QUERY);
        // query.set(CursorMarkParams.CURSOR_MARK_PARAM, cursorStart);
        query.setRows(10);
        query.setStart(0);
        // 方圆多少公里
        query.set("d", Integer.MAX_VALUE);
        // 设置当前所在地点的位置，format:lat(纬度[-90~90]),lon(经度[-180,180])
        // 按照地理位置由近到远排序
        // query.setSort("geodist()", ORDER.asc);
        query.setSort("score", ORDER.asc);
        // 设置需要返回的字段
        // query.setFields("*", "dist:geodist(" + FIELD_COORDINATE + "," +
        // fromPt + ")");
        query.setFields("*", "dist:geodist()");
        // 存放地理位置坐标的字段
        query.set("pt", fromPt);
        query.set("sfield", FIELD_COORDINATE);
        // query.setSort("dist", ORDER.asc);
        SimpleQueryResult<Shop> result = this.client.query(COLLECTION_SHOP, "0", /* 因为现在shop数量比较小直接設置一個默認值就行了 */
        query, Shop.class);
        System.out.println("hits:" + result.getResponse().getResults().getNumFound());
        result.getResponse().getResponse().get("change_num_statis_sum");
        for (Shop shop : result.getResult()) {
            System.out.println(shop.getName());
            // 离开当前坐标的距离,單位公里
            System.out.println("dist:" + shop.getDist() * 1000);
            System.out.println("=================================");
        }
        Thread.sleep(3000);
    // }
    // List<SimpleOrderedMap<String>> result =
    // (List<SimpleOrderedMap<String>>) response.getResponse()
    // .get("commonGroupby");
    // String[] GROUP_FIELD_KEY_ARRAY =
    // StringUtils.split(GROUP_FIELD_KEY_REPLIC, ",");
    // String[] SUM_FIELDS_ARRAY = StringUtils.split(SUM_FIELDS, ",");
    // String[] groupShowKeysArray = StringUtils.split(groupShowKeys, ",");
    // for (String f : GROUP_FIELD_KEY_ARRAY) {
    // System.out.print(String.format("%1$40s", f));
    // }
    // 
    // for (String f : groupShowKeysArray) {
    // System.out.print(String.format("%1$40s", f));
    // }
    // 
    // // for (String f : GROUP_FIELD_KEY_SHOW) {
    // // System.out.print(String.format("%1$20s", f));
    // // }
    // 
    // for (String f : SUM_FIELDS_ARRAY) {
    // System.out.print(String.format("%1$20s", f));
    // }
    // System.out.println();
    // for (SimpleOrderedMap<String> row : result) {
    // 
    // for (String f : GROUP_FIELD_KEY_ARRAY) {
    // System.out.print(String.format("%1$40s", row.get(f)));
    // }
    // 
    // for (String f : groupShowKeysArray) {
    // System.out.print(String.format("%1$40s", row.get(f)));
    // }
    // 
    // // for (String f : GROUP_FIELD_KEY_SHOW) {
    // // System.out.print(String.format("%1$20s", row.get(f)));
    // // }
    // 
    // for (String f : SUM_FIELDS_ARRAY) {
    // System.out.print(String.format("%1$20s", row.get(f)));
    // }
    // System.out.println();
    // }
    // 商圈查詢排序定制需求
    // http://k.2dfire.net/pages/viewpage.action?pageId=398098944
    }
}
