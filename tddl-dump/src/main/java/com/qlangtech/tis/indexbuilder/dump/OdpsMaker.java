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
package com.qlangtech.tis.indexbuilder.dump;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.Set;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import com.aliyun.odps.Column;
import com.aliyun.odps.Instance;
import com.aliyun.odps.Instance.StageProgress;
import com.aliyun.odps.Instance.Status;
import com.aliyun.odps.Instance.TaskStatus;
import com.aliyun.odps.Instance.TaskSummary;
import com.aliyun.odps.Odps;
import com.aliyun.odps.PartitionSpec;
import com.aliyun.odps.Project;
import com.aliyun.odps.Projects;
import com.aliyun.odps.Table;
import com.aliyun.odps.OdpsException;
import com.aliyun.odps.TableSchema;
import com.aliyun.odps.account.Account;
import com.aliyun.odps.account.AliyunAccount;
import com.aliyun.odps.data.Record;
import com.aliyun.odps.data.RecordReader;
import com.aliyun.odps.data.RecordWriter;
import com.aliyun.odps.task.SQLTask;
import com.aliyun.odps.tunnel.TableTunnel;
import com.aliyun.odps.tunnel.TableTunnel.UploadSession;
import com.aliyun.odps.tunnel.TunnelException;
import com.qlangtech.tis.indexbuilder.bean.OdpsTunnelContext;
import com.qlangtech.tis.indexbuilder.bean.TddlDumpContext;
import com.qlangtech.tis.indexbuilder.exception.MyException;
import com.qlangtech.tis.indexbuilder.utils.TddlUtil;
import com.qlangtech.tis.manage.common.Secret;
import com.qlangtech.tis.manage.common.trigger.ODPSConfig;

