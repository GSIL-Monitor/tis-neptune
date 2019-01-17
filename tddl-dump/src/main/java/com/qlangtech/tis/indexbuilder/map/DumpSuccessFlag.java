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

import java.util.concurrent.atomic.AtomicInteger;

/* *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class DumpSuccessFlag {

    public enum Flag {

        RUNNING, SUCCESS, KILL, FAILURE
    }

    private Flag flag = Flag.RUNNING;

    // ��ǰ����ı���
    private String tableName;

    // ��ǰ��Ҫ��������������
    private String totalNum;

    // ÿ�ŷֱ��Ѿ�������
    private String importNum;

    // ���ÿ�ű���Ҫ������
    private String maxImportNum;

    private String commit_session;

    private int maxErrorNum = 500;

    private AtomicInteger errorCount = new AtomicInteger();

    public static void main(String[] args) {
        System.out.println(new DumpSuccessFlag().getErrorCount());
    }

    public int getMaxErrorNum() {
        return maxErrorNum;
    }

    public void setMaxErrorNum(int maxErrorNum) {
        this.maxErrorNum = maxErrorNum;
    }

    public AtomicInteger getErrorCount() {
        return errorCount;
    }

    public void setErrorCount(AtomicInteger errorCount) {
        this.errorCount = errorCount;
    }

    public String getMaxImportNum() {
        return maxImportNum;
    }

    public void setMaxImportNum(String maxImportNum) {
        this.maxImportNum = maxImportNum;
    }

    public String getCommit_session() {
        return commit_session;
    }

    public void setCommit_session(String commit_session) {
        this.commit_session = commit_session;
    }

    private String process;

    public String getProcess() {
        return process;
    }

    public void setProcess(String process) {
        this.process = process;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getTotalNum() {
        return totalNum;
    }

    public void setTotalNum(String totalNum) {
        this.totalNum = totalNum;
    }

    public String getImportNum() {
        return importNum;
    }

    public void setImportNum(String importNum) {
        this.importNum = importNum;
    }

    public Flag getFlag() {
        return flag;
    }

    public void setFlag(Flag flag) {
        this.flag = flag;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    private String msg;
}
