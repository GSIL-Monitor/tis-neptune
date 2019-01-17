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

import java.io.Serializable;

/*
 * 反馈信息订阅对象，表明客户端关注什么事件
 *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class FeedbackRegister implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final Long MONITOR_ALL = 0l;

    private FeedbackRegister() {
        super();
    }

    private Long taskid;

    private String serviceName;

    public static FeedbackRegister createTaskMonitor(Long taskid) {
        FeedbackRegister register = new FeedbackRegister();
        register.taskid = taskid;
        return register;
    }

    public static FeedbackRegister createIndexServiceMonitor(String indexName) {
        FeedbackRegister register = new FeedbackRegister();
        register.serviceName = indexName;
        return register;
    }

    public Long getTaskid() {
        return taskid;
    }

    public String getServiceName() {
        return serviceName;
    }
}
