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

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.qlangtech.tis.solrextend.handler.component.s4fatInstance.KindMenuDocValues;
import com.qlangtech.tis.solrextend.handler.component.s4fatInstance.KindMenuSummaryStatis;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.search.SimpleCollector;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/*
 * Created by lingxiao on 2016/6/24.
 *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class KindMenuJSONFloatPairCollector extends SimpleCollector {

    public KindMenuDocValues docValues;

    public Map<String, KindMenuSummaryStatis> /* kindName */
    summaryStatisMap;

    public KindMenuSummaryStatis summaryStatis;

    public KindMenuJSONFloatPairCollector(KindMenuDocValues docValues) {
        this.docValues = docValues;
        this.summaryStatis = new KindMenuSummaryStatis();
        this.summaryStatisMap = new HashMap<>();
    }

    @Override
    public boolean needsScores() {
        return false;
    }

    protected void doSetNextReader(LeafReaderContext context) throws IOException {
        docValues.doSetNextReader(context);
    }

    @Override
    public void collect(int doc) throws IOException {
        docValues.fillValue(doc);
        String group_or_kind_name = docValues.getKindMenuName();
        KindMenuSummaryStatis kss2 = null;
        if ((kss2 = summaryStatisMap.get(group_or_kind_name)) == null) {
            kss2 = new KindMenuSummaryStatis(group_or_kind_name);
            summaryStatisMap.put(group_or_kind_name, kss2);
        }
        kss2.addIncrease(docValues);
    // for(Map.Entry<String,KindMenuSummaryStatis> entry:summaryStatisMap.entrySet() ) {
    // if(entry.getKey().equals(group_or_kind_name)) {
    // KindMenuSummaryStatis kss = entry.getValue();
    // kss.addIncrease(docValues);
    // summaryStatisMap.put(group_or_kind_name,kss);
    // } else {
    // KindMenuSummaryStatis kss = new KindMenuSummaryStatis(group_or_kind_name);
    // kss.addIncrease(docValues);
    // summaryStatisMap.put(group_or_kind_name,kss);
    // }
    // }
    }

    public String write(Map<String, KindMenuSummaryStatis> summaryStatisMap) {
        JSONArray results = new JSONArray();
        for (Map.Entry<String, KindMenuSummaryStatis> entry : summaryStatisMap.entrySet()) {
            JSONObject o = new JSONObject();
            o.put("kindName", entry.getValue().group_or_kind_name);
            o.put("accountNum", entry.getValue().accountNum.toString());
            o.put("ratioBeforeFee", entry.getValue().feeColumn.toString());
            o.put("ratioAfterFee", entry.getValue().ratioFee.toString());
            results.add(o);
        }
        return results.toJSONString();
    }
}
