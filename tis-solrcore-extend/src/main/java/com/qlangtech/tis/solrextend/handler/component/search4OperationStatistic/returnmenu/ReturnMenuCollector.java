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
package com.qlangtech.tis.solrextend.handler.component.search4OperationStatistic.returnmenu;

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
 * Created by lingxiao on 2016/7/27.
 *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class ReturnMenuCollector extends SimpleCollector {

    public ReturnMenuDocValues docValues;

    public ReturnMenuSumary rmSummary;

    public HashMap<String, ReturnMenuSumary> rmSummaryMap;

    public ReturnMenuCollector(ReturnMenuDocValues docValues) {
        this.docValues = docValues;
        this.rmSummary = new ReturnMenuSumary();
        this.rmSummaryMap = new HashMap<>();
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
        String rm_menu_id = docValues.getRm_menu_id();
        String rm_menu_name = docValues.getRm_menu_name();
        String rm_kind_id = docValues.getRm_kind_id();
        String rm_kind_name = docValues.getRm_kind_name();
        String rm_unit = docValues.getRm_unit();
        String rm_price = docValues.getRm_price();
        String rm_num = docValues.getRm_num();
        String rm_fee = docValues.getRm_fee();
        if (rm_menu_id == null && StringUtils.isBlank(rm_menu_id)) {
            return;
        }
        if (rm_menu_name == null && StringUtils.isBlank(rm_menu_name)) {
            return;
        }
        if (rm_kind_id == null && StringUtils.isBlank(rm_kind_id)) {
            return;
        }
        if (rm_kind_name == null && StringUtils.isBlank(rm_kind_name)) {
            return;
        }
        if (rm_unit == null && StringUtils.isBlank(rm_unit)) {
            return;
        }
        if (rm_price == null && StringUtils.isBlank(rm_price)) {
            return;
        }
        if (rm_num == null && StringUtils.isBlank(rm_num)) {
            return;
        }
        if (rm_fee == null && StringUtils.isBlank(rm_fee)) {
            return;
        }
        String[] menu_ids = StringUtils.split(rm_menu_id, ",");
        String[] kind_ids = StringUtils.split(rm_kind_id, ",");
        String[] menu_names = StringUtils.split(rm_menu_name, ",");
        String[] kind_names = StringUtils.split(rm_kind_name, ",");
        String[] rm_units = StringUtils.split(rm_unit, ",");
        String[] rm_prices = StringUtils.split(rm_price, ",");
        String[] rm_nums = StringUtils.split(rm_num, ",");
        String[] rm_fees = StringUtils.split(rm_fee, ",");
        if (menu_names.length > 0 && menu_names.length == kind_names.length) {
            for (int i = 0; i < menu_names.length; i++) {
                String concatNameUnit = menu_names[i] + "," + kind_names[i] + "," + rm_units[i];
                String concatIdUnit = menu_ids[i] + "," + kind_ids[i] + "," + rm_units[i];
                ReturnMenuSumary whSummary = rmSummaryMap.get(concatIdUnit);
                TisMoney num = TisMoney.create(rm_nums[i]);
                TisMoney fee = TisMoney.create(rm_fees[i]);
                if (whSummary == null) {
                    whSummary = new ReturnMenuSumary(concatNameUnit, rm_prices[i], num, fee);
                    rmSummaryMap.put(concatIdUnit, whSummary);
                } else {
                    ReturnMenuSumary whSummary1 = new ReturnMenuSumary(concatNameUnit, rm_prices[i], num, fee);
                    whSummary.addIncrease(whSummary);
                }
            }
        } else {
            System.out.println("solr数据无法对齐");
            return;
        }
    }

    public String write(HashMap<String, ReturnMenuSumary> map) {
        JSONArray result = new JSONArray();
        TisMoney sumNum = TisMoney.create();
        TisMoney sumFee = TisMoney.create();
        for (Map.Entry<String, ReturnMenuSumary> entry : map.entrySet()) {
            JSONObject o = new JSONObject();
            String[] concatNameUnits = entry.getValue().concatNameUnits.split(",");
            o.put("menuName", concatNameUnits[0]);
            o.put("kindName", concatNameUnits[1]);
            o.put("unit", concatNameUnits[2]);
            o.put("price", entry.getValue().rm_price);
            o.put("num", entry.getValue().rm_num.toString());
            o.put("fee", entry.getValue().rm_fee.toString());
            sumNum.add(entry.getValue().rm_num);
            sumFee.add(entry.getValue().rm_fee);
            result.add(o);
        }
        JSONObject sum = new JSONObject();
        sum.put("menuName", "合计");
        sum.put("kindName", "-");
        sum.put("unit", "-");
        sum.put("price", "-");
        sum.put("num", sumNum.toString());
        sum.put("fee", sumFee.toString());
        result.add(sum);
        return result.toJSONString();
    }
}
