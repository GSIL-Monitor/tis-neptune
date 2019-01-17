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

/* *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class TestDumpOverNotifyClient {
    // 
    // static final ApplicationContext context;
    // 
    // static {
    // context = new ClassPathXmlApplicationContext(
    // "/config/terminator-job-trigger-config-context.xml",
    // "/config/terminator-job-trigger-server-context.xml",
    // "/config/terminatorTriggerBizDal-datasource-context.xml");
    // 
    // }
    // public void testBuild() {
    // 
    // // DumpOverNotifyClient dumpClient = (DumpOverNotifyClient) context
    // // .getBean("dumpOverNotifyClient");
    // 
    // DumpOverNotifyClient dumpClient = new DumpOverNotifyClient();
    // dumpClient.setFeedbackService(new IFeedbackService() {
    // // @Override
    // // public void insertServiceLog(ExecuteState log) {
    // // }
    // 
    // @Override
    // public ITaskDAO getTaskDAO() {
    // 
    // return null;
    // }
    // 
    // @Override
    // public void addFeedbackInfo(String indexName, int taskid,
    // InfoType infoType, String info) {
    // }
    // 
    // @Override
    // public void addPhraseSuccess(String indexName, int taskid,
    // String info, Long phrase) {
    // }
    // 
    // @Override
    // public void addPhraseFaild(String indexName, int taskid,
    // String info, Long phrase) {
    // }
    // 
    // @Override
    // public void addPhraseFaild(String indexName, int taskid,
    // String info, Throwable e, Long phrase) {
    // }
    // 
    // @Override
    // public List<ExecuteLog> search(String rowkey) {
    // 
    // return null;
    // }
    // 
    // @Override
    // public List<ExecuteLog> search(Long taskid, InfoType logLevel,
    // int page, int pageSize) {
    // 
    // return null;
    // }
    // 
    // @Override
    // public List<ExecuteLog> search(String serviceName, Date startTime,
    // int page) {
    // 
    // return null;
    // }
    // 
    // @Override
    // public List<ExecuteLog> search(String serviceName, Date startTime,
    // int page, int pageSize) {
    // 
    // return null;
    // }
    // 
    // @Override
    // public void add(ExecuteState log) {
    // System.out.println("taskid:" + log.getTaskId() + "msg:"
    // + log.getMsg());
    // }
    // });
    // 
    // // ImportDataProcessInfo state = new ImportDataProcessInfo(270);
    // // //state.setIndexName("search4_182589402_index12314");
    // //
    // // state.setIndexName("search4_182589402_testnew");
    // //
    // // state.setOdpsAccessId("07mbAZ3it1QaLTE8");
    // // state.setOdpsAccessKey("BEGxJuH9JY7E6AYR2qWoHSjvufN0cV");
    // // state.setOdpsProject("wjb_tsearcher");
    // // OTable table = new OTable("t_buyer_detail", 1);
    // // table.setPartition("ps=20140821_limit_1000,seller_mod={groupIndex}");
    // // state.setOdpsTable(table);
    // // state.setTimepoint("20140822000001");
    // //
    // // dumpClient.processBuildIndexAndSynIndex(state);
    // 
    // try {
    // Thread.sleep(10000 * 1000);
    // } catch (InterruptedException e) {
    // e.printStackTrace();
    // }
    // }
    // 
    // public List<ExecuteLog> search(final Long taskid, final InfoType logLevel,
    // final int page, final int pageSize) {
    // return null;
    // }
    // 
    // public List<ExecuteLog> search(final String serviceName,
    // final Date startTime, final int page) {
    // return null;
    // }
    // 
    // public List<ExecuteLog> search(final String serviceName,
    // final Date startTime, final int page, final int pageSize) {
    // return null;
    // }
    // 
    // /**
    // * @param args
    // */
    // public static void main(String[] args) {
    // // /config/terminator-job-trigger-server-context.xml
    // 
    // }
}
