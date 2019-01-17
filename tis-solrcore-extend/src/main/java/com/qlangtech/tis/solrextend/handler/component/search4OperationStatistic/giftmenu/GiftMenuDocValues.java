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
package com.qlangtech.tis.solrextend.handler.component.search4OperationStatistic.giftmenu;

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
public class GiftMenuDocValues implements DocValuesInterface {

    public CommonFieldColumn gm_menu_id;

    public CommonFieldColumn gm_menu_name;

    public CommonFieldColumn gm_kind_id;

    public CommonFieldColumn gm_kind_name;

    public CommonFieldColumn gm_unit;

    public CommonFieldColumn gm_price;

    public CommonFieldColumn gm_num;

    public CommonFieldColumn gm_fee;

    @Override
    public void doSetNextReader(LeafReaderContext context) {
        gm_menu_id.doSetNextReader(context);
        gm_menu_name.doSetNextReader(context);
        gm_kind_id.doSetNextReader(context);
        gm_kind_name.doSetNextReader(context);
        gm_unit.doSetNextReader(context);
        gm_price.doSetNextReader(context);
        gm_num.doSetNextReader(context);
        gm_fee.doSetNextReader(context);
    }

    @Override
    public void fillValue(int doc) {
        gm_menu_id.filler.fillValue(doc);
        gm_menu_name.filler.fillValue(doc);
        gm_kind_id.filler.fillValue(doc);
        gm_kind_name.filler.fillValue(doc);
        gm_unit.filler.fillValue(doc);
        gm_price.filler.fillValue(doc);
        gm_num.filler.fillValue(doc);
        gm_fee.filler.fillValue(doc);
    }

    public String getGm_fee() {
        return TisUtil.getString(gm_fee);
    }

    public String getGm_menu_id() {
        return TisUtil.getString(gm_menu_id);
    }

    public String getGm_menu_name() {
        return TisUtil.getString(gm_menu_name);
    }

    public String getGm_kind_id() {
        return TisUtil.getString(gm_kind_id);
    }

    public String getGm_kind_name() {
        return TisUtil.getString(gm_kind_name);
    }

    public String getGm_unit() {
        return TisUtil.getString(gm_unit);
    }

    public String getGm_price() {
        return TisUtil.getString(gm_price);
    }

    public String getGm_num() {
        return TisUtil.getString(gm_num);
    }
}
