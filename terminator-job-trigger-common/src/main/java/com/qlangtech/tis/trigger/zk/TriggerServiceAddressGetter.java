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
package com.qlangtech.tis.trigger.zk;

import java.util.ArrayList;
import java.util.List;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import com.qlangtech.tis.trigger.socket.Constant;

/* *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class TriggerServiceAddressGetter {

    private final ZkGetter zkgetter;

    public TriggerServiceAddressGetter(ZkGetter zkgetter) {
        super();
        this.zkgetter = zkgetter;
    }

    public List<String> getServiceAddress() {
        return getServiceAddress(null);
    }

    private ZooKeeper getZk() {
        return zkgetter.getInstance();
    }

    public List<String> getServiceAddress(final Callback callback) {
        try {
            if (getZk().exists(Constant.TRIGGER_SERVER, false) == null) {
                throw new IllegalStateException("path:" + Constant.TRIGGER_SERVER + " in zk is null,");
            }
            List<String> ips = new ArrayList<String>();
            // if (callback == null) {
            List<String> child = null;
            if (callback == null) {
                child = getZk().getChildren(Constant.TRIGGER_SERVER, false);
            } else {
                child = getZk().getChildren(Constant.TRIGGER_SERVER, new Watcher() {

                    @Override
                    public void process(WatchedEvent event) {
                        if (event.getType() == Event.EventType.NodeChildrenChanged) {
                            callback.execute(getZk());
                        }
                    }
                });
            }
            for (String c : child) {
                ips.add(new String(getZk().getData(Constant.TRIGGER_SERVER + "/" + c, false, new Stat())));
            }
            return ips;
        } catch (IllegalStateException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public interface Callback {

        public void execute(ZooKeeper zookeeper);
    }
}
