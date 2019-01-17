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
package com.qlangtech.tis.solrextend.handler.component.search4OperationStatistic.giftmenu;

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
public class GiftMenuCollector extends SimpleCollector {

    public GiftMenuDocValues docValues;

    public GiftMenuSummary gmSummary;

    public HashMap<String, GiftMenuSummary> gmSummaryMap;

    public GiftMenuCollector(GiftMenuDocValues docValues) {
        this.docValues = docValues;
        this.gmSummary = new GiftMenuSummary();
        this.gmSummaryMap = new HashMap<>();
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
        String gm_menu_id = docValues.getGm_menu_id();
        String gm_menu_name = docValues.getGm_menu_name();
        String gm_kind_id = docValues.getGm_kind_id();
        String gm_kind_name = docValues.getGm_kind_name();
        String gm_unit = docValues.getGm_unit();
        String gm_price = docValues.getGm_price();
        String gm_num = docValues.getGm_num();
        String gm_fee = docValues.getGm_fee();
        if (gm_menu_id == null && StringUtils.isBlank(gm_menu_id)) {
            return;
        }
        if (gm_menu_name == null && StringUtils.isBlank(gm_menu_name)) {
            return;
        }
        if (gm_kind_id == null && StringUtils.isBlank(gm_kind_id)) {
            return;
        }
        if (gm_kind_name == null && StringUtils.isBlank(gm_kind_name)) {
            return;
        }
        if (gm_unit == null && StringUtils.isBlank(gm_unit)) {
            return;
        }
        if (gm_price == null && StringUtils.isBlank(gm_price)) {
            return;
        }
        if (gm_num == null && StringUtils.isBlank(gm_num)) {
            return;
        }
        if (gm_fee == null && StringUtils.isBlank(gm_fee)) {
            return;
        }
        String[] menu_ids = StringUtils.split(gm_menu_id, ",");
        String[] kind_ids = StringUtils.split(gm_kind_id, ",");
        String[] menu_names = StringUtils.split(gm_menu_name, ",");
        String[] kind_names = StringUtils.split(gm_kind_name, ",");
        String[] gm_units = StringUtils.split(gm_unit, ",");
        String[] gm_prices = StringUtils.split(gm_price, ",");
        String[] gm_nums = StringUtils.split(gm_num, ",");
        String[] gm_fees = StringUtils.split(gm_fee, ",");
        if (menu_names.length > 0 && menu_names.length == kind_names.length) {
            for (int i = 0; i < menu_names.length; i++) {
                String concatIdUnit = menu_ids[i] + "," + kind_ids[i] + "," + gm_units[i];
                String concatNameUnit = menu_names[i] + "," + kind_names[i] + "," + gm_units[i];
                GiftMenuSummary whSummary = gmSummaryMap.get(concatIdUnit);
                TisMoney num = TisMoney.create(gm_nums[i]);
                TisMoney fee = TisMoney.create(gm_fees[i]);
                if (whSummary == null) {
                    whSummary = new GiftMenuSummary(concatNameUnit, gm_prices[i], num, fee);
                    gmSummaryMap.put(concatIdUnit, whSummary);
                } else {
                    GiftMenuSummary whSummary1 = new GiftMenuSummary(concatNameUnit, gm_prices[i], num, fee);
                    whSummary.addIncrease(whSummary1);
                }
            }
        } else {
            System.out.println("solr数据无法对齐");
            return;
        }
    }

    public String write(HashMap<String, GiftMenuSummary> map) {
        JSONArray result = new JSONArray();
        TisMoney sumNum = TisMoney.create();
        TisMoney sumFee = TisMoney.create();
        for (Map.Entry<String, GiftMenuSummary> entry : map.entrySet()) {
            JSONObject o = new JSONObject();
            String[] concatNameUnits = entry.getValue().concatNameUnits.split(",");
            o.put("menuName", concatNameUnits[0]);
            o.put("kindName", concatNameUnits[1]);
            o.put("unit", concatNameUnits[2]);
            o.put("price", entry.getValue().gm_price);
            o.put("num", entry.getValue().gm_num.toString());
            o.put("fee", entry.getValue().gm_fee.toString());
            sumNum.add(entry.getValue().gm_num);
            sumFee.add(entry.getValue().gm_fee);
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
