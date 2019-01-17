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
public class TestFeedBackService {
    // 
    // private static final long serialVersionUID = 1L;
    // 
    // private final static ApplicationContext context;
    // 
    // static {
    // context = new ClassPathXmlApplicationContext(
    // "/config/terminator-job-trigger-config-context.xml",
    // "/config/terminator-job-trigger-server-context.xml",
    // "/config/terminatorTriggerBizDal-datasource-context.xml");
    // }
    // 
    // public void testInsert() throws Exception {
    // 
    // IFeedbackService feedback = (IFeedbackService) context
    // .getBean("feedbackService");
    // 
    // String serviceName = "search4123";
    // 
    // char c = 'a';
    // for (int i = 0; i < 3000; i++) {
    // 
    // System.out.println("start to insert log :" + i);
    // 
    // StringBuffer content = new StringBuffer();
    // char n = (char) (c + i % 26);
    // for (int j = 0; j < i; j++) {
    // content.append(n);
    // }
    // ExecuteState state = ExecuteState.create(InfoType.INFO, InetAddress
    // .getLocalHost().getHostAddress() + content.toString());
    // state.setTaskId(777l);
    // feedback.add(state);
    // 
    // Thread.sleep(1000);
    // }
    // }
    // public void testView() {
    // FeedbackService feedback = (FeedbackService) context
    // .getBean("feedbackService");
    // 
    // IHdfsIndexDAO hdfsindex = (IHdfsIndexDAO) context
    // .getBean("hdfsIndexDAO");
    // 
    // HdfsIndexCriteria query = new HdfsIndexCriteria();
    // query.createCriteria().andTaskIdEqualTo((short) 777l);
    // 
    // ExecuteState state = null;
    // for (HdfsIndex index : hdfsindex.selectByExample(query, 1, 300)) {
    // state = feedback.getLog(index);
    // 
    // System.out.println(state.getFrom());
    // System.out.println(state.getMsg());
    // //System.out.println(state.getJobId());
    // System.out.println(state.getTaskId());
    // System.out.println("============================");
    // }
    // }
}
