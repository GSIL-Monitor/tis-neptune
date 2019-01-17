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
package com.qlangtech.tis.solrextend.transformer.s4menu;

import com.qlangtech.tis.solrextend.fieldtype.s4menu.MultipleMenuField;
import com.qlangtech.tis.solrextend.handler.component.s4menu.RecommendMenuComponent;
import org.apache.commons.lang.StringUtils;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.response.transform.DocTransformer;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * multiple_menu_info字段的结构变成:
 * 菜单id_价格(规格id_规格价格)(规格id_规格价格);菜单id_价格(规格id_规格价格)(规格id_规格价格)
 * <p>
 * 需要替换的字段是 price 会员价格（这个列现在不要了） all_child_spec
 * ：00000241584816dc015856e8ede31080_杯_1_2_0.0 以上绿色背景的内容使用multiple_menu_info
 * 这个字段中的规格id所匹配的价格去替换。
 * <p>
 * 假设现在有这样一条记录： menu_id price all_child_spec multiple_menu_info 123456 56
 * 00000241584816dc015856e8ede31080_杯_1_2_0.0
 * kkkk_19(00000241584816dc015856e8ede31080_13) ———— 隐藏引用的内容 ————
 * 查询条件中带上了：q:multiple_menu_info:kkkk 然后出结果是这样： menu_id:123456 price:19
 * all_child_spec:00000241584816dc015856e8ede31080_杯_1_2_13
 * http://k.2dfire.net/pages/viewpage.action?pageId=315916731 on 7/4/2017.
 *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class MultipleMenuTransformer extends DocTransformer {

    private static final String PRICE = "price";

    private static final String MEMBER_PRICE = "member_price";

    private static final String ALL_CHILD_SPEC = "all_child_spec";

    // public static final String MULTIPLE_MENU_INFO = "multiple_menu_info";
    private static final String UNDERLINE = "_";

    private static final String CHILD_SPEC_SEPARATOR = ",";

    public static final Pattern SPEC_PATTERN = Pattern.compile("\\((\\w+)_(\\-{0,1}[\\d.]+)\\)");

    private final SolrParams params;

    public MultipleMenuTransformer(SolrParams params) {
        this.params = params;
    }

    @Override
    public String getName() {
        return null;
    }

    public static void main(String[] args) {
        // 001180166247a30c0162492c2e3072e1
        Matcher matcher = MultipleMenuField.MENU_INFO_PATTERN.matcher("001180166247a30c0162492c2e3072e1_0_9.0");
        if (matcher.matches()) {
            System.out.println(matcher.group(2) + "_" + matcher.group(1) + "_" + matcher.group(3));
        }
    }

    @Override
    public void transform(SolrDocument doc, int docid, float score) throws IOException {
        // 检查fl中必须传递multiple_menu_info参数
        String menuId = params.get(RecommendMenuComponent.DOC_FIELD_MULTIPLE_MENU_INFO);
        if (StringUtils.isBlank(menuId)) {
            throw new IllegalStateException("[multi] must have multiple_menu_info=xxxx");
        }
        // multiple_menu_info字段不能为空
        Object fieldObject = doc.getFirstValue(RecommendMenuComponent.DOC_FIELD_MULTIPLE_MENU_INFO);
        if (fieldObject == null) {
            return;
        }
        // multiple_menu_info必须在setFields中设置
        if (fieldObject instanceof org.apache.lucene.document.LazyDocument.LazyField) {
            throw new IllegalStateException("please set " + RecommendMenuComponent.DOC_FIELD_MULTIPLE_MENU_INFO + " in field");
        }
        // String menuId = params.get(MULTIPLE_MENU_INFO);
        String multipleMenuInfo = val(fieldObject);
        Matcher matcher = null;
        // .split(MultipleMenuField.SEPARATOR);
        String[] menuInfos = StringUtils.split(multipleMenuInfo, MultipleMenuField.SEPARATOR);
        for (String menuInfo : menuInfos) {
            matcher = MultipleMenuField.MENU_INFO_PATTERN.matcher(menuInfo);
            if (matcher.matches()) {
                // kkkk_1/0(价格替换开关，1：不需要去替换了，0：需要替换)_19(00000241584816dc015856e8ede31080_13)
                int replace = Integer.parseInt(matcher.group(2));
                if (replace == 0 && StringUtils.equals(matcher.group(1), menuId)) {
                    try {
                        doc.setField(PRICE, Double.parseDouble(matcher.group(3)));
                    } catch (Throwable e) {
                    }
                    try {
                        doc.setField(MEMBER_PRICE, Double.parseDouble(matcher.group(4)));
                    } catch (Throwable e) {
                    }
                    Object allChildSpecFieldObject = doc.getFirstValue(ALL_CHILD_SPEC);
                    if (allChildSpecFieldObject == null || allChildSpecFieldObject instanceof org.apache.lucene.document.LazyDocument.LazyField) {
                        return;
                    }
                    String allChildSpecField = val(allChildSpecFieldObject);
                    doc.setField(ALL_CHILD_SPEC, getAllChildSpecField(allChildSpecField, matcher.group(5)));
                    return;
                }
            }
        }
    }

    private String getAllChildSpecField(String allChildSpecField, String multiSpec) {
        if (StringUtils.isBlank(allChildSpecField)) {
            return null;
        }
        if (StringUtils.isBlank(multiSpec)) {
            return allChildSpecField;
        }
        Map<String, ChildSpec> childSpecMap = new HashMap<>();
        for (String childSpecString : allChildSpecField.split(CHILD_SPEC_SEPARATOR)) {
            String specId = StringUtils.substringBefore(childSpecString, UNDERLINE);
            ChildSpec childSpec = new ChildSpec(StringUtils.substringBeforeLast(childSpecString, UNDERLINE), StringUtils.substringAfterLast(childSpecString, UNDERLINE));
            childSpecMap.put(specId, childSpec);
        }
        Matcher matcher = SPEC_PATTERN.matcher(multiSpec);
        while (matcher.find()) {
            String specId = matcher.group(1);
            if (childSpecMap.containsKey(specId)) {
                ChildSpec childSpec = childSpecMap.get(specId);
                childSpec.setPrice(matcher.group(2));
            }
        }
        List<String> childSpecs = new ArrayList<>(childSpecMap.size());
        for (ChildSpec childSpec : childSpecMap.values()) {
            childSpecs.add(childSpec.toSpecString());
        }
        return StringUtils.join(childSpecs, CHILD_SPEC_SEPARATOR);
    }

    public static String val(Object o) {
        if (o == null) {
            return "";
        }
        if (!(o instanceof org.apache.lucene.document.Field)) {
            return String.valueOf(o);
        }
        org.apache.lucene.document.Field f = (org.apache.lucene.document.Field) o;
        return f.stringValue();
    }

    class ChildSpec {

        // 规格的前半部分
        String subString;

        // 规格的价格
        String price;

        ChildSpec(String subString, String price) {
            this.subString = subString;
            this.price = price;
        }

        void setPrice(String price) {
            this.price = price;
        }

        String toSpecString() {
            return subString + UNDERLINE + price;
        }
    }
}
