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

import static com.qlangtech.tis.solrextend.queryparse.MenuRecommendQparserPlugin.FIELD_IS_VALID;
import static com.qlangtech.tis.solrextend.queryparse.MenuRecommendQparserPlugin.FIELD_KEY_ENTITY_ID;
import static com.qlangtech.tis.solrextend.queryparse.MenuRecommendQparserPlugin.FIELD_KEY_MENU_SPEC;
import static com.qlangtech.tis.solrextend.queryparse.MenuRecommendQparserPlugin.FIELD_KEY_MP_REAL_ONLY;
import static com.qlangtech.tis.solrextend.queryparse.MenuRecommendQparserPlugin.FIELD_SALE_OUT;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.queries.TermsQuery;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.util.BytesRef;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.schema.IndexSchema;
import org.apache.solr.search.LuceneQParser;
import org.apache.solr.search.QParser;
import org.apache.solr.search.QParserPlugin;
import org.apache.solr.search.SyntaxError;
import com.qlangtech.tis.solrextend.handler.component.AllConsumeDimStatisComponent.FieldColumn;
import com.qlangtech.tis.solrextend.queryparse.MenuRecommendQparserPlugin;
import com.google.common.base.Preconditions;

/*
 * 智能点菜二期，基于模板的点菜，懒人点菜，对应的searchComponent为MenusRecommendByTemplateComponent
 *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class MenusRecommendByTemplateQParserPlugin extends QParserPlugin {

    // LabelTemplate
    public static final String KEY_CONTEXT_LABEL_TEMPLATE = "key_context_label_template";

    // 上下文口味偏好key
    private static final String KEY_CONTEXT_ACRID_PREFERENCES = "key_context_acrid_preferences";

    @Override
    public QParser createParser(String qstr, SolrParams localParams, SolrParams params, SolrQueryRequest req) {
        Integer page = getPage(localParams.getInt("page", 1));
        String entityId = localParams.get("entityid");
        MenuSortCode sortCode = MenuSortCode.getInstance(localParams.getInt("sortcode"));
        // 是否是換一批换一批
        final boolean change = localParams.getBool("change", false);
        // 客户口味偏好，辣味指數
        // localParams.getInt(MenuRecommendQparserPlugin.FIELD_KEY_ACRID_LEVEL, -1);
        int acridLevel = -1;
        // 同时设置了辣味指数，又设置了按照辣味排序，这是不允许的
        if (acridLevel < 0 && sortCode == MenuSortCode.ACRID_LEVEL) {
            acridLevel = Integer.MAX_VALUE;
        }
        req.getContext().put(KEY_CONTEXT_ACRID_PREFERENCES, new AcridPreferences(acridLevel));
        String excludeMenuids = localParams.get("exclude_menuids");
        Preconditions.checkNotNull(entityId, "entityid can not be null");
        LabelTemplateList labelTemps = new LabelTemplateList(sortCode, page, change);
        // List<LabelTemplate> labelTemps = new ArrayList<>();
        LabelTemplate labelTemp = null;
        CategoryRecommend categoryRecommend = null;
        String[] tuples = StringUtils.split(qstr, "|");
        String[] labelTuples = null;
        final Set<String> labels = new HashSet<>();
        for (String labelSubCategory : tuples) {
            labelTuples = StringUtils.split(labelSubCategory, ",");
            // labelId = labelTuples[0];
            Preconditions.checkState(labelTuples.length > 1, "labelTuples:" + labelSubCategory + " length shall big than 1,qstr:" + qstr);
            labelTemp = new LabelTemplate(labelTuples[0]);
            labels.add(labelTemp.labelId);
            for (int i = 1; i < labelTuples.length; i++) {
                categoryRecommend = new CategoryRecommend(sortCode, page);
                for (String subCategoryId : StringUtils.split(labelTuples[i], "_")) {
                    if (StringUtils.endsWith(subCategoryId, "+")) {
                        String suCId = StringUtils.substringBefore(subCategoryId, "+");
                        categoryRecommend.addCategoryOrSubCategory(suCId, true);
                        labels.add(suCId);
                    } else {
                        categoryRecommend.addCategoryOrSubCategory(subCategoryId, false);
                        labels.add(subCategoryId);
                    }
                }
                labelTemp.putCategoryRecommend(categoryRecommend);
            }
            Preconditions.checkArgument(labelTemps.labelTemps.put(labelTemp.labelId, labelTemp) == null, "labelid:" + labelTemp.labelId + " relevate value shall be null");
        }
        req.getContext().put(KEY_CONTEXT_LABEL_TEMPLATE, labelTemps);
        List<BytesRef> /* labelinfo */
        specialLabelInfo = new ArrayList<>();
        for (String c : labels) {
            specialLabelInfo.add(new BytesRef(c));
        }
        final BooleanQuery.Builder qbuilder = new BooleanQuery.Builder();
        TermsQuery query = new TermsQuery(FIELD_KEY_MENU_SPEC, specialLabelInfo);
        qbuilder.add(query, Occur.MUST);
        // 查询中需要将参数中添加的menuids排除掉
        addExcludeMenuidsCriteria(excludeMenuids, qbuilder);
        // LuceneQParser qparser = new LuceneQParser(FIELD_KEY_ENTITY_ID + ":" + entityId + " AND -"
        // + FIELD_KEY_MP_REAL_ONLY + ":1 AND -" + FIELD_SALE_OUT + ":1 AND " + FIELD_IS_VALID
        // + ":1 AND is_reserve:1 AND -" + FIELD_IS_SELF + ":0", null, params, req);
        LuceneQParser qparser = new LuceneQParser(FIELD_KEY_ENTITY_ID + ":" + entityId + " AND -" + FIELD_KEY_MP_REAL_ONLY + ":1 AND -" + FIELD_SALE_OUT + ":1 AND " + FIELD_IS_VALID + ":1 ", null, params, req);
        try {
            qbuilder.add(qparser.parse(), Occur.FILTER);
        } catch (SyntaxError e) {
            throw new RuntimeException(e);
        }
        final BooleanQuery bquery = qbuilder.build();
        return new QParser(qstr, localParams, params, req) {

            @Override
            public Query parse() throws SyntaxError {
                return bquery;
            // return LangAwareLuceneQParserPlugin.mergeI18NQuery(req.getSearcher(), localParams,
            // bquery);
            }
        };
    }

    private void addExcludeMenuidsCriteria(String excludeMenuids, final BooleanQuery.Builder qbuilder) {
        if (StringUtils.isNotEmpty(excludeMenuids)) {
            List<BytesRef> /* menuid */
            menuids = new ArrayList<>();
            for (String c : StringUtils.split(excludeMenuids, ",")) {
                menuids.add(new BytesRef(c));
            }
            qbuilder.add(new TermsQuery(MenuRecommendQparserPlugin.FIELD_KEY_MENU_ID, menuids), Occur.MUST_NOT);
        }
    }

    @SuppressWarnings("all")
    public static LabelTemplateList getLabelTemplate(SolrQueryRequest req) {
        return (LabelTemplateList) req.getContext().get(KEY_CONTEXT_LABEL_TEMPLATE);
    }

    @SuppressWarnings("all")
    public static AcridPreferences getAcridPreferencesColumnGetter(SolrQueryRequest req) {
        AcridPreferences acridPreferences = (AcridPreferences) req.getContext().get(KEY_CONTEXT_ACRID_PREFERENCES);
        if (acridPreferences == null) {
            throw new IllegalStateException("acridPreferences can not be null in request context");
        }
        acridPreferences.setFieldColumn(req);
        return acridPreferences;
    }

    @Override
    @SuppressWarnings("all")
    public void init(NamedList args) {
    }

    private int getPage(int page) {
        // }
        if (page < 1) {
            return 1;
        }
        return page;
    }

    public static class LabelTemplateList {

        private final MenuSortCode sortCode;

        private final Map<String, LabelTemplate> /* lableId */
        labelTemps = new HashMap<>();

        private final int page;

        private final boolean change;

        public FieldColumn getSortColumn(IndexSchema schema) {
            return sortCode.getFieldColumn(schema);
        }

        public int getPage() {
            return page;
        }

        public Map<String, /* lableId */
        LabelTemplate> getLableTemplate() {
            return this.labelTemps;
        }

        /**
         * @param sortCode
         * @param page
         * @param change
         *            是否是换一批
         */
        public LabelTemplateList(MenuSortCode sortCode, int page, boolean change) {
            super();
            this.sortCode = sortCode;
            this.page = page;
            this.change = change;
        }

        public LabelTemplate getLableTemplate(String lableId) {
            return labelTemps.get(lableId);
        }

        public float getFieldValue(FieldColumn column, int docid) {
            return sortCode.getValue(column, change, docid);
        }
    }

    public static class LabelTemplate {

        private final String labelId;

        public LabelTemplate(String labelId) {
            super();
            this.labelId = labelId;
        }

        private final List<CategoryRecommend> categoryList = new ArrayList<>();

        private final Map<String, List<CategoryRecommend>> /* category or subcategory */
        categoryMap = new HashMap<>();

        private void putCategoryRecommend(CategoryRecommend categoryRecommend) {
            List<CategoryRecommend> crList = null;
            categoryList.add(categoryRecommend);
            for (CategoryidORsubCategory c : categoryRecommend.getCategories()) {
                crList = categoryMap.get(c.getId());
                if (crList == null) {
                    crList = new ArrayList<CategoryRecommend>();
                    categoryMap.put(c.getId(), crList);
                }
                crList.add(categoryRecommend);
            }
        }

        public List<CategoryRecommend> getCategoryList() {
            return this.categoryList;
        }

        /**
         * @param doc
         * @param categoryId
         *            值可能是 ：label，主要大类，主料小类
         * @param sortScore
         * @throws IOException
         */
        public void push2SortQueue(int doc, Integer categoryId, float sortScore) throws IOException {
            List<CategoryRecommend> recommends = this.getCategoryRecommend(String.valueOf(categoryId));
            if (recommends == null) {
                return;
            }
            try {
                for (CategoryRecommend c : recommends) {
                    c.pushPriorityQueue(doc, sortScore, categoryId);
                }
            } catch (Exception e) {
                throw new IOException(e);
            }
        }

        private List<CategoryRecommend> getCategoryRecommend(String categoryid) {
            return categoryMap.get(categoryid);
        }
    }
}
