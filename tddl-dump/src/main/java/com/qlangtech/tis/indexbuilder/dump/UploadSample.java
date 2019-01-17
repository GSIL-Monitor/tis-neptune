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

import java.io.IOException;
import java.util.Date;
import com.alibaba.odps.tunnel.Column;
import com.alibaba.odps.tunnel.Configuration;
import com.alibaba.odps.tunnel.DataTunnel;
import com.alibaba.odps.tunnel.Download;
import com.alibaba.odps.tunnel.RecordSchema;
import com.alibaba.odps.tunnel.TunnelException;
import com.alibaba.odps.tunnel.Download.Status;
import com.alibaba.odps.tunnel.io.Record;
import com.alibaba.odps.tunnel.io.RecordReader;

/* *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class UploadSample {

    public static void main(String[] args) throws TunnelException, IOException {
        Configuration cfg = new Configuration("uJOQ6tdGYhwHmrWh", "28lrYJe6CyxWRySFF5fXCsg6HMLqe9", "dt.odps.aliyun-inc.com");
        DataTunnel tunnel = new DataTunnel(cfg);
        Download down = null;
        try {
            down = tunnel.createDownload("IFD", "s_feed_individu", "ds=20141031,mod=15");
            String id = down.getDownloadId();
            System.out.println("DownloadId = " + id);
            RecordSchema schema = down.getSchema();
            System.out.println("Schema is: " + schema.toJsonString());
            Status status = down.getStatus();
            System.out.println("Status is: " + status.toString());
            long count = down.getRecordCount();
            System.out.println("RecordCount is: " + count);
            RecordReader reader = down.openRecordReader(0, count);
            Record r = new Record(schema.getColumnCount());
            int i = 0;
            while ((r = reader.read()) != null) {
                if (i++ == 100) {
                    Thread.sleep(1200000);
                }
                consumeRecord(r, schema);
            }
        } catch (TunnelException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            down.complete();
        }
    }

    private static void consumeRecord(Record r, RecordSchema schema) {
        for (int i = 0; i < schema.getColumnCount(); i++) {
            Column.Type t = schema.getColumnType(i);
            String colValue = null;
            switch(t) {
                case ODPS_BIGINT:
                    {
                        Long v = r.getBigint(i);
                        colValue = v == null ? null : v.toString();
                        break;
                    }
                case ODPS_DOUBLE:
                    {
                        Double v = r.getDouble(i);
                        colValue = v == null ? null : v.toString();
                        break;
                    }
                case ODPS_DATETIME:
                    {
                        Date v = r.getDatetime(i);
                        colValue = v == null ? null : v.toString();
                        break;
                    }
                case ODPS_BOOLEAN:
                    {
                        Boolean v = r.getBoolean(i);
                        colValue = v == null ? null : v.toString();
                        break;
                    }
                case ODPS_STRING:
                    String v = r.getString(i);
                    colValue = v == null ? null : v.toString();
                    break;
                default:
                    throw new RuntimeException("Unknown column type: " + t);
            }
            System.out.print(colValue == null ? "null" : colValue);
            if (i != schema.getColumnCount())
                System.out.print("\t");
        }
        System.out.println();
    }
}
