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

import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.queries.function.FunctionValues;
import org.apache.lucene.queries.function.ValueSource;
import org.apache.lucene.util.mutable.MutableValue;
import org.apache.solr.schema.SchemaField;
import java.io.IOException;
import java.util.Collections;

/*
 * Created by Administrator on 2016/6/25.
 *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class FieldColumn {

    private final SchemaField field;

    FunctionValues.ValueFiller filler;

    MutableValue val;

    private ValueSource valueSource;

    public void doSetNextReader(LeafReaderContext context) {
        try {
            FunctionValues funcValues = valueSource.getValues(Collections.emptyMap(), context);
            filler = funcValues.getValueFiller();
            val = filler.getValue();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    FieldColumn(SchemaField field) {
        if (field == null) {
            throw new IllegalArgumentException("field can not be null");
        }
        this.field = field;
        this.valueSource = field.getType().getValueSource(field, null);
    }
}
