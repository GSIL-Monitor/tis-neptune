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
package com.qlangtech.tis.s4supplyUnionTabs;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.common.SolrException;

/*
 * Created by Qinjiu(Qinjiu@2dfire.com) on 2/27/2017.
 *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class InOptimizeStringBuilder {

    public static final String DOCID = "docid";

    // 最终的结果数
    private int finalRowCount;

    // 最终排序的列
    private String finalSortCol;

    // 最终排序的顺序
    private SolrQuery.ORDER finalSortOrder;

    // 搜索索引名
    private String searchTabName;

    // union索引名
    private String unionTabName;

    // 搜索的结束过
    private int searchRowCount;

    // 搜索查询语句
    private String searchQuery;

    // union表查询语句
    private String unionQuery;

    // fields
    private String[] searchFields;

    // union表field，一般只有一个
    private String unionField;

    // join顺序
    private SolrQuery.ORDER joinOrder;

    // 搜索join字段
    private String searchJoinField;

    // 是否需要翻页
    private boolean deepPage = false;

    // 第一页最后一条的docid
    private int afterId;

    // 第一个最后一条的排序字段对应的value
    private Object afterValue;

    // shardkey 分库键
    private String shardKey;

    public static void main(String[] args) {
        InOptimizeStringBuilder builder = new InOptimizeStringBuilder();
        builder.setFinalRowCount(3);
        builder.setFinalSortCol("create_time");
        builder.setFinalSortOrder(SolrQuery.ORDER.asc);
        builder.setSearchTabName("search4supplyGoods");
        builder.setUnionTabName("search4supplyUnionTabs");
        builder.setSearchRowCount(3);
        builder.setSearchQuery("entity_id:99928370");
        builder.setUnionQuery("entity_id:99928370");
        builder.setSearchFields(new String[] { "id", "entity_id", "create_time" });
        builder.setUnionField("goods_id");
        builder.setJoinOrder(SolrQuery.ORDER.asc);
        builder.setSearchJoinField("id");
        builder.setShardKey("99928370");
        System.out.println(builder.build());
    }

    public void setFinalRowCount(int finalRowCount) {
        this.finalRowCount = finalRowCount;
    }

    public void setFinalSortCol(String finalSortCol) {
        this.finalSortCol = finalSortCol;
    }

    public void setFinalSortOrder(SolrQuery.ORDER finalSortOrder) {
        this.finalSortOrder = finalSortOrder;
    }

    public void setSearchTabName(String searchTabName) {
        this.searchTabName = searchTabName;
    }

    public void setUnionTabName(String unionTabName) {
        this.unionTabName = unionTabName;
    }

    public void setSearchRowCount(int searchRowCount) {
        this.searchRowCount = searchRowCount;
    }

    public void setSearchQuery(String searchQuery) {
        this.searchQuery = searchQuery;
    }

    public void setUnionQuery(String unionQuery) {
        this.unionQuery = unionQuery;
    }

    public void setSearchFields(String[] searchFields) {
        this.searchFields = searchFields;
    }

    public void setUnionField(String unionField) {
        this.unionField = unionField;
    }

    public void setJoinOrder(SolrQuery.ORDER joinOrder) {
        this.joinOrder = joinOrder;
    }

    public void setSearchJoinField(String searchJoinField) {
        this.searchJoinField = searchJoinField;
    }

    public void setDeepPage(boolean deepPage) {
        this.deepPage = deepPage;
    }

    public void setAfterId(int afterId) {
        this.afterId = afterId;
    }

    public void setAfterValue(Object afterValue) {
        this.afterValue = afterValue;
    }

    public void setShardKey(String shardKey) {
        this.shardKey = shardKey;
    }

    public String getSearchJoinField() {
        return searchJoinField;
    }

    public String[] getSearchFields() {
        return searchFields;
    }

    public String getSearchTabName() {
        return searchTabName;
    }

    public String getShardKey() {
        return shardKey;
    }

    public String build() {
        if (finalRowCount == 0) {
            throw new SolrException(SolrException.ErrorCode.INVALID_STATE, "please set finalRowCount");
        }
        if (finalSortCol == null) {
            throw new SolrException(SolrException.ErrorCode.INVALID_STATE, "please set finalSortCol");
        }
        if (finalSortOrder == null) {
            throw new SolrException(SolrException.ErrorCode.INVALID_STATE, "please set finalSortOrder");
        }
        if (searchTabName == null) {
            throw new SolrException(SolrException.ErrorCode.INVALID_STATE, "please set searchTabName");
        }
        if (unionTabName == null) {
            throw new SolrException(SolrException.ErrorCode.INVALID_STATE, "please set unionTabName");
        }
        if (searchRowCount == 0) {
            throw new SolrException(SolrException.ErrorCode.INVALID_STATE, "please set searchRowCount");
        }
        if (searchQuery == null) {
            throw new SolrException(SolrException.ErrorCode.INVALID_STATE, "please set searchQuery");
        }
        if (unionQuery == null) {
            throw new SolrException(SolrException.ErrorCode.INVALID_STATE, "please set unionQuery");
        }
        if (searchFields == null) {
            throw new SolrException(SolrException.ErrorCode.INVALID_STATE, "please set searchFields");
        }
        if (unionField == null) {
            throw new SolrException(SolrException.ErrorCode.INVALID_STATE, "please set unionField");
        }
        if (joinOrder == null) {
            throw new SolrException(SolrException.ErrorCode.INVALID_STATE, "please set joinOrder");
        }
        if (searchJoinField == null) {
            throw new SolrException(SolrException.ErrorCode.INVALID_STATE, "please set searchJoinField");
        }
        if (deepPage) {
            if (afterId == 0) {
                throw new SolrException(SolrException.ErrorCode.INVALID_STATE, "please set afterId");
            }
            if (afterValue == null) {
                throw new SolrException(SolrException.ErrorCode.INVALID_STATE, "please set afterValue");
            }
        }
        if (shardKey == null) {
            throw new SolrException(SolrException.ErrorCode.INVALID_STATE, "please set shardKey");
        }
        StringBuilder sb = new StringBuilder();
        sb.append("top(");
        sb.append("\n\t");
        sb.append("n=").append(3).append(",");
        sb.append("\n\t");
        sb.append("leftOuterJoin(");
        sb.append("\n\t\t");
        sb.append("searchExtend(").append(searchTabName).append(", qt=/export, _route_=").append(shardKey);
        sb.append(", q=_query_:\"{!topNField rowCount=").append(searchRowCount);
        sb.append(" sort=").append(finalSortCol).append(" order=").append(finalSortOrder).append("}");
        sb.append(searchQuery).append("\", fl=\"").append(searchJoinField).append(", docid:[docid f=docid]\", sort=");
        sb.append(searchJoinField).append(" ").append(joinOrder).append("),");
        sb.append("\n\t\t");
        sb.append("unique(searchExtend(").append(unionTabName).append(", qt=/export, _route_=").append(shardKey);
        sb.append(", q=").append(unionQuery);
        sb.append(", fl=\"").append(unionField).append("\", sort=").append(unionField).append(" ").append(joinOrder).append("), over=").append(unionField).append("),\n");
        sb.append("\t").append("on=").append(searchJoinField).append("=").append(unionField).append("),\n\t");
        sb.append("sort=").append(finalSortCol).append(" ").append(finalSortOrder).append("\n)");
        return sb.toString();
    }
}
