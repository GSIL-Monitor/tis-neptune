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
package com.qlangtech.tis.trigger.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/* *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class TaskOverseer {

    protected final OverSeer oversee;

    private final long timeInterval;

    private static final Log log = LogFactory.getLog(TaskOverseer.class);

    private final ExecutorService threadPool = Executors.newCachedThreadPool();

    private int maxErrorCount;

    public TaskOverseer(long duration, TimeUnit timeunit, int maxErrorCount) {
        super();
        this.maxErrorCount = maxErrorCount;
        this.timeInterval = timeunit.toMillis(duration);
        this.oversee = new OverSeer(this.getClass().getName());
        this.oversee.setDaemon(true);
        startOverseer();
    }

    // 是否已经被关闭
    public boolean isClosed() {
        return this.oversee.stoped;
    }

    protected void startOverseer() {
        this.oversee.start();
    }

    protected String getTaskName() {
        return this.getClass().getSimpleName();
    }

    private final AtomicInteger count = new AtomicInteger();

    private final AtomicLong timestampe = new AtomicLong(System.currentTimeMillis());

    protected void startWork() throws Exception {
    }

    protected void recycleResource() {
    }

    protected class OverSeer extends Thread {

        public OverSeer(String name) {
            super(name);
        }

        private boolean stoped = false;

        @Override
        public void run() {
            try {
                while (!stoped) {
                    synchronized (this) {
                        System.out.println("launch a worker task:" + TaskOverseer.this.getClass().getName());
                        log.info("[" + getTaskName() + "]launch a worker task:" + TaskOverseer.this.getClass().getName());
                        threadPool.execute(new Worker());
                        this.wait();
                        // 休息三秒再干活
                        sleep(5000);
                    }
                }
                this.interrupt();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void dispose() {
        synchronized (this.oversee) {
            recycleResource();
            this.oversee.stoped = true;
            this.oversee.notifyAll();
            threadPool.shutdownNow();
        }
    }

    /**
     * 重新启动
     */
    public void relaunch() {
        synchronized (this.oversee) {
            recycleResource();
            // threadPool.shutdownNow();
            this.oversee.notifyAll();
        }
    }

    private class Worker implements Runnable {

        @Override
        public void run() {
            try {
                startWork();
            } catch (Throwable e) {
                log.error("[" + getTaskName() + "] an error occur", e);
                synchronized (oversee) {
                    recycleResource();
                    final long currentTimestamp = System.currentTimeMillis();
                    if ((currentTimestamp <= (timestampe.get() + timeInterval)) && count.incrementAndGet() > maxErrorCount) {
                        // 结束监工进程
                        oversee.stoped = true;
                        throw new RuntimeException("between 1 min " + count.get() + " errors occur", e);
                    } else {
                        if (currentTimestamp > (timestampe.get() + timeInterval)) {
                            count.set(0);
                            timestampe.set(currentTimestamp);
                        }
                    }
                    oversee.notifyAll();
                }
            }
        }
    }

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        TaskOverseer overseer = new TaskOverseer(1l, TimeUnit.MINUTES, 4) {

            @Override
            protected void startWork() throws Exception {
                Thread.sleep(1000 * 20);
                throw new Exception("i am die");
            }
        };
        overseer.dispose();
        Thread.sleep(10000 * 99);
    }
}
