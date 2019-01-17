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

import static com.qlangtech.tis.trigger.socket.Constant.TRIGGER_SERVER;
import junit.framework.TestCase;
import org.apache.zookeeper.ZooKeeper;
import com.qlangtech.tis.trigger.socket.OwnerTriggerJobGetter;
import com.qlangtech.tis.trigger.socket.Task;
import com.qlangtech.tis.trigger.socket.TaskContext;
import com.qlangtech.tis.trigger.socket.TriggerJobClient;

/* *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class TestTriggerClient extends TestCase {

    private static TriggerJobClient client;

    public void testClient() throws Exception {
    // ZooKeeper zookeeper = new ZooKeeper("10.232.15.46:2181", 30000, null);
    // 
    // OwnerTriggerJobGetter ownerTriggerJobGetter = new OwnerTriggerJobGetter() {
    // @Override
    // public Integer getOwnerTriggerJobClientHashcode() {
    // if (client == null) {
    // return null;
    // }
    // return client.hashCode();
    // }
    // 
    // };
    // 
    // client = TriggerJobClient.registerJob(new String(zookeeper.getData(
    // TRIGGER_SERVER, false, null)), "search4t3",
    // ownerTriggerJobGetter, new Task() {
    // 
    // @Override
    // protected void executeFull(TaskContext context)
    // throws Exception {
    // 
    // sendInfo(context, "i'm here,full execute,job id :"
    // + context.getJobId() + " taskid:"
    // + context.getTaskId());
    // Exception e = new Exception();
    // sendError(context, e);
    // sendError(context, "error msg 1", e);
    // sendError(context, "error msg 2");
    // 
    // System.out.println("jobid:" + context.getJobId()
    // + " taskid:" + context.getTaskId());
    // 
    // throw new UnsupportedOperationException("full");
    // }
    // 
    // @Override
    // protected void executeIncr(TaskContext context)
    // throws Exception {
    // 
    // //Thread.sleep(1000 * 60 * 2);
    // 
    // sendInfo(context, "i'm here,incr execute,job id :"
    // + context.getJobId() + " taskid:"
    // + context.getTaskId());
    // Exception e = new Exception();
    // sendError(context, e);
    // sendError(context, "incr error msg 1", e);
    // sendError(context, "incr error msg 2");
    // System.out.println("jobid:" + context.getJobId()
    // + " taskid:" + context.getTaskId());
    // throw new UnsupportedOperationException("incr");
    // }
    // 
    // @Override
    // public boolean shallExecute() {
    // return true;
    // }
    // });
    // 
    // // zookeeper.exists(TRIGGER_SERVER, new Watcher() {
    // // @Override
    // // public void process(WatchedEvent event) {
    // // if (EventType.NodeDataChanged == event.getType()
    // // || EventType.NodeCreated == event.getType()) {
    // //
    // // client.dispose();
    // // try {
    // // testClient();
    // // } catch (Exception e) {
    // // throw new RuntimeException(e);
    // // }
    // // }
    // // }
    // // });
    // 
    // Thread.sleep(9000 * 1000);
    }
}
