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
package com.qlangtech.tis.solrextend.handler.component.s4menu;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DocValues;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.index.SortedDocValues;
import org.apache.lucene.index.SortedNumericDocValues;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.SimpleCollector;
import org.apache.lucene.util.BytesRef;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.common.util.SimpleOrderedMap;
import org.apache.solr.handler.component.ResponseBuilder;
import org.apache.solr.handler.component.SearchComponent;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.response.DocsStreamer;
import org.apache.solr.schema.SchemaField;
import org.apache.solr.search.DocListAndSet;
import org.apache.solr.search.SolrIndexSearcher;
import com.qlangtech.tis.solrextend.fieldtype.s4menu.MenuSpecParserField;
import com.qlangtech.tis.solrextend.queryparse.MenuRecommendQparserPlugin;
import com.qlangtech.tis.solrextend.queryparse.MenuRecommendQparserPlugin.RecommendRule;
import com.qlangtech.tis.solrextend.queryparse.MenuRecommendQparserPlugin.RecommendRules;
import com.qlangtech.tis.solrextend.transformer.s4menu.MultipleMenuTransformer;

/*
 * 智能点菜使用
 *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class RecommendMenuComponent extends SearchComponent {

    private static final String COMPONENT_NAME = "menus-r";

    // private static final Logger LOG =
    // LoggerFactory.getLogger(RecommendMenuComponent.class);
    private static final String DOC_FIELD_NAME = "name";

    public static final String DOC_FIELD_MENU_ID = "id";

    private static final String DOC_FIELD_SERVER = "server";

    private static final String DOC_FIELD_PATH = "main_picture";

    public static final String DOC_FIELD_PRICE = "sku_price_min";

    public static final String DOC_FIELD_MULTIPLE_MENU_INFO = "multiple_menu_info";

    // 菜的辣椒指数(acridLevel)、 ->acrid_level
    // 单位(account、buyAccount)、->buy_account，account
    // 是有关联规格(hasSpec)、->is_special
    // 是否关联做法(hasMake)、->make_count , all_child_make
    // 是否含有加料菜(hasAddition)、->is_additional
    // 菜的描述(memo)、->memo
    // 起点份数(startNum)、->start_num
    // 特色标签() ->special_tag_id
    // 菜的标签 ->menu_spec
    // private static final String DOC_FIELD_ACRID_LEVEL = "acrid_level";
    private static final String DOC_FIELD_BUY_ACCOUNT = "unit_name";

    private static final String DOC_FIELD_ACCOUNT = "account_unit_name";

    // private static final String DOC_FIELD_IS_SPECIAL = "is_special";
    private static final String DOC_FIELD_MAKE_COUNT = "make_count";

    private static final String DOC_FIELD_ALL_CHILD_MAKE = "all_child_make";

    // private static final String DOC_FIELD_IS_ADDITIONAL = "is_additional";
    // private static final String DOC_FIELD_MEMO = "memo";
    private static final String DOC_FIELD_START_NUM = "start_num";

    // private static final String DOC_FIELD_SPECIAL_TAG_ID = "special_tag_id";
    // private static final String DOC_FIELD_MENU_SPEC = "menu_spec";
    // private static final String DOC_FIELD_recommend_level = "recommend_level";
    // 在店铺加载所有菜品的时候用
    private static final Set<String> DOC_LOADER_FIELDS_LOAD_ALL_MENUS = new HashSet<>(Arrays.asList(DOC_FIELD_MULTIPLE_MENU_INFO, DOC_FIELD_NAME, DOC_FIELD_MENU_ID, DOC_FIELD_SERVER, DOC_FIELD_PATH, DOC_FIELD_PRICE, MenuRecommendQparserPlugin.FIELD_KEY_RECOMMEND_LEVEL));

    /**
     * 在只能推荐的时候用
     */
    public static final Set<String> DOC_LOADER_FIELD_RECOMMEND = new HashSet<>(Arrays.asList(DOC_FIELD_MULTIPLE_MENU_INFO, DOC_FIELD_NAME, DOC_FIELD_MENU_ID, DOC_FIELD_SERVER, DOC_FIELD_PATH, DOC_FIELD_PRICE, MenuRecommendQparserPlugin.FIELD_KEY_RECOMMEND_LEVEL, // MenuRecommendQparserPlugin.FIELD_KEY_ACRID_LEVEL,
    DOC_FIELD_BUY_ACCOUNT, DOC_FIELD_ACCOUNT, // DOC_FIELD_IS_ADDITIONAL,
    DOC_FIELD_MAKE_COUNT, // DOC_FIELD_IS_ADDITIONAL,
    DOC_FIELD_ALL_CHILD_MAKE, // DOC_FIELD_MEMO,
    DOC_FIELD_START_NUM, MenuRecommendQparserPlugin.FIELD_KEY_SPECIAL_TAG_ID, MenuRecommendQparserPlugin.FIELD_KEY_MENU_SPEC));

    public static String KEY_RECOMMEND = "recommend";

    @Override
    public String getDescription() {
        return COMPONENT_NAME;
    }

    @Override
    public void prepare(ResponseBuilder rb) throws IOException {
        SolrQueryRequest req = rb.req;
        SolrParams params = req.getParams();
        if (!params.getBool(COMPONENT_NAME, false)) {
            return;
        }
        rb.setNeedDocSet(true);
    }

    @Override
    public void process(ResponseBuilder rb) throws IOException {
        // LOG.debug("process: {}", rb.req.getParams());
        SolrQueryRequest req = rb.req;
        SolrParams params = req.getParams();
        if (!params.getBool(COMPONENT_NAME, false)) {
            return;
        }
        RecommendRules ruleMap = MenuRecommendQparserPlugin.getRecommendRule(req);
        SolrIndexSearcher searcher = req.getSearcher();
        // IndexReader reader = searcher.getIndexReader();
        // SolrQueryResponse rsp = rb.rsp;
        // IndexSchema schema = searcher.getSchema();
        DocListAndSet results = rb.getResults();
        MenusCollector collector = new MenusCollector(ruleMap);
        rb.req.getSearcher().search(results.docSet.getTopFilter(), collector);
        List<Object> lst = new ArrayList<>();
        SimpleOrderedMap<Object> label;
        SimpleOrderedMap<String> menu;
        List<SimpleOrderedMap<String>> menus;
        // 生成结果
        int recommendAdd = 0;
        RecommendRule r = null;
        ScoreDoc e;
        for (Map.Entry<String, RecommendRule> /* label+'_'+type */
        entry : ruleMap.entrySet()) {
            // 需要继续点的
            r = entry.getValue();
            recommendAdd = r.recommandAdd();
            if (recommendAdd == 0) {
                continue;
            }
            // 点多了
            if (recommendAdd < 0) {
                label = new SimpleOrderedMap<>();
                label.add("label", String.valueOf(r.getLabelId()));
                label.add("proposal", String.valueOf(recommendAdd));
                label.add("allfound", "0");
                lst.add(label);
                continue;
            }
            // 点少了
            if (recommendAdd > 0 && r.getAllFound() > 0) {
                label = new SimpleOrderedMap<>();
                label.add("label", String.valueOf(r.getLabelId()));
                label.add("proposal", String.valueOf(recommendAdd));
                label.add("allfound", r.getAllFound());
                menus = new ArrayList<>();
                while ((e = r.popQueue()) != null) {
                    menu = getDocument(searcher, e.doc, params);
                    if (menu == null) {
                        continue;
                    }
                    // document = searcher.doc(e.doc, DOC_LOADER_FIELDS);
                    // if (document == null) {
                    // continue;
                    // }
                    // menu = new SimpleOrderedMap<>();
                    // menu.add(DOC_FIELD_NAME, document.get(DOC_FIELD_NAME));
                    // menu.add(DOC_FIELD_MENU_ID,
                    // document.get(DOC_FIELD_MENU_ID));
                    // menu.add(DOC_FIELD_SERVER,
                    // document.get(DOC_FIELD_SERVER));
                    // menu.add(DOC_FIELD_PATH, document.get(DOC_FIELD_PATH));
                    // menu.add(DOC_FIELD_PRICE, document.get(DOC_FIELD_PRICE));
                    // menu.add(DOC_FIELD_recommend_level,
                    // document.get(DOC_FIELD_recommend_level));
                    menus.add(menu);
                }
                Collections.reverse(menus);
                label.add("menus", menus);
                lst.add(label);
                continue;
            }
        }
        rb.rsp.add(RecommendMenuComponent.KEY_RECOMMEND, lst);
    }

    public static SimpleOrderedMap<String> getDocument(SolrIndexSearcher searcher, int doc, SolrParams params) throws IOException {
        return getDocument(searcher, doc, DOC_LOADER_FIELDS_LOAD_ALL_MENUS, params);
    }

    public static SimpleOrderedMap<String> getDocument(SolrIndexSearcher searcher, int doc, Set<String> needFields, SolrParams params) throws IOException {
        Document document = searcher.doc(doc, needFields);
        if (document == null) {
            return null;
        }
        SolrDocument solrdoc = DocsStreamer.getDoc(document, searcher.getSchema());
        // 多餐单字段转化
        if (params.get(RecommendMenuComponent.DOC_FIELD_MULTIPLE_MENU_INFO) != null) {
            MultipleMenuTransformer fieldTransfer = new MultipleMenuTransformer(params);
            fieldTransfer.transform(solrdoc, doc, 0);
        }
        SimpleOrderedMap<String> menu = new SimpleOrderedMap<>();
        for (String fieldName : needFields) {
            menu.add(fieldName, MultipleMenuTransformer.val(solrdoc.getFirstValue(fieldName)));
        }
        return menu;
    }

    private static class MenusCollector extends SimpleCollector {

        private SortedDocValues menuSpecField;

        private SortedNumericDocValues recommendLevel;

        // private SortedNumericDocValues acridLevel;
        private int docBase;

        private final RecommendRules ruleMap;

        // 需要统计辣味指数？所有推荐的菜都必须要 小于等于这个辣味级别
        private final boolean needCalculateAcridLevel;

        public MenusCollector(RecommendRules ruleMap) {
            this.ruleMap = ruleMap;
            this.needCalculateAcridLevel = ruleMap.getMaxAcridLevel() != null;
        }

        @Override
        public boolean needsScores() {
            return false;
        }

        protected void doSetNextReader(LeafReaderContext context) throws IOException {
            docBase = context.docBase;
            this.menuSpecField = DocValues.getSorted(context.reader(), MenuRecommendQparserPlugin.FIELD_KEY_MENU_SPEC);
            this.recommendLevel = DocValues.getSortedNumeric(context.reader(), MenuRecommendQparserPlugin.FIELD_KEY_RECOMMEND_LEVEL);
            if (needCalculateAcridLevel) {
            // this.acridLevel = DocValues.getSortedNumeric(context.reader(),
            // MenuRecommendQparserPlugin.FIELD_KEY_ACRID_LEVEL);
            }
        }

        @Override
        public void collect(int doc) throws IOException {
            BytesRef byteRef = menuSpecField.get(doc);
            long acridLevel;
            if (byteRef == null) {
                return;
            }
            String labelid = MenuSpecParserField.getLables(byteRef.utf8ToString());
            if (StringUtils.isEmpty(labelid)) {
                return;
            }
            List<Integer> lid = MenuSpecParserField.splitLabels(labelid);
            int lidSize = lid.size();
            String key = null;
            RecommendRule rule = null;
            try {
                for (int i = 0; i < MenuRecommendQparserPlugin.LABELS_ID.length && i < lidSize; i++) {
                    key = RecommendRule.getKey(lid.get(i), MenuRecommendQparserPlugin.LABELS_ID[i]);
                    rule = ruleMap.get(key);
                    if (rule == null || rule.recommandAdd() <= 0) {
                        continue;
                    }
                    // 推荐的菜中需要指定辣味指数，所有推荐的菜都必须要 小于等于这个辣味级别
                    if (needCalculateAcridLevel) {
                    // this.acridLevel.setDocument(doc);
                    // acridLevel = this.acridLevel.count() > 0 ? this.acridLevel.valueAt(0) : 0;
                    // if (acridLevel > ruleMap.getMaxAcridLevel()) {
                    // continue;
                    // }
                    }
                    this.recommendLevel.setDocument(doc);
                    rule.pushPriorityQueue(docBase + doc, this.recommendLevel.count() > 0 ? this.recommendLevel.valueAt(0) : 0);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
