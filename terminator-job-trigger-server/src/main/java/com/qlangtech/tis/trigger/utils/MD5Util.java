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
package com.qlangtech.tis.trigger.utils;

import org.apache.commons.codec.digest.DigestUtils;

/* *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class MD5Util {

    public static final String MD5(String s) {
        return new String(DigestUtils.md5(s));
    // char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
    // 'A', 'B', 'C', 'D', 'E', 'F' };
    // try {
    // byte[] btInput = s.getBytes();
    // // 获得MD5摘要算法的 MessageDigest 对象
    // MessageDigest mdInst = MessageDigest.getInstance("MD5");
    // // 使用指定的字节更新摘要
    // mdInst.update(btInput);
    // // 获得密文
    // byte[] md = mdInst.digest();
    // // 把密文转换成十六进制的字符串形式
    // int j = md.length;F
    // char str[] = new char[j * 2];
    // int k = 0;
    // for (int i = 0; i < j; i++) {
    // byte byte0 = md[i];
    // str[k++] = hexDigits[byte0 >>> 4 & 0xf];
    // str[k++] = hexDigits[byte0 & 0xf];
    // }
    // return new String(str);
    // } catch (Exception e) {
    // e.printStackTrace();
    // return null;
    // }
    }

    public static void main(String[] args) {
        System.out.println(MD5Util.MD5("20121221"));
        System.out.println(MD5Util.MD5("加密"));
    }
}
