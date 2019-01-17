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

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.common.cache.AbstractLoadingCache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;
import com.qlangtech.tis.manage.common.SendSMSUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisSentinelPool;
import redis.clients.jedis.Protocol;

/*
 * 取得redis实例
 *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class JedisFactory {

    private static JedisFactory jedisFactory;

    private static final Logger log = LoggerFactory.getLogger(DiscountPostFilterQuery.class);

    private final LoadingCache<String, EntityPreSellRule> preSellRuleCache;

    private JedisFactory() {
        // presell-stock.redis.cache.host=${presell-stock.redis.cache.host}
        // presell-stock.redis.cache.masterName=${presell-stock.redis.cache.masterName}
        // presell-stock.redis.cache.db=${presell-stock.redis.cache.db}
        this.preSellRuleCache = this.createRandomLoadingCache();
        ResourceBundle bundle = ResourceBundle.getBundle("com/tis/s4shop/config");
        this.setSentinels(bundle.getString("presell-stock.redis.cache.host"));
        this.setMaster(bundle.getString("presell-stock.redis.cache.masterName"));
        this.setDb(Integer.parseInt(bundle.getString("presell-stock.redis.cache.db")));
        try {
            this.jedis = this.init();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 取得店铺实体預售規則
     *
     * @param entityid
     * @return
     */
    public static EntityPreSellRule getEntityPreSellRule(String entityid) throws Exception {
        return getRedis().preSellRuleCache.get(entityid);
    }

    private static final JedisFactory getRedis() {
        if (jedisFactory == null) {
            synchronized (JedisFactory.class) {
                if (jedisFactory == null) {
                    jedisFactory = new JedisFactory();
                }
            }
        }
        return jedisFactory;
    }

    private final JedisSentinelPool jedis;

    private static final EntityPreSellRule NULL_RULE = new EntityPreSellRule(true);

    private static final int NUM_SEGMENT = 60;

    private final LoadingCache<String, EntityPreSellRule> createRandomLoadingCache() {
        final CacheLoader<String, EntityPreSellRule> cahceLoader = new CacheLoader<String, EntityPreSellRule>() {

            @Override
            public EntityPreSellRule load(String key) throws Exception {
                Jedis j = jedis.getResource();
                try {
                    EntityPreSellRule presellRule = null;
                    EntityPreSellRule dateSecIndex = null;
                    byte[] val = j.get(key.getBytes());
                    if (val == null) {
                        return NULL_RULE;
                    } else {
                        presellRule = new EntityPreSellRule(false);
                        String v = new String(val, "utf8");
                        String[] content = StringUtils.split(v, ",");
                        long time;
                        presellRule.redisValue = v;
                        String rule = null;
                        String[] ruleArray = null;
                        String[] rArray = null;
                        String[] datePair = null;
                        int minDiscount = 101;
                        for (int i = 0; i < content.length; i = i + 1) {
                            try {
                                datePair = StringUtils.split(content[i], ":");
                                if (datePair.length < 2) {
                                    continue;
                                }
                                time = Long.parseLong(datePair[0]);
                                dateSecIndex = new EntityPreSellRule(false);
                                presellRule.dateSet.put(time, dateSecIndex);
                                // 1528387200000:4_16_100：从左到右：桌位类型，时段，折扣
                                rule = datePair[1];
                                ruleArray = StringUtils.split(rule, ";");
                                for (int jj = 0; jj < ruleArray.length; jj++) {
                                    rArray = StringUtils.split(ruleArray[jj], "_");
                                    if (rArray.length < 3) {
                                        continue;
                                    }
                                    presellRule.tableSet.add(Integer.parseInt(rArray[0]));
                                    presellRule.hoursSet.add(Integer.parseInt(rArray[1]));
                                    presellRule.discountSet.add(Integer.parseInt(rArray[2]));
                                    minDiscount = Math.min(Integer.parseInt(rArray[2]), minDiscount);
                                    dateSecIndex.tableSet.add(Integer.parseInt(rArray[0]));
                                    dateSecIndex.hoursSet.add(Integer.parseInt(rArray[1]));
                                    dateSecIndex.discountSet.add(Integer.parseInt(rArray[2]));
                                }
                                presellRule.setMinDiscount(minDiscount);
                            } catch (Exception e) {
                                log.error(e.toString(), e);
                                SendSMSUtils.send("parse an error rule from entity_id:" + key + " v:" + rule, SendSMSUtils.SUGU_PHONE);
                                continue;
                            }
                        }
                    }
                    return presellRule;
                } finally {
                    try {
                        if (j != null) {
                            j.close();
                        }
                    } catch (Throwable e) {
                    }
                }
            }
        };
        List<LoadingCache<String, EntityPreSellRule>> caches = Lists.newArrayList();
        for (int i = 0; i < NUM_SEGMENT; i++) {
            caches.add(CacheBuilder.newBuilder().expireAfterAccess(40 + i, TimeUnit.SECONDS).build(cahceLoader));
        }
        return new AbstractLoadingCache<String, EntityPreSellRule>() {

            @Override
            public EntityPreSellRule get(String key) throws ExecutionException {
                LoadingCache<String, EntityPreSellRule> c = getSegmentCache(caches, key);
                return c.get(key);
            }

            private LoadingCache<String, EntityPreSellRule> getSegmentCache(List<LoadingCache<String, EntityPreSellRule>> caches, String key) throws WrapperExecutionException {
                int offset = keyOffset(key);
                LoadingCache<String, EntityPreSellRule> c = caches.get(offset);
                if (c == null) {
                    throw new WrapperExecutionException("offset " + offset + " relevant cache can not be null");
                }
                return c;
            }

            private int keyOffset(String key) {
                return Math.abs(key.hashCode() % NUM_SEGMENT);
            }

            @Override
            public EntityPreSellRule getIfPresent(Object key) {
                try {
                    LoadingCache<String, EntityPreSellRule> c = getSegmentCache(caches, String.valueOf(key));
                    return c.getIfPresent(key);
                } catch (WrapperExecutionException e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

    private static class WrapperExecutionException extends ExecutionException {

        private static final long serialVersionUID = 1L;

        public WrapperExecutionException(String message) {
            super(message);
        }
    }

    public String getMaster() {
        return master;
    }

    public void setMaster(String master) {
        this.master = master;
    }

    public int getDb() {
        return db;
    }

    public void setDb(int db) {
        this.db = db;
    }

    public String getSentinels() {
        return sentinels;
    }

    public void setSentinels(String sentinels) {
        this.sentinels = sentinels;
    }

    private String master;

    private int db;

    private String sentinels;

    private JedisSentinelPool init() throws Exception {
        // String sentinels = "10.1.6.20:26379";
        Set<String> sentinelset = new HashSet<>(Arrays.asList(sentinels.split(",")));
        GenericObjectPoolConfig config = new GenericObjectPoolConfig();
        config.setMaxIdle(20);
        config.setMinIdle(20);
        config.setMaxTotal(200);
        config.setTimeBetweenEvictionRunsMillis(60000L);
        config.setMaxWaitMillis(30000);
        config.setTestOnBorrow(true);
        JedisSentinelPool pool = new JedisSentinelPool(master, sentinelset, config, Protocol.DEFAULT_TIMEOUT * 5, null, db);
        return pool;
    }

    public static class EntityPreSellRule {

        private final boolean pseudo;

        private int minDiscount;

        private String redisValue;

        EntityPreSellRule(boolean pseudo) {
            this.pseudo = pseudo;
        }

        public boolean isPseudo() {
            return pseudo;
        }

        public final Map<Long, EntityPreSellRule> dateSet = new HashMap<>();

        public final Set<Integer> tableSet = new HashSet<>();

        public final Set<Integer> discountSet = new HashSet<>();

        public final Set<Integer> hoursSet = new HashSet<>();

        public String getRedisValue() {
            return redisValue;
        }

        public void setMinDiscount(int minDiscount) {
            this.minDiscount = minDiscount;
        }

        public int getMinDiscount() {
            return minDiscount;
        }

        /**
         * 是否有这样的折扣
         *
         * @param val
         * @return
         */
        public boolean hasDiscount(int val) {
            return this.discountSet.contains(val);
        }

        /**
         * 是否有这样的桌子类型
         *
         * @param val
         * @return
         */
        public boolean hasTable(int val) {
            return tableSet.contains(val);
        }

        public boolean hasDate(long val) {
            return dateSet.keySet().contains(val);
        }

        public EntityPreSellRule getDate(long val) {
            return dateSet.get(val);
        }

        /**
         * 是否有早上的
         *
         * @return
         */
        public boolean hasMorningPreSell() {
            for (Integer hour : hoursSet) {
                if (hour < 12) {
                    return true;
                }
            }
            return false;
        }

        /**
         * 是否有下午的
         *
         * @return
         */
        public boolean hasAfternoonPreSell() {
            for (Integer hour : hoursSet) {
                if (hour >= 12) {
                    return true;
                }
            }
            return false;
        }
    }

    public static void main(String[] args) {
        SimpleDateFormat f = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        System.out.println(f.format(1528473600000l));
        long dayMinSec = (1000 * 60 * 60 * 24);
        System.out.println((System.currentTimeMillis() / dayMinSec) * dayMinSec);
    }
}
