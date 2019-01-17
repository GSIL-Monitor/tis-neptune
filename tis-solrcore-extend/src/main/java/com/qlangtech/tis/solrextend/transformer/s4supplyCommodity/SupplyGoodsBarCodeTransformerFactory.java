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

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.join.BitSetProducer;
import org.apache.lucene.search.join.QueryBitSetProducer;
import org.apache.lucene.search.join.ToChildBlockJoinQuery;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrException;
import org.apache.solr.common.SolrException.ErrorCode;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.response.DocsStreamer;
import org.apache.solr.response.transform.DocTransformer;
import org.apache.solr.response.transform.TransformerFactory;
import org.apache.solr.schema.FieldType;
import org.apache.solr.schema.IndexSchema;
import org.apache.solr.schema.SchemaField;
import org.apache.solr.search.*;
import java.io.IOException;

/*
 * Created by Qinjiu on 2016/12/20.
 *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class SupplyGoodsBarCodeTransformerFactory extends TransformerFactory {

    @Override
    public DocTransformer create(String field, SolrParams params, SolrQueryRequest req) {
        SchemaField uniqueKeyField = req.getSchema().getUniqueKeyField();
        if (uniqueKeyField == null) {
            throw new SolrException(ErrorCode.BAD_REQUEST, "SupplyGoodsBarCodeTransformerFactory requires the schema to have a uniqueKeyFIeld.");
        } else {
            String parentFilter = params.get("parentFilter");
            if (parentFilter == null) {
                throw new SolrException(ErrorCode.BAD_REQUEST, "Parent filter shoule be send as parentFilter=filterCondition");
            } else {
                String childFilter = params.get("childFilter");
                int limit = params.getInt("limit", 10);
                QueryBitSetProducer parentsFilter;
                Query childFilterQuery;
                try {
                    childFilterQuery = QParser.getParser(parentFilter, (String) null, req).getQuery();
                    parentsFilter = new QueryBitSetProducer(new QueryWrapperFilter(childFilterQuery));
                } catch (SyntaxError var12) {
                    throw new SolrException(ErrorCode.BAD_REQUEST, "Failed to create correct parent filter query");
                }
                childFilterQuery = null;
                if (childFilter != null) {
                    try {
                        childFilterQuery = QParser.getParser(childFilter, (String) null, req).getQuery();
                    } catch (SyntaxError var11) {
                        throw new SolrException(ErrorCode.BAD_REQUEST, "Failed to create correct child filter query");
                    }
                }
                return new SupplyGoodsBarCodeTransformer(field, parentsFilter, uniqueKeyField, req.getSchema(), childFilterQuery, limit);
            }
        }
    }
}

class SupplyGoodsBarCodeTransformer extends DocTransformer {

    private final String name;

    private final SchemaField idField;

    private final IndexSchema schema;

    private BitSetProducer parentsFilter;

    private Query childFilterQuery;

    private int limit;

    private static final String SKU_CREATE_TIME = "sku_create_time";

    private static final String SKU_BAR_CODE = "sku_bar_code";

    private static final String BAR_CODE = "bar_code";

    SupplyGoodsBarCodeTransformer(String name, BitSetProducer parentsFilter, SchemaField idField, IndexSchema schema, Query childFilterQuery, int limit) {
        this.name = name;
        this.idField = idField;
        this.schema = schema;
        this.parentsFilter = parentsFilter;
        this.childFilterQuery = childFilterQuery;
        this.limit = limit;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void transform(SolrDocument doc, int docid, float score) throws IOException {
        FieldType idFt = this.idField.getType();
        Object parentIdField = doc.getFieldValue(this.idField.getName());
        String parentIdExt = parentIdField instanceof IndexableField ? idFt.toExternal((IndexableField) parentIdField) : parentIdField.toString();
        try {
            Query e = idFt.getFieldQuery((QParser) null, this.idField, parentIdExt);
            ToChildBlockJoinQuery query = new ToChildBlockJoinQuery(e, this.parentsFilter);
            DocList children = this.context.getSearcher().getDocList(query, this.childFilterQuery, new Sort(), 0, this.limit);
            if (children.matches() > 0) {
                DocIterator i = children.iterator();
                int sku_create_time = Integer.MAX_VALUE;
                String sku_bar_code = null;
                while (i.hasNext()) {
                    Integer childDocNum = (Integer) i.next();
                    Document childDoc = this.context.getSearcher().doc(childDocNum);
                    SolrDocument solrChildDoc = DocsStreamer.getDoc(childDoc, this.schema);
                    try {
                        int new_sku_create_time = Integer.parseInt(getFieldString(solrChildDoc.getFirstValue(SKU_CREATE_TIME)));
                        if (new_sku_create_time < sku_create_time) {
                            sku_create_time = new_sku_create_time;
                            sku_bar_code = getFieldString(solrChildDoc.getFirstValue(SKU_BAR_CODE));
                        }
                    } catch (NumberFormatException exception) {
                        throw new IllegalArgumentException("cannot convert sku_create_time to integer");
                    }
                }
                if (sku_bar_code != null) {
                    doc.addField(BAR_CODE, sku_bar_code);
                } else {
                    throw new IllegalStateException("the parent doc doesn't have correct bar_code");
                }
            }
        } catch (IOException var14) {
            doc.put(this.name, "Could not fetch child Documents");
        }
    }

    private static String getFieldString(Object o) {
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
