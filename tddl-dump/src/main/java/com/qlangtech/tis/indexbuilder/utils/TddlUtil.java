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
package com.qlangtech.tis.indexbuilder.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/* *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class TddlUtil {

    public static String map2Str(Map<String, String> map) {
        String need_cols = "";
        StringBuilder builder_temp = new StringBuilder();
        for (Entry<String, String> entry : map.entrySet()) {
            builder_temp.append(entry.getKey()).append(" ").append(entry.getValue()).append(",");
        }
        need_cols = builder_temp.substring(0, builder_temp.length() - 1).toString();
        return need_cols;
    }

    public static Map<String, String> str2Map(String str) {
        Map<String, String> map = new HashMap<String, String>();
        String[] delites = str.split(",");
        for (int i = 0; i < delites.length; i++) {
            String[] blanks = delites[i].split(" ");
            map.put(blanks[0], blanks[1]);
        }
        return map;
    }

    // ����������
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

    public static String columTrans(String colum, String type) {
        String new_colum = "";
        if ("datetime".equals(type) || "timestamp".equals(type)) {
            new_colum = "DATE_FORMAT(" + colum + ",'%Y%m%d%H%i%S') as " + colum;
        } else if ("date".equalsIgnoreCase(type)) {
            new_colum = "DATE_FORMAT(" + colum + ",'%Y%m%d') as " + colum;
        } else {
            return colum;
        }
        return new_colum;
    }
}
