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
package com.qlangtech.tis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.qlangtech.tis.manage.common.trigger.ExecType;
import com.qlangtech.tis.manage.common.trigger.ODPSConfig;
import com.qlangtech.tis.manage.common.trigger.ODPSConfig.DailyPartition;
import com.qlangtech.tis.manage.common.trigger.sources.TddlTaskConfig;

/* *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class TestDumpAndBuildClient {

    public static void main(String[] args) {
        TddlTaskConfig taskConfig = new TddlTaskConfig();
        taskConfig.setTddlAppName("JST_TERMHOME_APP");
        taskConfig.setAppName("search4app");
        taskConfig.setLogicTableName("application");
        taskConfig.setMaxDumpCount((Long.parseLong("0")));
        taskConfig.setShareId("app_id");
        taskConfig.setTaskId(1111);
        taskConfig.setType("tddl");
        taskConfig.setExecType(ExecType.CREATE);
        /*
		 * 数据量与组数的映射关系 0< x <=100W 1组 100W< x <=1000W 32组 1000W< x <=10000W
		 * 256组
		 */
        // taskConfig.setDataSizeEstimate(100000);
        ODPSConfig odpsConfig = new ODPSConfig();
        odpsConfig.setAccessId("07mbAZ3it1QaLTE8");
        odpsConfig.setAccessKey("BEGxJuH9JY7E6AYR2qWoHSjvufN0cV");
        odpsConfig.setDailyPartition(new DailyPartition("dp", "20141101"));
        odpsConfig.setGroupPartition("gp");
        odpsConfig.setDatatunelEndPoint("http://dt.odps.aliyun.com");
        odpsConfig.setServiceEndPoint("http://service.odps.aliyun.com/api");
        odpsConfig.setProject("jst_tsearcher");
        odpsConfig.setShallIgnorPartition(false);
        odpsConfig.setGroupSize(1);
        taskConfig.setOdpsConfig(odpsConfig);
        Map<String, List<String>> dbs = new HashMap<String, List<String>>();
        List<String> tables1 = new ArrayList<String>();
        tables1.add("application");
        dbs.put("JST_TERMINATORHOME_GROUP", tables1);
        // List<String> tables2=new ArrayList<String>();
        // tables2.add("department");
        // dbs.put("JST_TERMINATORHOME_GROUP", tables2);
        taskConfig.setDbs(dbs);
        List<String> cols = new ArrayList<String>();
        cols.add("app_id");
        cols.add("project_name");
        cols.add("recept");
        cols.add("dpt_id");
        cols.add("dpt_name");
        cols.add("update_time");
        cols.add("create_time");
        // cols.add("temp");
        taskConfig.setCols(cols);
    // String str=TriggerTaskConfig.serialize(taskConfig);
    // TriggerTaskConfig.parse(str);
    // 
    // System.out.println(str);
    // TddlDumpAndBuildClient client = new TddlDumpAndBuildClient();
    // client.dumpAndBuild(taskConfig);
    }
    // public static void main(String[] args) throws MalformedURLException {
    // URL allCrontabsUrl = new URL("http://10.68.211.194:8081"
    // +
    // "/config/config.ajax?action=trigger_config_action&event_submit_do_get_config=true&resulthandler=advance_query_result&indexname="
    // + "search4_182589402_testnew");
    // 
    // TriggerTaskConfig config = ConfigFileContext.processContent(
    // allCrontabsUrl, new StreamProcess<TriggerTaskConfig>() {
    // @Override
    // public TriggerTaskConfig p(int status, InputStream stream,String md5) {
    // try {
    // return TriggerTaskConfig.parse(IOUtils.toString(stream));
    // } catch (IOException e) {
    // throw new RuntimeException(e);
    // }
    // 
    // }
    // });
    // 
    // 
    // config.getOdpsConfig().setDailyPartition("20141101");
    // config.getOdpsConfig().setEndPoint("endPoint");
    // config.getOdpsConfig().setGroupPartition("256");
    // 
    // 
    // DumpAndBuildClient client=new DumpAndBuildClient();
    // client.dump(config);
    // 
    // 
    // }
}
