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
import org.apache.lucene.index.LeafReaderContext;

/*
 * Created by lingxiao on 2016/7/21.
 *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class ToltalPayDocValues {

    public CommonFieldColumn source_amount;

    public CommonFieldColumn discount_fee;

    public CommonFieldColumn profit;

    public CommonFieldColumn not_include;

    public CommonFieldColumn coupon_discount;

    public CommonFieldColumn receive_amount_real;

    public CommonFieldColumn seat_order_amount;

    public CommonFieldColumn order_num;

    public CommonFieldColumn people_num;

    public CommonFieldColumn seat_num;

    public CommonFieldColumn seat_order_num;

    // 报表五需要的字段
    public CommonFieldColumn discount_amount;

    public CommonFieldColumn receive_amount;

    void doSetNextReader(LeafReaderContext context) {
        source_amount.doSetNextReader(context);
        discount_fee.doSetNextReader(context);
        profit.doSetNextReader(context);
        not_include.doSetNextReader(context);
        coupon_discount.doSetNextReader(context);
        receive_amount_real.doSetNextReader(context);
        seat_order_amount.doSetNextReader(context);
        order_num.doSetNextReader(context);
        people_num.doSetNextReader(context);
        seat_num.doSetNextReader(context);
        seat_order_num.doSetNextReader(context);
        discount_amount.doSetNextReader(context);
        receive_amount.doSetNextReader(context);
    }

    void fillValue(int doc) {
        source_amount.filler.fillValue(doc);
        discount_fee.filler.fillValue(doc);
        profit.filler.fillValue(doc);
        not_include.filler.fillValue(doc);
        coupon_discount.filler.fillValue(doc);
        receive_amount_real.filler.fillValue(doc);
        seat_order_amount.filler.fillValue(doc);
        order_num.filler.fillValue(doc);
        people_num.filler.fillValue(doc);
        seat_num.filler.fillValue(doc);
        seat_order_num.filler.fillValue(doc);
        discount_amount.filler.fillValue(doc);
        receive_amount.filler.fillValue(doc);
    }

    public TisMoney getSource_amount() {
        return getFloatValue(source_amount);
    }

    public TisMoney getDiscount_fee() {
        return getFloatValue(discount_fee);
    }

    public TisMoney getProfit() {
        return getFloatValue(profit);
    }

    public TisMoney getCoupon_discount() {
        return getFloatValue(coupon_discount);
    }

    public TisMoney getNot_include() {
        return getFloatValue(not_include);
    }

    public TisMoney getrRceive_amount_real() {
        return getFloatValue(receive_amount_real);
    }

    public TisMoney getDiscount_amount() {
        return getFloatValue(discount_amount);
    }

    public TisMoney getSeceive_amount() {
        return getFloatValue(receive_amount);
    }

    public TisMoney getSeat_order_amount() {
        return getFloatValue(seat_order_amount);
    }

    public int getOrder_num() {
        return getIntValue(order_num);
    }

    public int getPeople_num() {
        return getIntValue(people_num);
    }

    public int getSeat_num() {
        return getIntValue(seat_num);
    }

    public int getSeat_ordr_num() {
        return getIntValue(seat_order_num);
    }

    private String getString(CommonFieldColumn field) {
        if (!field.val.exists) {
            return null;
        }
        return field.val.toString();
    }

    private int getIntValue(CommonFieldColumn field) {
        if (!field.val.exists) {
            return 0;
        }
        try {
            // new
            return Integer.parseInt(field.val.toString());
        } catch (Throwable e) {
            return 0;
        }
    }

    private TisMoney getFloatValue(CommonFieldColumn field) {
        if (!field.val.exists) {
            return TisMoney.create();
        }
        try {
            // new
            return TisMoney.create(field.val.toString());
        } catch (Throwable e) {
            return TisMoney.create();
        }
    }
}
