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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.fs.FileSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.qlangtech.tis.build.jobtask.TaskContext;
import com.qlangtech.tis.build.metrics.Counters;
import com.qlangtech.tis.build.metrics.Messages;
import com.qlangtech.tis.build.task.TaskMapper;
import com.qlangtech.tis.build.task.TaskReturn;
import com.qlangtech.tis.indexbuilder.bean.TddlDumpContext;
import com.qlangtech.tis.indexbuilder.dump.TddlMaker;
import com.qlangtech.tis.indexbuilder.map.DumpSuccessFlag.Flag;
import com.qlangtech.tis.manage.common.trigger.sources.TddlTaskConfig;

/* *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class TddlDump implements TaskMapper {

    public static final Logger logger = LoggerFactory.getLogger(TddlDump.class);

    IndexConf indexConf;

    FileSystem fs;

    long startTime;

    private Counters counters;

    private Messages messages;

    private TddlDumpContext tddlDumpContext;

    public enum Message {

        ERROR_MSG
    }

    public TddlDump() throws IOException {
        startTime = System.currentTimeMillis();
        indexConf = new IndexConf(false);
        indexConf.addResource("config.xml");
    }

    @Override
    public TaskReturn map(TaskContext context) {
        this.counters = context.getCounters();
        this.messages = context.getMessages();
        logger.warn("--------------���������յ�dump���빤��---------------");
        TddlDumpContext tddlDumpContext = new TddlDumpContext();
        String dump_config_parma = context.getUserParam("dump.config.parma");
        // ��Ҫ�������
        String dump_cols_trans_parma = context.getUserParam("dump.cols.trans.parma");
        String dump_cols_dump_parma = context.getUserParam("dump.cols.dump.parma");
        // ��ǰ��Ҫ���������
        String group_now = context.getUserParam("group_now");
        TddlTaskConfig taskConfig = (TddlTaskConfig) TddlTaskConfig.parse(dump_config_parma);
        int taskid = Integer.parseInt(String.valueOf(taskConfig.getTaskId()));
        String sessionNum = context.getUserParam("sessionNum");
        logger.warn("taskConfig:" + dump_config_parma);
        logger.warn("dump_cols_trans_parma:" + dump_cols_trans_parma);
        logger.warn("dump_cols_dump_parma:" + dump_cols_dump_parma);
        logger.warn("group_now:" + group_now);
        logger.warn("sessionNum:" + sessionNum);
        if (group_now == null || "".equals(group_now)) {
            logger.error(taskConfig.getAppName() + " APP's  dbs: " + taskConfig.getDbs() + " ��Ҫ�������Ϊ��!!!!");
            // logger.error(Lg.c(taskid, taskConfig.getAppName()+" APP's  dbs: "+taskConfig.getDbs()+" group_now null!!!!"));
            messages.addMessage(TddlDump.Message.ERROR_MSG, taskConfig.getAppName() + " APP's  dbs: " + taskConfig.getDbs() + " ��Ҫ�������Ϊ��!!!!");
            return new TaskReturn(TaskReturn.ReturnCode.FAILURE, taskConfig.getAppName() + " APP's  dbs: " + taskConfig.getDbs() + " ��Ҫ�������Ϊ��!!!!");
        }
        if (dump_cols_dump_parma == null || dump_cols_trans_parma == null || "".equals(dump_cols_dump_parma) || "".equals(dump_cols_trans_parma)) {
            logger.error(taskConfig.getAppName() + "��" + taskConfig.getDbs() + "��Ҫ�������Ϊ��");
            messages.addMessage(TddlDump.Message.ERROR_MSG, taskConfig.getAppName() + " APP's  dbs: " + taskConfig.getDbs() + " ��Ҫ�����columsΪ��!!!!");
            return new TaskReturn(TaskReturn.ReturnCode.FAILURE, taskConfig.getAppName() + " APP's  dbs: " + taskConfig.getDbs() + " ��Ҫ�����columsΪ��!!!!");
        }
        if (taskConfig.getOdpsConfig() == null || taskConfig.getOdpsConfig().getDailyPartition() == null || taskConfig.getOdpsConfig().getGroupPartition() == null || "".equals(taskConfig.getOdpsConfig().getGroupPartition())) {
            logger.error(taskConfig.getAppName() + "��odps��������Ϊ��");
            messages.addMessage(TddlDump.Message.ERROR_MSG, taskConfig.getAppName() + "��odps��������Ϊ��");
            return new TaskReturn(TaskReturn.ReturnCode.FAILURE, taskConfig.getAppName() + "��odps��������Ϊ��");
        }
        if (sessionNum == null || "".equals(sessionNum)) {
            logger.error(taskConfig.getAppName() + "��sessionNumΪ��");
            messages.addMessage(TddlDump.Message.ERROR_MSG, taskConfig.getAppName() + "��sessionNumΪ��");
            return new TaskReturn(TaskReturn.ReturnCode.FAILURE, taskConfig.getAppName() + "��sessionNumΪ��");
        }
        tddlDumpContext.setTaskConfig(taskConfig);
        tddlDumpContext.setTaskContext(context);
        tddlDumpContext.setTaskid(taskid);
        tddlDumpContext.setGroup(group_now);
        tddlDumpContext.setDump_cols_dump_parma(dump_cols_dump_parma);
        tddlDumpContext.setDump_cols_trans_parma(dump_cols_trans_parma);
        tddlDumpContext.setSessionNum(sessionNum);
        tddlDumpContext.setCounters(counters);
        tddlDumpContext.setMessages(messages);
        this.tddlDumpContext = tddlDumpContext;
        List<DumpSuccessFlag> threadList = new ArrayList<DumpSuccessFlag>();
        try {
            if ("tddl".equals(taskConfig.getType())) {
                logger.warn("���ε������������Ϊtddl,��Ҫ����ı�Ϊ:" + taskConfig.getDbs());
                TddlMaker tMaker = new TddlMaker(tddlDumpContext, threadList);
                tMaker.markJob();
            }
        } catch (Exception e) {
            logger.error("!!!!!!!!!!!!!!!!��������dump���빤��ʧ��!!!!!!!!!!!!!!" + e);
            messages.addMessage(TddlDump.Message.ERROR_MSG, "!!!!!!!!!!!!!!!!��������dump���빤��ʧ��!!!!!!!!!!!!!!" + e);
            return new TaskReturn(TaskReturn.ReturnCode.FAILURE, "!!!!!!!!!!!!!!!!��������dump���빤��ʧ��!!!!!!!!!!!!!!" + e);
        }
        while (true) {
            DumpSuccessFlag flag = checkSuccessFlag(threadList, taskid);
            if (flag.getFlag() == DumpSuccessFlag.Flag.SUCCESS) {
                // logger.warn("odps�ѵ�������"+flag.getTotalImportNum());
                logger.warn("group:" + group_now + "  �ܵ�������:  " + counters.getGroup("group-" + group_now).getCounter("counter-totalNum"));
                logger.warn("-------��������" + taskConfig.getAppName() + "dump����success----------");
                return new TaskReturn(TaskReturn.ReturnCode.SUCCESS, "success");
            } else if (flag.getFlag() == DumpSuccessFlag.Flag.FAILURE) {
                logger.warn("!!!!!!!!!!!��������" + taskConfig.getAppName() + "dump����ʧ��!!!!!!!!!!!!!");
                messages.addMessage(TddlDump.Message.ERROR_MSG, flag.getMsg());
                return new TaskReturn(TaskReturn.ReturnCode.FAILURE, "fail");
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    protected DumpSuccessFlag checkSuccessFlag(List<DumpSuccessFlag> threadList, int taskid) {
        DumpSuccessFlag flag = new DumpSuccessFlag();
        flag.setFlag(Flag.SUCCESS);
        for (DumpSuccessFlag sf : threadList) {
            if (sf.getFlag() == DumpSuccessFlag.Flag.FAILURE) {
                logger.error("table:" + sf.getTableName() + " dump error!!!!   errorNum:" + sf.getErrorCount());
                return sf;
            }
            if (sf.getFlag() == DumpSuccessFlag.Flag.RUNNING) {
                flag.setFlag(Flag.RUNNING);
                if (sf.getTableName() != null) {
                    if (StringUtils.isNotEmpty(sf.getCommit_session())) {
                        // logger.warn("table:"+sf.getTableName()+" commit in process.  sessionId:"+sf.getCommit_session());
                        logger.warn("table:" + sf.getTableName() + " commit in process.  sessionId:" + sf.getCommit_session());
                    // logger.warn(sf.getTableName()+"����ִ��commit����  "+"   sessionId  "+sf.getCommit_session());
                    } else {
                        logger.warn("table:" + sf.getTableName() + " dump in process.  all records:" + sf.getTotalNum() + "  max need import num:" + sf.getMaxImportNum() + "  current import:" + sf.getImportNum());
                    }
                }
            }
            if (sf.getFlag() == DumpSuccessFlag.Flag.SUCCESS) {
                int importNum = 0;
                if (!StringUtils.isEmpty(sf.getImportNum())) {
                    importNum = Integer.parseInt(sf.getImportNum());
                }
                counters.incrCounter("group-" + tddlDumpContext.getGroup(), "counter-totalNum", importNum);
            // counters.incrCounter(TddlDump.Counter.DUMP_COMPLETE, Integer.parseInt(sf.getImportNum()));
            }
        }
        return flag;
    }

    public static void main(String[] args) throws IOException {
        TaskContext context = new TaskContext();
        String dump_config_parma = "{\"appName\":\"search4_165_indexcccccc\",\"cols\":[\"app_id\",\"project_name\",\"recept\",\"manager\",\"create_time\",\"update_time\",\"is_auto_deploy\",\"dpt_id\",\"dpt_name\",\"yunti_path\"],\"dataSizeEstimate\":100,\"dbs\":{\"JST_TERMINATORHOME_GROUP\":[\"application\"]},\"execType\":\"CREATE\",\"logicTableName\":\"application\",\"odpsConfig\":{\"accessId\":\"ZWtTdszB6Yz+23zYD3qf535CKCJ3NmbA\",\"accessKey\":\"Q66jOTIypNGS+TN9u04XzS9nxMRQupTHp348DF8mQvA=\",\"dailyPartition\":{\"key\":\"ps\",\"value\":\"20141119\"},\"datatunelEndPoint\":\"http://dt-corp.odps.aliyun-inc.com\",\"groupPartition\":\"share_mod\",\"project\":\"terminator\",\"serviceEndPoint\":\"http://service-corp.odps.aliyun-inc.com/api\",\"shallIgnorPartition\":false},\"shareId\":\"create_time\",\"taskId\":797,\"tddlAppName\":\"JST_TERMHOME_APP\",\"type\":\"tddl\"}";
        String dump_cols_trans_parma = "app_id Bigint,create_time Bigint,dpt_id Bigint,dpt_name String,is_auto_deploy String,manager String,project_name String,recept String,update_time Bigint,yunti_path String";
        String dump_cols_dump_parma = "app_id int,create_time datetime,dpt_id int,dpt_name varchar,is_auto_deploy char,manager varchar,project_name varchar,recept varchar,update_time timestamp,yunti_path varchar";
        String group_now = "JST_TERMINATORHOME_GROUP";
        String sessionNum = "1";
        context.setUserParam("dump.config.parma", dump_config_parma);
        context.setUserParam("dump.cols.trans.parma", dump_cols_trans_parma);
        context.setUserParam("dump.cols.dump.parma", dump_cols_dump_parma);
        context.setUserParam("sessionNum", sessionNum);
        context.setUserParam("group_now", group_now);
        TaskReturn return1 = new TddlDump().map(context);
        System.out.println(return1.getReturnCode());
    }
}
