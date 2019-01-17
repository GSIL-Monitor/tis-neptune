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
package com.qlangtech.tis.solrextend.queryparse.s4supplyStatementAudit;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import org.apache.lucene.search.Query;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.schema.IndexSchema;
import org.apache.solr.search.QParser;
import org.apache.solr.search.QParserPlugin;
import org.apache.solr.search.SyntaxError;
import com.qlangtech.tis.solrextend.queryparse.s4supplyStatementAudit.SupplyStatementAuditQuery.Accumulator;
import com.google.common.base.Splitter;
import com.google.common.collect.MapMaker;
import com.google.common.collect.Sets;

/*
 * 查询CASE1，在通过type列聚合，分别统计type为3和4的sum总数，然后在resonse中需要通过这两个差值（v=sum(type:3)-sum(type:4)）
 * 进行排序，在结果中v也要展示，结果集合中需要可以翻页
 * http://k.2dfire.net/pages/viewpage.action?pageId=473006169
 *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class SupplyStatementAuditGroupByTradeIdQPlugin extends QParserPlugin implements ISupplyStatementAuditConstant {

    @Override
    @SuppressWarnings("all")
    public QParser createParser(String qstr, SolrParams localParams, SolrParams params, SolrQueryRequest req) {
        try {
            IndexSchema schema = req.getSchema();
            // int storageType = StringUtils.split(, ",");
            Set<Integer> storageTypes = Sets.newHashSet();
            Splitter.on(",").split(localParams.get("storage_type")).forEach((k) -> {
                int storageType = Integer.parseInt(k);
                if (storageType != TYPE_IN_STORAGE_3 && storageType != TYPE_IN_STORAGE_2 && storageType != TYPE_REFUND_4) {
                    throw new IllegalArgumentException("storageType:" + storageType + " is illegal");
                }
                storageTypes.add(storageType);
            });
            String groupKey = getGroupKey(localParams);
            boolean needsummary = req.getParams().getBool("type3And4Summary", false);
            Map<String, Accumulator> /* tradeid */
            amountAccumulator = new MapMaker().makeComputingMap((k) -> {
                return new Accumulator(storageTypes.contains(TYPE_IN_STORAGE_3));
            });
            req.getContext().put(KEY_AMOUNT_ACCUMULATOR, amountAccumulator);
            final // 
            SupplyStatementAuditQuery auditQuery = new SupplyStatementAuditQuery(storageTypes, needsummary, groupKey, amountAccumulator);
            return new QParser(qstr, localParams, params, req) {

                @Override
                public Query parse() throws SyntaxError {
                    return auditQuery;
                }
            };
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getGroupKey(SolrParams localParams) {
        String groupKey = localParams.get("groupby");
        if (!FIELD_KEY_GROUP_COLUMN_TRADE_ID.equals(groupKey) && !FIELD_KEY_GROUP_COLUMN_SUPPLY_TYPE_ID.equals(groupKey)) {
            throw new IllegalArgumentException("groupKey:" + groupKey + " is illegal");
        }
        return groupKey;
    }

    /**
     * 取全部单据的统计
     *
     * @param req
     * @return
     */
    public static Accumulator getAllSummaryAmountAccumulator(SolrQueryRequest req) {
        Map<String, Accumulator> /* tradeid */
        allAmount = getAmountAccumulator(req);
        Accumulator allsummary = allAmount.get(KEY_ALL_SUMMARY_TYPE_3_AND_4_SUM);
        if (allsummary == null) {
            throw new IllegalStateException(KEY_ALL_SUMMARY_TYPE_3_AND_4_SUM + " relevant object is not exist");
        }
        return allsummary;
    }

    public static Accumulator getAllSummaryCountStatus3ANDNot3Accumulator(SolrQueryRequest req) {
        Map<String, Accumulator> /* tradeid */
        allAmount = getAmountAccumulator(req);
        Accumulator allCount = allAmount.get(KEY_ALL_SUMMARY_Status3_AND_Not_COUNT);
        if (allCount == null) {
            throw new IllegalStateException(KEY_ALL_SUMMARY_Status3_AND_Not_COUNT + " relevant object is not exist");
        }
        return allCount;
    }

    @SuppressWarnings("all")
    public static Map<String, /* tradeid */
    Accumulator> getAmountAccumulator(SolrQueryRequest req) {
        Map<String, Accumulator> /* tradeid */
        result = (Map<String, /* tradeid */
        Accumulator>) req.getContext().get(KEY_AMOUNT_ACCUMULATOR);
        if (result == null) {
            throw new IllegalStateException(KEY_AMOUNT_ACCUMULATOR + " relevant object is not exist");
        }
        return result;
    }
}
