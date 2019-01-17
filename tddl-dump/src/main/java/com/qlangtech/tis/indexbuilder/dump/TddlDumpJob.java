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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.hadoop.fs.ContentSummary;
import org.apache.hadoop.fs.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.taobao.tddl.group.jdbc.TGroupDataSource;
import com.qlangtech.tis.build.task.Task;
import com.qlangtech.tis.indexbuilder.bean.OdpsTunnelContext;
import com.qlangtech.tis.indexbuilder.bean.TddlDumpContext;
import com.qlangtech.tis.indexbuilder.dump.TddlMaker.BuildResult;
import com.qlangtech.tis.indexbuilder.exception.MyException;
import com.qlangtech.tis.indexbuilder.map.DumpSuccessFlag;
import com.qlangtech.tis.indexbuilder.map.TddlDump;
import com.qlangtech.tis.manage.common.trigger.sources.TddlTaskConfig;

/* *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class TddlDumpJob implements Runnable {

    public static final Logger logger = LoggerFactory.getLogger(TddlDumpJob.class);

    private TddlDumpContext tddlDumpContext;

    // private OdpsMaker odpsAccount;
    // private TGroupDataSource ds;
    // private Connection conn;
    private String tableName;

    // private String dump_cols_parma;
    // private TddlTaskConfig taskConfig;
    private TddlReader reader;

    // private boolean isComplete=false;
    private DumpSuccessFlag successFlag = new DumpSuccessFlag();

    private StringBuilder errorStr = new StringBuilder();

    public DumpSuccessFlag getSuccessFlag() {
        return successFlag;
    }

    public void setSuccessFlag(DumpSuccessFlag successFlag) {
        this.successFlag = successFlag;
    }

    @Override
    public void run() {
        submit();
    }

    public void submit() {
        int importNum = 0;
        Map<String, String> row = new HashMap<String, String>();
        successFlag.setTableName(reader.tableName);
        successFlag.setTotalNum(String.valueOf(reader.totalNum));
        successFlag.setImportNum("0");
        while (true) {
            try {
                successFlag.setFlag(DumpSuccessFlag.Flag.RUNNING);
                importNum++;
                row.clear();
                if (reader.maxDumpCount == null || reader.maxDumpCount == 0) {
                    successFlag.setMaxImportNum("N");
                } else {
                    successFlag.setMaxImportNum(String.valueOf(reader.maxDumpCount));
                }
                if ((reader.maxDumpCount == null || reader.maxDumpCount == 0 || importNum < reader.maxDumpCount) && reader.rs.next()) {
                    row = reader.next(importNum);
                    successFlag.setImportNum(String.valueOf(importNum));
                    if (row == null || row.size() < 1) {
                        break;
                    }
                    tddlDumpContext.getCounters().setCounterValue(Task.Counter.MAP_INPUT_RECORDS, importNum);
                    tddlDumpContext.getOdpsAccount().upload_data(row);
                } else {
                    reader.closeSource();
                    break;
                }
            } catch (Exception e) {
                logger.error("submit() row fail!!!!   row:" + row + "    error:" + ExceptionUtils.getFullStackTrace(e));
                successFlag.getErrorCount().incrementAndGet();
                int totalErrorNum = Integer.parseInt(String.valueOf(successFlag.getErrorCount()));
                if (totalErrorNum <= 5) {
                    errorStr.append("	row:" + row + "\n" + "errorInfo:" + e + "  \n");
                }
                if (totalErrorNum >= successFlag.getMaxErrorNum()) {
                    logger.error("ʧ�������ﵽMaxErrorNum:" + successFlag.getMaxErrorNum() + "  submit() row fail!!!!");
                    successFlag.setMsg("\n" + "dumpCenter submit() import data fail!!!!" + "\n" + "the error num reach the need maxErrorNum  is:" + successFlag.getMaxErrorNum() + "\n" + "please check the data format!!!!!!" + "\n" + errorStr);
                    reader.closeSource();
                    successFlag.setFlag(DumpSuccessFlag.Flag.FAILURE);
                    return;
                }
            }
        }
        // ��������
        logger.warn(tableName + "������е�����д��ܵ���ɣ��ȴ��ύ");
        commitAll();
        // ��������
        logger.warn(tableName + "������е����ݵ������, " + " db��������:" + successFlag.getTotalNum() + "  �������������:" + successFlag.getMaxImportNum() + "  �ѵ�������:" + successFlag.getImportNum() + "  ʧ������" + successFlag.getErrorCount());
    }

    private void commitAll() {
        try {
            // �����ύ
            Map<Integer, OdpsTunnelContext> sessionMap = tddlDumpContext.getOdpsTunnelMap();
            for (Entry<Integer, OdpsTunnelContext> entry : sessionMap.entrySet()) {
                int sessionId = entry.getKey();
                OdpsTunnelContext odpsTunnelContext = entry.getValue();
                successFlag.setCommit_session(String.valueOf(sessionId));
                tddlDumpContext.getOdpsAccount().commit(odpsTunnelContext);
            }
            logger.warn(tableName + "������е������ύ���");
            successFlag.setFlag(DumpSuccessFlag.Flag.SUCCESS);
        } catch (Exception e) {
            logger.error("commit() error  " + e);
            successFlag.setMsg("commit() error" + e);
            successFlag.setFlag(DumpSuccessFlag.Flag.FAILURE);
        }
    }

    public TddlDumpJob(TddlDumpContext tddlDumpContext, String tableName) {
        super();
        this.tddlDumpContext = tddlDumpContext;
        this.tableName = tableName;
        reader = new TddlReader(tddlDumpContext, tableName);
        // tddlDumpContext.getTaskContext().getCounters().incrCounter(
        // "group-"+tddlDumpContext.getGroup(), "counter-totalNum-"+tableName, reader.totalNum);
        successFlag.setFlag(DumpSuccessFlag.Flag.RUNNING);
    }
}
