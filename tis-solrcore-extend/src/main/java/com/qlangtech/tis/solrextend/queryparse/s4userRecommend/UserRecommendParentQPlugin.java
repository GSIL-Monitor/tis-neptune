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
package com.qlangtech.tis.solrextend.queryparse.s4userRecommend;

import org.apache.solr.common.params.ModifiableSolrParams;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.search.QParser;
import org.apache.solr.search.join.BlockJoinParentQParserPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/* *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class UserRecommendParentQPlugin extends BlockJoinParentQParserPlugin {

    private static final Logger logger = LoggerFactory.getLogger(UserRecommendParentQPlugin.class);

    public UserRecommendParentQPlugin() {
        super();
    }

    @Override
    public QParser createParser(String qstr, SolrParams localParams, SolrParams params, SolrQueryRequest req) {
        String sfield = params.get("sfield", "coordinate");
        String pt = params.get("pt", "0.0,0.0");
        String d = params.get("d", "100");
        String q = "{!geofilt sfield=" + sfield + " pt=" + pt + " d=" + d + "} AND type:c";
        if (localParams instanceof ModifiableSolrParams) {
            ((ModifiableSolrParams) localParams).set("which", "type:p");
            ((ModifiableSolrParams) localParams).set("v", q);
        }
        QParser parser = createBJQParser(q, localParams, params, req);
        return parser;
    }

    @Override
    protected QParser createBJQParser(String qstr, SolrParams localParams, SolrParams params, SolrQueryRequest req) {
        return super.createBJQParser(qstr, localParams, params, req);
    }
}
