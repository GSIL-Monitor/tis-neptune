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
package com.qlangtech.tis.solrextend.queryparse.s4menu;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.schema.SchemaField;
import org.apache.solr.schema.TextField;
import com.qlangtech.tis.solrextend.handler.component.AllConsumeDimStatisComponent.FieldColumn;

/*
 * 辣味偏好
 *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class AcridPreferences {

    // 用户输入的辣味偏好值,大于等于0的一个值，0代表口味偏好为不辣，会把所有acrid>0的菜 全部过滤掉
    private final int filterLevel;

    private final MenuSortCode columnGetter = MenuSortCode.ACRID_LEVEL;

    private FieldColumn fieldColumn;

    /**
     * @param filterLevel
     */
    public AcridPreferences(int filterLevel) {
        super();
        this.filterLevel = filterLevel;
    }

    public void setFieldColumn(SolrQueryRequest req) {
        this.fieldColumn = filterLevel > -1 ? columnGetter.getFieldColumn(req.getSchema()) : MOCK_COLUMN;
    }

    public boolean filter(int doc) {
        return fieldColumn.getInt(doc) > filterLevel;
    }

    public void doSetNextReader(LeafReaderContext context) {
        fieldColumn.doSetNextReader(context);
    }

    private static final FieldColumn MOCK_COLUMN = new FieldColumn(new SchemaField("mock", new TextField())) {

        @Override
        public void doSetNextReader(LeafReaderContext context) {
        }

        public float getFloat(int docid) {
            return -1f;
        }

        public int getInt(int docid) {
            return -1;
        }

        public String getString(int docId) {
            return StringUtils.EMPTY;
        }
    };
}