/* *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class OdpsMaker {

    private static final Logger logger = Logger.getLogger(OdpsMaker.class);

    private TddlDumpContext tddlDumpContext;

    private int session_count;

    private String accessId;

    private String accessKey;

    private String partition;

    private String project;

    private String odps_endpoint;

    private String tunnel_endpoint;

    private Account account;

    private Odps odps;

    // ת�������͵��ֶ�map
    private Map<String, String> dump_cols_trans_map = new HashMap<String, String>();

    // δת�͵��ֶ�
    private Map<String, String> dump_cols_dump_map = new HashMap<String, String>();

    public OdpsMaker(TddlDumpContext tddlDumpContext) {
        super();
        logger.warn("��ʼ��odps����");
        try {
            ODPSConfig odpsConfig = tddlDumpContext.getTaskConfig().getOdpsConfig();
            this.tddlDumpContext = tddlDumpContext;
            this.accessId = Secret.decrypt(odpsConfig.getAccessId(), "TIS_Terminator_Key");
            this.accessKey = Secret.decrypt(odpsConfig.getAccessKey(), "TIS_Terminator_Key");
            this.odps_endpoint = odpsConfig.getServiceEndPoint();
            this.tunnel_endpoint = odpsConfig.getDatatunelEndPoint();
            this.project = odpsConfig.getProject();
            account = new AliyunAccount(this.accessId, this.accessKey);
            odps = new Odps(account);
            odps.setDefaultProject(project);
            odps.setEndpoint(odps_endpoint);
            dump_cols_dump_map = TddlUtil.str2Map(tddlDumpContext.getDump_cols_dump_parma());
            dump_cols_trans_map = TddlUtil.str2Map(tddlDumpContext.getDump_cols_trans_parma());
        } catch (Exception e) {
            // logger.error(Lg.c(tddlDumpContext.getTaskid(),
            // "OdpsMaker():  init odps configure  fail!!!!  "
            // +ExceptionUtils.getFullStackTrace(e)));
            logger.error("OdpsMaker()��ʼ��odps����ʧ��" + e);
            throw new MyException("OdpsMaker():  init odps configure  fail!!!!  " + ExceptionUtils.getFullStackTrace(e));
        }
        init_sessions();
    }

    public void init_sessions() {
        try {
            // ��ʼ��odps session
            session_count = Integer.parseInt(tddlDumpContext.getSessionNum());
            logger.warn("��ʼ��" + session_count + "��session��ʼ");
            // logger.warn(Lg.c(tddlDumpContext.getTaskid(),
            // "init "+session_count+"  sessions  start"));
            tddlDumpContext.getOdpsTunnelMap().clear();
            TableTunnel tunnel = new TableTunnel(odps);
            tunnel.setEndpoint(tunnel_endpoint);
            ODPSConfig odpsConfig = tddlDumpContext.getTaskConfig().getOdpsConfig();
            for (int i = 0; i < session_count; i++) {
                OdpsTunnelContext context = new OdpsTunnelContext();
                String partition = odpsConfig.getDailyPartition().getKey() + "='" + odpsConfig.getDailyPartition().getValue() + "'," + odpsConfig.getGroupPartition() + "='" + i + "',taskid='" + tddlDumpContext.getTaskConfig().getTaskId() + "'";
                String table = (tddlDumpContext.getTaskConfig().getAppName() + "_" + tddlDumpContext.getTaskConfig().getLogicTableName()).toLowerCase();
                PartitionSpec partitionSpec = new PartitionSpec(partition);
                UploadSession uploadSession = tunnel.createUploadSession(project, table, partitionSpec);
                TableSchema schema = uploadSession.getSchema();
                RecordWriter recordWriter = uploadSession.openRecordWriter(0);
                context.setTunnel(tunnel);
                context.setPartitionSpec(partitionSpec);
                context.setUploadSession(uploadSession);
                context.setSchema(schema);
                context.setRecordWriter(recordWriter);
                tddlDumpContext.getOdpsTunnelMap().put(i, context);
            }
            logger.warn("��ʼ��" + session_count + "��session�ɹ�");
        // logger.warn(Lg.c(tddlDumpContext.getTaskid(),
        // "init "+session_count+"  sessions  success"));
        } catch (Exception e) {
            logger.error("��ʼ��sessionsʧ��" + e);
            // "init sessions fail !!!!  "+e));
            throw new MyException("init sessions fail !!!!  " + e);
        }
    }

    public void createTable(String logicName, String cols) {
        try {
            String sql = "CREATE TABLE if not exists " + logicName + " (" + cols + " )" + " partitioned by (dailyPartition String,groupPartition String,taskId String);";
            Instance instance = SQLTask.run(odps, sql);
            logger.warn("ODPS sql is " + sql);
            sumit(instance);
        } catch (Exception e) {
            logger.error("createTable() Exception " + e);
        }
    }

    public void dropTable(String logicName) {
        try {
            String sql = "DROP TABLE IF EXISTS " + logicName + ";";
            Instance instance = SQLTask.run(odps, sql);
            logger.warn("ODPS sql is " + sql);
            sumit(instance);
        } catch (Exception e) {
            logger.error("createTable() Exception " + e);
        }
    }

    public void addPartition(String logicName, String partition) {
        try {
            // sale_date='201310', region='hangzhou'
            String sql = "ALTER TABLE " + logicName + " ADD if not exists PARTITION (" + partition + ");";
            Instance instance = SQLTask.run(odps, sql);
            logger.warn("ODPS sql is " + sql);
            sumit(instance);
        } catch (Exception e) {
            logger.error("createTable() Exception " + e);
        }
    }

    public void showPartition(String logicName) {
        try {
            // sale_date='201310', region='hangzhou'
            String sql = "SHOW PARTITIONS " + logicName + ";";
            Instance instance = SQLTask.run(odps, sql);
            logger.warn("ODPS sql is " + sql);
            sumit(instance);
        } catch (Exception e) {
            logger.error("createTable() Exception " + e);
        }
    }

    public void dropPartition(String logicName, String partition) {
        try {
            // sale_date='201310', region='hangzhou'
            String sql = "ALTER TABLE " + logicName + " DROP if exists PARTITION (" + partition + ");";
            Instance instance = SQLTask.run(odps, sql);
            logger.warn("ODPS sql is " + sql);
            sumit(instance);
        } catch (Exception e) {
            logger.error("createTable() Exception " + e);
        }
    }

    public void sumit(Instance instance) {
        String id = instance.getId();
        logger.warn("ODPS Task id is: " + id);
        Status status;
        try {
            do {
                Thread.sleep(3000);
                status = instance.getStatus();
                StringBuffer processSummary = new StringBuffer();
                int tcount = 0;
                int tworks = 0;
                int allcount = 0;
                int allworkds = 0;
                for (String name : instance.getTaskNames()) {
                    processSummary.append("ODPS Taskname:").append(name);
                    for (StageProgress progress : instance.getTaskProgress(name)) {
                        processSummary.append(",name:" + progress.getName());
                        tworks = progress.getTerminatedWorkers();
                        tcount += tworks;
                        processSummary.append(",tworkers:" + tworks);
                        allworkds = progress.getTotalWorkers();
                        allcount += allworkds;
                        processSummary.append(",totalworks:" + allworkds).append("\n");
                        System.out.println(processSummary);
                        logger.warn(processSummary);
                    }
                }
                if (allcount > 0) {
                    logger.warn("percent:" + ((float) tcount / allcount));
                }
                logger.warn("ODPS task:" + id + "; status:" + status + "; Summary " + processSummary.toString());
            } while (status != Status.TERMINATED);
            if (TaskStatus.Status.FAILED.equals(instance.getStatus())) {
                throw new IllegalStateException("Odps Instance Task " + instance.getId() + "failed");
            }
        } catch (InterruptedException e) {
            throw new IllegalStateException(e);
        } catch (OdpsException e) {
            throw new IllegalStateException(e);
        }
    }

    public void upload_data(Map<String, String> record_map) {
        int group = getGroup(record_map);
        if (tddlDumpContext.getOdpsTunnelMap() == null || tddlDumpContext.getOdpsTunnelMap().size() == 0) {
            throw new MyException("upload_data()   odps's  sessions  null!!!!  ");
        }
        OdpsTunnelContext odpsTunnelContext = tddlDumpContext.getOdpsTunnelMap().get(group);
        write(record_map, odpsTunnelContext);
    }

    public int getGroup(Map<String, String> record_map) {
        // ��ȡ�����ֶ�
        try {
            String key = tddlDumpContext.getTaskConfig().getShareId();
            String value = record_map.get(key);
            int group = (int) (Long.parseLong(value) % session_count);
            // int group = Integer.parseInt(value) % session_count;
            return group;
        } catch (Exception e) {
            // +ExceptionUtils.getFullStackTrace(e)));
            throw new MyException("getGroup() Exception!!!" + ExceptionUtils.getFullStackTrace(e));
        }
    }

    public void commit(OdpsTunnelContext odpsTunnelContext) {
        try {
            odpsTunnelContext.getRecordWriter().close();
            odpsTunnelContext.getUploadSession().commit(new Long[] { 0L });
        } catch (Exception e) {
            // logger.error(Lg.c(tddlDumpContext.getTaskid(),
            // "!!!!!!!commit() odps  session  close and commit fail!!!!!  "
            // + "		session partiotion:"+odpsTunnelContext.getPartitionSpec()
            // +"           "+ExceptionUtils.getFullStackTrace(e)));
            logger.error("!!!!!!!!!!!commit() ��odps  session�ύ���ݲ��ҹر�ʧ��!!!!!!!!!!!!!!!");
            throw new MyException("!!!!!!!commit() odps  session  close and commit fail!!!!!  " + "		session partiotion:" + odpsTunnelContext.getPartitionSpec() + "           " + ExceptionUtils.getFullStackTrace(e));
        }
    }

    public void write(Map<String, String> record_map, OdpsTunnelContext odpsTunnelContext) {
        try {
            Record record = odpsTunnelContext.getUploadSession().newRecord();
            for (int i = 0; i < odpsTunnelContext.getSchema().getColumns().size(); i++) {
                Column column = odpsTunnelContext.getSchema().getColumn(i);
                String column_name = column.getName();
                String value = record_map.get(column_name);
                if (StringUtils.isBlank(value)) {
                    continue;
                }
                switch(column.getType()) {
                    case BIGINT:
                        record.setBigint(i, Long.parseLong(value));
                        break;
                    case BOOLEAN:
                        record.setBoolean(i, Boolean.parseBoolean(value));
                        break;
                    // break;
                    case DOUBLE:
                        record.setDouble(i, Double.parseDouble(value));
                        break;
                    case STRING:
                        record.setString(i, value);
                        break;
                    default:
                        throw new RuntimeException("Unknown column type: " + column.getType());
                }
            }
            odpsTunnelContext.getRecordWriter().write(record);
        // odpsTunnelContext.getRecordWriter().close();
        // odpsTunnelContext.getUploadSession().commit(new Long[]{0L});
        } catch (Exception e) {
            // +ExceptionUtils.getFullStackTrace(e)));
            throw new MyException("write()  write record " + record_map + " to  odps fail!!!!" + ExceptionUtils.getFullStackTrace(e));
        }
    }

    public boolean contains(String tablename, Odps odps) {
        for (Table t : odps.tables()) {
            if (tablename.equals(t.getName())) {
                return true;
            }
        }
        return false;
    }

    public Table getODPSTable(String tablename, Odps odps) {
        for (Table t : odps.tables()) {
            if (tablename.equals(t.getName())) {
                return t;
            }
        }
        return null;
    }

    public String getAccessId() {
        return accessId;
    }

    public void setAccessId(String accessId) {
        this.accessId = accessId;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getProject() {
        return project;
    }

    public String getPartition() {
        return partition;
    }

    public void setPartition(String partition) {
        this.partition = partition;
    }

    public String getOdps_endpoint() {
        return odps_endpoint;
    }

    // public static void main(String[] args) throws TunnelException {
    // String partition="dp='1',gp='11',taskid='111'";
    // 
    // Account account = new AliyunAccount("07mbAZ3it1QaLTE8",
    // "BEGxJuH9JY7E6AYR2qWoHSjvufN0cV");
    // Odps odps = new Odps(account);
    // odps.setDefaultProject("jst_tsearcher");
    // odps.setEndpoint("http://service.odps.aliyun.com/api");
    // 
    // TableTunnel tunnel = new TableTunnel(odps);
    // tunnel.setEndpoint("http://dt.odps.aliyun.com");
    // 
    // PartitionSpec partitionSpec = new PartitionSpec(partition);
    // UploadSession uploadSession =
    // tunnel.createUploadSession("jst_tsearcher","application", partitionSpec);
    // 
    // }
    public static void main(String[] args) throws IOException, OdpsException {
        // final String dailyPartition="20141101";
        // final String groupPartition="1";
        // final String taskId="1001";
        // final String logicName="app_test";
        // final String
        // cols="id BIGINT,isSucess BOOLEAN,time DATETIME,price DOUBLE, name STRING";
        // sale_date='201310', region='hangzhou'
        // final String
        // partition="dailyPartition='"+dailyPartition+"', groupPartition='"+groupPartition+"', taskId='"+taskId+"'";
        // final OdpsMaker odpsAccount = new OdpsMaker(tddlDumpContext);
        // Map<String, String> record_map=new HashMap<String, String>();
        // record_map.put("id", "11111");
        // record_map.put("isSucess", "true");
        // record_map.put("time", "2008-08-08 12:10:12");
        // record_map.put("price", "10.9");
        // record_map.put("name", "�ⲻ����ϰ");
        // odpsAccount.upload(record_map,logicName, dailyPartition,
        // groupPartition, taskId);
        // odpsAccount.rehash("search4_182589402_tsearch_school", 4,
        // "20140926",2, "stu_id");
        // odpsAccount.dropTable(logicName);
        // 
        // odpsAccount.createTable(logicName, cols);
        // 
        // odpsAccount.addPartition(logicName, partition);
        // 
        // odpsAccount.showPartition(logicName);
        Account account = new AliyunAccount("07mbAZ3it1QaLTE8", "BEGxJuH9JY7E6AYR2qWoHSjvufN0cV");
        Odps odps = new Odps(account);
        odps.setDefaultProject("jst_tsearcher");
        odps.setEndpoint("http://service.odps.aliyun.com/api");
        // 
        for (Table t : odps.tables()) {
            if ("search4_182589402_mytest_school".equals(t.getName())) {
                System.out.println(t.getName());
                TableSchema schema = t.getSchema();
                List<Column> columns = schema.getColumns();
                System.out.println("colums:");
                for (int i = 0; i < columns.size(); i++) {
                    Column column = columns.get(i);
                    System.out.println(column.getName());
                    System.out.println(column.getType());
                    System.out.println(column.getComment());
                }
                List<Column> partitionColumns = schema.getPartitionColumns();
                System.out.println("partitionColumns");
                for (int i = 0; i < partitionColumns.size(); i++) {
                    Column column = partitionColumns.get(i);
                    System.out.println(column.getName());
                    System.out.println(column.getType());
                    System.out.println(column.getComment());
                }
            }
        }
    // 
    }
}
