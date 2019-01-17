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
package com.qlangtech.tis.solrextend.handler.component.search4OperationStatistic.warehouse;

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
 * Created by lingxiao on 2016/7/25.
 *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class WarehouseCollector extends SimpleCollector {

    public WarehouseDocValues docValues;

    public WarehouseSumarry whSummary;

    public HashMap<String, WarehouseSumarry> whSummaryMap;

    public WarehouseCollector(WarehouseDocValues docValues) {
        this.docValues = docValues;
        this.whSummary = new WarehouseSumarry();
        this.whSummaryMap = new HashMap<>();
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
        String wh_warehouse_id = docValues.getId();
        String wh_warehouse_name = docValues.getName();
        String wh_num = docValues.getNum();
        String wh_fee = docValues.getFee();
        String wh_ratio_fee = docValues.getRatiofee();
        String wh_discount_fee = docValues.getDiscountfee();
        if (wh_warehouse_name == null && StringUtils.isBlank(wh_warehouse_name)) {
            return;
        }
        if (wh_num == null && StringUtils.isBlank(wh_num)) {
            return;
        }
        if (wh_fee == null && StringUtils.isBlank(wh_fee)) {
            return;
        }
        if (wh_ratio_fee == null && StringUtils.isBlank(wh_ratio_fee)) {
            return;
        }
        if (wh_discount_fee == null && StringUtils.isBlank(wh_discount_fee)) {
            return;
        }
        String[] ids = wh_warehouse_id.split(",");
        String[] names = wh_warehouse_name.split(",");
        String[] nums = wh_num.split(",");
        String[] wh_fees = wh_fee.split(",");
        String[] wh_ratio_fees = wh_ratio_fee.split(",");
        String[] wh_discount_fees = wh_discount_fee.split(",");
        for (int i = 0; i < names.length; i++) {
            WarehouseSumarry whSummary = whSummaryMap.get(ids[i]);
            TisMoney num = TisMoney.create(nums[i]);
            TisMoney fee = TisMoney.create(wh_fees[i]);
            TisMoney ratio_fee = TisMoney.create(wh_ratio_fees[i]);
            TisMoney discount_fee = TisMoney.create(wh_discount_fees[i]);
            if (whSummary == null) {
                whSummary = new WarehouseSumarry(names[i], num, fee, ratio_fee, discount_fee);
                whSummaryMap.put(ids[i], whSummary);
            } else {
                WarehouseSumarry whSummary1 = new WarehouseSumarry(names[i], num, fee, ratio_fee, discount_fee);
                whSummary.addIncrease(whSummary1);
            }
        }
    }

    public String write(HashMap<String, WarehouseSumarry> map) {
        JSONArray result = new JSONArray();
        TisMoney sumNum = TisMoney.create();
        TisMoney sumFee = TisMoney.create();
        TisMoney sumRationFee = TisMoney.create();
        TisMoney sumDiscountFee = TisMoney.create();
        for (Map.Entry<String, WarehouseSumarry> entry : map.entrySet()) {
            JSONObject o = new JSONObject();
            o.put("warehouseName", entry.getValue().wh_warehouse_name);
            o.put("num", entry.getValue().wh_num.toString());
            o.put("fee", entry.getValue().wh_fee.toString());
            o.put("ratioFee", entry.getValue().wh_ratio_fee.toString());
            o.put("discountFee", entry.getValue().wh_discount_fee.toString());
            sumNum.add(entry.getValue().wh_num);
            sumFee.add(entry.getValue().wh_fee);
            sumRationFee.add(entry.getValue().wh_ratio_fee);
            sumDiscountFee.add(entry.getValue().wh_discount_fee);
            result.add(o);
        }
        JSONObject sum = new JSONObject();
        sum.put("warehouseName", "合计");
        sum.put("num", sumNum.toString());
        sum.put("fee", sumFee.toString());
        sum.put("ratioFee", sumRationFee.toString());
        sum.put("discountFee", sumDiscountFee.toString());
        result.add(sum);
        return result.toJSONString();
    }
}
