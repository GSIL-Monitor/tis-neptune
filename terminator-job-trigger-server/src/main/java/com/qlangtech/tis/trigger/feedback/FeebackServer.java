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

import java.io.IOException;
import java.util.Date;
import java.util.List;
import junit.framework.Assert;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.ipc.RPC;
import org.apache.hadoop.ipc.RPC.Server;
import org.springframework.beans.factory.InitializingBean;
import com.qlangtech.tis.trigger.biz.dal.dao.IJobMetaDataDAO;
import com.qlangtech.tis.trigger.biz.dal.dao.ITaskDAO;
import com.qlangtech.tis.trigger.biz.dal.dao.ITerminatorTriggerBizDalDAOFacade;
import static com.qlangtech.tis.trigger.biz.dal.dao.JobConstant.*;
import com.qlangtech.tis.trigger.biz.dal.dao.TriggerJob;
import com.qlangtech.tis.trigger.biz.dal.pojo.Task;
import com.qlangtech.tis.trigger.biz.dal.pojo.TaskCriteria;
import com.qlangtech.tis.trigger.biz.dal.pojo.TaskExecLog;
import com.qlangtech.tis.trigger.socket.InfoType;

/* *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class FeebackServer implements IJobFeedback, InitializingBean {

    private Server rpcServer;

    private static final Log log = LogFactory.getLog(FeebackServer.class);

    public FeebackServer() {
        super();
    }

    private int exportPort;

    @Override
    public void afterPropertiesSet() throws Exception {
    // rpcServer = RPC.getServer(this, "localhost", exportPort,
    // new Configuration());
    // rpcServer.start();
    // rpcServer.join();
    }

    public int getExportPort() {
        return exportPort;
    }

    public void setExportPort(int exportPort) {
        this.exportPort = exportPort;
    }

    private void log(InfoType infoType, String serviceName, long timestamp, String msg) {
        TriggerJob job = this.getJobMetaDataDAO().queryJob(serviceName, (int) JOB_TYPE_FULL_DUMP);
        if (job == null) {
            throw new IllegalStateException("appName:" + serviceName + " timestamp:" + timestamp + " has not corresponding service defination in db");
        }
        TaskCriteria criteria = new TaskCriteria();
        criteria.createCriteria().andDomainEqualTo(DOMAIN_TERMINAOTR).andJobIdEqualTo(job.getJobId()).andGmtCreateInSameDay(new Date(timestamp));
        List<Task> taskList = this.getTaskDAO().selectByExample(criteria, 1, 100);
        Long taskId = null;
        for (Task task : taskList) {
            taskId = task.getTaskId();
        }
        Assert.assertNotNull("taskid can not be null", taskId);
        TaskExecLog execLog = new TaskExecLog();
        execLog.setDomain(DOMAIN_TERMINAOTR);
        execLog.setFromIp(Server.getRemoteAddress());
        execLog.setGmtCreate(new Date());
        execLog.setLogContent(msg);
        execLog.setTaskId(taskId);
        execLog.setInfoType(infoType.toString());
        this.getTriggerBizDAO().getTaskExecLogDAO().insertSelective(execLog);
        log.info("InfoType:" + infoType + "  serviceName:" + serviceName + " timestamp:" + timestamp + " msg:" + msg);
    }

    @Override
    public void error(String serviceName, long timestamp, String msg) {
        this.log(InfoType.ERROR, serviceName, timestamp, msg);
    }

    @Override
    public void fatal(String serviceName, long timestamp, String msg) {
        this.log(InfoType.FATAL, serviceName, timestamp, msg);
    }

    @Override
    public void info(String serviceName, long timestamp, String msg) {
        this.log(InfoType.INFO, serviceName, timestamp, msg);
    }

    public static void main(String[] arg) {
        FeebackServer server = new FeebackServer();
    }

    private ITaskDAO getTaskDAO() {
        return getTriggerBizDAO().getTaskDAO();
    }

    public void dispose() {
        this.rpcServer.stop();
    }

    // 
    // public void setTaskDAO(ITaskDAO taskDAO) {
    // this.taskDAO = taskDAO;
    // }
    private IJobMetaDataDAO jobMetaDataDAO;

    // private ITaskDAO taskDAO;
    private ITerminatorTriggerBizDalDAOFacade triggerBizDAO;

    public ITerminatorTriggerBizDalDAOFacade getTriggerBizDAO() {
        return triggerBizDAO;
    }

    public void setTriggerBizDAO(ITerminatorTriggerBizDalDAOFacade triggerBizDAO) {
        this.triggerBizDAO = triggerBizDAO;
    }

    public IJobMetaDataDAO getJobMetaDataDAO() {
        return jobMetaDataDAO;
    }

    public void setJobMetaDataDAO(IJobMetaDataDAO jobMetaDataDAO) {
        this.jobMetaDataDAO = jobMetaDataDAO;
    }
}
