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
import com.qlangtech.tis.trigger.netty.TriggerLogServer;
import com.qlangtech.tis.trigger.socket.ExecuteState;

/*
 * 等待实时监听客户端监听请求
 *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class TerminatorHookAppender extends AppenderSkeleton {

    // private static final Log log = LogFactory
    // .getLog(TerminatorHookAppender.class);
    // private final Selector selector;
    // private final Connections<Connection> listenerList = new
    // Connections<Connection>();
    // private final ReceiveLogServer receiveLogServer;
    private final TriggerLogServer receiveLogServer;

    public TerminatorHookAppender() throws Exception {
        super();
        receiveLogServer = new TriggerLogServer(8848);
    // Thread testSend = new Thread() {
    // @Override
    // public void run() {
    // ExecuteState state;
    // int i = 0;
    // while (true) {
    // 
    // try {
    // state = ExecuteState.create(InfoType.INFO,
    // "info xxxxxxxxxxxx hello" + (i++));
    // state.setTaskId(888l);
    // receiveLogServer.writeState(state);
    // 
    // state = ExecuteState.create(InfoType.ERROR,
    // "error jjjjjjjjjjjjjjjjj hello" + (i++));
    // state.setTaskId(888l);
    // 
    // receiveLogServer.writeState(state);
    // } catch (UnknownHostException e) {
    // 
    // }
    // 
    // try {
    // Thread.sleep(2000);
    // } catch (InterruptedException e) {
    // 
    // }
    // }
    // }
    // 
    // };
    // testSend.setDaemon(true);
    // testSend.start();
    }

    @Override
    public void close() {
    }

    @Override
    public boolean requiresLayout() {
        return false;
    }

    @Override
    protected void append(LoggingEvent event) {
        ExecuteState state = (ExecuteState) event.getProperties().get("logstat");
        if (state == null) {
            return;
        }
    // 向底部连接对象发送广播消息
    // receiveLogServer.writeState(state);
    }
    // Thread thread = new Thread(runable, "feedback thread");
    // thread.start();
    // private class ReceiveLogServer extends
    // MaintainConnectionSocketServer<Connection, ExecuteState> {
    // 
    // public ReceiveLogServer(int port) {
    // super(port);
    // }
    // 
    // @Override
    // protected Connection createSession(SocketChannel socketChannel) {
    // FeedbackRegister register = NIOUtils
    // .readObjectFromChannel(socketChannel);
    // return new Connection(socketChannel, register);
    // }
    // 
    // }
    // private class Connection extends Session<ExecuteState> {
    // private final FeedbackRegister register;
    // 
    // public Connection(SocketChannel socket, FeedbackRegister register) {
    // // super();
    // super(socket);
    // if (register == null) {
    // throw new IllegalArgumentException("register can not be null");
    // }
    // log.warn("register, serviceName:" + register.getServiceName()
    // + ",taskid:" + register.getTaskid());
    // this.register = register;
    // }
    // 
    // public boolean send(ExecuteState o) {
    // // final LoggingEvent event = (LoggingEvent) o;
    // 
    // final ExecuteState state = o;// (ExecuteState) event.getProperties()
    // // .get("logstat");
    // try {
    // 
    // if (state == null) {
    // return false;
    // }
    // 
    // if ((FeedbackRegister.MONITOR_ALL + 0l) == register.getTaskid()) {
    // return super.send(state);
    // }
    // if ((state.getTaskId() != null && register.getTaskid() != null && state
    // .getTaskId() == register.getTaskid())) {
    // return super.send(state);
    // }
    // if (register.getServiceName() != null
    // && StringUtils.equals(state.getServiceName(),
    // register.getServiceName())) {
    // // 索引服务名称相同
    // return super.send(state);
    // }
    // 
    // } finally {
    // log.debug("state.getTaskId():" + state.getTaskId()
    // + ",this.servicename:" + register.getServiceName());
    // }
    // // if (!(MONITOR_ALL == this.taskId || )) {
    // 
    // // }
    // return false;
    // 
    // }
    // 
    // 
    // 
    // }
}
