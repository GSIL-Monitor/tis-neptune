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
package com.qlangtech.tis.indexbuilder.map;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.yarn.api.ApplicationConstants.Environment;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.qlangtech.tis.hdfs.TISHdfsUtils;
import com.qlangtech.tis.manage.common.ConfigFileReader;
import com.qlangtech.tis.manage.common.IndexBuildParam;

/* *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class HdfsIndexGetConfig {

    public static final Logger logger = LoggerFactory.getLogger(HdfsIndexGetConfig.class);

    private final FileSystem fs;

    long startTime;

    private String taskid = "";

    public HdfsIndexGetConfig() throws IOException {
        startTime = System.currentTimeMillis();
        fs = TISHdfsUtils.getFileSystem();
    // getAllFileName();
    // indexSchema = new IndexSchema(new
    // SolrResourceLoader("",IndexConf.class.getClassLoader() ,null),
    // indexConf.getSchemaName(), null);
    }

    // @Override
    public TaskReturn map(TaskContext context) {
        final IndexConf indexConf = getIndexConf(context);
        try {
            // System.out.println("config.xml url:"
            // + this.getClass().getClassLoader()
            // .getResource("config.xml"));
            Path tmpDir = new Path(Environment.PWD.$(), YarnConfiguration.DEFAULT_CONTAINER_TEMP_DIR);
            logger.info("tmp dir:" + tmpDir);
            // Configuration conf = new Configuration();
            // String fsName = indexConf.getFsName();
            String fsName = indexConf.getSourceFsName();
            // logger.warn("remote hdfs host:" + fsName);
            // conf.set("fs.default.name", fsName);
            // createFileSystem(TSearcherConfigFetcher.get().getHdfsAddress());
            taskid = context.getUserParam("indexing.taskid");
            // 词典处理
            String serviceName = // "indexing.servicename"
            context.getUserParam(IndexBuildParam.INDEXING_SERVICE_NAME);
            // DicManageClient dicManageClient=new DicManageClient(serviceName);
            // dicManageClient.checkRemoteDic();
            // final String taskOutPath = context.getMapPath();
            // String corepropPath = context
            // .getUserParam("indexing." + ConfigConstant.FILE_CORE_PROPERTIES);
            // if (StringUtils.isNotBlank(corepropPath)) {
            // 
            // InputStream input = fs.open(new Path(corepropPath));
            // File coreprop = new File(new File(taskOutPath, "core"),
            // ConfigConstant.FILE_CORE_PROPERTIES);
            // OutputStream output = FileUtils.openOutputStream(coreprop);
            // IOUtils.copy(input, output);
            // IOUtils.closeQuietly(input);
            // IOUtils.closeQuietly(output);
            // context.setUserParam("corepropfile", coreprop.getAbsolutePath());
            // }
            String schemaPath = context.getUserParam(IndexBuildParam.INDEXING_SCHEMA_PATH);
            if (schemaPath == null) {
                logger.error("indexing.schemapath 参数没有配置");
                return new TaskReturn(TaskReturn.ReturnCode.FAILURE, IndexBuildParam.INDEXING_SCHEMA_PATH + " 参数没有配置");
            }
            String solrConfigPath = context.getUserParam(IndexBuildParam.INDEXING_SOLRCONFIG_PATH);
            if (solrConfigPath == null) {
                logger.error(IndexBuildParam.INDEXING_SOLRCONFIG_PATH + " 参数没有配置");
                return new TaskReturn(TaskReturn.ReturnCode.FAILURE, IndexBuildParam.INDEXING_SOLRCONFIG_PATH + " 参数没有配置");
            }
            try {
                // File dstP =
                copyRemoteFile2Local(new PathStrategy() {

                    @Override
                    public String getRemotePath() {
                        return schemaPath;
                    }

                    @Override
                    public File getLocalDestFile() {
                        return getLocalTmpSchemaFile();
                    }
                });
                copyRemoteFile2Local(new PathStrategy() {

                    @Override
                    public String getRemotePath() {
                        return solrConfigPath;
                    }

                    @Override
                    public File getLocalDestFile() {
                        return getLocalTmpSolrConfigFile();
                    }
                });
            // logger.warn(indexConf.getCoreName() + " get schema to local
            // :" + dstP + " done!");
            // if (configPath != null) {
            // srcPath = new Path(configPath);
            // dstP = new File(taskOutPath, "config");
            // if (dstP.exists()) {
            // dstP.delete();
            // }
            // dstP.mkdirs();
            // 
            // dstPath = new Path(dstP.getAbsolutePath());
            // 
            // fs.copyToLocalFile(srcPath, dstPath);
            // logger.warn("[taskid:" + taskid + "]" +
            // indexConf.getCoreName()
            // + " get config done!");
            // String normalizePath = configPath.replaceAll("\\\\", "/");
            // String configFile = dstP.getAbsolutePath() + File.separator
            // + normalizePath.substring(normalizePath.lastIndexOf("/") +
            // 1);
            // context.setUserParam("configFile", configFile);
            // }
            } catch (IOException e) {
                // + e);
                return new TaskReturn(TaskReturn.ReturnCode.FAILURE, "get schema error:" + ExceptionUtils.getStackTrace(e));
            }
            return new TaskReturn(TaskReturn.ReturnCode.SUCCESS, "success");
        } catch (Throwable e) {
            // logger.error("[taskid:" + taskid + "]" + "get schema fail:", e);
            return new TaskReturn(TaskReturn.ReturnCode.FAILURE, "get schema fail:" + ExceptionUtils.getStackTrace(e));
        }
    }

    protected File copyRemoteFile2Local(PathStrategy pStrategy) throws IOException {
        Path remotePath = new Path(pStrategy.getRemotePath());
        // getLocalTmpSchemaFile();
        File dstP = pStrategy.getLocalDestFile();
        FileUtils.forceMkdirParent(dstP);
        Path dstPath = new Path(dstP.getParent());
        fs.copyToLocalFile(remotePath, dstPath);
        logger.info("remote:" + remotePath + " copy to local:" + dstP + " succsessful");
        return dstP;
    }

    private static interface PathStrategy {

        public String getRemotePath();

        public File getLocalDestFile();
    }

    public static File getLocalTmpSchemaFile() {
        return new File(getTmpConifgDir(), ConfigFileReader.FILE_SCHEMA.getFileName());
    }

    public static File getLocalTmpSolrConfigFile() {
        // return schemaFile;
        return new File(getTmpConifgDir(), ConfigFileReader.FILE_SOLOR.getFileName());
    }

    private static final File getTmpConifgDir() {
        return new File(Environment.PWD.$() + File.separator + YarnConfiguration.DEFAULT_CONTAINER_TEMP_DIR);
    }

    public static IndexConf getIndexConf(TaskContext context) {
        IndexConf indexConf;
        indexConf = new IndexConf(context);
        return indexConf;
    }

    // public static FileSystem createFileSystem(String hdfsHost) {
    // Configuration configuration = new Configuration();
    // FileSystem fileSys = null;
    // if (StringUtils.isEmpty(hdfsHost)) {
    // throw new IllegalStateException("hdfsHost can not be null");
    // }
    // try {
    // configuration.set("fs.default.name", hdfsHost);
    // // configuration.set("mapred.job.tracker",
    // // "10.232.36.131:9001");
    // // configuration.set("mapred.local.dir",
    // // "/home/yusen/hadoop/mapred/local");
    // // configuration.set("mapred.system.dir",
    // // "/home/yusen/hadoop/tmp/mapred/system");
    // // configuration.setInt("dump.split.size", 2);
    // //
    // configuration.addResource("core-site.xml");
    // configuration.addResource("mapred-site.xml");
    // 
    // fileSys = FileSystem.get(configuration);//
    // FileSystem.newInstance(configuration);
    // 
    // } catch (Exception e) {
    // throw new RuntimeException(e);
    // }
    // return fileSys;
    // }
    public static void main(String[] args) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        System.out.println(sdf.format(new Date(System.currentTimeMillis())));
    }
    /*
	 * @Override public void killTask() { // TODO Auto-generated method stub
	 * interruptFlag.notifyAll(); interruptFlag.flag = InterruptFlag.Flag.KILL;
	 * }
	 */
}
