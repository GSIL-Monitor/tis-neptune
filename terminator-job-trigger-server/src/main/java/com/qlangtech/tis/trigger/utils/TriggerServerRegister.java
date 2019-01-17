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
package com.qlangtech.tis.trigger.utils;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.solr.common.cloud.OnReconnect;
import org.apache.solr.common.cloud.SolrZkClient;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import com.qlangtech.tis.trigger.biz.dal.dao.JobConstant;
import com.qlangtech.tis.trigger.socket.Constant;

/*
 * @description 该类是ZK分布式锁的应用，保证客户端导入<br>
 *              任务只会由集群中的一台机器启动执行 <br>
 *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class TriggerServerRegister {

    private static final Log log = LogFactory.getLog(TriggerServerRegister.class);

    protected final String basePath;

    boolean stoped = false;

    private static TriggerServerRegister distributedSystemTaskLock = null;

    // private final AtomicBoolean successConnect = new AtomicBoolean(false);
    private int distrTaskId;

    // private static class ZKReCreator extends Thread {
    // 
    // @Override
    // public void run() {
    // 
    // try {
    // 
    // while (true) {
    // synchronized (this) {
    // if (distributedSystemTaskLock != null) {
    // final String address = distributedSystemTaskLock.zkaddress;
    // distributedSystemTaskLock.close();
    // distributedSystemTaskLock = null;
    // 
    // log.warn("reCreate zookeeper client,zk address is :"
    // + address);
    // getInstance(address);
    // }
    // this.wait();
    // }
    // }
    // 
    // } catch (InterruptedException e) {
    // 
    // throw new RuntimeException(e);
    // }
    // }
    // }
    // private static final ZKReCreator creator;
    // static {
    // creator = new ZKReCreator();
    // creator.setDaemon(true);
    // creator.start();
    // 
    // // 监控zk是否是正常的
    // Thread zkMonitor = new Thread(new Runnable() {
    // @Override
    // public void run() {
    // while (true) {
    // try {
    // Thread.sleep(1000 * 60 * 2);
    // if (distributedSystemTaskLock != null) {
    // distributedSystemTaskLock.zookeeper.getChildren(
    // distributedSystemTaskLock.basePath, false);
    // }
    // } catch (Throwable e) {
    // synchronized (creator) {
    // log.warn("send a recreate zk instance signal", e);
    // creator.notifyAll();
    // }
    // }
    // }
    // }
    // });
    // 
    // zkMonitor.setDaemon(true);
    // zkMonitor.start();
    // }
    private SolrZkClient zookeeper = null;

    // private String serviceName = null;
    // private static HsfConfigSupport support;
    public static synchronized TriggerServerRegister getInstance(String zkAddress) {
        try {
            if (distributedSystemTaskLock == null) {
                synchronized (TriggerServerRegister.class) {
                    if (distributedSystemTaskLock == null) {
                        distributedSystemTaskLock = new TriggerServerRegister(zkAddress);
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return distributedSystemTaskLock;
    }

    private static final Pattern taskPattern = Pattern.compile("[1-9]\\d*$");

    static final int DEFAULT_CLIENT_CONNECT_TIMEOUT = 30000;

    // private final String zkaddress;
    private final List<OnReconnect> reconnListener = new ArrayList<OnReconnect>();

    public void addReconnect(OnReconnect conn) {
        this.reconnListener.add(conn);
    }

    private TriggerServerRegister(String zkaddress) throws Exception {
        this.zookeeper = new SolrZkClient(zkaddress, DEFAULT_CLIENT_CONNECT_TIMEOUT, DEFAULT_CLIENT_CONNECT_TIMEOUT, new OnReconnect() {

            @Override
            public void command() {
                for (OnReconnect rconn : reconnListener) {
                    rconn.command();
                }
            }
        });
        this.basePath = Constant.TRIGGER_SERVER + JobConstant.DOMAIN_TERMINAOTR;
        registerMyself();
        this.addReconnect(new OnReconnect() {

            @Override
            public void command() {
                registerMyself();
            }
        });
    }

    /**
     */
    private void registerMyself() {
        try {
            String localHostIP = InetAddress.getLocalHost().getHostAddress();
            log.info(this.basePath + "=============================this.localHostIP" + localHostIP);
            String[] pathname = StringUtils.split(this.basePath, "/");
            StringBuffer patht = new StringBuffer();
            guaranteeExist(zookeeper, patht, pathname, 0);
            final String path = zookeeper.create(this.basePath + "/registerserver", localHostIP.getBytes(), CreateMode.EPHEMERAL_SEQUENTIAL, true);
            Matcher matcher = taskPattern.matcher(path);
            if (matcher.find()) {
                this.distrTaskId = Integer.parseInt(matcher.group());
            } else {
                // throw new IllegalStateException("path:" + path
                // + " is not illegal");
                this.distrTaskId = 0;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void guaranteeExist(SolrZkClient zookeeper, StringBuffer path, String[] paths, int deepth) throws Exception {
        if (deepth >= paths.length) {
            return;
        }
        path.append("/").append(paths[deepth]);
        if (!zookeeper.exists(path.toString(), true)) {
            zookeeper.create(path.toString(), StringUtils.EMPTY.getBytes(), CreateMode.PERSISTENT, true);
        }
        guaranteeExist(zookeeper, path, paths, ++deepth);
    }

    void close() throws InterruptedException {
        this.zookeeper.close();
    }

    public int getDistrTaskId() {
        return distrTaskId;
    }

    public SolrZkClient getZookeeper() {
        return this.zookeeper;
    }

    public void process(WatchedEvent event) {
    // log.warn("receive a zk message:" + event.toString());
    // 
    // // 由于刚刚创建对象的时候也会调用一次zk的回调
    // // 所以在刚刚创建zk的时候，这个回调方法需要过滤掉
    // if (successConnect.getAndSet(true)
    // && (event.getState() == Event.KeeperState.Expired || event
    // .getState() == Event.KeeperState.SyncConnected)) {
    // synchronized (creator) {
    // creator.notifyAll();
    // }
    // }
    }

    public static void main(String[] arg) {
        Matcher m = taskPattern.matcher("/terminator-lock/trigger_server_tsearcherterminator/registerserver00000000");
        if (m.find()) {
            System.out.println(m.group());
        } else {
            System.out.println("has not find");
        }
    }
}
