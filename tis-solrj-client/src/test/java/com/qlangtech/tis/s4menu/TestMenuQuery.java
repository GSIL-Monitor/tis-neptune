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
package com.qlangtech.tis.s4menu;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.beans.Field;
import org.apache.solr.common.params.CommonParams;
import org.apache.solr.common.util.SimpleOrderedMap;
import org.json.JSONArray;
import org.json.JSONObject;
import com.qlangtech.tis.solrj.extend.BasicTestCase;
import com.qlangtech.tis.solrj.extend.AbstractTisCloudSolrClient.SimpleQueryResult;

/*
 *         RecommendMenuComponent <br>
 *         MenuRecommendQparserPlugin <br>
 *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class TestMenuQuery extends BasicTestCase {

    @SuppressWarnings("all")
    public void testRecommend1() throws Exception {
        JSONArray rules = new JSONArray();
        JSONObject j = new JSONObject();
        j.put("labelId", 2);
        j.put("value", "2,1,3,0");
        j.put("type", "0");
        rules.put(j);
        j = new JSONObject();
        j.put("labelId", 9);
        j.put("value", "1,1,2,0");
        j.put("type", "1");
        rules.put(j);
        // 规则设置 end
        SolrQuery query = new SolrQuery();
        // String orderMenu =
        // "000013314c7d25ca014c91cd87290082:1,000013314c7d25ca014c91cd86680055:1,000013314c7d25ca014c91cd8681005a:1";
        // String orderMenu = "00000000000000000000000000000000:1";
        System.out.println(URLDecoder.decode("2%2C1%2C3%2C1_0_1%3B1%2C1%2C3%2C1_0_2%3B1%2C1%2C2%2C1_0_3%3B1%2C1%2C2%2C1_0_4%3B1%2C1%2C2%2C1_0_5%3B1%2C1%2C3%2C1_0_6%3B1%2C1%2C3%2C1_0_7%3B1%2C1%2C1%2C1_2_12%3B1%2C1%2C1%2C1_2_13%3B1%2C1%2C1%2C1_2_14%3B1%2C1%2C1%2C1_2_15%3B1%2C1%2C1%2C1_2_16%3B1%2C1%2C1%2C1_2_17%3B1%2C1%2C1%2C1_2_19%3B1%2C1%2C1%2C1_2_20%3B1%2C1%2C1%2C1_2_21%3B1%2C1%2C1%2C1_2_23%3B1%2C1%2C1%2C1_2_24%3B1%2C1%2C1%2C1_2_25%3B1%2C1%2C1%2C1_2_26%3B1%2C1%2C1%2C1_2_27d"));
        query.setQuery("{!mrecommend entity_id=00028788 rules=2%2C1%2C3%2C1_0_1%3B1%2C1%2C3%2C1_0_2%3B1%2C1%2C2%2C1_0_3%3B1%2C1%2C2%2C1_0_4%3B1%2C1%2C2%2C1_0_5%3B1%2C1%2C3%2C1_0_6%3B1%2C1%2C3%2C1_0_7%3B1%2C1%2C1%2C1_2_12%3B1%2C1%2C1%2C1_2_13%3B1%2C1%2C1%2C1_2_14%3B1%2C1%2C1%2C1_2_15%3B1%2C1%2C1%2C1_2_16%3B1%2C1%2C1%2C1_2_17%3B1%2C1%2C1%2C1_2_19%3B1%2C1%2C1%2C1_2_20%3B1%2C1%2C1%2C1_2_21%3B1%2C1%2C1%2C1_2_23%3B1%2C1%2C1%2C1_2_24%3B1%2C1%2C1%2C1_2_25%3B1%2C1%2C1%2C1_2_26%3B1%2C1%2C1%2C1_2_27}0002878853795a910153795f518b0003:3 AND menu_spec:[* TO *]");
        query.setStart(0);
        query.setRows(0);
        query.set("menus-r", true);
        query.set(CommonParams.DISTRIB, false);
        query.addFilterQuery("entity_id:99180079 AND -is_self:0");
        query.setFields("name", "menu_id", "server", "path", "price");
        System.out.println(query.toString());
        SimpleQueryResult<Object> result = client.query("search4menu", "99180079", query, Object.class);
        List<SimpleOrderedMap> recommend = (List<SimpleOrderedMap>) result.getResponse().getResponse().get("recommend");
        List<SimpleOrderedMap> menus = null;
        for (SimpleOrderedMap o : recommend) {
            System.out.println("label:" + o.get("label"));
            System.out.println("proposal:" + o.get("proposal"));
            System.out.println("allfound:" + o.get("allfound"));
            menus = (List<SimpleOrderedMap>) o.get("menus");
            if (menus == null) {
                System.out.println("===============================");
                continue;
            }
            System.out.println("menus:{");
            for (SimpleOrderedMap m : menus) {
                System.out.print("\t");
                System.out.println(m.get("name"));
                System.out.print("\t");
                System.out.println(m.get("menu_id"));
                System.out.print("\t");
                System.out.println(m.get("server"));
                System.out.print("\t");
                System.out.println("path:" + m.get("path"));
                System.out.print("\t");
                System.out.println("price:" + m.get("price"));
                System.out.print("\t");
                System.out.println("recommend_level:" + m.get("recommend_level"));
                System.out.println("===============================");
            }
            System.out.println("}");
        }
    }

    public String createRuleSample() throws Exception {
        // "value+'_'+type+'_'+labelId[;value+'_'+type+'_'+labelId]"
        return URLEncoder.encode("8,1,10,0_0_1;14,1,15,0_0_2;1,1,5,1,1,5,0_0_3", "utf8");
    }

    public static class MenuPojo {

        @Field("name")
        private String name;

        @Field("menu_id")
        private String menuId;

        @Field("server")
        private String server;

        @Field("path")
        private String path;

        @Field("price")
        private double price;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getMenuId() {
            return menuId;
        }

        public void setMenuId(String menuId) {
            this.menuId = menuId;
        }

        public String getServer() {
            return server;
        }

        public void setServer(String server) {
            this.server = server;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public double getPrice() {
            return price;
        }

        public void setPrice(double price) {
            this.price = price;
        }
    }
}
