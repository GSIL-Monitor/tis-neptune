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
 * 接收触发全量触发
 * 线上触发例子：http://tsearcher2010176126069.n.et2:8199/trigger_full_dump?service_name=search4_7_sydd&exec_type=fullbuild
 *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class TriggerFullDumpServlet {
    // private static final long serialVersionUID = 1L;
    // 
    // private static final Log logger = LogFactory
    // .getLog(TriggerFullDumpServlet.class);
    // 
    // private TriggerJobServer triggerServer;
    // 
    // public TriggerFullDumpServlet(TriggerJobServer triggerServer) {
    // super();
    // Assert.assertNotNull(triggerServer);
    // this.triggerServer = triggerServer;
    // }
    // 
    // public TriggerFullDumpServlet() {
    // super();
    // }
    // 
    // // @Override
    // // protected void doGet(HttpServletRequest req, HttpServletResponse resp)
    // // throws ServletException, IOException {
    // //
    // // //super.doGet(req, resp);
    // // resp.getWriter().println("<h1>Hello World</h1>");
    // // }
    // private static final ExecutorService threadPool = java.util.concurrent.Executors
    // .newCachedThreadPool();
    // public static final String XML_CONTENT_TYPE = "text/json";
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
    // final String serverName = req.getParameter("service_name");
    // final ExecType execType = ExecType.parse(req.getParameter("exec_type"));
    // 
    // if (!StringUtils.startsWith(serverName, "search4")) {
    // throw new ServletException(
    // "serverName should be start with 'search4'");
    // }
    // final TriggerFullResult triggerResult = new TriggerFullResult();
    // TriggerTaskConfig config = null;
    // final ITriggerContext triggerContext = new TriggerContextImpl(req);
    // try {
    // config = triggerServer.getTaskConfigFromRepository(serverName);
    // triggerServer.addFeedbackInfo(config, InfoType.INFO,
    // "start execute,type:" + execType.getType());
    // 
    // final TriggerTaskConfig finalConfig = config;
    // if (config == null) {
    // triggerResult.setSuccess(false);
    // triggerResult.setReason("execute too frequent");
    // } else {
    // triggerResult.setTaskid(config.getTaskId());
    // threadPool.execute(new Runnable() {
    // @Override
    // public void run() {
    // try {
    // triggerServer.triggerFullDump(finalConfig,
    // execType, triggerContext);
    // } catch (Exception e) {
    // logger.error(e.getMessage(), e);
    // triggerServer.addFeedbackError(finalConfig, e);
    // }
    // }
    // });
    // }
    // 
    // } catch (Exception e) {
    // triggerResult.setSuccess(false);
    // triggerResult.setReason(e.getMessage());
    // // throw new ServletException(e);
    // logger.error(e.getMessage(), e);
    // 
    // triggerServer.addFeedbackError(config, e);
    // }
    // 
    // response.setContentType(XML_CONTENT_TYPE);
    // response.getWriter().print(triggerResult.serialize());
    // }
}
