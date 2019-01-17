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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.ObjectOutputStream;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.LongBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import junit.framework.Assert;
import junit.framework.TestCase;
import com.qlangtech.tis.trigger.socket.RegisterData;

/* *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class NIOSocketClient extends TestCase {

    private static final long serialVersionUID = 1L;

    /**
     * @param args
     */
    public void testMain() throws Exception {
        Selector selector = Selector.open();
        SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1", 9998));
        socketChannel.configureBlocking(true);
        ByteBuffer serviceName = ByteBuffer.wrap("search4locationservicenew".getBytes());
        socketChannel.write(serviceName);
        serviceName.clear();
        ByteBuffer jobsid = ByteBuffer.allocate(16);
        int readCount = socketChannel.read(jobsid);
        System.out.println(readCount);
        Assert.assertEquals(16, readCount);
        jobsid.flip();
        DataInputStream jobsReader = new DataInputStream(new ByteArrayInputStream(jobsid.array(), 0, 16));
        long fjobid = jobsReader.readLong();
        long ijobid = jobsReader.readLong();
        System.out.println("fjob:" + fjobid + ",ijob:" + ijobid);
        jobsReader.close();
        jobsid.clear();
        ByteArrayOutputStream objContent = new ByteArrayOutputStream();
        ObjectOutputStream outStream = new ObjectOutputStream(objContent);
        outStream.writeObject(new RegisterData(Thread.currentThread().hashCode(), Inet4Address.getLocalHost(), new Long[] { fjobid, ijobid }));
        ByteBuffer content = ByteBuffer.allocate(objContent.size() + 4);
        content.putInt(objContent.size());
        content.put(objContent.toByteArray(), 0, objContent.size());
        content.flip();
        socketChannel.write(content);
        content.clear();
        outStream.close();
        socketChannel.configureBlocking(false);
        socketChannel.register(selector, SelectionKey.OP_READ);
        ByteBuffer byteBuffer = ByteBuffer.allocate(24);
        while (true) {
            int n = selector.select();
            if (n < 1) {
                continue;
            }
            Set<SelectionKey> keys = selector.selectedKeys();
            Iterator<SelectionKey> it = keys.iterator();
            while (it.hasNext()) {
                SelectionKey key = it.next();
                socketChannel = (SocketChannel) key.channel();
                byteBuffer.clear();
                socketChannel.read(byteBuffer);
                byteBuffer.flip();
                LongBuffer jobs = byteBuffer.asLongBuffer();
                // token
                System.out.println(jobs.get(0));
                // taskid
                System.out.println(jobs.get(1));
                // jobid
                System.out.println(jobs.get(2));
                it.remove();
            }
        }
    }
}
