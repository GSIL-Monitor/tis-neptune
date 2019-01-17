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
package com.qlangtech.tis;

import java.net.Inet4Address;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Level;
import org.apache.log4j.spi.LocationInfo;
import org.apache.log4j.spi.LoggingEvent;
import junit.framework.TestCase;
import com.qlangtech.tis.trigger.feedback.TerminatorHookAppender;
import com.qlangtech.tis.trigger.socket.ExecuteState;
import com.qlangtech.tis.trigger.socket.InfoType;
import com.qlangtech.tis.trigger.socket.LogType;

/* *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class TestTerminatorHookAppender extends TestCase {

    private static final long serialVersionUID = 1L;

    public void testThis() throws Exception {
        ExecuteState state = ExecuteState.create(LogType.FULL, "hello");
        state.setFrom(Inet4Address.getLocalHost());
        state.setTaskId(123l);
        state.setTime(System.currentTimeMillis());
        TerminatorHookAppender appender = new TerminatorHookAppender();
        Map<String, Object> prop = new HashMap<String, Object>();
        prop.put("name", "search4key");
        prop.put("logstat", state);
        LoggingEvent event = new LoggingEvent(null, null, System.currentTimeMillis(), Level.INFO, state, Thread.currentThread().getName(), null, null, LocationInfo.NA_LOCATION_INFO, prop);
        while (true) {
            System.out.println("do append");
            appender.doAppend(event);
            Thread.sleep(1000);
        }
    }
}
