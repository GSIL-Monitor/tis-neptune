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

import java.util.Collections;
import java.util.List;
import org.apache.solr.client.solrj.beans.Field;

/* *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class Spu {

    @Field("id")
    private String id;

    @Field("entity_id")
    private String entityId;

    @Field("store_code")
    private String storeCode;

    @Field("store_id")
    private String storeId;

    @Field("name")
    private String name;

    @Field("spell")
    private String spell;

    @Field("spec_type")
    private Integer specType;

    @Field("is_sale")
    private Integer isSale;

    @Field("priority")
    private Integer priority;

    @Field("system_priority")
    private Integer systemPriority;

    @Field("tag")
    private String tag;

    @Field("brand")
    private String brand;

    @Field("origin")
    private String origin;

    @Field("server")
    private String server;

    @Field("path")
    private String path;

    @Field("sales_num")
    private Long salesNum;

    @Field("sale_time")
    private Long saleTime;

    @Field("status")
    private Integer status;

    @Field("is_valid")
    private Integer isValid;

    @Field("create_time")
    private Long createTime;

    @Field("op_time")
    private Long opTime;

    @Field("last_ver")
    private Integer lastVer;

    @Field("op_user_id")
    private String opUserId;

    @Field("op_user_name")
    private String opUserName;

    @Field("extend_fields")
    private String extendFields;

    @Field(child = true)
    private List<Sku> skus = Collections.emptyList();

    public void setSkus(List<Sku> skus) {
        this.skus = skus;
    }

    public List<Sku> getSkus() {
        return this.skus;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public String getEntityId() {
        return this.entityId;
    }

    public void setStoreCode(String storeCode) {
        this.storeCode = storeCode;
    }

    public String getStoreCode() {
        return this.storeCode;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public String getStoreId() {
        return this.storeId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setSpell(String spell) {
        this.spell = spell;
    }

    public String getSpell() {
        return this.spell;
    }

    public void setSpecType(Integer specType) {
        this.specType = specType;
    }

    public Integer getSpecType() {
        return this.specType;
    }

    public void setIsSale(Integer isSale) {
        this.isSale = isSale;
    }

    public Integer getIsSale() {
        return this.isSale;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Integer getPriority() {
        return this.priority;
    }

    public void setSystemPriority(Integer systemPriority) {
        this.systemPriority = systemPriority;
    }

    public Integer getSystemPriority() {
        return this.systemPriority;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getTag() {
        return this.tag;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getBrand() {
        return this.brand;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getOrigin() {
        return this.origin;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getServer() {
        return this.server;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return this.path;
    }

    public void setSalesNum(Long salesNum) {
        this.salesNum = salesNum;
    }

    public Long getSalesNum() {
        return this.salesNum;
    }

    public void setSaleTime(Long saleTime) {
        this.saleTime = saleTime;
    }

    public Long getSaleTime() {
        return this.saleTime;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getStatus() {
        return this.status;
    }

    public void setIsValid(Integer isValid) {
        this.isValid = isValid;
    }

    public Integer getIsValid() {
        return this.isValid;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Long getCreateTime() {
        return this.createTime;
    }

    public void setOpTime(Long opTime) {
        this.opTime = opTime;
    }

    public Long getOpTime() {
        return this.opTime;
    }

    public Integer getLastVer() {
        return lastVer;
    }

    public void setLastVer(Integer lastVer) {
        this.lastVer = lastVer;
    }

    public void setOpUserId(String opUserId) {
        this.opUserId = opUserId;
    }

    public String getOpUserId() {
        return this.opUserId;
    }

    public void setOpUserName(String opUserName) {
        this.opUserName = opUserName;
    }

    public String getOpUserName() {
        return this.opUserName;
    }

    public void setExtendFields(String extendFields) {
        this.extendFields = extendFields;
    }

    public String getExtendFields() {
        return this.extendFields;
    }
}
