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
package com.qlangtech.tis.supplyGoods;

import org.apache.solr.client.solrj.beans.Field;

/* *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class SupplyGoods {

    @Field("id")
    private String id;

    @Field("entity_id")
    private String entityId;

    @Field("bar_code")
    private String barCode;

    @Field("inner_code")
    private String innerCode;

    @Field("name")
    private String name;

    @Field("short_code")
    private String shortCode;

    @Field("spell")
    private String spell;

    @Field("unit_type")
    private Integer unitType;

    @Field("package_type")
    private String packageType;

    @Field("category_id")
    private String categoryId;

    @Field("type")
    private Integer type;

    @Field("num_unit_id")
    private String numUnitId;

    @Field("num_unit_name")
    private String numUnitName;

    @Field("weight_unit_id")
    private String weightUnitId;

    @Field("weight_unit_name")
    private String weightUnitName;

    @Field("main_unit_id")
    private String mainUnitId;

    @Field("main_unit_name")
    private String mainUnitName;

    @Field("sub_unit_id1")
    private String subUnitId1;

    @Field("sub_unit_name1")
    private String subUnitName1;

    @Field("sub_unit_conversion1")
    private Double subUnitConversion1;

    @Field("sub_unit_id2")
    private String subUnitId2;

    @Field("sub_unit_name2")
    private String subUnitName2;

    @Field("sub_unit_conversion2")
    private Double subUnitConversion2;

    @Field("sub_unit_id3")
    private String subUnitId3;

    @Field("sub_unit_name3")
    private String subUnitName3;

    @Field("sub_unit_conversion3")
    private Double subUnitConversion3;

    @Field("sub_unit_id4")
    private String subUnitId4;

    @Field("sub_unit_name4")
    private String subUnitName4;

    @Field("sub_unit_conversion4")
    private Double subUnitConversion4;

    @Field("specification")
    private String specification;

    @Field("server")
    private String server;

    @Field("path")
    private String path;

    @Field("sort_code")
    private Integer sortCode;

    @Field("period")
    private Integer period;

    @Field("memo")
    private String memo;

    @Field("origin")
    private String origin;

    @Field("price_unit_no")
    private Integer priceUnitNo;

    @Field("inventory_unit_no")
    private Integer inventoryUnitNo;

    @Field("percentage")
    private Integer percentage;

    @Field("has_degree")
    private Integer hasDegree;

    @Field("is_sales")
    private Integer isSales;

    @Field("goods_plate_id")
    private String goodsPlateId;

    @Field("goods_plate_name")
    private String goodsPlateName;

    @Field("is_valid")
    private Integer isValid;

    @Field("create_time")
    private Long createTime;

    @Field("op_time")
    private Long opTime;

    @Field("last_ver")
    private Integer lastVer;

    @Field("extend_fields")
    private String extendFields;

    @Field("_version_")
    private Long Version;

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

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public String getBarCode() {
        return this.barCode;
    }

    public void setInnerCode(String innerCode) {
        this.innerCode = innerCode;
    }

    public String getInnerCode() {
        return this.innerCode;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setShortCode(String shortCode) {
        this.shortCode = shortCode;
    }

    public String getShortCode() {
        return this.shortCode;
    }

    public void setSpell(String spell) {
        this.spell = spell;
    }

    public String getSpell() {
        return this.spell;
    }

    public void setUnitType(Integer unitType) {
        this.unitType = unitType;
    }

    public Integer getUnitType() {
        return this.unitType;
    }

    public void setPackageType(String packageType) {
        this.packageType = packageType;
    }

    public String getPackageType() {
        return this.packageType;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryId() {
        return this.categoryId;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getType() {
        return this.type;
    }

    public void setNumUnitId(String numUnitId) {
        this.numUnitId = numUnitId;
    }

    public String getNumUnitId() {
        return this.numUnitId;
    }

    public void setNumUnitName(String numUnitName) {
        this.numUnitName = numUnitName;
    }

    public String getNumUnitName() {
        return this.numUnitName;
    }

    public void setWeightUnitId(String weightUnitId) {
        this.weightUnitId = weightUnitId;
    }

    public String getWeightUnitId() {
        return this.weightUnitId;
    }

    public void setWeightUnitName(String weightUnitName) {
        this.weightUnitName = weightUnitName;
    }

    public String getWeightUnitName() {
        return this.weightUnitName;
    }

    public void setMainUnitId(String mainUnitId) {
        this.mainUnitId = mainUnitId;
    }

    public String getMainUnitId() {
        return this.mainUnitId;
    }

    public void setMainUnitName(String mainUnitName) {
        this.mainUnitName = mainUnitName;
    }

    public String getMainUnitName() {
        return this.mainUnitName;
    }

    public void setSubUnitId1(String subUnitId1) {
        this.subUnitId1 = subUnitId1;
    }

    public String getSubUnitId1() {
        return this.subUnitId1;
    }

    public void setSubUnitName1(String subUnitName1) {
        this.subUnitName1 = subUnitName1;
    }

    public String getSubUnitName1() {
        return this.subUnitName1;
    }

    public void setSubUnitConversion1(Double subUnitConversion1) {
        this.subUnitConversion1 = subUnitConversion1;
    }

    public Double getSubUnitConversion1() {
        return this.subUnitConversion1;
    }

    public void setSubUnitId2(String subUnitId2) {
        this.subUnitId2 = subUnitId2;
    }

    public String getSubUnitId2() {
        return this.subUnitId2;
    }

    public void setSubUnitName2(String subUnitName2) {
        this.subUnitName2 = subUnitName2;
    }

    public String getSubUnitName2() {
        return this.subUnitName2;
    }

    public void setSubUnitConversion2(Double subUnitConversion2) {
        this.subUnitConversion2 = subUnitConversion2;
    }

    public Double getSubUnitConversion2() {
        return this.subUnitConversion2;
    }

    public void setSubUnitId3(String subUnitId3) {
        this.subUnitId3 = subUnitId3;
    }

    public String getSubUnitId3() {
        return this.subUnitId3;
    }

    public void setSubUnitName3(String subUnitName3) {
        this.subUnitName3 = subUnitName3;
    }

    public String getSubUnitName3() {
        return this.subUnitName3;
    }

    public void setSubUnitConversion3(Double subUnitConversion3) {
        this.subUnitConversion3 = subUnitConversion3;
    }

    public Double getSubUnitConversion3() {
        return this.subUnitConversion3;
    }

    public void setSubUnitId4(String subUnitId4) {
        this.subUnitId4 = subUnitId4;
    }

    public String getSubUnitId4() {
        return this.subUnitId4;
    }

    public void setSubUnitName4(String subUnitName4) {
        this.subUnitName4 = subUnitName4;
    }

    public String getSubUnitName4() {
        return this.subUnitName4;
    }

    public void setSubUnitConversion4(Double subUnitConversion4) {
        this.subUnitConversion4 = subUnitConversion4;
    }

    public Double getSubUnitConversion4() {
        return this.subUnitConversion4;
    }

    public void setSpecification(String specification) {
        this.specification = specification;
    }

    public String getSpecification() {
        return this.specification;
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

    public void setSortCode(Integer sortCode) {
        this.sortCode = sortCode;
    }

    public Integer getSortCode() {
        return this.sortCode;
    }

    public void setPeriod(Integer period) {
        this.period = period;
    }

    public Integer getPeriod() {
        return this.period;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getMemo() {
        return this.memo;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getOrigin() {
        return this.origin;
    }

    public void setPriceUnitNo(Integer priceUnitNo) {
        this.priceUnitNo = priceUnitNo;
    }

    public Integer getPriceUnitNo() {
        return this.priceUnitNo;
    }

    public void setInventoryUnitNo(Integer inventoryUnitNo) {
        this.inventoryUnitNo = inventoryUnitNo;
    }

    public Integer getInventoryUnitNo() {
        return this.inventoryUnitNo;
    }

    public void setPercentage(Integer percentage) {
        this.percentage = percentage;
    }

    public Integer getPercentage() {
        return this.percentage;
    }

    public void setHasDegree(Integer hasDegree) {
        this.hasDegree = hasDegree;
    }

    public Integer getHasDegree() {
        return this.hasDegree;
    }

    public void setIsSales(Integer isSales) {
        this.isSales = isSales;
    }

    public Integer getIsSales() {
        return this.isSales;
    }

    public void setGoodsPlateId(String goodsPlateId) {
        this.goodsPlateId = goodsPlateId;
    }

    public String getGoodsPlateId() {
        return this.goodsPlateId;
    }

    public void setGoodsPlateName(String goodsPlateName) {
        this.goodsPlateName = goodsPlateName;
    }

    public String getGoodsPlateName() {
        return this.goodsPlateName;
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

    public void setLastVer(Integer lastVer) {
        this.lastVer = lastVer;
    }

    public Integer getLastVer() {
        return this.lastVer;
    }

    public void setExtendFields(String extendFields) {
        this.extendFields = extendFields;
    }

    public String getExtendFields() {
        return this.extendFields;
    }

    public void setVersion(Long Version) {
        this.Version = Version;
    }

    public Long getVersion() {
        return this.Version;
    }
}
