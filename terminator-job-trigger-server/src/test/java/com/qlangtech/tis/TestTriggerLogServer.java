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

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import com.qlangtech.tis.trigger.jst.LogCollectorClientManager;
import com.qlangtech.tis.trigger.jst.LogCollectorClientManager.ILogListener;
import com.qlangtech.tis.trigger.jst.LogCollectorClientManager.MonotorTarget;
import com.qlangtech.tis.trigger.socket.ExecuteState;
import com.qlangtech.tis.trigger.socket.LogType;
import junit.framework.TestCase;

/* *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class TestTriggerLogServer extends TestCase {

    public void testConnect() throws Exception {
    // Integer port = 64512;
    // 
    // final AtomicInteger count = new AtomicInteger();
    // final AtomicBoolean unregister = new AtomicBoolean(false);
    // 
    // final LogCollectorClientManager logCollector = new LogCollectorClientManager();
    // final ILogListener listener = new ILogListener() {
    // @Override
    // public void read(ExecuteState event) {
    // 
    // //				if (count.incrementAndGet() > 20 && unregister.compareAndSet(false, true)) {
    // //					System.out.println("sned unregister signal");
    // //					logCollector.unregisterListener(
    // //							MonotorTarget.createUnregister("search4menu", LogType.FULL), this);
    // //				}
    // 
    // System.out.println("count:" + count + "," + event.getMsg());
    // 
    // }
    // };
    // 
    // logCollector.registerListener(MonotorTarget.createRegister("search4menu", LogType.FULL),
    // listener);
    // 
    // synchronized (port) {
    // port.wait();
    // }
    }
}
