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
import java.util.ArrayList;
import java.util.List;
import org.apache.hadoop.conf.Configuration;

/* *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class Hbase {

    private static final long serialVersionUID = 1L;

    private static final int MAX_TABLE_COUNT = 10;

    private static final String TAB_NAME = "terminator_tsearcher_servicefeedback_log";
    // public static void main(String[] arg) throws Exception {
    // 
    // System.out.println("hello1234=========================");
    // Configuration conf = HBaseConfiguration.create();
    // System.out.println("2");
    // // <property>
    // // <name>hbase.zookeeper.quorum</name>
    // // <value>10.98.108.25,10.98.108.26,10.98.108.27</value>
    // // </property>
    // // <property>
    // // <name>zookeeper.znode.parent</name>
    // // <value>/hbase-perf</value>
    // // </property>
    // conf.set("zookeeper.znode.parent", "/hbase-perf");
    // conf.set("hbase.zookeeper.quorum",
    // "10.98.108.25,10.98.108.26,10.98.108.27");
    // 
    // // conf.addResource(new Path(
    // // "/com/taobao/terminator/trigger/utils/hbase-site.xml"));
    // // InputStream reader =
    // // Hbase.class.getClassLoader().getResourceAsStream(
    // // "com/taobao/terminator/trigger/utils/hbase-site.xml");
    // //
    // // // System.out.println(IOUtils.toString(reader));
    // // conf.addResource(reader);
    // 
    // // conf
    // // .addResource(new Path(
    // // "D:\\tmp\\terminator-job-trigger\\terminator-job-trigger-server\\src\\main\\resources\\com\\taobao\\terminator\\trigger\\utils\\hbase-site.xml"));
    // 
    // System.out.println("3");
    // HBaseAdmin hAdmin = new HBaseAdmin(conf);
    // System.out.println("4");
    // HTablePool pool = new HTablePool(conf, MAX_TABLE_COUNT);
    // // IOUtils.closeQuietly(reader);
    // System.out.println("5");
    // System.out.println("tableExists:" + hAdmin.tableExists(TAB_NAME));
    // 
    // HTableDescriptor tabDesc = hAdmin.getTableDescriptor(TAB_NAME
    // .getBytes());
    // // HColumnDescriptor
    // // Collection<HColumnDescriptor> columns = tabDesc.getFamilies();
    // // for (HColumnDescriptor c : columns) {
    // // System.out.println(new String(c.getName()));
    // // }
    // 
    // // putIfTableExist(hAdmin, pool, TAB_NAME, "F");
    // 
    // Scan scan = new Scan();
    // 
    // scan.setCaching(200);
    // scan.setCacheBlocks(false);// 关闭cache 防止gc
    // 
    // // if (logLevel == null) {
    // // scan.setStartRow(createCriteria(InfoType.INFO.getType(), taskid));
    // // scan.setStopRow(createCriteria(InfoType.FATAL.getType(), taskid));
    // // } else {
    // // byte[] criteria = createCriteria(logLevel.getType(), taskid);
    // // scan.setStartRow(criteria);
    // // scan.setStopRow(criteria);
    // // }
    // 
    // // HTablePool pool = new HTablePool(conf, MAX_TABLE_COUNT);
    // HTable table = (HTable) pool.getTable(TAB_NAME);
    // ResultScanner resultScanner = table.getScanner(scan);
    // for (Result rr : resultScanner) {
    // for (KeyValue kv : rr.list()) {
    // System.out.println(Bytes.toString(kv.getQualifier()));
    // 
    // }
    // System.out.println("=======================");
    // }
    // 
    // }
    // 
    // public static void putIfTableExist(HBaseAdmin admin, HTablePool pool,
    // String tableName, String cfname) throws IOException {
    // 
    // System.out.println("\n===put===");
    // 
    // if (admin.tableExists(tableName)) {
    // 
    // HTable table = (HTable) pool.getTable(tableName);
    // 
    // List<Put> putL = new ArrayList<Put>();
    // 
    // for (int i = 0; i < 5; i++) {
    // 
    // for (int j = 0; j < 2; j++) {
    // 
    // String rowKey = "row" + i;
    // String column = "d" + j;
    // String value = "v" + j;
    // 
    // Put singlePut = new Put(Bytes.toBytes(rowKey));
    // 
    // singlePut.add(Bytes.toBytes(cfname), Bytes.toBytes(column),
    // Bytes.toBytes(value));
    // putL.add(singlePut);
    // }
    // }
    // 
    // table.put(putL);
    // // table.put(singlePut);
    // 
    // table.close(); // 用完以后要释放htablepool
    // 
    // } else {
    // System.out.println("WARNING:Table '" + tableName
    // + "' not exists in cluster!\n\tCheck it first.\n");
    // }
    // }
}
