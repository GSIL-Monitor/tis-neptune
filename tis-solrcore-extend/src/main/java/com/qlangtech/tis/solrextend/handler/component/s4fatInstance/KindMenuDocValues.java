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
package com.qlangtech.tis.solrextend.handler.component.s4fatInstance;

import com.qlangtech.tis.solrextend.handler.component.TisMoney;
import org.apache.lucene.index.LeafReaderContext;

/*
 * Created by lingxiao on 2016/6/24.
 *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class KindMenuDocValues {

    // 菜类id
    public FieldColumn group_or_kind_id;

    // 菜类名称
    public FieldColumn group_or_kind_name;

    // 销售数量
    public FieldColumn accountNumColumn;

    // 折前金额
    public FieldColumn feeColumn;

    // 折后金额
    public FieldColumn ratiofeeColumn;

    void doSetNextReader(LeafReaderContext context) {
        group_or_kind_id.doSetNextReader(context);
        accountNumColumn.doSetNextReader(context);
        feeColumn.doSetNextReader(context);
        ratiofeeColumn.doSetNextReader(context);
        group_or_kind_name.doSetNextReader(context);
    }

    void fillValue(int doc) {
        group_or_kind_id.filler.fillValue(doc);
        accountNumColumn.filler.fillValue(doc);
        feeColumn.filler.fillValue(doc);
        ratiofeeColumn.filler.fillValue(doc);
        group_or_kind_name.filler.fillValue(doc);
    }

    public String getKindMenu() {
        return this.getString(this.group_or_kind_id);
    }

    public String getKindMenuName() {
        return this.getString(this.group_or_kind_name);
    }

    public TisMoney getAccountNum() {
        return getFloatValue(this.accountNumColumn);
    }

    public TisMoney getFee() {
        return getFloatValue(this.feeColumn);
    }

    public TisMoney getRatioFee() {
        return getFloatValue(this.ratiofeeColumn);
    }

    private String getString(FieldColumn field) {
        if (!field.val.exists) {
            return null;
        }
        return field.val.toString();
    }

    private int geIntValue(FieldColumn field) {
        if (!field.val.exists) {
            return 0;
        }
        try {
            // new
            return Integer.parseInt(field.val.toString());
        } catch (Throwable e) {
            return 0;
        }
    }

    private TisMoney getFloatValue(FieldColumn field) {
        if (!field.val.exists) {
            return TisMoney.create();
        }
        try {
            return TisMoney.create(field.val.toString());
        } catch (Throwable e) {
            return TisMoney.create();
        }
    }
}
