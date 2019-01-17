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

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/* *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class Test {

    // private static final ExecutorService exec =
    // Executors.newCachedThreadPool();
    public static void main(String[] arg) throws Exception {
        long waitForConnection = Long.MAX_VALUE;
        long expire = System.nanoTime() + TimeUnit.NANOSECONDS.convert(waitForConnection, TimeUnit.MILLISECONDS);
        System.out.println(expire - System.nanoTime());
    // System.out.println("Math.pow(10, 3):" + (long)Math.pow(10, 3));
    // 
    // float f = 350.0f;
    // float f2 = 0.00004f;
    // 
    // System.out
    // .println((new BigDecimal(String.valueOf(f))).multiply(new BigDecimal(String.valueOf(f2))).floatValue());
    // 
    // SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    // System.out.println(format.parse("2018/11/18 12:01:01").getTime());
    // SimpleDateFormat f = new SimpleDateFormat("yyyy/MM/dd HH:mm");
    // long t = 1532324047378l/1000 + 4184000;
    // Date d = new Date(t*1000);
    // 
    // System.out.println(t+":"+f.format(d));
    // 
    // 
    // System.out.println("ddd");
    // System.out.println('\057' == '-');
    // System.out.println('\002');
    // System.out.println((new Date(1474378790438l)));
    // System.out.println( StringEscapeUtils.escapeXml("</dependency>"));
    // System.out.println(Integer.MIN_VALUE % 10);
    // 
    // // new Date(1460140668098l);
    // LineIterator it = FileUtils
    // .lineIterator(new
    // File("C:\\Users\\baisui\\Desktop\\menu-schema.txt"));
    // String line = null;
    // StringBuffer buffer = new StringBuffer();
    // while (it.hasNext()) {
    // line = it.next();
    // if (StringUtils.isBlank(line)) {
    // continue;
    // }
    // 
    // buffer.append("buffer.append(\"").append(StringUtils.replace(line,
    // "\"", "\\\""))
    // .append("\");\n");
    // 
    // }
    // 
    // System.out.println(buffer);
    // List<String> lines = FileUtils.readLines(new
    // File("D:\\Downloads\\10.46.74.6"));
    // // for (int i = 0; i < 5; i++) {
    // // System.out.println("dddd:" + lines.get(i));
    // // }
    // 
    // String paykind = "00034204508e7312015098c945a247aa";
    // String split = "\001";
    // 
    // LineIterator iterator = FileUtils.lineIterator(new
    // File("D:\\Downloads\\compare2.txt"));
    // int lineCount = 0;
    // String[] array = null;
    // String[] paykindArray = null;
    // 
    // while (iterator.hasNext()) {
    // array = StringUtils.split(iterator.next(), split);
    // paykindArray = StringUtils.split(array[2], ";");
    // 
    // // if ("000342045215b3a801521680bdce093d".equals(array[0])) {
    // // System.out.println(array[1]);
    // // }
    // if ("\\N".equals(array[1])) {
    // // System.out.println(array[0]);
    // continue;
    // }
    // 
    // if (!lines.contains(array[0])) {
    // for (String payinfo : paykindArray) {
    // // System.out.println(payinfo);
    // // StringUtils.substring(payinfo, 32);
    // if (paykind.equals(StringUtils.split(payinfo, "_")[1])) {
    // System.out.println(array[2]);
    // }
    // }
    // System.out.println(array[0]);
    // }
    // 
    // lineCount++;
    // }
    // 
    // System.out.println(lineCount);
    // System.out.println(new Double(1.233d) == 1.233d);
    // 
    // System.out.println((123.3d / 100) == 1.233d);
    // 
    // BigDecimal f = new BigDecimal(("132541.35"));
    // 
    // System.out.println(0.00035d - 0.000007d);
    // 
    // System.out.println(f.floatValue());
    // System.out.println(f.doubleValue());
    // System.out.println(f);
    // 
    // System.out.println("=============================");
    // System.out.println(Double.parseDouble("1325555555555555541.35"));
    // System.out.println(Float.parseFloat("0.123456771"));
    // System.out.println(Float.parseFloat("132541.35"));
    // LineIterator it = FileUtils.lineIterator(
    // new File("C:\\Users\\baisui\\Desktop\\00038623.txt"));
    // String[] array = null;
    // boolean findSpec = false;
    // 
    // Set<String> tidSet = new HashSet<String>();
    // 
    // while (it.hasNext()) {
    // array = it.nextLine().split(",");
    // findSpec = false;
    // StringBuffer buffer = new StringBuffer();
    // for (String p : array) {
    // if (StringUtils.indexOf(p, "totalpay_id") > -1) {
    // tidSet.add(p);
    // buffer.append(p);
    // }
    // if (StringUtils.indexOf(p, "special_fee_summary") > -1) {
    // 
    // findSpec = true;
    // buffer.append(p);
    // }
    // 
    // if (StringUtils.indexOf(p, "_version_") > -1) {
    // buffer.append(p);
    // }
    // 
    // }
    // if (findSpec) {
    // System.out.println(buffer);
    // }
    // 
    // }
    // 
    // System.out.println("id set size:" + tidSet.size());
    // 
    // BufferedWriter out = new BufferedWriter(
    // new OutputStreamWriter(FileUtils.openOutputStream(
    // new File("C:\\Users\\baisui\\Desktop\\out.txt"))))
    // 
    // RoundingMode[] roundModeAry = new RoundingMode[] {
    // RoundingMode.CEILING,
    // RoundingMode.DOWN, RoundingMode.FLOOR, RoundingMode.HALF_DOWN,
    // RoundingMode.HALF_EVEN, RoundingMode.HALF_UP, RoundingMode.UP };
    // MathContext BigDecimalContext = null;
    // String line = null;
    // String[] args = null;
    // for (RoundingMode r : roundModeAry) {
    // for (int i = 1; i < 9; i++) {
    // BigDecimalContext = new MathContext(i, r);
    // 
    // LineIterator it = FileUtils.lineIterator(
    // new File("C:\\Users\\baisui\\Desktop\\out.txt"));
    // // LineIterator it = FileUtils.lineIterator(new File(
    // //
    // "C:\\Users\\baisui\\Desktop\\00034204508e749e015098c80cff2aff.txt"));
    // 
    // BigDecimal ratioFee = null;
    // BigDecimal sum = null;
    // while (it.hasNext()) {
    // // out.write(StringUtils.substringAfterLast(it.nextLine(),
    // // "-"));
    // // out.newLine();
    // 
    // line = it.nextLine();
    // args = StringUtils.split(line, "-");
    // 
    // ratioFee = new BigDecimal(Float.parseFloat(args[0]),
    // BigDecimalContext);
    // 
    // if (sum == null) {
    // sum = ratioFee;
    // } else {
    // sum = sum.add(ratioFee);
    // }
    // 
    // }
    // System.out.println(
    // "i:" + i + ",round:" + r + " " + sum.floatValue());
    // 
    // }
    // }
    // logger.warn("baisui-" + pair[2] + "-" + pair[5]);
    // 
    // out.flush();
    // out.close();
    // System.out.println("ddddd");
    // System.out.println(URLEncoder.encode("+1DAY"));
    // 
    // System.out.println(Test.class
    // .getResource("/org/apache/commons/dbcp/BasicDataSource.class"));
    // 
    // System.out.println(Test.class.getResource("/java/lang/String.class"));
    // System.out.println(0.05 + 0.01);
    // System.out.println(1.0 - 0.42);
    // System.out.println(4.015 * 100);
    // System.out.println(123.3 / 100);
    // System.out.println(0.05);
    // System.out.println("==========================================");
    // // BigDecimal bd3 = new BigDecimal(String.valueOf(0.05));
    // //
    // // BigDecimal bd4 = new BigDecimal(String.valueOf(0.01));
    // MathContext context = new MathContext(2);
    // BigDecimal b = new BigDecimal(2.0, context);
    // 
    // // BigDecimal d = new BigD
    // 
    // System.out.println(b.floatValue());
    // System.out.println(b.doubleValue());
    // System.out.println(Test.class.getClassLoader().getResource(
    // "org/json/JSONTokener.class"));
    // 
    // Class clazz = Class.forName("org.json.JSONTokener");
    // 
    // Constructor<?>[] cc = clazz.getConstructors();
    // for (Constructor<?> c : cc) {
    // 
    // System.out.print(c.getName() + ":" + c.getParameterTypes().length
    // + ",");
    // for (Class<?> p : c.getParameterTypes()) {
    // System.out.print(p.getName() + ",");
    // }
    // System.out.println();
    // }
    // 
    // final Future<String> callResult = exec.submit(new Callable<String>()
    // {
    // @Override
    // public String call() throws Exception {
    // int i = 1;
    // while (i++ < 15) {
    // System.out.println(i);
    // Thread.sleep(1000);
    // }
    // return "hello";
    // }
    // });
    // 
    // final Thread t = new Thread(new Runnable() {
    // 
    // @Override
    // public void run() {
    // 
    // try {
    // Thread.sleep(6000);
    // 
    // callResult.cancel(true);
    // System.out.println("cancel the thread");
    // } catch (InterruptedException e) {
    // 
    // e.printStackTrace();
    // }
    // }
    // 
    // });
    // 
    // t.start();
    // 
    // System.out.println(callResult.get());
    // File dir = new File("c://Users//baisui//.ssh");
    // File dir = new File("c://Users//baisui//.ssh");
    // 
    // LineIterator it = FileUtils.lineIterator(dir);
    // int i = 0;
    // if (it.hasNext()) {
    // i++;
    // }
    // 
    // System.out.println("line:" + i);
    // System.out.println("&#183;");
    }
}
