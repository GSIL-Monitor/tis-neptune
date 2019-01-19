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
package com.qlangtech.tis.hdfs.client.data.sql;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import com.qlangtech.tis.exception.TimeManageException;
import com.qlangtech.tis.hdfs.client.time.TimeProvider;
import com.qlangtech.tis.hdfs.client.time.TimeProvider.StartAndEndTime;

/*
 * @description 该类主要针对SQL中导入HDFS起始结束进行替换<br>
 *              原来终搜有默认基于保存ZK的实现，但是为了隔离业务方、减少自身维护成本的目的，<br>
 *              我们将某些参数，eg:导入HDFS集群的起始结束时间点的参数交给业务方系统terminator-time.
 *              property文件维护 <br>
 * @since 2011-8-3 下午07:43:17
 * @version 1.0
 *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class SqlFunctionCollectors {

    private Map<String, SqlFunction> functions = null;

    public String allDumpStartTime = "2000-01-01 00:00:00";

    public void setTimerManager(TimeProvider timerManager) {
        this.timerManager = timerManager;
    }

    protected TimeProvider timerManager;

    public void initDefaultFunctions() {
        this.register(new LastModifiedFuncion());
        this.register(new StartDateFunction());
        this.register(new EndDateFunction());
        this.register(new NowFunction());
        this.register(new Month2BeforeFunction());
        this.register(new Days2BeforeFunction());
        this.register(new Month6BeforeFunction());
    }

    public SqlFunction register(SqlFunction function) {
        if (functions == null) {
            functions = new HashMap<String, SqlFunction>();
        }
        return functions.put(function.getPlaceHolderName(), function);
    }

    public String parseSql(String sql) {
        Iterator<String> i = SqlUtils.parseFunctions(sql);
        if (i == null) {
            return sql;
        }
        while (i.hasNext()) {
            String funcName = i.next();
            SqlFunction function = functions.get(funcName);
            if (function == null) {
                throw new RuntimeException("没有定义的SQL的Function  ==>  ==> " + funcName);
            }
            String placeHolderName = function.getPlaceHolderName();
            String value = function.getValue();
            sql = sql.replace(SqlUtils.PLACE_HOLDER_CHAR + placeHolderName + SqlUtils.PLACE_HOLDER_CHAR, value);
        }
        return sql;
    }

    public String getValue(String name) {
        SqlFunction func = this.functions.get(name);
        if (func == null)
            throw new NullPointerException("不包含名为=> " + name + "的函数");
        return func.getValue();
    }

    public class Month2BeforeFunction implements SqlFunction {

        private final String month3BeforeTimestamp;

        public Month2BeforeFunction() {
            super();
            Calendar c = Calendar.getInstance();
            c.setTime(new Date());
            c.add(Calendar.DAY_OF_YEAR, -60);
            this.month3BeforeTimestamp = (String.valueOf(c.getTimeInMillis() / 1000));
        }

        @Override
        public String getPlaceHolderName() {
            return "month2Before";
        }

        @Override
        public String getValue() {
            return this.month3BeforeTimestamp;
        }
    }

    public class Month6BeforeFunction implements SqlFunction {

        private final String month6Before;

        public Month6BeforeFunction() {
            super();
            Calendar c = Calendar.getInstance();
            c.setTime(new Date());
            c.add(Calendar.DAY_OF_YEAR, -180);
            this.month6Before = (String.valueOf(c.getTimeInMillis() / 1000));
        }

        @Override
        public String getPlaceHolderName() {
            return "month6Before";
        }

        @Override
        public String getValue() {
            return this.month6Before;
        }
    }

    public class Days2BeforeFunction implements SqlFunction {

        private final String days2Before;

        public Days2BeforeFunction() {
            super();
            Calendar c = Calendar.getInstance();
            c.setTime(new Date());
            c.add(Calendar.DAY_OF_YEAR, -2);
            this.days2Before = (String.valueOf(c.getTimeInMillis() / 1000));
        }

        @Override
        public String getPlaceHolderName() {
            return "days2Before";
        }

        @Override
        public String getValue() {
            return this.days2Before;
        }
    }

    public class LastModifiedFuncion implements SqlFunction {

        @Override
        public String getPlaceHolderName() {
            return "lastModified";
        }

        @Override
        public String getValue() {
            StartAndEndTime times = null;
            Date date = null;
            try {
                times = timerManager.justGetTimes();
                date = times.startTime;
            } catch (com.qlangtech.tis.exception.TimeManageException e) {
                // TODO Auto-generated catch block
                date = new Date();
            }
            return SqlUtils.parseDate(date);
        }
    }

    public class StartDateFunction implements SqlFunction {

        @Override
        public String getPlaceHolderName() {
            return "startDate";
        }

        @Override
        public String getValue() {
            StartAndEndTime times = null;
            try {
                times = timerManager.justGetTimes();
            } catch (TimeManageException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return SqlUtils.parseDate(times.startTime);
        }
    }

    public class EndDateFunction implements SqlFunction {

        @Override
        public String getPlaceHolderName() {
            return "endDate";
        }

        @Override
        public String getValue() {
            // times = ZKTimeManager.getInstance(serviceName).justGetTimes();
            StartAndEndTime times = null;
            try {
                times = timerManager.justGetTimes();
            } catch (TimeManageException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return SqlUtils.parseDate(times.endTime);
        }
    }

    public class NowFunction implements SqlFunction {

        @Override
        public String getPlaceHolderName() {
            return "now";
        }

        @Override
        public String getValue() {
            return SqlUtils.parseDate(new Date());
        }
    }

    public static void main(String[] args) {
        SqlFunctionCollectors collectors = new SqlFunctionCollectors();
        collectors.initDefaultFunctions();
        System.out.println(collectors.parseSql("select * from aaa where a < $month3Before$"));
        System.out.println(new Date(1478338466036l));
    }
}
