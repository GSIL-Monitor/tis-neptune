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
package com.qlangtech.tis.trigger.httpserver;

/*
http://localhost:8199/console?action=getAllJobsInServer
 *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class TriggerConsoleServlet {
    // TriggerJobConsole
    // private static final Log log = LogFactory
    // .getLog(TriggerConsoleServlet.class);
    // 
    // private static final long serialVersionUID = 1L;
    // 
    // private TriggerJobServer triggerServer;
    // 
    // public TriggerConsoleServlet(TriggerJobServer triggerServer) {
    // super();
    // Assert.assertNotNull(triggerServer);
    // this.triggerServer = triggerServer;
    // }
    // 
    // public TriggerConsoleServlet() {
    // super();
    // }
    // 
    // // private static Set<String> indexNameReliativeActions = new
    // // HashSet<String>();
    // // static {
    // // indexNameReliativeActions.add("isServing");
    // // indexNameReliativeActions.add("pause");
    // // indexNameReliativeActions.add("isPause");
    // // indexNameReliativeActions.add("resume");
    // // }
    // 
    // @Override
    // public void init(javax.servlet.ServletConfig config)
    // throws javax.servlet.ServletException {
    // super.init(config);
    // WebApplicationContext applicationContext = WebApplicationContextUtils
    // .getWebApplicationContext(config.getServletContext());
    // triggerServer = (TriggerJobServer) applicationContext
    // .getBean(TriggerJobServer.class);
    // }
    // 
    // @Override
    // protected void service(HttpServletRequest req, HttpServletResponse resp)
    // throws ServletException, IOException {
    // 
    // String action = req.getParameter("action");
    // String indexname = req.getParameter("indexname");
    // TriggerDumpResult result = new TriggerDumpResult();
    // if ("isServing".equals(action)) {
    // result.put("servicing", String.valueOf(this.isServing(indexname)));
    // } else if ("pause".equals(action)) {
    // Assert.assertTrue(this.isServing(indexname));
    // // triggerServer.pause(indexname);
    // } else if ("isPause".equals(action)) {
    // // Assert.assertTrue(this.isServing(indexname));
    // // result.put("pause",
    // // String.valueOf(triggerServer.isPause(indexname)));
    // } else if ("resume".equals(action)) {
    // // Assert.assertTrue(this.isServing(indexname));
    // // triggerServer.resume(indexname);
    // } else if ("getAllJobsInServer".equalsIgnoreCase(action)) {
    // List<JobDesc> jobdesc = triggerServer.getAllJobsInServer();
    // JsonUtil.toString(jobdesc, resp.getOutputStream());
    // return;
    // } else if ("getJob".equalsIgnoreCase(action)) {
    // JsonUtil.toString(
    // triggerServer.getJob(req.getParameter("indexname"),
    // Long.parseLong(req.getParameter("jobid"))),
    // resp.getOutputStream());
    // return;
    // } else if ("getallconn".equals(action)) {
    // // 取得服务器中的连接数据
    // // Collection<Connection> conns = triggerServer.getAllConnection();
    // // JSONArray array = new JSONArray();
    // // JSONObject j = null;
    // // try {
    // // for (Connection cn : conns) {
    // // j = new JSONObject();
    // // j.put("service", cn.getServiceName());
    // //
    // // array.put(j);
    // // }
    // //
    // // IOUtils.write(array.toString(1), resp.getOutputStream());
    // // } catch (JSONException e) {
    // // throw new ServletException(e);
    // // }
    // } else {
    // throw new IllegalArgumentException("action:" + action
    // + " can not match any method");
    // }
    // 
    // // write result to outputStream
    // IOUtils.write(result.serialize(), resp.getOutputStream());
    // 
    // // super.service(req, resp);
    // }
    // 
    // public boolean isServing(String coreName) {
    // // return triggerServer.getConnection(coreName) != null;
    // return true;
    // }
    // 重新启动定时任务
    // @Override
    // public void resume(String coreName) {
    // 
    // log.info("execute resume for app:" + coreName);
    // 
    // try {
    // this.setAppState(coreName, false);
    // } catch (Exception e) {
    // log.error(e.getMessage(), e);
    // throw new RuntimeException(e);
    // }
    // }
    // 
    // @Override
    // public List<JobDesc> getAllJobsInServer() {
    // 
    // try {
    // final Set<TriggerKey> triggerkeys = triggerServer
    // .getScheduler()
    // .getTriggerKeys(
    // GroupMatcher
    // .triggerGroupStartsWith(TriggerJobServer.GROUP_PREFIX));
    // return getJobDesc(triggerkeys);
    // } catch (SchedulerException e) {
    // throw new RuntimeException(e.getMessage(), e);
    // }
    // }
    // 
    // private List<JobDesc> getJobDesc(final Set<TriggerKey> triggerkeys)
    // throws SchedulerException {
    // List<JobDesc> result = new ArrayList<JobDesc>();
    // for (TriggerKey key : triggerkeys) {
    // 
    // JobDesc desc = new JobDesc(Long.parseLong(StringUtils
    // .substringAfter(key.getGroup(),
    // TriggerJobServer.GROUP_PREFIX)));
    // 
    // try {
    // desc.setServerIp(InetAddress.getLocalHost().getHostAddress());
    // } catch (UnknownHostException e) {
    // 
    // }
    // 
    // RTriggerKey trigerkey = new RTriggerKey();
    // trigerkey.setGroup(key.getGroup());
    // trigerkey.setName(key.getName());
    // desc.setTriggerKey(trigerkey);
    // 
    // Trigger trigger = triggerServer.getScheduler().getTrigger(key);
    // 
    // desc.setPreviousFireTime(trigger.getPreviousFireTime());
    // 
    // if (trigger instanceof CronTrigger) {
    // desc.setCrontabExpression(((CronTrigger) trigger)
    // .getCronExpression());
    // }
    // 
    // result.add(desc);
    // }
    // 
    // return result;
    // }
    // 
    // @Override
    // public List<JobDesc> getJob(Long jobid) {
    // 
    // try {
    // 
    // final Set<TriggerKey> triggerkeys = triggerServer.getScheduler()
    // .getTriggerKeys(
    // GroupMatcher.triggerGroupEquals(triggerServer
    // .getGroupName(jobid)));
    // 
    // return getJobDesc(triggerkeys);
    // } catch (SchedulerException e) {
    // throw new RuntimeException(e);
    // }
    // }
    // 
    // @Override
    // public void pause(String coreName) {
    // // 数据库持久层设置
    // // 将scheduleer中的任务更新
    // try {
    // 
    // log.info("execute pause for app:" + coreName);
    // 
    // final List<Long> jobs = setAppState(coreName, true);
    // 
    // Set<TriggerKey> triggerIdSet = null;
    // JobSchedule jobSchedule = null;
    // for (Long jobid : jobs) {
    // triggerIdSet = triggerServer.getScheduler().getTriggerKeys(
    // GroupMatcher.triggerGroupEquals(triggerServer
    // .getGroupName(jobid)));
    // 
    // for (TriggerKey triggerkey : triggerIdSet) {
    // 
    // jobSchedule = new JobSchedule(jobid,
    // TriggerJobServer.NERVER_WILL_TRIGGER_CRONTAB, true);
    // 
    // // scheduler.rescheduleJob(triggerkey, createTrigger(
    // // triggerkey.getName(), getGroupName(jobid),
    // // NERVER_WILL_TRIGGER_CRONTAB));
    // 
    // triggerServer.getScheduler().rescheduleJob(
    // triggerkey,
    // triggerServer.createTrigger(triggerkey.getName(),
    // jobSchedule));
    // }
    // 
    // }
    // 
    // // Set<JobKey> jobIdSet = new HashSet<JobKey>();
    // // for (Long jobid : jobs) {
    // // jobIdSet.addAll(scheduler.getJobKeys(GroupMatcher
    // // .jobGroupEquals(getGroupName(jobid))));
    // // }
    // //
    // // for (JobKey jobkey : jobIdSet) {
    // //
    // // JobDetail detail = scheduler.getJobDetail(jobkey);
    // // Connection conn = null;
    // // if (detail != null) {
    // // // 关闭客户端的连接socket
    // // conn = (Connection) detail.getJobDataMap().get(CONNECTION);
    // // conn.disopse();
    // // }
    // //
    // // scheduler.deleteJob(jobkey);
    // // }
    // } catch (Exception e) {
    // 
    // log.error(e.getMessage(), e);
    // 
    // throw new RuntimeException(e);
    // }
    // }
    // 
    // @Override
    // public boolean isPause(String coreName) {
    // 
    // final AppTrigger trigger = triggerServer.getJobMetaDataDAO().queryJob(
    // coreName);
    // if (!trigger.isPause()) {
    // return false;
    // }
    // 
    // if (trigger.getJobsId().size() < 1) {
    // // 该应用还没有定义dump job 算作是停止状态
    // return true;
    // }
    // 
    // if (triggerServer.getTriggerBizDAO().getTriggerJobDAO().countByExample(
    // createTriggerJobCriteria(trigger.getJobsId(), true)) > 0) {
    // // 有dump，无论是增量还是全量有没有停止的
    // return false;
    // }
    // 
    // // List<JobDesc> triggerlist = new ArrayList<JobDesc>();
    // //
    // // for (Long jobid : trigger.getJobsId()) {
    // // triggerlist.addAll(getJob(jobid));
    // // }
    // 
    // // for (JobDesc desc : triggerlist) {
    // // if (!StringUtils.contains(desc.getCrontabExpression(),
    // // TriggerJobServer.YEAR_OF_2099)) {
    // // return true;
    // // }
    // // }
    // return true;
    // }
    // 
    // private List<Long> setAppState(String coreName, boolean isStop) {
    // // 数据库持久层设置
    // final AppTrigger trigger = triggerServer.getJobMetaDataDAO().queryJob(
    // coreName);
    // final List<Long> jobs = trigger.getJobsId();
    // 
    // if (jobs.size() < 1) {
    // log.warn("coreName:" + coreName + " has not define any dump job");
    // return jobs;
    // }
    // 
    // final IJobMetaDataDAO metaDataDAO = triggerServer.getJobMetaDataDAO();
    // metaDataDAO.setStop(coreName, isStop);
    // 
    // TriggerJobCriteria tcriteria = this.createTriggerJobCriteria(jobs,
    // isStop);
    // // new TriggerJobCriteria();
    // // tcriteria.createCriteria().andJobIdIn(jobs).andIsStopEqualTo(
    // // isStop ? STOPED_NOT : STOPED).andDomainEqualTo(
    // // JobConstant.DOMAIN_TERMINAOTR);
    // 
    // TriggerJob record = new TriggerJob();
    // record.setIsStop(isStop ? TriggerJobServer.STOPED
    // : TriggerJobServer.STOPED_NOT);
    // 
    // // 更新触发器表
    // triggerServer.getTriggerBizDAO().getTriggerJobDAO()
    // .updateByExampleSelective(record, tcriteria);
    // 
    // return jobs;
    // }
    // 
    // private TriggerJobCriteria createTriggerJobCriteria(List<Long> jobs,
    // boolean isStop) {
    // TriggerJobCriteria tcriteria = new TriggerJobCriteria();
    // tcriteria.createCriteria().andJobIdIn(jobs).andIsStopEqualTo(
    // isStop ? TriggerJobServer.STOPED_NOT : TriggerJobServer.STOPED)
    // .andDomainEqualTo(JobConstant.DOMAIN_TERMINAOTR);
    // return tcriteria;
    // }
}
