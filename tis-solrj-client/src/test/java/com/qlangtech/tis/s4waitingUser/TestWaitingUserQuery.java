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
package com.qlangtech.tis.s4waitingUser;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.common.params.CursorMarkParams;
import com.qlangtech.tis.solrj.extend.BasicTestCase;
import com.qlangtech.tis.solrj.extend.AbstractTisCloudSolrClient.SimpleQueryResult;

/* *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class TestWaitingUserQuery extends BasicTestCase {

    public void testQuery() throws Exception {
        String cursorMark = CursorMarkParams.CURSOR_MARK_START;
        while (true) {
            String entityId = "00180079";
            SolrQuery query = new SolrQuery();
            // 
            // query.setQuery("{!distinctOrder}order_id:[* TO *] AND entity_id:"
            // + entityId);
            query.setQuery("{!distinctOrder}order_id:[* TO *] AND customer_ids:c69a5f96088648a9bedf1042619044b3 AND {!terms f=entity_id}99925788,99001331");
            query.addSort("create_time", ORDER.desc);
            query.addSort("waitingorder_id", ORDER.desc);
            query.setRows(20);
            query.setFields("order_id", "waitingorder_id", "create_time");
            query.set(CursorMarkParams.CURSOR_MARK_PARAM, cursorMark);
            // SimpleQueryResult<WaitingUser> result =
            // client.query("search4waitingUser", entityId, query,
            // WaitingUser.class);
            SimpleQueryResult<WaitingUser> result = client.mergeQuery("search4waitingUser", query, WaitingUser.class);
            for (WaitingUser doc : result.getResult()) {
                System.out.println(doc.getWaitingorderId() + ",order_id:" + doc.getOrderId() + ",create:" + doc.getCreateTime());
            }
            if (cursorMark.equals(result.getResponse().getNextCursorMark())) {
                // 已经翻到了最后一页
                break;
            } else {
                cursorMark = result.getResponse().getNextCursorMark();
            }
        }
    }
}
