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
package com.qlangtech.tis.hdfs.client.bean.searcher;

import org.apache.solr.client.solrj.response.QueryResponse;
import com.qlangtech.tis.common.ServiceType;
import com.qlangtech.tis.common.TerminatorServiceException;
import com.qlangtech.tis.common.config.IServiceConfig;
import com.qlangtech.tis.common.protocol.SearchService;
import com.qlangtech.tis.common.protocol.TerminatorQueryRequest;

/*
 * 普通模式查询客户端
 *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class CommonTerminatorSearcher extends BasicTerminatorSearcher {

    // private static final Log logger = LogFactory
    // .getLog(CommonTerminatorSearcher.class);
    protected final SearchService getSearchService(ServiceType serviceType, String serviceName, int group) {
        return new SearchService() {

            @Override
            public QueryResponse query(TerminatorQueryRequest query) throws TerminatorServiceException {
                return null;
            }
        };
    }

    protected final void subscribeSearcherService(IServiceConfig config) {
    // Set<String> versionSet = TerminatorSearchHSFContainer.Utils
    // .generateTerminatorVersions(config.getCoreNameSet());
    // for (String version : versionSet) {
    // logger.warn("(重新)订阅HSF服务" + version);
    // try {
    // TerminatorSearchHSFContainer.subscribeService(
    // SearchService.class.getName(), version);
    // } catch (TerminatorHsfSubException e) {
    // logger.error("订阅HSF服务异常" + version);
    // throw new TerminatorInitException(e);
    // }
    // }
    }
    // @Override
    // protected final void setServiceConfig(ServiceConfig serviceConfig) {
    // super.setServiceConfig(serviceConfig);
    // Set<String> versionSet = TerminatorSearchHSFContainer.Utils
    // .generateTerminatorVersions(serviceConfig.getCoreNameSet());
    // for (String version : versionSet) {
    // logger.warn("(重新)订阅HSF服务" + version);
    // try {
    // TerminatorSearchHSFContainer.subscribeService(
    // SearchService.class.getName(), version);
    // } catch (TerminatorHsfSubException e) {
    // logger.error("订阅HSF服务异常" + version);
    // throw new TerminatorInitException(e);
    // }
    // }
    // }
}
