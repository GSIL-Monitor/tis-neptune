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
package com.qlangtech.tis.indexbuilder.doc;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.Term;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.schema.IndexSchema;
import org.apache.solr.schema.SchemaField;
import org.apache.solr.update.DocumentBuilder;
import org.apache.solr.update.VersionInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.qlangtech.tis.build.yarn.BuildNodeMaster;
import com.qlangtech.tis.hdfs.dump.index.stream.UIDTokenStream;
import com.qlangtech.tis.indexbuilder.exception.FieldException;
import com.qlangtech.tis.indexbuilder.exception.RowException;
import com.qlangtech.tis.indexbuilder.map.Counters;
import com.qlangtech.tis.indexbuilder.map.HdfsIndexBuilder;
import com.qlangtech.tis.indexbuilder.map.IndexConf;
import com.qlangtech.tis.indexbuilder.map.Messages;
import com.qlangtech.tis.indexbuilder.map.SuccessFlag;
import com.qlangtech.tis.indexbuilder.source.SourceReader;
import com.qlangtech.tis.indexbuilder.source.SourceReaderFactory;

/* *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class LuceneDocMaker implements Runnable {

    // public static final Logger logger =
    // LoggerFactory.getLogger(LuceneDocMaker.class);
    private static final Logger logger = LoggerFactory.getLogger("buildinfoLogger");

    protected static long startTime = 0L;

    private SourceReaderFactory readerFactory;

    // private InterruptFlag interruptFlag;
    // private LinkedList<Document> docPool;
    // private SimpleStack<Document> docPools;
    // private SimpleStack<Document> clearDocPool;
    BlockingQueue<Document> docPoolQueue;

    // BlockingQueue<SimpleStack<Document>> clearDocPoolQueue;
    private AtomicInteger aliveDocMakerCount;

    private final SuccessFlag successFlag = new SuccessFlag();

    private String name;

    private Counters counters;

    private Messages messages;

    // private DataProcessor dataprocessor;
    private SolrInputDocument solrDoc;

    private Map<String, SchemaField> fieldMap;

    // private float DEFAULT_DOCUMENT_BOOST;
    // private int docPoolSize;
    private IndexConf indexConf;

    private IndexSchema indexSchema;

    private final Set<String> schemaFields;

    // private boolean hashMutiValue;
    public static String BOOST_NAME = "!$boost";

    // int printInterval;
    // boolean filterDelete;
    private final long newVersion;

    private static final AtomicLong allDocPutQueueTime = new AtomicLong();

    private static final AtomicLong allDocPutCount = new AtomicLong();

    /**
     * 取得结果标记位
     *
     * @return
     */
    public SuccessFlag getResultFlag() {
        return this.successFlag;
    }

    public LuceneDocMaker(IndexConf indexConf, IndexSchema indexSchema, Messages messages, Counters counters, BlockingQueue<Document> docPoolQueue, // BlockingQueue<SimpleStack<Document>> clearDocPoolQueue,
    SourceReaderFactory readerFactory, AtomicInteger aliveDocMakerCount) {
        this.messages = messages;
        this.counters = counters;
        this.indexConf = indexConf;
        this.indexSchema = indexSchema;
        this.schemaFields = indexSchema.getFields().keySet();
        this.docPoolQueue = docPoolQueue;
        this.aliveDocMakerCount = aliveDocMakerCount;
        this.solrDoc = new SolrInputDocument();
        this.fieldMap = indexSchema.getFields();
        // this.DEFAULT_DOCUMENT_BOOST = indexConf.getDocBoost();
        this.readerFactory = readerFactory;
        // this.printInterval = indexConf.getPrintInterval();
        // this.filterDelete = indexConf.getBoolean("delete.filter", false);
        SimpleDateFormat dataFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        try {
            this.newVersion = Long.parseLong(dataFormat.format(dataFormat.parse(indexConf.getIncrTime())));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        init();
    }

    public void init() {
        for (SchemaField field : fieldMap.values()) {
            if (field.multiValued()) {
                // this.hashMutiValue = true;
                break;
            }
        }
    }

    private Document getLuceneDocument(Map<String, String> fieldValues) {
        // 重用SolrInputDocument
        // solrDoc.clear();
        String fieldKey = null;
        // if (fieldValues.containsKey(BOOST_NAME)) {
        // try {
        // solrDoc.setDocumentBoost(Float.parseFloat(fieldValues.get(BOOST_NAME)));
        // } catch (NumberFormatException nfe) {
        // logger.error("转化文档boost出错，将默认设为" + DEFAULT_DOCUMENT_BOOST, nfe);
        // solrDoc.setDocumentBoost(DEFAULT_DOCUMENT_BOOST);
        // }
        // // fieldValues.remove(BoostDataProcessor.BOOST_NAME);
        // } else {
        // solrDoc.setDocumentBoost(DEFAULT_DOCUMENT_BOOST);
        // }
        solrDoc.clear();
        // } else {
        for (Entry<String, String> entry : fieldValues.entrySet()) {
            // String value = fieldValues.get(name);
            // 只重置值不创建field，减少gc
            fieldKey = entry.getKey();
            if (!this.schemaFields.contains(fieldKey)) {
                continue;
            }
            if (entry.getValue() != null) {
                solrDoc.setField(entry.getKey(), entry.getValue());
            } else {
                solrDoc.removeField(entry.getKey());
            }
        }
        // }
        solrDoc.setField(VersionInfo.VERSION_FIELD, newVersion);
        return getLuceneDocument(solrDoc, indexSchema);
    }

    private Document getLuceneDocument(SolrInputDocument doc, IndexSchema schema) {
        try {
            return DocumentBuilder.toDocument(doc, schema);
        } catch (Exception e) {
            throw new RuntimeException(doc.toString(), e);
        }
    }

    public static final String TERM_VALUE = "_UID_";

    public static final Term UID_TERM = new Term("_ID", TERM_VALUE);

    public static void fillDocumentID(Document doc, long id) {
        TextField uidField = new TextField(UID_TERM.field(), new UIDTokenStream(id));
        // uidField.setOmitNorms(true);.
        doc.add(uidField);
    }

    @Override
    public void run() {
        BuildNodeMaster.setMdcAppName(this.indexConf.getCollectionName());
        try {
            doRun();
        } catch (Throwable e) {
            messages.addMessage(HdfsIndexBuilder.Message.ERROR_MSG, "doc maker fatal error:" + e.toString());
            logger.error("LuceneDocMaker ", e);
            successFlag.setFlag(SuccessFlag.Flag.FAILURE);
            successFlag.setMsg("doc maker fatal error:" + e.toString());
        } finally {
            aliveDocMakerCount.decrementAndGet();
        }
    }

    private void doRun() throws Exception {
        long startDocPushTimestamp;
        // int count = 0;
        // int successCount = 0;
        int failureCount = 0;
        int filteredCount = 0;
        // int successCountCore = 0;
        // boolean[] coreFull = new boolean[cores.length];
        // int index = 0;
        // docPool是否已经放到了queue中。
        Map<String, String> row = null;
        while (true) {
            /*
			 * File file = sfm.undealFileQueue.poll(1000,TimeUnit.MILLISECONDS);
			 * if(file==null) { if(sfm.isAllFileDownload()) break; else
			 * continue; } else {
			 */
            SourceReader recordReader = readerFactory.nextReader();
            if (recordReader == null) {
                successFlag.setFlag(SuccessFlag.Flag.SUCCESS);
                // counters.incrCounter(HdfsIndexBuilder.Counter.DOCMAKE_COMPLETE,
                // successCount - count);
                logger.warn(name + ":filtered:" + filteredCount);
                // 已处理完成
                return;
            }
            while (true) {
                try {
                    // row.clear();
                    row = recordReader.next();
                    if (row == null) {
                        break;
                    }
                    // 过滤掉删除的记录
                    // if (filterDelete) {
                    // String isDel = row.get("opt");
                    // if ("d".equals(isDel)) {
                    // filteredCount++;
                    // continue;
                    // } else {
                    // row.remove("opt");
                    // row.remove("delId");
                    // }
                    // }
                    /*
					 * if(dynamicFields!=null) { //动态字段 for(int
					 * i=0;i<dynamicFields.length;i++) { String dynamicField =
					 * dynamicFields[i]; String dynamicColumns =
					 * row.get(dynamicField);
					 * if(!StringUtils.isEmpty(dynamicColumns)) { String
					 * []fields = dynamicColumns.split(fieldSplitors[i]);
					 * for(String field:fields) { String []pairs =
					 * field.split(valueSplitors[i]);
					 * row.put(prefixs[i]+pairs[0], pairs[1]); }
					 * 
					 * //去掉源字段 //logger.warn("remove field:-"+dynamicField+"-");
					 * row.remove(dynamicField); } } }
					 */
                    // if (dataprocessor != null) {
                    // ResultCode ret = dataprocessor.process(row);
                    // // 过滤的记录i
                    // 
                    // if (!ret.isSuc()) {
                    // filteredCount++;
                    // continue;
                    // }
                    // }
                    Document doc = getLuceneDocument(row);
                    // if (indexConf.getRecordLimit() != 0) {
                    // if ((successCount++) % 10000 == 0) {
                    // // logger.warn("cores.length:" + cores.length);
                    // // for (int i = 0; i < cores.length; i++) {
                    // logger.warn("doc make count=" + successCountCore);
                    // // }
                    // }
                    // 
                    // if (successCount > indexConf.getRecordLimit()) {
                    // successFlag.setFlag(SuccessFlag.Flag.SUCCESS);
                    // return;
                    // }
                    // }
                    startDocPushTimestamp = System.currentTimeMillis();
                    docPoolQueue.put(doc);
                    counters.setCounter(HdfsIndexBuilder.Counter.DOCMAKE_COMPLETE, allDocPutCount.incrementAndGet());
                    counters.setCounter(HdfsIndexBuilder.Counter.DOCMAKE_QUEUE_PUT_TIME, allDocPutQueueTime.addAndGet(System.currentTimeMillis() - startDocPushTimestamp));
                // if (allDocPutCount.get() % 10000 == 0) {
                // logger.info("doc make count:" + allDocPutCount.get());
                // logger.info("allDocPutQueueTime:" +
                // allDocPutQueueTime.get());
                // }
                // if (!docPools.push(doc)) {
                // // 没有插入成功
                // /*
                // * if((docPoolQueue.size()==0||docPoolQueue.size()==
                // * indexConf.getDocQueueSize())&&successCount%1000==0) {
                // * logger.warn("docpoolqueuesize="+docPoolQueue.size());
                // * }
                // */
                // if (printCount++ % 100 == 0) {
                // logger.warn("docpoolqueuesize="
                // + docPoolQueue.size());
                // }
                // 
                // 
                // docPools[arrayIndex] = new SimpleStack<Document>(
                // this.docPoolSize);
                // docPools[arrayIndex].setIndex(arrayIndex);
                // docPools[arrayIndex].push(doc);
                // }
                // row.clear();
                // successCount++;
                /*
					 * if(successCount%1000 == 0) {
					 * logger.warn("docqueuesize="+docQueue.size()); }
					 */
                // if ((successCount & (printInterval - 1)) == 0) {
                // count += printInterval;
                // // logger.warn("docpoolqueuesize="+docPoolQueue.size());
                // counters.incrCounter(HdfsIndexBuilder.Counter.DOCMAKE_COMPLETE,
                // printInterval);
                // logger.warn(name + ":success:" + successCount +
                // "failure:" + failureCount);
                // }
                } catch (Throwable e) {
                    logger.error(e.getMessage(), e);
                    counters.incrCounter(HdfsIndexBuilder.Counter.DOCMAKE_FAIL, 1);
                    String errorMsg = "";
                    if (e instanceof RowException || e instanceof FieldException) {
                        errorMsg = e.toString();
                    }
                    messages.addMessage(HdfsIndexBuilder.Message.ERROR_MSG, "doc maker exception:" + e + errorMsg);
                    // logger.error("doc maker error,field:"+row,e);
                    if (++failureCount > indexConf.getMaxFailCount()) {
                        successFlag.setFlag(SuccessFlag.Flag.FAILURE);
                        successFlag.setMsg("LuceneDocMaker error:failureCount>" + indexConf.getMaxFailCount());
                        // error:failureCount>"+indexConf.getMaxFailCount());
                        return;
                    }
                }
            /*
					 * catch (Error e) { logger.error("LuceneDocMaker error:" +
					 * e); e.printStackTrace();
					 * counters.incrCounter(HdfsIndexBuilder.Counter.
					 * DOCMAKE_FAIL, 1); //
					 * messages.addMessage(HdfsIndexBuilder.Message.ERROR_MSG,
					 * // "doc maker error:"+e); //
					 * messages.addMessage(HdfsIndexBuilder.Message.ERROR_MSG,
					 * // "doc maker error,row:"+row);
					 * successFlag.setFlag(SuccessFlag.Flag.FAILURE);
					 * successFlag.setMsg("doc maker error:" + e); return; }
					 */
            }
        // file.delete();
        /*
			 * else {
			 */
        // return ;
        // }
        }
    // 把最后一个docPool放到queue中
    // for (int i = 0; i < cores.length; i++) {
    // docPoolQueue.put(docPools);
    // }
    // successFlag.setFlag(SuccessFlag.Flag.SUCCESS);
    }

    // public SuccessFlag getSuccessFlag() {
    // return successFlag;
    // }
    // 
    // public void setSuccessFlag(SuccessFlag successFlag) {
    // this.successFlag = successFlag;
    // }
    public String getName() {
        return name;
    }

    // public DataProcessor getDataprocessor() {
    // return dataprocessor;
    // }
    // 
    // public void setDataprocessor(DataProcessor dataprocessor) {
    // this.dataprocessor = dataprocessor;
    // }
    public void setName(String name) {
        this.name = name;
    }

    // private int getDocIndex(long routeValue) {
    // return (int) ((routeValue % groupCount) / oldGroupCount);
    // }
    /*
	 * private String getMultiValueSplitor(String fieldName) {
	 * if(multiValueFields==null) return null; for(int
	 * i=0;i<multiValueFields.length;i++) { String field = multiValueFields[i];
	 * if(field.equals(fieldName)) { return multiValueSplitors[i]; } } return
	 * null; }
	 */
    public static void main(String[] args) {
        System.out.println(809057284 % 128);
        String dFields = "numeric_field_data_value~;~:~_double_field_,date_field_data_value~;~:~_int_field_,text_field_data_value~;~:~_text_field_";
        String[] prefixs;
        String[] dynamicFields;
        String[] fieldSplitors;
        String[] valueSplitors;
        // String dFields = indexConf.get("indexing.dynamicFields");
        if (dFields != null) {
            String[] fields = dFields.split(",");
            prefixs = new String[fields.length];
            dynamicFields = new String[fields.length];
            fieldSplitors = new String[fields.length];
            valueSplitors = new String[fields.length];
            for (int i = 0; i < fields.length; i++) {
                String field = fields[i];
                String[] pair = field.split("~");
                dynamicFields[i] = pair[0];
                fieldSplitors[i] = pair[1];
                valueSplitors[i] = pair[2];
                prefixs[i] = pair[3];
            }
            int oi = 0;
        }
    /*
		 * String pairs[] =
		 * "pay_date_item_ids~:;pay_date_new_and_old_buyer~:".split(";");
		 * for(String pair:pairs) { System.out.println(pair); String []p =
		 * pair.split("~"); System.out.println(p.length); for(String p1:p) {
		 * System.out.println(p1); } }
		 */
    }
}
