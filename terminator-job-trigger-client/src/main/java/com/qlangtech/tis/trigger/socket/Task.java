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
package com.qlangtech.tis.trigger.socket;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.qlangtech.tis.trigger.util.NIOUtils;

/* *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public abstract class Task implements Feedback {

    private static final Log log = LogFactory.getLog(Task.class);

    // task 是否正在工作？
    private final AtomicBoolean fullDoing = new AtomicBoolean(false);

    private final AtomicBoolean incrDoing = new AtomicBoolean(false);

    public Task() {
        super();
    }

    /**
     * 查看本地服务器是否拿到执行任务的锁 20121030 百岁
     *
     * @return
     */
    public abstract boolean shallExecute();

    public void execute(TaskContext context) {
        log.warn(" task start context.getJobId()" + context.getJobId() + " context.getTaskId():" + context.getTaskId());
        try {
            if (isPreTaskWorking(context)) {
                log.warn("task is working!!!");
                this.sendError(context, "per task is work!!! the current task is tnterrupt");
                return;
            }
            // 查看本地是否得到锁
            if (!shallExecute()) {
                log.warn("has not get the execute lock");
                this.sendError(context, "has not get the execute lock");
                return;
            }
            this.startRun(context);
            if (context.isFullJob()) {
                log.warn("start to execute full task:" + context.getTaskId());
                this.executeFull(context);
            } else if (context.isIncrJob()) {
                log.warn("start to execute incr task:" + context.getTaskId());
                this.executeIncr(context);
            } else {
                throw new IllegalStateException("jobid:" + context.getJobId() + " neither match the full job id:" + context.getTriggerJob().getFulljobid() + " incr job id:" + context.getTriggerJob().getIncrjobid());
            }
        // this.process(context);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            this.sendError(context, e);
        } finally {
            getDoingLock(context).set(false);
            this.endRun(context);
        }
    }

    protected final boolean isPreTaskWorking(TaskContext context) {
        return !getDoingLock(context).compareAndSet(false, true);
    }

    private final AtomicBoolean getDoingLock(TaskContext context) {
        return context.isFullJob() ? fullDoing : incrDoing;
    }

    @Override
    public void sendError(TaskContext context, String msg, Exception e) {
        StringWriter writer = new StringWriter();
        if (msg != null) {
            writer.append(msg + "\n");
        }
        e.printStackTrace(new PrintWriter(writer));
        // ExecuteState state = ExecuteState.create(InfoType.ERROR, writer
        // .toString());
        // state.setJobId(jobId);
        // out.writeObject(state);
        // } catch (Exception e1) {
        // throw new RuntimeException(e1);
        // }
        // }
        sendMessage(InfoType.ERROR, context, writer.toString());
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.taobao.terminator.trigger.socket.Feedback#sendError(com.taobao.terminator
	 * .trigger.socket.TaskContext, java.lang.Exception)
	 */
    public void sendError(TaskContext context, Exception e) {
        sendError(context, null, e);
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.taobao.terminator.trigger.socket.Feedback#sendError(com.taobao.terminator
	 * .trigger.socket.TaskContext, java.lang.String)
	 */
    public void sendError(TaskContext context, String message) {
        sendMessage(InfoType.ERROR, context, message);
    }

    public static void sendMessage(InfoType infotype, TaskContext context, String message) {
        try {
            synchronized (context.getChannel()) {
                ExecuteState<String> state = ExecuteState.create(LogType.FULL, message);
                // 标明这个是client传输的
                state.setComponent("client");
                state.setJobId(context.getJobId());
                state.setTaskId(context.getTaskId());
                state.setServiceName(context.getServiceName());
                NIOUtils.writeObject(context.getChannel(), state);
            }
        } catch (Throwable e1) {
            // 将异常吃掉
            // throw new RuntimeException(e1);
            log.warn(e1.getMessage(), e1);
        }
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.taobao.terminator.trigger.socket.Feedback#sendInfo(com.taobao.terminator
	 * .trigger.socket.TaskContext, java.lang.String)
	 */
    public void sendInfo(TaskContext context, String message) {
        sendMessage(InfoType.INFO, context, message);
    }

    /**
     * @param context
     * @throws Exception
     */
    protected abstract void executeFull(TaskContext context) throws Exception;

    protected abstract void executeIncr(TaskContext context) throws Exception;

    /**
     * 执行任务
     *
     * @param context
     */
    // protected abstract void process(TaskContext context) throws Exception;
    /**
     * 开始执行任务
     *
     * @param context
     */
    private void startRun(TaskContext context) {
        sendMessage(InfoType.INFO, context, "task start");
    }

    /**
     * 任务执行结束
     *
     * @param context
     * @throws Exception
     */
    private void endRun(TaskContext context) {
        sendMessage(InfoType.INFO, context, "task end");
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
