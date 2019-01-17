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

import com.qlangtech.tis.solrextend.handler.component.TisMoney;

/*
 * Created by lingxiao on 2016/7/27.
 *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class ReturnMenuSumary {

    public String concatNameUnits;

    public String rm_price;

    public TisMoney rm_num;

    public TisMoney rm_fee;

    public ReturnMenuSumary() {
    }

    public ReturnMenuSumary(String concatNameUnits) {
        this.concatNameUnits = concatNameUnits;
    }

    public ReturnMenuSumary(String concatNameUnits, String rm_price, TisMoney rm_num, TisMoney rm_fee) {
        this.rm_price = rm_price;
        this.rm_num = rm_num;
        this.rm_fee = rm_fee;
        this.concatNameUnits = concatNameUnits;
    }

    public void addIncrease(ReturnMenuSumary giftMenuSummary) {
        this.rm_num.add(giftMenuSummary.rm_num);
        this.rm_fee.add(giftMenuSummary.rm_fee);
    }
}
