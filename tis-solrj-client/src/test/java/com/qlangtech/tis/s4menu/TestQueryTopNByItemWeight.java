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

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.Group;
import org.apache.solr.client.solrj.response.GroupCommand;
import org.apache.solr.client.solrj.response.GroupResponse;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import com.qlangtech.tis.solrj.extend.BasicTestCase;

/* *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class TestQueryTopNByItemWeight extends BasicTestCase {

    @SuppressWarnings("all")
    public void testRecommend1() throws Exception {
        SolrQuery query = new SolrQuery();
        query.setQuery("{!terms f=entity_id}99928622,99001331");
        query.setFields("menu_id", "name", "item_weight");
        query.set("group", true);
        query.set("group.field", "entity_id");
        query.set("group.limit", 5);
        query.set("group.sort", "item_weight desc");
        QueryResponse result = this.client.query("search4menu_new", "0", query);
        GroupResponse gresult = result.getGroupResponse();
        for (GroupCommand g : gresult.getValues()) {
            for (Group gval : g.getValues()) {
                System.out.println("entity_id:" + gval.getGroupValue());
                for (SolrDocument doc : gval.getResult()) {
                    System.out.println("menu_id" + doc.getFirstValue("menu_id") + ",name:" + doc.getFirstValue("name") + ",item_weight:" + doc.getFirstValue("item_weight"));
                }
                System.out.println("================================================");
            }
        }
    }
}
