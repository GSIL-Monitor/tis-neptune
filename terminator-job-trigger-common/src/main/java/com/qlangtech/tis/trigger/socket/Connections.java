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

import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;

/* *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class Connections<T extends Session> extends ArrayList<T> {

    private static final long serialVersionUID = 1L;

    @Override
    public Iterator<T> iterator() {
        for (int i = 0; i < this.size(); i++) {
            if (this.get(i).isClosed()) {
                this.remove(i);
            }
        }
        return super.iterator();
    }

    public static void main(String[] arg) {
        Connections<TestClosable> cons = new Connections<TestClosable>();
        for (int i = 0; i < 100; i++) {
            cons.add(new TestClosable(null, i));
        }
        int kk = 0;
        while (true) {
            kk++;
            for (Session t : cons) {
                int i = ((TestClosable) t).i;
                System.out.println(i);
                if (i % (10 + kk) == 0) {
                    ((TestClosable) t).close();
                }
            }
            if (kk == 5) {
                break;
            }
        }
    }

    private static class TestClosable extends Session {

        private int i;

        /**
         * @param socketChannel
         * @param i
         */
        public TestClosable(SocketChannel socketChannel, int i) {
            super(socketChannel);
            this.i = i;
        }
        // @Override
        // public void send(Serializable o) {
        // }
        // 
        // /**
        // * @param i
        // */
        // public TestClosable(int i) {
        // super();
        // this.i = i;
        // }
    }
}