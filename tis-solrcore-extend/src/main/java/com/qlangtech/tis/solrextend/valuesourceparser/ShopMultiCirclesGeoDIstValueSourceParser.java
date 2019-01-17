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

import org.apache.lucene.queries.function.ValueSource;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.search.FunctionQParser;
import org.apache.solr.search.SyntaxError;
import org.apache.solr.search.function.distance.GeoDistValueSourceParser;
import org.apache.solr.search.function.distance.HaversineConstFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/* *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class ShopMultiCirclesGeoDIstValueSourceParser extends GeoDistValueSourceParser {

    static final Logger log = LoggerFactory.getLogger(ShopMultiCirclesGeoDIstValueSourceParser.class);

    private Params params;

    public void init(NamedList args) {
        int firstCircle = (Integer) ((NamedList) args.get("params")).get("firstCircle");
        int secondCircle = (Integer) ((NamedList) args.get("params")).get("secondCircle");
        int thirdCircle = (Integer) ((NamedList) args.get("params")).get("thirdCircle");
        String weightField = (String) ((NamedList) args.get("params")).get("weightField");
        params = new Params(firstCircle, secondCircle, thirdCircle, weightField);
    // log.info("init params：  firstCircle : " + firstCircle
    // +" secondCircle : " + secondCircle
    // +" thirdCircle : " + thirdCircle
    // +" weightField : " + weightField);
    }

    @Override
    public ValueSource parse(FunctionQParser fp) throws SyntaxError {
        /**
         * 得到ValueSource为计算得到的两个坐标点之间的距离
         */
        ValueSource valueSource = super.parse(fp);
        if (!(valueSource instanceof HaversineConstFunction)) {
            throw new IllegalStateException("instance type must be HaversineConstFunction,but now is " + valueSource.getClass());
        }
        return new ShopMultiCirclesValueSource((HaversineConstFunction) valueSource, params);
    }

    protected class Params {

        protected int firstCircle;

        protected int secondCircle;

        protected int thirdCircle;

        protected String weightField;

        public Params(int firstCircle, int secondCircle, int thirdCircle, String weightField) {
            this.firstCircle = firstCircle;
            this.secondCircle = secondCircle;
            this.thirdCircle = thirdCircle;
            this.weightField = weightField;
        }
    }
}
