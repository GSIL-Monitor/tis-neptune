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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang.StringUtils;
import com.qlangtech.tis.pubhook.common.RunEnvironment;

/* *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class IpFileGenerator {

    private static final long serialVersionUID = 1L;

    public static void main(String[] args) throws Exception {
        // Integer groupCount =
        // Integer.parseInt(System.getProperty("groupCount"));
        // String appname = System.getProperty("appname");
        // 
        // List<String> lines = getIpLines(new
        // File(System.getProperty("ipfile")),
        // null);
        // System.out.println("have servers:" + lines.size() + " group:"
        // + groupCount);
        // SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        // BufferedWriter output = new BufferedWriter(new OutputStreamWriter(
        // FileUtils.openOutputStream(new File(appname + "_app_servers"
        // + format.format(new Date())))));
        // 
        // // for (int i = 0; i < groupCount; i++) {
        // // output
        // // }
        // 
        // output.flush();
        // IOUtils.closeQuietly(output);
        Matcher m = ipPattern.matcher("255.0.0.1");
        if (m.matches()) {
            System.out.println("match");
        }
    }

    private static final Pattern ipPattern = Pattern.compile("\\d{1,3}\\.\\d{0,3}.\\d{0,3}.\\d{0,3}");

    public static List<String> getIpLines(File ipFile, RunEnvironment runtime) throws FileNotFoundException, IOException {
        InputStream fileStream = new FileInputStream(ipFile);
        LineIterator it = IOUtils.lineIterator(fileStream, "utf8");
        List<String> lines = new ArrayList<String>();
        String line = null;
        String ip = null;
        Matcher matcher = null;
        while (it.hasNext()) {
            line = (String) it.next();
            if (StringUtils.isNotEmpty(line)) {
                ip = StringUtils.trim(line);
                matcher = ipPattern.matcher(ip);
                if (!matcher.matches()) {
                    throw new IllegalArgumentException("line:" + ip + " is not match the ip pattern");
                }
                // 需要这个ip上的应用是否可用
                if (runtime == RunEnvironment.ONLINE) // || runtime == RunEnvironment.READY
                {
                }
                lines.add(ip);
            }
        }
        IOUtils.closeQuietly(fileStream);
        return lines;
    }

    public static List<Pair> getIpPair(List<String> lines) throws FileNotFoundException, IOException {
        List<Pair> pares = new ArrayList<Pair>();
        for (String line : lines) {
            if (StringUtils.isNotEmpty(line)) {
                String[] pair = line.split(",");
                pares.add(new Pair(StringUtils.trim(pair[0]), StringUtils.trim(pair[1])));
            }
        }
        return pares;
    }
    /*
	 * public static void main(String[] args) throws FileNotFoundException,
	 * IOException { String a="dfd 123"; String b="rtyd  899"; String
	 * c=" uio  45 "; List<String>lines = new ArrayList<String>(); lines.add(a);
	 * lines.add(b); lines.add(c); List<Pair>pairs =
	 * IpFileGenerator.getIpPair(lines); String n=""; }
	 */
}
