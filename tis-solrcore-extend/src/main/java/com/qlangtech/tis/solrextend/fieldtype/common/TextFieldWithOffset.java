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
package com.qlangtech.tis.solrextend.fieldtype.common;

import org.apache.lucene.index.IndexOptions;
import org.apache.solr.schema.SchemaField;
import org.apache.solr.schema.TextField;

/* *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class TextFieldWithOffset extends TextField {

    @Override
    protected IndexOptions getIndexOptions(SchemaField field, String internalVal) {
        return IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS;
    }
    // @Override
    // public Query getFieldQuery(QParser parser, SchemaField field, String externalVal) {
    // 
    // String[] p = StringUtils.split(externalVal, "^");
    // String qstr = p[0];
    // int startPos = 0;
    // if (p.length > 1) {
    // startPos = Integer.parseInt(p[1]);
    // }
    // SpanTermQuery tq = new SpanTermQuery(new Term(field.getName(), qstr));
    // final MobileSpanPositionCheckQuery fquery = new MobileSpanPositionCheckQuery(tq, startPos,
    // StringUtils.length(qstr));
    // return fquery;
    // }
    // protected IndexableField createField(String name, String val,
    // org.apache.lucene.document.FieldType type, float boost) {
    // Field f = (Field) super.createField(name, val, type, boost);
    // f.setTokenStream(new DefaultTokenStream());
    // return f;
    // }
    // 
    // private static class DefaultTokenStream extends TokenStream {
    // private final AllWithPositionNGramTokenFilter fullTermFilter;
    // // return new NGramTokenFilter(input, minGramSize, maxGramSize);
    // private final NGramTokenFilter ngramTokenFilter;
    // private boolean fullTermProcess = false;
    // 
    // /**
    // *
    // */
    // public DefaultTokenStream() {
    // super();
    // this.fullTermFilter = new AllWithPositionNGramTokenFilter(new
    // EmptyTokenStream());
    // this.ngramTokenFilter = new NGramTokenFilter(new EmptyTokenStream(), 2,
    // 5);
    // }
    // 
    // @Override
    // public final boolean incrementToken() throws IOException {
    // if (!fullTermProcess && fullTermFilter.incrementToken()) {
    // this.reset();
    // fullTermProcess = true;
    // return true;
    // }
    // if (this.ngramTokenFilter.incrementToken()) {
    // return true;
    // }
    // return false;
    // }
    // 
    // }
    // 
    // public static final class EmptyTokenStream extends TokenStream {
    // 
    // /**
    // * @param input
    // */
    // public EmptyTokenStream(AttributeSource input) {
    // super(input);
    // 
    // }
    // 
    // private boolean process=true;
    // 
    // @Override
    // public final boolean incrementToken() {
    // if (process) {
    // process = false;
    // return true;
    // } else {
    // return false;
    // }
    // }
    // 
    // }
}
