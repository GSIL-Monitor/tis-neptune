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
package com.qlangtech.tis.indexbuilder;

import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.util.Version;

/* *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class Test {

    /**
     * @param args
     */
    public static void main(String[] args) {
        /*System.out.println(Long.MAX_VALUE);
			Long.parseLong("20561533820645818011");
	
		// TODO Auto-generated method stub
		IndexWriterConfig config = new IndexWriterConfig(
	            Version.LUCENE_34, null)
	            .setOpenMode(OpenMode.CREATE_OR_APPEND);*/
        // IndexWriter writer = new IndexWriter(,config);
        System.out.println();
    }

    public static long hash(java.lang.String s) {
        long ret = 0;
        if (s == null) {
            return 0;
        }
        ;
        char[] arr = s.toCharArray();
        for (int i = 0; i < arr.length; i++) {
            ret = (ret * 31 + arr[i]) % Integer.MAX_VALUE;
        }
        return ret;
    }
}
