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

import org.apache.lucene.index.DocValues;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.index.NumericDocValues;
import org.apache.lucene.index.SortedDocValues;
import org.apache.lucene.util.BytesRef;
import java.io.IOException;
import java.util.Map;

/* *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class EntityIdRemainDiscountQParserPlugin extends EntityIdAndDiscountQParserPlugin {

    @Override
    public EntityIdMinDiscountCollector getCollector(Map<String, Long> entityId2MinDicount) {
        return new EntityIdRemainMinDiscountCollector(entityId2MinDicount);
    }
}

class EntityIdRemainMinDiscountCollector extends EntityIdMinDiscountCollector {

    private Map<String, Long> entityId2MinDicount;

    private SortedDocValues entityIdDV;

    private NumericDocValues discountDV;

    private NumericDocValues remainVolumeDV;

    public EntityIdRemainMinDiscountCollector(Map<String, Long> entityId2MinDicount) {
        super(entityId2MinDicount);
        this.entityId2MinDicount = entityId2MinDicount;
    }

    @Override
    protected void doSetNextReader(LeafReaderContext context) throws IOException {
        super.doSetNextReader(context);
        this.entityIdDV = DocValues.getSorted(context.reader(), "entity_id");
        this.discountDV = DocValues.getNumeric(context.reader(), "discount");
        this.remainVolumeDV = DocValues.getNumeric(context.reader(), "remain_volume");
    }

    @Override
    public void collect(int doc) throws IOException {
        BytesRef eidByte = this.entityIdDV.get(doc);
        long discount = this.discountDV.get(doc);
        long remainVolume = this.remainVolumeDV.get(doc);
        if (eidByte == null) {
            return;
        }
        String entityId = eidByte.utf8ToString();
        if (remainVolume > 0 && discount < entityId2MinDicount.getOrDefault(entityId, 100l)) {
            entityId2MinDicount.put(entityId, discount);
        } else if (remainVolume == 0 && entityId2MinDicount.get(entityId) == null) {
            entityId2MinDicount.put(entityId, discount);
        }
    }
}
