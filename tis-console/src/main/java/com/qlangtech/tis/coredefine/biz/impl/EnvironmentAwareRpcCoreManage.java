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

import com.qlangtech.tis.coredefine.biz.RpcCoreManage;
import com.qlangtech.tis.manage.common.OperationLogger;
import com.qlangtech.tis.manage.spring.EnvironmentBindService;
import com.qlangtech.tis.pubhook.common.RunEnvironment;

/* *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class EnvironmentAwareRpcCoreManage extends EnvironmentBindService<RpcCoreManage> implements RpcCoreManage, OperationLogger {

    @Override
    public String getEntityName() {
        return null;
    }

    @Override
    protected RpcCoreManage createSerivce(RunEnvironment runtime) {
        return null;
    }
    // 
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
    // public boolean hasRealTimeLeaderServer(String serviceName, String[] ips)
    // {
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
    // public boolean isCreateByGallardo(String serviceName) throws IOException
    // {
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
    // public CoreNodeInfo[] getClusterCoreNodeInfoByFreeDesc() throws
    // IOException {
    // 
    // return null;
    // }
    // 
    // @Override
    // public CoreNodeInfo[] getFreeServersDesc(String appName) throws
    // IOException {
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
    // public LocatedCores getCoreLocations(String serviceName) throws
    // IOException {
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
    // public long getCoreOneRowIndexSize(String serviceName) throws IOException
    // {
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
    // public long getProtocolVersion(String arg0, long arg1) throws IOException
    // {
    // 
    // return 0;
    // }
    // 
    // @Override
    // public DumpJobStatus getServiceCoreDumpStatus(String serviceName, int
    // group)
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
    // public void releaseCoreCommandExecute(String serviceName, String
    // hostName,
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
    // public void rePublishHsf(String serviceName, int group) throws
    // IOException {
    // }
    // 
    // @Override
    // public void rePublishHsf(String serviceName) throws IOException {
    // }
    // 
    // @Override
    // public void schemaChange(String serviceName, int group) throws
    // IOException {
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
    // public void repairGroupIndex(String serviceName, MachinePair[]
    // machinePair)
    // throws IOException {
    // }
    // @Override
    // public CoreManagerClient getClientProtocol() {
    // return this.getInstance().getClientProtocol();
    // }
    // @Override
    // public void repairGroupIndex(String serviceName, MachinePair[]
    // machinePair)
    // throws IOException {
    // 
    // this.getInstance().repairGroupIndex(serviceName, machinePair);
    // }
    // 
    // @Override
    // public void addCoreNums(CoreRequest request, short group,
    // short[] replications) throws IOException {
    // 
    // this.getInstance().addCoreNums(request, group, replications);
    // }
    // 
    // @Override
    // public CoreNodeInfo[] getFreeServersDesc(String appName) throws
    // IOException {
    // 
    // return this.getInstance().getFreeServersDesc(appName);// (request,
    // // replication);
    // }
    // 
    // @Override
    // public void addCoreReplication(CoreRequest request, short[] replication)
    // throws IOException {
    // this.getInstance().addCoreReplication(request, replication);
    // }
    // 
    // @Override
    // public void desCoreReplication(CoreRequest request, short[] replication)
    // throws IOException {
    // this.getInstance().desCoreReplication(request, replication);
    // }
    // 
    // private static final Log log = LogFactory
    // .getLog(EnvironmentAwareRpcCoreManage.class);
    // 
    // @Override
    // public void changeBuidlerJob(String serviceName, int group, String ip)
    // throws IOException {
    // getInstance().changeBuidlerJob(serviceName, group, ip);
    // 
    // }
    // 
    // @Override
    // public void publishNewService(CoreRequest request, short[] replication,
    // int groupNum) throws IOException {
    // getInstance().publishNewService(request, replication, groupNum);
    // }
    // 
    // @Override
    // public void changeBuidlerJob(String serviceName, int group)
    // throws IOException {
    // getInstance().changeBuidlerJob(serviceName, group);
    // 
    // }
    // 
    // @Override
    // public void changeBuidlerJob(String serviceName) throws IOException {
    // getInstance().changeBuidlerJob(serviceName);
    // }
    // 
    // @Override
    // public String getEntityName() {
    // return "rpcCoreManage";
    // }
    // 
    // @Override
    // public void triggerFullDumpFile(String serviceName, String userPoint) {
    // getInstance().triggerFullDumpFile(serviceName, userPoint);
    // }
    // 
    // @Override
    // public void triggerSynIndexFile(String serviceName, String userPoint) {
    // getInstance().triggerSynIndexFile(serviceName, userPoint);
    // 
    // }
    // 
    // @Override
    // public boolean hasRealTimeLeaderServer(String serviceName, String[] ips)
    // {
    // return getInstance().hasRealTimeLeaderServer(serviceName, ips);
    // }
    // 
    // @Override
    // public int getAllServersCount() {
    // return getInstance().getAllServersCount();
    // }
    // 
    // @Override
    // public void triggerServiceFullDump(String serviceName) {
    // getInstance().triggerServiceFullDump(serviceName);
    // }
    // 
    // //
    // cn1.terminator.taobao.org:9010,cn2.terminator.taobao.org:9010,cn3.terminator.taobao.org:9010,cn4.terminator.taobao.org:9010,cn5.terminator.taobao.org:9010,cn6.terminator.taobao.org:9010
    // @Override
    // protected RpcCoreManage createSerivce(final RunEnvironment runtime) {
    // 
    // final String zkConnectStr = Config.getZkAddress(runtime);
    // 
    // log.debug("runtime:" + runtime + ", zkConnectString:"
    // + zkConnectStr + " start connect");
    // final Object target = RpcCoreManageImpl.create(zkConnectStr);
    // 
    // return (RpcCoreManage) Proxy.newProxyInstance(this.getClass()
    // .getClassLoader(), new Class<?>[] { RpcCoreManage.class },
    // new InvocationHandler() {
    // @Override
    // public Object invoke(Object proxy, Method method,
    // Object[] args) throws Throwable {
    // try {
    // return method.invoke(target, args);
    // } catch (InvocationTargetException e) {
    // if (e.getTargetException() instanceof SocketException
    // || e.getTargetException() instanceof IOException) {
    // cleanInstance(runtime);
    // }
    // Thread.sleep(3000);
    // throw new IllegalStateException(
    // "has not invoke success", e);
    // } catch (Exception e) {
    // throw new RuntimeException(e);
    // }
    // 
    // }
    // 
    // }); // 要绑定接口
    // 
    // }
    // 
    // @Override
    // public void unprotectedAddCoreNums(CoreRequest request, int group)
    // throws IOException {
    // getInstance().unprotectedAddCoreNums(request, group);
    // }
    // 
    // @Override
    // public void unprotectRepublishService(String serviceName, int group,
    // String ip) {
    // getInstance().unprotectRepublishService(serviceName, group, ip);
    // }
    // 
    // @Override
    // public void unprotectRepublishService(String serviceName, int group)
    // throws IOException {
    // getInstance().unprotectRepublishService(serviceName, group);
    // }
    // 
    // @Override
    // public void unprotectRepublishService(String serviceName)
    // throws IOException {
    // getInstance().unprotectRepublishService(serviceName);
    // }
    // 
    // @Override
    // public void unprotectedAddReplication(CoreRequest request, int group)
    // throws IOException {
    // getInstance().unprotectedAddReplication(request, group);
    // 
    // }
    // 
    // @Override
    // public void unprotectedRemoveStoredCore(String serviceName, String ip,
    // int group) throws IOException {
    // getInstance().unprotectedRemoveStoredCore(serviceName, ip, group);
    // }
    // 
    // @Override
    // public void unprotectProcessExcessReplication(String serviceName,
    // int group, String ip) throws IOException {
    // getInstance().unprotectProcessExcessReplication(serviceName, group, ip);
    // }
    // 
    // // 取得某个应用中可以被选的服务列表，已经对lucene版本过滤过了
    // @Override
    // public CoreNodeInfo[] getCoreNodeNotSolrCoreList(String serviceName,
    // int group) throws IOException {
    // return getInstance().getCoreNodeNotSolrCoreList(serviceName, group);
    // }
    // 
    // // public void addCoreNums(CoreRequest request, short group)
    // // throws IOException {
    // // getInstance().addCoreNums(request, group);
    // // }
    // 
    // // public void addCoreReplication(CoreRequest request, int replication)
    // // throws IOException {
    // // getInstance().addCoreReplication(request, replication);
    // // }
    // 
    // public void coreConfigChange(String serviceName, int group, String host)
    // throws IOException {
    // getInstance().coreConfigChange(serviceName, group, host);
    // }
    // 
    // public void coreConfigChange(String serviceName, int group)
    // throws IOException {
    // getInstance().coreConfigChange(serviceName, group);
    // }
    // 
    // public void coreConfigChange(String serviceName) throws IOException {
    // getInstance().coreConfigChange(serviceName);
    // }
    // 
    // public void decrCoreNums(CoreRequest request, short group)
    // throws IOException {
    // getInstance().decrCoreNums(request, group);
    // }
    // 
    // // public void desCoreReplication(CoreRequest request, int replication)
    // // throws IOException {
    // // getInstance().desCoreReplication(request, replication);
    // // }
    // 
    // public Core[] getAllCores() throws IOException {
    // return getInstance().getAllCores();
    // }
    // 
    // public SolrCoreInfo[] getClusterCoreInfos(int start, int rows)
    // throws IOException {
    // return getInstance().getClusterCoreInfos(start, rows);
    // }
    // 
    // public CoreNodeInfo[] getClusterCoreNodeInfo(int start, int row)
    // throws IOException {
    // return getInstance().getClusterCoreNodeInfo(start, row);
    // }
    // 
    // public CoreNodeInfo[] getClusterCoreNodeInfoByFreeDesc() throws
    // IOException {
    // return getInstance().getClusterCoreNodeInfoByFreeDesc();
    // }
    // 
    // public long getClusterIndexSize() throws IOException {
    // return getInstance().getClusterIndexSize();
    // }
    // 
    // public long[] getClusterInfo() throws IOException {
    // return getInstance().getClusterInfo();
    // }
    // 
    // public long getCoreAllIndexSize(String serviceName) throws IOException {
    // return getInstance().getCoreAllIndexSize(serviceName);
    // }
    // 
    // public long getCoreIndexSizeByHost(String serviceName, int groupNum,
    // String host) throws IOException {
    // return getInstance()
    // .getCoreIndexSizeByHost(serviceName, groupNum, host);
    // }
    // 
    // public LocatedCores getCoreLocations(String serviceName) throws
    // IOException {
    // return getInstance().getCoreLocations(serviceName);
    // }
    // 
    // public CoreNodeInfo[] getCoreNodeInfos(int start, int rows)
    // throws IOException {
    // return getInstance().getCoreNodeInfos(start, rows);
    // }
    // 
    // public long getCoreOneColumnIndexSize(String serviceName, int groupNum)
    // throws IOException {
    // return getInstance().getCoreOneColumnIndexSize(serviceName, groupNum);
    // }
    // 
    // public long getCoreOneRowIndexSize(String serviceName) throws IOException
    // {
    // return getInstance().getCoreOneRowIndexSize(serviceName);
    // }
    // 
    // public Core[] getCoresByCoreNode(String hostname) throws IOException {
    // return getInstance().getCoresByCoreNode(hostname);
    // }
    // 
    // public Set<Core> getCorruptCores() throws IOException {
    // return getInstance().getCorruptCores();
    // }
    // 
    // public DiskInfo getDiskInfo(String hotname) throws IOException {
    // return getInstance().getDiskInfo(hotname);
    // }
    // 
    // public JvmInfo getJVMInfo(String hostname) throws IOException {
    // return getInstance().getJVMInfo(hostname);
    // }
    // 
    // public LuceneInfo getLuceneInfo(String hostname) throws IOException {
    // return getInstance().getLuceneInfo(hostname);
    // }
    // 
    // public SolrCoreInfo getOneSolrCoreInfoByService(String serviceName,
    // int groupNum, String host) throws IOException {
    // return getInstance().getOneSolrCoreInfoByService(serviceName, groupNum,
    // host);
    // }
    // 
    // public long getProtocolVersion(String arg0, long arg1) throws IOException
    // {
    // return getInstance().getProtocolVersion(arg0, arg1);
    // }
    // 
    // public DumpJobStatus getServiceCoreDumpStatus(String serviceName, int
    // group)
    // throws IOException {
    // return getInstance().getServiceCoreDumpStatus(serviceName, group);
    // }
    // 
    // public List<CoreNode> getServiceCoreNodeInfo(String serviceName) {
    // return getInstance().getServiceCoreNodeInfo(serviceName);
    // }
    // 
    // public int getServiceGroup(String serviceName) throws IOException {
    // return getInstance().getServiceGroup(serviceName);
    // }
    // 
    // public MapWritable getSolrCoreInfosByHost(String hostname)
    // throws IOException {
    // return getInstance().getSolrCoreInfosByHost(hostname);
    // }
    // 
    // public SolrCoreInfo[] getSolrCoresInfoByService(String serviceName,
    // int groupNum) throws IOException {
    // return getInstance().getSolrCoresInfoByService(serviceName, groupNum);
    // }
    // 
    // public SystemInfo getSystemInfo(String hostname) throws IOException {
    // return getInstance().getSystemInfo(hostname);
    // }
    // 
    // public boolean isCreateNewServiceSuc(String servceName) {
    // return getInstance().isCreateNewServiceSuc(servceName);
    // }
    // 
    // // public void moveCore(String serviceName, int groupNum, String srcHost,
    // // String[] includeHostList, String[] excludeHostList)
    // // throws IOException {
    // // getInstance().moveCore(serviceName, groupNum, srcHost,
    // includeHostList,
    // // excludeHostList);
    // // }
    // //
    // // public void moveCore(String serviceName, int groupNum, String host)
    // // throws IOException {
    // // getInstance().moveCore(serviceName, groupNum, host);
    // // }
    // //
    // // public void moveCore(String serviceName, int groupNum,
    // // String[] includeHostList, String[] excludeHostList)
    // // throws IOException {
    // // getInstance().moveCore(serviceName, groupNum, includeHostList,
    // // excludeHostList);
    // // }
    // //
    // // public void moveCore(String serviceName, int groupNum) throws
    // IOException
    // // {
    // // getInstance().moveCore(serviceName, groupNum);
    // // }
    // 
    // // public void publishNewService(CoreRequest request, short replication,
    // // short groupNum) throws IOException {
    // // getInstance().publishNewService(request, replication, groupNum);
    // // }
    // 
    // public void releaseCoreCommandExecute(String serviceName, String
    // hostName,
    // int group) throws IOException {
    // getInstance().releaseCoreCommandExecute(serviceName, hostName, group);
    // }
    // 
    // public void removeService(String serviceName) throws IOException {
    // getInstance().removeService(serviceName);
    // }
    // 
    // public void republishHsf(String serviceName, int group, String host)
    // throws IOException {
    // getInstance().republishHsf(serviceName, group, host);
    // }
    // 
    // public void rePublishHsf(String serviceName, int group) throws
    // IOException {
    // getInstance().rePublishHsf(serviceName, group);
    // }
    // 
    // public void rePublishHsf(String serviceName) throws IOException {
    // getInstance().rePublishHsf(serviceName);
    // }
    // 
    // public void schemaChange(String serviceName, int group) throws
    // IOException {
    // getInstance().schemaChange(serviceName, group);
    // }
    // 
    // public void schemaChange(String serviceName) throws IOException {
    // getInstance().schemaChange(serviceName);
    // }
    // 
    // @Override
    // public void publishNewServiceOnGallardo(CoreRequest request,
    // short replication, int groupNum) throws IOException {
    // getInstance().publishNewServiceOnGallardo(request, replication,
    // groupNum);
    // }
    // 
    // @Override
    // public ServiceStatus getServiceStatus(String serviceName)
    // throws IOException {
    // return getInstance().getServiceStatus(serviceName);
    // }
    // 
    // @Override
    // public boolean startOne(String serviceName, int groupNum, int replicaNum)
    // throws Exception {
    // return getInstance().startOne(serviceName, groupNum, replicaNum);
    // }
    // 
    // @Override
    // public boolean stopOne(String serviceName, int groupNum, int replicaNum)
    // throws Exception {
    // return getInstance().stopOne(serviceName, groupNum, replicaNum);
    // }
    // 
    // @Override
    // public boolean setCoreReplication(CoreRequest request, short groupNum,
    // short numReplica) throws Exception {
    // return getInstance().setCoreReplication(request, groupNum, numReplica);
    // }
    // 
    // @Override
    // public boolean isCreateByGallardo(String serviceName) throws IOException
    // {
    // return getInstance().isCreateByGallardo(serviceName);
    // }
    // 
    // @Override
    // public CheckingResponse checkingCoreCompletion(String serviceName)
    // throws IOException {
    // return getInstance().checkingCoreCompletion(serviceName);
    // }
    // 
    // @Override
    // public ReplicaNodeCrood getCoreReplicaNodeCrood(String serviceName)
    // throws Exception {
    // return getInstance().getCoreReplicaNodeCrood(serviceName);
    // }
}
