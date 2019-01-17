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

import java.io.IOException;
import java.util.List;
import com.qlangtech.tis.cloud.ipc.parameter.CoreRequest;
import com.qlangtech.tis.pubhook.common.RunEnvironment;

/* *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class AddReplic extends PubService {

    /**
     * @param appname
     * @param groupSize
     * @param replicaCount
     * @param runtime
     * @param servers
     */
    public AddReplic(String appname, int groupSize, int replicaCount, RunEnvironment runtime, List<String> servers) {
        super(appname, groupSize, replicaCount, runtime, servers);
    }

    @Override
    protected void publishNewService(CoreRequest request, short[] replication, int groupNum) throws IOException {
        getClientprotocol().addReplication(request, convert2intAry(replication));
    }

    private int[] convert2intAry(short[] replication) {
        int[] addreplic = new int[replication.length];
        for (int i = 0; i < replication.length; i++) {
            addreplic[i] = replication[i];
        }
        return addreplic;
    }

    @SuppressWarnings("all")
    public static void main(String[] args) throws Exception {
        String serviceName = System.getProperty("name");
    // RunEnvironment runtime = RunEnvironment.getEnum(System
    // .getProperty("runtime"));
    // File replicaFile = new File(System.getProperty("replicas"));
    // if (!replicaFile.exists()) {
    // throw new IllegalArgumentException("file:"
    // + replicaFile.getAbsolutePath() + " is not exist");
    // }
    // List<String> replicIps = FileUtils.readLines(replicaFile);
    // 
    // FCoreRequest request = PubService.createCoreRequest(serviceName,
    // group,
    // runtime, ips);
    // 
    // PubService.getClientprotocol().addCoreReplication(request,
    // replication);
    }
}
