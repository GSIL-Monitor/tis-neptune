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

import com.qlangtech.tis.solrextend.handler.component.TisMoney;
import java.math.BigDecimal;

/*
 * Created by lingxiao on 2016/7/22.
 *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class TisUtil {

    public static TisMoney devide(TisMoney t, int n) {
        if (n == 0) {
            return TisMoney.create("0");
        }
        BigDecimal c = new BigDecimal(n);
        BigDecimal t1 = new BigDecimal(t.toString());
        t1 = t1.divide(c, 2, BigDecimal.ROUND_HALF_UP);
        return TisMoney.create(t1.toString());
    }

    public static TisMoney devide(TisMoney t, TisMoney n) {
        String t1 = t.toString();
        String n1 = n.toString();
        if (n1.equals("0") || n1.equals("0.0") || t1.equals("0") || t1.equals("0.0")) {
            return TisMoney.create("0");
        }
        BigDecimal t2 = new BigDecimal(t.toString());
        BigDecimal n2 = new BigDecimal(n.toString());
        t2 = t2.divide(n2, 2, BigDecimal.ROUND_HALF_UP);
        return TisMoney.create(t2.toString());
    }

    /**
     * TisMoney 两数相减
     * @param t
     * @param n
     * @return
     */
    public static TisMoney subtract(TisMoney t, TisMoney n) {
        BigDecimal t2 = new BigDecimal(t.toString());
        BigDecimal n2 = new BigDecimal(n.toString());
        t2 = t2.subtract(n2);
        return TisMoney.create(t2.toString());
    }

    /**
     * * 公用的获取solr中的每个字段的数据的值的方法
     */
    public static String getString(CommonFieldColumn field) {
        if (!field.val.exists) {
            return null;
        }
        return field.val.toString();
    }

    public static int getIntValue(CommonFieldColumn field) {
        if (!field.val.exists) {
            return 0;
        }
        try {
            return Integer.parseInt(field.val.toString());
        } catch (Throwable e) {
            return 0;
        }
    }

    public static TisMoney getFloatValue(CommonFieldColumn field) {
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
