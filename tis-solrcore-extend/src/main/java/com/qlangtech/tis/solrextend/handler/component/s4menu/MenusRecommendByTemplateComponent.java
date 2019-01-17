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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.SimpleCollector;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.common.util.SimpleOrderedMap;
import org.apache.solr.handler.component.ResponseBuilder;
import org.apache.solr.handler.component.SearchComponent;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.search.DocListAndSet;
import org.apache.solr.search.SolrIndexSearcher;
import com.qlangtech.tis.solrextend.handler.component.AllConsumeDimStatisComponent.FieldColumn;
import com.qlangtech.tis.solrextend.queryparse.MenuRecommendQparserPlugin;
import com.qlangtech.tis.solrextend.queryparse.s4menu.AcridPreferences;
import com.qlangtech.tis.solrextend.queryparse.s4menu.CategoryRecommend;
import com.qlangtech.tis.solrextend.queryparse.s4menu.MenusRecommendByTemplateQParserPlugin;
import com.qlangtech.tis.solrextend.queryparse.s4menu.MenusRecommendByTemplateQParserPlugin.LabelTemplate;
import com.qlangtech.tis.solrextend.queryparse.s4menu.MenusRecommendByTemplateQParserPlugin.LabelTemplateList;

/*
 * 一键只能模板点餐
 *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class MenusRecommendByTemplateComponent extends SearchComponent {

    private static final String COMPONENT_NAME = "menus-tpl";

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
        SolrQueryRequest req = rb.req;
        SolrParams params = req.getParams();
        if (!params.getBool(COMPONENT_NAME, false)) {
            return;
        }
        final LabelTemplateList labelTpl = MenusRecommendByTemplateQParserPlugin.getLabelTemplate(req);
        SolrIndexSearcher searcher = req.getSearcher();
        FieldColumn menuSpecField = new FieldColumn(req.getSchema().getField(MenuRecommendQparserPlugin.FIELD_KEY_MENU_SPEC));
        AcridPreferences acridPreferences = MenusRecommendByTemplateQParserPlugin.getAcridPreferencesColumnGetter(req);
        // req.getContext().get(MenusRecommendByTemplateQParserPlugin.KEY_CONTEXT_ACRID_PREFERENCES);
        // IndexReader reader = searcher.getIndexReader();
        DocListAndSet results = rb.getResults();
        MenusTplCollector collector = new MenusTplCollector(labelTpl, menuSpecField, labelTpl.getSortColumn(req.getSchema()), acridPreferences);
        rb.req.getSearcher().search(results.docSet.getTopFilter(), collector);
        List<SimpleOrderedMap<Object>> result = new ArrayList<>();
        SimpleOrderedMap<Object> labelMenus = null;
        List<SimpleOrderedMap<String>> menus = null;
        SimpleOrderedMap<String> menu = null;
        LabelTemplate labTpl = null;
        final Set<Integer> filterDocSet = new HashSet<>();
        ScoreDoc doc = null;
        for (Map.Entry<String, LabelTemplate> /* lableId */
        entry : labelTpl.getLableTemplate().entrySet()) {
            labelMenus = new SimpleOrderedMap<Object>();
            labelMenus.add("label", entry.getKey());
            menus = new ArrayList<>();
            labTpl = entry.getValue();
            for (CategoryRecommend recommend : labTpl.getCategoryList()) {
                doc = recommend.popQueue(filterDocSet);
                if (doc == null) {
                    continue;
                }
                menu = RecommendMenuComponent.getDocument(searcher, doc.doc, RecommendMenuComponent.DOC_LOADER_FIELD_RECOMMEND, params);
                if (menu == null) {
                    continue;
                }
                menu.add("labelinfo", recommend.getCategoriesDesc());
                menu.add("allmatch", String.valueOf(recommend.getAllMatchCount()));
                menu.add("page", String.valueOf(recommend.getPage()));
                menus.add(menu);
            }
            labelMenus.add("menus", menus);
            result.add(labelMenus);
        }
        rb.rsp.add(RecommendMenuComponent.KEY_RECOMMEND, result);
        rb.rsp.add(RecommendMenuComponent.KEY_RECOMMEND + "-page", labelTpl.getPage());
    }

    private static class MenusTplCollector extends SimpleCollector {

        // private SortedDocValues menuSpecField;
        // private SortedNumericDocValues recommendLevel;
        private int docBase;

        private final LabelTemplateList labelTpl;

        private final FieldColumn menuSpecField;

        private final FieldColumn sortColumn;

        private final AcridPreferences acridPreferences;

        /**
         * @param labelTpl
         * @param menuSpecField
         * @param sortColumn
         * @param acridColumn
         *            口味偏好列
         */
        public MenusTplCollector(LabelTemplateList labelTpl, FieldColumn menuSpecField, FieldColumn sortColumn, AcridPreferences acridPreferences) {
            this.labelTpl = labelTpl;
            this.menuSpecField = menuSpecField;
            this.sortColumn = sortColumn;
            this.acridPreferences = acridPreferences;
        }

        @Override
        public boolean needsScores() {
            return false;
        }

        protected void doSetNextReader(LeafReaderContext context) throws IOException {
            this.docBase = context.docBase;
            this.menuSpecField.doSetNextReader(context);
            this.sortColumn.doSetNextReader(context);
            this.acridPreferences.doSetNextReader(context);
        }

        @Override
        public void collect(int doc) throws IOException {
            String menuSpec = menuSpecField.getString(doc);
            List<Integer> specLabels = MenuRecommendQparserPlugin.getMenuSpecLabel(menuSpec);
            int size = specLabels.size();
            // 规格长度必须等于3
            if (size < 1) {
                return;
            }
            String labelId = String.valueOf(specLabels.get(0));
            LabelTemplate labelTemplate = labelTpl.getLableTemplate(labelId);
            if (labelTemplate == null) {
                return;
            }
            float sortScore = labelTpl.getFieldValue(sortColumn, doc);
            // 是否需要按照辣味偏好来过滤
            if (this.acridPreferences.filter(doc)) {
                return;
            }
            for (Integer token : specLabels) {
                labelTemplate.push2SortQueue(docBase + doc, token, sortScore);
            }
        // // 主料大类
        // Integer categoryId = specLabels.get(1);
        // // 主料小类
        // Integer subCategoryId = specLabels.get(2);
        // 
        // 
        // labelTemplate.push2SortQueue(docBase + doc, subCategoryId,
        // sortScore);
        }
        // protected void push2SortQueue(int doc, Integer categoryId,
        // LabelTemplate labelTemplate)
        // throws IOException {
        // List<CategoryRecommend> recommends = labelTemplate
        // .getCategoryRecommend(String.valueOf(categoryId));
        // try {
        // for (CategoryRecommend c : recommends) {
        // c.pushPriorityQueue(docBase + doc, labelTpl.getFieldValue(sortColumn,
        // doc));
        // }
        // } catch (Exception e) {
        // throw new IOException(e);
        // }
        // }
    }

    @Override
    public String getDescription() {
        return COMPONENT_NAME;
    }
}
