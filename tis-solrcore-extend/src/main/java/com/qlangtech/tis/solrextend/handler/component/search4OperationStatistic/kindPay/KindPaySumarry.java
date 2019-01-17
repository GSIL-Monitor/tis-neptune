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

import com.qlangtech.tis.solrextend.handler.component.TisMoney;

/*
 * Created by lingxiao on 2016/7/25.
 *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class KindPaySumarry {

    public String kp_kindpay_name;

    public TisMoney kp_kindpay_fee;

    public TisMoney kp_coupon_fee;

    public String kp_sort_code;

    public String kp_create_time;

    public KindPaySumarry(String kp_kindpay_name) {
        this.kp_kindpay_name = kp_kindpay_name;
    }

    public KindPaySumarry(String kp_kindpay_name, TisMoney kp_kindpay_fee, TisMoney kp_coupon_fee, String kp_sort_code, String kp_create_time) {
        this.kp_kindpay_name = kp_kindpay_name;
        this.kp_kindpay_fee = kp_kindpay_fee;
        this.kp_coupon_fee = kp_coupon_fee;
        this.kp_sort_code = kp_sort_code;
        this.kp_create_time = kp_create_time;
    }

    public KindPaySumarry() {
    }

    public void addIncrease(KindPaySumarry kps) {
        this.kp_kindpay_fee.add(kps.kp_kindpay_fee);
        this.kp_coupon_fee.add(kps.kp_coupon_fee);
    }

    public void addIncrease(KindpayDocValues docValues) {
    }
}
