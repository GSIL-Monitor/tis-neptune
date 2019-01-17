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
package com.qlangtech.tis.solrextend.handler.component.search4OperationStatistic.kindname;

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
public class KindNameCollector extends SimpleCollector {

    public KindNameDocValues docValues;

    public KindNameSumarry knSummary;

    public HashMap<String, KindNameSumarry> knSummaryMap;

    public KindNameCollector(KindNameDocValues docValues) {
        this.docValues = docValues;
        this.knSummary = new KindNameSumarry();
        this.knSummaryMap = new HashMap<>();
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
        String km_kind_id = docValues.getKm_kind_id();
        String km_kind_name = docValues.getKm_kind_name();
        String km_create_time = docValues.getKm_create_time();
        String km_sort_code = docValues.getKm_sort_code();
        String km_sale_num = docValues.getKm_sale_num();
        String km_fee = docValues.getKm_fee();
        String km_ratio_fee = docValues.getKm_ratio_fee();
        String km_gift_num = docValues.getKm_gift_num();
        String km_return_num = docValues.getKm_return_num();
        if (km_create_time == null && StringUtils.isBlank(km_create_time)) {
            return;
        }
        if (km_kind_name == null && StringUtils.isBlank(km_kind_name)) {
            return;
        }
        if (km_sort_code == null && StringUtils.isBlank(km_sort_code)) {
            return;
        }
        if (km_sale_num == null && StringUtils.isBlank(km_sale_num)) {
            return;
        }
        if (km_fee == null && StringUtils.isBlank(km_fee)) {
            return;
        }
        if (km_ratio_fee == null && StringUtils.isBlank(km_ratio_fee)) {
            return;
        }
        if (km_gift_num == null && StringUtils.isBlank(km_gift_num)) {
            return;
        }
        if (km_return_num == null && StringUtils.isBlank(km_return_num)) {
            return;
        }
        String[] ids = StringUtils.split(km_kind_id, ",");
        String[] names = StringUtils.split(km_kind_name, ",");
        String[] create_times = StringUtils.split(km_create_time, ",");
        String[] sort_codes = StringUtils.split(km_sort_code, ",");
        String[] nums = StringUtils.split(km_sale_num, ",");
        String[] fees = StringUtils.split(km_fee, ",");
        String[] ratio_fees = StringUtils.split(km_ratio_fee, ",");
        String[] gift_nums = StringUtils.split(km_gift_num, ",");
        String[] return_nums = StringUtils.split(km_return_num, ",");
        for (int i = 0; i < names.length; i++) {
            KindNameSumarry whSummary = knSummaryMap.get(ids[i]);
            TisMoney num = TisMoney.create(nums[i]);
            TisMoney fee = TisMoney.create(fees[i]);
            TisMoney ratio_fee = TisMoney.create(ratio_fees[i]);
            TisMoney gift_num = TisMoney.create(gift_nums[i]);
            TisMoney return_num = TisMoney.create(return_nums[i]);
            if (names[i].equals("热菜")) {
                System.out.println(names[i] + nums[i]);
            }
            if (whSummary == null) {
                whSummary = new KindNameSumarry(names[i], num, fee, ratio_fee, gift_num, return_num, sort_codes[i], create_times[i]);
                knSummaryMap.put(ids[i], whSummary);
            } else {
                KindNameSumarry whSummary1 = new KindNameSumarry(names[i], num, fee, ratio_fee, gift_num, return_num, sort_codes[i], create_times[i]);
                whSummary.addIncrease(whSummary1);
            }
        }
    }

    public String write(HashMap<String, KindNameSumarry> map) {
        JSONArray result = new JSONArray();
        TisMoney sumSaleNum = TisMoney.create();
        TisMoney sumFee = TisMoney.create();
        TisMoney sumRatioFee = TisMoney.create();
        TisMoney sumGiftNum = TisMoney.create();
        TisMoney sumReturnNum = TisMoney.create();
        for (Map.Entry<String, KindNameSumarry> entry : map.entrySet()) {
            JSONObject o = new JSONObject();
            o.put("kindName", entry.getValue().km_kind_name);
            o.put("saleNum", entry.getValue().km_sale_num.toString());
            o.put("fee", entry.getValue().km_fee.toString());
            o.put("ratioFee", entry.getValue().km_ratio_fee.toString());
            o.put("giftNum", entry.getValue().km_gift_num.toString());
            o.put("returnNum", entry.getValue().km_return_num.toString());
            o.put("sortCode", entry.getValue().km_sort_code.toString());
            o.put("createTime", entry.getValue().km_create_time.toString());
            sumSaleNum.add(entry.getValue().km_sale_num);
            sumFee.add(entry.getValue().km_fee);
            sumRatioFee.add(entry.getValue().km_ratio_fee);
            sumGiftNum.add(entry.getValue().km_gift_num);
            sumReturnNum.add(entry.getValue().km_return_num);
            result.add(o);
        }
        JSONObject sum = new JSONObject();
        sum.put("kindName", "总计");
        sum.put("saleNum", sumSaleNum.toString());
        sum.put("fee", sumFee.toString());
        sum.put("ratioFee", sumRatioFee.toString());
        sum.put("giftNum", sumGiftNum.toString());
        sum.put("returnNum", sumReturnNum.toString());
        result.add(sum);
        return result.toJSONString();
    }
}
