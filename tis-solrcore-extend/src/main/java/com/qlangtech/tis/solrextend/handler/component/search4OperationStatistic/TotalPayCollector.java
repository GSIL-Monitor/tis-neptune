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
package com.qlangtech.tis.solrextend.handler.component.search4OperationStatistic;

import com.alibaba.fastjson.JSONObject;
import com.qlangtech.tis.solrextend.handler.component.TisMoney;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.search.SimpleCollector;
import java.io.IOException;
import java.math.BigDecimal;

/*
 * Created by lingxiao on 2016/7/21.
 *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class TotalPayCollector extends SimpleCollector {

    public TotalPayStatis totalPayStatis;

    public ToltalPayDocValues docValues;

    public TotalPayCollector(ToltalPayDocValues docValues) {
        this.docValues = docValues;
        this.totalPayStatis = new TotalPayStatis();
    }

    @Override
    protected void doSetNextReader(LeafReaderContext context) {
        docValues.doSetNextReader(context);
    }

    @Override
    public boolean needsScores() {
        return false;
    }

    @Override
    public void collect(int doc) throws IOException {
        docValues.fillValue(doc);
        totalPayStatis.addIncrease(docValues);
    }

    public String write(TotalPayStatis totalPayStatis, int num) {
        JSONObject o = new JSONObject();
        o.put("sourceAmount", totalPayStatis.source_amount.toString());
        o.put("discountFee", totalPayStatis.discount_fee.toString());
        o.put("profit", totalPayStatis.profit.toString());
        o.put("notInclude", totalPayStatis.not_include.toString());
        o.put("couponDiscount", totalPayStatis.coupon_discount.toString());
        o.put("receiveAmountReal", totalPayStatis.receive_amount_real.toString());
        o.put("orderNum", totalPayStatis.order_num);
        o.put("peopleNum", totalPayStatis.people_num);
        o.put("discountAmount", totalPayStatis.discount_amount.toString());
        o.put("receiveAmount", totalPayStatis.receive_amount.toString());
        TisMoney receive_amount_real = totalPayStatis.receive_amount_real;
        TisMoney seat_order_amount = totalPayStatis.seat_order_amount;
        TisMoney receive_amount = totalPayStatis.receive_amount;
        int order_num = totalPayStatis.order_num;
        int people_num = totalPayStatis.people_num;
        int seat_num = totalPayStatis.seat_num;
        int seat_order_num = totalPayStatis.seat_order_num;
        System.out.println("order_num:" + order_num + "seat_order_num:" + seat_order_num);
        TisMoney singleTableConsume = TisUtil.devide(seat_order_amount, seat_order_num);
        o.put("singleAvgConsume", TisUtil.devide(receive_amount, order_num).toString());
        o.put("singleTableConsume", singleTableConsume.toString());
        o.put("singlePeopleConsume", TisUtil.devide(receive_amount, people_num).toString());
        BigDecimal turnTableRate = new BigDecimal(seat_order_num);
        BigDecimal bseat_num = new BigDecimal(seat_num);
        // 查询记录条数
        BigDecimal bnum = new BigDecimal(num);
        // 平均桌位数
        BigDecimal avseat = bseat_num.divide(bnum, 7, BigDecimal.ROUND_HALF_UP);
        turnTableRate = turnTableRate.divide(avseat, 7, BigDecimal.ROUND_HALF_UP);
        // turnTableRate = turnTableRate.multiply(new BigDecimal(100));//转化为百分数
        // 格式化，取翻桌率两位小数
        BigDecimal t = turnTableRate.setScale(7, BigDecimal.ROUND_HALF_UP);
        o.put("turnTableRate", t.toString());
        o.put("singlePeopleConsume1", TisUtil.devide(receive_amount, people_num).toString());
        o.put("singleAvgConsume1", TisUtil.devide(receive_amount, order_num).toString());
        return o.toJSONString();
    }
}
