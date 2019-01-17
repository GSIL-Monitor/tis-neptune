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
package com.qlangtech.tis.solrextend.queryparse.s4presellStock;

import java.io.IOException;
import java.util.Map;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.queries.function.FunctionValues;
import org.apache.lucene.queries.function.ValueSource;
import org.apache.lucene.queries.function.docvalues.DoubleDocValues;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.search.FunctionQParser;
import org.apache.solr.search.SyntaxError;
import org.apache.solr.search.ValueSourceParser;

/* *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class ShuffleValueSourceParser extends ValueSourceParser {

    private int mod;

    private float weight;

    @SuppressWarnings("all")
    public void init(NamedList args) {
        NamedList namedList = ((NamedList) args.get("params"));
        this.mod = (int) (namedList.get("mod"));
        this.weight = (float) namedList.get("weight");
    // log.info("init params： firstCircle : " + firstCircle
    // +" secondCircle : " + secondCircle
    // +" thirdCircle : " + thirdCircle
    // +" weightField : " + weightField);
    }

    @Override
    public ValueSource parse(FunctionQParser fp) throws SyntaxError {
        return new NumDocsValueSource();
    }

    private class NumDocsValueSource extends ValueSource {

        private FunctionValues fcVal = new DoubleDocValues(this) {

            @Override
            public double doubleVal(int doc) {
                return (doc % mod) * weight;
            }
        };

        @Override
        public FunctionValues getValues(Map context, LeafReaderContext readerContext) throws IOException {
            return fcVal;
        }

        public String name() {
            return "shuffle";
        }

        @Override
        public boolean equals(Object o) {
            return this.getClass() == o.getClass();
        }

        @Override
        public int hashCode() {
            return this.getClass().hashCode();
        }

        @Override
        public String description() {
            return name() + "()";
        }
    }
}
