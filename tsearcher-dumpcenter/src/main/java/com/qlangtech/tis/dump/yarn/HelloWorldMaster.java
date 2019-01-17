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
package com.qlangtech.tis.dump.yarn;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.FileUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.yarn.api.ApplicationConstants;
import org.apache.hadoop.yarn.api.ApplicationConstants.Environment;
import org.apache.hadoop.yarn.api.records.Container;
import org.apache.hadoop.yarn.api.records.ContainerId;
import org.apache.hadoop.yarn.api.records.ContainerLaunchContext;
import org.apache.hadoop.yarn.api.records.ContainerStatus;
import org.apache.hadoop.yarn.api.records.FinalApplicationStatus;
import org.apache.hadoop.yarn.api.records.LocalResource;
import org.apache.hadoop.yarn.api.records.LocalResourceType;
import org.apache.hadoop.yarn.api.records.LocalResourceVisibility;
import org.apache.hadoop.yarn.api.records.NodeId;
import org.apache.hadoop.yarn.api.records.NodeReport;
import org.apache.hadoop.yarn.api.records.Priority;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.client.api.AMRMClient.ContainerRequest;
import org.apache.hadoop.yarn.client.api.NMClient;
import org.apache.hadoop.yarn.client.api.async.AMRMClientAsync;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.util.Apps;
import org.apache.hadoop.yarn.util.ConverterUtils;
import org.apache.hadoop.yarn.util.Records;

