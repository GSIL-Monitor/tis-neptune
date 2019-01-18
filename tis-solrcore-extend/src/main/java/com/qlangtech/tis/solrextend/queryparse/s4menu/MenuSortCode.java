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

import static com.qlangtech.tis.solrextend.queryparse.MenuRecommendQparserPlugin.FIELD_KEY_RECOMMEND_LEVEL;
import static com.qlangtech.tis.solrextend.queryparse.MenuRecommendQparserPlugin.FIELD_KEY_SPECIAL_TAG_ID;
import org.apache.commons.lang.StringUtils;
import org.apache.solr.schema.IndexSchema;
import com.qlangtech.tis.solrextend.handler.component.AllConsumeDimStatisComponent.FieldColumn;
import com.qlangtech.tis.solrextend.handler.component.s4menu.RecommendMenuComponent;

/*
 * 智能点餐二期“懒得想，帮我点”菜肴展示优先级类型枚举
 *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public enum MenuSortCode {

    RECOMMEND_LEVEL(0, "推荐指数高的优先", new ColumnGetter() {

        public FieldColumn get(IndexSchema schema) {
            return new FieldColumn(schema.getField(FIELD_KEY_RECOMMEND_LEVEL));
        }

        public float getValue(FieldColumn column, boolean change, /* 换一批 */
        int docid) {
            return random((float) (column.getInt(docid)), change);
        }

        public boolean compare(float doc1Score, float doc2Score) {
            return doc1Score > doc2Score;
        }
    }), SPECIAL_LABEL(1, "有特色标签的优先", new ColumnGetter() {

        public FieldColumn get(IndexSchema schema) {
            return new FieldColumn(schema.getField(FIELD_KEY_SPECIAL_TAG_ID));
        }

        public float getValue(FieldColumn column, boolean change, /* 换一批 */
        int docid) {
            String specTagId = column.getString(docid);
            return random((StringUtils.isNotBlank(specTagId) && !StringUtils.contains(specTagId, "null")) ? 1 : 0, change);
        }

        public boolean compare(float doc1Score, float doc2Score) {
            return doc1Score > doc2Score;
        }
    }), ACRID_LEVEL(2, "辣椒指数高的优先", new ColumnGetter() {

        public FieldColumn get(IndexSchema schema) {
            // return new FieldColumn(schema.getField(FIELD_KEY_ACRID_LEVEL));
            return new FieldColumn(schema.getField(RecommendMenuComponent.DOC_FIELD_PRICE));
        }

        public float getValue(FieldColumn column, boolean change, /* 换一批 */
        int docid) {
            return random(column.getInt(docid), change);
        }

        public boolean compare(float doc1Score, float doc2Score) {
            return doc1Score > doc2Score;
        }
    }), EXPENSIVE_PRICE(3, "价格高的优先", new ColumnGetter() {

        public FieldColumn get(IndexSchema schema) {
            return new FieldColumn(schema.getField(RecommendMenuComponent.DOC_FIELD_PRICE));
        }

        public float getValue(FieldColumn column, boolean change, /* 换一批 */
        int docid) {
            return random(column.getFloat(docid), change);
        }

        public boolean compare(float doc1Score, float doc2Score) {
            return doc1Score > doc2Score;
        }
    }), CHEAP_PRICE(4, "价格低的优先", new ColumnGetter() {

        public FieldColumn get(IndexSchema schema) {
            return new FieldColumn(schema.getField(RecommendMenuComponent.DOC_FIELD_PRICE));
        }

        public float getValue(FieldColumn column, boolean change, /* 换一批 */
        int docid) {
            return random(column.getFloat(docid), change);
        }

        public boolean compare(float doc1Score, float doc2Score) {
            return doc2Score > doc1Score;
        }
    });

    // RANDOM(-1, "随机排序", new ColumnGetter() {
    // 
    // public FieldColumn get(IndexSchema schema) {
    // return MOCK_COLUMN;
    // }
    // 
    // /**
    // * 取得随机因子
    // */
    // public float getValue(FieldColumn column, int docid) {
    // return (float) (java.lang.Math.random() * 100);
    // }
    // 
    // public boolean compare(float doc1Score, float doc2Score) {
    // return doc2Score > doc1Score;
    // }
    // });
    private static float random(float value, boolean chage) {
        // 使用log10函数的原因是当使用了log10函数之后 大数 小数的曲线 就不是很陡峭了,基本上概率就能平均分布了
        return ((float) (chage ? Math.log10((value + 100)) : (value + 2))) * (float) (chage ? ((Math.random() / 10) + 1) : ((Math.random() / 4) + 1));
    }

    public FieldColumn getFieldColumn(IndexSchema schema) {
        return columnGetter.get(schema);
    }

    public float getValue(FieldColumn column, boolean change, int docid) {
        return columnGetter.getValue(column, change, docid);
    }

    public boolean compare(float doc1Score, float doc2Score) {
        return columnGetter.compare(doc1Score, doc2Score);
    }

    private interface ColumnGetter {

        public FieldColumn get(IndexSchema schema);

        /**
         * @param column
         * @param chage
         *            是否是换一批操作
         * @param docid
         * @return
         */
        public float getValue(FieldColumn column, boolean chage, int docid);

        public boolean compare(float doc1Score, float doc2Score);
    }

    /**
     * 菜肴展示优先级类型
     */
    private final int type;

    private final ColumnGetter columnGetter;

    /**
     * 菜肴展示优先级描述
     */
    private String description;

    MenuSortCode(int type, String description, ColumnGetter columnGetter) {
        this.type = type;
        this.description = description;
        this.columnGetter = columnGetter;
    }

    public int getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 根据菜肴展示优先级类型获取枚举实例
     */
    public static MenuSortCode getInstance(Integer type) {
        if (type == null) {
            throw new IllegalArgumentException("argument type can not be null");
        }
        for (MenuSortCode sortCode : MenuSortCode.values()) {
            if (sortCode.type == type) {
                return sortCode;
            }
        }
        throw new IllegalStateException("type:" + type + " is illegal");
    }
}
