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
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.apache.log4j.Logger;
import com.aliyun.odps.Column;
import com.aliyun.odps.Instance;
import com.aliyun.odps.Instance.StageProgress;
import com.aliyun.odps.Instance.Status;
import com.aliyun.odps.Instance.TaskStatus;
import com.aliyun.odps.Odps;
import com.aliyun.odps.Project;
import com.aliyun.odps.Projects;
import com.aliyun.odps.Table;
import com.aliyun.odps.OdpsException;
import com.aliyun.odps.TableSchema;
import com.aliyun.odps.account.Account;
import com.aliyun.odps.account.AliyunAccount;
import com.aliyun.odps.data.Record;
import com.aliyun.odps.data.RecordReader;
import com.aliyun.odps.task.SQLTask;
import com.aliyun.odps.tunnel.DataTunnel;

/*
 * @Description ODPS Account Information
 *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class ODPSAccount {

    private String accessId;

    private String accessKey;

    private String partition;

    private final String project = "jst_tsearcher";

    private final String endpoint = "http://service.odps.aliyun.com/api";

    private Account account;

    public ODPSAccount(String accessId, String accessKey) {
        super();
        this.accessId = accessId;
        this.accessKey = accessKey;
        account = new AliyunAccount(this.accessId, this.accessKey);
    }

    /**
     * ODPS��Ĵ���
     *
     * @param sql
     *            �����sql���
     * @param taskid
     *            ������������id��������־�ռ�
     */
    public void createTable(String sql, int taskid) {
        try {
            Odps odps = new Odps(account);
            odps.setDefaultProject(this.project);
            odps.setEndpoint(this.endpoint);
            sql += "partitioned by (pid String,pdate String,taskid String);";
            Instance instance = SQLTask.run(odps, sql);
            String id = instance.getId();
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
                        }
                    }
                    if (allcount > 0) {
                    }
                } while (status != Status.TERMINATED);
                if (TaskStatus.Status.FAILED.equals(instance.getStatus())) {
                    throw new IllegalStateException("Odps Instance Task " + instance.getId() + "failed");
                }
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
        } catch (OdpsException e) {
            throw new IllegalStateException("Odps Exception " + e.getMessage() + "ErrorCode: " + e.getErrorCode());
        }
    }

    /**
     * �������̫��ʱ����Ҫ����rehash�������������·���
     *
     * @param tablename
     *            ODPS�ı���
     * @param group
     *            Ӧ����Ҫ���������
     * @param pdate
     *            ��ǰ���ڣ�ODPS������
     * @param taskid
     *            ��������ID��ODPS������
     * @param sharedKey
     *            rehashʹ�õķ����
     */
    public boolean rehash(String tablename, int group, String pdate, int taskid, String sharedKey) {
        try {
            Odps odps = new Odps(account);
            odps.setDefaultProject(this.project);
            odps.setEndpoint(this.endpoint);
            if (!contains(tablename, odps)) {
                return false;
            }
            Table table = getODPSTable(tablename, odps);
            TableSchema tableSchema = table.getSchema();
            List<Column> columnList = tableSchema.getColumns();
            StringBuffer buffer = new StringBuffer();
            buffer.append("INSERT INTO TABLE " + tablename + " PARTITION (pid,pdate,taskid) SELECT ");
            for (Column column : columnList) {
                buffer.append(column.getName() + ",");
            }
            buffer.append("pmod(" + sharedKey + "," + group + ") as pid,concat(" + "\"mod_\"" + ",pdate),taskid from " + tablename + " where pdate=" + pdate + " and taskid=" + taskid + ";");
            System.out.println(String.valueOf(buffer));
            Instance instance = SQLTask.run(odps, String.valueOf(buffer));
            String id = instance.getId();
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
                        processSummary.append("Taskname:").append(name);
                        for (StageProgress progress : instance.getTaskProgress(name)) {
                            processSummary.append(", Name:" + progress.getName());
                            tworks = progress.getTerminatedWorkers();
                            tcount += tworks;
                            processSummary.append(",tworkers: " + tworks);
                            allworkds = progress.getTotalWorkers();
                            allcount += allworkds;
                            processSummary.append(",totalworks : " + allworkds);
                        }
                    }
                    if (allcount > 0) {
                    }
                } while (status != Status.TERMINATED);
                if (TaskStatus.Status.FAILED.equals(instance.getStatus())) {
                    throw new IllegalStateException("Odps Instance Task " + instance.getId() + "failed");
                }
                return true;
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
        } catch (OdpsException e) {
            throw new IllegalStateException("Odps Exception " + e.getMessage() + "ErrorCode: " + e.getErrorCode());
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

    public String getEndpoint() {
        return endpoint;
    }

    public static void main(String[] args) throws IOException, OdpsException {
        ODPSAccount odpsAccount = new ODPSAccount("07mbAZ3it1QaLTE8", "BEGxJuH9JY7E6AYR2qWoHSjvufN0cV");
        Odps odps = new Odps(odpsAccount.account);
        odps.setDefaultProject(odpsAccount.project);
        odps.setEndpoint(odpsAccount.endpoint);
        for (Table t : odps.tables()) {
            TableSchema schema = t.getSchema();
            List<Column> columns = schema.getColumns();
            for (int i = 0; i < columns.size(); i++) {
                Column column = columns.get(i);
                System.out.println(column.getName());
                System.out.println(column.getType());
                System.out.println(column.getComment());
            }
            List<Column> partitionColumns = schema.getPartitionColumns();
            for (int i = 0; i < partitionColumns.size(); i++) {
                Column column = partitionColumns.get(i);
                System.out.println(column.getName());
                System.out.println(column.getType());
                System.out.println(column.getComment());
            }
            System.out.println(t.getName());
        }
    // odpsAccount.rehash("search4_182589402_tsearch_school", 4, "20140926",
    // 2, "stu_id");
    // String sql = "CREATE TABLE if not exists test1" + "(" + "id Bigint)";
    // odpsAccount.createTable(sql, 11);
    }

    public void init() {
        ODPSAccount odpsAccount = new ODPSAccount("07mbAZ3it1QaLTE8", "BEGxJuH9JY7E6AYR2qWoHSjvufN0cV");
        Odps odps = new Odps(odpsAccount.account);
        odps.setDefaultProject(odpsAccount.project);
        odps.setEndpoint(odpsAccount.endpoint);
    }

    public void creat_table(ODPSAccount odpsAccount, Odps odps) {
        String tableName = "";
        String sql = "CREATE TABLE if not exists " + tableName + " + (" + "id Bigint)";
        odpsAccount.createTable(sql, 11);
    }
}
