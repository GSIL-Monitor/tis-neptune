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
package com.qlangtech.tis.s4supplyCommodity;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.lucene.util.PriorityQueue;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.io.Tuple;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import com.qlangtech.tis.solrj.extend.BasicTestCase;
import com.qlangtech.tis.solrj.extend.AbstractTisCloudSolrClient.ResponseCallback;
import com.qlangtech.tis.solrj.extend.AbstractTisCloudSolrClient.SimpleQueryResult;

/*
 * Created by Qinjiu(Qinjiu@2dfire.com) on 2016/12/23.
 *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class Test4supplyCommodity extends BasicTestCase {

    /**
     * 1 根据spu的name去查询spu ——>返回整个spu 返回结果以status字段过滤 返回结果以create_time升序排列
     */
    public void testGetSpuWithSpuName() throws Exception {
        // query.setQuery("name:cs");
        // query.setFields("id", "entity_id", "name", "status");
        // query.setFilterQueries("status:2");
        // query.addSort("create_time", SolrQuery.ORDER.asc);
        // SimpleQueryResult<Spu> response = this.client.query("search4supplyCommodity",
        // "0", query, Spu.class);
        // for (Spu spu : response.getResult()) {
        // System.out.println("--------------------");
        // System.out.print(spu.getId());
        // System.out.print(" ");
        // System.out.print(spu.getEntityId());
        // System.out.print(" ");
        // System.out.print(spu.getName());
        // System.out.print(" ");
        // System.out.print(spu.getStatus());
        // System.out.println("");
        // }
        SolrDocument doc = this.client.getById("search4supplyCommodity", "4038ed4feebdd7e4dbed5d3dc88ea759", "99926300");
        for (String field : doc.getFieldNames()) {
            System.out.println(field + ":" + doc.getFirstValue(field));
        }
        System.out.println(doc.hasChildDocuments());
    }

    /**
     * 2 根据sku的barcode去查询spu，大括号中内容不变 ——>返回整个spu 返回结果以status字段过滤
     * 返回结果以create_time升序排列
     */
    public void testGetSpuWithSkuBarcode() throws Exception {
        query.setQuery("{!parent which=\"type:p\"}sku_bar_code:2000000913");
        query.setFields("id", "standard_category_id", "name", "status");
        query.setFilterQueries("status:2");
        query.addSort("create_time", SolrQuery.ORDER.asc);
        SimpleQueryResult<Spu> response = this.client.query("search4supplyCommodity", "0", query, Spu.class);
        for (Spu spu : response.getResult()) {
            System.out.println("--------------------");
            System.out.print(spu.getId());
            System.out.print(" ");
            System.out.print(spu.getName());
            System.out.print(" ");
            System.out.print(spu.getStatus());
            System.out.println("");
        }
    }

    /**
     * 3 根据spu的entity_id去查询所有类目 ——>返回所有类目的前四个全套spu
     * ，每个类目里的spu按照默认按照销量降序展示全部商品，当销量相同时按照商品新建时间降序排序， 返回结果以status字段过滤 返回结果以
     * 按照销量降序展示全部商品，当销量相同时按照商品新建时间降序排序，
     */
    public void testGetSpuGroup() throws Exception {
        // query.setQuery("entity_id:99926300"); // 搜索单个店铺
        // query.setQuery("type:p"); // 搜索单个店铺
        // 搜索单个店铺
        query.setQuery("type:p AND is_valid:1 AND status:2");
        // 需要搜索7个类目就添加此条件
        query.set("SPUGroup", true);
        query.setFields("*");
        QueryResponse response = this.client.query("search4supplyCommodity", "0", query);
        LinkedHashMap map = (LinkedHashMap) response.getResponse().get("SPUGroup");
        for (Object key1 : map.keySet()) {
            System.out.println(key1);
            List<SolrDocument> list = (List<SolrDocument>) map.get(key1);
            int i = 0;
            for (SolrDocument doc : list) {
                Spu spu = client.transferBean(Spu.class, doc);
                System.out.println("--------------------parent" + i++);
                System.out.print(spu.getId());
                System.out.print(" ");
                System.out.print(spu.getName());
                System.out.print(" ");
                System.out.print(spu.getStatus());
                System.out.println("");
                // System.out.println(spu.getSaleTime());
                System.out.println(spu.getSalesNum());
                System.out.println(spu.getCreateTime());
            // System.out.println("--------------------child");
            // for (Sku sku : spu.getSkus()) {
            // System.out.print(sku.getId());
            // System.out.print(" ");
            // System.out.print(sku.getSkuStandardGoodsId());
            // System.out.println(" ");
            // }
            }
        }
    }

    /**
     * 4 根据spu的name和sku的inner_code去查询 -> 返回全套，spu和对应的所有sku<br>
     * 以sku_bar_code为条件去查询，以spu.status过滤，大括号中的内容不用变
     */
    public void testSearchSpuWithSku() throws Exception {
        SolrQuery query = new SolrQuery();
        query.setQuery("status:2 AND name:cs AND {!parent which=\"type:p\"}sku_standard_inner_code:1000");
        query.setFields("id", "entity_id", "name", "[child parentFilter=type:p]");
        // 按照create_time排序
        query.addSort("create_time", SolrQuery.ORDER.asc);
        SimpleQueryResult<Spu> response = this.client.query("search4supplyCommodity", "0", query, Spu.class);
        System.out.println("Number Found: " + response.getNumberFound());
        for (Spu doc : response.getResult()) {
            System.out.println("---------------------------");
            System.out.print(doc.getId());
            System.out.print(" ");
            System.out.print(doc.getEntityId());
            System.out.print(" ");
            System.out.print(doc.getName());
            System.out.println(" ");
            System.out.println("child---------------------------");
            for (Sku sku : doc.getSkus()) {
                System.out.print(sku.getSkuGoodsId());
                System.out.print(" ");
                System.out.print(sku.getSkuStandardGoodsId());
                System.out.println(" ");
            }
        }
    }

    /**
     * 5 根据sku的list去查询 ——> 返回对应的所有spu和当前sku
     * 在setFields中添加'f=store_code,name'就是要取当前spu的某些字段，这里f的字段必须在class Sku中有该字段对应的定义
     * 并且在前面的fields中也添加该字段
     */
    public void testGetSpuWithSkuList() throws Exception {
        query.setQuery("{!terms f=id}99926350599c4f9101599c5d1a36000f,990008985997109301599bc3de85002e,99928370598d24bd01598d6c10e50017,99928370598d24bd01598d5c8b6c0011");
        // query.setQuery("{!terms f=id}99926350599c4f9101599c5d1a36000f");
        query.setFields("id", "store_code", "name", "spec_type", "[parent parentFilter=type:p f=store_code,name,spec_type]");
        SimpleQueryResult<Sku> response = this.client.query("search4supplyCommodity", "0", query, Sku.class);
        for (Sku spu : response.getResult()) {
            System.out.println("--------------------");
            System.out.print(spu.getId());
            System.out.print(" ");
            System.out.print(spu.getStoreCode());
            System.out.print(" ");
            System.out.print(spu.getName());
            System.out.println("");
            System.out.print(spu.getSpecType());
            System.out.println("");
        }
    }

    /**
     * 6 根据spu的store_code去查询 ——> 返回对应的sku
     * 以sku的sku_create_time过滤，精确到年月日，后面时分秒毫秒用000000000和999999999补齐
     * 范围查询中括号表示闭区间，大括号表示开区间 以sku_goods_id去重，取sku_create_time的最小值
     * 搜索结果返回sku的sku_sort_code字段，并且附带对应spu的store_id 并且在前面的fields中也添加该字段
     */
    public void testGetSkuAndGroupBy() throws Exception {
        query.setQuery("{!child of=\"type:p\"}{!terms f=store_code}00001,00002");
        query.setFilterQueries("sku_create_time:{20161222000000000 TO 20161222999999999]", "{!collapse field=sku_goods_id min=sku_create_time}");
        query.setFields("id", "sku_sort_code", "store_code", "[parent parentFilter=type:p f=store_code]");
        SimpleQueryResult<Sku> response = this.client.query("search4supplyCommodity", "0", query, Sku.class);
        for (Sku spu : response.getResult()) {
            System.out.println("--------------------");
            System.out.print(spu.getId());
            System.out.print(" ");
            System.out.print(spu.getSkuSortCode());
            System.out.print("");
            System.out.print(spu.getStoreCode());
            System.out.println(" ");
        }
    }

    public void testSearchSpuWithSku1() throws Exception {
        SolrQuery query = new SolrQuery();
        query.setQuery("id:1ebaf0e33f90d3e1e5b3be9ac83f4e9a");
        query.setFields("id", "entity_id", "name", "[child parentFilter=type:p]");
        // 按照create_time排序
        query.addSort("create_time", SolrQuery.ORDER.asc);
        SimpleQueryResult<Spu> response = this.client.query("search4supplyCommodity", "0", query, Spu.class);
        System.out.println("Number Found: " + response.getNumberFound());
        for (Spu doc : response.getResult()) {
            System.out.println("---------------------------");
            System.out.print(doc.getId());
            System.out.print(" ");
            System.out.print(doc.getEntityId());
            System.out.print(" ");
            System.out.print(doc.getName());
            System.out.println(" ");
            System.out.println("child---------------------------");
            for (Sku sku : doc.getSkus()) {
                System.out.println(sku.getId());
                System.out.print(" ");
                System.out.print(sku.getSkuGoodsId());
                System.out.print(" ");
            // System.out.print(sku.getSkuStandardGoodsId());
            // System.out.println(" ");
            }
        }
    }

    /**
     * description: 测试Lucene中的优先队列 date: 3:50 PM 5/10/2017
     */
    public void testPriorityQueue() throws Exception {
        PriorityQueue<SpuWithSkuIdAndSortFields> priorityQueue = new PriorityQueue<SpuWithSkuIdAndSortFields>(3) {

            @Override
            protected boolean lessThan(SpuWithSkuIdAndSortFields a, SpuWithSkuIdAndSortFields b) {
                if (a.getSalesNum() == b.getSalesNum()) {
                    return a.getCreateTime() < b.getCreateTime();
                } else {
                    return a.getSalesNum() < b.getSalesNum();
                }
            }
        };
        priorityQueue.insertWithOverflow(new SpuWithSkuIdAndSortFields(5, 1));
        priorityQueue.insertWithOverflow(new SpuWithSkuIdAndSortFields(5, 4));
        priorityQueue.insertWithOverflow(new SpuWithSkuIdAndSortFields(6, 3));
        priorityQueue.insertWithOverflow(new SpuWithSkuIdAndSortFields(5, 2));
        priorityQueue.insertWithOverflow(new SpuWithSkuIdAndSortFields(2, 1));
        priorityQueue.insertWithOverflow(new SpuWithSkuIdAndSortFields(3, 1));
        priorityQueue.insertWithOverflow(new SpuWithSkuIdAndSortFields(4, 1));
        while (priorityQueue.size() > 0) {
            System.out.println(priorityQueue.pop());
        }
    }

    private static class SpuWithSkuIdAndSortFields {

        private int createTime;

        private int salesNum;

        SpuWithSkuIdAndSortFields(int salesNum, int createTime) {
            this.salesNum = salesNum;
            this.createTime = createTime;
        }

        public int getCreateTime() {
            return createTime;
        }

        public int getSalesNum() {
            return salesNum;
        }

        @Override
        public String toString() {
            return this.salesNum + " " + this.createTime;
        }
    }

    // search4supplyOrder索引的GroupSumComponent日常环境测试
    public void testQuery() throws Exception {
        SolrQuery query = new SolrQuery();
        query.setQuery("self_entity_id:99000903");
        query.setRows(0);
        query.set("GroupSum", true);
        query.set("groupField", "pay_time");
        query.set("sumField", "discount_amount");
        QueryResponse response = client.query("search4supplyOrder", "99000903", query);
        System.out.println("************");
        Map<String, Number> resultMap = (Map<String, Number>) response.getResponse().get("GroupSum");
        for (Map.Entry<String, Number> entry : resultMap.entrySet()) {
            System.out.println(entry.getKey() + ":" + entry.getValue());
        }
    }
}
