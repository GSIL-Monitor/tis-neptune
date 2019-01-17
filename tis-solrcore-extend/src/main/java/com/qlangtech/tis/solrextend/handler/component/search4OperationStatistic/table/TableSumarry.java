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
package com.qlangtech.tis.solrextend.handler.component.search4OperationStatistic.table;

import com.qlangtech.tis.solrextend.handler.component.TisMoney;

/*
 * Created by lingxiao on 2016/7/26.
 *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class TableSumarry {

    public String sa_area_name;

    public String sa_sort_code;

    public TisMoney sa_order_num = TisMoney.create();

    public TisMoney sa_people_num = TisMoney.create();

    public TisMoney sa_source_amount = TisMoney.create();

    public TisMoney sa_discount_amount = TisMoney.create();

    public TisMoney sa_discount_fee = TisMoney.create();

    public TisMoney sa_profit = TisMoney.create();

    public TisMoney sa_receive_amount = TisMoney.create();

    public TisMoney sa_seat_num = TisMoney.create();

    public String sa_create_time;

    // 统计该区域桌位数量
    public int count;

    public TableSumarry() {
    }

    public TableSumarry(String sa_area_name) {
        this.sa_area_name = sa_area_name;
    }

    public TableSumarry(String sa_area_name, TisMoney sa_order_num, TisMoney sa_people_num, TisMoney sa_source_amount, TisMoney sa_discount_amount, TisMoney sa_discount_fee, TisMoney sa_profit, TisMoney sa_receive_amount, TisMoney sa_seat_num, int count, String sa_sort_code, String sa_create_time) {
        this.sa_area_name = sa_area_name;
        this.sa_order_num = sa_order_num;
        this.sa_people_num = sa_people_num;
        this.sa_source_amount = sa_source_amount;
        this.sa_discount_amount = sa_discount_amount;
        this.sa_discount_fee = sa_discount_fee;
        this.sa_profit = sa_profit;
        this.sa_receive_amount = sa_receive_amount;
        this.sa_seat_num = sa_seat_num;
        this.count = count;
        this.sa_sort_code = sa_sort_code;
        this.sa_create_time = sa_create_time;
    }

    public TableSumarry(String sa_area_name, TisMoney sa_order_num, TisMoney sa_people_num, TisMoney sa_source_amount, TisMoney sa_discount_amount, TisMoney sa_discount_fee, TisMoney sa_profit, TisMoney sa_receive_amount, TisMoney sa_seat_num, String sa_sort_code, String sa_create_time) {
        this.sa_area_name = sa_area_name;
        this.sa_order_num = sa_order_num;
        this.sa_people_num = sa_people_num;
        this.sa_source_amount = sa_source_amount;
        this.sa_discount_amount = sa_discount_amount;
        this.sa_discount_fee = sa_discount_fee;
        this.sa_profit = sa_profit;
        this.sa_receive_amount = sa_receive_amount;
        this.sa_seat_num = sa_seat_num;
        this.sa_sort_code = sa_sort_code;
        this.sa_create_time = sa_create_time;
    }

    public void addIncrease(TableSumarry ts) {
        this.sa_order_num.add(ts.sa_order_num);
        this.sa_people_num.add(ts.sa_people_num);
        this.sa_source_amount.add(ts.sa_source_amount);
        this.sa_discount_amount.add(ts.sa_discount_amount);
        this.sa_discount_fee.add(ts.sa_discount_fee);
        this.sa_profit.add(ts.sa_profit);
        this.sa_receive_amount.add(ts.sa_receive_amount);
        this.sa_seat_num.add(ts.sa_seat_num);
    }
}
