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
package com.qlangtech.tis.trigger.jst;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import com.google.common.collect.Sets;
import com.qlangtech.tis.trigger.jst.LogCollectorClientManager.ILogListener;
import com.qlangtech.tis.trigger.jst.LogCollectorClientManager.MonotorTarget;
import com.qlangtech.tis.trigger.jst.LogCollectorClientManager.TopicTagStatus;
import com.qlangtech.tis.trigger.socket.ExecuteState;
import com.qlangtech.tis.trigger.socket.LogType;
import junit.framework.Assert;
import junit.framework.TestCase;

/* *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class TestLogCollectorClientManager extends TestCase {

    // public void testTopicTagStatus() throws Exception {
    // LogCollectorClientManager clientManager =
    // LogCollectorClientManager.getInstance();
    // MonotorTarget monitorTarget = MonotorTarget.createRegister("search4totalpay",
    // LogType.MQ_TAGS_STATUS);
    // 
    // // StringBuffer tagsParams = new StringBuffer(
    // //
    // "&topic=binlogmsg,instancedetail,totalpayinfo,order_bill,specialfee,servicebillinfo,takeout_order_extra,ent_expense,payinfo,orderdetail,ent_expense_order&topic=binlogorder,instancedetail,totalpayinfo,order_bill,specialfee,servicebillinfo,takeout_order_extra,ent_expense,payinfo,orderdetail,ent_expense_order");
    // 
    // StringBuffer tagsParams = new
    // StringBuffer("&topic=binlogorder,servicebillinfo");
    // 
    // Map<String, TopicTagStatus> topicTagStatus = new HashMap<>();
    // 
    // while (true) {
    // 
    // clientManager.getTopicTagStatus(monitorTarget, tagsParams, topicTagStatus);
    // 
    // Assert.assertNotNull(topicTagStatus);
    // Assert.assertTrue(!topicTagStatus.isEmpty());
    // 
    // TopicTagStatus tagStatus = topicTagStatus.get("servicebillinfo");
    // 
    // System.out.println(tagStatus);
    // Thread.sleep(2000);
    // }
    // 
    // // System.out.println(topicTagStatus.toString());
    // }
    public void testReadFile() throws Exception {
        LogCollectorClientManager clientManager = LogCollectorClientManager.getInstance();
        MonotorTarget monitorTarget = MonotorTarget.createRegister("search4supplyGoods", LogType.MQ_TAGS_STATUS);
        // MonotorTarget monitorincrTarget =
        // MonotorTarget.createRegister("search4totalpay", LogType.INCR);
        // 
        // MonotorTarget monitorAssembleTarget =
        // MonotorTarget.createRegister("search4totalpay", LogType.FULL);
        Set<MonotorTarget> monitorTargetSet = Sets.newHashSet(monitorTarget);
        final ILogListener l1 = new ILogListener() {

            @Override
            public Set<MonotorTarget> getMonitorTypes() {
                return monitorTargetSet;
            }

            @Override
            public void read(ExecuteState<?> event) {
                // if (event.getLogType() == LogType.FULL) {
                System.out.println(event.serializeJSON());
            // }
            // else {
            // System.out.println("l1:" + event.getCollectionName() + ",type" +
            // event.getLogType());
            // }
            }
        };
        // ILogListener l2 = new ILogListener() {
        // @Override
        // public Set<MonotorTarget> getMonitorTypes() {
        // return monitorTargetSet;
        // }
        // 
        // @Override
        // public void read(ExecuteState event) {
        // System.out.println("l2:" + event.getCollectionName());
        // }
        // };
        clientManager.registerListener(l1);
        clientManager.registerMonitorEvent(monitorTarget);
        synchronized (clientManager) {
            clientManager.wait();
        }
    }
}
