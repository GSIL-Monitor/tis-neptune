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
package com.qlangtech.tis.indexbuilder.bean;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.taobao.tddl.group.jdbc.TGroupDataSource;
import com.qlangtech.tis.build.jobtask.TaskContext;
import com.qlangtech.tis.build.metrics.Counters;
import com.qlangtech.tis.build.metrics.Messages;
import com.qlangtech.tis.indexbuilder.dump.OdpsMaker;
import com.qlangtech.tis.manage.common.trigger.sources.TddlTaskConfig;

/* *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class TddlDumpContext {

    private int taskid;

    private TddlTaskConfig taskConfig;

    private TaskContext taskContext;

    private String sessionNum;

    private String dump_cols_trans_parma;

    private String dump_cols_dump_parma;

    // ��ǰdump����ͱ���
    private String group;

    private List<String> tables;

    private TGroupDataSource ds;

    private Connection conn;

    private OdpsMaker odpsAccount;

    private Map<Integer, OdpsTunnelContext> odpsTunnelMap = new HashMap<Integer, OdpsTunnelContext>();

    private Counters counters;

    private Messages messages;

    public Counters getCounters() {
        return counters;
    }

    public void setCounters(Counters counters) {
        this.counters = counters;
    }

    public Messages getMessages() {
        return messages;
    }

    public void setMessages(Messages messages) {
        this.messages = messages;
    }

    public TaskContext getTaskContext() {
        return taskContext;
    }

    public void setTaskContext(TaskContext taskContext) {
        this.taskContext = taskContext;
    }

    public int getTaskid() {
        return taskid;
    }

    public void setTaskid(int taskid) {
        this.taskid = taskid;
    }

    public String getSessionNum() {
        return sessionNum;
    }

    public void setSessionNum(String sessionNum) {
        this.sessionNum = sessionNum;
    }

    public Map<Integer, OdpsTunnelContext> getOdpsTunnelMap() {
        return odpsTunnelMap;
    }

    public void setOdpsTunnelMap(Map<Integer, OdpsTunnelContext> odpsTunnelMap) {
        this.odpsTunnelMap = odpsTunnelMap;
    }

    public OdpsMaker getOdpsAccount() {
        return odpsAccount;
    }

    public void setOdpsAccount(OdpsMaker odpsAccount) {
        this.odpsAccount = odpsAccount;
    }

    public Connection getConn() {
        return conn;
    }

    public void setConn(Connection conn) {
        this.conn = conn;
    }

    public TGroupDataSource getDs() {
        return ds;
    }

    public void setDs(TGroupDataSource ds) {
        this.ds = ds;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public List<String> getTables() {
        return tables;
    }

    public void setTables(List<String> tables) {
        this.tables = tables;
    }

    public TddlTaskConfig getTaskConfig() {
        return taskConfig;
    }

    public void setTaskConfig(TddlTaskConfig taskConfig) {
        this.taskConfig = taskConfig;
    }

    public String getDump_cols_trans_parma() {
        return dump_cols_trans_parma;
    }

    public void setDump_cols_trans_parma(String dump_cols_trans_parma) {
        this.dump_cols_trans_parma = dump_cols_trans_parma;
    }

    public String getDump_cols_dump_parma() {
        return dump_cols_dump_parma;
    }

    public void setDump_cols_dump_parma(String dump_cols_dump_parma) {
        this.dump_cols_dump_parma = dump_cols_dump_parma;
    }
}
