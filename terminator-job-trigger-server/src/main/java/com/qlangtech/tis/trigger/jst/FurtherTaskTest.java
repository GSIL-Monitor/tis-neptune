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
package com.qlangtech.tis.trigger.jst;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

/* *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class FurtherTaskTest {

    // private FutureTask task = new FutureTask(new Callable<String>() {
    // 
    // @Override
    // public String call() throws Exception {
    // 
    // Thread.sleep(1000 * 3);
    // 
    // return "hello";
    // }
    // 
    // });
    static ExecutorService pool = Executors.newCachedThreadPool();

    private void start() throws Exception {
        // Thread t = new Thread(task);
        // t.run();
        // 
        // System.out.println(task.get());
        List<FutureTask> tasks = new ArrayList<FutureTask>();
        for (int i = 0; i < 10; i++) {
            final int index = i;
            Callable<String> callable = new Callable<String>() {

                @Override
                public String call() throws Exception {
                    System.out.println("start:" + index);
                    Thread.sleep(1000 * 3);
                    return "hello" + index;
                }
            };
            FutureTask task = new FutureTask(callable);
            pool.execute(task);
            tasks.add(task);
        }
        for (int i = 0; i < 10; i++) {
            for (FutureTask t : tasks) {
                System.out.println(t.get());
            }
        }
    }

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        FurtherTaskTest test = new FurtherTaskTest();
        test.start();
    }
}
