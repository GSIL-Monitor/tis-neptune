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
package com.qlangtech.tis.s4waitingUser;

import org.apache.solr.client.solrj.beans.Field;

/* *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class WaitingUser {

    @Field("waitingorder_id")
    private String waitingorderId;

    @Field("order_id")
    private String orderId;

    @Field("entity_id")
    private String entityId;

    @Field("create_time")
    private Long createTime;

    @Field("last_ver")
    private Integer lastVer;

    @Field("customer_ids")
    private String customerIds;

    @Field("_version_")
    private Long Version;

    public void setWaitingorderId(String waitingorderId) {
        this.waitingorderId = waitingorderId;
    }

    public String getWaitingorderId() {
        return this.waitingorderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderId() {
        return this.orderId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public String getEntityId() {
        return this.entityId;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Long getCreateTime() {
        return this.createTime;
    }

    public void setLastVer(Integer lastVer) {
        this.lastVer = lastVer;
    }

    public Integer getLastVer() {
        return this.lastVer;
    }

    public void setCustomerIds(String customerIds) {
        this.customerIds = customerIds;
    }

    public String getCustomerIds() {
        return this.customerIds;
    }

    public void setVersion(Long Version) {
        this.Version = Version;
    }

    public Long getVersion() {
        return this.Version;
    }
}
