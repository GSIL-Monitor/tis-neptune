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
package com.qlangtech.tis.trigger.feedback;

/* *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class FeedbackService {
    // implements InitializingBean, IFeedbackService {
    // public static final Log logger = LogFactory.getLog(FeedbackService.class);
    // // .getLogger(FeedbackService.class);
    // private ITaskDAO taskDAO;
    // 
    // private final AsyncAppender asyncAppender;
    // 
    // 
    // 
    // public FeedbackService(ITSearchCache cache) throws Exception {
    // this.asyncAppender = new AsyncAppender();
    // this.asyncAppender.setBlocking(false);
    // this.asyncAppender.setBufferSize(5);
    // TerminatorHookAppender hookappender = new TerminatorHookAppender();
    // asyncAppender.addAppender(new ExecuteTaskLogFreshAppender(cache));
    // asyncAppender.addAppender(hookappender);
    // asyncAppender.addAppender(new HbaseExecuteLogAppender());
    // }
    // 
    // public static void addFeedbackInfo(String indexName, int taskid,
    // InfoType infoType, String info, IFeedbackService feedbackService,
    // ExecToken execSignal) {
    // if (taskid < 1) {
    // throw new IllegalArgumentException(
    // "taskid can not be null,indexName:" + indexName);
    // }
    // 
    // if (infoType == InfoType.INFO) {
    // logger.info(info);
    // }
    // if (infoType == InfoType.WARN) {
    // logger.warn(info);
    // }
    // if (infoType == InfoType.ERROR) {
    // logger.error(info);
    // }
    // 
    // try {
    // ExecuteState log = ExecuteState.create(infoType, info);
    // log.setTaskId(new Long(taskid));
    // log.setComponent(DumpOverNotifyClient.MESSAGE_COMPONENT);
    // log.setServiceName(indexName);
    // if (execSignal != null) {
    // log.setPhrase(execSignal.getPhrase());
    // log.setExecState(execSignal.isSuccess() ? "success" : "fail");
    // }
    // 
    // feedbackService.add(log);
    // } catch (Throwable e1) {
    // 
    // }
    // }
    // 
    // public static class ExecToken {
    // public static final Long EXEC_PHRASE_DUMP_DATA = 1l;
    // public static final Long EXEC_PHRASE_BUILD_INDEX = 2l;
    // public static final Long EXEC_PHRASE_CORE_SWAP = 3l;
    // public static final Long EXEC_PHRASE_CORE_PUBLISH_SERVICE = 4l;
    // private final Long phrase;
    // 
    // private final boolean success;
    // 
    // public ExecToken(Long phrase, boolean success) {
    // super();
    // this.phrase = phrase;
    // this.success = success;
    // }
    // 
    // public Long getPhrase() {
    // return phrase;
    // }
    // 
    // public boolean isSuccess() {
    // return success;
    // }
    // 
    // }
    // 
    // @Override
    // public void addFeedbackInfo(String indexName, int taskid,
    // InfoType infoType, String info) {
    // addFeedbackInfo(indexName, taskid, infoType, info, this, null);
    // }
    // 
    // @Override
    // public void addPhraseSuccess(String indexName, int taskid, String info,
    // Long phrase) {
    // addFeedbackInfo(indexName, taskid, InfoType.INFO, info, this,
    // new ExecToken(phrase, true));
    // 
    // }
    // 
    // @Override
    // public void addPhraseFaild(String indexName, int taskid, String info,
    // Throwable e, Long phrase) {
    // addPhraseFaild(indexName, taskid,
    // info + "\n" + ExceptionUtils.getStackTrace(e), phrase);
    // }
    // 
    // @Override
    // public void addPhraseFaild(String indexName, int taskid, String info,
    // Long phrase) {
    // addFeedbackInfo(indexName, taskid, InfoType.ERROR, info, this,
    // new ExecToken(phrase, false));
    // }
    // 
    // @Autowired
    // public void setTaskDAO(ITaskDAO taskDAO) {
    // this.taskDAO = taskDAO;
    // }
    // 
    // public ITaskDAO getTaskDAO() {
    // return taskDAO;
    // }
    // 
    // private class HbaseExecuteLogAppender extends AppenderSkeleton {
    // protected void append(LoggingEvent event) {
    // final ExecuteState log = (ExecuteState) event.getProperties().get(
    // "logstat");
    // 
    // if (log == null) {// || log.getTaskId() == null) {
    // return;
    // }
    // 
    // if (log.getTaskId() != null) {
    // 
    // // byte[] taskidBuffer = Bytes.toBytes(taskid);
    // // insertServiceLog(log);
    // // } else {
    // insert(log);
    // }
    // }
    // 
    // @Override
    // public void close() {
    // }
    // 
    // @Override
    // public boolean requiresLayout() {
    // 
    // return false;
    // }
    // 
    // }
    // 
    // /**
    // * @param log
    // */
    // // public void insertServiceLog(final ExecuteState log) {
    // // String serviceName;
    // // byte[] key;
    // // serviceName = StringUtils.substringAfter(log.getServiceName(),
    // // "search4");
    // // Integer level = log.getInfoType().getType();
    // // final long sequence = i++;
    // //
    // // // key = Bytes.add(
    // // // Bytes.add(Bytes.toBytes(serviceName),
    // // // Bytes.toBytes(log.getTime()), Bytes.toBytes(level)),
    // // // Bytes.toBytes(sequence));
    // // key = Bytes.toBytes(serviceName + log.getTime() + level + sequence);
    // // Put singlePut = new Put(key);
    // // insertLog(log, singlePut, sequence);
    // // }
    // 
    // @Override
    // public void add(final ExecuteState log) {
    // 
    // Map<String, Object> prop = new HashMap<String, Object>();
    // // prop.put("name", serviceName);
    // prop.put("logstat", log);
    // 
    // LoggingEvent event = new LoggingEvent(null,
    // Logger.getLogger(FeedbackService.class),
    // System.currentTimeMillis(), Level.INFO, log.getMsg(), Thread
    // .currentThread().getName(), null, null,
    // LocationInfo.NA_LOCATION_INFO, prop);
    // 
    // asyncAppender.append(event);
    // }
    // 
    // // private static final int MAX_ENTRIES = 30;
    // 
    // // private final LinkedHashMap<HdfsIndex, FSDataInputStream> fileSystemCache
    // // = new LinkedHashMap<HdfsIndex, FSDataInputStream>(
    // // MAX_ENTRIES, 0.75f, true) {
    // // private static final long serialVersionUID = 1L;
    // //
    // // @Override
    // // protected boolean removeEldestEntry(
    // // Entry<HdfsIndex, FSDataInputStream> eldest) {
    // // if (size() > MAX_ENTRIES) {
    // // IOUtils.closeQuietly(eldest.getValue());
    // // return true;
    // // }
    // //
    // // return false;
    // // }
    // //
    // // };
    // 
    // private static final int MAX_TABLE_COUNT = 10;
    // private static final String TAB_NAME = "terminator_tsearcher_servicefeedback_log";
    // //	private HBaseAdmin hAdmin;
    // //	private HTablePool pool;
    // 
    // @Override
    // public void afterPropertiesSet() throws Exception {
    // 
    // // this.fileSystem = new TSearcherFileSystem(Config.getHdfsHost());
    // // String confPath = "";
    // //Configuration conf = HBaseConfiguration.create();
    // // conf.set("zookeeper.znode.parent", "/hbase-perf");
    // //		String znodeParent = "true".equals(System.getProperty("daily")) ? "/hbase-perf"
    // //				: "/hbase-et2-ad";
    // 
    // //	conf.set("zookeeper.znode.parent", znodeParent);
    // //		conf.set("hbase.zookeeper.quorum", TSearcherConfigFetcher.get()
    // //				.getHbaseAddress());
    // 
    // //		conf.set("hbase.client.pause", "20");
    // //		conf.set("hbase.client.retries.number", "3");
    // //		conf.set("hbase.ipc.client.tcpnodelay", "true");
    // //		conf.set("ipc.ping.interval", "20000");
    // //		conf.set("hbase.rpc.timeout", "5000");
    // //
    // //		conf.set("hbase.client.scanner.caching", "150");
    // //		conf.set("hbase.ipc.client.connection.maxidletime", "86400000");
    // //
    // //		this.hAdmin = new HBaseAdmin(conf);
    // //		this.pool = new HTablePool(conf, MAX_TABLE_COUNT);
    // //
    // //		if (!hAdmin.tableExists(TAB_NAME)) {
    // //			throw new IllegalArgumentException("tab:" + TAB_NAME
    // //					+ " is not exist");
    // //		}
    // 
    // }
    // 
    // public static void main(String[] arg) throws Exception {
    // 
    // FeedbackService service = new FeedbackService(null);
    // service.afterPropertiesSet();
    // 
    // ExecuteState log = ExecuteState.create(InfoType.FATAL, "hello");
    // log.setServiceName("search4hello");
    // log.setTaskId(711l);
    // service.insert(log);
    // System.out.println("insert time:" + log.getTime());
    // System.out.println("insert seccess");
    // 
    // List<ExecuteLog> results = service.search("search4hello",
    // new Date(log.getTime()), 0);
    // 
    // for (ExecuteLog r : results) {
    // System.out.println(r.getAddress());
    // System.out.println(r.getMsg());
    // System.out.println(r.getComponent());
    // System.out.println(r.getTime().getTime());
    // System.out.println("-------------------------");
    // }
    // 
    // System.out.println("over:" + results.size());
    // // System.out.println("hello1234=========================");
    // // Configuration conf = HBaseConfiguration.create();
    // // conf.addResource(new Path(
    // // "/com/taobao/terminator/trigger/utils/hbase-site.xml"));
    // // HBaseAdmin hAdmin = new HBaseAdmin(conf);
    // // HTablePool pool = new HTablePool(conf, MAX_TABLE_COUNT);
    // //
    // // System.out.println("tableExists:" + hAdmin.tableExists(TAB_NAME));
    // }
    // 
    // //	private static final byte[] columnFamily = Bytes.toBytes("F");
    // //	private static final byte[] columnLevel = Bytes.toBytes("level");
    // //	private static final byte[] columnMsg = Bytes.toBytes("msg");
    // //	private static final byte[] columnTaskId = Bytes.toBytes("taskid");
    // //	private static final byte[] seqId = Bytes.toBytes("sequence");
    // //	private static final byte[] columnAddress = Bytes.toBytes("address");
    // //
    // //	private static final byte[] columnComponent = Bytes.toBytes("component");
    // //	private static final byte[] columnTime = Bytes.toBytes("occurtime");
    // 
    // //	private void insertLog(ExecuteState log, Put singlePut, final long sequence) {
    // //
    // //		Integer level = log.getInfoType().getType();
    // //		String msg = log.getMsg();
    // //
    // //		// ByteBuffer taskidBuffer = ByteBuffer.allocate(8);
    // //		// taskidBuffer.putLong(taskid);
    // //		// taskidBuffer.flip();
    // //		// ByteBuffer levelBuffer = ByteBuffer.allocate(4);
    // //		// levelBuffer.putInt(level);
    // //		// levelBuffer.flip();
    // //		HTable table = null;
    // //		try {
    // //			// long sequence = i++;
    // //
    // //			singlePut.add(columnFamily, columnLevel, Bytes.toBytes(level));
    // //			singlePut.add(columnFamily, columnMsg, Bytes.toBytes(msg));
    // //
    // //			if (log.getTaskId() != null) {
    // //				singlePut.add(columnFamily, columnTaskId,
    // //						Bytes.toBytes(log.getTaskId()));
    // //			}
    // //
    // //			singlePut.add(columnFamily, seqId, Bytes.toBytes(sequence));
    // //			if (log.getFrom() != null) {
    // //				singlePut.add(columnFamily, columnAddress,
    // //						Bytes.toBytes(log.getFrom().getHostAddress()));
    // //			}
    // //			if (log.getComponent() != null) {
    // //				singlePut.add(columnFamily, columnComponent,
    // //						Bytes.toBytes(log.getComponent()));
    // //			}
    // //
    // //			singlePut.add(columnFamily, columnTime,
    // //					Bytes.toBytes(log.getTime()));
    // //
    // //			table = (HTable) pool.getTable(TAB_NAME);
    // //			table.put(singlePut);
    // //
    // //		} catch (Exception e) {
    // //			throw new RuntimeException(e);
    // //		} finally {
    // //			try {
    // //				table.close();
    // //			} catch (Throwable e) {
    // //
    // //			}
    // //
    // //		}
    // //	}
    // 
    // /**
    // *
    // * @param taskid
    // * @param level
    // * @param msg
    // */
    // private void insert(ExecuteState log) {
    // //		long sequence = i++;
    // //		Long taskid = log.getTaskId();
    // //		Integer level = log.getInfoType().getType();
    // //		Put singlePut = new Put(createCriteria(level, taskid, sequence));
    // //		if (log.getPhrase() != null) {
    // //			Task task = new Task(); // this.getTaskDAO().selectByPrimaryKey(log.getTaskId());
    // //			task.setExecState(log.getExecState());
    // //			task.setPhrase(log.getPhrase());
    // //			if("fail".equalsIgnoreCase(log.getExecState())) {
    // //				task.setErrLogId(createCriteriaString(level, taskid, sequence));
    // //			}
    // //			TaskCriteria criteria = new TaskCriteria();
    // //			criteria.createCriteria().andTaskIdEqualTo(taskid);
    // //			this.getTaskDAO().updateByExampleSelective(task, criteria);
    // //		}
    // //		this.insertLog(log, singlePut, sequence);
    // }
    // 
    // static long i = 0l;
    // 
    // /**
    // *
    // * @param level
    // * @param taskid
    // * @return
    // */
    // private byte[] createCriteria(Integer level, Long taskid, Long sequence) {
    // 
    // return createCriteriaString(level, taskid, sequence).getBytes();
    // }
    // 
    // private String createCriteriaString(Integer level, Long taskid,
    // Long sequence) {
    // String prefixe = String.valueOf(taskid % 100);
    // String taskidBuffer = String.format("%014d", taskid);
    // String sequenceBuffer = String.format("%014d", sequence);
    // return (prefixe + taskidBuffer + sequenceBuffer + level.toString());
    // }
    // 
    // private byte[] createCriteria(Integer level, Long taskid) {
    // return createCriteria(level, taskid, 0l);
    // }
    // 
    // //public ExecuteLog getById(Long taskid, InfoType logLevel, long sequence) {
    // //		HTable table = null;
    // //		try {
    // //			table = (HTable) pool.getTable(TAB_NAME);
    // //
    // //			Get singleGet = new Get(createCriteria(logLevel.getType(), taskid,
    // //					sequence));
    // //
    // //			Result result = table.get(singleGet);
    // //			if (result == null) {
    // //				return new ExecuteLog();
    // //			}
    // //			return createExecuteLog(result);
    // //		} catch (IOException e) {
    // //			throw new RuntimeException(e);
    // //		} finally {
    // //			try {
    // //				table.close();
    // //			} catch (Throwable e) {
    // //
    // //			}
    // //		}
    // 
    // //}
    // 
    // //private static final int PAGE_SIZE = 10;
    // 
    // @Override
    // public List<ExecuteLog> search(final String serviceName,
    // final Date startTime, final int page, final int pageSize) {
    // 
    // Assert.assertTrue("serviceName:" + serviceName
    // + " must start with search4",
    // StringUtils.startsWith(serviceName, "search4"));
    // 
    // //		final CriteriaSetter setter = new CriteriaSetter() {
    // //			@Override
    // //			public void set(Scan scan) {
    // //				// scan.setMaxResultSize(3);
    // //				// scan.setFilter(new PageFilter(3));
    // //				scan.setOffset(pageSize * page);
    // //				// scan.setOffset(2);
    // //				String name = StringUtils
    // //						.substringAfter(serviceName, "search4");
    // //				//
    // //				String start = name + ((startTime.getTime()));// / (1000 * 60 *
    // //																// 60 * 24)) *
    // //																// (1000 * 60 *
    // //																// 60 * 24));
    // //				String end = (name + (startTime.getTime() + 99999));
    // //				//
    // //				// System.out.println(String.format("start:%s , end:%s", start,
    // //				// end));
    // //
    // //				//
    // //				//scan.setStartRow(Bytes.toBytes(start));
    // //				// scan.setStartRow(name);
    // //				// scan.setStopRow(Bytes.toBytes("hello1400151610040"));
    // //			}
    // //		};
    // //return search(setter, pageSize);
    // return null;
    // }
    // 
    // /**
    // *
    // * @param serviceName
    // * @param startTime
    // * @return
    // */
    // @Override
    // public List<ExecuteLog> search(final String serviceName,
    // final Date startTime, final int page) {
    // 
    // //return search(serviceName, startTime, page, PAGE_SIZE);
    // return null;
    // }
    // 
    // /**
    // * @param taskid
    // * @param logLevel
    // * @return
    // */
    // @Override
    // public List<ExecuteLog> search(final Long taskid, final InfoType logLevel,
    // final int page, final int pageSize) {
    // //		final CriteriaSetter setter = new CriteriaSetter() {
    // //			@Override
    // //			public void set(Scan scan) {
    // //				scan.setOffset(pageSize * page);
    // //				if (logLevel == null) {
    // //					scan.setStartRow(createCriteria(InfoType.ERROR.getType(),
    // //							taskid));
    // //					scan.setStopRow(createCriteria(InfoType.WARN.getType(),
    // //							taskid, Long.MAX_VALUE));
    // //					// scan.setStopRow(createCriteria(InfoType.FATAL.getType(),
    // //					// taskid));
    // //				} else {
    // //					byte[] criteria = createCriteria(logLevel.getType(), taskid);
    // //					scan.setStartRow(criteria);
    // //					// scan.setStopRow(criteria);
    // //				}
    // //			}
    // //		};
    // //		return search(setter, pageSize);
    // return null;
    // }
    // 
    // @Override
    // public List<ExecuteLog> search(String rowkey) {
    // //		HTable table = null;
    // //		List<ExecuteLog> result = new ArrayList<ExecuteLog>();
    // //		ExecuteLog log = null;
    // //		try {
    // //			table = (HTable) pool.getTable(TAB_NAME);
    // //			Get get = new Get(rowkey.getBytes());
    // //			Result r = table.get(get);
    // //			log = createExecuteLog(r);
    // //			result.add(log);
    // //		} catch (Exception e) {
    // //			throw new RuntimeException(e);
    // //		} finally {
    // //			try {
    // //				table.close();
    // //			} catch (Throwable e) {
    // //
    // //			}
    // //		}
    // //		return result;
    // return null;
    // }
    // 
    // //	private List<ExecuteLog> search(CriteriaSetter csetter, final int pageSize) {
    // //		return null;
    // //		List<ExecuteLog> result = new ArrayList<ExecuteLog>();
    // //		ExecuteLog log = null;
    // //		// KeyValue keyValue = null;
    // //		HTable table = null;
    // //		try {
    // //			table = (HTable) pool.getTable(TAB_NAME);
    // //
    // //			Scan scan = new Scan();
    // //
    // //			// scan.setCaching(200);
    // //			// scan.setCacheBlocks(false);// 閸忔娊妫碿ache 闂冨弶顒沢c
    // //			// scan.setMaxResultSize(200);
    // //			csetter.set(scan);
    // //			// if (logLevel == null) {
    // //			// scan.setStartRow(createCriteria(InfoType.INFO.getType(),
    // //			// taskid));
    // //			// scan.setStopRow(createCriteria(InfoType.FATAL.getType(),
    // //			// taskid));
    // //			// } else {
    // //			// byte[] criteria = createCriteria(logLevel.getType(), taskid);
    // //			// scan.setStartRow(criteria);
    // //			// scan.setStopRow(csetter);
    // //			// }
    // //
    // //			ResultScanner resultScanner = table.getScanner(scan);
    // //			int count = 0;
    // //			for (Result rr : resultScanner) {
    // //				if (count++ >= pageSize) {
    // //					break;
    // //				}
    // //				log = createExecuteLog(rr);
    // //				result.add(log);
    // //			}
    // //		} catch (Exception e) {
    // //			throw new RuntimeException(e);
    // //		} finally {
    // //			try {
    // //				table.close();
    // //			} catch (Throwable e) {
    // //
    // //			}
    // //		}
    // //
    // //		return result;
    // //}
    // 
    // //	private interface CriteriaSetter {
    // //		public void set(Scan scan);
    // //	}
    // //
    // //	private ExecuteLog createExecuteLog(Result rr) {
    // //		ExecuteLog log;
    // //		KeyValue keyValue;
    // //		log = new ExecuteLog();
    // //
    // //		keyValue = rr.getColumnLatest(columnFamily, columnLevel);
    // //		if (keyValue != null && keyValue.getValue() != null) {
    // //			log.setLevel(InfoType.getType(Bytes.toInt(keyValue.getValue())));
    // //		}
    // //		log.setMsg(Bytes.toString(rr.getColumnLatest(columnFamily, columnMsg)
    // //				.getValue()));
    // //		keyValue = rr.getColumnLatest(columnFamily, columnTaskId);
    // //		if (keyValue != null && keyValue.getValue() != null) {
    // //			log.setTaskid(Bytes.toLong(keyValue.getValue()));
    // //			// log.setTime(new Date(keyValue.getTimestamp()));
    // //		}
    // //		keyValue = rr.getColumnLatest(columnFamily, columnAddress);
    // //		if (keyValue != null && keyValue.getValue() != null) {
    // //			log.setAddress(Bytes.toString(keyValue.getValue()));
    // //		}
    // //		keyValue = rr.getColumnLatest(columnFamily, columnComponent);
    // //		if (keyValue != null && keyValue.getValue() != null) {
    // //			log.setComponent(Bytes.toString(keyValue.getValue()));
    // //		}
    // //
    // //		keyValue = rr.getColumnLatest(columnFamily, seqId);
    // //		if (keyValue != null && keyValue.getValue() != null) {
    // //			log.setSequence(Bytes.toLong(keyValue.getValue()));
    // //		}
    // //
    // //		keyValue = rr.getColumnLatest(columnFamily, columnTime);
    // //		if (keyValue != null && keyValue.getValue() != null) {
    // //			log.setTime(new Date(Bytes.toLong(keyValue.getValue())));
    // //		}
    // //		// System.out.println(log.getLevel() + " " + log.getMsg() + " time:"
    // //		// + log.getTime() + ", ft:" + log.getTime().getTime());
    // //		return log;
    // //	}
    // 
    // // private final int readInt(byte[] value) throws IOException {
    // // return ((value[0] << 24) + (value[1] << 16) + (value[2] << 8) + (value[3]
    // // << 0));
    // // }
    // //
    // // private final long readLong(byte[] value) throws IOException {
    // // return (((long) value[0] << 56) + ((long) (value[1] & 255) << 48)
    // // + ((long) (value[2] & 255) << 40)
    // // + ((long) (value[3] & 255) << 32)
    // // + ((long) (value[4] & 255) << 24) + ((value[5] & 255) << 16)
    // // + ((value[6] & 255) << 8) + ((value[7] & 255) << 0));
    // // }
    // 
    // public static class ExecuteLog {
    // private InfoType level;
    // private Long taskid;
    // private String msg;
    // private Date time = new Date();
    // private long sequence;
    // 
    // /**
    // 
    // */
    // private String component;
    // 
    // private String address;
    // 
    // public String getAddress() {
    // return address;
    // }
    // 
    // public String getComponent() {
    // return component;
    // }
    // 
    // public void setComponent(String component) {
    // this.component = component;
    // }
    // 
    // public void setAddress(String address) {
    // this.address = address;
    // }
    // 
    // public Date getTime() {
    // return time;
    // }
    // 
    // public void setTime(Date time) {
    // this.time = time;
    // }
    // 
    // public long getSequence() {
    // return sequence;
    // }
    // 
    // public void setSequence(long sequence) {
    // this.sequence = sequence;
    // }
    // 
    // public InfoType getLevel() {
    // return level;
    // }
    // 
    // public void setLevel(InfoType level) {
    // this.level = level;
    // }
    // 
    // public Long getTaskid() {
    // return taskid;
    // }
    // 
    // public void setTaskid(Long taskid) {
    // this.taskid = taskid;
    // }
    // 
    // public String getMsg() {
    // return msg;
    // }
    // 
    // public void setMsg(String msg) {
    // this.msg = msg;
    // }
    // 
    // }
}
