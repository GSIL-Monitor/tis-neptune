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
package com.qlangtech.tis.indexbuilder.map;

import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import org.jboss.netty.util.internal.ConcurrentHashMap;

/* *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class Counters {

    private final Map<HdfsIndexBuilder.Counter, AtomicLong> counterMap = new ConcurrentHashMap<>();

    public void incrCounter(HdfsIndexBuilder.Counter counter, int deta) {
        AtomicLong c = counterMap.get(counter);
        if (c == null) {
            c = new AtomicLong();
            AtomicLong tmp = counterMap.putIfAbsent(counter, c);
            if (tmp != null) {
                c = tmp;
            }
        }
        c.addAndGet(deta);
    }

    public long getCounter(HdfsIndexBuilder.Counter counter) {
        AtomicLong c = counterMap.get(counter);
        return (c != null) ? c.get() : 0;
    }

    public void setCounter(HdfsIndexBuilder.Counter counter, long value) {
    }
}
