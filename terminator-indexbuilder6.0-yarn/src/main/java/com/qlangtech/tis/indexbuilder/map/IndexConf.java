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

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.qlangtech.tis.manage.common.IndexBuildParam;

/* *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class IndexConf {

    public static final Logger logger = LoggerFactory.getLogger(IndexConf.class);

    private final TaskContext context;

    public IndexConf(TaskContext context) {
        this.context = context;
    }

    // public String getBucketname() {
    // return get("indexing.bucketname");
    // }
    // public String getFileSystemType() {
    // return get("indexing.fileSystemType", "hdfs");
    // }
    /*
	 * public int getMaxFileCount() { return
	 * getInt("indexing.maxlocalfilecount",10); }
	 */
    public String getFsName() {
        return getSourceFsName();
    }

    public String getSourceFsName() {
        return get(IndexBuildParam.INDEXING_SOURCE_FS_NAME);
    }

    public String getOutputPath() {
        return this.get(IndexBuildParam.INDEXING_OUTPUT_PATH);
    }

    // example:search4OperationStatistic
    public String getCollectionName() {
        String collectionName = this.get(IndexBuildParam.INDEXING_SERVICE_NAME);
        if (StringUtils.isBlank(collectionName)) {
            throw new IllegalStateException("collection name:" + collectionName + " can not be null");
        }
        return collectionName;
    }

    private String get(String key) {
        return this.context.getUserParam(key);
    }

    // public String getWriteFileUser() {
    // return get("terminator.write.file.user");
    // }
    public String getSchemaName() {
        return "schema.xml";
    }

    // public MergeMode getMergeMode() {
    // return MergeMode.valueOf(get("indexing.mergemode",
    // "serial").toUpperCase());
    // }
    public String getSourcePath() {
        return get(IndexBuildParam.INDEXING_SOURCE_PATH);
    }

    // public int getHdfsReaderBufferSize() {
    // return getInt("indexing.hdfsreaderbuffersize", 1024);
    // }
    public int getFlushCountThreshold() {
        return 90000000;
    }

    public long getFlushSizeThreshold() {
        // return getLong("indexing.flushSizeThreshold", 50000000);
        return 100 * 1024 * 1024;
    }

    /**
     * docMaker 到 indexMaker之间阻塞隊列長度
     *
     * @return
     */
    public int getDocQueueSize() {
        return 200;
    // return getInt("indexing.docqueuesize", 2000);
    }

    // public int getDocPoolSize() {
    // return getInt("indexing.docpoolsize", 1000);
    // }
    // public float getDocBoost() {
    // return getFloat("indexing.docboost", 1.0f);
    // }
    public int getMinSplitSize() {
        // getInt("indexing.minsplitsize", );
        return 128 * 1024 * 1024;
    }

    // public String getServiceName() {
    // return get("indexing.servicename");
    // }
    public String getCoreName() {
        return get(IndexBuildParam.INDEXING_CORE_NAME);
    }

    public int getDocMakerThreadCount() {
        // return count;
        return 2;
    }

    public int getIndexMakerThreadCount() {
        // return count;
        return 2;
    }

    // public float getDocMakerThreadRatio() {
    // return getFloat("indexing.docmakerthreadratio", 0.5f);
    // }
    // public float getIndexMakerThreadRatio() {
    // return getFloat("indexing.indexmakerthreadratio", 0.5f);
    // }
    public int getRamDirQueueSize() {
        return 2;
    // return getInt("indexing.ramdirqueuesize", 1);
    }

    // public int getMaxMergeThreadCount() {
    // return getInt("indexing.maxmergethreadcount", 3);
    // }
    // 
    // public int getMaxMergeCount() {
    // return getInt("indexing.maxmergecount", 5);
    // }
    // 
    // public int getMaxNumSegments() {
    // return getInt(// "indexing.maxNumSegments"
    // IndexBuildParam.INDEXING_MAX_NUM_SEGMENTS, 1);
    // }
    // 
    // public int getFileQueueSize() {
    // return getInt("indexing.filequeuesize", 10);
    // }
    // 
    public int getMaxFailCount() {
        // return getInt("indexing.maxfailcount", 100);
        return 20;
    }

    // 
    // public int getGroupNum() {
    // return getInt(IndexBuildParam.INDEXING_GROUP_NUM,
    // Integer.parseInt(get(IndexBuildParam.INDEXING_CORE_NAME, "-0").substring(
    // get(IndexBuildParam.INDEXING_CORE_NAME, "-0").lastIndexOf("-") + 1)));
    // }
    // 
    // public int getOldGroupNum() {
    // return getInt("indexing.oldgroupnum", 0);
    // }
    // 
    // public String getFileSplitor() {
    // return get( IndexBuildParam.INDEXING_DELIMITER
    // //"indexing.filesplitor"
    // );
    // }
    // 
    // public String getIndexUserName() {
    // return get(// "indexing.username"
    // IndexBuildParam.INDEXING_USER_NAME);
    // }
    // 
    // public String getDelimiter() {
    // return get(IndexBuildParam.INDEXING_DELIMITER, "\t");
    // }
    // 
    // public String getMakerMergePolicy() {
    // return get("indexing.maker.mergePolicy", "TieredMergePolicy");
    // }
    // 
    // public int getMakerMergeFacotr() {
    // return getInt("indexing.maker.mergefactor", 10000000);
    // }
    // 
    // public int getMergeFacotr() {
    // return getInt("indexing.mergefactor", 10);
    // }
    // 
    // public String getMergePolicy() {
    // return get("indexing.mergePolicy", "TieredMergePolicy");
    // }
    // 
    // public boolean getUseCompoundFile() {
    // return getBoolean("indexing.UseCompoundFile", true);
    // }
    // 
    // public int getSegmentsPerTier() {
    // return getInt("indexing.SegmentsPerTier", 10000);
    // }
    // 
    // public int getMaxMergeAtOnce() {
    // return getInt("indexing.MaxMergeAtOnce", 10000);
    // }
    // 
    // public boolean isRamOptimize() {
    // return getBoolean("indexing.ramoptimize", true);
    // }
    // 
    // public int getMakerRAMBufferSizeMB() {
    // return getInt("indexing.maker.RAMBufferSizeMB", 100);
    // }
    // 
    // public boolean getMakerUseCompoundFile() {
    // return getBoolean("indexing.maker.UseCompoundFile", true);
    // }
    // 
    // /*
    // * public boolean isYunti() { return getBoolean("indexing.isyunti",false);
    // }
    // */
    // public int getMakerSegmentsPerTier() {
    // return getInt("indexing.maker.SegmentsPerTier", 10000);
    // }
    // 
    // public int getMakerMaxMergeAtOnce() {
    // return getInt("indexing.maker.MaxMergeAtOnce", 10000);
    // }
    // 
    // public String getLockFilePath() {
    // return get("indexing.merger.lockfilepath",
    // "/home/admin/terminator_builder/temp/lock");
    // }
    // 
    public long getOptimizeSizeThreshold() {
        // return getLong("indexing.optimze.optimizeSizeThreshold", );
        return 500 * 1024 * 1024;
    }

    // 
    // /**
    // * @return
    // */
    // public int getOptimizeNumThreshold() {
    // 
    // return getInt("indexing.optimze.optimizeNumThreshold", 10000000);
    // }
    // 
    // public int getFlushCheckInterval() {
    // return getInt("indexing.flushcheckinterval", 32768);
    // }
    // 
    // public int getPrintInterval() {
    // return getInt("indexing.printinterval", 131072);
    // }
    // 
    // public void loadFrom(Context context) {
    // Map<String, String> paramMap = context.getUserParamMap();
    // for (Entry<String, String> en : paramMap.entrySet()) {
    // set(en.getKey(), en.getValue());
    // }
    // }
    // 
    // /**
    // * @return
    // */
    // public boolean isDownloadFile() {
    // return getBoolean("indexing.isdownloadfile", false);
    // }
    // 
    // // public String getGroupNums() {
    // // return get("indexing.groupnums", "0");//
    // //
    // ,"0;1;2;40;41;42;80;81;82;120;121;122;160;161;162;200;201;202;240;241;242;280;281;282|3;4;5;43;44;45;83;84;85;123;124;125;");
    // // }
    // 
    // public String getRouteKey() {
    // return get("indexing.routekey", "id");
    // }
    // 
    // public int getGroupCount() {
    // return getInt("indexing.groupcount", 1);
    // }
    // 
    // public int getOldGroupCount() {
    // return getInt("indexing.oldgroupcount", 1);
    // }
    // 
    public int getMergeThreads() {
        return 1;
    // return getInt("indexing.mergethreads", 2);
    }

    // 
    // public String getLocalOutputPath() {
    // return get("indexing.localoutputpath", "/home/admin/ecrmIndex");
    // }
    // 
    // /**
    // * @return
    // */
    // public String getTargetOkFilePath() {
    // return get("indexing.targetokfilepath", "/user/admin/" + getServiceName()
    // + "/all/okfile");
    // }
    // 
    // public int getIndexRamDirBufferSize() {
    // return getInt("indexing.indexramdirbuffersize", 1024);
    // }
    // 
    // public int getMergeRamDirBufferSize() {
    // return getInt("indexing.mergeramdirbuffersize", 10240);
    // }
    // 
    // public int getIndexSmallCacheSize() {
    // return getInt("indexing.indexsmallcachesize", 102400);
    // }
    // 
    // public int getIndexLargeCacheSize() {
    // return getInt("indexing.indexlargecachesize", 10240000);
    // }
    // 
    // public int getMergeSmallCacheSize() {
    // return getInt("indexing.mergesmallcachesize", 1024 * 2);
    // }
    // 
    // public int getMergeLargeCacheSize() {
    // return getInt("indexing.mergelargecachesize", getMergeRamDirBufferSize()
    // * 2 + 1);
    // }
    // 
    // public boolean isDirectRAM() {
    // return getBoolean("indexing.directram", false);
    // }
    // 
    // public boolean isStoreLocal() {
    // return getBoolean("indexing.isstorelocal", false);
    // }
    // 
    // public int getRecordLimit() {
    // return getInt(// "indexing.recordlimit"
    // IndexBuildParam.INDEXING_RECORD_LIMIT, 0);
    // }
    // 
    // // 兼容ecrm的独特的方式
    // public boolean isEcrm() {
    // return getBoolean("isecrm", false);
    // }
    // 
    // public String getIndexDate() {
    // 
    // // 检索节点不传indexing.date,只能从路径中截取建索引的日期，恶心死了
    // // /user/admin/search4realecrm/all/11/output/20140707131547
    // String date = null;
    // try {
    // String path = get(IndexBuildParam.INDEXING_OUTPUT_PATH);
    // if (path.endsWith("/")) {
    // path = path.substring(0, path.length() - 1);
    // }
    // path = path.substring(path.lastIndexOf("/") + 1, path.length() - 6);
    // date = path;
    // // return date;
    // } catch (Exception e) {
    // logger.warn("获取建索引日期失败！", e);
    // }
    // SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
    // if (get("indexing.okfileTimeZone") != null) {
    // format.setTimeZone(TimeZone.getTimeZone(get("indexing.okfileTimeZone")));
    // }
    // return get("indexing.date", date != null ? date : format.format(new
    // Date()));
    // }
    // 
    // public String[] getCores() {
    // String[] cores = new String[(int) (getGroupCount() /
    // getOldGroupCount())];
    // for (int i = 0; i < cores.length; i++) {
    // cores[i] = String.valueOf(getOldGroupCount() * i + getGroupNum());
    // }
    // return cores;
    // }
    // 
    // public String getOkFileName() {
    // return get("indexing.okfilename", "tsearcher.ok");
    // }
    // 
    // public int getPayloadThreadCount() {
    // return getInt("indexing.payloadthreadcount", 8);
    // }
    // 
    // public boolean getGenUid() {
    // return getBoolean("indexing.genuid", true);
    // }
    // 
    // public String getHdfsjarpath() {
    // return get("indexing.hdfsjarpath",
    // "/home/admin/terminator_builder/sharelib/tsearcherhdfs3.3");
    // }
    // 
    // /**
    // * @return
    // */
    // public String getYuntijarpath() {
    // 
    // return get("indexing.yuntijarpath",
    // "/home/admin/terminator_builder/sharelib/yuntihdfs");
    // }
    // 
    // public String getHbase094jarpath() {
    // 
    // return get("indexing.hbase094jarpath",
    // "/home/admin/terminator_builder/sharelib/hbase0.94");
    // }
    // 
    // public String getHbaseSite() {
    // 
    // return get("indexing.hbasesite",
    // "/home/admin/terminator_builder/sharelib/hbase0.94/hbase-site.xml");
    // }
    // 
    // public String getOdpsjarpath() {
    // 
    // return get("indexing.odpsjarpath",
    // "/home/admin/terminator_builder/sharelib/odps");
    // }
    // 
    // /**
    // * @return
    // */
    // public String getFileNameSplitor() {
    // 
    // return get("indexing.filenamesplitor", "-");
    // }
    // 
    // // hadoop-site.xml文件位置
    // public String getHadoopSite() {
    // return get("indexing.hadoopsite", "/home/admin/config/hadoop-site.xml");
    // }
    // 
    // public int getSplitGroupCount() {
    // return getInt("indexing.splitgroupcount", 0);
    // }
    // 
    // /**
    // * @return
    // */
    // public boolean isOptimzeRelease() {
    // 
    // return getBoolean("indexing.optimizerelease", false);
    // }
    // 
    // /**
    // * @return
    // */
    // public String getPayloadFields() {
    // 
    // return get("indexing.payloadfields");
    // }
    // 
    // /**
    // * @return
    // */
    // public boolean genRangeField() {
    // 
    // return getBoolean("indexing.genrangefield", true);
    // }
    // 
    // /**
    // * @return
    // */
    // public String getServicejarpath() {
    // 
    // return "/home/admin/terminator_builder/sharelib/userJar/" +
    // getServiceName();
    // }
    // 
    // /**
    // * @return
    // */
    // public RunEnvironment getRunEnvironment() {
    // String env = get("job.runEnvironment", "online");
    // if (env.equals("daily")) {
    // return RunEnvironment.DAILY;
    // } else if (env.equals("ready")) {
    // return RunEnvironment.READY;
    // } else if (env.equals("online")) {
    // return RunEnvironment.ONLINE;
    // } else {
    // return RunEnvironment.ONLINE;
    // }
    // }
    // 
    // public SourceType getSourceType() {
    // return getEnum(IndexBuildParam.INDEXING_SOURCE_TYPE, SourceType.UNKNOWN);
    // }
    // 
    // // /**
    // // * @return
    // // */
    // // public String getRemoteJarHost() {
    // // return get("indexing.remotejarhost",
    // // "http://terminator.admin.tbsite.net:9999");
    // // }
    // 
    // /**
    // * @return
    // */
    // public boolean isLoadJar() {
    // 
    // return getBoolean("indexing.isloadjar", true);
    // }
    // 
    /**
     * @return
     */
    public String getSourceReaderFactory() {
        // get("indexing.sourcereaderfactory","com.taobao.terminator.indexbuilder.source.HDFSReaderFactory");
        return "com.taobao.terminator.indexbuilder.source.impl.HDFSReaderFactory";
    // return get("indexing.sourcereaderfactory",
    // );
    // } else if (getSourceType().equals(SourceType.HBASE094)) {
    // return get("indexing.sourcereaderfactory",
    // "com.taobao.terminator.indexbuilder.source.HBaseReaderFactory");
    // } else if (getSourceType().equals(SourceType.ODPS)) {
    // return get("indexing.sourcereaderfactory",
    // "com.taobao.terminator.indexbuilder.source.OdpsReaderFactory");
    // } else {
    // return get("indexing.sourcereaderfactory");
    // }
    }

    // 
    // /**
    // * @return
    // */
    // public String getSouceReaderRactoryJarPath() {
    // 
    // if (getSourceType().equals(SourceType.YUNTI) ||
    // getSourceType().equals(SourceType.YUNTI2)
    // || getSourceType().equals(SourceType.YUNTI3)) {
    // return get("indexing.sourcereaderfactoryjarpath", getYuntijarpath());
    // } else if (getSourceType().equals(SourceType.HDFS)) {
    // return get("indexing.sourcereaderfactoryjarpath", getHdfsjarpath());
    // } else if (getSourceType().equals(SourceType.HBASE094)) {
    // return get("indexing.sourcereaderfactoryjarpath", getHbase094jarpath());
    // } else if (getSourceType().equals(SourceType.ODPS)) {
    // return get("indexing.sourcereaderfactoryjarpath", getOdpsjarpath());
    // } else {
    // return get("indexing.sourcereaderfactoryjarpath");
    // }
    // 
    // }
    // 
    // /**
    // * @return
    // */
    // public String getFileFormat() {
    // 
    // return get("indexing.fileformat", "text");
    // }
    // 
    // public String getHbaseTable() {
    // return get("indexing.hbasetable");
    // }
    // 
    // public int getStackSize() {
    // return getInt("indexing.stacksize", 1000);
    // }
    // 
    // /**
    // * @return
    // */
    // public String getRsAddr() {
    // 
    // return get("indexing.rsaddr");
    // }
    // 
    // /**
    // * @return
    // */
    // public String getAppKey() {
    // 
    // return get("indexing.appkey");
    // }
    // 
    // /**
    // * @return
    // */
    // public String getOdpsEndpoint() {
    // 
    // return get("indexing.odpsendpoint", "http://dt.odps.aliyun-inc.com");
    // }
    // 
    // /**
    // * @return
    // */
    // public String getOdpsAccessId() {
    // 
    // return get("indexing.odpsaccessid", "");
    // }
    // 
    // /**
    // * @return
    // */
    // public String getOdpsAccessKey() {
    // 
    // return get("indexing.odpsaccesskey", "");
    // }
    // 
    // /**
    // * @return
    // */
    // public String getOdpsProject() {
    // 
    // return get("indexing.odpsproject", "");
    // }
    // 
    // /**
    // * @return
    // */
    // public String getOdpsTable() {
    // 
    // return get("indexing.odpstable", "");
    // }
    // 
    // /**
    // * @return
    // */
    public String getIncrTime() {
        return get(IndexBuildParam.INDEXING_INCR_TIME);
    }
    // 
    // /**
    // * @return
    // */
    // public String getShardKey() {
    // 
    // return get("indexing.shardkey");
    // }
    // 
    // /**
    // * @return
    // */
    // public int getTotalGroups() {
    // 
    // return getInt("indexing.totalgroups", 1);
    // }
    // 
    // /**
    // * @return
    // */
    // public String getOdpsPartition() {
    // 
    // String partdesc = get("indexing.odpspartition", "");
    // partdesc = partdesc.trim();
    // int start = partdesc.indexOf("{");
    // int end = partdesc.indexOf("}");
    // if (start != -1 && end != -1) {
    // SimpleDateFormat sf = new SimpleDateFormat(partdesc.substring(start + 1,
    // end));
    // return partdesc.substring(0, start) + sf.format(new Date());
    // } else {
    // return partdesc;
    // }
    // }
    // 
    // public static void main(String[] args) {
    // 
    // SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
    // format.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
    // System.out.println(format.format(new Date()));
    // 
    // IndexConf conf = new IndexConf(false);
    // System.out.println(conf.get("ddd") == null);
    // 
    // String corename = "searchxdxx-9";
    // System.out.println(corename.substring(corename.lastIndexOf("-") + 1));
    // 
    // /*
    // * String partdesc = " date{yyyyMMdd}"; int start =
    // * partdesc.indexOf("{"); int end = partdesc.indexOf("}"); String p =
    // * ""; if(start!=-1 && end!=-1) { SimpleDateFormat sf=new
    // * SimpleDateFormat(partdesc.substring(start+1,end)); p=
    // * partdesc.substring(0, start)+sf.format(new Date()); } else { p=
    // * partdesc; } System.out.println(p);
    // */
    // }
    // 
    // /**
    // * @return
    // */
    // public int gethbaseScanCache() {
    // 
    // return getInt("indexing.hbasescancache", 1000);
    // }
}
