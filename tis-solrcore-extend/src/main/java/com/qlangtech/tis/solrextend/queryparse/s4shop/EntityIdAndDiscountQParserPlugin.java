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

import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.util.BytesRef;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.common.SolrException;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.search.LuceneQParser;
import org.apache.solr.search.LuceneQParserPlugin;
import org.apache.solr.search.QParser;
import org.apache.solr.search.SyntaxError;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/* *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class EntityIdAndDiscountQParserPlugin extends LuceneQParserPlugin {

    @Override
    @SuppressWarnings("all")
    public QParser createParser(String qstr, SolrParams localParams, SolrParams params, SolrQueryRequest req) {
        try {
            QParser parser = super.createParser(qstr, localParams, params, req);
            Query sortedQuery = parser.parse();
            Map<String, Long> entityId2MinDicount = new HashMap<>();
            EntityIdMinDiscountCollector collector = getCollector(entityId2MinDicount);
            req.getSearcher().search(sortedQuery, collector);
            SolrQuery query = new SolrQuery();
            StringBuilder stringBuilder = new StringBuilder("(");
            for (String entityId : entityId2MinDicount.keySet()) {
                if (stringBuilder.length() != 1) {
                    stringBuilder.append(" OR (");
                }
                stringBuilder.append("entity_id:" + entityId + " AND discount:" + entityId2MinDicount.get(entityId) + ")");
            }
            // stringBuilder.append(" AND "+qstr);
            StringBuilder stringBuilderNew = new StringBuilder("(" + stringBuilder.toString() + ") AND " + qstr);
            if (entityId2MinDicount.size() == 0) {
                stringBuilderNew = new StringBuilder("");
            }
            LuceneQParser termQParser = new LuceneQParser(stringBuilderNew.toString(), localParams, params, req);
            Query termQuery = termQParser.parse();
            return new QParser(qstr, localParams, params, req) {

                @Override
                public Query parse() throws SyntaxError {
                    return termQuery;
                }
            };
        } catch (Exception e) {
            throw new SolrException(SolrException.ErrorCode.SERVER_ERROR, e);
        }
    }

    public EntityIdMinDiscountCollector getCollector(Map<String, Long> entityId2MinDicount) {
        return new EntityIdMinDiscountCollector(entityId2MinDicount);
    }
}

class EntityIdMinDiscountCollector extends SimpleCollector {

    private Map<String, Long> entityId2MinDicount;

    private SortedDocValues entityIdDV;

    private NumericDocValues discountDV;

    public EntityIdMinDiscountCollector(Map<String, Long> entityId2MinDicount) {
        this.entityId2MinDicount = entityId2MinDicount;
    }

    @Override
    protected void doSetNextReader(LeafReaderContext context) throws IOException {
        super.doSetNextReader(context);
        this.entityIdDV = DocValues.getSorted(context.reader(), "entity_id");
        this.discountDV = DocValues.getNumeric(context.reader(), "discount");
    }

    @Override
    public void collect(int doc) throws IOException {
        BytesRef eidByte = this.entityIdDV.get(doc);
        long discount = this.discountDV.get(doc);
        if (eidByte == null) {
            return;
        }
        String entityId = eidByte.utf8ToString();
        if (discount < entityId2MinDicount.getOrDefault(entityId, 100l)) {
            entityId2MinDicount.put(entityId, discount);
        }
    }

    @Override
    public boolean needsScores() {
        return false;
    }
}
