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

import java.util.ArrayList;
import java.util.List;

/* *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class Test {

    private static void delete(Integer i, List<Integer> list) {
        synchronized (list) {
            list.remove(i);
            System.out.println("has delete:" + i);
        }
    }

    public static void main(String[] args) throws Exception {
        final List<Integer> list = new ArrayList<Integer>();
        for (int i = 0; i < 4; i++) {
            list.add(i);
        }
        synchronized (list) {
            for (Integer i : list) {
                System.out.println(i);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                }
                delete(i, list);
            }
        }
    // Thread t = new Thread(new Runnable() {
    // @Override
    // public void run() {
    // 
    // while (true) {
    // synchronized (list) {
    // for (Integer i : list) {
    // System.out.println(i);
    // try {
    // Thread.sleep(100);
    // } catch (InterruptedException e) {
    // 
    // }
    // }
    // }
    // }
    // }
    // });
    // t.start();
    // 
    // Thread tt = new Thread(new Runnable() {
    // @Override
    // public void run() {
    // int i = 999;
    // while (true) {
    // synchronized (list) {
    // System.out.println("start to add ");
    // list.add(i++);
    // }
    // try {
    // Thread.sleep(200);
    // } catch (InterruptedException e) {
    // e.printStackTrace();
    // }
    // }
    // 
    // }
    // });
    // tt.start();
    // Thread.sleep(1000 * 1000);
    }
}
