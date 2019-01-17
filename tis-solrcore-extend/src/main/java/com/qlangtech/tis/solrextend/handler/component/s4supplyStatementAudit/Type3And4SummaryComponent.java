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
package com.qlangtech.tis.solrextend.handler.component.s4supplyStatementAudit;

import java.io.IOException;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.handler.component.ResponseBuilder;
import org.apache.solr.handler.component.SearchComponent;
import com.qlangtech.tis.solrextend.queryparse.s4supplyStatementAudit.ISupplyStatementAuditConstant;
import com.qlangtech.tis.solrextend.queryparse.s4supplyStatementAudit.SupplyStatementAuditGroupByTradeIdQPlugin;
import com.qlangtech.tis.solrextend.queryparse.s4supplyStatementAudit.SupplyStatementAuditQuery.Accumulator;
import com.google.common.collect.Lists;

/*
 * Case2,Case3 结果显示
 *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class Type3And4SummaryComponent extends SearchComponent implements ISupplyStatementAuditConstant {

    private static final String NAME = "type3And4Summary";

    @Override
    public void prepare(ResponseBuilder rb) throws IOException {
        if (rb.req.getParams().getBool(NAME, false)) {
            rb.setNeedDocSet(true);
        }
    }

    @Override
    @SuppressWarnings("all")
    public void process(ResponseBuilder rb) throws IOException {
        if (!rb.req.getParams().getBool(NAME, false)) {
            return;
        }
        Accumulator allsummary = SupplyStatementAuditGroupByTradeIdQPlugin.getAllSummaryAmountAccumulator(rb.req);
        Accumulator type3ANDNot3CountSummary = SupplyStatementAuditGroupByTradeIdQPlugin.getAllSummaryCountStatus3ANDNot3Accumulator(rb.req);
        rb.rsp.add(NAME, new NamedList<String>(// 
        Lists.newArrayList(// 
        RETURN_FIELD_type3SumAmount, // 
        allsummary.getType3SumAmount(), // 
        RETURN_FIELD_type2SumAmount, // 
        allsummary.getType2SumAmount(), // 
        RETURN_FIELD_type4SumAmount, // 
        allsummary.getType4SumAmount(), // 
        RETURN_FIELD_difference, // 
        allsummary.getDifference(), // 
        RETURN_FIELD_Status3Count, // 
        type3ANDNot3CountSummary.getStatus3Count(), // 
        RETURN_FIELD_StatusNot3Count, // 
        type3ANDNot3CountSummary.getStatusNot3Count())));
    }

    @Override
    public String getDescription() {
        return NAME;
    }
}
