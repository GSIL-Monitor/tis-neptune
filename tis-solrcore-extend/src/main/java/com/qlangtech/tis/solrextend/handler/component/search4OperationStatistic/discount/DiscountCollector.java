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
package com.qlangtech.tis.solrextend.handler.component.search4OperationStatistic.discount;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.qlangtech.tis.solrextend.handler.component.TisMoney;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.search.SimpleCollector;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/*
 * Created by lingxiao on 2016/7/26.
 *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class DiscountCollector extends SimpleCollector {

    public DiscountDocValues docValues;

    public DiscountSumarry dcSummary;

    public HashMap<String, DiscountSumarry> dcSummaryMap;

    public DiscountCollector(DiscountDocValues docValues) {
        this.docValues = docValues;
        this.dcSummary = new DiscountSumarry();
        this.dcSummaryMap = new HashMap<>();
    }

    @Override
    public void doSetNextReader(LeafReaderContext context) {
        docValues.doSetNextReader(context);
    }

    @Override
    public boolean needsScores() {
        return false;
    }

    @Override
    public void collect(int doc) throws IOException {
        docValues.fillValue(doc);
        String dc_Discount_id = docValues.getDc_discount_id();
        String dc_Discount_name = docValues.getDc_discount_name();
        String dc_Discount_fee = docValues.getDc_discount_fee();
        String dc_Discount_num = docValues.getDc_discount_num();
        if (dc_Discount_id == null && StringUtils.isBlank(dc_Discount_id)) {
            return;
        }
        if (dc_Discount_name == null && StringUtils.isBlank(dc_Discount_name)) {
            return;
        }
        if (dc_Discount_fee == null && StringUtils.isBlank(dc_Discount_fee)) {
            return;
        }
        if (dc_Discount_num == null && StringUtils.isBlank(dc_Discount_num)) {
            return;
        }
        String[] ids = StringUtils.split(dc_Discount_id, ",");
        String[] names = StringUtils.split(dc_Discount_name, ",");
        String[] fees = StringUtils.split(dc_Discount_fee, ",");
        String[] nums = StringUtils.split(dc_Discount_num, ",");
        for (int i = 0; i < names.length; i++) {
            // 按照id聚合
            DiscountSumarry dcSummary = dcSummaryMap.get(ids[i]);
            TisMoney fee = TisMoney.create(fees[i]);
            TisMoney num = TisMoney.create(nums[i]);
            if (dcSummary == null) {
                dcSummary = new DiscountSumarry(names[i], num, fee);
                dcSummaryMap.put(ids[i], dcSummary);
            } else {
                DiscountSumarry dcSummary1 = new DiscountSumarry(names[i], num, fee);
                dcSummary.addIncrease(dcSummary1);
            }
        }
    }

    public String write(HashMap<String, DiscountSumarry> map) {
        JSONArray result = new JSONArray();
        TisMoney sumDiscountFee = TisMoney.create();
        TisMoney sumDiscountNum = TisMoney.create();
        for (Map.Entry<String, DiscountSumarry> entry : map.entrySet()) {
            JSONObject o = new JSONObject();
            o.put("discountName", entry.getValue().dc_discount_name);
            o.put("discountFee", entry.getValue().dc_discount_fee.toString());
            o.put("discountNum", entry.getValue().dc_discount_num.toString());
            sumDiscountFee.add(entry.getValue().dc_discount_fee);
            sumDiscountNum.add(entry.getValue().dc_discount_num);
            result.add(o);
        }
        JSONObject sum = new JSONObject();
        sum.put("discountName", "合计");
        sum.put("discountFee", sumDiscountFee.toString());
        sum.put("discountNum", sumDiscountNum.toString());
        result.add(sum);
        return result.toJSONString();
    }
}
