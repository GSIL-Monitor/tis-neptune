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
package com.qlangtech.tis.solrextend.queryparse.s4shop;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.search.Query;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.core.SolrCore;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.search.LuceneQParserPlugin;
import org.apache.solr.search.QParser;
import org.apache.solr.search.SyntaxError;
import org.slf4j.Logger;
import java.util.Arrays;
import java.util.List;

/* *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class ShopDetailQParserPlugin extends LuceneQParserPlugin {

    private static final Logger log = SolrCore.requestLog;

    public QParser createParser(String qstr, SolrParams localParams, SolrParams params, SolrQueryRequest req) {
        // 用于筛选
        // 
        String entityId = localParams.get("entity_id");
        List<String> entityIds = null;
        if (entityId != null) {
            entityIds = Arrays.asList(StringUtils.split(entityId, ","));
        }
        // 
        String brandEntityId = localParams.get("branch_eids");
        List<String> brandEntityIds = null;
        if (brandEntityId != null) {
            brandEntityIds = Arrays.asList(StringUtils.split(brandEntityId, ","));
        }
        // 
        String plateEntityId = localParams.get("plate_eid");
        List<String> plateEntityIds = null;
        if (plateEntityId != null) {
            plateEntityIds = Arrays.asList(StringUtils.split(plateEntityId, ","));
        }
        String name = localParams.get("name");
        String code = localParams.get("code");
        String industry = localParams.get("industry");
        // 
        String presellPlatform = localParams.get("presell_platform");
        List<String> presellPlatforms = null;
        if (presellPlatform != null) {
            presellPlatforms = Arrays.asList(StringUtils.split(presellPlatform, ","));
        }
        String statusFlag = localParams.get("status_flag");
        // 
        String foodStyle = localParams.get("food_style");
        List<String> foodStyles = null;
        if (foodStyle != null) {
            foodStyles = Arrays.asList(StringUtils.split(foodStyle, ","));
        }
        String isTest = localParams.get("is_test");
        Integer maxNum = localParams.getInt("maxNum");
        // 
        String townId = localParams.get("town_id");
        List<String> townIds = null;
        if (townId != null) {
            townIds = Arrays.asList(StringUtils.split(townId, ","));
        }
        final ShopDetailPostFilterQuery q = new ShopDetailPostFilterQuery(entityIds, brandEntityIds, plateEntityIds, name, code, industry, presellPlatforms, statusFlag, foodStyles, isTest, maxNum, townIds);
        return new QParser(qstr, localParams, params, req) {

            @Override
            public Query parse() throws SyntaxError {
                return q;
            }
        };
    }
}
