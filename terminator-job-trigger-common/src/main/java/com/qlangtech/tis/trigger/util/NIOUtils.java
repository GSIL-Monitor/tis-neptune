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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.SocketChannel;
import org.apache.commons.io.IOUtils;

/* *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class NIOUtils {

    // private static final Log log = LogFactory.
    /**
     * 从Channel管道中读出一个对象
     *
     * @param channel
     * @return
     */
    @SuppressWarnings("all")
    public static <T> T readObjectFromChannel(final SocketChannel channel) {
        ByteBuffer content = null;
        ByteBuffer dataSizeBuffer = null;
        int dataSize = 0;
        try {
            // ByteBuffer.allocate(4);
            dataSizeBuffer = objectSizeBuffer.get();
            dataSizeBuffer.clear();
            while (dataSizeBuffer.hasRemaining()) {
                channel.read(dataSizeBuffer);
            }
            dataSizeBuffer.flip();
            IntBuffer b = dataSizeBuffer.asIntBuffer();
            dataSize = b.get();
            if (dataSize < 1 || dataSize > BUFFER_SIZE) {
                throw new IllegalStateException("dataSize is invalid:" + dataSize);
            }
            // content = ByteBuffer.allocate(dataSize);
            content = objectBuffer.get();
            content.clear();
            content.limit(dataSize);
        } catch (Exception e) {
            try {
                dataSizeBuffer.clear();
            } catch (Throwable ee) {
            }
            try {
                content.clear();
            } catch (Throwable ee) {
            }
            throw new RuntimeException("dataSize:" + dataSize, e);
        } finally {
            try {
                dataSizeBuffer.clear();
            } catch (Throwable e) {
            }
        }
        // while (true) {
        ObjectInputStream objInputStream = null;
        try {
            while (content.hasRemaining()) {
                channel.read(content);
            }
            content.flip();
            ByteArrayInputStream inputReader = new ByteArrayInputStream(content.array());
            objInputStream = new ObjectInputStream(inputReader);
            return (T) objInputStream.readObject();
        } catch (SocketException e) {
            // 说明socket连接断掉了需要把当前connection 销毁
            throw new RuntimeException(e);
        } catch (Exception e) {
            // log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        } finally {
            content.clear();
            IOUtils.closeQuietly(objInputStream);
        }
    }

    private static final int BUFFER_SIZE = 10000;

    private static final ThreadLocal<ByteBuffer> objectBuffer = new ThreadLocal<ByteBuffer>() {

        @Override
        protected ByteBuffer initialValue() {
            return ByteBuffer.allocate(BUFFER_SIZE);
        }
    };

    private static final ThreadLocal<ByteBuffer> objectSizeBuffer = new ThreadLocal<ByteBuffer>() {

        @Override
        protected ByteBuffer initialValue() {
            return ByteBuffer.allocate(4);
        }
    };

    /**
     * 向socket流中写入一个对象
     *
     * @param channel
     * @param obj
     * @throws IOException
     */
    public static void writeObject(SocketChannel channel, Object obj) throws IOException {
        if (channel == null) {
            throw new IllegalArgumentException("param channel can not be null");
        }
        ByteArrayOutputStream objContent = null;
        ObjectOutputStream outStream = null;
        try {
            objContent = new ByteArrayOutputStream();
            outStream = new ObjectOutputStream(objContent);
            outStream.writeObject(obj);
            // ByteBuffer.allocate(objContent.size()
            ByteBuffer content = objectBuffer.get();
            // + 4);
            content.clear();
            if (objContent.size() > BUFFER_SIZE) {
                throw new IllegalStateException("write data size has exceed the max limit of " + BUFFER_SIZE);
            }
            content.putInt(objContent.size());
            content.put(objContent.toByteArray(), 0, objContent.size());
            content.flip();
            while (content.hasRemaining()) {
                channel.write(content);
            }
        } finally {
            IOUtils.closeQuietly(outStream);
            IOUtils.closeQuietly(objContent);
        }
    }
}
