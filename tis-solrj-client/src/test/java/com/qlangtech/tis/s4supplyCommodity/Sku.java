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

import org.apache.solr.client.solrj.beans.Field;

/* *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class Sku {

    @Field("sku_goods_id")
    private String skuGoodsId;

    @Field("sku_standard_goods_id")
    private String skuStandardGoodsId;

    @Field("sku_standard_category_id")
    private String skuStandardCategoryId;

    @Field("sku_standard_inner_code")
    private String skuStandardInnerCode;

    @Field("sku_sort_code")
    private Integer skuSortCode;

    @Field("sku_spec_name")
    private String skuSpecName;

    @Field("sku_package_unit")
    private String skuPackageUnit;

    @Field("sku_package_num")
    private Float skuPackageNum;

    @Field("sku_status")
    private Integer skuStatus;

    @Field("sku_create_time")
    private Long skuCreateTime;

    @Field("sku_bar_code")
    private String skuBarCode;

    @Field("sku_is_valid")
    private Integer skuIsValid;

    // from parent
    @Field("store_code")
    private String storeCode;

    // from parent
    @Field("name")
    private String name;

    // from parent
    @Field("spec_type")
    private int specType;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSpecType() {
        return specType;
    }

    public void setSpecType(int specType) {
        this.specType = specType;
    }

    @Field("id")
    private String id;

    public void setSkuGoodsId(String skuGoodsId) {
        this.skuGoodsId = skuGoodsId;
    }

    public String getSkuGoodsId() {
        return this.skuGoodsId;
    }

    public void setSkuStandardGoodsId(String skuStandardGoodsId) {
        this.skuStandardGoodsId = skuStandardGoodsId;
    }

    public String getSkuStandardGoodsId() {
        return this.skuStandardGoodsId;
    }

    public void setSkuStandardCategoryId(String skuStandardCategoryId) {
        this.skuStandardCategoryId = skuStandardCategoryId;
    }

    public String getSkuStandardCategoryId() {
        return this.skuStandardCategoryId;
    }

    public void setSkuStandardInnerCode(String skuStandardInnerCode) {
        this.skuStandardInnerCode = skuStandardInnerCode;
    }

    public String getSkuStandardInnerCode() {
        return this.skuStandardInnerCode;
    }

    public void setSkuSortCode(Integer skuSortCode) {
        this.skuSortCode = skuSortCode;
    }

    public Integer getSkuSortCode() {
        return this.skuSortCode;
    }

    public void setSkuSpecName(String skuSpecName) {
        this.skuSpecName = skuSpecName;
    }

    public String getSkuSpecName() {
        return this.skuSpecName;
    }

    public void setSkuPackageUnit(String skuPackageUnit) {
        this.skuPackageUnit = skuPackageUnit;
    }

    public String getSkuPackageUnit() {
        return this.skuPackageUnit;
    }

    public void setSkuPackageNum(Float skuPackageNum) {
        this.skuPackageNum = skuPackageNum;
    }

    public Float getSkuPackageNum() {
        return this.skuPackageNum;
    }

    public void setSkuStatus(Integer skuStatus) {
        this.skuStatus = skuStatus;
    }

    public Integer getSkuStatus() {
        return this.skuStatus;
    }

    public void setSkuCreateTime(Long skuCreateTime) {
        this.skuCreateTime = skuCreateTime;
    }

    public Long getSkuCreateTime() {
        return this.skuCreateTime;
    }

    public void setSkuBarCode(String skuBarCode) {
        this.skuBarCode = skuBarCode;
    }

    public String getSkuBarCode() {
        return this.skuBarCode;
    }

    public void setSkuIsValid(Integer skuIsValid) {
        this.skuIsValid = skuIsValid;
    }

    public Integer getSkuIsValid() {
        return this.skuIsValid;
    }

    public String getStoreCode() {
        return storeCode;
    }

    public void setStoreCode(String storeCode) {
        this.storeCode = storeCode;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
