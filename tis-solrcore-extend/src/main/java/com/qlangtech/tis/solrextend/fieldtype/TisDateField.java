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
package com.qlangtech.tis.solrextend.fieldtype;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.LegacyLongField;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexableField;
import org.apache.solr.schema.SchemaField;
import org.apache.solr.schema.TrieDateField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * 日期类型支持 支持原始数据为 ‘yyyyMMddHHmmss’这样的格式
 *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class TisDateField extends TrieDateField {

    private static final Logger log = LoggerFactory.getLogger(TisDateField.class);

    private static final ThreadLocal<SimpleDateFormat> dateFormatyyyyMMddHHmmss = new ThreadLocal<SimpleDateFormat>() {

        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyyMMddHHmmss");
        }
    };

    @Override
    public Date toObject(IndexableField f) {
        Number val = f.numericValue();
        if (val == null) {
            return super.toObject(f);
        }
        return new Date(val.longValue());
    }

    @Override
    public IndexableField createField(SchemaField field, Object value, float boost) {
        boolean indexed = field.indexed();
        boolean stored = field.stored();
        boolean docValues = field.hasDocValues();
        if (!indexed && !stored && !docValues) {
            if (log.isTraceEnabled())
                log.trace("Ignoring unindexed/unstored field: " + field);
            return null;
        }
        FieldType ft = new FieldType();
        ft.setStored(stored);
        ft.setTokenized(true);
        ft.setOmitNorms(field.omitNorms());
        ft.setIndexOptions(indexed ? getIndexOptions(field, value.toString()) : IndexOptions.NONE);
        ft.setNumericType(FieldType.LegacyNumericType.LONG);
        ft.setNumericPrecisionStep(precisionStep);
        final org.apache.lucene.document.Field f;
        Date date = null;
        try {
            date = dateFormatyyyyMMddHHmmss.get().parse(value.toString());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        f = new LegacyLongField(field.getName(), date.getTime(), ft);
        // f = new org.apache.lucene.document.LongField(field.getName(),
        // date.getTime(), ft);
        f.setBoost(boost);
        return f;
    }

    @Override
    public String toExternal(IndexableField f) {
        Date date = this.toObject(f);
        return dateFormatyyyyMMddHHmmss.get().format(date);
    }
}
