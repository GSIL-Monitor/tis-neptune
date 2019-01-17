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
package com.qlangtech.tis.solrextend.handler.component.search4OperationStatistic.table;

import com.qlangtech.tis.solrextend.handler.component.search4OperationStatistic.CommonFieldColumn;
import org.apache.solr.handler.component.ResponseBuilder;
import org.apache.solr.handler.component.SearchComponent;
import org.apache.solr.schema.IndexSchema;
import org.apache.solr.search.DocListAndSet;
import java.io.IOException;

/*
 * Created by lingxiao on 2016/7/26.
 *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class TableComponent extends SearchComponent {

    public static String COMPONENT_NAME = "table_component";

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
        TableDocValues docValues = new TableDocValues();
        docValues.sa_area_id = new CommonFieldColumn(schema.getField("sa_area_id"));
        docValues.sa_area_name = new CommonFieldColumn(schema.getField("sa_area_name"));
        docValues.sa_sort_code = new CommonFieldColumn(schema.getField("sa_sort_code"));
        docValues.sa_order_num = new CommonFieldColumn(schema.getField("sa_order_num"));
        docValues.sa_people_num = new CommonFieldColumn(schema.getField("sa_people_num"));
        docValues.sa_source_amount = new CommonFieldColumn(schema.getField("sa_source_amount"));
        docValues.sa_discount_amount = new CommonFieldColumn(schema.getField("sa_discount_amount"));
        docValues.sa_discount_fee = new CommonFieldColumn(schema.getField("sa_discount_fee"));
        docValues.sa_profit = new CommonFieldColumn(schema.getField("sa_profit"));
        docValues.sa_receive_amount = new CommonFieldColumn(schema.getField("sa_receive_amount"));
        docValues.sa_seat_num = new CommonFieldColumn(schema.getField("sa_seat_num"));
        docValues.sa_create_time = new CommonFieldColumn(schema.getField("sa_create_time"));
        DocListAndSet results = responseBuilder.getResults();
        TableCollector collector = new TableCollector(docValues);
        responseBuilder.req.getSearcher().search(results.docSet.getTopFilter(), collector);
        responseBuilder.rsp.add("table_component", collector.write(collector.tableSumarryMap));
    }

    @Override
    public String getDescription() {
        return COMPONENT_NAME;
    }
}
