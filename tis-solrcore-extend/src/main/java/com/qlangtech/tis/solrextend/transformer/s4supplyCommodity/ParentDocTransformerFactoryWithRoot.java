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
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.LazyDocument;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.BooleanQuery.Builder;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.TermQuery;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrException;
import org.apache.solr.common.SolrException.ErrorCode;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.response.DocsStreamer;
import org.apache.solr.response.transform.DocTransformer;
import org.apache.solr.response.transform.TransformerFactory;
import org.apache.solr.schema.IndexSchema;
import org.apache.solr.schema.SchemaField;
import org.apache.solr.search.DocIterator;
import org.apache.solr.search.DocList;
import org.apache.solr.search.QParser;
import org.apache.solr.search.SyntaxError;
import org.apache.solr.update.TisDirectUpdateHandler2;

/* *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class ParentDocTransformerFactoryWithRoot extends TransformerFactory {

    private static final int CACHE_SIZE = 100;

    static final LinkedHashMap<String, SolrDocument> ParentQueryCache = new LinkedHashMap<String, SolrDocument>(CACHE_SIZE) {

        @Override
        protected boolean removeEldestEntry(Map.Entry<String, SolrDocument> eldest) {
            return size() > CACHE_SIZE;
        }
    };

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
        int limit = params.getInt("limit", 10);
        String fieldsString = params.get(ParentDocTransformerFactory.EXTRA_FIELDS, null);
        Query parentFilterQuery;
        try {
            parentFilterQuery = QParser.getParser(parentFilter, null, req).getQuery();
        } catch (SyntaxError syntaxError) {
            throw new SolrException(ErrorCode.BAD_REQUEST, "Failed to create correct parent filter query.");
        }
        Query childFilterQuery = null;
        if (childFilter != null) {
            try {
                childFilterQuery = QParser.getParser(childFilter, null, req).getQuery();
            } catch (SyntaxError syntaxError) {
                throw new SolrException(ErrorCode.BAD_REQUEST, "Failed to create correct child filter query.");
            }
        }
        return new ParentDocTransFormerWithRoot(field, parentFilterQuery, req.getSchema(), childFilterQuery, limit, fieldsString);
    }

    public static String val(Object o) {
        if (o == null) {
            return "";
        }
        if (!(o instanceof org.apache.lucene.document.Field)) {
            return String.valueOf(o);
        }
        org.apache.lucene.document.Field f = (org.apache.lucene.document.Field) o;
        return f.stringValue();
    }
}

class ParentDocTransFormerWithRoot extends DocTransformer {

    private final String name;

    private final IndexSchema schema;

    private Query parentFilterQuery;

    private Query childFilterQuery;

    private int limit;

    private String[] fields = null;

    ParentDocTransFormerWithRoot(String name, final Query parentFilterQuery, IndexSchema schema, final Query childFilterQuery, int limit, String fieldsString) {
        this.name = name;
        this.schema = schema;
        this.parentFilterQuery = parentFilterQuery;
        this.childFilterQuery = childFilterQuery;
        this.limit = limit;
        if (fieldsString != null) {
            this.fields = StringUtils.split(fieldsString, ",");
        }
    }

    @Override
    public String getName() {
        return name;
    }

    private void addFields(SolrDocument solrParentDoc, SolrDocument doc) {
        for (String field : fields) {
            Object fieldValue = solrParentDoc.getFieldValue(field);
            if (fieldValue instanceof LazyDocument.LazyField)
                throw new IllegalStateException("the parent doc has not field " + field);
            else
                doc.addField(field, solrParentDoc.getFieldValue(field));
        }
    }

    @Override
    public void transform(SolrDocument doc, int docid, float score) {
        if (fields == null)
            return;
        String rootString = ParentDocTransformerFactoryWithRoot.val(doc.getFieldValue(TisDirectUpdateHandler2.ROOT_FIELD));
        if (rootString == null) {
            throw new IllegalArgumentException("doc" + doc + " must have " + TisDirectUpdateHandler2.ROOT_FIELD + " field");
        }
        SolrDocument solrParentDoc = ParentDocTransformerFactoryWithRoot.ParentQueryCache.get(rootString);
        if (solrParentDoc != null) {
            addFields(solrParentDoc, doc);
        } else {
            TermQuery rootQuery = new TermQuery(new Term(TisDirectUpdateHandler2.ROOT_FIELD, rootString));
            Builder builder = new Builder();
            builder.add(parentFilterQuery, BooleanClause.Occur.MUST);
            builder.add(rootQuery, BooleanClause.Occur.MUST);
            BooleanQuery bQuery = builder.build();
            try {
                DocList parents = context.getSearcher().getDocList(bQuery, childFilterQuery, new Sort(), 0, limit);
                if (parents.matches() > 0) {
                    DocIterator iterator = parents.iterator();
                    if (iterator.hasNext()) {
                        Integer parentDocNum = iterator.next();
                        Document parentDoc = context.getSearcher().doc(parentDocNum);
                        solrParentDoc = DocsStreamer.getDoc(parentDoc, schema);
                        ParentDocTransformerFactoryWithRoot.ParentQueryCache.putIfAbsent(rootString, solrParentDoc);
                        addFields(solrParentDoc, doc);
                    }
                }
            } catch (IOException e) {
                doc.put(name, "Could not fetch parent Documents");
            }
        }
    }

    public String[] getFields() {
        return fields;
    }
}
