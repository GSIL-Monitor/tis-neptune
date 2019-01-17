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
package com.qlangtech.tis.solrextend.handler.component.search4OperationStatistic;

import com.qlangtech.tis.solrextend.handler.component.TisMoney;

/*
 * Created by lingxiao on 2016/7/21.
 *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class TotalPayStatis {

    public TisMoney source_amount = TisMoney.create("0");

    public TisMoney discount_fee = TisMoney.create("0");

    public TisMoney profit = TisMoney.create("0");

    public TisMoney not_include = TisMoney.create("0");

    public TisMoney coupon_discount = TisMoney.create("0");

    public TisMoney receive_amount_real = TisMoney.create("0");

    public TisMoney seat_order_amount = TisMoney.create("0");

    public int order_num;

    public int people_num;

    public int seat_num;

    public int seat_order_num;

    // 最后留作求平均值用
    public int count;

    // 报表五需要的字段
    public TisMoney discount_amount = TisMoney.create("0");

    public TisMoney receive_amount = TisMoney.create("0");

    public TotalPayStatis() {
    }

    public void addIncrease(ToltalPayDocValues docValues) {
        this.source_amount.add(docValues.getSource_amount());
        this.discount_fee.add(docValues.getDiscount_fee());
        this.profit.add(docValues.getProfit());
        this.not_include.add(docValues.getNot_include());
        this.coupon_discount.add(docValues.getCoupon_discount());
        this.receive_amount_real.add(docValues.getrRceive_amount_real());
        this.seat_order_amount.add(docValues.getSeat_order_amount());
        this.order_num += docValues.getOrder_num();
        this.people_num += docValues.getPeople_num();
        this.seat_num += docValues.getSeat_num();
        this.seat_order_num += docValues.getSeat_ordr_num();
        this.discount_amount.add(docValues.getDiscount_amount());
        this.receive_amount.add(docValues.getSeceive_amount());
    }
}
