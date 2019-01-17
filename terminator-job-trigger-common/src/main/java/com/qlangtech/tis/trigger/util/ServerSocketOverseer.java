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

import java.util.concurrent.TimeUnit;

/* *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public final class ServerSocketOverseer extends TaskOverseer {

    /**
     * @param duration
     * @param timeunit
     * @param maxErrorCount
     */
    public ServerSocketOverseer(long duration, TimeUnit timeunit, int maxErrorCount) {
        super(duration, timeunit, maxErrorCount);
    }
    // private ServerSocketChannel serverSocketChannel;
    // private Selector selector;
    // private final Log log;
    // 
    // private final int port;
    // 
    // public ServerSocketOverseer(int port) {
    // super(2, TimeUnit.MINUTES, 20);
    // this.log = LogFactory.getLog(this.getClass());
    // this.port = port;
    // }
    // 
    // @Override
    // protected void startWork() throws Exception {
    // log.warn("task class:" + this.getClass() + "port:" + port);
    // try {
    // serverSocketChannel = ServerSocketChannel.open();
    // serverSocketChannel.socket().bind(new InetSocketAddress(port));
    // selector = Selector.open();
    // serverSocketChannel.configureBlocking(false);
    // serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
    // } catch (Exception e1) {
    // throw new RuntimeException("bind port:" + port, e1);
    // }
    // 
    // log.warn("has success publish server on port:" + port);
    // 
    // int n = 0;
    // SelectionKey key = null;
    // SocketChannel socketChannel = null;
    // while (true) {
    // n = selector.select();
    // if (n < 1) {
    // continue;
    // }
    // 
    // Set<SelectionKey> keys = selector.selectedKeys();
    // Iterator<SelectionKey> it = keys.iterator();
    // 
    // while (it.hasNext()) {
    // key = it.next();
    // try {
    // if (key.isAcceptable()) {
    // socketChannel = ((ServerSocketChannel) key.channel())
    // .accept();
    // if (socketChannel != null) {
    // 
    // clientLinkApply(key, socketChannel);
    // socketChannel.configureBlocking(false);
    // socketChannel.register(selector,
    // SelectionKey.OP_READ);
    // }
    // continue;
    // }
    // 
    // if (key.isReadable()) {
    // 
    // try {
    // socketChannel = (SocketChannel) key.channel();
    // receiveClient(key, socketChannel);
    // } catch (Exception e) {
    // socketChannel.close();
    // log.error(e.getMessage(), e);
    // }
    // 
    // continue;
    // }
    // } finally {
    // it.remove();
    // }
    // 
    // }
    // 
    // }
    // }
    // 
    // protected abstract void clientLinkApply(SelectionKey selectKey,
    // SocketChannel socketChannel) throws Exception;
    // 
    // protected abstract void receiveClient(SelectionKey selectKey,
    // SocketChannel socketChannel);
    // 
    // @Override
    // protected void recycleResource() {
    // try {
    // if (selector != null) {
    // selector.close();
    // }
    // } catch (IOException e) {
    // }
    // IOUtils.closeQuietly(serverSocketChannel);
    // }
}
