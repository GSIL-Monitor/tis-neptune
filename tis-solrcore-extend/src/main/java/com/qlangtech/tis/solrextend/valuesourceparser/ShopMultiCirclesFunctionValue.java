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

import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.index.NumericDocValues;
import org.apache.lucene.queries.function.FunctionValues;
import org.apache.lucene.queries.function.ValueSource;
import org.apache.lucene.queries.function.ValueSourceScorer;
import org.apache.lucene.queries.function.docvalues.DoubleDocValues;
import org.apache.lucene.search.Explanation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/* *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class ShopMultiCirclesFunctionValue extends DoubleDocValues {

    static final Logger log = LoggerFactory.getLogger(ShopMultiCirclesFunctionValue.class);

    static final int FIRST_CIRCLE_SCORE = 800;

    static final int SECOND_CIRCLE_SCORE = 400;

    static final int THIRD_CIRCLE_SCORE = 200;

    static final int OUTSIDE_CIRCLE_SCORE = 1;

    private FunctionValues functionValues;

    private NumericDocValues weightDocValues;

    private ShopMultiCirclesGeoDIstValueSourceParser.Params params;

    public ShopMultiCirclesFunctionValue(ValueSource vs, FunctionValues functionValues, NumericDocValues weightDocValues, ShopMultiCirclesGeoDIstValueSourceParser.Params params) {
        super(vs);
        this.functionValues = functionValues;
        this.weightDocValues = weightDocValues;
        this.params = params;
    }

    /**
     * 利用得到的距离，做三个环的距离评分，再用得到的距离分加上权重，作为最后的得分
     */
    @Override
    public double doubleVal(int doc) {
        Double distance = functionValues.doubleVal(doc);
        double weight = Double.longBitsToDouble((long) weightDocValues.get(doc));
        double x = (distance >= 0 && distance <= params.firstCircle) ? FIRST_CIRCLE_SCORE : (distance > params.firstCircle && distance <= params.secondCircle) ? SECOND_CIRCLE_SCORE : (distance > params.secondCircle && distance <= params.thirdCircle) ? THIRD_CIRCLE_SCORE : OUTSIDE_CIRCLE_SCORE / distance;
        double finalScore = x + weight;
        // + " final score " + finalScore);
        return finalScore;
    }

    @Override
    public String toString(int doc) {
        return functionValues.toString(doc);
    }

    @Override
    public ValueFiller getValueFiller() {
        return functionValues.getValueFiller();
    }

    @Override
    public int ordVal(int doc) {
        return functionValues.ordVal(doc);
    }

    @Override
    public Explanation explain(int doc) {
        return functionValues.explain(doc);
    }

    @Override
    public ValueSourceScorer getScorer(LeafReaderContext readerContext) {
        return functionValues.getScorer(readerContext);
    }

    @Override
    public ValueSourceScorer getRangeScorer(LeafReaderContext readerContext, String lowerVal, String upperVal, boolean includeLower, boolean includeUpper) {
        return functionValues.getRangeScorer(readerContext, lowerVal, upperVal, includeLower, includeUpper);
    }

    @Override
    public boolean exists(int doc) {
        return functionValues.exists(doc);
    }
}
