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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.taobao.tddl.group.jdbc.TGroupDataSource;
import com.qlangtech.tis.build.task.Task;
import com.qlangtech.tis.indexbuilder.bean.TddlDumpContext;
import com.qlangtech.tis.indexbuilder.exception.MyException;
import com.qlangtech.tis.indexbuilder.map.TddlDump;
import com.qlangtech.tis.indexbuilder.utils.TddlUtil;
import com.qlangtech.tis.manage.common.trigger.sources.TddlTaskConfig;

/* *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class TddlReader {

    public static final Logger logger = LoggerFactory.getLogger(TddlReader.class);

    protected TddlDumpContext tddlDumpContext;

    protected TGroupDataSource ds;

    protected Connection conn;

    protected PreparedStatement ps;

    protected ResultSet rs;

    // private List<Map<String, String>> rows;
    private Map<String, String> row;

    protected Long maxDumpCount;

    protected String tableName;

    protected int totalNum;

    // dpt_id Bigint,name String,gmt_modified Bigint,full_name String
    // private String dump_cols_parma;
    // dpt_id,name,gmt_modified,full_name
    private String dump_cols_value;

    public void cols_parse(TddlDumpContext tddlDumpContext) {
        try {
            StringBuilder builder = new StringBuilder();
            Map<String, String> dump_cols_dump_map = TddlUtil.str2Map(tddlDumpContext.getDump_cols_dump_parma());
            for (Entry<String, String> entry : dump_cols_dump_map.entrySet()) {
                String new_colum = TddlUtil.columTrans(entry.getKey(), entry.getValue());
                builder.append(new_colum).append(",");
            }
            dump_cols_value = builder.substring(0, builder.length() - 1);
        } catch (Exception e) {
            logger.error("cols_parse()  dump_cols_dump_parma :fail!!!!  " + ExceptionUtils.getFullStackTrace(e));
            throw new MyException("cols_parse()  dump_cols_dump_parma :fail!!!!  " + ExceptionUtils.getFullStackTrace(e));
        }
    }

    public TddlReader(TddlDumpContext tddlDumpContext, String tableName) {
        super();
        logger.warn("��ʼ��ʼ��tddlReader");
        this.tddlDumpContext = tddlDumpContext;
        this.ds = tddlDumpContext.getDs();
        this.conn = tddlDumpContext.getConn();
        this.tableName = tableName;
        cols_parse(tddlDumpContext);
        this.maxDumpCount = tddlDumpContext.getTaskConfig().getMaxDumpCount();
        opensource();
        executeQuery();
        logger.warn("init tddlReader success  get Table:" + totalNum + "  records");
    }

    public void opensource() {
        try {
            StringBuilder sqlbulBuilder = new StringBuilder();
            sqlbulBuilder.append("select count(*) from ").append(tableName);
            PreparedStatement ps = conn.prepareStatement(sqlbulBuilder.toString());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                totalNum = Integer.parseInt(filter(rs.getString(1)));
            }
            if (totalNum > 0) {
                tddlDumpContext.getCounters().setCounterValue(Task.Counter.MAP_ALL_RECORDS, totalNum);
            }
        } catch (SQLException e) {
            logger.error("opensource()  ��ȡ��" + tableName + "������ʱ����    " + e);
            throw new MyException("opensource()  read table " + tableName + " total records fail!!!!   " + ExceptionUtils.getFullStackTrace(e));
        }
    }

    public void executeQuery() {
        try {
            StringBuilder builder = new StringBuilder();
            builder.append("select " + dump_cols_value + " from ").append(tableName);
            ps = conn.prepareStatement(builder.toString());
            rs = ps.executeQuery();
        } catch (Exception e) {
            logger.error("executeQuery() " + tableName + "��ʱ�����    " + e);
            throw new MyException("executeQuery() " + tableName + "��ʱ�����    " + e);
        }
    }

    // public boolean hasNext(){
    // //�ж����ڵ������Ƿ������С
    // if(startNum<totalNum){
    // return true;
    // }
    // 
    // return false;
    // 
    // }
    public void closeSource() {
        try {
            rs.close();
            ps.close();
            conn.close();
        } catch (SQLException e) {
            logger.error("�ر���Դ��ʱ�����Exception" + e);
            throw new MyException("�ر���Դ��ʱ�����Exception" + e);
        }
    }

    public Map<String, String> next(int line) {
        if (row != null) {
            row = null;
        }
        String key = null;
        String value = null;
        try {
            row = new LinkedHashMap<String, String>();
            int count = rs.getMetaData().getColumnCount();
            for (int k = 1; k <= count; k++) {
                key = rs.getMetaData().getColumnLabel(k);
                value = filter(rs.getString(k));
                row.put(key, value != null ? value : "");
            }
        } catch (Exception e) {
            // logger.error(Lg.c(taskid, "read table:"+tableName+" line:"+line+"  Exception!!!"+e));
            throw new MyException("read table:" + tableName + " line:" + line + "  Exception!!!" + e);
        }
        return row;
    }

    // public List<Map<String, String>> next(){
    // if (rows!=null){
    // rows = null;
    // }
    // if(temp!=null){
    // temp=null;
    // }
    // rows=new ArrayList<Map<String,String>>();
    // 
    // PreparedStatement ps = null;
    // ResultSet rs = null;
    // 
    // String key = null;
    // String value = null;
    // try {
    // StringBuilder builder=new StringBuilder();
    // builder.append("select "+dump_cols_value+" from ").append(tableName).append("  limit ").append(startNum).append(" , ").append(maxDumpCount);
    // System.out.println(builder);
    // ps= conn.prepareStatement(builder.toString());
    // rs= ps.executeQuery();
    // while (rs.next()) {
    // temp=new LinkedHashMap<String, String>(maxDumpCount);
    // int count = rs.getMetaData().getColumnCount();
    // for (int k = 1; k <= count; k++) {
    // 
    // key = rs.getMetaData().getColumnLabel(k);
    // value = filter(rs.getString(k));
    // //					System.out.println("[" + rs.getMetaData().getTableName(i) + "." + key+ "->" + value + "]");
    // temp.put(key, value != null ? value : "");
    // 
    // }
    // rows.add(temp);
    // temp=null;
    // }
    // 
    // } catch (Exception e) {
    // logger.error("��ȡ��"+tableName+"��"+startNum+"-------->"+(startNum+maxDumpCount)+"������ʱ����Exception"+e);
    // throw new MyException("��ȡ��"+tableName+"��"+startNum+"-------->"+(startNum+maxDumpCount)+"������ʱ����Exception"+e);
    // }finally{
    // try {
    // rs.close();
    // ps.close();
    // } catch (SQLException e) {
    // logger.error("��ȡ��"+tableName+"��"+startNum+"-------->"+(startNum+maxDumpCount)+"��������ر���Դʱ����Exception"+e);
    // throw new MyException("��ȡ��"+tableName+"��"+startNum+"-------->"+(startNum+maxDumpCount)+"��������ر���Դʱ����Exception"+e);
    // }
    // 
    // }
    // 
    // startNum=startNum+maxDumpCount;
    // return rows;
    // 
    // }
    public static String filter(String input) {
        if (input == null) {
            return input;
        }
        StringBuffer filtered = new StringBuffer(input.length());
        char c;
        for (int i = 0; i <= input.length() - 1; i++) {
            c = input.charAt(i);
            switch(c) {
                case '\t':
                    break;
                case '\r':
                    break;
                case '\n':
                    break;
                default:
                    filtered.append(c);
            }
        }
        return (filtered.toString());
    }
}
