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
import com.qlangtech.tis.solrextend.handler.component.TisMoney;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.search.SimpleCollector;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/*
 * Created by lingxiao on 2016/6/29.
 *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class ProductDepartJSONFloatPairCollector extends SimpleCollector {

    public ProductDepartDocValues productDepartDocValues;

    public Map<String, ProductDepartSummaryStatis> /* wareName */
    summaryStatisMap;

    public ProductDepartSummaryStatis summaryStatis;

    public ProductDepartJSONFloatPairCollector(ProductDepartDocValues productDepartDocValues) {
        this.productDepartDocValues = productDepartDocValues;
        this.summaryStatis = new ProductDepartSummaryStatis();
        this.summaryStatisMap = new HashMap<>();
    }

    @Override
    public boolean needsScores() {
        return false;
    }

    protected void doSetNextReader(LeafReaderContext context) throws IOException {
        productDepartDocValues.doSetNextReader(context);
    }

    static int i = 0;

    @Override
    public void collect(int doc) throws IOException {
        productDepartDocValues.fillValue(doc);
        String whs = productDepartDocValues.getWarehouseNameColumn();
        if (whs == null || StringUtils.isBlank(whs)) {
            whs = "-1";
        }
        String[] wh_names = StringUtils.split(whs, ",");
        String[] mwh_ratios = StringUtils.split(productDepartDocValues.getRatio(), ",");
        if (!(mwh_ratios.length > 0) || !(wh_names.length == mwh_ratios.length)) {
            return;
        }
        for (int i = 0; i < mwh_ratios.length; i++) {
            // (冷, {1.5 30 40 0.6}) (热, {1.5,30,40,0.4})
            TisMoney account_num = productDepartDocValues.getAccountNumColumn();
            TisMoney in_fee = productDepartDocValues.getRatioBeforeFee();
            TisMoney ratio_fee = productDepartDocValues.getRatioAfterFee();
            ProductDepartSummaryStatis pss = summaryStatisMap.get(wh_names[i]);
            if (pss == null) {
                BigDecimal percent = (new BigDecimal(mwh_ratios[i])).divide(new BigDecimal(100));
                account_num.addCoefficient(percent);
                in_fee.addCoefficient(percent);
                ratio_fee.addCoefficient(percent);
                pss = new ProductDepartSummaryStatis(wh_names[i], account_num, in_fee, ratio_fee, mwh_ratios[i]);
                summaryStatisMap.put(wh_names[i], pss);
            } else {
                pss.addIncrease(new ProductDepartSummaryStatis(wh_names[i], account_num, in_fee, ratio_fee, mwh_ratios[i]));
            }
            if ("热菜".equals(wh_names[i])) {
                System.out.println(account_num.toString() + ":" + mwh_ratios[i] + ":" + pss.accountNum);
            }
        }
    }

    public String write(Map<String, ProductDepartSummaryStatis> summaryStatisMap) {
        JSONArray result = new JSONArray();
        for (Map.Entry<String, ProductDepartSummaryStatis> entry : summaryStatisMap.entrySet()) {
            JSONObject o = new JSONObject();
            o.put("warehouseName", entry.getValue().wh_name);
            o.put("accountNum", entry.getValue().accountNum.toString());
            o.put("ratioBeforeFee", entry.getValue().ratioBeforeFee.toString());
            o.put("ratioAfterFee", entry.getValue().ratioAfterFee.toString());
            System.out.println(o.toJSONString());
            result.add(o);
        }
        return result.toJSONString();
    }
}
