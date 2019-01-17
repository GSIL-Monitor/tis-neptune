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
package com.qlangtech.tis.solrextend.valuesourceparser;

import org.apache.lucene.index.DocValues;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.index.NumericDocValues;
import org.apache.lucene.queries.function.FunctionValues;
import org.apache.lucene.queries.function.ValueSource;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.SortField;
import org.apache.solr.search.function.distance.HaversineConstFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.util.Map;

/* *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class ShopMultiCirclesValueSource extends ValueSource {

    static final Logger log = LoggerFactory.getLogger(ShopMultiCirclesValueSource.class);

    private HaversineConstFunction valuesource;

    private ShopMultiCirclesGeoDIstValueSourceParser.Params params;

    public ShopMultiCirclesValueSource(HaversineConstFunction valuesource, ShopMultiCirclesGeoDIstValueSourceParser.Params params) {
        this.valuesource = valuesource;
        this.params = params;
    }

    @Override
    public FunctionValues getValues(Map context, LeafReaderContext readerContext) throws IOException {
        FunctionValues functionValues = valuesource.getValues(context, readerContext);
        final NumericDocValues weightDcoValue = DocValues.getNumeric(readerContext.reader(), params.weightField);
        ShopMultiCirclesFunctionValue shopMultiCirclesFunctionValue = new ShopMultiCirclesFunctionValue(this, functionValues, weightDcoValue, params);
        return shopMultiCirclesFunctionValue;
    }

    @Override
    public String toString() {
        return valuesource.toString();
    }

    @Override
    public SortField getSortField(boolean reverse) {
        return valuesource.getSortField(reverse);
    }

    @Override
    public void createWeight(Map context, IndexSearcher searcher) throws IOException {
        valuesource.createWeight(context, searcher);
    }

    @Override
    public boolean equals(Object o) {
        return valuesource.equals(o);
    }

    @Override
    public int hashCode() {
        return valuesource.hashCode();
    }

    @Override
    public String description() {
        return valuesource.description();
    }
}
