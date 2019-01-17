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
package com.qlangtech.tis.solrextend.handler.component.s4supplyAudit;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.queries.function.FunctionValues;
import org.apache.lucene.queries.function.ValueSource;
import org.apache.lucene.search.SimpleCollector;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.handler.component.ResponseBuilder;
import org.apache.solr.handler.component.SearchComponent;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.schema.IndexSchema;
import org.apache.solr.schema.SchemaField;
import org.apache.solr.search.DocListAndSet;
import org.apache.solr.search.SolrIndexSearcher;

/*
 * 【供应商版供应链】项目首页需要提供订单数的聚合查询，请协助！<br>
 * 1.需求说明：<br>
 * 销售订单：统计【待审核】及【待处理】的销售订单的总单数<br>
 * 配送发货：统计【待发货】及【拣货完成】的配送发货单的总单数<br>
 * 销售退货：统计【待审核】及【退货中】的销售退货单的总单数<br>
 * 2.涉及索引：supplyGoods->search4supplyAudit<br>
 * 3.聚合（group by）字段：<br>
 * `type` tinyint(2) NOT NULL COMMENT '单据类型: 1.采购单, 2.配送单, 3.入库单, 4.退货单, 5.加工单,
 * 6.盘存单, 7.门店调拨单',<br>
 * 4.期望结果： Map<Short, Integer> <br>
 * key(Short)<br>
 * value(Integer)<br>
 * 单据类型<br>
 * 符合条件的单据数量<br>
 *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class TypeGroupByComponent extends SearchComponent {

    private static final String COMPONENT_NAME = "typeGroupby";

    @Override
    public void prepare(ResponseBuilder rb) throws IOException {
        if (rb.req.getParams().getBool(COMPONENT_NAME, false)) {
            rb.setNeedDocSet(true);
        }
    }

    @Override
    public void process(ResponseBuilder rb) throws IOException {
        SolrQueryRequest req = rb.req;
        SolrParams params = req.getParams();
        if (!params.getBool(COMPONENT_NAME, false)) {
            return;
        }
        IndexSchema schema = req.getSchema();
        SolrIndexSearcher searcher = req.getSearcher();
        SchemaField typeField = schema.getField("type");
        ValueSource vsType = typeField.getType().getValueSource(typeField, null);
        TypeGroupCollector collector = new TypeGroupCollector(vsType);
        DocListAndSet results = rb.getResults();
        // 搜索出7个豆腐块和每个父doc所对应的子doc
        searcher.search(results.docSet.getTopFilter(), collector);
        NamedList<Integer> result = new NamedList<Integer>();
        for (Map.Entry<Integer, int[]> e : collector.typeGroupbyCount.entrySet()) {
            result.add(String.valueOf(e.getKey()), e.getValue()[0]);
        }
        rb.rsp.add(COMPONENT_NAME, result);
    }

    private static class TypeGroupCollector extends SimpleCollector {

        private final ValueSource vsType;

        private FunctionValues typeValues;

        private Map<Integer, int[]> typeGroupbyCount = new HashMap<>();

        TypeGroupCollector(ValueSource vsType) {
            this.vsType = vsType;
        }

        @Override
        protected void doSetNextReader(LeafReaderContext context) throws IOException {
            this.typeValues = vsType.getValues(Collections.emptyMap(), context);
        }

        @Override
        public boolean needsScores() {
            return false;
        }

        @Override
        public void collect(int doc) throws IOException {
            int type = typeValues.intVal(doc);
            int[] count = typeGroupbyCount.get(type);
            if (count == null) {
                count = new int[1];
                typeGroupbyCount.put(type, count);
            }
            count[0]++;
        }
    }

    @Override
    public String getDescription() {
        return COMPONENT_NAME;
    }
}
