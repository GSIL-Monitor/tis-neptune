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

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.qlangtech.tis.solrextend.handler.component.TisMoney;
import com.qlangtech.tis.solrextend.handler.component.search4OperationStatistic.TisUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.search.SimpleCollector;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * Created by lingxiao on 2016/7/26.
 *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class TableCollector extends SimpleCollector {

    public TableDocValues docValues;

    public TableSumarry tableSumarry;

    public HashMap<String, TableSumarry> tableSumarryMap;

    public TableCollector(TableDocValues docValues) {
        this.docValues = docValues;
        this.tableSumarry = new TableSumarry();
        this.tableSumarryMap = new HashMap<>();
    }

    @Override
    public void doSetNextReader(LeafReaderContext context) {
        docValues.doSetNextReader(context);
    }

    @Override
    public boolean needsScores() {
        return false;
    }

    @Override
    public void collect(int doc) throws IOException {
        docValues.fillValue(doc);
        String sa_area_id = docValues.getSa_area_id();
        String sa_area_name = docValues.getSa_area_name();
        String sa_sort_code = docValues.getSa_sort_code();
        String sa_order_num = docValues.getSa_order_num();
        String sa_people_num = docValues.getSa_people_num();
        String sa_source_amount = docValues.getSa_source_amount();
        String sa_discount_amount = docValues.getSa_discount_amount();
        String sa_discount_fee = docValues.getSa_discount_fee();
        String sa_profit = docValues.getSa_profit();
        String sa_receive_amount = docValues.getSa_receive_amount();
        String sa_seat_num = docValues.getSa_seat_num();
        String sa_create_time = docValues.getSa_create_time();
        if (sa_create_time == null && StringUtils.isBlank(sa_create_time)) {
            return;
        }
        if (sa_sort_code == null && StringUtils.isBlank(sa_sort_code)) {
            return;
        }
        if (sa_area_id == null && StringUtils.isBlank(sa_area_id)) {
            return;
        }
        if (sa_area_name == null && StringUtils.isBlank(sa_area_name)) {
            return;
        }
        if (sa_order_num == null && StringUtils.isBlank(sa_order_num)) {
            return;
        }
        if (sa_people_num == null && StringUtils.isBlank(sa_people_num)) {
            return;
        }
        if (sa_source_amount == null && StringUtils.isBlank(sa_source_amount)) {
            return;
        }
        if (sa_discount_amount == null && StringUtils.isBlank(sa_discount_amount)) {
            return;
        }
        if (sa_discount_fee == null && StringUtils.isBlank(sa_discount_fee)) {
            return;
        }
        if (sa_profit == null && StringUtils.isBlank(sa_profit)) {
            return;
        }
        if (sa_receive_amount == null && StringUtils.isBlank(sa_receive_amount)) {
            return;
        }
        if (sa_seat_num == null && StringUtils.isBlank(sa_seat_num)) {
            return;
        }
        String[] area_id = StringUtils.split(sa_area_id, ",");
        String[] sort_code = StringUtils.split(sa_sort_code, ",");
        String[] area_name = StringUtils.split(sa_area_name, ",");
        String[] order_num = StringUtils.split(sa_order_num, ",");
        String[] people_num = StringUtils.split(sa_people_num, ",");
        String[] source_amount = StringUtils.split(sa_source_amount, ",");
        String[] discount_amount = StringUtils.split(sa_discount_amount, ",");
        String[] discount_fee = StringUtils.split(sa_discount_fee, ",");
        String[] profits = StringUtils.split(sa_profit, ",");
        String[] receive_amount = StringUtils.split(sa_receive_amount, ",");
        String[] seat_num = StringUtils.split(sa_seat_num, ",");
        String[] create_time = StringUtils.split(sa_create_time, ",");
        if (!(area_name.length > 0) || !(area_name.length == order_num.length)) {
            return;
        }
        for (int i = 0; i < area_name.length; i++) {
            TableSumarry ts = tableSumarryMap.get(area_id[i]);
            TisMoney orderNum = TisMoney.create(order_num[i]);
            TisMoney peopleNum = TisMoney.create(people_num[i]);
            TisMoney sourceAmount = TisMoney.create(source_amount[i]);
            TisMoney discountAmount = TisMoney.create(discount_amount[i]);
            TisMoney discountFee = TisMoney.create(discount_fee[i]);
            TisMoney profit = TisMoney.create(profits[i]);
            TisMoney receiveAmount = TisMoney.create(receive_amount[i]);
            TisMoney seatNum = TisMoney.create(seat_num[i]);
            if (ts == null) {
                int count = 1;
                ts = new TableSumarry(area_name[i], orderNum, peopleNum, sourceAmount, discountAmount, discountFee, profit, receiveAmount, seatNum, count, sort_code[i], create_time[i]);
                tableSumarryMap.put(area_id[i], ts);
            } else {
                ts.count++;
                ts.addIncrease(new TableSumarry(area_name[i], orderNum, peopleNum, sourceAmount, discountAmount, discountFee, profit, receiveAmount, seatNum, sort_code[i], create_time[i]));
            }
        }
    }

    public String write(HashMap<String, TableSumarry> tableSumarryMap) {
        List<JSONObject> list = new ArrayList<>();
        /**
         * 不含零售单的合计
         */
        TisMoney sumOrderNum = TisMoney.create();
        TisMoney sumPeopleNum = TisMoney.create();
        TisMoney sumSourceAmount = TisMoney.create();
        TisMoney sumDiscountAmount = TisMoney.create();
        TisMoney sumDiscountFee = TisMoney.create();
        TisMoney sumProfit = TisMoney.create();
        TisMoney sumReceiveAmount = TisMoney.create();
        TisMoney sumSeatNum = TisMoney.create();
        /**
         * 不包含零售单的总计平均桌位数
         */
        BigDecimal avgSeatNum = new BigDecimal("0");
        /**
         * 包含零售单的合计
         */
        TisMoney sumOrderNum1 = TisMoney.create();
        TisMoney sumPeopleNum1 = TisMoney.create();
        TisMoney sumSourceAmount1 = TisMoney.create();
        TisMoney sumDiscountAmount1 = TisMoney.create();
        TisMoney sumDiscountFee1 = TisMoney.create();
        TisMoney sumProfit1 = TisMoney.create();
        TisMoney sumReceiveAmount1 = TisMoney.create();
        TisMoney sumSeatNum1 = TisMoney.create();
        for (Map.Entry<String, TableSumarry> entry : tableSumarryMap.entrySet()) {
            JSONObject o = new JSONObject();
            o.put("areaName", entry.getValue().sa_area_name);
            o.put("orderNum", entry.getValue().sa_order_num.toString());
            o.put("peopleNum", entry.getValue().sa_people_num.toString());
            o.put("sourceAmount", entry.getValue().sa_source_amount.toString());
            o.put("discountAmount", entry.getValue().sa_discount_amount.toString());
            o.put("discountFee", entry.getValue().sa_discount_fee.toString());
            o.put("profit", entry.getValue().sa_profit.toString());
            o.put("receiveAmount", entry.getValue().sa_receive_amount.toString());
            o.put("sortCode", entry.getValue().sa_sort_code.toString());
            o.put("createTime", entry.getValue().sa_create_time.toString());
            TisMoney singlePeopleConsume = TisUtil.devide(entry.getValue().sa_receive_amount, entry.getValue().sa_people_num);
            o.put("singlePeopleConsume", singlePeopleConsume.toString());
            TisMoney singleAvgConsume = TisUtil.devide(entry.getValue().sa_receive_amount, entry.getValue().sa_order_num);
            o.put("singleAvgConsume", singleAvgConsume.toString());
            sumOrderNum1.add(entry.getValue().sa_order_num);
            sumPeopleNum1.add(entry.getValue().sa_people_num);
            sumSourceAmount1.add(entry.getValue().sa_source_amount);
            sumDiscountAmount1.add(entry.getValue().sa_discount_amount);
            sumDiscountFee1.add(entry.getValue().sa_discount_fee);
            sumProfit1.add(entry.getValue().sa_profit);
            sumReceiveAmount1.add(entry.getValue().sa_receive_amount);
            sumSeatNum1.add(entry.getValue().sa_seat_num);
            /**
             *  翻桌率的计算方式 sum(order_num)/(avg(seat_num)*时间段)，如果区域id为0，表示为零售单
             *  如果seat_num为空（=零售单）则没有翻桌率
             */
            if (entry.getKey().equals("0")) {
                // 有可能全部是零售单的情况
                o.put("turnTableRate", "100%");
            } else {
                // 不含零售单的合计
                sumOrderNum.add(entry.getValue().sa_order_num);
                sumPeopleNum.add(entry.getValue().sa_people_num);
                sumSourceAmount.add(entry.getValue().sa_source_amount);
                sumDiscountAmount.add(entry.getValue().sa_discount_amount);
                sumDiscountFee.add(entry.getValue().sa_discount_fee);
                sumProfit.add(entry.getValue().sa_profit);
                sumReceiveAmount.add(entry.getValue().sa_receive_amount);
                sumSeatNum.add(entry.getValue().sa_seat_num);
                BigDecimal count = new BigDecimal(entry.getValue().count);
                BigDecimal seatNum = new BigDecimal(entry.getValue().sa_seat_num.toString());
                BigDecimal orderNum = new BigDecimal(entry.getValue().sa_order_num.toString());
                BigDecimal turnTableRate = null;
                // 平均桌位数
                BigDecimal avseat = seatNum.divide(count, 4, BigDecimal.ROUND_HALF_UP);
                System.out.println("avseat:" + avseat);
                // 合计的平均座位数
                avgSeatNum = avgSeatNum.add(avseat);
                turnTableRate = orderNum.divide(avseat, 4, BigDecimal.ROUND_HALF_UP);
                // turnTableRate = turnTableRate.multiply(new BigDecimal(100));//转化为百分数
                // 格式化，取翻桌率两位小数
                BigDecimal t = turnTableRate.setScale(4, BigDecimal.ROUND_HALF_UP);
                o.put("turnTableRate", t.toString());
            }
            list.add(o);
        }
        JSONObject sum = new JSONObject();
        sum.put("areaName", "合计");
        sum.put("orderNum", sumOrderNum1.toString());
        sum.put("peopleNum", sumPeopleNum1.toString());
        sum.put("sourceAmount", sumSourceAmount1.toString());
        sum.put("discountAmount", sumDiscountAmount1.toString());
        sum.put("discountFee", sumDiscountFee1.toString());
        sum.put("profit", sumProfit1.toString());
        sum.put("receiveAmount", sumReceiveAmount1.toString());
        sum.put("orderNum", sumOrderNum1.toString());
        sum.put("singlePeopleConsume", TisUtil.devide(sumReceiveAmount1, sumPeopleNum1).toString());
        sum.put("singleAvgConsume", TisUtil.devide(sumReceiveAmount1, sumOrderNum1).toString());
        if (list.size() == 1 && list.get(0).get("areaName").equals("RETAIL")) {
            // 只有零售单的情况判断
            sum.put("turnTableRate", "100%");
        } else {
            // 合计的翻桌率的计算不包含零售单
            BigDecimal ssumOrderNum = new BigDecimal(sumOrderNum.toString());
            // BigDecimal ssumSeatNum = new BigDecimal(sumSeatNum.toString());
            BigDecimal turnTableRate = null;
            // 平均桌位数
            turnTableRate = ssumOrderNum.divide(avgSeatNum, 4, BigDecimal.ROUND_HALF_UP);
            // turnTableRate = turnTableRate.multiply(new BigDecimal(100));//转化为百分数
            // 格式化，取翻桌率两位小数
            BigDecimal t = turnTableRate.setScale(4, BigDecimal.ROUND_HALF_UP);
            sum.put("turnTableRate", t.toString());
        }
        list.add(sum);
        /**
         * 返回结果集转化为json串
         */
        JSONArray result = new JSONArray();
        for (JSONObject js : list) {
            result.add(js);
        }
        return result.toJSONString();
    }
}
