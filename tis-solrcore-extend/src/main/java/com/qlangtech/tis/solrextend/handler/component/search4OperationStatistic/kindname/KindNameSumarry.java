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

import com.qlangtech.tis.solrextend.handler.component.TisMoney;

/*
 * Created by lingxiao on 2016/7/26.
 *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class KindNameSumarry {

    public String km_kind_name;

    public String km_sort_code;

    public String km_create_time;

    public TisMoney km_sale_num = TisMoney.create();

    public TisMoney km_fee = TisMoney.create();

    public TisMoney km_ratio_fee = TisMoney.create();

    public TisMoney km_gift_num = TisMoney.create();

    public TisMoney km_return_num = TisMoney.create();

    public KindNameSumarry() {
    }

    public KindNameSumarry(String km_kind_name) {
        this.km_kind_name = km_kind_name;
    }

    public KindNameSumarry(String km_kind_name, TisMoney km_sale_num, TisMoney km_fee, TisMoney km_ratio_fee, TisMoney km_gift_num, TisMoney km_return_num, String km_sort_code, String km_create_time) {
        this.km_kind_name = km_kind_name;
        this.km_sale_num = km_sale_num;
        this.km_fee = km_fee;
        this.km_ratio_fee = km_ratio_fee;
        this.km_gift_num = km_gift_num;
        this.km_return_num = km_return_num;
        this.km_create_time = km_create_time;
        this.km_sort_code = km_sort_code;
    }

    public void addIncrease(KindNameSumarry kns) {
        this.km_sale_num.add(kns.km_sale_num);
        this.km_fee.add(kns.km_fee);
        this.km_ratio_fee.add(kns.km_ratio_fee);
        this.km_gift_num.add(kns.km_gift_num);
        this.km_return_num.add(kns.km_return_num);
    }
}
