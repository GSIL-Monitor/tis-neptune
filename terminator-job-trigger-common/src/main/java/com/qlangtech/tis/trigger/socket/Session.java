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
package com.qlangtech.tis.trigger.socket;

import java.io.Closeable;
import java.io.IOException;
import java.io.Serializable;
import java.nio.channels.SocketChannel;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.qlangtech.tis.trigger.util.NIOUtils;

/* *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class Session<T extends Serializable> implements Closeable {

    private static final Log log = LogFactory.getLog(Session.class);

    private boolean closed = false;

    protected final SocketChannel socketChannel;

    /**
     * @param socketChannel
     */
    public Session(SocketChannel socketChannel) {
        super();
        this.socketChannel = socketChannel;
    }

    @Override
    public void close() {
        this.closed = true;
    }

    public boolean isClosed() {
        return this.closed;
    }

    public boolean send(T o) {
        synchronized (socketChannel) {
            try {
                NIOUtils.writeObject(socketChannel, o);
                return true;
            } catch (IOException e) {
                IOUtils.closeQuietly(socketChannel);
                log.warn(e.getMessage(), e);
                this.close();
            }
            return false;
        }
    }
}