/* *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class HelloWorldMaster implements AMRMClientAsync.CallbackHandler {

    int numContainersToWaitFor;

    boolean isShutdown = false;

    public static void main(String[] args) throws Exception {
        HelloWorldMaster master = new HelloWorldMaster();
        master.run();
    }

    private YarnConfiguration conf;

    private NMClient nmClient;

    private Configuration getConfiguration() {
        return this.conf;
    }

    public void run() throws Exception {
        conf = new YarnConfiguration();
        conf.addResource(FileUtils.openInputStream(new File(BaisuiTest.PATH_YARN_SITE)));
        this.nmClient = NMClient.createNMClient();
        this.nmClient.init(conf);
        this.nmClient.start();
        AMRMClientAsync<ContainerRequest> rmClient = AMRMClientAsync.createAMRMClientAsync(100, this);
        rmClient.init(getConfiguration());
        rmClient.start();
        // Register with ResourceManager
        rmClient.registerApplicationMaster("", 0, "");
        // Priority for worker containers - priorities are intra-application
        Priority priority = Records.newRecord(Priority.class);
        priority.setPriority(0);
        // Resource requirements for worker containers
        Resource capability = Records.newRecord(Resource.class);
        capability.setMemory(1024 * 4);
        capability.setVirtualCores(1);
        this.numContainersToWaitFor = 1;
        // ����ڵ�
        rmClient.addContainerRequest(new ContainerRequest(capability, null, null, priority));
        System.out.println("Waiting for 1 containers to finish");
        while (!doneWithContainers()) {
            Thread.sleep(10000);
        }
        System.out.println("master application shutdown.");
        // Un-register with ResourceManager
        try {
            rmClient.unregisterApplicationMaster(FinalApplicationStatus.SUCCEEDED, "", "");
        } catch (Exception exc) {
        // safe to ignore ... this usually fails anyway
        }
    }

    protected static // CommandLine cli,
    void setupAppMasterEnv(Map<String, String> appMasterEnv, YarnConfiguration conf) throws IOException {
        for (String c : conf.getStrings(YarnConfiguration.YARN_APPLICATION_CLASSPATH, YarnConfiguration.DEFAULT_YARN_APPLICATION_CLASSPATH)) {
            Apps.addToEnvironment(appMasterEnv, Environment.CLASSPATH.name(), c.trim(), File.pathSeparator);
        }
        Apps.addToEnvironment(appMasterEnv, Environment.CLASSPATH.name(), Environment.PWD.$() + File.separator + "*", File.pathSeparator);
    }

    public synchronized boolean doneWithContainers() {
        return isShutdown || numContainersToWaitFor <= 0;
    }

    public void onContainersCompleted(List<ContainerStatus> statuses) {
        System.out.println("onContainersCompleted");
        for (ContainerStatus s : statuses) {
            System.out.println("status:" + s.getState() + ",exit_stat:" + s.getExitStatus());
        }
        synchronized (this) {
            numContainersToWaitFor--;
        }
    }

    public synchronized void onContainersAllocated(List<Container> containers) {
        // String zkHost = cli.getOptionValue("zkHost");
        // String solrArchive = cli.getOptionValue("solr");
        // String hdfsHome = cli.getOptionValue("hdfs_home");
        Configuration config = new Configuration();
        config.set("fs.default.name", "hdfs://10.1.7.25:8020");
        Path pathToRes = new Path(BaisuiTest.resource);
        FileStatus jarStat = null;
        try {
            jarStat = FileSystem.get(config).getFileStatus(pathToRes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        LocalResource solrPackageRes = Records.newRecord(LocalResource.class);
        org.apache.hadoop.yarn.api.records.URL url = ConverterUtils.getYarnUrlFromPath(jarStat.getPath());
        System.out.println("jar url:" + url);
        solrPackageRes.setResource(url);
        solrPackageRes.setSize(jarStat.getLen());
        solrPackageRes.setTimestamp(jarStat.getModificationTime());
        solrPackageRes.setType(LocalResourceType.FILE);
        solrPackageRes.setVisibility(LocalResourceVisibility.APPLICATION);
        Map<String, LocalResource> localResourcesMap = new HashMap<String, LocalResource>();
        localResourcesMap.put("app2.jar", solrPackageRes);
        for (Container container : containers) {
            ContainerId containerId = container.getId();
            // increment the port if running on the same host
            // int jettyPort = nextPort++;
            // String jettyHost = container.getNodeId().getHost();
            // Set<Integer> portsOnHost = solrHosts.get(jettyHost);
            // if (portsOnHost == null) {
            // portsOnHost = new HashSet<Integer>();
            // solrHosts.put(jettyHost, portsOnHost);
            // }
            // portsOnHost.add(jettyPort);
            // log.info("Added port " + jettyPort + " to host: " + jettyHost);
            // try {
            ContainerLaunchContext ctx = Records.newRecord(ContainerLaunchContext.class);
            ctx.setLocalResources(localResourcesMap);
            try {
                final Map<String, String> environment = new HashMap<String, String>();
                BaisuiTest.setupAppMasterEnv(environment, conf);
                Apps.addToEnvironment(environment, Environment.CLASSPATH.name(), "/opt/cloudera/parcels/CDH-5.5.1-1.cdh5.5.1.p0.11/jars/*", File.pathSeparator);
                ctx.setEnvironment(environment);
                System.out.println("classpath:" + environment.get(Environment.CLASSPATH.name()));
                NodeId nodeId = container.getNodeId();
                System.out.println("=========containerId:" + containerId + ",node:" + nodeId.getHost());
                // String cmd = String.format(command, jettyPort,
                // randomStopKey);
                // log.info("\n\nRunning command: " + cmd);
                ctx.setCommands(Collections.singletonList("$JAVA_HOME/bin/java com.dfire.yarn.test.HelloWorldContainer " + containerId.toString() + " " + nodeId.getHost() + " " + nodeId.getPort() + " 1>" + ApplicationConstants.LOG_DIR_EXPANSION_VAR + "/stdout" + " 2>" + ApplicationConstants.LOG_DIR_EXPANSION_VAR + "/stderr"));
                nmClient.startContainer(container, ctx);
                System.out.println("send msg to launcher the container");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        // } catch (Exception exc) {
        // log.error("Failed to start container to run Solr on port "
        // + jettyPort + " due to: " + exc, exc);
        // }
        }
    }

    public void onShutdownRequest() {
        System.out.println("onShutdownRequest");
        this.isShutdown = true;
    }

    public void onNodesUpdated(List<NodeReport> updatedNodes) {
        System.out.println("onNodesUpdated");
    }

    public float getProgress() {
        System.out.println("getProgress");
        return 0;
    }

    public void onError(Throwable e) {
        e.printStackTrace();
    }
}
