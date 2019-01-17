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

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

/* *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class FileTest {

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        File dir = new File("/opt/tmp/20160920.txt");
        InputStream inputstream = null;
        byte[] tmp = new byte[10240];
        int read = 0;
        int alllineCount = 0;
        int lineCount = 0;
        for (String f : dir.list()) {
            inputstream = new FileInputStream(new File(dir, f));
            lineCount = 0;
            while ((read = inputstream.read(tmp, 0, tmp.length)) > -1) {
                for (int i = 0; i < read; i++) {
                    if ('\n' == tmp[i]) {
                        lineCount++;
                        alllineCount++;
                    }
                }
            }
            System.out.println("file:" + f + " line:" + lineCount + ",allcount:" + alllineCount);
            inputstream.close();
        }
    }
}
