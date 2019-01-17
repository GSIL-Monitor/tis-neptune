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
package com.qlangtech.tis.s4supplyAudit;

import org.apache.commons.lang.StringUtils;
import org.apache.solr.common.SolrInputDocument;
import com.qlangtech.tis.solrj.extend.BasicTestCase;

/* *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class TestSupplyAuditInsert extends BasicTestCase {

    public void testInsert() throws Exception {
        // id:000406955ba6084a015ba88a106906a1
        // String pairs = "date=20170427, no=CG20170426000075, op_user_id=00040695544c4286015450e8bf822176, create_time=20170426122618441, self_entity_id=00040695, origin=1, op_time=20170426122618441, entity_id=00037477, type=1, paper_id=000406955ba607a5015ba88287c975eb, op_user_name=仓管, last_ver=0, split=-1, is_valid=1, warehouse_is_supplied=1, id=000406955ba6084a015ba88a106906a1, supplier_id=0, status=2, warehouse_id=000374775621f4a601562ba8bcbe7b39, _version_=0";
        String pairs = "date=20170427, no=CG20170426000075, op_user_id=000374775a637bf4015a649b6a83175c, create_time=20170426122618441, self_entity_id=00040695, origin=1, op_time=20170426133816497, entity_id=00037477, type=1, paper_id=000406955ba607a5015ba88287c975eb, op_user_name=陈伟, last_ver=1, split=-1, is_valid=1, warehouse_is_supplied=1, id=000406955ba6084a015ba88a106906a1, supplier_id=0, status=3, warehouse_id=000374775621f4a601562ba8bcbe7b39, _version_=1";
        SolrInputDocument doc = new SolrInputDocument();
        String[] p = pairs.split(",");
        String[] kvAry = null;
        for (String kv : p) {
            kvAry = StringUtils.trim(kv).split("=");
            doc.addField(kvAry[0], kvAry[1]);
        }
        this.client.add("search4supplyAudit", doc, 1);
    }
}
