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
package com.qlangtech.tis.trigger.tddl;

/* *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class TddlReader {
    // private static final Log logger = LogFactory.getLog(TddlReader.class);
    // 
    // private IFeedbackService feedbackService;
    // 
    // public IFeedbackService getFeedbackService() {
    // return feedbackService;
    // }
    // 
    // public void setFeedbackService(IFeedbackService feedbackService) {
    // this.feedbackService = feedbackService;
    // }
    // 
    // //	private TGroupDataSource ds;
    // private Connection conn;
    // private List<Map<String, String>> rows;
    // private Map<String, String> temp;
    // private TddlTaskConfig taskConfig;
    // protected Long maxDumpCount;
    // protected String tableName;
    // protected int totalNum;
    // protected int startNum;
    // 
    // //	public TddlReader(TGroupDataSource ds, Connection conn,
    // //			TddlTaskConfig taskConfig, String tableName) {
    // //		super();
    // //		this.ds = ds;
    // //		this.conn = conn;
    // //		this.tableName = tableName;
    // //		this.taskConfig = taskConfig;
    // //		this.maxDumpCount=taskConfig.getMaxDumpCount();
    // //	}
    // 
    // 
    // 
    // public void closeSource() {
    // try {
    // conn.close();
    // } catch (SQLException e) {
    // // TODO Auto-generated catch block
    // e.printStackTrace();
    // }
    // }
    // 
    // 
    // public Map<String, String> getCols() {
    // addFeedbackInfo(taskConfig, InfoType.WARN, "get table:" + tableName
    // + " cols info");
    // try {
    // Map<String, String> cols = new HashMap<String, String>();
    // DatabaseMetaData metadata = conn.getMetaData();
    // ResultSet columns = metadata
    // .getColumns(null, null, tableName, null);
    // 
    // while (columns.next()) {
    // cols.put(
    // StringUtils.lowerCase(columns.getString("COLUMN_NAME")),
    // StringUtils.lowerCase(columns.getString("TYPE_NAME")));
    // }
    // return cols;
    // } catch (Exception e) {
    // addFeedbackInfo(taskConfig, InfoType.ERROR,
    // "get table:" + tableName + " cols info fail!!!"
    // + ExceptionUtils.getFullStackTrace(e));
    // }
    // 
    // return null;
    // 
    // }
    // 
    // 
    // private void addFeedbackInfo(TriggerTaskConfig triggerConfig,
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
