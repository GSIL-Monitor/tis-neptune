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
package com.qlangtech.tis.trigger;

/* *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class LaunchServer {
    // private static final ApplicationContext spring_context;
    // 
    // private static final Log log = LogFactory.getLog(LaunchServer.class);
    // 
    // public static void main(String[] arg) throws Exception {
    // // LaunchServer launch = new LaunchServer();
    // //
    // // launch.start();
    // //
    // // Thread.sleep(1000 * 1000);
    // }
    // 
    // private FeebackServer feebackServer;
    // static {
    // spring_context = new ClassPathXmlApplicationContext(
    // "/config/terminator-job-trigger-config-context.xml",
    // "/config/terminator-job-trigger-server-context.xml",
    // "/config/terminatorTriggerBizDal-datasource-context.xml");
    // }
    // 
    // @Override
    // public void destroy() {
    // 
    // }
    // 
    // @Override
    // public void init(DaemonContext context) throws DaemonInitException,
    // Exception {
    // // setCustomClassLoader(context);
    // }
    // 
    // // private void setCustomClassLoader(DaemonContext context)
    // // throws MalformedURLException {
    // // DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory)
    // // spring_context
    // // .getAutowireCapableBeanFactory();
    // //
    // // String[] argument = context.getArguments();
    // // Assert.assertNotNull(argument);
    // // File jarDir = new File(argument[0]);
    // // Assert.assertTrue("jarDir:" + jarDir.getAbsolutePath()
    // // + " shall exists", jarDir.exists());
    // // List<URL> dir = new ArrayList<URL>();
    // // for (String fileName : jarDir.list(new FilenameFilter() {
    // // public boolean accept(File dir, String name) {
    // // return StringUtils.endsWith(name, ".jar");
    // // }
    // // })) {
    // // dir.add((new File(jarDir, fileName).toURI().toURL()));
    // // }
    // //
    // // URLClassLoader classLoader = new URLClassLoader(dir.toArray(new URL[dir
    // // .size()]));
    // //
    // // beanFactory.setBeanClassLoader(classLoader);
    // // }
    // 
    // @Override
    // public void start() throws Exception {
    // 
    // // File file = new File("./dependency");
    // // ArrayList<URL> urls = new ArrayList<URL>();
    // // for (String f : file.list()) {
    // // urls.add(new File(file, f).toURI().toURL());
    // // }
    // // ClassLoader classLoader = new URLClassLoader(urls.toArray(new
    // // URL[0]),
    // // this.getClass().getClassLoader());
    // // classLoader.loadClass();
    // 
    // this.feebackServer = spring_context.getBean("feebackServer",
    // FeebackServer.class);
    // 
    // spring_context.getBean("consoleTriggerJobService");
    // Assert.assertNotNull("distributeComponentLogCollect can not be null",
    // spring_context.getBean("distributeComponentLogCollect",
    // DistributeComponentLogCollect.class));
    // 
    // log.info("LaunchServer started,waiting client register");
    // }
    // 
    // @Override
    // public void stop() throws Exception {
    // // this.feebackServer.dispose();
    // }
}
