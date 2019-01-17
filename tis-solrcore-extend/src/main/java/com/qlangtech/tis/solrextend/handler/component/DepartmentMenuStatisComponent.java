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
package com.qlangtech.tis.solrextend.handler.component;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.queries.function.FunctionValues;
import org.apache.lucene.queries.function.ValueSource;
import org.apache.lucene.search.SimpleCollector;
import org.apache.lucene.util.mutable.MutableValue;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.handler.component.ResponseBuilder;
import org.apache.solr.handler.component.SearchComponent;
import org.apache.solr.schema.IndexSchema;
import org.apache.solr.schema.SchemaField;
import org.apache.solr.search.DocListAndSet;
import com.alibaba.fastjson.JSON;
import com.qlangtech.tis.solrextend.handler.component.TripleValueMapReduceComponent.AllMenuRow;
import com.qlangtech.tis.solrextend.handler.component.TripleValueMapReduceComponent.CountAndFeeGroupByMenuName;

/*
 * 出品部门统计
 *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class DepartmentMenuStatisComponent extends SearchComponent {

    public static final String COMPONENT_NAME = "department_menu_statis_component";

    private static final BigDecimal PERCENT_100 = new BigDecimal(1, TripleValueMapReduceComponent.BigDecimalContext);

    @Override
    public void prepare(ResponseBuilder rb) throws IOException {
        if (rb.req.getParams().getBool(PairValSummary.FIELD_MAP_REDUCE, false)) {
            rb.setNeedDocSet(true);
        }
    }

    private static final String blankMenuBucket = "menu0";

    private static final Pattern MENUID_PERCENT = Pattern.compile("(.+?)(_(.+))?");

    @Override
    public void process(ResponseBuilder rb) throws IOException {
        SolrParams params = rb.req.getParams();
        if (!params.getBool(COMPONENT_NAME, false)) {
            return;
        }
        Map<String, SummaryStatis> /* menuBucketKey */
        summaryStatisMap = new HashMap<String, SummaryStatis>();
        final Map<String, Map<String, MenuPercent>> /* menu_id */
        departmentMenuPercent = new HashMap<>();
        Map<String, MenuPercent> /* menu_id */
        menuPercentList = null;
        int count = 1;
        String menuBucketKey = null;
        SummaryStatis statis = null;
        String[] menuids = null;
        Matcher matcher = null;
        MenuPercent menuPercent = null;
        BigDecimal percent = new BigDecimal(1);
        while (true) {
            menuBucketKey = "menu" + (count++);
            menuids = StringUtils.split(params.get(menuBucketKey), ",");
            if (StringUtils.isEmpty(menuBucketKey) || menuids == null || menuids.length < 1) {
                break;
            }
            menuPercentList = new HashMap<String, /* menu_id */
            MenuPercent>();
            departmentMenuPercent.put(menuBucketKey, menuPercentList);
            for (String menuidPercent : menuids) {
                matcher = MENUID_PERCENT.matcher(menuidPercent);
                if (matcher.matches()) {
                    percent = PERCENT_100;
                    try {
                        percent = new BigDecimal(matcher.group(3), // Float.parseFloat();
                        TripleValueMapReduceComponent.BigDecimalContext);
                    } catch (Throwable e) {
                    }
                    menuPercent = new MenuPercent(matcher.group(1), percent);
                    menuPercentList.put(menuPercent.menuid, menuPercent);
                }
            }
            statis = new SummaryStatis(menuBucketKey);
            summaryStatisMap.put(menuBucketKey, statis);
        }
        // 默认的品类，如果instance 没有设置menuid的话就自动分类到menu0中去
        summaryStatisMap.put(blankMenuBucket, new SummaryStatis(blankMenuBucket));
        IndexSchema schema = rb.req.getSchema();
        DocValues docValues = new DocValues();
        docValues.menuAll = new FieldColumn(schema.getField("all_menu"));
        DocListAndSet results = rb.getResults();
        JSONFloatPairCollector collector = new JSONFloatPairCollector(docValues, summaryStatisMap, departmentMenuPercent);
        rb.req.getSearcher().search(results.docSet.getTopFilter(), collector);
        rb.rsp.add("all_department_statis", ResultUtils.writeMap(summaryStatisMap));
    }

    /**
     * 菜单在各个部门的隶属比例
     *
     * @author 百岁 baisui@2dfire.com
     * @date 2016年1月13日 下午3:13:22
     */
    private static class MenuPercent {

        private final String menuid;

        private final BigDecimal percent;

        public MenuPercent(String menuid, BigDecimal percent) {
            super();
            this.menuid = menuid;
            this.percent = percent;
        }
    }

    private static class JSONFloatPairCollector extends SimpleCollector {

        private final DocValues docValues;

        private final Map<String, SummaryStatis> /* menuid */
        summaryStatisMap;

        // private final Map<String/* menuid */, String/* bucketid */>
        // menuBucketIndex;
        private final Map<String, Map<String, MenuPercent>> /* menu_id */
        departmentMenuPercent;

        public JSONFloatPairCollector(DocValues docValues, Map<String, /* menuid */
        SummaryStatis> menuMap, final Map<String, /* bucketid */
        Map<String, /* menu_id */
        MenuPercent>> departmentMenuPercent) // final Map<String/* menuid */, String/* bucketid */> menuBucketIndex
        {
            super();
            this.docValues = docValues;
            this.summaryStatisMap = menuMap;
            this.departmentMenuPercent = departmentMenuPercent;
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
            String menuAll = docValues.getMenuAll();
            if (StringUtils.isBlank(menuAll)) {
                return;
            }
            String[] allInstance = StringUtils.split(menuAll, ";");
            CountAndFeeGroupByMenuName countFee = null;
            String menuId = null;
            SummaryStatis summary = null;
            AllMenuRow tuple = null;
            MenuPercent menuPercent = null;
            boolean matchedmenuid = false;
            for (String i : allInstance) {
                tuple = TripleValueMapReduceComponent.parseRow(i);
                if (tuple == null) {
                    continue;
                }
                matchedmenuid = false;
                menuId = tuple.getMenuId();
                countFee = TripleValueMapReduceComponent.createCountAndFee(TripleValueMapReduceComponent.GROUP_KEY_KIND, tuple);
                for (Map.Entry<String, Map<String, MenuPercent>> /* menu_id */
                entry : departmentMenuPercent.entrySet()) {
                    menuPercent = entry.getValue().get(menuId);
                    if (menuPercent == null) {
                        continue;
                    }
                    summary = summaryStatisMap.get(/* bucketid */
                    entry.getKey());
                    if (summary == null) {
                        throw new IllegalStateException("bucketId:" + entry.getKey() + " is null in menuMap:" + JSON.toJSONString(summaryStatisMap));
                    }
                    matchedmenuid = true;
                    if (menuPercent.percent.doubleValue() > 0f && menuPercent.percent.doubleValue() < 1f) {
                        countFee.addCoefficient(menuPercent.percent);
                    }
                    summary.add(countFee);
                }
                if (!matchedmenuid) {
                    summary = summaryStatisMap.get(blankMenuBucket);
                    if (summary == null) {
                        throw new IllegalStateException("bucketId:" + blankMenuBucket + " is null in menuMap:" + JSON.toJSONString(summaryStatisMap));
                    }
                    summary.add(countFee);
                }
            }
        }
    }

    private static class SummaryStatis {

        private final String groupKey;

        // 出品数目
        // AllConsumeDimStatisComponent.ZERO;
        private TisMoney allNum = TisMoney.create();

        // 折前金额
        // AllConsumeDimStatisComponent.ZERO;
        private TisMoney feeAll = TisMoney.create();

        // 折后金额
        // AllConsumeDimStatisComponent.ZERO;
        private TisMoney ratioFeeAll = TisMoney.create();

        public void add(CountAndFeeGroupByMenuName countFee) {
            // this.allNum += countFee.getCount();
            // 点菜单位一条鱼0.5斤为一个单位，如果是两斤那就是4个单位
            // =
            this.allNum.add(countFee.getAccountNum());
            // this.allNum.add(countFee.getAccountNum());
            // =
            this.feeAll.add(countFee.getFee());
            // this.feeAll.add(countFee.getFee());
            // =
            this.ratioFeeAll.add(countFee.getRatioFee());
        // this.ratioFeeAll.add(countFee.getRatioFee());
        }

        /**
         * @param groupKey
         */
        public SummaryStatis(String groupKey) {
            super();
            this.groupKey = groupKey;
        }

        @Override
        public String toString() {
            return this.write();
        }

        public String write() {
            return "allNum:" + allNum + ",feeAll:" + feeAll + ",ratioFeeAll:" + ratioFeeAll;
        }
    }

    public static class FieldColumn {

        private FunctionValues.ValueFiller filler;

        private MutableValue val;

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
            // this.field = field;
            this.valueSource = field.getType().getValueSource(field, null);
        }
    }

    private static class DocValues {

        private FieldColumn menuAll;

        // private void addIncrease() {
        // String groupKey = this.getGroupByKey();
        // if (StringUtils.isBlank(groupKey)) {
        // return;
        // }
        // SummaryStatis statis = summaryStatis.get(groupKey);
        // if (statis == null) {
        // statis = new SummaryStatis(groupKey);
        // summaryStatis.put(groupKey, statis);
        // }
        // statis.addIncrease(this);
        // }
        private void doSetNextReader(LeafReaderContext context) {
            menuAll.doSetNextReader(context);
        }

        private void fillValue(int doc) {
            menuAll.filler.fillValue(doc);
        }

        public String getMenuAll() {
            return this.getString(this.menuAll);
        }

        /**
         * @return
         */
        // private float getFloatValue(FieldColumn field) {
        // if (!field.val.exists) {
        // return 0;
        // }
        // return Float.parseFloat(field.val.toString());
        // }
        /**
         * @return
         */
        private String getString(FieldColumn field) {
            if (!field.val.exists) {
                return null;
            }
            return field.val.toString();
        }
    }

    @Override
    public String getDescription() {
        return COMPONENT_NAME;
    }
}
