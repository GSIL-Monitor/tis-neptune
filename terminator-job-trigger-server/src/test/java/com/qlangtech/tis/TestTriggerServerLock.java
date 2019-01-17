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

import junit.framework.TestCase;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import com.qlangtech.tis.trigger.utils.TriggerServerRegister;

/* *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class TestTriggerServerLock extends TestCase {

    public void testGetLock() throws Exception {
        ZooKeeper zookeeper = new ZooKeeper("10.232.15.46:2181", 30000, null);
    // TriggerServerLock lock = TriggerServerLock.getInstance(zookeeper);
    // 
    // boolean getlock = lock.localhostCanRun(new Watcher() {
    // @Override
    // public void process(WatchedEvent event) {
    // try {
    // testGetLock();
    // } catch (Exception e) {
    // throw new RuntimeException(e);
    // }
    // }
    // });
    // 
    // if (getlock) {
    // System.out.println("i have get the lock");
    // } else {
    // System.out.println("cant get the lock");
    // }
    // //
    // // Thread.sleep(3000);
    // 
    // // zookeeper.create("/terminator-lock/trigger_server", "127.0.0.1"
    // // .getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
    // 
    // // zookeeper.close();
    // 
    // Thread.sleep(1000000);
    }
}
