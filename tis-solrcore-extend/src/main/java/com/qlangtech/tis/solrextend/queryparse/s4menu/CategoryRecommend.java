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

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.util.PriorityQueue;
import com.google.common.base.Joiner;
import com.google.common.collect.Maps;

/* *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class CategoryRecommend {

    private final Set<CategoryidORsubCategory> categories = new HashSet<>();

    private final Map<Integer, CategoryidORsubCategory> /* categoryid */
    categoriesMap = Maps.newHashMap();

    // 推荐的docid
    private PriorityQueue<ScoreDoc> queue;

    // 所有命中的条数
    private int allMatchCount;

    private final int targetPage;

    private int returnPage;

    private static final Joiner joiner;

    static {
        joiner = Joiner.on('_');
        joiner.skipNulls();
    }

    public Collection<CategoryidORsubCategory> getCategories() {
        return this.categories;
    }

    public void addCategoryOrSubCategory(String categoryid, boolean emphasis) {
        CategoryidORsubCategory category = new CategoryidORsubCategory(categoryid, emphasis);
        this.categories.add(category);
        this.categoriesMap.put(Integer.parseInt(categoryid), category);
    }

    public int getAllMatchCount() {
        return allMatchCount;
    }

    private final MenuSortCode sortCode;

    public String getCategoriesDesc() {
        return joiner.join(categories);
    }

    public CategoryRecommend(MenuSortCode sortCode, int page) {
        super();
        this.sortCode = sortCode;
        this.targetPage = page;
    }

    public int getPage() {
        return this.returnPage;
    }

    public ScoreDoc popQueue(Set<Integer> filterDocSet) {
        return popQueue(filterDocSet, 1, /* page */
        null);
    }

    /**
     * 从已经排序好的队列中（已经是排序好的） 返回并删除一个推荐菜
     *
     * @param filterDocSet
     * @param currentPage
     * @param pre
     * @return
     */
    private ScoreDoc popQueue(Set<Integer> filterDocSet, int currentPage, ScoreDoc pre) {
        if (queue == null) {
            return null;
        }
        ScoreDoc scoreDoc = queue.pop();
        if (scoreDoc == null) {
            // return pre; 这样会导致重复
            return null;
        }
        if (filterDocSet.contains(scoreDoc.doc) || currentPage < (targetPage % allMatchCount)) {
            return popQueue(filterDocSet, ++currentPage, scoreDoc);
        } else {
            filterDocSet.add(scoreDoc.doc);
            this.returnPage = currentPage;
            return scoreDoc;
        }
    }

    public void pushPriorityQueue(int doc, float score, Integer categoryId) throws Exception {
        if (queue == null) {
            queue = new PriorityQueue<ScoreDoc>(20) {

                @Override
                protected boolean lessThan(ScoreDoc a, ScoreDoc b) {
                    return sortCode.compare(a.score, b.score);
                }
            };
        }
        allMatchCount++;
        // ▼▼▼ 判断是否设置了口味偏好
        CategoryidORsubCategory category = categoriesMap.get(categoryId);
        if (category != null && category.isEmphasis()) /* 是否设置了口味偏好 */
        {
            // 设置上一个加权值，让它的分值绝对高
            score *= 100;
        }
        // ▲▲▲
        ScoreDoc e = new ScoreDoc(doc, score);
        queue.insertWithOverflow(e);
    }
}
