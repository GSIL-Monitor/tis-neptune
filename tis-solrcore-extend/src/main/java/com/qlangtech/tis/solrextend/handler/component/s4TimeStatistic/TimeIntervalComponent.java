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
package com.qlangtech.tis.solrextend.handler.component.s4TimeStatistic;

import org.apache.solr.handler.component.ResponseBuilder;
import org.apache.solr.handler.component.SearchComponent;
import org.apache.solr.schema.IndexSchema;
import org.apache.solr.search.DocListAndSet;
import java.io.IOException;

/*
 * Created by lingxiao on 2016/7/19.
 *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class TimeIntervalComponent extends SearchComponent {

    public static final String COMPONENT_NAME = "time_interval_statis_component";

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
        TimeIntervalStatisDocValues tsDocvalues = new TimeIntervalStatisDocValues();
        tsDocvalues.hour = new TimeStatisFieldColumn(schema.getField("hour"));
        tsDocvalues.openOrderCount = new TimeStatisFieldColumn(schema.getField("open_order_num"));
        tsDocvalues.endOrderCount = new TimeStatisFieldColumn(schema.getField("end_order_num"));
        tsDocvalues.openOrderPeople = new TimeStatisFieldColumn(schema.getField("open_order_people"));
        tsDocvalues.endOrderPeople = new TimeStatisFieldColumn(schema.getField("end_order_people"));
        tsDocvalues.recieveAmount = new TimeStatisFieldColumn(schema.getField("receive_amount"));
        tsDocvalues.discountAmount = new TimeStatisFieldColumn(schema.getField("discount_fee"));
        tsDocvalues.profit = new TimeStatisFieldColumn(schema.getField("profit"));
        tsDocvalues.invoice = new TimeStatisFieldColumn(schema.getField("invoice"));
        DocListAndSet results = responseBuilder.getResults();
        TimeIntervalStatisCollector collector = new TimeIntervalStatisCollector(tsDocvalues);
        responseBuilder.req.getSearcher().search(results.docSet.getTopFilter(), collector);
        responseBuilder.rsp.add("time_interval_statis_component", collector.write(collector.timeIntervalStatisMap));
    }

    @Override
    public String getDescription() {
        return COMPONENT_NAME;
    }
}
