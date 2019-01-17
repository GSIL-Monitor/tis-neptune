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

import com.qlangtech.tis.manage.common.SendSMSUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.index.DocValues;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.index.SortedDocValues;
import org.apache.lucene.search.IndexSearcher;
import org.apache.solr.core.SolrCore;
import org.apache.solr.search.DelegatingCollector;
import org.apache.solr.search.ExtendedQueryBase;
import org.apache.solr.search.PostFilter;
import org.slf4j.Logger;
import redis.clients.jedis.exceptions.JedisException;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class ShopDetailPostFilterQuery extends ExtendedQueryBase implements PostFilter {

    List<String> entityIds;

    List<String> brandEntityIds;

    List<String> plateEntityIds;

    String name;

    String code;

    String industry;

    List<String> presellPlatforms;

    String statusFlag;

    List<String> foodStyles;

    String isTest;

    private int maxNum;

    private List<String> townIds;

    private AtomicInteger count;

    private static final Logger log = SolrCore.requestLog;

    public ShopDetailPostFilterQuery(List<String> entityIds, List<String> brandEntityIds, List<String> plateEntityIds, String name, String code, String industry, List<String> presellPlatforms, String statusFlag, List<String> foodStyles, String isTest, Integer maxNum, List<String> townIds) {
        this.entityIds = entityIds;
        this.brandEntityIds = brandEntityIds;
        this.plateEntityIds = plateEntityIds;
        this.name = name;
        this.code = code;
        this.industry = industry;
        this.presellPlatforms = presellPlatforms;
        this.statusFlag = statusFlag;
        this.foodStyles = foodStyles;
        this.isTest = isTest;
        this.maxNum = maxNum == null ? 9999999 : maxNum;
        this.count = new AtomicInteger(1);
        this.townIds = townIds;
    }

    public DelegatingCollector getFilterCollector(IndexSearcher indexSearcher) {
        Set<String> collectedEids = new HashSet<>();
        return new DelegatingCollector() {

            private SortedDocValues docValues;

            @Override
            protected void doSetNextReader(LeafReaderContext context) throws IOException {
                super.doSetNextReader(context);
                this.docValues = DocValues.getSorted(context.reader(), "entity_id");
            }

            @Override
            public void collect(int doc) throws IOException {
                String eid = docValues.get(doc).utf8ToString();
                if (collectedEids.contains(eid) || entityIds != null && !entityIds.contains(eid)) {
                    return;
                }
                if (count.incrementAndGet() > maxNum) {
                    throw new RuntimeException("the hit num is " + maxNum + " now, more than the given maxNum");
                }
                ShopDetailFactory.ShopDetail shopDetail = null;
                try {
                    shopDetail = ShopDetailFactory.getShopDetail(eid);
                } catch (Exception e) {
                    SendSMSUtils.send("has an error when get shopDetail from tis", SendSMSUtils.SUGU_PHONE);
                    log.error(e.toString(), e);
                    return;
                }
                if (eid == null || shopDetail == null || shopDetail.isPseudo()) {
                    return;
                }
                Map<String, String> fieldValue = shopDetail.getFieldValue();
                if (brandEntityIds != null && !matchValueFromTisByName(fieldValue, ShopDetailFactory.ShopDetail.BrandEntityId, brandEntityIds)) {
                    return;
                }
                if (plateEntityIds != null && !matchValueFromTisByName(fieldValue, ShopDetailFactory.ShopDetail.PlateEntityId, plateEntityIds)) {
                    return;
                }
                // name 和 code 都不满足
                if (name != null && code != null && !matchValueFromTisByName(fieldValue, ShopDetailFactory.ShopDetail.Name, name) && !matchValueFromTisByName(fieldValue, ShopDetailFactory.ShopDetail.Code, code)) {
                    return;
                }
                if (industry != null && !matchValueFromTisByName(fieldValue, ShopDetailFactory.ShopDetail.Industry, industry)) {
                    return;
                }
                if (presellPlatforms != null && !matchValueFromTisByName(fieldValue, ShopDetailFactory.ShopDetail.PresellPlatform, presellPlatforms)) {
                    return;
                }
                if (statusFlag != null && !matchValueFromTisByName(fieldValue, ShopDetailFactory.ShopDetail.StatusFlag, statusFlag)) {
                    return;
                }
                if (foodStyles != null && !matchValueFromTisByName(fieldValue, ShopDetailFactory.ShopDetail.FoodStyle, foodStyles)) {
                    return;
                }
                if (isTest != null && !matchValueFromTisByName(fieldValue, ShopDetailFactory.ShopDetail.IsTest, isTest)) {
                    return;
                }
                if (townIds != null && !matchValueFromTisByName(fieldValue, ShopDetailFactory.ShopDetail.TownId, townIds)) {
                    return;
                }
                super.collect(doc);
                collectedEids.add(eid);
            }

            private boolean matchValueFromTisByName(Map<String, String> fieldValue, String fieldName, List<String> givenValues) {
                String separatorChar = ",";
                /**
                 * 标志是不是对于该字段的值和tis中的值，是不是or处理,即只要有一个值满足即可
                 */
                boolean isORField = false;
                if (StringUtils.equals(fieldName, ShopDetailFactory.ShopDetail.PlateEntityId) || StringUtils.equals(fieldName, ShopDetailFactory.ShopDetail.BrandEntityId)) {
                    separatorChar = "\005";
                }
                if (StringUtils.equals(fieldName, ShopDetailFactory.ShopDetail.FoodStyle)) {
                    isORField = true;
                }
                List<String> tisValues = Arrays.asList(StringUtils.split(fieldValue.get(fieldName), separatorChar));
                for (String val : givenValues) {
                    if (tisValues.contains(val) && isORField) {
                        return true;
                    } else if (!tisValues.contains(val) && !isORField) {
                        return false;
                    }
                }
                if (isORField && tisValues.size() == 0) {
                    return false;
                }
                return true;
            }

            private boolean matchValueFromTisByName(String tisValue, String givenValue) {
                String binaryVal = Integer.toBinaryString(Integer.valueOf(tisValue));
                StringBuilder stringBuilder = new StringBuilder();
                int valLen = givenValue.length();
                for (int i = 0; i < valLen - binaryVal.length(); i++) {
                    stringBuilder.append("0");
                }
                String x = stringBuilder.append(binaryVal).toString();
                String val = x.substring(x.length() - valLen, x.length());
                assert val.length() == givenValue.length();
                for (int i = 0; i < givenValue.length(); i++) {
                    if (givenValue.charAt(i) != '*' && (givenValue.charAt(i) != val.charAt(i))) {
                        return false;
                    }
                }
                return true;
            }

            private boolean matchValueFromTisByName(Map<String, String> fieldValue, String fieldName, String givenValue) {
                if (StringUtils.equals(fieldName, ShopDetailFactory.ShopDetail.StatusFlag)) {
                    String tisValue = fieldValue.get(fieldName);
                    return matchValueFromTisByName(tisValue, givenValue);
                }
                return StringUtils.contains(fieldValue.get(fieldName), givenValue);
            }
        };
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj;
    }

    @Override
    public boolean getCache() {
        return false;
    }

    @Override
    public int getCost() {
        return Math.max(super.getCost(), 100);
    }
}
