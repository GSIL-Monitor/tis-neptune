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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/* *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class TddlUtil {

    /*
	 * 数据量与组数的映射关系 0< x <=100W 1组 100W< x <=1000W 32组 1000W< x <=10000W 256组
	 * 根据预估的值得到的odps分区
	 */
    public static int getGroupNum(long totalNum) {
        if ((totalNum <= 1000000) && totalNum > 0) {
            return 1;
        } else if ((totalNum > 1000000) && (totalNum <= 10000000)) {
            return 32;
        } else if (totalNum > 10000000) {
            return 256;
        }
        throw new IllegalArgumentException("totalNum is error:" + totalNum);
    }

    public static int estimateGroupNum(Map<String, Long> successGroupMap) {
        long totalNum = 0;
        for (Entry<String, Long> entry : successGroupMap.entrySet()) {
            totalNum += entry.getValue();
        }
        // 根据实际导入的值得到的分组，用于后续build
        if (totalNum > 0 && (totalNum <= 20000000)) {
            return 1;
        } else if (totalNum > 20000000 && totalNum <= 50000000) {
            return 2;
        } else if (totalNum > 50000000 && totalNum <= 100000000) {
            return 4;
        } else if (totalNum > 100000000 && totalNum <= 500000000) {
            return 8;
        } else if (totalNum > 500000000 && totalNum <= 1000000000) {
            return 16;
        } else if (totalNum > 1000000000 && totalNum <= 2000000000) {
            return 32;
        }
        throw new IllegalArgumentException("illegal totle record number:" + totalNum);
    }

    public static String map2Str(Map<String, String> map) {
        String need_cols = "";
        StringBuilder builder_temp = new StringBuilder();
        Collection<String> keyset = map.keySet();
        List<String> list = new ArrayList<String>(keyset);
        Collections.sort(list);
        for (String str : list) {
            builder_temp.append(str).append(" ").append(map.get(str)).append(",");
        }
        need_cols = builder_temp.substring(0, builder_temp.length() - 1).toString();
        return need_cols;
    }

    // 两个集合求交
    public static Map<String, String> retain(List<String> schema_cols, Map<String, String> tddl_cols) {
        Map<String, String> need_dump_cols = new HashMap<String, String>();
        for (String schema_col : schema_cols) {
            if (tddl_cols.containsKey(schema_col)) {
                String type = tddl_cols.get(schema_col);
                need_dump_cols.put(schema_col, type);
            }
        }
        return need_dump_cols;
    }

    public static Map<String, String> transType(Map<String, String> merge_dump_cols) {
        Map<String, String> tddl_trans_cols = new HashMap<String, String>();
        for (Entry<String, String> entry : merge_dump_cols.entrySet()) {
            tddl_trans_cols.put(entry.getKey(), getOdpsType(entry.getValue()));
        }
        return tddl_trans_cols;
    }

    public static String getOdpsType(String rdsType) {
        if ("tinyint".equals(rdsType) || "bit".equals(rdsType) || "smallint".equals(rdsType) || "mediumint".equals(rdsType) || "int".equals(rdsType) || "integer".equals(rdsType) || "bigint".equals(rdsType) || "datetime".equals(rdsType) || "date".equals(rdsType) || "timestamp".equals(rdsType) || "time".equals(rdsType) || "year".equals(rdsType))
            return "Bigint";
        if ("float".equals(rdsType) || "double".equals(rdsType) || "decimal".equals(rdsType) || "real".equals(rdsType))
            return "Double";
        return "String";
    }

    public static void main(String[] args) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("price", "String");
        map.put("book", "paoding");
        map.put("adm", "Inter");
        map.put("oh", "aa");
        System.out.println(map2Str(map));
    }
}
