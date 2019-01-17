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
package com.qlangtech.tis.solrextend.handler.component.s4SupplyOrder.docValueGetter.impl;

import com.qlangtech.tis.solrextend.handler.component.s4SupplyOrder.docValueGetter.IDocValueGetter;
import org.apache.lucene.index.LeafReader;
import org.apache.lucene.index.NumericDocValues;
import java.io.IOException;

/*
 solr中保存的是一个精确到毫秒的时间，这里需要把时间精度转换成天，并以String类型输出
 * Created by Qinjiu on 6/29/2017.
 *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class DateDocValueGetter implements IDocValueGetter {

    private NumericDocValues docValues;

    private final String fieldName;

    private static final long TIMES = (long) Math.pow(10, 9);

    public DateDocValueGetter(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public String getString(int docId) {
        return Long.toString(this.docValues.get(docId) / TIMES);
    }

    @Override
    public Number getNumber(int docId) {
        return this.docValues.get(docId);
    }

    @Override
    public void setDocValues(LeafReader reader) throws IOException {
        this.docValues = reader.getNumericDocValues(this.fieldName);
    }

    @Override
    public String getFieldName() {
        return this.fieldName;
    }
}
