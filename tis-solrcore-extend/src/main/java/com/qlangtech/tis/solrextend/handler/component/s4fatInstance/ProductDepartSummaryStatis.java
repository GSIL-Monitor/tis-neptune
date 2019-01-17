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
import java.io.IOException;
import java.math.BigDecimal;

/*
 * Created by lingxiao on 2016/6/29.
 *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class ProductDepartSummaryStatis {

    public String wh_name;

    public TisMoney accountNum;

    public TisMoney ratioBeforeFee;

    public TisMoney ratioAfterFee;

    public String mwh_ratio;

    public ProductDepartSummaryStatis() {
    }

    public ProductDepartSummaryStatis(String wh_name) {
        super();
        this.wh_name = wh_name;
    }

    public ProductDepartSummaryStatis(String wh_name, TisMoney accountNum, TisMoney ratioBeforeFee, TisMoney ratioAfterFee, String mwh_ratio) {
        super();
        this.wh_name = wh_name;
        this.accountNum = accountNum;
        this.ratioBeforeFee = ratioBeforeFee;
        this.ratioAfterFee = ratioAfterFee;
        this.mwh_ratio = mwh_ratio;
    }

    // @Override
    public String write() throws IOException {
        return "accountNum:" + accountNum + ",feeColumn:" + ratioBeforeFee + ",ratioFee:" + ratioAfterFee;
    }

    public void addIncrease(ProductDepartDocValues productDepartDocValues) {
        // 1.5
        TisMoney account_num = productDepartDocValues.getAccountNumColumn();
        // 冷，热
        String wh_name = productDepartDocValues.getWarehouseNameColumn();
        // 30
        TisMoney in_fee = productDepartDocValues.getRatioBeforeFee();
        // 40
        TisMoney ratio_fee = productDepartDocValues.getRatioAfterFee();
        // 0.6,0.4
        String mwh_ratio = productDepartDocValues.getRatio();
    }

    public void addIncrease(ProductDepartSummaryStatis subStatic) {
        // 聚合操作
        BigDecimal percent = (new BigDecimal(subStatic.mwh_ratio)).divide(new BigDecimal(100));
        subStatic.ratioBeforeFee.addCoefficient(percent);
        this.ratioBeforeFee.add(subStatic.ratioBeforeFee);
        subStatic.ratioAfterFee.addCoefficient(percent);
        this.ratioAfterFee.add(subStatic.ratioAfterFee);
        subStatic.accountNum.addCoefficient(percent);
        this.accountNum.add(subStatic.accountNum);
    }
}
