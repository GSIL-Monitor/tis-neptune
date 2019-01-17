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

import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang.StringUtils;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import com.qlangtech.tis.pubhook.common.RunEnvironment;

/* *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class ICPubService extends PubService {

    public static final int MasterCount = 2;

    private final ZooKeeper zk;

    private final List<String> masterIpsFromFile;

    public ICPubService(String appname, int groupSize, int replicaCount, RunEnvironment runtime, List<String> servers, ZooKeeper zookeeper) throws Exception {
        super(appname, groupSize, replicaCount, runtime, servers);
        if (zookeeper == null) {
            throw new IllegalStateException("zookeeper can not be null");
        }
        this.zk = zookeeper;
        this.masterIpsFromFile = getMasterIps();
    }

    @Override
    void postExecuteProcess(Map<Integer, Set<String>> ips) throws Exception {
        int accumulator = 0;
        String ip = null;
        for (Map.Entry<Integer, Set<String>> entry : ips.entrySet()) {
            for (int i = 0; i < MasterCount; i++) {
                if (accumulator >= masterIpsFromFile.size()) {
                    accumulator = 0;
                }
                ip = masterIpsFromFile.get(accumulator++);
                entry.getValue().add(ip);
                String path = "/terminator-lock/mutex/Terminator" + this.getAppname() + "-" + entry.getKey();
                ceateZKPathIfNotExist(path);
                path = path + "/Dumper";
                ceateZKPathIfNotExist(path);
                path = path + "/squence-" + i;
                Stat stat = null;
                if ((stat = zk.exists(path, false)) != null) {
                    zk.delete(path, stat.getVersion());
                }
                zk.create(path, ip.getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }
        }
    }

    /**
     * @param path
     * @throws KeeperException
     * @throws InterruptedException
     */
    private void ceateZKPathIfNotExist(String path) throws KeeperException, InterruptedException {
        if (zk.exists(path, false) == null) {
            zk.create(path, StringUtils.EMPTY.getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        }
    }

    /**
     * @return
     * @throws UnsupportedEncodingException
     */
    protected List<String> getMasterIps() throws UnsupportedEncodingException {
        List<String> ipsFromFile = new ArrayList<String>();
        InputStreamReader isr = new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream("master.txt"), "UTF-8");
        LineIterator lineIterator = IOUtils.lineIterator(isr);
        while (lineIterator.hasNext()) {
            ipsFromFile.add(lineIterator.nextLine());
        }
        IOUtils.closeQuietly(isr);
        return ipsFromFile;
    }

    public void preExecuteProcess() throws Exception {
    }
}
