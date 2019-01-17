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
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.qlangtech.tis.cloud.ipc.parameter.CoreRequest;
import com.qlangtech.tis.manage.client.CoreManagerClient;
import com.qlangtech.tis.pubhook.common.ConfigConstant;
import com.qlangtech.tis.pubhook.common.RunEnvironment;

/* *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class AddService {

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
        // readfile
        final File sourceFile = new File(ipfile);
        if (!sourceFile.exists()) {
            throw new IllegalArgumentException("file " + sourceFile.getAbsolutePath() + " is not exist");
        }
        // Pattern rowPattern = Pattern
        // .compile("(\\S+)\\s+" + appname + "-(\\d+)");
        // 
        // String line = null;
        // String serverName = null;
        // // int groupIndex = 0;
        // BufferedReader reader = null;
        Map<Integer, Set<String>> ips = new HashMap<Integer, Set<String>>();
        // short[] replication = new short[groupSize];
        List<String> servers = IpFileGenerator.getIpLines(sourceFile, runtime);
        List<Pair> pairs = IpFileGenerator.getIpPair(servers);
        CoreRequest request = createIps(appname, runtime);
        int[] cores = new int[groupSize];
        for (Pair pair : pairs) {
            request.addNodeIps(String.valueOf(pair.core.replace(appname + "-", "")), pair.ip);
            cores[Integer.parseInt(pair.core.replace(appname + "-", ""))] += 1;
        }
        clientProtocol.addReplication(request, cores);
    }

    // private static class ParseIpResult {
    // private boolean valid;
    // private String[] ips;
    // // private final StringBuffer ipLiteria = new StringBuffer();
    // }
    private static CoreRequest createIps(final String appName, RunEnvironment runtime) {
        CoreRequest request = new CoreRequest();
        request.setIncludeIps(null);
        request.setServiceName(appName);
        Assert.assertNotNull("must set terminatorHost", System.getProperty("terminatorHost"));
        request.setTerminatorUrl(System.getProperty("terminatorHost"));
        request.setMonopolized(true);
        Assert.assertNotNull("this.getAppDomain().getRunEnvironment() can not be null", runtime);
        request.setRunEnv(runtime.getId());
        request.setConfigFiles(new String[] { ConfigConstant.FILE_SCHEMA, ConfigConstant.FILE_SOLOR, ConfigConstant.FILE_CORE_PROPERTIES });
        return request;
    }

    private static void publishNewService(CoreRequest request, short[] replication, int groupNum) throws IOException {
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

    private static final CoreManagerClient clientProtocol;

    static {
        // try {
        // 
        // // Class clazz = Class
        // // .forName("com.taobao.terminator.manage.client.CoreClient");
        // 
        // IOUtils
        // .copy(
        // Class.class
        // .getResourceAsStream("/com/taobao/terminator/manage/client/CoreClient.class"),
        // new FileOutputStream(new java.io.File(
        // "D:\\tmp\\CoreClient.class")));
        // } catch (Exception e1) {
        // throw new RuntimeException(e1);
        // }
        // final String centerNodeAddress = System.getProperty("centerNode");
        // try {
        // // RpcCoreManageImpl coreManage = new RpcCoreManageImpl();
        // 
        // System.setProperty("javax.xml.parsers.DocumentBuilderFactory",
        // "com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl");
        // 
        // Configuration config = new Configuration();
        // 
        // // 10.232.21.117
        // 
        // if (centerNodeAddress == null) {
        // throw new IllegalStateException(
        // "centerNode node address can not be null");
        // }
        // 
        // // config.set("ns.default.name", "terminator://10.235.160.75:9000");
        // config.set("ns.default.name", "terminator://" + centerNodeAddress);
        // 
        // clientProtocol = new CoreManagerClient(config);
        // 
        // } catch (Throwable e) {
        // throw new RuntimeException("centerNodeAddress:" + centerNodeAddress
        // + " can not be connect", e);
        // }
        clientProtocol = null;
    }
}
