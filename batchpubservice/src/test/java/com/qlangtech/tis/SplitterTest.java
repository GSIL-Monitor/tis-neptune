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
import org.apache.commons.lang.StringUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class SplitterTest extends TestCase {

    public void test() {
    // Iterable<String> it = Splitter.on(',').trimResults().omitEmptyStrings().split("foo,bar,,   qux");
    // 
    // it.forEach(this::print);
    }

    public void testOld() {
        List<String> result = new ArrayList<>();
        String[] params = "foo,bar,,   qux".split(",");
        String param = null;
        for (int i = 0; i < params.length; i++) {
            param = params[i];
            if (StringUtils.isNotBlank(param)) {
                result.add(param);
            }
        }
        result.forEach(System.out::println);
    }

    private static final Pattern pattern = Pattern.compile("[^,\\s]+");

    public void testPattern() {
        System.out.println("testPattern start=============================");
        List<String> result = new ArrayList<>();
        Matcher m = pattern.matcher("foo,bar,,   qux");
        while (m.find()) {
            result.add(m.group());
        }
        result.forEach(System.out::println);
        System.out.println("end=============================");
    }

    private void print(String msg) {
        System.out.println(msg);
    }
}
