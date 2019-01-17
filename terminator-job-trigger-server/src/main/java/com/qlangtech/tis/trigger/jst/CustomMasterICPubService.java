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

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.qlangtech.tis.FCoreRequest;
import com.qlangtech.tis.PubService;
import com.qlangtech.tis.cloud.ipc.parameter.CoreRequest;
import com.qlangtech.tis.pubhook.common.RunEnvironment;

/* *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class CustomMasterICPubService extends PubService {

    private final Map<Integer, Long> groupFileConsumeSizeMap;

    private final ImportDataProcessInfo state;

    public CustomMasterICPubService(String appname, int groupSize, int replicaCount, RunEnvironment runtime, List<String> servers, Map<Integer, Long> groupSizeMap, ImportDataProcessInfo state) throws Exception {
        super(appname, groupSize, replicaCount, runtime, servers);
        this.groupFileConsumeSizeMap = groupSizeMap;
        Long diskConsume = null;
        for (int i = 0; i < groupSize; i++) {
            diskConsume = this.groupFileConsumeSizeMap.get(i);
            if (diskConsume == null || diskConsume < 1) {
                throw new IllegalStateException("appname:" + appname + ",group:" + i + " consume disk size is not illeal,timepoint:" + state.getTimepoint());
            }
        }
        this.state = state;
    }

    // @Override
    protected void addIpsToGroup(Map<Integer, Set<String>> ips) {
    // 向服务器组中添加服务器
    }

    @Override
    public void publishNewCore() throws Exception {
        final int assignGroupCount = 0;
        FCoreRequest result = new FCoreRequest(createIps(this.getAppname(), this.getRuntime()), assignGroupCount + this.getGroupSize(), assignGroupCount);
        short[] replicCount = new short[this.getGroupSize()];
        for (int i = 0; i < this.getGroupSize(); i++) {
            replicCount[i] = (short) this.getReplicaCount();
        }
        if (state.getContainerSpecification() == null || state.getContainerSpecification().getMaxQps() == null) {
            throw new IllegalStateException("state.getContainerSpecification().getMaxQps() can not be null");
        }
    // public void publishNewService( CoreRequest request , short groupNum ,
    // int requiredQPS ,
    // Map<Integer, Long> requiredDiskSpace , int specificationId ) throws
    // IOException
    // this.getClientprotocol().publishNewService(result.getRequest(),
    // (short) this.getGroupSize(),
    // //state.getContainerSpecification().getMaxQps(),
    // 1,
    // groupFileConsumeSizeMap, -1);
    }

    @Override
    protected void publishNewService(CoreRequest request, short[] replication, int groupNum) throws IOException {
    // this.getClientprotocol().publishNewService(request, replication,
    // (short) groupNum, groupFileConsumeSizeMap);
    }
}
