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
package com.qlangtech.tis.order.dump.biz.search4waitInstance;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.sql.DataSource;
import com.qlangtech.tis.build.jobtask.TaskContext;
import junit.framework.TestCase;

/* *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class TestTableDumpTask extends TestCase {

    public void testTableRowCount() throws Exception {
        TableDumpTask dumpTask = new TableDumpTask();
        TaskContext context = new TaskContext();
        dumpTask.initialSpringContext(context);
        DataSource datasource = null;
        Connection conn = null;
        Statement statement = null;
        ResultSet result = null;
        long sum = 0;
        for (int i = 1; i <= 128; i++) {
            datasource = (DataSource) dumpTask.springContext.getBean("order" + i);
            conn = datasource.getConnection();
            statement = conn.createStatement();
            result = statement.executeQuery("select count(1) from waitingorderdetail");
            if (result.next()) {
                sum += result.getInt(1);
            // System.out.println(i + "," + );
            }
            System.out.println("sum:" + sum);
            result.close();
            statement.close();
            conn.close();
        }
    }
}
