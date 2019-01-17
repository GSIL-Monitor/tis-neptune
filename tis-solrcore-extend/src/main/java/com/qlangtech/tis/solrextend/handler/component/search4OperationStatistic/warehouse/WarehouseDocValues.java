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

import com.qlangtech.tis.solrextend.handler.component.search4OperationStatistic.CommonFieldColumn;
import com.qlangtech.tis.solrextend.handler.component.search4OperationStatistic.DocValuesInterface;
import com.qlangtech.tis.solrextend.handler.component.search4OperationStatistic.TisUtil;
import org.apache.lucene.index.LeafReaderContext;

/*
 * Created by lingxiao on 2016/7/25.
 *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class WarehouseDocValues implements DocValuesInterface {

    public CommonFieldColumn wh_warehouse_id;

    public CommonFieldColumn wh_warehouse_name;

    public CommonFieldColumn wh_num;

    public CommonFieldColumn wh_fee;

    public CommonFieldColumn wh_ratio_fee;

    public CommonFieldColumn wh_discount_fee;

    @Override
    public void doSetNextReader(LeafReaderContext context) {
        wh_warehouse_id.doSetNextReader(context);
        wh_warehouse_name.doSetNextReader(context);
        wh_num.doSetNextReader(context);
        wh_fee.doSetNextReader(context);
        wh_ratio_fee.doSetNextReader(context);
        wh_discount_fee.doSetNextReader(context);
    }

    @Override
    public void fillValue(int doc) {
        wh_warehouse_id.filler.fillValue(doc);
        wh_warehouse_name.filler.fillValue(doc);
        wh_num.filler.fillValue(doc);
        wh_fee.filler.fillValue(doc);
        wh_ratio_fee.filler.fillValue(doc);
        wh_discount_fee.filler.fillValue(doc);
    }

    public String getId() {
        return TisUtil.getString(wh_warehouse_id);
    }

    public String getName() {
        return TisUtil.getString(wh_warehouse_name);
    }

    public String getNum() {
        return TisUtil.getString(wh_num);
    }

    public String getFee() {
        return TisUtil.getString(wh_fee);
    }

    public String getRatiofee() {
        return TisUtil.getString(wh_ratio_fee);
    }

    public String getDiscountfee() {
        return TisUtil.getString(wh_discount_fee);
    }
}
