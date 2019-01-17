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
package com.qlangtech.tis.build.log;

import java.util.Map;
import org.apache.commons.lang.StringUtils;
import com.gilt.logback.flume.FlumeLogstashV1Appender;
import com.qlangtech.tis.common.utils.TSearcherConfigFetcher;
import ch.qos.logback.classic.spi.ILoggingEvent;

/*
 * 发送日志的时候会将当前上下文MDC“app”参数发送
 *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class AppnameAwareFlumeLogstashV1Appender extends FlumeLogstashV1Appender {

    public static final String KEY_COLLECTION = "app";

    public AppnameAwareFlumeLogstashV1Appender() {
        super();
        super.setFlumeAgents(TSearcherConfigFetcher.get().getLogFlumeAddress());
    }

    public void setFlumeAgents(String flumeAgents) {
    // super.setFlumeAgents(flumeAgents);
    }

    @Override
    protected Map<String, String> extractHeaders(ILoggingEvent eventObject) {
        Map<String, String> result = super.extractHeaders(eventObject);
        final Map<String, String> mdc = eventObject.getMDCPropertyMap();
        String collection = StringUtils.defaultIfEmpty(mdc.get(KEY_COLLECTION), "unknown");
        result.put(KEY_COLLECTION, collection);
        return result;
    }
}
