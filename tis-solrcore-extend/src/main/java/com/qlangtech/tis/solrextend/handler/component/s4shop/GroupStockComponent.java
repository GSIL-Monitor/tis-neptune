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

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.index.DocValues;
import org.apache.lucene.index.NumericDocValues;
import org.apache.solr.handler.component.ResponseBuilder;
import org.apache.solr.handler.component.SearchComponent;
import org.apache.solr.search.DocIterator;
import org.apache.solr.search.DocListAndSet;
import org.apache.solr.search.DocSet;
import java.io.IOException;
import java.util.*;

/* *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class GroupStockComponent extends SearchComponent {

    public static final String COMPONENT_NAME = "group_stock";

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
        DocListAndSet results = responseBuilder.getResults();
        Map<String, StockDetail> timeAndDiscountStockDetail = new HashMap<>();
        DocSet docSet = results.docSet;
        NumericDocValues remainVolume = DocValues.getNumeric(responseBuilder.req.getSearcher().getLeafReader(), "remain_volume");
        NumericDocValues salesVolume = DocValues.getNumeric(responseBuilder.req.getSearcher().getLeafReader(), "sales_volume");
        NumericDocValues totalVolume = DocValues.getNumeric(responseBuilder.req.getSearcher().getLeafReader(), "total");
        NumericDocValues sellTime = DocValues.getNumeric(responseBuilder.req.getSearcher().getLeafReader(), "sell_time");
        NumericDocValues discount = DocValues.getNumeric(responseBuilder.req.getSearcher().getLeafReader(), "discount");
        DocIterator iterator = docSet.iterator();
        while (iterator.hasNext()) {
            int doc = iterator.nextDoc();
            long remainNum = remainVolume.get(doc);
            long salesNum = salesVolume.get(doc);
            long totalNum = totalVolume.get(doc);
            long sellTimeNum = sellTime.get(doc);
            long discountNum = discount.get(doc);
            String timeAndDiscountKey = sellTimeNum + ":" + discountNum;
            StockDetail stockDetail;
            if ((stockDetail = timeAndDiscountStockDetail.get(timeAndDiscountKey)) != null) {
                stockDetail.updateStockNum(remainNum, salesNum, totalNum);
            } else {
                stockDetail = new StockDetail(remainNum, salesNum, totalNum);
            }
            timeAndDiscountStockDetail.put(timeAndDiscountKey, stockDetail);
        }
        Map<String, List<String>> resultBody = new HashMap<>();
        for (String key : timeAndDiscountStockDetail.keySet()) {
            String[] keyArr = StringUtils.split(key, ":");
            List<String> discountDetailList = resultBody.getOrDefault(keyArr[0], new LinkedList<>());
            discountDetailList.add(keyArr[1] + ":" + timeAndDiscountStockDetail.get(key).toString());
            Collections.sort(discountDetailList);
            resultBody.put(keyArr[0], discountDetailList);
        }
        responseBuilder.rsp.add(COMPONENT_NAME, resultBody);
    }

    @Override
    public String getDescription() {
        return COMPONENT_NAME;
    }

    private class StockDetail {

        long remainNum;

        long salesNum;

        long totalNum;

        public StockDetail(long remainNum, long salesNum, long totalNum) {
            this.remainNum = remainNum;
            this.salesNum = salesNum;
            this.totalNum = totalNum;
        }

        public void updateStockNum(long remainNumAdd, long salesNumAdd, long totalNumAdd) {
            this.remainNum += remainNumAdd;
            this.salesNum += salesNumAdd;
            this.totalNum += totalNumAdd;
        }

        @Override
        public String toString() {
            return remainNum + "," + salesNum + "," + totalNum;
        }
    }
}
