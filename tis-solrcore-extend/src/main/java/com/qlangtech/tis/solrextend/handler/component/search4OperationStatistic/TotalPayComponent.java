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
package com.qlangtech.tis.solrextend.handler.component.search4OperationStatistic;

import org.apache.solr.handler.component.ResponseBuilder;
import org.apache.solr.handler.component.SearchComponent;
import org.apache.solr.schema.IndexSchema;
import org.apache.solr.search.DocListAndSet;
import java.io.IOException;

/*
 * Created by lingxiao on 2016/7/21.
 *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class TotalPayComponent extends SearchComponent {

    public static final String COMPONENT_NAME = "total_report_component";

    @Override
    public void prepare(ResponseBuilder responseBuilder) throws IOException {
        if (responseBuilder.req.getParams().getBool(COMPONENT_NAME, false)) {
            responseBuilder.setNeedDocSet(true);
        }
    }

    @Override
    public void process(ResponseBuilder responseBuilder) throws IOException {
        if (!responseBuilder.req.getParams().getBool(COMPONENT_NAME, false)) {
            return;
        }
        IndexSchema schema = responseBuilder.req.getSchema();
        ToltalPayDocValues tpd = new ToltalPayDocValues();
        tpd.source_amount = new CommonFieldColumn(schema.getField("total_source_amount"));
        tpd.discount_fee = new CommonFieldColumn(schema.getField("total_discount_fee"));
        tpd.profit = new CommonFieldColumn(schema.getField("total_profit"));
        tpd.not_include = new CommonFieldColumn(schema.getField("total_not_include_amount"));
        tpd.coupon_discount = new CommonFieldColumn(schema.getField("total_coupon_discount"));
        tpd.receive_amount_real = new CommonFieldColumn(schema.getField("total_receive_amount_real"));
        tpd.seat_order_amount = new CommonFieldColumn(schema.getField("total_seat_order_amount"));
        tpd.order_num = new CommonFieldColumn(schema.getField("total_order_num"));
        tpd.seat_order_num = new CommonFieldColumn(schema.getField("total_seat_order_num"));
        tpd.people_num = new CommonFieldColumn(schema.getField("total_people_num"));
        tpd.seat_num = new CommonFieldColumn(schema.getField("total_seat_num"));
        tpd.discount_amount = new CommonFieldColumn(schema.getField("total_discount_amount"));
        tpd.receive_amount = new CommonFieldColumn(schema.getField("total_receive_amount"));
        DocListAndSet results = responseBuilder.getResults();
        /**
         * //获取命中的结果的条目数，用来求平均桌位数，计算翻桌率使用
         */
        int num = results.docSet.size();
        TotalPayCollector collector = new TotalPayCollector(tpd);
        responseBuilder.req.getSearcher().search(results.docSet.getTopFilter(), collector);
        responseBuilder.rsp.add("total_report_component", collector.write(collector.totalPayStatis, num));
    }

    @Override
    public String getDescription() {
        return COMPONENT_NAME;
    }
}
