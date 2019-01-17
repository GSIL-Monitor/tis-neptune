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
package com.qlangtech.tis.coredefine.biz.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.io.MapWritable;
import com.qlangtech.tis.coredefine.biz.CoreNode;
import com.qlangtech.tis.coredefine.biz.RpcCoreManage;
import com.qlangtech.tis.manage.common.Config;
import com.qlangtech.tis.pubhook.common.RunEnvironment;

/* *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class RpcCoreManageImpl implements RpcCoreManage {
    // @Override
    // public void changeBuidlerJob(String serviceName) throws IOException {
    // }
    // 
    // @Override
    // public void changeBuidlerJob(String serviceName, int group)
    // throws IOException {
    // }
    // 
    // @Override
    // public void changeBuidlerJob(String serviceName, int group, String ip)
    // throws IOException {
    // }
    // 
    // @Override
    // public void triggerServiceFullDump(String serviceName) {
    // }
    // 
    // @Override
    // public void triggerSynIndexFile(String serviceName, String userPoint) {
    // }
    // 
    // @Override
    // public void triggerFullDumpFile(String serviceName, String userPoint) {
    // }
    // 
    // @Override
    // public boolean hasRealTimeLeaderServer(String serviceName, String[] ips) {
    // 
    // return false;
    // }
    // 
    // @Override
    // public int getAllServersCount() {
    // 
    // return 0;
    // }
    // 
    // @Override
    // public void unprotectRepublishService(String serviceName, int group,
    // String ip) {
    // }
    // 
    // @Override
    // public void unprotectRepublishService(String serviceName, int group)
    // throws IOException {
    // }
    // 
    // @Override
    // public void unprotectRepublishService(String serviceName)
    // throws IOException {
    // }
    // 
    // @Override
    // public CoreNodeInfo[] getCoreNodeNotSolrCoreList(String serviceName,
    // int group) throws IOException {
    // 
    // return null;
    // }
    // 
    // @Override
    // public void unprotectedAddCoreNums(CoreRequest request, int group)
    // throws IOException {
    // }
    // 
    // @Override
    // public void unprotectedAddReplication(CoreRequest request, int group)
    // throws IOException {
    // }
    // 
    // @Override
    // public void unprotectedRemoveStoredCore(String serviceName, String ip,
    // int group) throws IOException {
    // }
    // 
    // @Override
    // public void unprotectProcessExcessReplication(String serviceName,
    // int group, String ip) throws IOException {
    // }
    // 
    // @Override
    // public boolean isCreateNewServiceSuc(String servceName) {
    // 
    // return false;
    // }
    // 
    // @Override
    // public void addCoreReplication(CoreRequest request, short[] replication)
    // throws IOException {
    // }
    // 
    // @Override
    // public void desCoreReplication(CoreRequest request, short[] replication)
    // throws IOException {
    // }
    // 
    // @Override
    // public void addCoreNums(CoreRequest request, short group,
    // short[] replications) throws IOException {
    // }
    // 
    // @Override
    // public void publishNewService(CoreRequest request, short[] replication,
    // int groupNum) throws IOException {
    // }
    // 
    // @Override
    // public void publishNewServiceOnGallardo(CoreRequest request,
    // short replication, int groupNum) throws IOException {
    // }
    // 
    // @Override
    // public ServiceStatus getServiceStatus(String serviceName)
    // throws IOException {
    // 
    // return null;
    // }
    // 
    // @Override
    // public boolean startOne(String serviceName, int groupNum, int replicaNum)
    // throws Exception {
    // 
    // return false;
    // }
    // 
    // @Override
    // public boolean stopOne(String serviceName, int groupNum, int replicaNum)
    // throws Exception {
    // 
    // return false;
    // }
    // 
    // @Override
    // public boolean setCoreReplication(CoreRequest request, short groupNum,
    // short numReplica) throws Exception {
    // 
    // return false;
    // }
    // 
    // @Override
    // public CheckingResponse checkingCoreCompletion(String serviceName)
    // throws IOException {
    // 
    // return null;
    // }
    // 
    // @Override
    // public ReplicaNodeCrood getCoreReplicaNodeCrood(String serviceName)
    // throws Exception {
    // 
    // return null;
    // }
    // 
    // @Override
    // public boolean isCreateByGallardo(String serviceName) throws IOException {
    // 
    // return false;
    // }
    // 
    // @Override
    // public void coreConfigChange(String serviceName, int group, String host)
    // throws IOException {
    // }
    // 
    // @Override
    // public void coreConfigChange(String serviceName, int group)
    // throws IOException {
    // }
    // 
    // @Override
    // public void coreConfigChange(String serviceName) throws IOException {
    // }
    // 
    // @Override
    // public void decrCoreNums(CoreRequest request, short group)
    // throws IOException {
    // }
    // 
    // @Override
    // public Core[] getAllCores() throws IOException {
    // 
    // return null;
    // }
    // 
    // @Override
    // public SolrCoreInfo[] getClusterCoreInfos(int start, int rows)
    // throws IOException {
    // 
    // return null;
    // }
    // 
    // @Override
    // public CoreNodeInfo[] getClusterCoreNodeInfo(int start, int row)
    // throws IOException {
    // 
    // return null;
    // }
    // 
    // @Override
    // public CoreNodeInfo[] getClusterCoreNodeInfoByFreeDesc() throws IOException {
    // 
    // return null;
    // }
    // 
    // @Override
    // public CoreNodeInfo[] getFreeServersDesc(String appName) throws IOException {
    // 
    // return null;
    // }
    // 
    // @Override
    // public long getClusterIndexSize() throws IOException {
    // 
    // return 0;
    // }
    // 
    // @Override
    // public long getCoreAllIndexSize(String serviceName) throws IOException {
    // 
    // return 0;
    // }
    // 
    // @Override
    // public long getCoreIndexSizeByHost(String serviceName, int groupNum,
    // String host) throws IOException {
    // 
    // return 0;
    // }
    // 
    // @Override
    // public LocatedCores getCoreLocations(String serviceName) throws IOException {
    // 
    // return null;
    // }
    // 
    // @Override
    // public CoreNodeInfo[] getCoreNodeInfos(int start, int rows)
    // throws IOException {
    // 
    // return null;
    // }
    // 
    // @Override
    // public long getCoreOneColumnIndexSize(String serviceName, int groupNum)
    // throws IOException {
    // 
    // return 0;
    // }
    // 
    // @Override
    // public long getCoreOneRowIndexSize(String serviceName) throws IOException {
    // 
    // return 0;
    // }
    // 
    // @Override
    // public Core[] getCoresByCoreNode(String hostname) throws IOException {
    // 
    // return null;
    // }
    // 
    // @Override
    // public Set<Core> getCorruptCores() throws IOException {
    // 
    // return null;
    // }
    // 
    // @Override
    // public DiskInfo getDiskInfo(String hotname) throws IOException {
    // 
    // return null;
    // }
    // 
    // @Override
    // public JvmInfo getJVMInfo(String hostname) throws IOException {
    // 
    // return null;
    // }
    // 
    // @Override
    // public LuceneInfo getLuceneInfo(String hostname) throws IOException {
    // 
    // return null;
    // }
    // 
    // @Override
    // public SolrCoreInfo getOneSolrCoreInfoByService(String serviceName,
    // int groupNum, String host) throws IOException {
    // 
    // return null;
    // }
    // 
    // @Override
    // public long getProtocolVersion(String arg0, long arg1) throws IOException {
    // 
    // return 0;
    // }
    // 
    // @Override
    // public DumpJobStatus getServiceCoreDumpStatus(String serviceName, int group)
    // throws IOException {
    // 
    // return null;
    // }
    // 
    // @Override
    // public List<CoreNode> getServiceCoreNodeInfo(String serviceName) {
    // 
    // return null;
    // }
    // 
    // @Override
    // public int getServiceGroup(String serviceName) throws IOException {
    // 
    // return 0;
    // }
    // 
    // @Override
    // public MapWritable getSolrCoreInfosByHost(String hostname)
    // throws IOException {
    // 
    // return null;
    // }
    // 
    // @Override
    // public SolrCoreInfo[] getSolrCoresInfoByService(String serviceName,
    // int groupNum) throws IOException {
    // 
    // return null;
    // }
    // 
    // @Override
    // public SystemInfo getSystemInfo(String hostname) throws IOException {
    // 
    // return null;
    // }
    // 
    // @Override
    // public void releaseCoreCommandExecute(String serviceName, String hostName,
    // int group) throws IOException {
    // }
    // 
    // @Override
    // public void removeService(String serviceName) throws IOException {
    // }
    // 
    // @Override
    // public void republishHsf(String serviceName, int group, String host)
    // throws IOException {
    // }
    // 
    // @Override
    // public void rePublishHsf(String serviceName, int group) throws IOException {
    // }
    // 
    // @Override
    // public void rePublishHsf(String serviceName) throws IOException {
    // }
    // 
    // @Override
    // public void schemaChange(String serviceName, int group) throws IOException {
    // }
    // 
    // @Override
    // public void schemaChange(String serviceName) throws IOException {
    // }
    // 
    // @Override
    // public long[] getClusterInfo() throws IOException {
    // 
    // return null;
    // }
    // 
    // @Override
    // public void repairGroupIndex(String serviceName, MachinePair[] machinePair)
    // throws IOException {
    // }
}
