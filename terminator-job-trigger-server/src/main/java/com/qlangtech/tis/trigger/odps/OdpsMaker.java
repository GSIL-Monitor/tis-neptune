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
package com.qlangtech.tis.trigger.odps;

/* *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class OdpsMaker {
    // private final static Logger logger = Logger.getLogger(OdpsMaker.class);
    // private static IFeedbackService feedbackService;
    // 
    // public IFeedbackService getFeedbackService() {
    // return feedbackService;
    // }
    // 
    // public void setFeedbackService(IFeedbackService feedbackService) {
    // this.feedbackService = feedbackService;
    // }
    // 
    // private TddlTaskConfig taskConfig;
    // private DailyPartition dailyPartition;
    // 
    // private String accessId;
    // private String accessKey;
    // private String partition;
    // private final String project;
    // private final String odps_endpoint;
    // private final String tunnel_endpoint;
    // 
    // private Account account;
    // private Odps odps;
    // 
    // public OdpsMaker(TddlTaskConfig taskConfig) {
    // super();
    // try {
    // ODPSConfig odpsConfig = taskConfig.getOdpsConfig();
    // 
    // this.accessId = Secret.decrypt(odpsConfig.getAccessId(),
    // "TIS_Terminator_Key");
    // this.accessKey = Secret.decrypt(odpsConfig.getAccessKey(),
    // "TIS_Terminator_Key");
    // this.odps_endpoint = odpsConfig.getServiceEndPoint();
    // this.tunnel_endpoint = odpsConfig.getDatatunelEndPoint();
    // this.project = odpsConfig.getProject();
    // account = new AliyunAccount(this.accessId, this.accessKey);
    // odps = new Odps(account);
    // odps.setDefaultProject(project);
    // odps.setEndpoint(odps_endpoint);
    // 
    // this.dailyPartition = odpsConfig.getDailyPartition();
    // this.taskConfig = taskConfig;
    // } catch (Exception e) {
    // throw new RuntimeException(e);
    // }
    // }
    // 
    // public boolean existTable(String tableName) {
    // try {
    // return odps.tables().exists(tableName);
    // } catch (OdpsException e) {
    // // TODO Auto-generated catch block
    // e.printStackTrace();
    // }
    // return false;
    // 
    // }
    // 
    // public OdpsTable getTableInfo(String tableName) {
    // OdpsTable odpsTable = new OdpsTable();
    // 
    // Table t = odps.tables().get(tableName);
    // 
    // TableSchema schema = t.getSchema();
    // List<Column> columns = schema.getColumns();
    // 
    // List<Column> partitionColumns = schema.getPartitionColumns();
    // odpsTable.setColumns(columns);
    // odpsTable.setPartitionColumns(partitionColumns);
    // 
    // return odpsTable;
    // }
    // 
    // public void createTable(String cols) {
    // try {
    // String sql = "CREATE TABLE if not exists "
    // + (taskConfig.getAppName() + "_" + taskConfig
    // .getLogicTableName()).toLowerCase() + " (" + cols
    // + " )" + " partitioned by (" + dailyPartition.getKey()
    // + " String,"
    // + taskConfig.getOdpsConfig().getGroupPartition()
    // + " String,taskId String);";
    // 
    // Instance instance = SQLTask.run(odps, sql);
    // 
    // addFeedbackInfo(taskConfig, InfoType.WARN,
    // "createTable () ODPS sql is " + sql);
    // 
    // sumit(instance);
    // 
    // } catch (Exception e) {
    // addFeedbackInfo(
    // taskConfig,
    // InfoType.ERROR,
    // "createTable() Exception "
    // + ExceptionUtils.getFullStackTrace(e));
    // throw new RuntimeException("createTable() Exception "
    // + ExceptionUtils.getFullStackTrace(e));
    // }
    // }
    // 
    // public void dropTable(String tableName) {
    // try {
    // String sql = "DROP TABLE IF EXISTS " + tableName + ";";
    // Instance instance = SQLTask.run(odps, sql);
    // // logger.warn("ODPS sql is " + sql);
    // 
    // sumit(instance);
    // 
    // } catch (Exception e) {
    // logger.error("createTable() Exception " + e);
    // }
    // }
    // 
    // public boolean addPartition(String partition) {
    // try {
    // // sale_date='201310', region='hangzhou'
    // String sql = "ALTER TABLE "
    // + (taskConfig.getAppName() + "_" + taskConfig
    // .getLogicTableName()).toLowerCase()
    // + " ADD if not exists PARTITION (" + partition + ");";
    // Instance instance = SQLTask.run(odps, sql);
    // // logger.warn("addPartition() ODPS sql is " + sql);
    // 
    // boolean isSuccess = sumit(instance);
    // return isSuccess;
    // 
    // } catch (Exception e) {
    // logger.error("createTable() Exception " + e);
    // }
    // return false;
    // }
    // 
    // public void showPartition(String logicName) {
    // try {
    // // sale_date='201310', region='hangzhou'
    // String sql = "SHOW PARTITIONS " + logicName + ";";
    // Instance instance = SQLTask.run(odps, sql);
    // logger.warn("ODPS sql is " + sql);
    // 
    // sumit(instance);
    // 
    // } catch (Exception e) {
    // logger.error("createTable() Exception " + e);
    // }
    // }
    // 
    // public boolean dropPartition(String logicName, String partition) {
    // try {
    // // sale_date='201310', region='hangzhou'
    // String sql = "ALTER TABLE " + logicName
    // + " DROP if exists PARTITION (" + partition + ");";
    // Instance instance = SQLTask.run(odps, sql);
    // logger.warn("ODPS sql is " + sql);
    // 
    // boolean isSuccess = sumit(instance);
    // 
    // return isSuccess;
    // } catch (Exception e) {
    // logger.error("createTable() Exception " + e);
    // }
    // return false;
    // }
    // 
    // public boolean sumit(Instance instance) {
    // String id = instance.getId();
    // // logger.warn("ODPS Task id is: " + id);
    // Status status;
    // try {
    // do {
    // status = instance.getStatus();
    // StringBuffer processSummary = new StringBuffer();
    // int tcount = 0;
    // int tworks = 0;
    // int allcount = 0;
    // int allworkds = 0;
    // for (String name : instance.getTaskNames()) {
    // processSummary.append("ODPS Taskname:").append(name);
    // for (StageProgress progress : instance
    // .getTaskProgress(name)) {
    // processSummary.append(",name:" + progress.getName());
    // tworks = progress.getTerminatedWorkers();
    // tcount += tworks;
    // processSummary.append(",tworkers:" + tworks);
    // allworkds = progress.getTotalWorkers();
    // allcount += allworkds;
    // processSummary.append(",totalworks:" + allworkds)
    // .append("\n");
    // // logger.warn(processSummary);
    // }
    // }
    // if (allcount > 0) {
    // logger.warn("percent:" + ((float) tcount / allcount));
    // }
    // // logger.warn("ODPS task:" + id + "; status:"+ status +
    // // "; Summary " + processSummary.toString());
    // } while (status != Status.TERMINATED);
    // if (TaskStatus.Status.FAILED.equals(instance.getStatus())) {
    // throw new IllegalStateException("Odps Instance Task "
    // + instance.getId() + "failed");
    // }
    // 
    // return true;
    // } catch (OdpsException e) {
    // throw new IllegalStateException(e);
    // }
    // }
    // 
    // public boolean contains(String tablename, Odps odps) {
    // for (Table t : odps.tables()) {
    // if (tablename.equals(t.getName())) {
    // return true;
    // }
    // }
    // return false;
    // }
    // 
    // public Table getODPSTable(String tablename, Odps odps) {
    // for (Table t : odps.tables()) {
    // if (tablename.equals(t.getName())) {
    // return t;
    // }
    // }
    // return null;
    // }
    // 
    // public String getAccessId() {
    // return accessId;
    // }
    // 
    // public void setAccessId(String accessId) {
    // this.accessId = accessId;
    // }
    // 
    // public String getAccessKey() {
    // return accessKey;
    // }
    // 
    // public void setAccessKey(String accessKey) {
    // this.accessKey = accessKey;
    // }
    // 
    // public String getProject() {
    // return project;
    // }
    // 
    // public String getPartition() {
    // return partition;
    // }
    // 
    // public void setPartition(String partition) {
    // this.partition = partition;
    // }
    // 
    // public String getOdps_endpoint() {
    // return odps_endpoint;
    // }
    // 
    // private static void addFeedbackInfo(TriggerTaskConfig triggerConfig,
    // InfoType infoType, String info) {
    // if (infoType == InfoType.INFO) {
    // logger.info(info);
    // }
    // if (infoType == InfoType.WARN) {
    // logger.warn(info);
    // }
    // if (infoType == InfoType.ERROR) {
    // logger.error(info);
    // }
    // try {
    // ExecuteState log = ExecuteState.create(infoType, info);
    // log.setTaskId(new Long(triggerConfig.getTaskId()));
    // log.setComponent(DumpOverNotifyClient.MESSAGE_COMPONENT);
    // log.setServiceName(triggerConfig.getAppName());
    // feedbackService.add(log);
    // } catch (Throwable e1) {
    // 
    // }
    // }
}
