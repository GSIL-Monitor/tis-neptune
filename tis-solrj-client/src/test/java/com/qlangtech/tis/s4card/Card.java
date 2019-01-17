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
package com.qlangtech.tis.s4card;

import org.apache.solr.client.solrj.beans.Field;

/* *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class Card {

    @Field("id")
    private String id;

    @Field("kind_card_id")
    private String kindCardId;

    @Field("customer_id")
    private String customerId;

    @Field("code")
    private String code;

    @Field("inner_code")
    private String innerCode;

    @Field("pwd")
    private String pwd;

    @Field("pay")
    private Double pay;

    @Field("active_date")
    private Long activeDate;

    @Field("pre_fee")
    private Double preFee;

    @Field("balance")
    private Double balance;

    @Field("gift_balance")
    private Double giftBalance;

    @Field("real_balance")
    private Double realBalance;

    @Field("degree")
    private Double degree;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKindCardId() {
        return kindCardId;
    }

    public void setKindCardId(String kindCardId) {
        this.kindCardId = kindCardId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getInnerCode() {
        return innerCode;
    }

    public void setInnerCode(String innerCode) {
        this.innerCode = innerCode;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public Double getPay() {
        return pay;
    }

    public void setPay(Double pay) {
        this.pay = pay;
    }

    public Long getActiveDate() {
        return activeDate;
    }

    public void setActiveDate(Long activeDate) {
        this.activeDate = activeDate;
    }

    public Double getPreFee() {
        return preFee;
    }

    public void setPreFee(Double preFee) {
        this.preFee = preFee;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public Double getGiftBalance() {
        return giftBalance;
    }

    public void setGiftBalance(Double giftBalance) {
        this.giftBalance = giftBalance;
    }

    public Double getRealBalance() {
        return realBalance;
    }

    public void setRealBalance(Double realBalance) {
        this.realBalance = realBalance;
    }

    public Double getDegree() {
        return degree;
    }

    public void setDegree(Double degree) {
        this.degree = degree;
    }
}
