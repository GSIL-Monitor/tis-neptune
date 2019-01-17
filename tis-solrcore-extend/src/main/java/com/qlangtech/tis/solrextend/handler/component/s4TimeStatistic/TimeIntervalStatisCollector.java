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
package com.qlangtech.tis.solrextend.handler.component.s4TimeStatistic;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.qlangtech.tis.solrextend.handler.component.TisMoney;
import com.qlangtech.tis.solrextend.handler.component.search4OperationStatistic.TisUtil;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.search.SimpleCollector;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/*
 * Created by lingxiao on 2016/7/19.
 *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class TimeIntervalStatisCollector extends SimpleCollector {

    public TimeIntervalStatisDocValues docValues;

    public Map<String, TimeIntervalStatisSummary> timeIntervalStatisMap;

    public TimeIntervalStatisSummary timeIntervalStatisSummary;

    public TimeIntervalStatisCollector(TimeIntervalStatisDocValues docValues) {
        this.docValues = docValues;
        this.timeIntervalStatisSummary = new TimeIntervalStatisSummary();
        this.timeIntervalStatisMap = new HashMap<>();
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
        String hour = docValues.getHour();
        TimeIntervalStatisSummary timeIntervalStatisSummary = timeIntervalStatisMap.get(hour);
        if (timeIntervalStatisSummary == null) {
            TimeIntervalStatisSummary timeIntervalStatisSummary1 = new TimeIntervalStatisSummary(hour);
            timeIntervalStatisMap.put(hour, timeIntervalStatisSummary1);
            timeIntervalStatisSummary1.addIncrease(docValues);
        } else {
            timeIntervalStatisSummary.addIncrease(docValues);
        }
    }

    public String write(Map<String, TimeIntervalStatisSummary> timeIntervalStatisMap) {
        JSONArray results = new JSONArray();
        for (Map.Entry<String, TimeIntervalStatisSummary> entry : timeIntervalStatisMap.entrySet()) {
            JSONObject o = new JSONObject();
            o.put("time", entry.getKey().toString() + ":00");
            o.put("openOrderNum", entry.getValue().openOrderCount);
            o.put("endOrderNum", entry.getValue().endOrderCount);
            o.put("openOrderPeople", entry.getValue().openOrderPeople);
            o.put("endOrderPeople", entry.getValue().endOrderPeople);
            o.put("receiveAmount", entry.getValue().recieveAmount.toString());
            o.put("discountAmount", entry.getValue().discountAmount.toString());
            o.put("profit", entry.getValue().profit.toString());
            o.put("invoice", entry.getValue().invoice.toString());
            int endOrderPeople = entry.getValue().endOrderPeople;
            TisMoney recieveAmount = entry.getValue().recieveAmount;
            o.put("averageAmout", TisUtil.devide(recieveAmount, endOrderPeople).toString());
            results.add(o);
        }
        return results.toJSONString();
    }
}
