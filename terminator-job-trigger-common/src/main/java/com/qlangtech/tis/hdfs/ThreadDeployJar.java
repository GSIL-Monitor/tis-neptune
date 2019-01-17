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
package com.qlangtech.tis.hdfs;

import org.apache.commons.cli.CommandLine;
import org.apache.hadoop.fs.Path;
import com.qlangtech.tis.yarn.common.YarnConstant;
import com.qlangtech.tis.pubhook.common.RunEnvironment;

/* *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class ThreadDeployJar implements Runnable {

    private final CommandLine commandLine;

    public ThreadDeployJar(CommandLine commandLine) {
        super();
        this.commandLine = commandLine;
    }

    @Override
    public void run() {
        try {
            RunEnvironment runtime = RunEnvironment.getEnum(commandLine.getOptionValue(RunEnvironment.KEY_RUNTIME));
            // TSearcherConfigFetcher.setConfigCenterHost(runtime.getKeyName());
            System.setProperty(RunEnvironment.KEY_RUNTIME, runtime.getKeyName());
            String localJarDir = commandLine.getOptionValue(YarnConstant.PARAM_OPTION_LOCAL_JAR_DIR);
            final Path dest = new Path(YarnConstant.HDFS_GROUP_LIB_DIR + YarnConstant.INDEX_BUILD_JAR_DIR + '/' + runtime.getKeyName());
            TISHdfsUtils.copyLibs2Hdfs(localJarDir, dest);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
