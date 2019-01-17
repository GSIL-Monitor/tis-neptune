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
 * Created by Administrator on 2016/6/25.
 *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class ProductDepartDocValues {

    public FieldColumn warehouseName;

    public FieldColumn accountNum;

    public FieldColumn ratioBeforeFee;

    public FieldColumn ratioAfterFee;

    public FieldColumn mwh_ratio;

    void doSetNextReader(LeafReaderContext context) {
        warehouseName.doSetNextReader(context);
        accountNum.doSetNextReader(context);
        ratioBeforeFee.doSetNextReader(context);
        ratioAfterFee.doSetNextReader(context);
        mwh_ratio.doSetNextReader(context);
    }

    void fillValue(int doc) {
        warehouseName.filler.fillValue(doc);
        accountNum.filler.fillValue(doc);
        ratioBeforeFee.filler.fillValue(doc);
        ratioAfterFee.filler.fillValue(doc);
        mwh_ratio.filler.fillValue(doc);
    }

    public String getWarehouseNameColumn() {
        return this.getString(this.warehouseName);
    }

    public TisMoney getRatioBeforeFee() {
        return getFloatValue(this.ratioBeforeFee);
    }

    public TisMoney getRatioAfterFee() {
        return getFloatValue(this.ratioAfterFee);
    }

    public TisMoney getAccountNumColumn() {
        return getFloatValue(this.accountNum);
    }

    public String getRatio() {
        return getString(this.mwh_ratio);
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

    /**
     * @return
     */
    private TisMoney getFloatValue(FieldColumn field) {
        if (!field.val.exists) {
            return TisMoney.create();
        }
        try {
            // new
            return TisMoney.create(field.val.toString());
        } catch (Throwable e) {
            return TisMoney.create();
        }
    }
}
