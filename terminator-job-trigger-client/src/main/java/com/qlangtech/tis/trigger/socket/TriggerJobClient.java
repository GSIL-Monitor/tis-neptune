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

import static com.qlangtech.tis.trigger.socket.Constant.TRIGGER_SERVER_PORT;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.LongBuffer;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.zookeeper.ZooKeeper;
import com.qlangtech.tis.trigger.exception.TriggerJobNotValidException;
import com.qlangtech.tis.trigger.util.Assert;
import com.qlangtech.tis.trigger.util.NIOUtils;

/* *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class TriggerJobClient {

    // private static final int MAX_RECONNECT_COUNT = 15;// Integer.MAX_VALUE;
    private static final ExecutorService threadPool = java.util.concurrent.Executors.newCachedThreadPool();

    private final String ipaddress;

    private final int port;

    private static final Log log = LogFactory.getLog(TriggerJobClient.class);

    // private SocketChannel channel;
    // private final Task task;
    private final String serverName;

    private final OwnerTriggerJobGetter ownerTriggerJobGetter;

    private final SocketChannel socketChannel;

    private TriggerJobClient(String triggerServerAddress, int port, String serverName, OwnerTriggerJobGetter ownerTriggerJobGetter) {
        super();
        Assert.assertNotNull(serverName);
        Assert.assertNotNull(triggerServerAddress);
        Assert.assertTrue(port > 0);
        this.ipaddress = triggerServerAddress;
        this.port = port;
        this.serverName = serverName;
        // this.task = task;
        this.ownerTriggerJobGetter = ownerTriggerJobGetter;
        try {
            // Selector selector = Selector.open();
            this.socketChannel = this.createConnect();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // private void createConnect() {
    // synchronized (this) {
    // closeSocket();
    // try {
    // this.socket = new Socket(this.getIpaddress(), this.getPort());
    // log("success connection to the job register server");
    // this.dataoutputstream = new ObjectOutputStream(socket
    // .getOutputStream());
    // this.datareader = new ObjectInputStream(socket.getInputStream());
    // } catch (Exception e) {
    // throw new RuntimeException("the connection of trigger server:"
    // + this.serverName + ",ipaddress:" + this.getIpaddress()
    // + ":" + this.port + " can not be established", e);
    // }
    // }
    // }
    private CoreTriggerJob coreTriggerJob;

    private SocketChannel createConnect() throws Exception {
        synchronized (this) {
            SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress(this.getIpaddress(), this.getPort()));
            socketChannel.configureBlocking(true);
            ByteBuffer serviceName = ByteBuffer.wrap(this.serverName.getBytes());
            socketChannel.write(serviceName);
            serviceName.clear();
            ByteBuffer jobsid = ByteBuffer.allocate(16);
            int readCount = socketChannel.read(jobsid);
            Assert.assertEquals(16, readCount);
            jobsid.flip();
            // DataInputStream jobsReader = new DataInputStream(
            // new ByteArrayInputStream(jobsid.array(), 0, 16));
            long fjobid = jobsid.getLong();
            long ijobid = jobsid.getLong();
            log.info("fjob:" + fjobid + ",ijob:" + ijobid);
            this.coreTriggerJob = new CoreTriggerJob(fjobid, ijobid);
            // IOUtils.closeQuietly(jobsReader);
            jobsid.clear();
            NIOUtils.writeObject(socketChannel, new RegisterData(Thread.currentThread().hashCode(), Inet4Address.getLocalHost(), new Long[] { fjobid, ijobid }));
            return socketChannel;
        }
    }

    // public static void writeObject(SocketChannel channel, Object obj)
    // throws IOException {
    // 
    // ByteArrayOutputStream objContent = new ByteArrayOutputStream();
    // ObjectOutputStream outStream = new ObjectOutputStream(objContent);
    // 
    // outStream.writeObject(obj);
    // 
    // ByteBuffer content = ByteBuffer.allocate(objContent.size() + 4);
    // content.putInt(objContent.size());
    // content.put(objContent.toByteArray(), 0, objContent.size());
    // content.flip();
    // channel.write(content);
    // content.clear();
    // IOUtils.closeQuietly(outStream);
    // 
    // }
    private void closeSocket() {
        // IOUtils.closeQuietly(dataoutputstream);
        // IOUtils.closeQuietly(datareader);
        // try {
        // this.socketChannel.close();
        IOUtils.closeQuietly(this.socketChannel);
    // } catch (Throwable e) {
    // 
    // }
    }

    public void dispose() {
        closeSocket();
        this.stoped = true;
    }

    // private CoreTriggerJob getServiceJobInfo() {
    // try {
    // this.dataoutputstream.writeObject(this.serverName);
    // // long fulljobid, long incrjobid
    // return new CoreTriggerJob(this.datareader.readLong(),
    // this.datareader.readLong());
    // } catch (IOException e) {
    // throw new RuntimeException(e);
    // }
    // }
    // private static final Map<Task, TriggerJobClient>
    // triggerJobClientRepository = Collections
    // .synchronizedMap(new HashMap<Task, TriggerJobClient>());
    /**
     * @param ipaddress
     *            触发中心服务器地址
     * @param serverName
     *            search4xxxxx
     * @param task
     * @throws TriggerJobNotValidException
     */
    public static TriggerJobClient registerJob(final String ipaddress, final String serverName, OwnerTriggerJobGetter ownerTriggerJobGetter) throws TriggerJobNotValidException {
        // 创建与服务连接
        final TriggerJobClient cleint = new TriggerJobClient(ipaddress, TRIGGER_SERVER_PORT, serverName, ownerTriggerJobGetter);
        return cleint;
    // TriggerServiceAddressGetter serviceAddressGetter = new
    // TriggerServiceAddressGetter(
    // zookeeper);
    // 触发中心service 地址
    // final String ipaddress = serviceAddressGetter
    // .getServiceAddress(new TriggerServiceAddressGetter.Callback() {
    // @Override
    // public void execute(ZooKeeper zookeeper) {
    // try {
    // registerJob(zookeeper, serverName, task);
    // } catch (TriggerJobNotValidException e) {
    // throw new RuntimeException(e);
    // }
    // }
    // });
    // if (ipaddress == null) {
    // throw new IllegalStateException("path:" + Constant.TRIGGER_SERVER
    // + " in zk is null," + zookeeper);
    // }
    // TriggerJobClient cleint = triggerJobClientRepository.get(task);
    // 是否不需要更新？
    // if (!shallRefeshTriggerClient(cleint, ipaddress)) {
    // // 是的不需要更新
    // return;
    // }
    // 
    // // 客户端到服务器端的连接已经被中断？
    // if (cleint != null && cleint.isStoped()) {
    // triggerJobClientRepository.remove(task);
    // return;
    // }
    // final CoreTriggerJob triggerJob = cleint.getServiceJobInfo();
    // if (triggerJob.isInvalid()) {
    // throw new TriggerJobNotValidException(serverName);
    // }
    // // triggerJobClientRepository.put(task, cleint);
    // 
    // final TriggerJobClient c = cleint;
    // 
    // threadPool.execute(new Runnable() {
    // @Override
    // public void run() {
    // c.registerJob(triggerJob, task, 0);
    // }
    // });
    }

    /**
     * 客户端的触发器是否应该被更新？
     *
     * @param cleint
     * @param ipaddress
     *            当前trigger Server ip
     * @return
     */
    // private static boolean shallRefeshTriggerClient(TriggerJobClient cleint,
    // String ipaddress) {
    // if (cleint == null) {
    // return true;
    // }
    // // 当前trigger server的地址和 本地执行的trigger server地址相同吗？
    // if (!StringUtils.equals(cleint.getIpaddress(), ipaddress)) {
    // cleint.dispose();
    // return true;
    // }
    // return false;
    // }
    /**
     * 注册任务，该任务负责多个任务的执行
     *
     * @param address
     * @param task
     * @param executeindex
     */
    // private void registerJob(final InetAddress address, Task task,
    // int executeindex) {
    // applyAndWaitTrigger(new DataSender() {
    // @Override
    // public void send(ObjectOutputStream output) throws IOException {
    // output.writeObject(new RegisterData(Thread.currentThread()
    // .hashCode(), address, null));
    // }
    // }, task, executeindex);
    // }
    // public void registerJob(InetAddress address, Task task) {
    // registerJob(address, task, 0);
    // }
    /**
     * 最大重试次数
     *
     * @param jobid
     * @param task
     * @param executeindex
     */
    // private void registerJob(final int executeindex) {
    // 
    // Assert.assertNotNull("this.task can not be null" + this.task);
    // final CoreTriggerJob triggerJob = this.getServiceJobInfo();
    // 
    // threadPool.execute(new Runnable() {
    // @Override
    // public void run() {
    // try {
    // applyAndWaitTrigger(new DataSender() {
    // @Override
    // public void send(ObjectOutputStream output)
    // throws IOException {
    // output.writeObject(new RegisterData(Thread
    // .currentThread().hashCode(), InetAddress
    // .getLocalHost(), triggerJob.getJobArray()));
    // }
    // }, task, triggerJob, executeindex);
    // } catch (EOFException e) {
    // createConnect();
    // run();
    // }
    // }
    // });
    // }
    public static long TRIGGER_TOKEN = 21386L;

    public String getIpaddress() {
        return ipaddress;
    }

    public int getPort() {
        return port;
    }

    /**
     * @param jobid
     * @param task
     * @param executeindex
     */
    public void waitTrigger(// long jobid,
    final Task task) throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.allocate(24);
        while (true) {
            byteBuffer.clear();
            socketChannel.read(byteBuffer);
            byteBuffer.flip();
            LongBuffer jobs = byteBuffer.asLongBuffer();
            long token = jobs.get(0);
            long taskid = jobs.get(1);
            long jobid = jobs.get(2);
            Assert.assertNotNull(this.coreTriggerJob);
            // taskid, socketChannel);
            try {
                // 等待触发
                if (TRIGGER_TOKEN != token) {
                    return;
                }
                log(this.serverName + " receive trigger server data readRoken");
                // 读取jobId
                final TaskContext taskContext = new TaskContext(jobid, taskid, this.serverName, this.socketChannel, this.coreTriggerJob);
                if (this.ownerTriggerJobGetter.getOwnerTriggerJobClientHashcode() != null && this.ownerTriggerJobGetter.getOwnerTriggerJobClientHashcode() != this.hashCode()) {
                    // 判断当前的这个client已经被其他Client取代了
                    Task.sendMessage(InfoType.WARN, taskContext, "this client has be replace with another new client  ");
                    this.dispose();
                    return;
                }
                // 查看本地机器是否有执行任务的权限
                if (!task.shallExecute()) {
                    Task.sendMessage(InfoType.WARN, taskContext, "the local server has not gain the dump lock,so exit ");
                    this.dispose();
                    return;
                }
                threadPool.execute(new Runnable() {

                    @Override
                    public void run() {
                        // 执行任务
                        task.execute(taskContext);
                    }
                });
            // }
            // } catch (EOFException e) {
            // throw e;
            // } catch (IOException e) {
            // processException(sender, task, executeindex, e, triggerJob);
            } finally {
            // try {
            // socket.close();
            // } catch (IOException e) {
            // 
            // }
            // IOUtils.closeQuietly(datareader);
            }
        }
    // ////////////////////////////////////////////////////////////////////////
    }

    private void log(String msg) {
        log.warn(msg);
        System.out.println(msg);
    }

    /**
     * 该客户端是否被终止
     */
    private boolean stoped = false;

    public boolean isStoped() {
        return stoped;
    }

    /**
     * 处理异常服务器端异常
     *
     * @param sender
     * @param task
     * @param executeindex
     * @param e
     * @param triggerJob
     */
    // private void processException(DataSender sender, final Task task,
    // int executeindex, Exception e, CoreTriggerJob triggerJob) {
    // 
    // // 如果超过了最大重试次数则抛出异常，并且退出
    // if (stoped || executeindex >= MAX_RECONNECT_COUNT) {
    // // 说明链接关闭了，需要重新连接
    // throw new RuntimeException("appname:" + serverName, e);
    // }
    // 
    // try {
    // // Thread.sleep(6000 * (long) Math.pow(2, executeindex));
    // Thread.sleep(6000);
    // } catch (InterruptedException e1) {
    // 
    // }
    // 
    // // 重新尝试连接
    // try {
    // this.createConnect();
    // } catch (Throwable e1) {
    // log.error(e1.getMessage(), e1);
    // processException(sender, task, ++executeindex, e, triggerJob);
    // }
    // 
    // triggerJob = this.getServiceJobInfo();
    // if (triggerJob.isInvalid()) {
    // throw new IllegalStateException("appname:" + serverName
    // + ",illegal trigger crontab ,please check the core trigger");
    // }
    // 
    // StringWriter errWriter = new StringWriter();
    // e.printStackTrace(new PrintWriter(errWriter));
    // 
    // log("start to connect register server,retry count :" + executeindex
    // + "\n" + errWriter.toString() + " server address:"
    // + this.getIpaddress() + ",port:" + this.getPort());
    // 
    // try {
    // applyAndWaitTrigger(sender, task, triggerJob, ++executeindex);
    // } catch (EOFException e1) {
    // throw new RuntimeException(e1);
    // }
    // }
    // private interface DataSender {
    // void send(ObjectOutputStream output) throws IOException;
    // }
    public static void main(String[] arg) throws Exception {
        // Client client = new Client();
        // long jobid = 123456L;
        System.out.println("start to work.......");
        ZooKeeper zookeeper = new ZooKeeper("10.232.15.46:2181", 30000, null);
        System.out.println(zookeeper.getChildren("/", false));
    // TriggerJobClient.registerJob(new String(zookeeper.getData(
    // TRIGGER_SERVER, false, new Stat())), "search4t1", null,
    // new Task() {
    // 
    // @Override
    // public boolean shallExecute() {
    // return true;
    // }
    // 
    // @Override
    // protected void executeFull(TaskContext context)
    // throws Exception {
    // sendInfo(context, "i'm here,full execute,job id :"
    // + context.getJobId());
    // Exception e = new Exception();
    // sendError(context, e);
    // System.out.println("jobid:" + context.getJobId()
    // + " taskid:" + context.getTaskId());
    // }
    // 
    // @Override
    // protected void executeIncr(TaskContext context)
    // throws Exception {
    // sendInfo(context, "i'm here,incr execute,job id :"
    // + context.getJobId());
    // Exception e = new Exception();
    // sendError(context, e);
    // System.out.println("jobid:" + context.getJobId()
    // + " taskid:" + context.getTaskId());
    // }
    // });
    // System.out
    // .println("initial1==========================================");
    // 
    // TriggerJobClient.registerJob(zookeeper, "search4t3", new Task() {
    // @Override
    // protected void executeFull(TaskContext context) throws Exception {
    // 
    // }
    // 
    // @Override
    // protected void executeIncr(TaskContext context) throws Exception {
    // 
    // }
    // });
    // System.out
    // .println("initial2==========================================");
    // 
    // TriggerJobClient.registerJob(zookeeper, "search4t2", new Task() {
    // @Override
    // protected void executeFull(TaskContext context) throws Exception {
    // 
    // }
    // 
    // @Override
    // protected void executeIncr(TaskContext context) throws Exception {
    // 
    // }
    // });
    // 
    // System.out.println("waitting.......");
    // 
    // Thread.sleep(1000000);
    }
}
