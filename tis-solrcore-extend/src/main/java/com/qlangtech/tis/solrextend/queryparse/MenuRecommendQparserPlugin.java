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
package com.qlangtech.tis.solrextend.queryparse;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.index.Term;
import org.apache.lucene.queries.TermsQuery;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.SimpleCollector;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.PriorityQueue;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.schema.IndexSchema;
import org.apache.solr.search.QParser;
import org.apache.solr.search.QParserPlugin;
import org.apache.solr.search.SyntaxError;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.qlangtech.tis.solrextend.fieldtype.s4menu.MenuSpecParserField;
import com.qlangtech.tis.solrextend.handler.component.AreaConsumeStatisComponent.FieldColumn;

/*
 * 根据用户购物车中的点菜记录，根据实时传送过来的推荐规则，進行點菜建議 <br>
 * http://k.2dfire.net/pages/viewpage.action?pageId=152895542<br>
 * http://k.2dfire.net/pages/viewpage.action?pageId=158957588<br>
 * 20170215新需求 1.推荐菜中需要传输不喜欢的菜： {!mrecommend dislike=xxx,xxx,xxx}
 * 注意：dislike的值是menu_spec中的标签值 2.推荐的菜中需要指定辣味指数，所有推荐的菜都必须要 小于等于这个辣味级别
 * {!mrecommend max_acrid_level=3}
 *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class MenuRecommendQparserPlugin extends QParserPlugin {

    private static final Pattern MENUS_PATTERN = Pattern.compile("([\\w|\\d]{32}):(\\d+)");

    public static final String FIELD_KEY_MENU_ID = "id";

    public static final String FIELD_KEY_ENTITY_ID = "entity_id";

    public static final String FIELD_KEY_MENU_SPEC = "menu_spec";

    public static final String FIELD_KEY_MP_REAL_ONLY = "mp_meal_only";

    public static final String FIELD_SALE_OUT = "sale_out";

    public static final String FIELD_IS_VALID = "is_valid";

    // public static final String FIELD_IS_SELF = "is_self";
    public static final String FIELD_KEY_RECOMMEND_LEVEL = "recommend_level";

    public static final String FIELD_KEY_SPECIAL_TAG_ID = "special_tag_id";

    // public static final String FIELD_KEY_ACRID_LEVEL = "acrid_level";
    private static final String CONTEXT_KEY_s4menu_recommend_rule = "s4menu_recommend_rule";

    // 不喜欢的标签
    private static final String LOCAL_PARAM_DISLIKE = "dislike";

    // 最大辣味指数
    // private static final String LOCAL_PARAM_MAX_ACRID_LEVEL = "max_" + FIELD_KEY_ACRID_LEVEL;
    private static final Logger logger = LoggerFactory.getLogger(MenuRecommendQparserPlugin.class);

    @Override
    @SuppressWarnings("all")
    public void init(NamedList params) {
    }

    public static final List<BytesRef> BytesRef_1 = Arrays.asList(new BytesRef("1"));

    public static final List<BytesRef> BytesRef_0 = Arrays.asList(new BytesRef("0"));

    @Override
    public QParser createParser(String menus, SolrParams localParams, SolrParams params, SolrQueryRequest req) {
        String entityId = localParams.get(FIELD_KEY_ENTITY_ID);
        if (StringUtils.isBlank(entityId)) {
            throw new IllegalArgumentException("entityid can not be null");
        }
        List<BytesRef> /* menu_id */
        menuids = new ArrayList<>();
        Map<String, Integer> /* count */
        menuCount = new HashMap<>();
        String menuid = null;
        Matcher matcher = MENUS_PATTERN.matcher(menus);
        while (matcher.find()) {
            // menu_id
            menuids.add(new BytesRef(menuid = matcher.group(1)));
            menuCount.put(menuid, Integer.parseInt(matcher.group(2)));
        }
        if (menuids.size() < 1) {
            throw new IllegalStateException("menus:" + menus + " is not illegal");
        }
        String rules = localParams.get("rules");
        if (StringUtils.isEmpty(rules)) {
            throw new IllegalArgumentException("rules can not be null");
        }
        // 不喜欢的菜
        String[] dislike = getDislikeParam(localParams);
        // 最大辣味指数
        // localParams.getInt(LOCAL_PARAM_MAX_ACRID_LEVEL);
        Integer maxAcridLevel = 0;
        try {
            /**
             * ▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼
             * =================================================================
             * 通过已经点的menuids，通過傳輸上來的rules計算出每個類目喜愛是點多了還是點燒了
             * 如果是點燒了的話通過對應類目的lableid再進行一次搜索，找到對應類目的菜
             * =================================================================
             */
            Map<String, RecommendRule> /* label+'_'+type */
            rl = parseRule(maxAcridLevel, rules);
            BooleanQuery.Builder qbuilder = new BooleanQuery.Builder();
            TermsQuery query = new TermsQuery(FIELD_KEY_MENU_ID, menuids);
            qbuilder.add(query, Occur.MUST);
            qbuilder.add(new TermQuery(new Term(FIELD_KEY_ENTITY_ID, new BytesRef(entityId))), Occur.FILTER);
            CheckedMenusCollector collector = new CheckedMenusCollector(req.getSchema(), rl.values(), menuCount);
            req.getSearcher().search(qbuilder.build(), collector);
            collector.processSummaryByRecommanRule();
            /**
             * ▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲
             */
            int recommanAdd;
            List<BytesRef> /* label_id */
            labelIds = new ArrayList<>();
            for (RecommendRule rule : collector.recommendRule) {
                recommanAdd = rule.recommandAdd();
                if (recommanAdd > 0) {
                    // 菜不够还要再加
                    labelIds.add(new BytesRef(String.valueOf(rule.labelId)));
                }
            }
            req.getContext().put(CONTEXT_KEY_s4menu_recommend_rule, rl);
            // QueryParser qp = new QueryParser(Version.LUCENE_5_3_0,
            // "sale_out", analyzer);
            qbuilder = new BooleanQuery.Builder();
            query = new TermsQuery(FIELD_KEY_MENU_ID, menuids);
            qbuilder.add(query, Occur.MUST_NOT);
            // 不能是只能在套餐子菜中使用的菜
            query = new TermsQuery(FIELD_KEY_MP_REAL_ONLY, BytesRef_1);
            qbuilder.add(query, Occur.MUST_NOT);
            TermsQuery specQuery = new TermsQuery(FIELD_KEY_MENU_SPEC, labelIds);
            qbuilder.add(specQuery, Occur.MUST);
            if (dislike.length > 0) {
                List<BytesRef> /* label_id */
                dislikeLabelIds = new ArrayList<>();
                for (String labelid : dislike) {
                    dislikeLabelIds.add(new BytesRef(labelid));
                }
                qbuilder.add(new TermsQuery(FIELD_KEY_MENU_SPEC, dislikeLabelIds), Occur.MUST_NOT);
            }
            qbuilder.add(new TermsQuery(FIELD_SALE_OUT, BytesRef_1), Occur.MUST_NOT);
            qbuilder.add(new TermQuery(new Term(FIELD_KEY_ENTITY_ID, new BytesRef(entityId))), Occur.FILTER);
            final BooleanQuery bquery = qbuilder.build();
            return new QParser(menus, localParams, params, req) {

                @Override
                public Query parse() throws SyntaxError {
                    return bquery;
                // return LangAwareLuceneQParserPlugin.mergeI18NQuery(req.getSearcher(),
                // localParams, bquery);
                // return bquery;
                }
            };
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String[] getDislikeParam(SolrParams localParams) {
        String[] dislike = null;
        try {
            String localParamDislike = localParams.get(LOCAL_PARAM_DISLIKE);
            if (localParamDislike == null) {
                dislike = new String[] {};
            } else {
                dislike = StringUtils.split(URLDecoder.decode(localParamDislike, "utf8"), ",");
            }
        } catch (UnsupportedEncodingException e1) {
            throw new RuntimeException(e1);
        }
        return dislike;
    }

    @SuppressWarnings("all")
    public static RecommendRules getRecommendRule(SolrQueryRequest req) {
        RecommendRules rules = (RecommendRules) req.getContext().get(CONTEXT_KEY_s4menu_recommend_rule);
        if (rules == null) {
            throw new IllegalStateException("context key:" + CONTEXT_KEY_s4menu_recommend_rule + " relevant val can not be null");
        }
        return rules;
    }

    protected Map<String, /* label+'_'+type */
    RecommendRule> parseRule(Integer maxAcridLevel, String rules) throws Exception {
        Map<String, RecommendRule> rcommandList = new RecommendRules(maxAcridLevel);
        RecommendRule rule = null;
        // style:"value+'_'+type+'_'+labelId[;value+'_'+type+'_'+labelId]"
        String[] tuple = StringUtils.split(URLDecoder.decode(rules, "utf8"), ";");
        String[] pair = null;
        String thresholdValue = null;
        String[] thresholdValues = null;
        String type = null;
        int labelId;
        for (String t : tuple) {
            pair = StringUtils.split(t, "_");
            thresholdValue = pair[0];
            thresholdValues = StringUtils.split(thresholdValue, ",");
            labelId = Integer.parseInt(pair[2]);
            type = pair[1];
            rule = new RecommendRule(labelId, LabelType.parse(type));
            rule.low = new Threshold();
            rule.low.open = "1".equals(thresholdValues[1]);
            rule.low.value = Integer.parseInt(thresholdValues[0]);
            rule.high = new Threshold();
            rule.high.open = "1".equals(thresholdValues[3]);
            rule.high.value = Integer.parseInt(thresholdValues[2]);
            rcommandList.put(rule.getKey(), rule);
        }
        return rcommandList;
    }

    /**
     * 已点菜收集器
     */
    private static class CheckedMenusCollector extends SimpleCollector {

        private final Collection<RecommendRule> recommendRule;

        private final FieldColumn menuIdFiled;

        // 菜规格
        private final FieldColumn menuSpecField;

        // 每一道菜的絕對量
        private final Map<String, Integer> /* count */
        menuCount;

        public CheckedMenusCollector(IndexSchema schema, Collection<RecommendRule> recommendRule, Map<String, /* menuid */
        Integer> menuCount) {
            super();
            this.recommendRule = recommendRule;
            this.menuIdFiled = new FieldColumn(schema.getField(FIELD_KEY_MENU_ID));
            // 菜规格
            this.menuSpecField = new FieldColumn(schema.getField(FIELD_KEY_MENU_SPEC));
            this.menuCount = menuCount;
        }

        @Override
        public boolean needsScores() {
            return false;
        }

        @Override
        protected void doSetNextReader(LeafReaderContext context) throws IOException {
            this.menuSpecField.doSetNextReader(context);
            this.menuIdFiled.doSetNextReader(context);
        }

        // 对应 menu_spec 字段（json）的label_info属性
        private Map<String, int[]> /* labelids */
        menuSpec = new HashMap<>();

        @Override
        public void collect(int doc) throws IOException {
            String menuid = menuIdFiled.getString(doc);
            String labelIds = menuSpecField.getString(doc);
            int[] labelids;
            if (StringUtils.isNotBlank(menuid) && StringUtils.isNotBlank(labelIds)) {
                labelids = new int[3];
                List<Integer> array = getMenuSpecLabel(labelIds);
                for (int i = 0; (i < array.size() && i < labelids.length); i++) {
                    labelids[i] = array.get(i);
                }
                menuSpec.put(menuid, labelids);
            }
        }

        /**
         * 根据规则，生成點菜推薦意見
         */
        private void processSummaryByRecommanRule() {
            // 遍历规则
            for (RecommendRule rule : recommendRule) {
                // 遍历已点菜
                for (Map.Entry<String, int[]> /* labels */
                entry : menuSpec.entrySet()) {
                    if (isMatch(rule, entry)) {
                        rule.addApplySum(menuCount.get(entry.getKey()));
                    }
                }
            }
        }

        protected static boolean isMatch(RecommendRule rule, Map.Entry<String, int[]> entry) {
            return rule.labelId == entry.getValue()[rule.type.arrayOffset];
        }
    }

    public static List<Integer> getMenuSpecLabel(String labelIds) {
        if (!StringUtils.startsWith(StringUtils.trim(labelIds), "{")) {
            return Collections.emptyList();
        }
        JSONTokener jt = new JSONTokener(labelIds);
        JSONObject o = new JSONObject(jt);
        if (o.isNull(MenuSpecParserField.KEY_LABEL_STRING)) {
            return Collections.emptyList();
        }
        // 12;44,55 从左向右，1.标签类型，2.主料大类，3.主料小类
        List<Integer> array = MenuSpecParserField.splitLabels(o.getString(MenuSpecParserField.KEY_LABEL_STRING));
        return array;
    }

    public static class RecommendRules extends HashMap<String, /* label+'_'+type */
    RecommendRule> {

        private static final long serialVersionUID = 1L;

        private final Integer maxAcridLevel;

        public Integer getMaxAcridLevel() {
            return maxAcridLevel;
        }

        /**
         * @param maxAcridLevel
         */
        public RecommendRules(Integer maxAcridLevel) {
            super();
            this.maxAcridLevel = maxAcridLevel;
        }
    }

    public static class RecommendRule {

        private final int labelId;

        private final LabelType type;

        // =
        private PriorityQueue<ScoreDoc> queue;

        // FieldValueHitQueue.create(sort.fields,
        // numHits);
        private int allFound = 0;

        public void pushPriorityQueue(int doc, float score) throws Exception {
            if (queue == null) {
                queue = new PriorityQueue<ScoreDoc>(3) {

                    /* 最终点菜点少了的情况下只推荐三个菜 */
                    @Override
                    protected boolean lessThan(ScoreDoc a, ScoreDoc b) {
                        return b.score > a.score;
                    }
                };
            }
            ScoreDoc e = new ScoreDoc(doc, score);
            queue.insertWithOverflow(e);
            allFound++;
        }

        public int getAllFound() {
            return allFound;
        }

        public ScoreDoc popQueue() {
            if (queue == null) {
                return null;
            }
            return queue.pop();
        }

        // label下的量,点菜量
        private int applySum;

        public void addApplySum(Integer delet) {
            if (delet == null) {
                return;
            }
            this.applySum += delet;
        }

        public int getLabelId() {
            return labelId;
        }

        public RecommendRule(int labelId, LabelType type) {
            super();
            this.labelId = labelId;
            this.type = type;
        }

        public String getKey() {
            return getKey(labelId, type);
        }

        public static String getKey(int labelId, LabelType type) {
            return String.valueOf(labelId) + type;
        }

        private Threshold low;

        private Threshold high;

        private Integer recommandAdd;

        public int recommandAdd() {
            if (recommandAdd == null) {
                recommandAdd = calculateRecommend();
            }
            return recommandAdd;
        }

        protected int calculateRecommend() {
            if (this.low.open && this.applySum < this.low.value) {
                // 少点了返回正數
                return this.low.value - this.applySum;
            }
            if (this.high.open && this.applySum > this.high.value) {
                // 多点了,返回一个负数
                return this.high.value - this.applySum;
            }
            return 0;
        }
    }

    private static class Threshold {

        private boolean open;

        private int value;
    }

    public enum LabelType {

        COMMON(0, /* arrayOffset */
        0),
        /* 普通类目 */
        PRIMARY_SOURCE(1, /* arrayOffset */
        1),
        /* 主料大类 */
        SECONDARY_SOURCE(2, /* arrayOffset */
        2);

        /* 主料小类 */
        private final int value;

        private final int arrayOffset;

        private LabelType(int value, int arrayOffset) {
            this.value = value;
            this.arrayOffset = arrayOffset;
        }

        // public void addApplySum(RecommendRule rule, int[] /* labels */
        // labelsId) {
        // if (this == LabelType.COMMON && rule.labelId ==
        // labelsId[arrayOffset]) {
        // rule.addApplySum(menuCount.get(entry.getKey()/* menuid */));
        // }
        // }
        public static LabelType parse(String value) {
            if (String.valueOf(COMMON.value).equals(value)) {
                return COMMON;
            }
            if (String.valueOf(PRIMARY_SOURCE.value).equals(value)) {
                return PRIMARY_SOURCE;
            }
            if (String.valueOf(SECONDARY_SOURCE.value).equals(value)) {
                return SECONDARY_SOURCE;
            }
            throw new IllegalStateException("TypeValue:" + value + " is not illegal");
        }
    }

    public static LabelType[] LABELS_ID = new LabelType[] { LabelType.COMMON, LabelType.PRIMARY_SOURCE, LabelType.SECONDARY_SOURCE };
}
