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

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.security.auth.login.Configuration;
import org.apache.commons.collections.Closure;
import org.apache.commons.collections.CollectionUtils;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import com.qlangtech.tis.cloud.ipc.parameter.CoreRequest;
import com.qlangtech.tis.manage.client.CoreManagerClient;
import com.qlangtech.tis.pubhook.common.RunEnvironment;

/* *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class PubService {

    /**
     * @param appname
     * @param groupSize
     * @param replicaCount
     * @param runtime
     * @param servers
     */
    public PubService(String appname, int groupSize, int replicaCount, RunEnvironment runtime, List<String> servers) {
        super();
        this.appname = appname;
        this.groupSize = groupSize;
        this.replicaCount = replicaCount;
        this.runtime = runtime;
        this.servers = servers;
    }

    public static void main(String[] args) throws Exception {
        // System.getProperty("appname");
        String appname = System.getProperty("appname");
        int groupSize = Integer.parseInt(System.getProperty("groupsize"));
        int replicaCount = Integer.parseInt(System.getProperty("replicaCount"));
        if (groupSize < 1 || replicaCount < 1) {
            throw new IllegalArgumentException("groupsize or replica count has not set");
        }
        RunEnvironment runtime = RunEnvironment.getEnum(System.getProperty("runtime"));
        String ipfile = System.getProperty("ipfile");
        final File sourceFile = new File(ipfile);
        if (!sourceFile.exists()) {
            throw new IllegalArgumentException("file " + sourceFile.getAbsolutePath() + " is not exist");
        }
        List<String> servers = IpFileGenerator.getIpLines(sourceFile, runtime);
        final StringBuffer serversLiteria = new StringBuffer();
        CollectionUtils.forAllDo(servers, new Closure() {

            @Override
            public void execute(Object input) {
                serversLiteria.append(input).append(",");
            }
        });
        System.out.println("appname:" + appname + ",groupsize:" + groupSize + ",replicaCount:" + replicaCount + ",runtime:" + runtime + ",servers:" + serversLiteria.toString());
        String executeType = System.getProperty("execute_type");
        PubService pubService = null;
        if ("addreplic".equals(executeType)) {
            pubService = new AddReplic(appname, groupSize, replicaCount, runtime, servers);
        } else if ("ic".equals(executeType)) {
            String zkaddress = System.getProperty("zkaddress");
            ZooKeeper zk = createZK(zkaddress);
            pubService = new ICPubService(appname, groupSize, replicaCount, runtime, servers, zk);
        } else if ("classic".equals(executeType)) {
            pubService = new PubService(appname, groupSize, replicaCount, runtime, servers);
        } else {
            throw new IllegalStateException("you have not set sys param'execute_type' propery");
        }
        final String centerNodeAddress = System.getProperty("centerNode");
        pubService.setClientProtocol(createCoreManagerClient(centerNodeAddress));
        pubService.publishNewCore();
    }

    /**
     * @return
     * @throws IOException
     */
    public static ZooKeeper createZK(String zkaddress) throws IOException {
        if (zkaddress == null) {
            throw new IllegalStateException("zkaddress can not be null");
        }
        ZooKeeper zk = new ZooKeeper(zkaddress, 3000, new Watcher() {

            @Override
            public void process(WatchedEvent event) {
            }
        });
        return zk;
    }

    private String appname;

    private int groupSize;

    private int replicaCount;

    private RunEnvironment runtime;

    private List<String> servers;

    void preExecuteProcess() throws Exception {
    }

    void postExecuteProcess(Map<Integer, Set<String>> ips) throws Exception {
    }

    public Map<Integer, Set<String>> createPublishNewCoreParam() throws Exception {
        Map<Integer, Set<String>> ips = new HashMap<Integer, Set<String>>();
        preExecuteProcess();
        addIpsToGroup(ips);
        postExecuteProcess(ips);
        return ips;
    }

    /**
     * @param ips
     */
    protected void addIpsToGroup(Map<Integer, Set<String>> ips) {
        if (servers == null) {
            throw new IllegalStateException("servers can not be null");
        }
        System.out.println("have servers:" + servers.size() + " group:" + groupSize);
        int accumulator = 0;
        aa: for (int groupIndex = 0; groupIndex < groupSize; groupIndex++) {
            Set<String> groupServers = new HashSet<String>();
            for (int i = 0; i < replicaCount; i++) {
                groupServers.add(servers.get(accumulator++));
                if (accumulator >= servers.size()) {
                    accumulator = 0;
                // if ("true".equalsIgnoreCase(System
                // .getProperty("fillserver"))) {
                // accumulator = 0;
                // } else {
                // ips.put(groupIndex, groupServers);
                // break aa;
                // }
                }
            }
            ips.put(groupIndex, groupServers);
        }
    }

    /**
     * @param appname
     * @param groupSize
     * @param replicaCount
     * @param runtime
     * @param servers
     * @throws IOException
     */
    public void publishNewCore() throws Exception {
        Map<Integer, Set<String>> ips = createPublishNewCoreParam();
        FCoreRequest request = createCoreRequest(appname, groupSize, runtime, ips);
        StringBuffer serverSummary = new StringBuffer();
        for (Map.Entry<Integer, Set<String>> entry : ips.entrySet()) {
            serverSummary.append("group:").append(entry.getKey()).append("[");
            for (String ip : entry.getValue()) {
                serverSummary.append(ip).append(",");
            }
            serverSummary.append("]\n");
        }
        System.out.println("construct servers \n" + serverSummary.toString());
        publishNewService(request.getRequest(), request.getReplicCount(), groupSize);
    }

    public static FCoreRequest createCoreRequest(String serviceName, int group, RunEnvironment runtime, Map<Integer, Set<String>> ips) {
        final int assignGroupCount = 0;
        FCoreRequest result = new FCoreRequest(createIps(serviceName, runtime), assignGroupCount + group, assignGroupCount);
        for (int i = 0; i < group; i++) {
            final Set<String> groupips = ips.get(i);
            if (groupips == null) {
                throw new IllegalArgumentException("group:" + i + " ips can not be null");
            }
            for (String ip : groupips) {
                result.addNodeIps((assignGroupCount + i), ip);
            }
        }
        return result;
    }

    protected static CoreRequest createIps(final String appName, RunEnvironment runtime) {
        CoreRequest request = new CoreRequest();
        return request;
    }

    protected void publishNewService(CoreRequest request, short[] replication, int groupNum) throws IOException {
        if (replication.length != groupNum) {
            throw new IllegalArgumentException("replication.length(" + replication.length + ") != groupNum:" + groupNum);
        }
        for (int i = 0; i < replication.length; i++) {
            if (replication[i] < 1) {
                throw new IllegalArgumentException("group:" + i + " replic must big than zero");
            }
        }
        clientProtocol.publishNewService(request, replication, (short) groupNum);
    }

    private CoreManagerClient clientProtocol;

    public CoreManagerClient getClientprotocol() {
        return clientProtocol;
    }

    public void setClientProtocol(CoreManagerClient clientProtocol) {
        this.clientProtocol = clientProtocol;
    }

    public static CoreManagerClient createCoreManagerClient(final String centerNodeAddress) {
        return null;
    }

    public String getAppname() {
        return appname;
    }

    public void setAppname(String appname) {
        this.appname = appname;
    }

    public int getGroupSize() {
        return groupSize;
    }

    public void setGroupSize(int groupSize) {
        this.groupSize = groupSize;
    }

    public int getReplicaCount() {
        return replicaCount;
    }

    public void setReplicaCount(int replicaCount) {
        this.replicaCount = replicaCount;
    }

    public RunEnvironment getRuntime() {
        return runtime;
    }

    public void setRuntime(RunEnvironment runtime) {
        this.runtime = runtime;
    }

    public List<String> getServers() {
        return servers;
    }

    public void setServers(List<String> servers) {
        this.servers = servers;
    }
}
