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

import junit.framework.TestCase;

/* *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class TestOrding extends TestCase {

    public void test() {
    // Ordering<Foo> ordering = Ordering.natural().reverse().nullsFirst().onResultOf(new Function<Foo, String>() {
    // public String apply(Foo foo) {
    // return foo.sortedBy;
    // }
    // });
    // 
    // List<Foo> lists = new ArrayList<>();
    // Foo foo = new Foo();
    // foo.sortedBy = "aaa";
    // foo.notSortedBy = 1;
    // lists.add(foo);
    // 
    // foo = new Foo();
    // foo.sortedBy = null;
    // foo.notSortedBy = 1;
    // lists.add(foo);
    // 
    // foo = new Foo();
    // foo.sortedBy = "bbb";
    // foo.notSortedBy = 2;
    // lists.add(foo);
    // List<Foo> result = ordering.sortedCopy(lists);
    // 
    // for (Foo f : result) {
    // System.out.println(f.sortedBy);
    // }
    }

    private static class Foo {

        String sortedBy;

        private int notSortedBy;
    }
}
