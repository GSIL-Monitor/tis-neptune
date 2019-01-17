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
package com.qlangtech.tis.solrextend.handler.component.search4OperationStatistic.discount;

import com.qlangtech.tis.solrextend.handler.component.TisMoney;

/*
 * Created by lingxiao on 2016/7/26.
 *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class DiscountSumarry {

    public String dc_discount_name;

    public TisMoney dc_discount_num = TisMoney.create();

    public TisMoney dc_discount_fee = TisMoney.create();

    public DiscountSumarry() {
    }

    public DiscountSumarry(String dc_discount_name) {
        this.dc_discount_name = dc_discount_name;
    }

    public DiscountSumarry(String dc_discount_name, TisMoney dc_discount_num, TisMoney dc_discount_fee) {
        this.dc_discount_name = dc_discount_name;
        this.dc_discount_num = dc_discount_num;
        this.dc_discount_fee = dc_discount_fee;
    }

    public void addIncrease(DiscountSumarry sumarry) {
        this.dc_discount_num.add(sumarry.dc_discount_num);
        this.dc_discount_fee.add(sumarry.dc_discount_fee);
    }
}
