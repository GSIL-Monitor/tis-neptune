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
package com.qlangtech.tis.solrextend.handler.component.s4shop;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.index.DocValues;
import org.apache.lucene.index.NumericDocValues;
import org.apache.lucene.index.SortedDocValues;
import org.apache.solr.core.SolrCore;
import org.apache.solr.handler.component.ResponseBuilder;
import org.apache.solr.handler.component.SearchComponent;
import org.apache.solr.search.DocIterator;
import org.apache.solr.search.DocListAndSet;
import org.apache.solr.search.DocSet;
import org.slf4j.Logger;

/* *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class ShopMinDiscountComponent extends SearchComponent {

    public static final String COMPONENT_NAME = "shop_mindiscount";

    private static final Logger log = SolrCore.requestLog;

    @Override
    public void prepare(ResponseBuilder responseBuilder) throws IOException {
        boolean debugComponent = StringUtils.equals((String) responseBuilder.req.getContext().getOrDefault(getDescription(), "false"), "true");
        // log.info("debugMode:"+debugComponent);
        if (debugComponent || responseBuilder.req.getParams().getBool(getDescription(), false)) {
            responseBuilder.setNeedDocSet(true);
        }
    }

    @Override
    public void process(ResponseBuilder responseBuilder) throws IOException {
        boolean debugComponent = StringUtils.equals((String) responseBuilder.req.getContext().getOrDefault(getDescription(), "false"), "true");
        if (!debugComponent && !responseBuilder.req.getParams().getBool(getDescription(), false)) {
            return;
        }
        boolean debugMode = StringUtils.equals((String) responseBuilder.req.getContext().getOrDefault("debugMode", "false"), "true");
        DocListAndSet results = responseBuilder.getResults();
        Map<String, ShopMindiscount> entityIdAndDiscountStockDetail = new HashMap<>();
        DocSet docSet = results.docSet;
        NumericDocValues remainVolume = DocValues.getNumeric(responseBuilder.req.getSearcher().getLeafReader(), "remain_volume");
        NumericDocValues salesVolume = DocValues.getNumeric(responseBuilder.req.getSearcher().getLeafReader(), "sales_volume");
        NumericDocValues totalVolume = DocValues.getNumeric(responseBuilder.req.getSearcher().getLeafReader(), "total");
        SortedDocValues entityIdDV = DocValues.getSorted(responseBuilder.req.getSearcher().getLeafReader(), "entity_id");
        NumericDocValues discount = DocValues.getNumeric(responseBuilder.req.getSearcher().getLeafReader(), "discount");
        DocIterator iterator = docSet.iterator();
        while (iterator.hasNext()) {
            int doc = iterator.nextDoc();
            long remainNum = remainVolume.get(doc);
            long salesNum = salesVolume.get(doc);
            long totalNum = totalVolume.get(doc);
            String entityId = entityIdDV.get(doc).utf8ToString();
            long discountNum = discount.get(doc);
            ShopMindiscount shopMindiscount = entityIdAndDiscountStockDetail.get(entityId);
            if (shopMindiscount != null) {
                updateShopMindiscount(remainNum, salesNum, totalNum, discountNum, shopMindiscount);
            } else {
                shopMindiscount = new ShopMindiscount(remainNum, salesNum, totalNum, discountNum);
                entityIdAndDiscountStockDetail.put(entityId, shopMindiscount);
            }
        }
        Map<String, List<String>> resultBody = new HashMap<>();
        for (String entityId : entityIdAndDiscountStockDetail.keySet()) {
            List<String> discountDetailList = resultBody.getOrDefault(entityId, new LinkedList<>());
            ShopMindiscount shopMindiscount = entityIdAndDiscountStockDetail.get(entityId);
            if (!isValid(shopMindiscount)) {
                continue;
            }
            discountDetailList.add(shopMindiscount.toString());
            Collections.sort(discountDetailList);
            resultBody.put(entityId, discountDetailList);
        }
        if (debugMode) {
            log.info("component:" + getDescription());
            log.info("the query:" + responseBuilder.req.getParams().get("q"));
            for (String key : resultBody.keySet()) {
                log.info(key + resultBody.get(key), toString() + "\n");
            }
        }
        responseBuilder.rsp.add(getDescription(), resultBody);
    }

    protected boolean isValid(ShopMindiscount shopMindiscount) {
        return true;
    }

    protected void updateShopMindiscount(long remainNum, long salesNum, long totalNum, long discountNum, ShopMindiscount shopMindiscount) {
        if (discountNum < shopMindiscount.discount) {
            shopMindiscount.flash(remainNum, salesNum, totalNum, discountNum);
        } else if (discountNum == shopMindiscount.discount) {
            shopMindiscount.updateMindiscountStockNum(remainNum, salesNum, totalNum);
        }
    }

    @Override
    public String getDescription() {
        return ShopMinDiscountComponent.COMPONENT_NAME;
    }

    protected class ShopMindiscount {

        long remainNum;

        long salesNum;

        long totalNum;

        long discount;

        public ShopMindiscount(long remainNum, long salesNum, long totalNum, long discount) {
            this.remainNum = remainNum;
            this.salesNum = salesNum;
            this.totalNum = totalNum;
            this.discount = discount;
        }

        public void updateMindiscountStockNum(long remainNumAdd, long salesNumAdd, long totalNumAdd) {
            this.remainNum += remainNumAdd;
            this.salesNum += salesNumAdd;
            this.totalNum += totalNumAdd;
        }

        public void flash(long remainNum, long salesNum, long totalNum, long discount) {
            this.remainNum = remainNum;
            this.salesNum = salesNum;
            this.totalNum = totalNum;
            this.discount = discount;
        }

        @Override
        public String toString() {
            return discount + ":" + remainNum + "," + salesNum + "," + totalNum;
        }
    }
}
