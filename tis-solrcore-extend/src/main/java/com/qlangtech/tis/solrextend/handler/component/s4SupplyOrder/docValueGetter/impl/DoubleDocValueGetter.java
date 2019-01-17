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
 * Created by Qinjiu on 6/29/2017.
 *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class DoubleDocValueGetter implements IDocValueGetter {

    private NumericDocValues docValues;

    private final String fieldName;

    public DoubleDocValueGetter(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public void setDocValues(LeafReader reader) throws IOException {
        this.docValues = reader.getNumericDocValues(this.fieldName);
    }

    @Override
    public Number getNumber(int docId) {
        long value = this.docValues.get(docId);
        return Double.longBitsToDouble(value);
    }

    @Override
    public String getString(int docId) {
        return null;
    }

    @Override
    public String getFieldName() {
        return this.fieldName;
    }
}
