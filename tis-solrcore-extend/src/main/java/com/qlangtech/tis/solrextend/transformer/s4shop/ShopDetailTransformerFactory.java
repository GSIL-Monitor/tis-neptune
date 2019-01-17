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
package com.qlangtech.tis.solrextend.transformer.s4shop;

import java.io.IOException;
import java.util.Map;
import org.apache.lucene.index.IndexableField;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.response.transform.DocTransformer;
import org.apache.solr.response.transform.TransformerFactory;
import com.qlangtech.tis.solrextend.queryparse.s4shop.ShopDetailFactory;

/* *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class ShopDetailTransformerFactory extends TransformerFactory {

    @Override
    public DocTransformer create(String field, SolrParams solrParams, SolrQueryRequest solrQueryRequest) {
        return new ShopDetailTransformer(field);
    }
}

class ShopDetailTransformer extends DocTransformer {

    // private static final String Shop_Detail = "shop_detail";
    private String name;

    public ShopDetailTransformer(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String[] getExtraRequestFields() {
        return ShopDetailFactory.ShopDetail.ReturnFields.toArray(new String[ShopDetailFactory.ShopDetail.ReturnFields.size()]);
    }

    @Override
    public void transform(SolrDocument doc, int docId, float value) throws IOException {
        for (String fieldName : ShopDetailFactory.ShopDetail.ReturnFields) {
            doc.remove(fieldName);
        }
        IndexableField entityId = (IndexableField) doc.getFieldValue("entity_id");
        if (entityId == null) {
            return;
        }
        // }
        try {
            Map<String, String> fieldValue = ShopDetailFactory.getShopDetail(entityId.stringValue()).getFieldValue();
            for (String fieldName : ShopDetailFactory.ShopDetail.ReturnFields) {
                doc.addField(fieldName, fieldValue.get(fieldName));
            }
        } catch (Exception e) {
            throw new IOException(e);
        }
    }
}
