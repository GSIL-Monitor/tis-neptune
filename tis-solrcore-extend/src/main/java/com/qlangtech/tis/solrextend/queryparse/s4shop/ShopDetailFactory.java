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

import com.qlangtech.tis.solrj.extend.TisCloudSolrClient;
import com.google.common.cache.AbstractLoadingCache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;
import com.qlangtech.tis.common.utils.TSearcherConfigFetcher;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/* *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class ShopDetailFactory {

    private String COLLECTION_NAME = "search4shop";

    private String ROUTER_ID = "entity_id";

    private static ShopDetailFactory shopDetailFactory;

    private static TisCloudSolrClient tisClient;

    // private static final Logger log =
    // LoggerFactory.getLogger(DiscountPostFilterQuery.class);
    private final LoadingCache<String, ShopDetail> shopDetailCache;

    private ShopDetailFactory() {
        this.shopDetailCache = this.createRandomLoadingCache();
        // ResourceBundle bundle = ResourceBundle.getBundle("com/tis/s4shop/config");
        // String zkhost = bundle.getString("presell-stock.zkaddress");
        String zkhost = TSearcherConfigFetcher.get().getZkAddress();
        tisClient = new TisCloudSolrClient(zkhost);
    }

    /**
     * 取得店铺实体信息
     *
     * @param entityid
     * @return
     */
    public static ShopDetail getShopDetail(String entityid) throws Exception {
        return getShopDetailFactory().shopDetailCache.get(entityid);
    }

    private static final ShopDetailFactory getShopDetailFactory() {
        if (shopDetailFactory == null) {
            synchronized (ShopDetailFactory.class) {
                if (shopDetailFactory == null) {
                    shopDetailFactory = new ShopDetailFactory();
                }
            }
        }
        return shopDetailFactory;
    }

    private static final ShopDetail NULL_SHOPDETAIL = new ShopDetail(true);

    private static final int NUM_SEGMENT = 60;

    private final LoadingCache<String, ShopDetail> createRandomLoadingCache() {
        final CacheLoader<String, ShopDetail> cahceLoader = new CacheLoader<String, ShopDetail>() {

            @Override
            public ShopDetail load(String key) throws Exception {
                // SolrQuery query = new SolrQuery("entity_id:" + key);
                // QueryResponse queryResponse = tisClient.query(COLLECTION_NAME, ROUTER_ID, query);
                SolrDocument document = tisClient.getById(COLLECTION_NAME, key, ROUTER_ID);
                ShopDetail shopDetail;
                if (document == null) {
                    return NULL_SHOPDETAIL;
                } else {
                    shopDetail = new ShopDetail(false);
                    shopDetail.initFieldMap(document);
                }
                // }
                return shopDetail;
            }
        };
        List<LoadingCache<String, ShopDetail>> caches = Lists.newArrayList();
        for (int i = 0; i < NUM_SEGMENT; i++) {
            caches.add(CacheBuilder.newBuilder().expireAfterAccess(60 + i, TimeUnit.SECONDS).build(cahceLoader));
        }
        return new AbstractLoadingCache<String, ShopDetail>() {

            @Override
            public ShopDetail get(String key) throws ExecutionException {
                LoadingCache<String, ShopDetail> c = getSegmentCache(caches, key);
                return c.get(key);
            }

            private LoadingCache<String, ShopDetail> getSegmentCache(List<LoadingCache<String, ShopDetail>> caches, String key) throws WrapperExecutionException {
                int offset = keyOffset(key);
                LoadingCache<String, ShopDetail> c = caches.get(offset);
                if (c == null) {
                    throw new WrapperExecutionException("offset " + offset + " relevant cache can not be null");
                }
                return c;
            }

            private int keyOffset(String key) {
                return Math.abs(key.hashCode() % NUM_SEGMENT);
            }

            @Override
            public ShopDetail getIfPresent(Object key) {
                try {
                    LoadingCache<String, ShopDetail> c = getSegmentCache(caches, String.valueOf(key));
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

    public static class ShopDetail {

        public static final Set<String> FilterFields;

        public static final Set<String> ReturnFields;

        // 用于筛选
        // private static final String EntityId = "entity_id";
        // private static final String BrandEntityId = "branch_eids";
        // private static final String PlateEntityId = "plate_eid";
        // private static final String Name = "name";
        // private static final String Code = "code";
        // private static final String Industry = "industry";
        public static final String PresellPlatform = "presell_platform";

        public static final String StatusFlag = "status_flag";

        public static final String FoodStyle = "food_style";

        public static final String TownId = "town_id";

        public static final String IsTest = "is_test";

        // 用于返回
        public static final String EntityId = "entity_id";

        public static final String Code = "code";

        public static final String Name = "name";

        public static final String AVG_PRICE = "avg_price";

        public static final String BrandEntityId = "branch_eids";

        public static final String PlateEntityId = "plate_eid";

        private static final String CityId = "city_id";

        private static final String Address = "address";

        private static final String Phone1 = "phone1";

        private static final String BusinessTime = "business_time";

        private static final String Introduce = "introduce";

        private static final String ConsumptionPerCapita = "consumption_per_capita";

        private static final String FoodMulti = "food_multi";

        public static final String Industry = "industry";

        private static final String LogoUrl = "logo_url";

        private static final String PeriodOrder = "period_order";

        private static final String PresellImg = "presell_img";

        private static final String Dist = "dist";

        static {
            FilterFields = new HashSet<>();
            FilterFields.add(EntityId);
            FilterFields.add(BrandEntityId);
            FilterFields.add(PlateEntityId);
            FilterFields.add(Name);
            FilterFields.add(Code);
            FilterFields.add(Industry);
            FilterFields.add(PresellPlatform);
            FilterFields.add(StatusFlag);
            FilterFields.add(FoodStyle);
            FilterFields.add(IsTest);
            FilterFields.add(TownId);
            ReturnFields = new HashSet<>();
            // ReturnFields.add(EntityId);
            ReturnFields.add(Code);
            ReturnFields.add(Name);
            ReturnFields.add(BrandEntityId);
            ReturnFields.add(PlateEntityId);
            // ReturnFields.add(CityId);
            ReturnFields.add(Address);
            ReturnFields.add(Phone1);
            ReturnFields.add(BusinessTime);
            ReturnFields.add(Introduce);
            ReturnFields.add(ConsumptionPerCapita);
            ReturnFields.add(FoodMulti);
            ReturnFields.add(Industry);
            ReturnFields.add(LogoUrl);
            ReturnFields.add(PeriodOrder);
            ReturnFields.add(PresellImg);
            ReturnFields.add(AVG_PRICE);
            ReturnFields.add(TownId);
        // ReturnFields.add(Dist);
        }

        private Map<String, String> fieldValue = null;

        private final boolean pseudo;

        ShopDetail(boolean pseudo) {
            this.pseudo = pseudo;
            fieldValue = new HashMap<>();
        }

        public void initFieldMap(SolrDocument document) {
            for (String fieldName : FilterFields) {
                fieldValue.put(fieldName, String.valueOf(document.getOrDefault(fieldName, "")));
            }
            for (String fieldName : ReturnFields) {
                fieldValue.put(fieldName, String.valueOf(document.getOrDefault(fieldName, "")));
            }
        }

        public boolean isPseudo() {
            return pseudo;
        }

        public Map<String, String> getFieldValue() {
            return fieldValue;
        }
    }

    public static void main(String[] args) {
        SimpleDateFormat f = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        System.out.println(f.format(1528473600000l));
        long dayMinSec = (1000 * 60 * 60 * 24);
        System.out.println((System.currentTimeMillis() / dayMinSec) * dayMinSec);
    }
}
