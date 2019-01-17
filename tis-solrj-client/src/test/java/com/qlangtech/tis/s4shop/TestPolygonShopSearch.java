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

import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import com.qlangtech.tis.solrj.extend.BasicTestCase;

/* *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class TestPolygonShopSearch extends BasicTestCase {

    private static final String FIELD_COORDINATE = "coordinate";

    public void testQuery() throws Exception {
        // String fromPt = "30.2547,120.1596";
        // String fromPtPos = "30.2547_120.1596";
        // final String pointContain = "Contains(POINT(120.1596 30.2547))";
        String fromPtPos = "40.596679_79.83271";
        queryServiceRange(fromPtPos);
        System.out.println("==========================================");
        queryServiceRange("30.295627_120.134274");
    }

    private void queryServiceRange(String fromPtPos) throws Exception {
        // String fromPt = "40.596679,79.83271";
        // String fromPt = "30.2547,120.1596";
        // String fromPtPos = "40.596679_79.83271";
        SolrQuery query = new SolrQuery();
        // query.setQuery(
        // "{!inservice curr_pos=30.2547_120.1596}{!terms f=entity_id}99927139,99001347,99934038,99933872,99936012,99226462,99934020");
        // query.setQuery(
        // "{!inservice curr_pos=40.596679_79.83271}{!terms f=entity_id}99933871,99933872,99933874");
        query.setQuery("{!inservice curr_pos=" + fromPtPos + "}{!terms f=entity_id}99933871,99933872,99933874");
        query.addFilterQuery("service_flag:[* TO *]");
        // SolrQuery query = new SolrQuery();
        // StringBuffer q = new StringBuffer();
        // q.append("service_flag:1 OR");
        // q.append(" ( service_flag:8 AND
        // pickup_range:\"").append(pointContain).append("\")");
        // q.append(" OR ( service_flag:4 AND
        // delivery_range:\"").append(pointContain).append("\")");
        // q.append(" OR ( service_flag:2 AND
        // gps_data:\"").append(pointContain).append("\")");
        // query.setQuery("service_flag:[* TO *] AND {!inservice curr_pos=" + fromPtPos
        // + "}{!terms f=entity_id}99927139,99001347,99934038,99933872,99936012,99226462,99934020");
        // query.setQuery("service_flag:[* TO *] AND {!inservice curr_pos=31.232781_121.475137}{!terms f=entity_id}99933871");
        query.set("sendfrombaisui", "1");
        query.set("pt", StringUtils.replace(fromPtPos, "_", ","));
        query.set("sfield", FIELD_COORDINATE);
        query.setFields("entity_id", "service_flag", "dist:geodist()", "score");
        // query.setQuery(q.toString());
        // SolrQuery query = new SolrQuery();
        // query.setQuery("svc_area:\"Contains(POINT( 120.1596 30.2547))\"");
        // query.setQuery("delivery_region:\"Contains(POINT( 120.2699 30.2547))\"");
        QueryResponse result = this.client.query("search4shop", "0", query);
        System.out.println("getNumFound:" + result.getResults().getNumFound());
        for (SolrDocument doc : result.getResults()) {
            System.out.println("entity_id" + doc.getFieldValue("entity_id") + ",service_flag:" + doc.getFieldValue("service_flag") + ",dist:" + doc.getFieldValue("dist") + ",score:" + doc.getFieldValue("score"));
        }
    }
}
