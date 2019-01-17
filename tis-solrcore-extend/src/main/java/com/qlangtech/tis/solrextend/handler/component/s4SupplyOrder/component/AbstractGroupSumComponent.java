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
package com.qlangtech.tis.solrextend.handler.component.s4SupplyOrder.component;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.search.Collector;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.handler.component.ResponseBuilder;
import org.apache.solr.handler.component.SearchComponent;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.schema.IndexSchema;
import org.apache.solr.search.DocListAndSet;
import org.apache.solr.search.SolrIndexSearcher;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/*
 * 将最多一个月内每天的金额进行汇总
 * 应用名：search4supplyOrder
 * 需求：条件字段 status（订单状态）、pay_time（支付完成时间）、seller_entity_id（卖家实体ID）。
 * seller_entity_id指定某个卖家；status指定订单的多种状态（待发货，待收货，待清分等）；pay_time时间跨度是一个月；
 * 按照上述条件以天为单位统计discount_amount的合计。
 * <p>
 * 应用名：search4supplyRefund
 * 需求：条件字段 status（退货单状态）、seller_entity_id（卖家实体ID）、order_status（订单状态）、pay_time（支付时间）。
 * seller_entity_id指定某个卖家；status指定退货单的状态（已退款）；pay_time时间跨度是一个月；order_status指定订单的多种状态（待发货，待收货，待清分等）。
 * 按照上述条件以天为单位统计discount_amount的合计。
 * <p>
 * 设置groupField字段为long 日期类型
 * 设置sumField字段为float数值类型
 * <p>
 * Created by Qinjiu on 6/27/2017.
 *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public abstract class AbstractGroupSumComponent extends SearchComponent {

    private static final String COMPONENT_NAME = "GroupSum";

    @Override
    public void prepare(ResponseBuilder rb) throws IOException {
        if (rb.req.getParams().getBool(COMPONENT_NAME, false)) {
            rb.setNeedDocSet(true);
        }
    }

    @Override
    public void process(ResponseBuilder responseBuilder) throws IOException {
        SolrQueryRequest req = responseBuilder.req;
        SolrParams params = req.getParams();
        if (!params.getBool(COMPONENT_NAME, false)) {
            return;
        }
        String groupField = params.get("groupField");
        String sumField = params.get("sumField");
        // 检查两个字段的 参数是否都不为空
        if (StringUtils.isBlank(groupField)) {
            throw new IllegalStateException("groupField字段不能为空");
        }
        if (StringUtils.isBlank(sumField)) {
            throw new IllegalStateException("sumField字段不能为空");
        }
        // 检查两个字段的 参数是否都在schama中
        IndexSchema schema = req.getSchema();
        if (schema.getField(groupField) == null || schema.getField(sumField) == null) {
            throw new IllegalStateException("groupField字段'" + groupField + "'在schema中不存在");
        }
        if (schema.getField(sumField) == null) {
            throw new IllegalStateException("sumField字段'" + sumField + "'在schema中不存在");
        }
        SolrIndexSearcher searcher = req.getSearcher();
        Map<String, Number> resultMap = new HashMap<>();
        DocListAndSet results = responseBuilder.getResults();
        searcher.search(results.docSet.getTopFilter(), this.getCollector(req, groupField, sumField, resultMap));
        this.formatResultMap(resultMap);
        responseBuilder.rsp.add(COMPONENT_NAME, resultMap);
    }

    @Override
    public String getDescription() {
        return COMPONENT_NAME;
    }

    // 生成不同类型的collector
    abstract Collector getCollector(SolrQueryRequest req, String groupField, String sumField, Map<String, Number> resultMap);

    // 对最终的结果做格式化处理
    abstract void formatResultMap(Map<String, Number> resultMap);
}
