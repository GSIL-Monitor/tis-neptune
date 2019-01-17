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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.store.RAMDirectory;
import org.apache.solr.core.SolrConfig;
import org.apache.solr.core.SolrResourceLoader;
import org.apache.solr.schema.IndexSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import com.qlangtech.tis.indexbuilder.doc.LuceneDocMaker;
import com.qlangtech.tis.indexbuilder.index.IndexMaker;
import com.qlangtech.tis.indexbuilder.index.IndexMerger;
import com.qlangtech.tis.indexbuilder.map.SuccessFlag.Flag;
import com.qlangtech.tis.indexbuilder.merger.IndexMergerImpl;
import com.qlangtech.tis.indexbuilder.source.impl.HDFSReaderFactory;
import com.qlangtech.tis.indexbuilder.utils.Context;
import com.qlangtech.tis.manage.common.IndexBuildParam;

/*
 * HdfsIndexBuilder 索引入口map类
 *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class HdfsIndexBuilder {

    public static final Logger logger = LoggerFactory.getLogger(HdfsIndexBuilder.class);

    private HDFSReaderFactory readerFactory = null;

    // FileSystem fs;
    // ExecutorCompletionService
    private final CompletionService<SuccessFlag> executorService = new ExecutorCompletionService<SuccessFlag>(Executors.newCachedThreadPool(new ThreadFactory() {

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r);
            return t;
        }
    }));

    // IndexMerger indexMerger;
    InterruptFlag interruptFlag = new InterruptFlag();

    long startTime;

    // 由consel传入的taskid
    private String taskid = "";

    private Integer allRowCount;

    public enum Counter {

        // 每一万条记录一次
        DOCMAKE_COMPLETE,
        // DocMaker向Indexmaker阻塞队列压累积时间
        DOCMAKE_QUEUE_PUT_TIME,
        INDEXMAKE_COMPLETE,
        MERGE_COMPLETE,
        DOCMAKE_FAIL,
        INDEXMAKE_FAIL
    }

    public enum Message {

        BEGIN_OPTIMIZE,
        INDEX_TIME,
        OPTIMIZE_TIME,
        ERROR_MSG,
        ALL_TIME,
        WAIT_OPTIMIZE
    }

    public HdfsIndexBuilder() throws IOException {
        startTime = System.currentTimeMillis();
    // getAllFileName();
    // indexSchema = new IndexSchema(new
    // SolrResourceLoader("",IndexConf.class.getClassLoader() ,null),
    // indexConf.getSchemaName(), null);
    }

    public int getBuildAllRowCount() {
        if (this.allRowCount == null) {
            return Integer.MAX_VALUE;
        }
        return this.allRowCount;
    }

    private Future<SuccessFlag> mergeExecResult;

    /**
     * 索引merge是否完成
     *
     * @return
     */
    public boolean getMergeOver() {
        if (mergeExecResult == null) {
            return false;
        }
        return mergeExecResult.isDone();
    }

    public TaskReturn map(TaskContext context) throws Exception {
        IndexConf indexConf = HdfsIndexGetConfig.getIndexConf(context);
        Counters counters = context.getCounters();
        Messages messages = context.getMessages();
        // try {
        String taskAttemptId = context.getInnerParam("task.map.task.id");
        String rowCount = context.getUserParam(IndexBuildParam.INDEXING_ROW_COUNT);
        if (StringUtils.isNotBlank(rowCount)) {
            this.allRowCount = Integer.parseInt(rowCount);
        }
        // String configFile = context.getUserParam("configFile");
        taskid = context.getUserParam("indexing.taskid");
        // logger.warn("[taskid:" + taskid + "]" + "configFile=" +
        // configFile);
        logger.warn("indexMaker.flushCountThreshold:" + indexConf.getFlushCountThreshold() + ",indexMaker.flushSizeThreshold:" + indexConf.getFlushSizeThreshold());
        // ApplicationConfig appConfig = null;
        // String fieldSequence = null;
        // 生成的index保存在磁盘上的路径
        // 根据hdfs上的schema文件构造IndexSchema
        // String schemaPath = context.getUserParam("indexing.schemapath");
        // 
        // String normalizePath = schemaPath.replaceAll("\\\\", "/");
        // String schemaFileName =
        // normalizePath.substring(normalizePath.lastIndexOf("/"));
        // File schemaFile = new File(context.getMapPath() + File.separator
        // + "schema"
        // + File.separator + schemaFileName);
        final IndexSchema indexSchema = getIndexSchema(indexConf);
        String[] schemaFields = indexSchema.getFields().keySet().toArray(new String[0]);
        // 开始构建索引。。。
        logger.warn("[taskid:" + taskid + "]" + indexConf.getCoreName() + " indexing start......");
        // List<SuccessFlag> threadList = new
        // ArrayList<SuccessFlag>();
        /*
		 * //建索引线程数 float indexMakerCount =
		 * indexConf.getIndexMakerThreadCount(); //构造document线程数 float
		 * docMakerCount = indexConf.getDocMakerThreadCount();
		 */
        int docMakerCount = indexConf.getDocMakerThreadCount();
        int indexMakerCount = indexConf.getIndexMakerThreadCount();
        // int indexMakerCount = 5;
        // int docMakerCount = 6;
        logger.warn("----indexMakerCount=" + indexMakerCount);
        logger.warn("----docMakerCount=" + docMakerCount);
        AtomicInteger aliveIndexMakerCount = new AtomicInteger(indexMakerCount);
        AtomicInteger aliveDocMakerCount = new AtomicInteger(docMakerCount);
        // 存放document对象池的队列
        final BlockingQueue<Document> docPoolQueues = new ArrayBlockingQueue<Document>(indexConf.getDocQueueSize());
        logger.info("RamDirQueueSize:" + indexConf.getRamDirQueueSize());
        final BlockingQueue<RAMDirectory> dirQueue = new ArrayBlockingQueue<RAMDirectory>(indexConf.getRamDirQueueSize());
        if (indexConf.getSourceReaderFactory() == null) {
            logger.error("[taskid:" + taskid + "]" + "配置错误：indexing.sourcetype参数没有配置");
            throw new Exception("配置错误：indexing.sourcetype参数没有配置");
        }
        this.readerFactory = new HDFSReaderFactory();
        this.readerFactory.setIndexSchema(indexSchema);
        Context readerContext = new Context();
        readerContext.put("schemaFields", schemaFields);
        readerContext.put("indexconf", indexConf);
        readerContext.put("taskcontext", context);
        setDumpFileTitles(context, readerContext);
        logger.info("----------readerContext:" + readerContext.toString());
        readerFactory.init(readerContext);
        List<SuccessFlag> resultFlagSet = new ArrayList<SuccessFlag>();
        // 多线程构建document
        for (int i = 0; i < docMakerCount; i++) {
            /*
			 * SourceRecordReader srreader = new HdfsSourceRecordReader(fs,
			 * splitQueues, fieldSequence, indexConf.getDelimiter());
			 */
            String docMakterCountName = indexConf.getCoreName() + "-docMaker-" + docMakerCount + "-" + i;
            LuceneDocMaker luceneDocMaker = new LuceneDocMaker(indexConf, indexSchema, messages, counters, docPoolQueues, // clearDocPoolQueues,
            readerFactory, aliveDocMakerCount);
            initialDataprocess(luceneDocMaker);
            // luceneDocMaker.setConfigFile(configFile);
            luceneDocMaker.setName(docMakterCountName);
            resultFlagSet.add(luceneDocMaker.getResultFlag());
            executorService.submit(luceneDocMaker, luceneDocMaker.getResultFlag());
        // threadList.add(luceneDocMaker.getSuccessFlag());
        // Thread t = new Thread(luceneDocMaker);
        // if (loader != null && isTaskFromTis() // && getJarStatus()
        // ) {
        // logger.warn("loader is not null and is task from tis");
        // t.setContextClassLoader(loader);
        // }
        // t.setName(luceneDocMaker.getName());
        // es.execute(t);
        // t.start();
        }
        this.mergeExecResult = waitIndexMergeTask(indexConf, aliveIndexMakerCount, counters, messages, dirQueue, indexSchema);
        // 多线程构建索引
        for (int i = 0; i < indexMakerCount; i++) {
            IndexMaker indexMaker = new IndexMaker(indexConf, indexSchema, messages, counters, dirQueue, docPoolQueues, aliveDocMakerCount, aliveIndexMakerCount);
            final String indexMakerName = indexConf.getCoreName() + "-indexMaker-" + indexMakerCount + "-" + i;
            indexMaker.setName(indexMakerName);
            resultFlagSet.add(indexMaker.getResultFlag());
            this.executorService.submit(indexMaker, indexMaker.getResultFlag());
        }
        try {
            for (int threadCount = 0; threadCount < resultFlagSet.size(); threadCount++) {
                this.executorService.take().get();
            }
        // 之前的任何一个進程出錯都會短路
        } catch (Exception e) {
            logger.warn(indexConf.getCoreName() + " dump fail!!");
            logger.error("[taskid:" + taskid + "]" + "dump fail!!" + e.getMessage());
            messages.addMessage(HdfsIndexBuilder.Message.ERROR_MSG, e.getMessage());
            return new TaskReturn(TaskReturn.ReturnCode.FAILURE, e.getMessage());
        }
        // 检查docmake 和index make 是否出错，出错的话要终止此次流程继续执行
        for (SuccessFlag f : resultFlagSet) {
            if (f.getFlag() == SuccessFlag.Flag.FAILURE) {
                return new TaskReturn(TaskReturn.ReturnCode.FAILURE, f.getMsg());
            }
        }
        SuccessFlag mergeThread = mergeExecResult.get();
        // 检查各个线程的运行状态
        if (mergeThread.getFlag() == SuccessFlag.Flag.SUCCESS) {
            logger.warn("[taskid:" + taskid + "]" + indexConf.getCoreName() + " dump done!!");
            logger.warn("[taskid:" + taskid + "]" + "indexing done,take " + (System.currentTimeMillis() + 1 - startTime) / (1000 * 60) + " minutes!");
            messages.addMessage(HdfsIndexBuilder.Message.ALL_TIME, (System.currentTimeMillis() - startTime) / 1000 + " seconds");
            return new TaskReturn(TaskReturn.ReturnCode.SUCCESS, "success");
        }
        // else if (flag.getFlag() == SuccessFlag.Flag.FAILURE) {
        logger.warn(indexConf.getCoreName() + " dump fail!!");
        logger.error("[taskid:" + taskid + "]" + "dump fail!!" + mergeThread.getMsg());
        messages.addMessage(HdfsIndexBuilder.Message.ERROR_MSG, mergeThread.getMsg());
        return new TaskReturn(TaskReturn.ReturnCode.FAILURE, mergeThread.getMsg());
    // }
    // } catch (Throwable e1) {
    // 
    // logger.error("[taskid:" + taskid + "]" + "build error:", e1);
    // 
    // context.getMessages().addMessage(HdfsIndexBuilder.Message.ERROR_MSG,
    // e1.toString());
    // 
    // TaskReturn tr = new TaskReturn(TaskReturn.ReturnCode.FAILURE,
    // e1.toString());
    // return tr;
    // }
    }

    /**
     * 取得schema对象
     *
     * @param indexConf
     * @return
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws SAXException
     */
    private IndexSchema getIndexSchema(IndexConf indexConf) throws IOException, ParserConfigurationException, SAXException {
        IndexSchema indexSchema = null;
        InputStream inputStream = null;
        InputStream schemaInputStream = null;
        try {
            File schemaFile = HdfsIndexGetConfig.getLocalTmpSchemaFile();
            logger.warn(" local schema path ==>" + schemaFile.getAbsolutePath());
            // new
            schemaInputStream = FileUtils.openInputStream(schemaFile);
            inputStream = FileUtils.openInputStream(HdfsIndexGetConfig.getLocalTmpSolrConfigFile());
            // inputStream = this.getClass().getClassLoader()
            // .getResourceAsStream("solr-config-template.xml");
            SolrConfig solrConfig = new SolrConfig(createSolrResourceLoader(), "solrconfig", new InputSource(inputStream));
            indexSchema = new IndexSchema(solrConfig, indexConf.getSchemaName(), new InputSource(schemaInputStream));
            return indexSchema;
        } finally {
            IOUtils.closeQuietly(schemaInputStream);
            IOUtils.closeQuietly(inputStream);
        }
    }

    /**
     * @return
     */
    protected SolrResourceLoader createSolrResourceLoader() {
        return new SolrResourceLoader(null) {

            @Override
            public InputStream openResource(String resource) throws IOException {
                // 希望在服务端不需要校验schema的正确性
                if (StringUtils.equals("dtd/solrschema.dtd", resource)) {
                    return new ByteArrayInputStream(new byte[0]);
                }
                return super.openResource(resource);
            }
        };
    }

    /**
     * baisui
     *
     * @param context
     * @param readerContext
     * @throws IOException
     */
    protected void setDumpFileTitles(TaskContext context, Context readerContext) throws IOException {
        final String buildtabletitleitems = context.getUserParam(IndexBuildParam.INDEXING_BUILD_TABLE_TITLE_ITEMS);
        if (StringUtils.isBlank(buildtabletitleitems)) {
            throw new IllegalStateException(" indexing.buildtabletitleitems shall be set in user param ");
        }
        logger.info("indexing.buildtabletitleitems:" + buildtabletitleitems);
        readerContext.put("titletext", StringUtils.split(buildtabletitleitems, ","));
    }

    private Future<SuccessFlag> waitIndexMergeTask(final IndexConf indexConf, AtomicInteger aliveIndexMakerCount, Counters counters, Messages messages, BlockingQueue<RAMDirectory> dirQueue, IndexSchema schema) throws Exception {
        if (schema == null) {
            throw new IllegalArgumentException("schema can not be null");
        }
        // final ClassLoader currentClassloader =
        // Thread.currentThread().getContextClassLoader();
        IndexMerger indexMerger = new IndexMergerImpl(schema);
        indexMerger.setAtomicInteger(aliveIndexMakerCount);
        indexMerger.setCounters(counters);
        indexMerger.setMessages(messages);
        // indexMerger.setDiskDir(mergeIndexPath);
        indexMerger.setIndexConf(indexConf);
        // indexMerger.setMergerAllocator(mergerAllocator);
        indexMerger.setDirQueue(dirQueue);
        // indexMerger.setTaskAttemptId(taskAttemptId);
        indexMerger.setName(indexConf.getCoreName());
        // threadList.add(indexMerger.getSuccessFlag());
        // ExecutorService executor = Executors.newSingleThreadExecutor(new
        // ThreadFactory() {
        // public Thread newThread(Runnable r) {
        // Thread indexMergerThread = new Thread(r);
        // indexMergerThread.setName(indexConf.getCoreName());
        // indexMergerThread.setContextClassLoader(currentClassloader);
        // return indexMergerThread;
        // }
        // });
        Future<SuccessFlag> mergeExecResult = this.executorService.submit(indexMerger);
        // Future<SuccessFlag> mergeExecResult = executor.submit(indexMerger);
        return mergeExecResult;
    }

    /**
     * @param luceneDocMaker
     */
    private void initialDataprocess(LuceneDocMaker luceneDocMaker) {
    // luceneDocMaker.setDataprocessor(new DataProcessor() {
    // @Override
    // public String getDesc() {
    // return "desc";
    // }
    // 
    // @Override
    // public ResultCode process(Map<String, String> row)
    // throws DataProcessException {
    // return ResultCode.SUC;
    // }
    // });
    }

    /*
	 * public static int getThreadCount(float ratio) { OperatingSystemMXBean os
	 * = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
	 * int threadCount = (int)(os.getAvailableProcessors() * ratio); return
	 * threadCount>0?threadCount:1; }
	 */
    protected SuccessFlag checkSuccessFlag(List<SuccessFlag> threadList) {
        SuccessFlag flag = new SuccessFlag();
        flag.setFlag(Flag.SUCCESS);
        for (SuccessFlag sf : threadList) {
            if (sf.getFlag() == SuccessFlag.Flag.FAILURE) {
                return sf;
            }
            if (sf.getFlag() == SuccessFlag.Flag.RUNNING) {
                flag.setFlag(Flag.RUNNING);
            }
        }
        return flag;
    }

    public static void main(String[] args) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        System.out.println(sdf.format(new Date(System.currentTimeMillis())));
    }
    // private boolean downloadJar() throws Exception {
    // logger.info("downloadJar start");
    // // 使用HttpConfigFileReader 工具类取得终搜后台仓库中的配置文件
    // SnapshotDomain domain = HttpConfigFileReader.getResource(
    // indexConf.getRemoteJarHost(), indexConf.getServiceName(), 0,
    // indexConf.getRunEnvironment(), ConfigFileReader.FILE_JAR);
    // logger.info("appId=" + domain.getAppId());
    // UploadResource resource = domain.getJarFile();
    // if (resource != null) {
    // logger.info("upload_resource:ur_id=" + resource.getUrId());
    // FileOutputStream fos = null;
    // FileChannel fc = null;
    // try {
    // File jarPath = new File(indexConf.getServicejarpath());
    // if (!jarPath.exists()) {
    // jarPath.mkdirs();
    // }
    // File jarFile = new File(jarPath, indexConf.getServiceName()
    // + "." + "jar");
    // if (jarFile.exists()) {
    // jarFile.delete();
    // }
    // jarFile.createNewFile();
    // fos = new FileOutputStream(jarFile);
    // fc = fos.getChannel();
    // if (null == resource.getContent()) {
    // logger.error("resource.getContent() is null,ur_id="
    // + resource.getUrId());
    // }
    // ByteBuffer bb = ByteBuffer.wrap(resource.getContent());
    // fc.write(bb);
    // } catch (Exception e) {
    // e.printStackTrace();
    // return false;
    // } finally {
    // if (fos != null)
    // fos.close();
    // if (fc != null)
    // fc.close();
    // }
    // return true;
    // } else {
    // logger.warn("no user jar file!");
    // }
    // return false;
    // }
    // private boolean downloadTisJar() throws Exception {
    // if (!getJarStatus()) {
    // return false;
    // }
    // StringBuffer ossKey = new StringBuffer();
    // ossKey.append(JarOssClient.OSS_ROOT_KEY)
    // .append(indexConf.getRunEnvironment().getKeyName()).append("_")
    // .append(indexConf.getServiceName());
    // StringBuffer destPath = new StringBuffer();
    // destPath.append(indexConf.getServicejarpath()).append(File.separator)
    // .append(indexConf.getServiceName()).append(".jar");
    // 
    // logger.info("downloadTisJar start");
    // 
    // try {
    // File jarPath = new File(indexConf.getServicejarpath());
    // if (!jarPath.exists()) {
    // jarPath.mkdirs();
    // }
    // File jarFile = new File(jarPath, indexConf.getServiceName() + "."
    // + "jar");
    // if (jarFile.exists()) {
    // jarFile.delete();
    // }
    // jarOssClient.download(ossKey.toString(), destPath.toString());
    // logger.info("ossKey : " + ossKey + " ---- destPath : " + destPath);
    // 
    // } catch (Exception e) {
    // e.printStackTrace();
    // return false;
    // }
    // return true;
    // 
    // }
    // private boolean getJarStatus() {
    // try {
    // StringBuffer ossKey = new StringBuffer();
    // ossKey.append(JarOssClient.OSS_ROOT_KEY)
    // .append(indexConf.getRunEnvironment().getKeyName())
    // .append("_").append(indexConf.getServiceName());
    // StringBuffer ossStatus = new StringBuffer();
    // ossStatus.append(ossKey).append("_status");
    // if (!jarOssClient.getJarStatus(ossStatus.toString())) {
    // return false;
    // }
    // } catch (Exception e) {
    // return false;
    // }
    // return true;
    // }
}
