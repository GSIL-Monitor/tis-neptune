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
package com.qlangtech.tis.deploy.servlet;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import com.qlangtech.tis.manage.common.ConfigFileContext.StreamProcess;
import com.qlangtech.tis.manage.common.HttpUtils;
import com.qlangtech.tis.pubhook.common.RunEnvironment;

/*
 * 增量发布节点入口 curl
 * 'http://localhost:8080/deploy/incr-deploy?qid=3&packageaddress=dddd&groupName
 * =tis-incr-search4xxxx'
 * sh ./rsync_app.sh tis-realtime-transfer /opt/data/jetty/incrdeploy/tis-realtime-transfer/tis-realtime-transfer-999.tar.gz daily
 *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class IncrDeployServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private static final String KEY_QID = "qid";

    private static final String KEY_PACKAGE_ADDRESS = "packageaddress";

    private static final String KEY_APPNAME = "groupName";

    private static final ExecutorService executeService = Executors.newCachedThreadPool();

    private static final Logger logger = LoggerFactory.getLogger("incr_deploy");

    @Override
    public void init(ServletConfig config) throws ServletException {
    }

    // protected boolean shallValidateCollectionExist() {
    // return false;
    // }
    private final Map<String, ReentrantLock> idles = new ConcurrentHashMap<String, ReentrantLock>();

    // public TisServlet() {
    // super();
    // }
    protected ReentrantLock getExecLock(String groupName) {
        ReentrantLock lock = idles.get(groupName);
        if (lock == null) {
            synchronized (IncrDeployServlet.this) {
                lock = idles.get(groupName);
                if (lock == null) {
                    lock = new ReentrantLock();
                    ReentrantLock tmp = null;
                    tmp = idles.putIfAbsent(groupName, lock);
                    if (tmp != null) {
                        lock = tmp;
                    }
                }
            }
        }
        return lock;
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!isValidParams(req, resp)) {
            return;
        }
        final String indexGroup = req.getParameter(KEY_APPNAME);
        MDC.put("app", indexGroup);
        // execContext.getString(KEY_PACKAGE_ADDRESS);//
        final String packageAddress = req.getParameter(KEY_PACKAGE_ADDRESS);
        // "http://10.1.5.214:8080/tgz/tis-realtime-transfer.tar.gz";
        // //
        String dataDir = System.getProperty("data.dir");
        File workDir = new File(dataDir, "incrdeploy");
        FileUtils.forceMkdir(workDir);
        // 理论上一个增量实例中可以跑N个索引的增量实例
        // execContext.getString(TisServlet.KEY_APP_NAME);
        final String qid = req.getParameter(KEY_QID);
        final File indexDeployDir = new File(workDir, indexGroup);
        FileUtils.forceMkdir(indexDeployDir);
        logger.info("work dir:" + indexDeployDir.getAbsolutePath());
        // 保留最近的三个文件
        keepLatest3TGZPackage(indexGroup, indexDeployDir);
        // 下载到本地的打包文件
        File tarFile = new File(indexDeployDir, indexGroup + "-" + qid + ".tar.gz");
        final ReentrantLock lock = getExecLock(indexGroup);
        if (lock.isLocked()) {
            sendNobleCallback(false, qid);
            writeResult(false, "is under deploy process", resp);
            return;
        }
        executeService.execute(new Runnable() {

            @Override
            public void run() {
                if (lock.tryLock()) {
                    MDC.put("app", indexGroup);
                    try {
                        // 下载远程最新的包
                        HttpUtils.processContent(new URL(packageAddress), new StreamProcess<Void>() {

                            @Override
                            public Void p(int status, InputStream stream, String md5) {
                                try {
                                    FileUtils.copyInputStreamToFile(stream, tarFile);
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                                return null;
                            }
                        });
                        deploy(indexDeployDir, indexGroup, tarFile, RunEnvironment.current(), qid);
                    } catch (Exception e) {
                        try {
                            sendNobleCallback(false, qid);
                        } catch (Throwable e1) {
                        }
                        throw new RuntimeException(e);
                    } finally {
                        lock.unlock();
                    }
                } else {
                    logger.error("is under deploy process");
                }
            }
        });
        writeResult(true, "success submit process", resp);
    }

    protected void keepLatest3TGZPackage(final String indexGroup, final File indexDeployDir) throws IOException {
        List<String> fnames = new ArrayList<>();
        for (String fname : indexDeployDir.list((dir, name) -> {
            return StringUtils.startsWith(name, indexGroup);
        })) {
            fnames.add(fname);
        }
        Collections.sort(fnames);
        File delfile = null;
        for (int i = 3; i < fnames.size(); i++) {
            delfile = new File(indexDeployDir, fnames.get(i));
            FileUtils.forceDelete(delfile);
            logger.info("file delete:" + delfile.getAbsolutePath());
        }
    }

    /**
     * 向noble节点发送成功或者是失败的反馈
     *
     * @param success
     * @param qid
     * @throws ServletException
     */
    private void sendNobleCallback(boolean success, String qid) throws ServletException {
        try {
            logger.info("sned callback to " + DeployConfig.getDeployCallbackPath() + ",success:" + success + ",qid:" + qid);
            StringBuffer applyURL = new StringBuffer(DeployConfig.getDeployCallbackPath());
            applyURL.append("?qid=").append(qid);
            applyURL.append("&status=").append(success);
            applyURL.append("&localIp=").append(URLEncoder.encode("255.255.255.255", "utf8"));
            URL url = new URL(applyURL.toString());
            try {
                HttpUtils.processContent(url, new StreamProcess<Void>() {

                    @Override
                    public Void p(int status, InputStream stream, String md5) {
                        // }
                        return null;
                    }
                });
            } catch (Throwable e) {
                logger.error("apply url:" + url, e);
            }
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    /**
     * 发布
     *
     * @param workDir
     * @param incrGroupName
     * @param tarPath
     * @param runtime
     * @throws Exception
     */
    private void deploy(final File workDir, String incrGroupName, File tarPath, RunEnvironment runtime, String qid) throws Exception {
        // File workDir = new File("/opt/data/jetty/incrdeploy/search4xxxx");
        List<String> env = new ArrayList<String>();
        logger.info("run environment==================================================");
        String pair = null;
        for (Map.Entry<String, String> entry : System.getenv().entrySet()) {
            pair = entry.getKey() + "=" + entry.getValue();
            env.add(pair);
            logger.info(pair);
        }
        logger.info("=================================================================");
        File incrGroupDir = new File(workDir, incrGroupName);
        FileUtils.deleteDirectory(incrGroupDir);
        logger.info("pre IncrGroupDir removed:" + incrGroupDir.getAbsolutePath());
        Process process = Runtime.getRuntime().exec("sh /opt/data/jetty/incrdeploy/rsync_app.sh " + incrGroupName + " " + tarPath.getAbsolutePath() + " " + runtime.getKeyName() + " 2>&1", env.toArray(new String[] {}), workDir);
        BufferedReader error = null;
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), Charset.forName("gbk")));
        try {
            executeService.execute(new Runnable() {

                public void run() {
                    try {
                        String line = null;
                        while ((line = reader.readLine()) != null) {
                            logger.info(line);
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
            error = new BufferedReader(new InputStreamReader(process.getErrorStream(), Charset.forName("gbk")));
            final BufferedReader err = error;
            executeService.execute(new Runnable() {

                public void run() {
                    try {
                        String line = null;
                        while ((line = err.readLine()) != null) {
                            logger.info(line);
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
            int exitCode = process.waitFor();
            sendNobleCallback((exitCode == 0), qid);
        } finally {
            IOUtils.closeQuietly(error);
            IOUtils.closeQuietly(reader);
        }
    }

    protected boolean isValidParams(HttpServletRequest req, HttpServletResponse res) throws ServletException {
        // 发布批次ID
        String qid = req.getParameter(KEY_QID);
        String packageaddress = req.getParameter(KEY_PACKAGE_ADDRESS);
        String indexGroupName = req.getParameter(KEY_APPNAME);
        if (StringUtils.isBlank(qid) || StringUtils.isBlank(packageaddress) || StringUtils.isBlank(indexGroupName)) {
            // qid,packageaddress,indexName is null");
            return writeResult(false, "either 'qid','packageaddress','indexName' is null", res);
        }
        return true;
    }

    protected boolean writeResult(boolean success, String msg, ServletResponse res) throws ServletException {
        res.setContentType("text/json");
        try {
            JSONObject json = new JSONObject();
            json.put("success", success);
            if (StringUtils.isNotBlank(msg)) {
                json.put("msg", msg);
            }
            res.getWriter().write(json.toString(1));
            return success;
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}
