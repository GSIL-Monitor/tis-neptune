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

import com.qlangtech.tis.solrextend.handler.component.search4OperationStatistic.CommonFieldColumn;
import com.qlangtech.tis.solrextend.handler.component.search4OperationStatistic.DocValuesInterface;
import com.qlangtech.tis.solrextend.handler.component.search4OperationStatistic.TisUtil;
import org.apache.lucene.index.LeafReaderContext;

/*
 * Created by lingxiao on 2016/7/26.
 *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class TableDocValues implements DocValuesInterface {

    public CommonFieldColumn sa_area_id;

    public CommonFieldColumn sa_area_name;

    public CommonFieldColumn sa_sort_code;

    public CommonFieldColumn sa_order_num;

    public CommonFieldColumn sa_people_num;

    public CommonFieldColumn sa_source_amount;

    public CommonFieldColumn sa_discount_amount;

    public CommonFieldColumn sa_discount_fee;

    public CommonFieldColumn sa_profit;

    public CommonFieldColumn sa_receive_amount;

    public CommonFieldColumn sa_seat_num;

    public CommonFieldColumn sa_create_time;

    @Override
    public void doSetNextReader(LeafReaderContext context) {
        sa_area_id.doSetNextReader(context);
        sa_area_name.doSetNextReader(context);
        sa_sort_code.doSetNextReader(context);
        sa_order_num.doSetNextReader(context);
        sa_people_num.doSetNextReader(context);
        sa_source_amount.doSetNextReader(context);
        sa_discount_amount.doSetNextReader(context);
        sa_discount_fee.doSetNextReader(context);
        sa_profit.doSetNextReader(context);
        sa_receive_amount.doSetNextReader(context);
        sa_seat_num.doSetNextReader(context);
        sa_create_time.doSetNextReader(context);
    }

    @Override
    public void fillValue(int doc) {
        sa_area_id.filler.fillValue(doc);
        sa_area_name.filler.fillValue(doc);
        sa_sort_code.filler.fillValue(doc);
        sa_order_num.filler.fillValue(doc);
        sa_people_num.filler.fillValue(doc);
        sa_source_amount.filler.fillValue(doc);
        sa_discount_amount.filler.fillValue(doc);
        sa_discount_fee.filler.fillValue(doc);
        sa_profit.filler.fillValue(doc);
        sa_receive_amount.filler.fillValue(doc);
        sa_seat_num.filler.fillValue(doc);
        sa_create_time.filler.fillValue(doc);
    }

    public String getSa_area_id() {
        return TisUtil.getString(sa_area_id);
    }

    public String getSa_area_name() {
        return TisUtil.getString(sa_area_name);
    }

    public String getSa_sort_code() {
        return TisUtil.getString(sa_sort_code);
    }

    public String getSa_order_num() {
        return TisUtil.getString(sa_order_num);
    }

    public String getSa_people_num() {
        return TisUtil.getString(sa_people_num);
    }

    public String getSa_source_amount() {
        return TisUtil.getString(sa_source_amount);
    }

    public String getSa_discount_amount() {
        return TisUtil.getString(sa_discount_amount);
    }

    public String getSa_discount_fee() {
        return TisUtil.getString(sa_discount_fee);
    }

    public String getSa_profit() {
        return TisUtil.getString(sa_profit);
    }

    public String getSa_receive_amount() {
        return TisUtil.getString(sa_receive_amount);
    }

    public String getSa_seat_num() {
        return TisUtil.getString(sa_seat_num);
    }

    public String getSa_create_time() {
        return TisUtil.getString(sa_create_time);
    }
}
