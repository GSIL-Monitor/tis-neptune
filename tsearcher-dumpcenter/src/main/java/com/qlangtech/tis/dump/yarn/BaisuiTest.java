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
import java.util.Map;
import org.apache.commons.io.FileUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.yarn.api.ApplicationConstants;
import org.apache.hadoop.yarn.api.ApplicationConstants.Environment;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.ApplicationReport;
import org.apache.hadoop.yarn.api.records.ApplicationSubmissionContext;
import org.apache.hadoop.yarn.api.records.ContainerLaunchContext;
import org.apache.hadoop.yarn.api.records.LocalResource;
import org.apache.hadoop.yarn.api.records.LocalResourceType;
import org.apache.hadoop.yarn.api.records.LocalResourceVisibility;
import org.apache.hadoop.yarn.api.records.Priority;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.api.records.URL;
import org.apache.hadoop.yarn.api.records.YarnApplicationState;
import org.apache.hadoop.yarn.client.api.YarnClient;
import org.apache.hadoop.yarn.client.api.YarnClientApplication;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.util.Apps;
import org.apache.hadoop.yarn.util.ConverterUtils;
import org.apache.hadoop.yarn.util.Records;

/* *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class BaisuiTest {

    public static final String resource = "/user/admin/basui9.jar";

    public static final String PATH_YARN_SITE = "/etc/hive/conf.cloudera.hive/yarn-site.xml";

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        YarnConfiguration conf = new YarnConfiguration();
        // conf.addResource(FileUtils.openInputStream(
        // new File("D:\\tmp\\yarn-proto\\yarn-site.xml")));
        conf.addResource(FileUtils.openInputStream(new File(PATH_YARN_SITE)));
        YarnClient yarnClient = YarnClient.createYarnClient();
        yarnClient.init(conf);
        yarnClient.start();
        YarnClientApplication app = yarnClient.createApplication();
        ApplicationSubmissionContext submissionContext = app.getApplicationSubmissionContext();
        final ApplicationId appid = submissionContext.getApplicationId();
        submissionContext.setApplicationName("baisuiyarntest");
        ContainerLaunchContext amContainer = Records.newRecord(ContainerLaunchContext.class);
        amContainer.setCommands(Collections.singletonList("$JAVA_HOME/bin/java com.dfire.yarn.test.HelloWorldMaster" + " 1>" + ApplicationConstants.LOG_DIR_EXPANSION_VAR + "/stdout" + " 2>" + ApplicationConstants.LOG_DIR_EXPANSION_VAR + "/stderr"));
        // amContainer.setCommands(Collections.singletonList(
        // "echo 'hello' >/opt/var/log/hadoop-yarn/hello.txt"));
        Map<String, LocalResource> localResources = new HashMap<String, LocalResource>();
        LocalResource solrAppJarLocalResource = Records.newRecord(LocalResource.class);
        setupSolrAppJar(new Path(resource), solrAppJarLocalResource);
        localResources.put("app.jar", solrAppJarLocalResource);
        amContainer.setLocalResources(localResources);
        Map<String, String> environment = new HashMap<String, String>();
        setupAppMasterEnv(environment, conf);
        Apps.addToEnvironment(environment, Environment.CLASSPATH.name(), "/opt/cloudera/parcels/CDH-5.5.1-1.cdh5.5.1.p0.11/jars/*", File.pathSeparator);
        System.out.println("classpath:" + environment.get(Environment.CLASSPATH.name()));
        amContainer.setEnvironment(environment);
        submissionContext.setAMContainerSpec(amContainer);
        Resource capability = Records.newRecord(Resource.class);
        capability.setMemory(128);
        capability.setVirtualCores(1);
        // AM��Ҫ����Դ���
        submissionContext.setResource(capability);
        submissionContext.setQueue("default");
        Priority p = Records.newRecord(Priority.class);
        p.setPriority(1000);
        submissionContext.setPriority(p);
        System.out.println("start to submit=============================" + appid);
        yarnClient.submitApplication(submissionContext);
        System.out.println("success to submit=============================");
        ApplicationReport appReport = yarnClient.getApplicationReport(appid);
        System.out.println("get app report=============================");
        YarnApplicationState appState = appReport.getYarnApplicationState();
        while (appState != YarnApplicationState.RUNNING && appState != YarnApplicationState.KILLED && appState != YarnApplicationState.FAILED) {
            Thread.sleep(10000);
            appReport = yarnClient.getApplicationReport(appid);
            appState = appReport.getYarnApplicationState();
        }
        System.out.println("\n \n app (" + appid + ") is " + appState + "\n\n");
    }

    public static // CommandLine cli,
    void setupAppMasterEnv(Map<String, String> appMasterEnv, YarnConfiguration conf) throws IOException {
        for (String c : conf.getStrings(YarnConfiguration.YARN_APPLICATION_CLASSPATH, YarnConfiguration.DEFAULT_YARN_APPLICATION_CLASSPATH)) {
            Apps.addToEnvironment(appMasterEnv, Environment.CLASSPATH.name(), c.trim(), File.pathSeparator);
        }
        Apps.addToEnvironment(appMasterEnv, Environment.CLASSPATH.name(), Environment.PWD.$() + File.separator + "*", File.pathSeparator);
    }

    protected static void setupSolrAppJar(Path jarPath, LocalResource solrMasterJar) throws IOException {
        Configuration conf = new Configuration();
        conf.set("fs.default.name", "hdfs://10.1.7.25:8020");
        FileStatus jarStat = FileSystem.get(conf).getFileStatus(jarPath);
        URL resURI = ConverterUtils.getYarnUrlFromPath(jarStat.getPath());
        System.out.println("//////////////////////" + resURI);
        solrMasterJar.setResource(resURI);
        solrMasterJar.setSize(jarStat.getLen());
        solrMasterJar.setTimestamp(jarStat.getModificationTime());
        solrMasterJar.setType(LocalResourceType.FILE);
        solrMasterJar.setVisibility(LocalResourceVisibility.APPLICATION);
    }
}
