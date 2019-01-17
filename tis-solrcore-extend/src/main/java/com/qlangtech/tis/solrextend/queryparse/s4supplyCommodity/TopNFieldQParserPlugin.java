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
package com.qlangtech.tis.solrextend.queryparse.s4supplyCommodity;

import com.qlangtech.tis.solrextend.queryparse.BitQuery;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.search.FieldDoc;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.SortField.Type;
import org.apache.lucene.search.TopFieldCollector;
import org.apache.lucene.search.TopFieldDocs;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.FixedBitSet;
import org.apache.solr.common.SolrException;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.search.LuceneQParserPlugin;
import org.apache.solr.search.QParser;
import org.apache.solr.search.SyntaxError;

/*
 * Created by Qinjiu(Qinjiu@2dfire.com) on 2/23/2017.
 * TIS 供应链IN 操作优化方案 http://k.2dfire.net/pages/viewpage.action?pageId=260210876
 * 此qp用来为streaming expression优化做服务
 * 给定query条件和排序方式，找出top n，在此基础上做streaming的innerJoin会快很多
 *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class TopNFieldQParserPlugin extends LuceneQParserPlugin {

    // 默认搜出100条结果
    private static int DEFAULT_ROW_COUNT = 100;

    private static int MAX_ROW_COUNT = 20000;

    // 期望搜出的结果
    private static String ROWS = "rows";

    // 排序字段
    private static String SORT = "sort";

    // 翻页，提供的上一页最后一个ID
    private static String AFTER_ID = "afterId";

    // 翻页，提供afterId对应排序字段的值
    private static String AFTER_VALUE = "afterValue";

    private static String DESC = "desc";

    private static String COMMA = ",";

    private static String SORT_FIELDS_SPLITOR = ";";

    private static Type getSortType(String type) {
        switch(type.toUpperCase()) {
            case "INT":
                return Type.INT;
            case "FLOAT":
                return Type.FLOAT;
            case "LONG":
                return Type.LONG;
            case "DOUBLE":
                return Type.DOUBLE;
            case "STRING":
                return Type.STRING;
            default:
                return Type.STRING;
        }
    }

    @Override
    public void init(NamedList args) {
    }

    @Override
    public QParser createParser(String qstr, SolrParams localParams, SolrParams params, SolrQueryRequest req) {
        int rowCount = DEFAULT_ROW_COUNT;
        if (StringUtils.equals(localParams.get(ROWS, ""), "unlimited")) {
            rowCount = MAX_ROW_COUNT;
        }
        String sort = localParams.get(SORT);
        if (StringUtils.isBlank(sort)) {
            throw new IllegalStateException("sort is null in " + localParams);
        }
        // 用作翻页，只找此docId以后的topN个命中
        int afterDocId = localParams.getInt(AFTER_ID, -1);
        try {
            QParser parser = super.createParser(qstr, localParams, params, req);
            Query sortedQuery = parser.parse();
            String[] sortString = sort.split(SORT_FIELDS_SPLITOR);
            int sortFieldsSize = sortString.length;
            SortField[] sortFields = new SortField[sortFieldsSize];
            for (int i = 0; i < sortFieldsSize; i++) {
                String[] pros = sortString[i].split(COMMA);
                assert (pros.length == 3);
                sortFields[i] = new SortField(pros[0], getSortType(pros[2]), DESC.equals(pros[1]));
            }
            TopFieldCollector collector;
            if (afterDocId <= -1) {
                collector = TopFieldCollector.create(new Sort(sortFields), rowCount, false, false, false);
            } else {
                Object[] fields = new Object[sortFieldsSize];
                String[] afterValues = localParams.get(AFTER_VALUE, "").split(SORT_FIELDS_SPLITOR);
                assert (afterValues.length == sortFieldsSize);
                for (int i = 0; i < sortFieldsSize; i++) {
                    switch(sortFields[i].getType()) {
                        case INT:
                            fields[i] = Integer.parseInt(afterValues[i]);
                            break;
                        case LONG:
                            fields[i] = Long.parseLong(afterValues[i]);
                            break;
                        case FLOAT:
                            fields[i] = Float.parseFloat(afterValues[i]);
                            break;
                        case DOUBLE:
                            fields[i] = Double.parseDouble(afterValues[i]);
                            break;
                        case STRING:
                            fields[i] = new BytesRef(afterValues[i]);
                            break;
                        default:
                            throw new SolrException(SolrException.ErrorCode.BAD_REQUEST, "can not get value type of " + sortFields[i].getType());
                    }
                }
                collector = TopFieldCollector.create(new Sort(sortFields), rowCount, new FieldDoc(afterDocId, 1, fields), false, false, false);
            }
            req.getSearcher().search(sortedQuery, collector);
            final TopFieldDocs sortedDocs = collector.topDocs();
            if (sortedDocs.totalHits > MAX_ROW_COUNT) {
                // 此处应该抛出异常，这么多如果用来排序，可能会导致OOM
                throw new SolrException(SolrException.ErrorCode.BAD_REQUEST, "totalHits " + sortedDocs.totalHits + " is more than 20000, please optimize your query string, " + qstr);
            }
            // bitSet用来快速排序
            final FixedBitSet bitSet = new FixedBitSet(req.getSearcher().getIndexReader().maxDoc());
            for (ScoreDoc hitDoc : sortedDocs.scoreDocs) {
                bitSet.set(hitDoc.doc);
            }
            final BitQuery bitQuery = new BitQuery(bitSet);
            return new QParser(qstr, localParams, params, req) {

                @Override
                public Query parse() throws SyntaxError {
                    return bitQuery;
                }
            };
        } catch (Exception e) {
            throw new SolrException(SolrException.ErrorCode.SERVER_ERROR, e);
        }
    }
}
