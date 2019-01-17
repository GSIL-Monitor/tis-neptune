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
package com.qlangtech.tis.trigger.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/* *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class TriggerDumpResult extends HashMap<String, String> {

    public String serialize() {
        return JsonUtil.serialize(this);
    }

    public static TriggerDumpResult deserialize(String value) {
        return JsonUtil.deserialize(value, new TriggerDumpResult());
    }

    private static final long serialVersionUID = 1L;

    public void setStatus(String status) {
        this.put("status", status);
    }

    public void setTime(Date time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
        this.put("time", format.format(time));
    }

    public void setTaskId(Long taskid) {
        if (taskid == null) {
            throw new IllegalArgumentException("task id can not be null");
        }
        this.put("taskid", String.valueOf(taskid));
    }

    public Long getTaskId() {
        // }
        return Long.valueOf(this.get("taskid"));
    }

    public boolean getBoolean(String name) {
        return Boolean.parseBoolean(this.get(name));
    }

    public void setServiceName(String serviceName) {
        this.put("serviecName", serviceName);
    }

    public void setErrorMsg(String errorMsg) {
        this.put("errorMsg", errorMsg);
    }

    // //////////////////////////////
    public String getStatus() {
        return this.get("status");
    }

    public String getTime() {
        return this.get("time");
    }

    public String getServiceName() {
        return this.get("serviecName");
    }

    public String getErrorMsg() {
        return this.get("errorMsg");
    }
}
