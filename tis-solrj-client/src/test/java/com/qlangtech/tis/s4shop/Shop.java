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
package com.qlangtech.tis.s4shop;

import org.apache.solr.client.solrj.beans.Field;

/* *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class Shop {

    @Field("dist")
    private double dist;

    @Field("entity_id")
    private String entityId;

    @Field("shop_kind")
    private Integer shopKind;

    @Field("name")
    private String name;

    @Field("english_name")
    private String englishName;

    @Field("customer_kind")
    private Integer customerKind;

    @Field("introduce")
    private String introduce;

    @Field("status")
    private Integer status;

    @Field("expire")
    private String expire;

    @Field("business_time")
    private String businessTime;

    @Field("coordinate")
    private String coordinate;

    @Field("country_id")
    private String countryId;

    @Field("province_id")
    private String provinceId;

    @Field("city_id")
    private String cityId;

    @Field("town_id")
    private String townId;

    @Field("address")
    private String address;

    @Field("avg_price")
    private String avgPrice;

    @Field("merchant_id")
    private String merchantId;

    @Field("is_init")
    private Integer isInit;

    @Field("map_address")
    private String mapAddress;

    @Field("status_flag")
    private String statusFlag;

    @Field("industry")
    private Integer industry;

    @Field("is_valid")
    private Integer isValid;

    @Field("is_test")
    private Integer isTest;

    @Field("available")
    private Integer available;

    @Field("plate_names")
    private String plateNames;

    @Field("logo_url")
    private String logoUrl;

    @Field("shop_level")
    private Integer shopLevel;

    @Field("scan_amount")
    private Integer scanAmount;

    @Field("consumption_per_capita")
    private Double consumptionPerCapita;

    @Field("order_count")
    private Integer orderCount;

    @Field("notification_num")
    private Integer notificationNum;

    @Field("can_takeout")
    private Integer canTakeout;

    @Field("can_reserve")
    private Integer canReserve;

    @Field("_version_")
    private Long Version;

    public double getDist() {
        return dist;
    }

    public void setDist(double dist) {
        this.dist = dist;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public String getEntityId() {
        return this.entityId;
    }

    public void setShopKind(Integer shopKind) {
        this.shopKind = shopKind;
    }

    public Integer getShopKind() {
        return this.shopKind;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setEnglishName(String englishName) {
        this.englishName = englishName;
    }

    public String getEnglishName() {
        return this.englishName;
    }

    public void setCustomerKind(Integer customerKind) {
        this.customerKind = customerKind;
    }

    public Integer getCustomerKind() {
        return this.customerKind;
    }

    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }

    public String getIntroduce() {
        return this.introduce;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getStatus() {
        return this.status;
    }

    public void setExpire(String expire) {
        this.expire = expire;
    }

    public String getExpire() {
        return this.expire;
    }

    public void setBusinessTime(String businessTime) {
        this.businessTime = businessTime;
    }

    public String getBusinessTime() {
        return this.businessTime;
    }

    public void setCoordinate(String coordinate) {
        this.coordinate = coordinate;
    }

    public String getCoordinate() {
        return this.coordinate;
    }

    public void setCountryId(String countryId) {
        this.countryId = countryId;
    }

    public String getCountryId() {
        return this.countryId;
    }

    public void setProvinceId(String provinceId) {
        this.provinceId = provinceId;
    }

    public String getProvinceId() {
        return this.provinceId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    public String getCityId() {
        return this.cityId;
    }

    public void setTownId(String townId) {
        this.townId = townId;
    }

    public String getTownId() {
        return this.townId;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress() {
        return this.address;
    }

    public void setAvgPrice(String avgPrice) {
        this.avgPrice = avgPrice;
    }

    public String getAvgPrice() {
        return this.avgPrice;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public String getMerchantId() {
        return this.merchantId;
    }

    public void setIsInit(Integer isInit) {
        this.isInit = isInit;
    }

    public Integer getIsInit() {
        return this.isInit;
    }

    public void setMapAddress(String mapAddress) {
        this.mapAddress = mapAddress;
    }

    public String getMapAddress() {
        return this.mapAddress;
    }

    public void setStatusFlag(String statusFlag) {
        this.statusFlag = statusFlag;
    }

    public String getStatusFlag() {
        return this.statusFlag;
    }

    public void setIndustry(Integer industry) {
        this.industry = industry;
    }

    public Integer getIndustry() {
        return this.industry;
    }

    public void setIsValid(Integer isValid) {
        this.isValid = isValid;
    }

    public Integer getIsValid() {
        return this.isValid;
    }

    public void setIsTest(Integer isTest) {
        this.isTest = isTest;
    }

    public Integer getIsTest() {
        return this.isTest;
    }

    public void setAvailable(Integer available) {
        this.available = available;
    }

    public Integer getAvailable() {
        return this.available;
    }

    public void setPlateNames(String plateNames) {
        this.plateNames = plateNames;
    }

    public String getPlateNames() {
        return this.plateNames;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public String getLogoUrl() {
        return this.logoUrl;
    }

    public void setShopLevel(Integer shopLevel) {
        this.shopLevel = shopLevel;
    }

    public Integer getShopLevel() {
        return this.shopLevel;
    }

    public void setScanAmount(Integer scanAmount) {
        this.scanAmount = scanAmount;
    }

    public Integer getScanAmount() {
        return this.scanAmount;
    }

    public void setConsumptionPerCapita(Double consumptionPerCapita) {
        this.consumptionPerCapita = consumptionPerCapita;
    }

    public Double getConsumptionPerCapita() {
        return this.consumptionPerCapita;
    }

    public void setOrderCount(Integer orderCount) {
        this.orderCount = orderCount;
    }

    public Integer getOrderCount() {
        return this.orderCount;
    }

    public void setNotificationNum(Integer notificationNum) {
        this.notificationNum = notificationNum;
    }

    public Integer getNotificationNum() {
        return this.notificationNum;
    }

    public void setCanTakeout(Integer canTakeout) {
        this.canTakeout = canTakeout;
    }

    public Integer getCanTakeout() {
        return this.canTakeout;
    }

    public void setCanReserve(Integer canReserve) {
        this.canReserve = canReserve;
    }

    public Integer getCanReserve() {
        return this.canReserve;
    }

    public void setVersion(Long Version) {
        this.Version = Version;
    }

    public Long getVersion() {
        return this.Version;
    }
}
