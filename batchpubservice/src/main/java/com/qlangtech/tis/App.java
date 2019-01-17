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

import java.net.URLEncoder;

/*
 * Hello world!
 *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class App {

    public static void main(String[] args) throws Exception {
        // System.out.println( "Hello World!" );
        // String aa =
        // "justSpuIdBoost=on&q=%28spu_id%3A%28%22239763%22%5E220+OR+%22222132%22%5E210+OR+%22237103%22%5E200+OR+%22237277%22%5E190+OR+%22237370%22%5E180+OR+%22238139%22%5E170+OR+%22238980%22%5E160+OR+%22239246%22%5E150+OR+%22239637%22%5E140+OR+%22193386%22%5E130+OR+%22240957%22%5E120+OR+%22241067%22%5E110+OR+%22241069%22%5E100+OR+%22241195%22%5E90+OR+%22241195%22%5E80+OR+%22241230%22%5E70+OR+%22240571%22%5E60+OR+%22240578%22%5E50+OR+%22240794%22%5E40+OR+%22240890%22%5E30+OR+%22240892%22%5E20+OR+%22240907%22%5E10%29%29%5E100000000+AND+%28type%3A0%29+AND+%28status%3A1%29+AND+%28%28%28*%3A*+-%28device_model_support%3A1%29%29+AND+-%28device_model%3AC2%29%29+OR+%28device_model_support%3A1+AND+%28device_model%3AC2%29%29%29+AND+-%28yunos_ver%3A2.3.0-R1-20131115.1023%29+AND+-%28android_ver%3A4.2%29&group=true&group.ngroups=true&group.field=package_name&group.sort=version_code+desc&group.limit=1&start=0&rows=20";
        // 
        // System.out.println(URLDecoder.decode(aa, "utf8"));
        System.out.println(URLEncoder.encode("{groupIndex}"));
    }
}
