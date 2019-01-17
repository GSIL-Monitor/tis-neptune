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
package com.qlangtech.tis.fullbuild.taskflow.odps;

import com.qlangtech.tis.fullbuild.taskflow.AdapterTask;

/* *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class ODPSTask extends AdapterTask {

    // private Odps odps;
    /**
     * @param sql
     */
    protected void executeSql(String taskname, String sql) {
    // int allworkds = 0;
    // // System.out.println(writer.toString());
    // try {
    // 
    // System.out.println(sql);
    // 
    // Instance instance = SQLTask.run(odps, sql);
    // 
    // Status status;// = instance.getStatus();
    // 
    // // 等待执行完成
    // do {
    // Thread.sleep(3000);
    // status = instance.getStatus();
    // StringBuffer processSummary = new StringBuffer();
    // int tcount = 0;
    // int tworks = 0;
    // int allcount = 0;
    // 
    // for (String name : instance.getTaskNames()) {
    // processSummary.append("\ntskname:").append(name);
    // for (StageProgress progress : instance
    // .getTaskProgress(name)) {
    // processSummary.append(",name:" + progress.getName());
    // tworks = progress.getTerminatedWorkers();
    // tcount += tworks;
    // processSummary.append(",tworkers:" + tworks);
    // allworkds = progress.getTotalWorkers();
    // allcount += allworkds;
    // processSummary.append(",totleworks:" + allworkds)
    // .append("");
    // }
    // }
    // 
    // if (allcount > 0) {
    // System.out.print("percent:" + ((float) tcount / allcount)
    // + ",");
    // }
    // System.out.println((StringUtils.isNotBlank(taskname) ? ("task:"
    // + taskname + ",") : StringUtils.EMPTY)
    // + "status:"
    // + status
    // + ",Summary:"
    // + processSummary.toString());
    // 
    // } while (status != Status.TERMINATED);
    // 
    // if (!instance.isSuccessful()) {
    // throw new IllegalStateException("taskname:" + taskname
    // + " is faild," + sql);
    // }
    // 
    // } catch (Exception e) {
    // throw new RuntimeException("sql:" + sql, e);
    // }
    }
    // public Odps getOdps() {
    // return odps;
    // }
    // 
    // public void setOdps(Odps odps) {
    // this.odps = odps;
    // }
}
