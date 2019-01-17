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
package com.qlangtech.tis.solrextend.handler.component.s4TimeStatistic;

import com.qlangtech.tis.solrextend.handler.component.TisMoney;
import org.apache.lucene.index.LeafReaderContext;

/*
 * Created by lingxiao on 2016/7/19.
 *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class TimeIntervalStatisDocValues {

    public TimeStatisFieldColumn hour;

    public TimeStatisFieldColumn openOrderCount;

    public TimeStatisFieldColumn endOrderCount;

    public TimeStatisFieldColumn openOrderPeople;

    public TimeStatisFieldColumn endOrderPeople;

    public TimeStatisFieldColumn recieveAmount;

    public TimeStatisFieldColumn discountAmount;

    public TimeStatisFieldColumn profit;

    public TimeStatisFieldColumn invoice;

    void doSetNextReader(LeafReaderContext context) {
        hour.doSetNextReader(context);
        openOrderCount.doSetNextReader(context);
        endOrderCount.doSetNextReader(context);
        openOrderPeople.doSetNextReader(context);
        endOrderPeople.doSetNextReader(context);
        recieveAmount.doSetNextReader(context);
        discountAmount.doSetNextReader(context);
        profit.doSetNextReader(context);
        invoice.doSetNextReader(context);
    }

    void fillValue(int doc) {
        hour.filler.fillValue(doc);
        openOrderCount.filler.fillValue(doc);
        endOrderCount.filler.fillValue(doc);
        openOrderPeople.filler.fillValue(doc);
        endOrderPeople.filler.fillValue(doc);
        recieveAmount.filler.fillValue(doc);
        discountAmount.filler.fillValue(doc);
        profit.filler.fillValue(doc);
        invoice.filler.fillValue(doc);
    }

    public String getHour() {
        return getString(hour);
    }

    public int getOpenOrderCount() {
        return getIntValue(openOrderCount);
    }

    public int getEndOrderCount() {
        return getIntValue(endOrderCount);
    }

    public int getOpenOrderPeople() {
        return getIntValue(openOrderPeople);
    }

    public int getEndOrderPeople() {
        return getIntValue(endOrderPeople);
    }

    public TisMoney getRecieveAmout() {
        return getFloatValue(recieveAmount);
    }

    public TisMoney getDiscountAmout() {
        return getFloatValue(discountAmount);
    }

    public TisMoney getProfit() {
        return getFloatValue(profit);
    }

    public TisMoney getInvoice() {
        return getFloatValue(invoice);
    }

    private String getString(TimeStatisFieldColumn field) {
        if (!field.val.exists) {
            return null;
        }
        return field.val.toString();
    }

    private int getIntValue(TimeStatisFieldColumn field) {
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

    private TisMoney getFloatValue(TimeStatisFieldColumn field) {
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
