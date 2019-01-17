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
package com.qlangtech.tis.solrextend.handler.component.search4OperationStatistic.kindPay;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.qlangtech.tis.solrextend.handler.component.TisMoney;
import com.qlangtech.tis.solrextend.handler.component.search4OperationStatistic.TisUtil;
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
public class KindPayCollector extends SimpleCollector {

    public KindpayDocValues docValues;

    public KindPaySumarry kpSummary;

    public HashMap<String, KindPaySumarry> kpSummaryMap;

    public KindPayCollector(KindpayDocValues docValues) {
        this.docValues = docValues;
        this.kpSummary = new KindPaySumarry();
        this.kpSummaryMap = new HashMap<>();
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
        String kp_kindpay_id = docValues.getId();
        String kp_kindpay_name = docValues.getName();
        String kp_kindpay_fee = docValues.getFee();
        String kp_coupon_fee = docValues.getCouponFee();
        String kp_sort_code = docValues.getCode();
        String kp_create_time = docValues.getTime();
        if (kp_create_time == null && StringUtils.isBlank(kp_create_time)) {
            return;
        }
        if (kp_sort_code == null && StringUtils.isBlank(kp_sort_code)) {
            return;
        }
        if (kp_kindpay_id == null && StringUtils.isBlank(kp_kindpay_id)) {
            return;
        }
        if (kp_kindpay_name == null && StringUtils.isBlank(kp_kindpay_name)) {
            return;
        }
        if (kp_kindpay_fee == null && StringUtils.isBlank(kp_kindpay_fee)) {
            return;
        }
        if (kp_coupon_fee == null && StringUtils.isBlank(kp_coupon_fee)) {
            return;
        }
        String[] ids = kp_kindpay_id.split(",");
        String[] names = kp_kindpay_name.split(",");
        String[] fees = kp_kindpay_fee.split(",");
        String[] coupon_fees = kp_coupon_fee.split(",");
        String[] sort_codes = kp_sort_code.split(",");
        String[] create_times = kp_create_time.split(",");
        for (int i = 0; i < names.length; i++) {
            KindPaySumarry kpSummary = kpSummaryMap.get(ids[i]);
            TisMoney fee = TisMoney.create(fees[i]);
            TisMoney coupon_fee = TisMoney.create(coupon_fees[i]);
            if (kpSummary == null) {
                kpSummary = new KindPaySumarry(names[i], fee, coupon_fee, sort_codes[i], create_times[i]);
                kpSummaryMap.put(ids[i], kpSummary);
            } else {
                KindPaySumarry kpSummary1 = new KindPaySumarry(names[i], fee, coupon_fee, sort_codes[i], create_times[i]);
                kpSummary.addIncrease(kpSummary1);
            }
        }
    }

    public String write(HashMap<String, KindPaySumarry> map) {
        JSONArray result = new JSONArray();
        TisMoney sumKindPayFee = TisMoney.create();
        TisMoney sumCouponFee = TisMoney.create();
        for (Map.Entry<String, KindPaySumarry> entry : map.entrySet()) {
            JSONObject o = new JSONObject();
            o.put("kindPayName", entry.getValue().kp_kindpay_name);
            // 总金额减去优惠就为实收
            o.put("kindPayFee", TisUtil.subtract(entry.getValue().kp_kindpay_fee, entry.getValue().kp_coupon_fee).toString());
            o.put("couponFee", entry.getValue().kp_coupon_fee.toString());
            o.put("sortCode", entry.getValue().kp_sort_code.toString());
            o.put("createTime", entry.getValue().kp_create_time.toString());
            sumKindPayFee.add(entry.getValue().kp_kindpay_fee);
            sumCouponFee.add(entry.getValue().kp_coupon_fee);
            result.add(o);
        }
        JSONObject o = new JSONObject();
        o.put("kindPayName", "合计");
        o.put("kindPayFee", TisUtil.subtract(sumKindPayFee, sumCouponFee).toString());
        o.put("couponFee", sumCouponFee.toString());
        result.add(o);
        return result.toJSONString();
    }
}
