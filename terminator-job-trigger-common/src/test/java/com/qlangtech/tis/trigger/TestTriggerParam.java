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
package com.qlangtech.tis.trigger;

import junit.framework.TestCase;
import org.json.JSONArray;
import org.json.JSONObject;
import com.qlangtech.tis.trigger.util.Assert;
import com.qlangtech.tis.trigger.util.TriggerParam;

/* *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class TestTriggerParam extends TestCase {

    public void test() {
        TriggerParam param = new TriggerParam();
        // String datepattern = "yyyyMMdd";
        String time = "2012110911";
        // String yuntihost = "hdfs://hdpnn:9000";
        String yuntipath = "/group/tbptd-dev/jianxiao/wantumobile/album";
        // final String userToken = "jianxiao,cug-tbptd-dev,cug-tbdp,cug-analysts,#llx110108";
        final String userName = "baisui";
        final int groupSize = 9527;
        String taskid = "12345678";
        // param.setDatePattern(datepattern);
        param.setTime(time);
        // param.setYuntiHost(yuntihost);
        param.setYuntiPath(yuntipath);
        param.setTaskId(taskid);
        param.setCurrentUserName(userName);
        param.setGroupSize(groupSize);
        // param.setYuntiUserToken(userToken);
        param = TriggerParam.deserialize(param.serialize());
        // Assert.assertEquals(datepattern, param.getDatePattern());
        Assert.assertEquals(time, param.getTime());
        // Assert.assertEquals(yuntihost, param.getYuntiHost());
        Assert.assertEquals(yuntipath, param.getYuntiPath());
        // Assert.assertEquals(userToken, param.getYuntiUserToken());
        Assert.assertEquals(taskid, param.getTaskId());
        Assert.assertEquals(userName, param.getCurrentUserName());
        Assert.assertEquals(groupSize, param.getGroupSize());
    }

    public void test2() throws Exception {
        JSONArray array = new JSONArray();
        JSONObject o = new JSONObject();
        o.put("name", "baisui");
        o.put("age", 123);
        array.put(o);
        o = new JSONObject();
        o.put("name", "jj");
        o.put("age", 55);
        array.put(o);
        System.out.println(array.toString());
    }

    public void test3() throws Exception {
    // System.out.println(URLEncoder.encode("select * from search4sucai;"));
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
    // TODO Auto-generated method stub
    }
}
