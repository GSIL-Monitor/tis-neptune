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

/*
 * Created by lingxiao on 2016/6/24.
 *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class KindMenuSummaryStatis {

    // 菜类名称
    public String group_or_kind_id;

    public String group_or_kind_name;

    // 折前金额
    public TisMoney feeColumn = TisMoney.create("0");

    // 折后金额
    public TisMoney ratioFee = TisMoney.create("0");

    // 销售数量
    public TisMoney accountNum = TisMoney.create("0");

    public KindMenuSummaryStatis() {
    }

    /**
     * @param group_or_kind_name
     */
    public KindMenuSummaryStatis(String group_or_kind_name) {
        super();
        this.group_or_kind_name = group_or_kind_name;
    }

    // @Override
    public String write() throws IOException {
        return "group_or_kind_name" + group_or_kind_name + "accountNum:" + accountNum + ",feeColumn:" + feeColumn + ",ratioFee:" + ratioFee;
    }

    public void addIncrease(KindMenuDocValues val) {
        this.feeColumn.add(val.getFee());
        this.ratioFee.add(val.getFee());
        this.accountNum.add(val.getAccountNum());
    }
}
