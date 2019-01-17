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

import com.qlangtech.tis.solrextend.handler.component.search4OperationStatistic.CommonFieldColumn;
import com.qlangtech.tis.solrextend.handler.component.search4OperationStatistic.DocValuesInterface;
import com.qlangtech.tis.solrextend.handler.component.search4OperationStatistic.TisUtil;
import org.apache.lucene.index.LeafReaderContext;

/*
 * Created by lingxiao on 2016/7/25.
 *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class KindpayDocValues implements DocValuesInterface {

    public CommonFieldColumn kp_kindpay_id;

    public CommonFieldColumn kp_kindpay_name;

    public CommonFieldColumn kp_kindpay_fee;

    public CommonFieldColumn kp_sort_code;

    public CommonFieldColumn kp_create_time;

    public CommonFieldColumn kp_coupon_fee;

    @Override
    public void doSetNextReader(LeafReaderContext context) {
        kp_kindpay_id.doSetNextReader(context);
        kp_kindpay_name.doSetNextReader(context);
        kp_kindpay_fee.doSetNextReader(context);
        kp_sort_code.doSetNextReader(context);
        kp_create_time.doSetNextReader(context);
        kp_coupon_fee.doSetNextReader(context);
    }

    @Override
    public void fillValue(int doc) {
        kp_kindpay_id.filler.fillValue(doc);
        kp_kindpay_name.filler.fillValue(doc);
        kp_kindpay_fee.filler.fillValue(doc);
        kp_sort_code.filler.fillValue(doc);
        kp_create_time.filler.fillValue(doc);
        kp_coupon_fee.filler.fillValue(doc);
    }

    public String getId() {
        return TisUtil.getString(kp_kindpay_id);
    }

    public String getName() {
        return TisUtil.getString(kp_kindpay_name);
    }

    public String getFee() {
        return TisUtil.getString(kp_kindpay_fee);
    }

    public String getCode() {
        return TisUtil.getString(kp_sort_code);
    }

    public String getTime() {
        return TisUtil.getString(kp_create_time);
    }

    public String getCouponFee() {
        return TisUtil.getString(kp_coupon_fee);
    }
}
