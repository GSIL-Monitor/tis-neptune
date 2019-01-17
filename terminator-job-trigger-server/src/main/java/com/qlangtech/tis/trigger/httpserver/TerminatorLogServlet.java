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

/* *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class TerminatorLogServlet {
    // private static final long serialVersionUID = 1L;
    // 
    // private TriggerJobServer triggerServer;
    // 
    // public TerminatorLogServlet(TriggerJobServer triggerServer) {
    // super();
    // Assert.assertNotNull(triggerServer);
    // this.triggerServer = triggerServer;
    // }
    // 
    // public TerminatorLogServlet() {
    // super();
    // }
    // 
    // @Override
    // public void init(javax.servlet.ServletConfig config) throws javax.servlet.ServletException {
    // super.init(config);
    // WebApplicationContext applicationContext = WebApplicationContextUtils.getWebApplicationContext(config.getServletContext());
    // triggerServer = (TriggerJobServer)applicationContext.getBean(TriggerJobServer.class);
    // }
    // 
    // @Override
    // protected void service(HttpServletRequest req, HttpServletResponse response)
    // throws ServletException, IOException {
    // 
    // final int page = Integer.parseInt(req.getParameter("page"));
    // final int pagesize = Integer.parseInt(StringUtils.defaultIfEmpty(
    // req.getParameter("pagesize"), "20"));
    // 
    // if ("getlogbytaskid".equals(req.getParameter("action"))) {
    // 
    // Long taskid = Long.parseLong(req.getParameter("taskid"));
    // InfoType type = null;
    // try {
    // type = InfoType.getType(Integer.parseInt(req
    // .getParameter("level")));
    // } catch (Throwable e) {
    // 
    // }
    // 
    // try {
    // List<ExecuteLog> logs = triggerServer.getFeedbackService()
    // .search(taskid, type, page, pagesize);
    // 
    // writeResult2Client(response, logs);
    // } catch (Exception e) {
    // throw new ServletException(e);
    // }
    // } else if ("getlogbyname".equals(req.getParameter("action"))) {
    // // http://localhost:8199/execute_log?action=getlogbyname&indexname=search4sucai&page=4&start=201409021000
    // try {
    // final String indexname = req.getParameter("indexname");
    // StringUtils.startsWith(indexname, "search4");
    // 
    // final String time = req.getParameter("start");
    // SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmm");
    // Calendar calendar = Calendar.getInstance();
    // calendar.setTime(format.parse(time));
    // 
    // List<ExecuteLog> logs = triggerServer.getFeedbackService()
    // .search(indexname, calendar.getTime(), page, pagesize);
    // writeResult2Client(response, logs);
    // } catch (Exception e) {
    // throw new ServletException(e);
    // }
    // 
    // } else if ("getlogbyrowkey".equals(req.getParameter("action"))) {
    // try {
    // String rowkey = req.getParameter("rowkey");
    // List<ExecuteLog> logs = triggerServer.getFeedbackService().search(
    // rowkey);
    // writeResult2Client(response, logs);
    // } catch (Exception e) {
    // throw new ServletException(e);
    // }
    // 
    // } else {
    // 
    // throw new IllegalStateException("action:"
    // + req.getParameter("action") + " is not illegal");
    // }
    // 
    // }
    // 
    // /**
    // * @param response
    // * @param logs
    // 
    // * @throws JSONException
    // * @throws IOException
    // */
    // private void writeResult2Client(HttpServletResponse response,
    // List<ExecuteLog> logs) throws JSONException, IOException {
    // SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    // JSONArray array = new JSONArray();
    // JSONObject row;
    // for (ExecuteLog log : logs) {
    // row = new JSONObject();
    // row.put("level", log.getLevel().getType());
    // row.put("levelLiteria", log.getLevel());
    // row.put("taskid", log.getTaskid());
    // row.put("msg", log.getMsg());
    // 
    // row.put("time", format.format(log.getTime()));
    // row.put("component", log.getComponent());
    // row.put("address", log.getAddress());
    // row.put("sequence", log.getSequence());
    // 
    // array.put(row);
    // }
    // 
    // IOUtils.write(array.toString(1), response.getOutputStream());
    // }
}
