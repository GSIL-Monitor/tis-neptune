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
import java.net.InetSocketAddress;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.ipc.RPC;

/* *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class FeedbackClient implements IJobFeedback {

    private IJobFeedback jobfeedback;

    private static FeedbackClient client;

    private FeedbackClient(String ip, int port) {
        InetSocketAddress addr = new InetSocketAddress(ip, port);
        try {
            jobfeedback = (IJobFeedback) RPC.waitForProxy(IJobFeedback.class, 1, addr, new Configuration());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static FeedbackClient getInstance(String ip, int port) {
        if (client == null) {
            synchronized (FeedbackClient.class) {
                if (client == null) {
                    client = new FeedbackClient(ip, port);
                }
            }
        }
        return client;
    }

    public void error(String serviceName, long timestamp, String msg) {
        jobfeedback.error(serviceName, timestamp, msg);
    }

    public void fatal(String serviceName, long timestamp, String msg) {
        jobfeedback.fatal(serviceName, timestamp, msg);
    }

    public void info(String serviceName, long timestamp, String msg) {
        jobfeedback.info(serviceName, timestamp, msg);
    }

    public long getProtocolVersion(String protocol, long clientVersion) throws IOException {
        throw new UnsupportedOperationException();
    }

    public void dispose() {
        RPC.stopProxy(jobfeedback);
    }

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        FeedbackClient client = FeedbackClient.getInstance("localhost", 8091);
        for (int i = 0; i < 20; i++) {
            client.fatal("search4realwidget", System.currentTimeMillis(), "hello" + i);
            Thread.sleep(900);
        }
        client.dispose();
    }
}
