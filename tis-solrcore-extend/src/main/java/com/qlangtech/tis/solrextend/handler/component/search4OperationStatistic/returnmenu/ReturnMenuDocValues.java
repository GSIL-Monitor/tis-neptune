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

import com.qlangtech.tis.solrextend.handler.component.search4OperationStatistic.CommonFieldColumn;
import com.qlangtech.tis.solrextend.handler.component.search4OperationStatistic.DocValuesInterface;
import com.qlangtech.tis.solrextend.handler.component.search4OperationStatistic.TisUtil;
import org.apache.lucene.index.LeafReaderContext;

/*
 * Created by lingxiao on 2016/7/27.
 *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class ReturnMenuDocValues implements DocValuesInterface {

    public CommonFieldColumn rm_menu_id;

    public CommonFieldColumn rm_menu_name;

    public CommonFieldColumn rm_kind_id;

    public CommonFieldColumn rm_kind_name;

    public CommonFieldColumn rm_unit;

    public CommonFieldColumn rm_price;

    public CommonFieldColumn rm_num;

    public CommonFieldColumn rm_fee;

    @Override
    public void doSetNextReader(LeafReaderContext context) {
        rm_menu_id.doSetNextReader(context);
        rm_menu_name.doSetNextReader(context);
        rm_kind_id.doSetNextReader(context);
        rm_kind_name.doSetNextReader(context);
        rm_unit.doSetNextReader(context);
        rm_price.doSetNextReader(context);
        rm_num.doSetNextReader(context);
        rm_fee.doSetNextReader(context);
    }

    @Override
    public void fillValue(int doc) {
        rm_menu_id.filler.fillValue(doc);
        rm_menu_name.filler.fillValue(doc);
        rm_kind_id.filler.fillValue(doc);
        rm_kind_name.filler.fillValue(doc);
        rm_unit.filler.fillValue(doc);
        rm_price.filler.fillValue(doc);
        rm_num.filler.fillValue(doc);
        rm_fee.filler.fillValue(doc);
    }

    public String getRm_fee() {
        return TisUtil.getString(rm_fee);
    }

    public String getRm_menu_id() {
        return TisUtil.getString(rm_menu_id);
    }

    public String getRm_menu_name() {
        return TisUtil.getString(rm_menu_name);
    }

    public String getRm_kind_id() {
        return TisUtil.getString(rm_kind_id);
    }

    public String getRm_kind_name() {
        return TisUtil.getString(rm_kind_name);
    }

    public String getRm_unit() {
        return TisUtil.getString(rm_unit);
    }

    public String getRm_price() {
        return TisUtil.getString(rm_price);
    }

    public String getRm_num() {
        return TisUtil.getString(rm_num);
    }
}
