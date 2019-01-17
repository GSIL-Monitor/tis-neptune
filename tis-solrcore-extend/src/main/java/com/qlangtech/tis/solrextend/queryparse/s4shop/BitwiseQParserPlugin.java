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
package com.qlangtech.tis.solrextend.queryparse.s4shop;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.search.QParser;
import org.apache.solr.search.QParserPlugin;
import org.apache.solr.search.SyntaxError;
import com.qlangtech.tis.solrextend.fieldtype.BitwiseField;

/*
 * 支持用户 {!bitwise f=xx}1*0* 这样的查询方式，可以明确表示某一位为非的状态
 *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class BitwiseQParserPlugin extends QParserPlugin {

    private static final Pattern PATTERN_QUERY = Pattern.compile("[0-1]{1}[0-1|\\*]*");

    // private static final Logger log = LoggerFactory.getLogger(BitwiseQParserPlugin.class);
    @Override
    public QParser createParser(String qstr, SolrParams localParams, SolrParams params, SolrQueryRequest req) {
        final String fieldName = localParams.get("f");
        if (StringUtils.isBlank(fieldName)) {
            throw new IllegalArgumentException("local param 'f' can not be null");
        }
        Matcher matcher = PATTERN_QUERY.matcher(qstr);
        if (!matcher.matches()) {
            throw new IllegalStateException("param qstr is not match the pattern:" + PATTERN_QUERY);
        }
        int length = StringUtils.length(qstr);
        char c;
        final BooleanQuery.Builder qbuilder = new BooleanQuery.Builder();
        for (int i = length - 1; i >= 0; i--) {
            c = qstr.charAt(length - (i + 1));
            if (c == '1') {
                qbuilder.add(createTermQuery(fieldName, i), Occur.MUST);
            } else if (c == '0') {
                qbuilder.add(createTermQuery(fieldName, i), Occur.MUST_NOT);
            }
        }
        return new QParser(qstr, localParams, params, req) {

            @Override
            public Query parse() throws SyntaxError {
                return qbuilder.build();
            }
        };
    }

    private TermQuery createTermQuery(final String fieldName, int i) {
        String val = String.valueOf(BitwiseField.masks.get(i));
        // log.info("index:" + i + ",val:" + val);
        return new TermQuery(new Term(fieldName, val));
    // return q;
    }

    public static void main(String[] args) {
        Matcher matcher = PATTERN_QUERY.matcher("1***");
        System.out.println(matcher.matches());
    }
}
