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
package com.qlangtech.tis.solrextend.transformer.s4supplyStatementAudit;

import java.io.IOException;
import java.util.Map;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.response.ResultContext;
import org.apache.solr.response.transform.DocTransformer;
import org.apache.solr.response.transform.TransformerFactory;
import com.qlangtech.tis.solrextend.queryparse.s4supplyStatementAudit.ISupplyStatementAuditConstant;
import com.qlangtech.tis.solrextend.queryparse.s4supplyStatementAudit.SupplyStatementAuditGroupByTradeIdQPlugin;
import com.qlangtech.tis.solrextend.queryparse.s4supplyStatementAudit.SupplyStatementAuditQuery.Accumulator;
import com.qlangtech.tis.solrextend.queryparse.s4supplyStatementAudit.SupplyStatementAuditRerankQParserPlugin;
import com.qlangtech.tis.solrextend.queryparse.s4supplyStatementAudit.SupplyStatementAuditRerankQParserPlugin.Doc2TradeMap;

/*
 * 聚合之后的三个字段值显示,参考: SupplyStatementAuditQuery.AmountAccumulator
 * http://k.2dfire.net/pages/viewpage.action?pageId=473006169<br>
 *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class AmountSummaryTransformerFactory extends TransformerFactory implements ISupplyStatementAuditConstant {

    // private
    // 
    // @Override
    // public void init(NamedList args) {
    // 
    // super.init(args);
    // }
    @Override
    public DocTransformer create(String field, SolrParams params, SolrQueryRequest req) {
        return new AmountSummaryTransformer();
    }

    private class AmountSummaryTransformer extends DocTransformer {

        private Map<String, Accumulator> /* groupKey */
        amountAccumulator;

        private Doc2TradeMap doc2TradeMap;

        @Override
        public void setContext(ResultContext context) {
            super.setContext(context);
            this.amountAccumulator = SupplyStatementAuditGroupByTradeIdQPlugin.getAmountAccumulator(context.getRequest());
            this.doc2TradeMap = SupplyStatementAuditRerankQParserPlugin.getFromRequestContext();
        }

        public AmountSummaryTransformer() {
            super();
        }

        @Override
        public String getName() {
            return "amountSummary";
        }

        @Override
        public String[] getExtraRequestFields() {
            return RETURN_FIELDS;
        }

        @Override
        public void transform(SolrDocument doc, int docid, float score) throws IOException {
            String groupKey = doc2TradeMap.get(docid);
            Accumulator accumulator = null;
            if (groupKey != null && (accumulator = amountAccumulator.get(groupKey)) != null) {
                doc.setField(RETURN_FIELD_type2SumAmount, accumulator.getType2SumAmount());
                doc.setField(RETURN_FIELD_type3SumAmount, accumulator.getType3SumAmount());
                doc.setField(RETURN_FIELD_type4SumAmount, accumulator.getType4SumAmount());
                doc.setField(RETURN_FIELD_difference, accumulator.getDifference());
                doc.setField(RETURN_FIELD_type2CountAmount, accumulator.getType2CountAmount());
                doc.setField(RETURN_FIELD_type3CountAmount, accumulator.getType3CountAmount());
                doc.setField(RETURN_FIELD_type4CountAmount, accumulator.getType4CountAmount());
            }
        }
    }
}
