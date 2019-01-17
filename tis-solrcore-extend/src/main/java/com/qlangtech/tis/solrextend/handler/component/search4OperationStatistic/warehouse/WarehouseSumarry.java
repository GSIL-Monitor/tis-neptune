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
package com.qlangtech.tis.solrextend.handler.component.search4OperationStatistic.warehouse;

import com.qlangtech.tis.solrextend.handler.component.TisMoney;

/*
 * Created by lingxiao on 2016/7/25.
 *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class WarehouseSumarry {

    public String wh_warehouse_name;

    public TisMoney wh_num = TisMoney.create();

    public TisMoney wh_fee = TisMoney.create();

    public TisMoney wh_ratio_fee = TisMoney.create();

    public TisMoney wh_discount_fee = TisMoney.create();

    public WarehouseSumarry(String wh_warehouse_name) {
        this.wh_warehouse_name = wh_warehouse_name;
    }

    public WarehouseSumarry(String wh_warehouse_name, TisMoney wh_num, TisMoney wh_fee, TisMoney wh_ratio_fee, TisMoney wh_discount_fee) {
        this.wh_warehouse_name = wh_warehouse_name;
        this.wh_num = wh_num;
        this.wh_fee = wh_fee;
        this.wh_ratio_fee = wh_ratio_fee;
        this.wh_discount_fee = wh_discount_fee;
    }

    public WarehouseSumarry() {
    }

    public void addIncrease(WarehouseSumarry whs) {
        this.wh_num.add(whs.wh_num);
        this.wh_fee.add(whs.wh_fee);
        this.wh_ratio_fee.add(whs.wh_ratio_fee);
        this.wh_discount_fee.add(whs.wh_discount_fee);
    }
}
