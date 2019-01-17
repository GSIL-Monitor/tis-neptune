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
package com.qlangtech.tis.trigger.feedback;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;
import com.qlangtech.tis.trigger.cache.ITSearchCache;
import com.qlangtech.tis.trigger.socket.ExecuteState;

/*
 * 每次收到task消息之后就会向tair中更新一条消息，锁不失效保持两分钟
 *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class ExecuteTaskLogFreshAppender extends AppenderSkeleton {

    private final ITSearchCache cache;

    /**
     * @param cache
     */
    public ExecuteTaskLogFreshAppender(ITSearchCache cache) {
        super();
        this.cache = cache;
    }

    public ITSearchCache getCache() {
        return cache;
    }

    @Override
    public void close() {
    }

    @Override
    public boolean requiresLayout() {
        return false;
    }

    public static final String EXECUTE_TASK_LOCK_KEY = "exec_task_lock_key_";

    @Override
    protected void append(LoggingEvent event) {
        ExecuteState state = (ExecuteState) event.getProperties().get("logstat");
        if (state == null || state.getTaskId() == null) {
            return;
        }
        final String key = EXECUTE_TASK_LOCK_KEY + state.getTaskId();
        cache.increase(key);
    }
}
