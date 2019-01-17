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

import com.qlangtech.tis.common.utils.TSearcherConfigFetcher;
import com.qlangtech.tis.pubhook.common.RunEnvironment;

/* *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class Config {

    private static Config config;

    // 代表当前代码所在的运行环境
    private RunEnvironment currnetEnvirment;

    private TSearcherConfigFetcher fetcherConfig;

    static {
    }

    private Config() {
        // ResourceBundle bundle = ResourceBundle
        // .getBundle("com/taobao/terminator/trigger/utils/config");
        this.fetcherConfig = TSearcherConfigFetcher.getInstance("search4xxxx");
        this.currnetEnvirment = fetcherConfig.getRuntime();
    }

    static Config getInstance() {
        if (config == null) {
            synchronized (Config.class) {
                if (config == null) {
                    config = new Config();
                }
            }
        }
        return config;
    }

    /**
     * 取得当前的运行环境
     *
     * @return
     */
    public static RunEnvironment getRunEnvironment() {
        return getInstance().currnetEnvirment;
    }

    public static String getZkAddress() {
        return getInstance().fetcherConfig.getZkAddress();
    }

    public static String getHdfsHost() {
        return getInstance().fetcherConfig.getHdfsAddress();
    }
}
