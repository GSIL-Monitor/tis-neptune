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
package com.qlangtech.tis.trigger.socket;

import java.nio.channels.SocketChannel;
import com.qlangtech.tis.trigger.util.Assert;

/* *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class TaskContext {

    private final long jobId;

    private final long taskId;

    private final SocketChannel channel;

    private final CoreTriggerJob triggerJob;

    private final String serviceName;

    public SocketChannel getChannel() {
        return channel;
    }

    public TaskContext(long jobId, long taskId, String serviceName, SocketChannel channel, CoreTriggerJob triggerJob) {
        super();
        this.jobId = jobId;
        this.taskId = taskId;
        this.channel = channel;
        Assert.assertNotNull(triggerJob);
        this.triggerJob = triggerJob;
        this.serviceName = serviceName;
    }

    public String getServiceName() {
        return serviceName;
    }

    public CoreTriggerJob getTriggerJob() {
        return triggerJob;
    }

    public boolean isFullJob() {
        return this.triggerJob.getFulljobid() == jobId;
    }

    public boolean isIncrJob() {
        return this.triggerJob.getIncrjobid() == jobId;
    }

    public long getJobId() {
        return jobId;
    }

    public long getTaskId() {
        return taskId;
    }
}
