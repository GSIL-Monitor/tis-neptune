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

import java.io.IOException;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.List;
import junit.framework.Assert;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionException;
import org.springframework.util.CollectionUtils;
import com.qlangtech.tis.trigger.biz.dal.dao.ITerminatorTriggerBizDalDAOFacade;
import com.qlangtech.tis.trigger.biz.dal.pojo.AppTrigger;
import com.qlangtech.tis.trigger.util.NIOUtils;

/*
 * 服务端和客户端联通的一个长连接，服务端可以通过该对象触发客户端执行全/曾量。 业务端可以将自己执行的状态报告通过该对象反馈给服务端
 *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class Connection {
    // // private final Socket socket;
    // 
    // private final TriggerJobServer server;
    // 
    // // private final ObjectInputStream in;
    // // private final ObjectOutputStream out;
    // // 读取业务方的应用名称,search4abc
    // private final String serviceName;
    // 
    // // 应用端线程id，因为要应付一个jobid在多个地方订阅服务
    // private Long clientThreadId;
    // 
    // private final AppTrigger trigger;
    // 
    // private InetAddress clientAddress;
    // 
    // private final SocketChannel socketChannel;
    // 
    // public Long getClientThreadId() {
    // return clientThreadId;
    // }
    // 
    // public List<Long> getJobsId() {
    // return trigger.getJobsId();
    // }
    // 
    // private static final Log log = LogFactory.getLog(Connection.class);
    // 
    // public TriggerJobServer getServer() {
    // return server;
    // }
    // 
    // public ITerminatorTriggerBizDalDAOFacade getTriggerBizDAO() {
    // return server.getTriggerBizDAO();
    // }
    // 
    // public Connection(TriggerJobServer server, String serviceName,
    // SocketChannel socketChannel) throws IOException {
    // super();
    // // this.socket = socket;
    // this.server = server;
    // this.socketChannel = socketChannel;
    // // 打开任务注册通
    // // this.in = new ObjectInputStream(socket.getInputStream());
    // // this.out = new ObjectOutputStream(socket.getOutputStream());
    // 
    // // initialize();
    // this.serviceName = serviceName;
    // 
    // log.info("receive serviceName from client:" + serviceName);
    // 
    // this.trigger = this.server.getJobMetaDataDAO().queryJob(serviceName);
    // 
    // Assert.assertNotNull("server name:" + serviceName
    // + " can not find trigger obj", trigger);
    // 
    // try {
    // // 1接收用户发送 full incr jobid的查询
    // initialize();
    // } catch (Exception e) {
    // this.disopse();
    // throw new RuntimeException(e);
    // }
    // }
    // 
    // private static ByteBuffer createLongArrayBuffer(Long... value)
    // throws IOException {
    // ByteBuffer jobsBuffer = ByteBuffer.allocate(value.length * 8);
    // 
    // // ByteArrayOutputStream jobsTemp = null;
    // // DataOutputStream jobsStream = null;
    // 
    // try {
    // // jobsTemp = new ByteArrayOutputStream(16);
    // // jobsStream = new DataOutputStream(jobsTemp);
    // 
    // for (int i = 0; i < value.length; i++) {
    // // jobsStream.writeLong();
    // jobsBuffer.putLong(value[i]);
    // }
    // // jobsStream.flush();
    // jobsBuffer.flip();
    // return jobsBuffer;
    // } finally {
    // // IOUtils.closeQuietly(jobsTemp);
    // }
    // }
    // 
    // private void initialize() throws IOException, ClassNotFoundException {
    // 
    // com.taobao.terminator.trigger.biz.dal.dao.TriggerJob ftrigger = trigger
    // .getFullTrigger();
    // com.taobao.terminator.trigger.biz.dal.dao.TriggerJob itrigger = trigger
    // .getIncTrigger();
    // 
    // ByteBuffer jobsBuffer = createLongArrayBuffer(
    // ftrigger != null ? ftrigger.getJobId() : 0l,
    // itrigger != null ? itrigger.getJobId() : 0l);// ByteBuffer.allocate(16);
    // // 客户端先接收fulljob id
    // // this.out.writeLong(ftrigger != null ? ftrigger.getJobId() : 0l);
    // // this.out.writeLong(itrigger != null ? itrigger.getJobId() : 0l);
    // // this.out.flush();
    // log.info(this.getServiceName() + " flush ftriggerid:"
    // + ftrigger.getJobId() + ",itriggerid:" + itrigger.getJobId()
    // + " to client");
    // this.socketChannel.write(jobsBuffer);
    // jobsBuffer.clear();
    // 
    // // 读注册对象
    // RegisterData resigerData = NIOUtils
    // .readObjectFromChannel(this.socketChannel);
    // 
    // log.info("receive a client RegisterData obj:"
    // + resigerData.getAddress().getHostAddress() + ","
    // + resigerData.getJobid()[0] + "," + resigerData.getJobid()[1]);
    // 
    // // Assert.assertTrue("obj must be a type of RegisterData",
    // // (obj instanceof RegisterData));
    // 
    // // if (obj instanceof RegisterData) {
    // // RegisterData resigerData = (RegisterData) obj;
    // 
    // // 设置客户端线程id
    // this.clientThreadId = resigerData.getThreadId();
    // 
    // if (resigerData.getAddress() != null) {
    // // build中心注册
    // // server.addJobs2TriggerManager(resigerData.getAddress(),
    // // this);
    // this.clientAddress = resigerData.getAddress();
    // // return;
    // }
    // if (resigerData.getJobid() != null) {
    // setResponsibleJobIdList(Arrays.asList(resigerData.getJobid()));
    // 
    // server.addJobs2TriggerManager(getResponsibleJobIdList(), this);
    // // return;
    // }
    // // }
    // 
    // Assert.assertNotNull("ResponsibleJobIdList can not be null",
    // this.getResponsibleJobIdList());
    // 
    // // in.reset();
    // // throw new IllegalArgumentException("obj is illegal:" + obj);
    // }
    // 
    // private List<Long> responsibleJobIdList;
    // 
    // private List<Long> getResponsibleJobIdList() {
    // return responsibleJobIdList;
    // }
    // 
    // private void setResponsibleJobIdList(List<Long> responsibleJobIdList) {
    // this.responsibleJobIdList = responsibleJobIdList;
    // }
    // 
    // public void disopse() {
    // try {
    // if (!CollectionUtils.isEmpty(this.getResponsibleJobIdList())) {
    // server.removeJobs2TriggerManager(
    // this.getResponsibleJobIdList(), this);
    // }
    // } catch (Throwable e) {
    // log.error(e.getMessage(), e);
    // }
    // // IOUtils.closeQuietly(in);
    // // IOUtils.closeQuietly(out);
    // // try {
    // // socket.close();
    // // } catch (IOException e) {
    // // log.error(e.getMessage(), e);
    // // }
    // try {
    // this.socketChannel.close();
    // } catch (Throwable e) {
    // }
    // // this.interrupt();
    // }
    // 
    // // @Override
    // // public void run() {
    // //
    // // try {
    // // startRun(0);
    // // } catch (Exception e) {
    // // this.disopse();
    // // throw new RuntimeException(e);
    // // }
    // // }
    // 
    // // private void startRun(int count) {
    // // // 接受 客户端执行状态反馈
    // // while (true) {
    // //
    // // try {
    // // Object obj = in.readObject();
    // //
    // // if (!(obj instanceof ExecuteState)) {
    // // return;
    // // }
    // //
    // // // 状态反馈接收反馈状态
    // // server.logExecState((ExecuteState) obj);
    // //
    // // } catch (SocketException e) {
    // // // 说明socket连接断掉了需要把当前connection 销毁
    // // throw new RuntimeException(e);
    // // } catch (Exception e) {
    // //
    // // // 最多 试错6次就结束
    // // if (count > 5) {
    // // throw new RuntimeException(e);
    // // }
    // //
    // // startRun(++count);
    // // log.error(e.getMessage(), e);
    // // }
    // // }
    // // }
    // 
    // /**
    // * 触发客户端执行全量DUMP
    // *
    // * @throws JobExecutionException
    // */
    // public Long triggerFullDump() throws JobExecutionException {
    // return this.trigger(this.trigger.getFullTrigger().getJobId());
    // }
    // 
    // /**
    // * @param jobid
    // * @return
    // * @throws JobExecutionException
    // */
    // public Long trigger(// final Trigger trigger,
    // final Long jobid) throws JobExecutionException {
    // 
    // // Assert.assertNotNull("trigger can not be null", trigger);
    // Assert.assertNotNull("schedule can not be null", jobid);
    // Long taskid = null;
    // // Assert.assertTrue( );
    // // 判断当前连接是否正常
    // // if (!socketChannel.isOpen()) {
    // // this.disopse();
    // // // 需要终止当前触发
    // // JobExecutionException e2 = new JobExecutionException();
    // // e2.setUnscheduleAllTriggers(true);
    // // throw e2;
    // // }
    // 
    // if (!this.trigger.getJobsId().contains(jobid)) {
    // throw new IllegalStateException("jobid:" + jobid
    // + " shall be in one of the list:" + getJobListLiterial());
    // }
    // 
    // try {
    // 
    // synchronized (this) {
    // log.info("trigger " + this.getServiceName() + "write to "
    // + this.clientAddress + " jobid:" + jobid);
    // taskid = this.server.createTask(jobid);
    // ByteBuffer triggerMsg = createLongArrayBuffer(
    // Constant.TRIGGER_TOKEN, taskid, jobid);
    // this.socketChannel.write(triggerMsg);
    // triggerMsg.clear();
    // 
    // // out.writeLong(Constant.TRIGGER_TOKEN);
    // // // 写taskid
    // // out.writeLong(this.server.createTask(jobid));
    // // // 触发客户端job
    // // out.writeLong(jobid);
    // // out.flush();
    // }
    // } catch (IOException e) {
    // 
    // log.error(
    // this.getServiceName() + ",jobid:" + jobid + ","
    // + this.clientAddress + " client may be closed"
    // + e.getMessage(), e);
    // this.disopse();
    // 
    // JobExecutionException e2 = new JobExecutionException(e);
    // // Quartz will automatically unschedule
    // // all triggers associated with this job
    // // so that it does not run again
    // e2.setUnscheduleAllTriggers(true);
    // // 将自己从连接容器中删除
    // this.server.removeConnection(this.getServiceName());
    // throw e2;
    // } catch (Exception e) {
    // log.error(
    // this.getServiceName() + "," + this.clientAddress
    // + ",jobid:" + jobid + " has occur an error \n"
    // + e.getMessage(), e);
    // 
    // try {
    // Thread.sleep(1000);
    // } catch (InterruptedException e1) {
    // 
    // }
    // }
    // 
    // return taskid;
    // }
    // 
    // private String getJobListLiterial() {
    // StringBuffer result = new StringBuffer("[");
    // for (Long jobid : this.trigger.getJobsId()) {
    // result.append(jobid).append(",");
    // }
    // result.append("]");
    // return result.toString();
    // }
    // 
    // public String getServiceName() {
    // return this.serviceName;
    // }
}
