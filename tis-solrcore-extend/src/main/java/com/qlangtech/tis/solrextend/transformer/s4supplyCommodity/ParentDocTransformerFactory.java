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
package com.qlangtech.tis.solrextend.transformer.s4supplyCommodity;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.LazyDocument;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.join.BitSetProducer;
import org.apache.lucene.search.join.QueryBitSetProducer;
import org.apache.lucene.search.join.ScoreMode;
import org.apache.lucene.search.join.ToParentBlockJoinQuery;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrException;
import org.apache.solr.common.SolrException.ErrorCode;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.response.transform.DocTransformer;
import org.apache.solr.response.transform.TransformerFactory;
import org.apache.solr.schema.FieldType;
import org.apache.solr.schema.IndexSchema;
import org.apache.solr.schema.SchemaField;
import org.apache.solr.search.DocIterator;
import org.apache.solr.search.DocList;
import org.apache.solr.search.QParser;
import org.apache.solr.search.QueryWrapperFilter;
import org.apache.solr.search.SyntaxError;

/* *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class ParentDocTransformerFactory extends TransformerFactory {

    public static final String EXTRA_FIELDS = "f";

    @Override
    public DocTransformer create(String field, SolrParams params, SolrQueryRequest req) {
        SchemaField uniqueKeyField = req.getSchema().getUniqueKeyField();
        if (uniqueKeyField == null) {
            throw new SolrException(ErrorCode.BAD_REQUEST, "ParentDocTransformer requires the schema to have a uniqueKeyField.");
        }
        String parentFilter = params.get("parentFilter", null);
        if (parentFilter == null) {
            throw new SolrException(ErrorCode.BAD_REQUEST, "Parent filter should be sent as parentFilter=filterCondition");
        }
        String childFilter = params.get("childFilter", null);
        // int limit = params.getInt("limit", 10);
        String fieldsString = params.get(EXTRA_FIELDS, null);
        BitSetProducer parentsFilter;
        try {
            Query parentFilterQuery = QParser.getParser(parentFilter, null, req).getQuery();
            parentsFilter = new QueryBitSetProducer(new QueryWrapperFilter(parentFilterQuery));
        } catch (SyntaxError syntaxError) {
            throw new SolrException(ErrorCode.BAD_REQUEST, "Failed to create correct parent filter query");
        }
        Query childFilterQuery = null;
        if (childFilter != null) {
            try {
                childFilterQuery = QParser.getParser(childFilter, null, req).getQuery();
            } catch (SyntaxError syntaxError) {
                throw new SolrException(ErrorCode.BAD_REQUEST, "Failed to create correct child filter query");
            }
        }
        return new ParentDocTransformer(field, parentsFilter, uniqueKeyField, req.getSchema(), childFilterQuery, 1, fieldsString);
    }
}

class ParentDocTransformer extends DocTransformer {

    private final String name;

    private final SchemaField idField;

    // private final IndexSchema schema;
    private BitSetProducer parentsFilter;

    private Query childFilterQuery;

    private int limit;

    private String[] fields = null;

    // 父Doc需要取的字段
    private Set<String> fieldsSet;

    ParentDocTransformer(String name, final BitSetProducer parentsFilter, final SchemaField idField, IndexSchema schema, final Query childFilterQuery, int limit, String fieldsString) {
        this.name = name;
        this.idField = idField;
        // this.schema = schema;
        this.parentsFilter = parentsFilter;
        this.childFilterQuery = childFilterQuery;
        this.limit = limit;
        if (fieldsString != null) {
            this.fields = StringUtils.split(fieldsString, ",");
            fieldsSet = new HashSet<>(Arrays.asList(this.fields));
        }
        if (CollectionUtils.isEmpty(fieldsSet)) {
            throw new IllegalStateException("fieldsSet can not be null");
        }
    }

    @Override
    public String[] getExtraRequestFields() {
        return this.fieldsSet.toArray(new String[] {});
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void transform(SolrDocument doc, int docid, float score) {
        FieldType idFt = idField.getType();
        Object childIdField = doc.getFirstValue(idField.getName());
        String childIdExt = childIdField instanceof IndexableField ? idFt.toExternal((IndexableField) childIdField) : childIdField.toString();
        // 将子文档中的记录删除
        for (String parentField : this.fieldsSet) {
            doc.removeFields(parentField);
        }
        try {
            IndexableField fieldValue = null;
            Query childQuery = idFt.getFieldQuery(null, idField, childIdExt);
            Query query = new ToParentBlockJoinQuery(childQuery, parentsFilter, ScoreMode.None);
            DocList parents = context.getSearcher().getDocList(query, childFilterQuery, new Sort(), 0, limit);
            if (parents.matches() > 0) {
                DocIterator i = parents.iterator();
                if (i.hasNext()) {
                    Integer parentDocNum = i.next();
                    Document parentDoc = context.getSearcher().doc(parentDocNum, fieldsSet);
                    // SolrDocument solrParentDoc = DocsStreamer.getDoc(parentDoc, schema);
                    for (String field : fieldsSet) {
                        // .getFieldValue(field);
                        fieldValue = parentDoc.getField(field);
                        if (fieldValue == null || fieldValue instanceof LazyDocument.LazyField) {
                            continue;
                        }
                        doc.addField(field, fieldValue);
                    }
                    return;
                }
            }
        } catch (IOException e) {
            // doc.put(name, "Could not fetch parent Documents");
            throw new RuntimeException(e);
        }
    }

    public String[] getFields() {
        return this.fields;
    }
}
