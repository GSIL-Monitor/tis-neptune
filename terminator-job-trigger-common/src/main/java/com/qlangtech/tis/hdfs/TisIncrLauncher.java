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

import java.lang.reflect.Constructor;
import java.net.URL;
import java.net.URLClassLoader;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import com.qlangtech.tis.pubhook.common.RunEnvironment;

/* *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class TisIncrLauncher extends Configured implements Tool {

    private static final String APP_NAME = "transferApp";

    public static final String PARAM_OPTION_LOCAL_JAR_DIR = "localjardir";

    public static final String PARAM_OPTION_LOCAL_QUEUE = "queue";

    public static final String ENVIRONMENT_INCR_EXEC_GROUP = "incr_exec_group";

    // private static final Logger logger =
    // LoggerFactory.getLogger(TisIncrLauncher.class);
    @Override
    @SuppressWarnings("all")
    public int run(String[] args) throws Exception {
        if (args.length < 1) {
            throw new IllegalArgumentException("args length can not less than 1");
        }
        CommandLine commandLine = processCommandLineArgs(APP_NAME, getOptions(), args);
        Class<Runnable> deployJarClazz = (Class<Runnable>) Thread.currentThread().getContextClassLoader().loadClass("com.dfire.tis.realtime.yarn.TransferStart");
        URLClassLoader loader = (URLClassLoader) deployJarClazz.getClassLoader();
        System.out.println("=URLClassLoader====================");
        for (URL url : loader.getURLs()) {
            System.out.println(url);
        }
        System.out.println("=args=============================");
        for (Option arg : commandLine.getOptions()) {
            System.out.println("key:" + arg.getOpt() + ",value:" + arg.getValue());
        }
        System.out.println("================================");
        Constructor<Runnable> constructor = deployJarClazz.getConstructor(CommandLine.class);
        Runnable runnable = constructor.newInstance(commandLine);
        runnable.run();
        return 0;
    }

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        int res = ToolRunner.run(new Configuration(), new TisIncrLauncher(), args);
        System.exit(res);
    }

    /**
     * Parses the command-line arguments passed by the user.
     */
    private static CommandLine processCommandLineArgs(String app, Options options, String[] args) {
        CommandLine cli = null;
        try {
            cli = (new GnuParser()).parse(options, args);
        } catch (ParseException exp) {
            boolean hasHelpArg = false;
            if (args != null && args.length > 0) {
                for (int z = 0; z < args.length; z++) {
                    if ("-h".equals(args[z]) || "-help".equals(args[z])) {
                        hasHelpArg = true;
                        break;
                    }
                }
            }
            if (!hasHelpArg) {
                System.err.println("Failed to parse command-line arguments due to: " + exp.getMessage());
            }
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp(app, options);
            System.exit(1);
        }
        if (cli.hasOption("help")) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp(app, options);
            System.exit(0);
        }
        return cli;
    }

    private static Options getOptions() {
        Options options = new Options();
        options.addOption("h", "help", false, "Print this message");
        options.addOption("v", "verbose", false, "Generate verbose log messages");
        Option[] opts = getClientOptions();
        for (int i = 0; i < opts.length; i++) {
            options.addOption(opts[i]);
        }
        return options;
    }

    @SuppressWarnings("all")
    public static Option[] getClientOptions() {
        return new Option[] { OptionBuilder.withArgName("LOCAL_JAR_DIR").hasArg().isRequired(false).withDescription("local jar dir").create(PARAM_OPTION_LOCAL_JAR_DIR), // 当前增量任务组
        OptionBuilder.withArgName(ENVIRONMENT_INCR_EXEC_GROUP).hasArg().isRequired(true).withDescription("incr run group").create(ENVIRONMENT_INCR_EXEC_GROUP), // Queue
        OptionBuilder.withArgName("QUEUE").hasArg().isRequired(true).withDescription("yarn queue").create(PARAM_OPTION_LOCAL_QUEUE), OptionBuilder.withArgName("RUNTIME").hasArg().isRequired(true).withDescription("execute runtime").create(RunEnvironment.KEY_RUNTIME), OptionBuilder.withArgName("libjars").hasArg().isRequired(false).withDescription("lib jars").create("libjars") };
    }
}
