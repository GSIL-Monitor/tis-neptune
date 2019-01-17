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

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.taobao.tddl.common.exception.TddlException;
import com.taobao.tddl.group.jdbc.TGroupDataSource;
import com.qlangtech.tis.build.metrics.Counters;
import com.qlangtech.tis.build.metrics.Messages;
import com.qlangtech.tis.indexbuilder.bean.TddlDumpContext;
import com.qlangtech.tis.indexbuilder.exception.MyException;
import com.qlangtech.tis.indexbuilder.map.DumpSuccessFlag;
import com.qlangtech.tis.indexbuilder.map.TddlDump;
import com.qlangtech.tis.manage.common.trigger.sources.TddlTaskConfig;

/* *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class TddlMaker {

    public static final Logger logger = LoggerFactory.getLogger(TddlMaker.class);

    private static final ExecutorService taskPool = Executors.newFixedThreadPool(10);

    private TddlDumpContext tddlDumpContext;

    private List<DumpSuccessFlag> threadList;

    // private Counters counters;
    private void initDs() throws NullPointerException {
        logger.warn("��ʼ��ʼ��tddl����");
        TGroupDataSource ds = new TGroupDataSource();
        ds.setAppName(tddlDumpContext.getTaskConfig().getTddlAppName());
        ds.setDbGroupKey(tddlDumpContext.getGroup());
        try {
            ds.init();
            Connection conn = ds.getConnection();
            tddlDumpContext.setDs(ds);
            tddlDumpContext.setConn(conn);
        } catch (TddlException e) {
            logger.error("��ʼ��tddl���ó��ִ���TddlException    " + e);
            throw new MyException("initDs():init tddl configure TddlException" + ExceptionUtils.getFullStackTrace(e));
        } catch (SQLException e) {
            logger.error("��ʼ��tddl���ó��ִ���SQLException    " + e);
            throw new MyException("initDs():init tddl configure SQLException" + ExceptionUtils.getFullStackTrace(e));
        }
    }

    private void initOdps() {
        OdpsMaker odpsAccount = new OdpsMaker(tddlDumpContext);
        tddlDumpContext.setOdpsAccount(odpsAccount);
    }

    public String parseClos(Map<String, String> cols) {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, String> entry : cols.entrySet()) {
            builder.append(entry.getKey()).append(" ").append(entry.getValue()).append(",");
        }
        String clos_value = builder.substring(0, builder.length() - 1);
        return clos_value;
    }

    public TddlMaker(TddlDumpContext tddlDumpContext, List<DumpSuccessFlag> threadList) {
        super();
        this.tddlDumpContext = tddlDumpContext;
        this.threadList = threadList;
        Map<String, List<String>> dbs = tddlDumpContext.getTaskConfig().getDbs();
        if (dbs.containsKey(tddlDumpContext.getGroup())) {
            tddlDumpContext.setTables(dbs.get(tddlDumpContext.getGroup()));
        } else {
            logger.error("the dbs parms don't have the group" + tddlDumpContext.getGroup());
            throw new MyException("the dbs parms don't have the group" + tddlDumpContext.getGroup());
        }
        initDs();
        initOdps();
    }

    public void markJob() {
        TddlDumpJob dumpJob = null;
        for (String tableName : tddlDumpContext.getTables()) {
            dumpJob = new TddlDumpJob(tddlDumpContext, tableName);
            logger.warn("����ִ������ԴTDDl appName:" + tddlDumpContext.getTaskConfig().getAppName() + "��" + tddlDumpContext.getGroup() + "���" + tableName + "��ȫ��dump-----------");
            // logger.warn(Lg.c(tddlDumpContext.getTaskid(),
            // "start dump APP:"+tddlDumpContext.getTaskConfig().getAppName()
            // +"  group:"+tddlDumpContext.getGroup()
            // +"  table:"+tableName));
            threadList.add(dumpJob.getSuccessFlag());
            taskPool.execute(dumpJob);
        }
    }

    public static class BuildResult {

        private String tableName;

        private TddlReader reader;

        private long total_time;

        private boolean isComplete = false;

        public TddlReader getReader() {
            return reader;
        }

        public void setReader(TddlReader reader) {
            this.reader = reader;
        }

        public long getTotal_time() {
            return total_time;
        }

        public void setTotal_time(long total_time) {
            this.total_time = total_time;
        }

        public String getTableName() {
            return tableName;
        }

        public void setTableName(String tableName) {
            this.tableName = tableName;
        }

        public BuildResult(TddlReader reader) {
            super();
            this.reader = reader;
        }

        public boolean isComplete() {
            return isComplete;
        }

        public void setComplete(boolean isComplete) {
            this.isComplete = isComplete;
        }
    }

    public static interface SCallback {

        public void execute(TddlTaskConfig state, BuildResult buildResult);
    }
}
