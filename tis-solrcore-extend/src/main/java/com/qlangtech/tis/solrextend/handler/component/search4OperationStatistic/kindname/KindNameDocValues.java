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
class KindNameDocValues implements DocValuesInterface {

    public CommonFieldColumn km_kind_id;

    public CommonFieldColumn km_kind_name;

    public CommonFieldColumn km_sort_code;

    public CommonFieldColumn km_create_time;

    public CommonFieldColumn km_sale_num;

    public CommonFieldColumn km_fee;

    public CommonFieldColumn km_ratio_fee;

    public CommonFieldColumn km_gift_num;

    public CommonFieldColumn km_return_num;

    @Override
    public void doSetNextReader(LeafReaderContext context) {
        km_kind_id.doSetNextReader(context);
        km_kind_name.doSetNextReader(context);
        km_sort_code.doSetNextReader(context);
        km_create_time.doSetNextReader(context);
        km_sale_num.doSetNextReader(context);
        km_fee.doSetNextReader(context);
        km_ratio_fee.doSetNextReader(context);
        km_gift_num.doSetNextReader(context);
        km_return_num.doSetNextReader(context);
    }

    @Override
    public void fillValue(int doc) {
        km_kind_id.filler.fillValue(doc);
        km_kind_name.filler.fillValue(doc);
        km_sort_code.filler.fillValue(doc);
        km_create_time.filler.fillValue(doc);
        km_sale_num.filler.fillValue(doc);
        km_fee.filler.fillValue(doc);
        km_ratio_fee.filler.fillValue(doc);
        km_gift_num.filler.fillValue(doc);
        km_return_num.filler.fillValue(doc);
    }

    public String getKm_kind_id() {
        return TisUtil.getString(km_kind_id);
    }

    public String getKm_kind_name() {
        return TisUtil.getString(km_kind_name);
    }

    public String getKm_sort_code() {
        return TisUtil.getString(km_sort_code);
    }

    public String getKm_create_time() {
        return TisUtil.getString(km_create_time);
    }

    public String getKm_sale_num() {
        return TisUtil.getString(km_sale_num);
    }

    public String getKm_fee() {
        return TisUtil.getString(km_fee);
    }

    public String getKm_ratio_fee() {
        return TisUtil.getString(km_ratio_fee);
    }

    public String getKm_gift_num() {
        return TisUtil.getString(km_gift_num);
    }

    public String getKm_return_num() {
        return TisUtil.getString(km_return_num);
    }
}
