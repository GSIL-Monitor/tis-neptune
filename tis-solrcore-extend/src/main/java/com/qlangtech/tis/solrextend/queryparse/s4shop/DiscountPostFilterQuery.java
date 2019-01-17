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
import org.slf4j.LoggerFactory;
import redis.clients.jedis.exceptions.JedisException;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/* *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class DiscountPostFilterQuery extends ExtendedQueryBase implements PostFilter {

    private Integer discount;

    private Integer table;

    private String morning;

    private String afternoon;

    private String debugEntityId;

    private Long date;

    private AtomicInteger count;

    private int maxNum;

    private static final Logger log = SolrCore.requestLog;

    public DiscountPostFilterQuery(Integer discount, Long date, Integer table, String morning, String afternoon, String debugEntityId, Integer maxNum) throws IOException {
        this.discount = discount;
        this.table = table;
        this.morning = morning;
        this.afternoon = afternoon;
        this.date = date;
        this.debugEntityId = debugEntityId;
        this.maxNum = maxNum == null ? 2000 : maxNum;
        this.count = new AtomicInteger(1);
    }

    public DelegatingCollector getFilterCollector(IndexSearcher indexSearcher) {
        return new DelegatingCollector() {

            private SortedDocValues docValues;

            @Override
            protected void doSetNextReader(LeafReaderContext context) throws IOException {
                super.doSetNextReader(context);
                this.docValues = DocValues.getSorted(context.reader(), "entity_id");
            }

            @Override
            public void collect(int doc) throws IOException {
                if (debugEntityId != null) {
                    log.info("get a docId:" + doc);
                }
                if (count.incrementAndGet() > maxNum) {
                    throw new RuntimeException("the hit num is " + maxNum + " now, more than the given maxNum");
                }
                String eid = docValues.get(doc).utf8ToString();
                if (debugEntityId != null) {
                    log.info("hit the entity_id: " + eid);
                }
                boolean debugMode = (debugEntityId == null) ? false : StringUtils.equals(eid, debugEntityId);
                if (debugMode) {
                    log.info("match the given entity_id:" + eid);
                }
                JedisFactory.EntityPreSellRule entityPreSellRule = null;
                try {
                    entityPreSellRule = JedisFactory.getEntityPreSellRule(eid);
                } catch (JedisException e) {
                    super.collect(doc);
                    SendSMSUtils.send("Could not return the redis resource to the pool", SendSMSUtils.SUGU_PHONE);
                    log.error(e.toString(), e);
                    return;
                } catch (Exception e) {
                    SendSMSUtils.send("error when get rule from redis", SendSMSUtils.SUGU_PHONE);
                    log.error(e.toString(), e);
                    return;
                }
                if (eid == null || entityPreSellRule == null || entityPreSellRule.isPseudo()) {
                    return;
                }
                if (debugMode) {
                    log.info("\n the valueStr from redis:" + entityPreSellRule.getRedisValue() + "\n" + "tableSet:" + Arrays.toString(entityPreSellRule.tableSet.toArray()) + "\n" + "discountSet:" + Arrays.toString(entityPreSellRule.discountSet.toArray()) + "\n" + "hoursSet:" + Arrays.toString(entityPreSellRule.hoursSet.toArray()) + "\n" + "dateSet:" + Arrays.toString(entityPreSellRule.dateSet.keySet().toArray()));
                }
                if (date != null) {
                    if (!entityPreSellRule.hasDate(date)) {
                        return;
                    } else {
                        JedisFactory.EntityPreSellRule entityPreSellRule2 = entityPreSellRule.getDate(date);
                        if (discount != null && !entityPreSellRule2.hasDiscount(discount)) {
                            return;
                        } else if (table != null && !entityPreSellRule2.hasTable(table)) {
                            return;
                        } else if (morning != null && !entityPreSellRule2.hasMorningPreSell()) {
                            return;
                        } else if (afternoon != null && !entityPreSellRule2.hasAfternoonPreSell()) {
                            return;
                        }
                    }
                } else {
                    if (discount != null && !entityPreSellRule.hasDiscount(discount)) {
                        return;
                    }
                    if (table != null && !entityPreSellRule.hasTable(table)) {
                        return;
                    }
                    if (afternoon != null && !entityPreSellRule.hasAfternoonPreSell()) {
                        return;
                    }
                    if (morning != null && !entityPreSellRule.hasMorningPreSell()) {
                        return;
                    }
                }
                super.collect(doc);
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
