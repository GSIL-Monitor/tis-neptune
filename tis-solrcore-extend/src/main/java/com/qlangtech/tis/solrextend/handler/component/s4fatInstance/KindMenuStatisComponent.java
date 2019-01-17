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
package com.qlangtech.tis.solrextend.handler.component.s4fatInstance;

import org.apache.solr.handler.component.ResponseBuilder;
import org.apache.solr.handler.component.SearchComponent;
import org.apache.solr.schema.IndexSchema;
import org.apache.solr.search.DocListAndSet;
import java.io.IOException;

/*
 * 简要日报表统计
 * Created by lingxiao on 2016/6/23.
 *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class KindMenuStatisComponent extends SearchComponent {

    // 插件名称
    public static final String COMPONENT_NAME = "kind_menu_statis_component";

    @Override
    public void prepare(ResponseBuilder rb) throws IOException {
        if (rb.req.getParams().getBool(COMPONENT_NAME, false)) {
            rb.setNeedDocSet(true);
        }
    }

    @Override
    public void process(ResponseBuilder rb) throws IOException {
        if (!rb.req.getParams().getBool(COMPONENT_NAME, false)) {
            return;
        }
        IndexSchema schema = rb.req.getSchema();
        KindMenuDocValues docValues = new KindMenuDocValues();
        docValues.group_or_kind_id = new FieldColumn(schema.getField("group_or_kind_id"));
        docValues.group_or_kind_name = new FieldColumn(schema.getField("group_or_kind_name"));
        docValues.accountNumColumn = new FieldColumn(schema.getField("account_num"));
        docValues.feeColumn = new FieldColumn(schema.getField("in_fee"));
        docValues.ratiofeeColumn = new FieldColumn(schema.getField("ratio_fee"));
        DocListAndSet results = rb.getResults();
        KindMenuJSONFloatPairCollector collector = new KindMenuJSONFloatPairCollector(docValues);
        rb.req.getSearcher().search(results.docSet.getTopFilter(), collector);
        // 将map结果返回
        rb.rsp.add("kind_menu_statis_component", collector.write(collector.summaryStatisMap));
    }

    @Override
    public String getDescription() {
        return COMPONENT_NAME;
    }
}
