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
package com.qlangtech.tis.coredefine.biz;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import org.apache.hadoop.io.MapWritable;

/* *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public interface RpcCoreManage {
    // public CoreManagerClient getClientProtocol();
    // public void changeBuidlerJob(String serviceName) throws IOException;
    // 
    // public void changeBuidlerJob(String serviceName, int group)
    // throws IOException;
    // 
    // public void changeBuidlerJob(String serviceName, int group, String ip)
    // throws IOException;
    // 
    // /**
    // * 触发从客户端导入的全量
    // *
    // * @param serviceName
    // * @param userPoint
    // * 一定为-1
    // * @throws IOException
    // */
    // public void triggerServiceFullDump(String serviceName);
    // 
    // /**
    // * 同步某个时间点的全量索引数据
    // *
    // * @param serviceName
    // * @param userPoint
    // * @throws IOException
    // */
    // public void triggerSynIndexFile(String serviceName, String userPoint);
    // 
    // /**
    // * 提交HDFS某个时间点的文件到Dump层进行DUMP
    // *
    // * @param serviceName
    // * @param userPoint
    // * @throws IOException
    // */
    // public void triggerFullDumpFile(String serviceName, String userPoint);
    // 
    // /**
    // * 减少副本数目需要校验一下
    // *
    // * @param serviceName
    // * @param ips
    // * @return
    // */
    // public boolean hasRealTimeLeaderServer(String serviceName, String[] ips);
    // 
    // public int getAllServersCount();
    // 
    // // 重新build
    // public void unprotectRepublishService(String serviceName, int group,
    // String ip);
    // 
    // public void unprotectRepublishService(String serviceName, int group)
    // throws IOException;
    // 
    // public void unprotectRepublishService(String serviceName)
    // throws IOException;
    // 
    // // 重新build
    // 
    // public CoreNodeInfo[] getCoreNodeNotSolrCoreList(String serviceName,
    // int group) throws IOException;
    // 
    // public void unprotectedAddCoreNums(CoreRequest request, int group)
    // throws IOException;
    // 
    // public void unprotectedAddReplication(CoreRequest request, int group)
    // throws IOException;
    // 
    // public void unprotectedRemoveStoredCore(String serviceName, String ip,
    // int group) throws IOException;
    // 
    // public void unprotectProcessExcessReplication(String serviceName,
    // int group, String ip) throws IOException;
    // 
    // // 是否成功创建了新的应用
    // public boolean isCreateNewServiceSuc(String servceName);
    // 
    // public void addCoreReplication(CoreRequest request, short[] replication)
    // throws IOException;
    // 
    // public void desCoreReplication(CoreRequest request, short[] replication)
    // throws IOException;
    // 
    // /**
    // * 添加组
    // *
    // * @param request
    // * @param group
    // * @throws IOException
    // */
    // public void addCoreNums(CoreRequest request, short group,
    // short[] replications) throws IOException;
    // 
    // // public void publishNewService(CoreRequest request, short replication,
    // // short groupNum) throws IOException;
    // 
    // /**
    // * @param request
    // * @param replication
    // * 标识 每组组内副本数目
    // * @param groupNum
    // * @throws IOException
    // */
    // public void publishNewService(CoreRequest request, short[] replication,
    // int groupNum) throws IOException;
    // 
    // public void publishNewServiceOnGallardo(CoreRequest request, short
    // replication,
    // int groupNum) throws IOException;
    // 
    // public ServiceStatus getServiceStatus( String serviceName) throws
    // IOException;
    // 
    // 
    // public boolean startOne(String serviceName, int groupNum , int replicaNum
    // ) throws Exception ;
    // 
    // public boolean stopOne( String serviceName , int groupNum , int
    // replicaNum ) throws Exception ;
    // 
    // public boolean setCoreReplication(CoreRequest request, short groupNum,
    // short numReplica) throws Exception;
    // 
    // public CheckingResponse checkingCoreCompletion( String serviceName )
    // throws IOException;
    // 
    // public ReplicaNodeCrood getCoreReplicaNodeCrood( String serviceName )
    // throws Exception;
    // 
    // public boolean isCreateByGallardo(String serviceName) throws IOException;
    // 
    // public void coreConfigChange(String serviceName, int group, String host)
    // throws IOException;
    // 
    // public void coreConfigChange(String serviceName, int group)
    // throws IOException;
    // 
    // public void coreConfigChange(String serviceName) throws IOException;
    // 
    // public void decrCoreNums(CoreRequest request, short group)
    // throws IOException;
    // 
    // public Core[] getAllCores() throws IOException;
    // 
    // public SolrCoreInfo[] getClusterCoreInfos(int start, int rows)
    // throws IOException;
    // 
    // public CoreNodeInfo[] getClusterCoreNodeInfo(int start, int row)
    // throws IOException;
    // 
    // public CoreNodeInfo[] getClusterCoreNodeInfoByFreeDesc() throws
    // IOException;
    // 
    // public CoreNodeInfo[] getFreeServersDesc(String appName) throws
    // IOException;
    // 
    // public long getClusterIndexSize() throws IOException;
    // 
    // public long getCoreAllIndexSize(String serviceName) throws IOException;
    // 
    // public long getCoreIndexSizeByHost(String serviceName, int groupNum,
    // String host) throws IOException;
    // 
    // public LocatedCores getCoreLocations(String serviceName) throws
    // IOException;
    // 
    // public CoreNodeInfo[] getCoreNodeInfos(int start, int rows)
    // throws IOException;
    // 
    // public long getCoreOneColumnIndexSize(String serviceName, int groupNum)
    // throws IOException;
    // 
    // public long getCoreOneRowIndexSize(String serviceName) throws
    // IOException;
    // 
    // public Core[] getCoresByCoreNode(String hostname) throws IOException;
    // 
    // public Set<Core> getCorruptCores() throws IOException;
    // 
    // public DiskInfo getDiskInfo(String hotname) throws IOException;
    // 
    // public JvmInfo getJVMInfo(String hostname) throws IOException;
    // 
    // public LuceneInfo getLuceneInfo(String hostname) throws IOException;
    // 
    // public SolrCoreInfo getOneSolrCoreInfoByService(String serviceName,
    // int groupNum, String host) throws IOException;
    // 
    // public long getProtocolVersion(String arg0, long arg1) throws
    // IOException;
    // 
    // // baisui 重要
    // public DumpJobStatus getServiceCoreDumpStatus(String serviceName, int
    // group)
    // throws IOException;
    // 
    // // public List<CoreNode> getServiceCoreNodeInfo(String serviceName, int
    // // group)
    // // throws IOException;
    // 
    // // baisui end
    // public List<CoreNode> getServiceCoreNodeInfo(String serviceName);
    // 
    // public int getServiceGroup(String serviceName) throws IOException;
    // 
    // public MapWritable getSolrCoreInfosByHost(String hostname)
    // throws IOException;
    // 
    // public SolrCoreInfo[] getSolrCoresInfoByService(String serviceName,
    // int groupNum) throws IOException;
    // 
    // public SystemInfo getSystemInfo(String hostname) throws IOException;
    // 
    // // public void moveCore(String serviceName, int groupNum, String srcHost,
    // // String[] includeHostList, String[] excludeHostList)
    // // throws IOException;
    // //
    // // public void moveCore(String serviceName, int groupNum, String host)
    // // throws IOException;
    // //
    // // public void moveCore(String serviceName, int groupNum,
    // // String[] includeHostList, String[] excludeHostList)
    // // throws IOException;
    // //
    // // public void moveCore(String serviceName, int groupNum) throws
    // // IOException;
    // 
    // public void releaseCoreCommandExecute(String serviceName, String
    // hostName,
    // int group) throws IOException;
    // 
    // // public void removeOneCore(String serviceName, int groupNum, String
    // host)
    // // throws IOException;
    // 
    // public void removeService(String serviceName) throws IOException;
    // 
    // public void republishHsf(String serviceName, int group, String host)
    // throws IOException;
    // 
    // public void rePublishHsf(String serviceName, int group) throws
    // IOException;
    // 
    // public void rePublishHsf(String serviceName) throws IOException;
    // 
    // public void schemaChange(String serviceName, int group) throws
    // IOException;
    // 
    // public void schemaChange(String serviceName) throws IOException;
    // 
    // // public void setCoreReplication(String serviceName, int replication)
    // // throws IOException;
    // 
    // // public void addServiceCore(CoreRequest request) throws IOException;
    // 
    // // public void createNewService(String coreName, short replication,
    // // short groupNum) throws IOException;
    // 
    // public long[] getClusterInfo() throws IOException;
    // 
    // public void repairGroupIndex(String serviceName, MachinePair[]
    // machinePair)
    // throws IOException;
}
