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
package com.qlangtech.tis.solrextend.cloud;

import org.apache.solr.cloud.ZkController;
import org.apache.solr.cloud.ZkSolrResourceLoader;
import org.apache.solr.common.SolrException;
import org.apache.solr.common.SolrException.ErrorCode;
import org.apache.solr.core.ConfigSetService;
import org.apache.solr.core.CoreDescriptor;
import org.apache.solr.core.SolrConfig;
import org.apache.solr.core.SolrResourceLoader;
import com.qlangtech.tis.solrextend.dir.TisSolrConfig;

/* *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class TisConfigSetService extends ConfigSetService {

    private final ZkController zkController;

    public TisConfigSetService(SolrResourceLoader loader, ZkController zkController) {
        super(loader);
        this.zkController = zkController;
    }

    @Override
    public String configName(CoreDescriptor desc) {
        return "collection " + desc.getCloudDescriptor().getCollectionName();
    }

    @Override
    protected SolrResourceLoader createCoreResourceLoader(CoreDescriptor desc) {
        // String instanceDir, String configSet,
        // ClassLoader parent, Properties coreProperties,
        // ZkController zooKeeperController,String collection
        // 
        // zkController.createCollectionZkNode(cd.getCloudDescriptor());
        // String configName = zkController.getZkStateReader().readConfigName(cd.getCollectionName());
        // return new ZkSolrResourceLoader(cd.getInstanceDir(), configName, parentLoader.getClassLoader(),
        // cd.getSubstitutableProperties(), zkController);
        String configName = zkController.getZkStateReader().readConfigName(desc.getCollectionName());
        zkController.createCollectionZkNode(desc.getCloudDescriptor());
        String collectionName = desc.getCloudDescriptor().getCollectionName();
        return new TisSolrResourceLoader(desc.getInstanceDir(), configName, parentLoader.getClassLoader(), desc.getSubstitutableProperties(), zkController, collectionName);
    }

    protected SolrConfig createSolrConfig(CoreDescriptor cd, SolrResourceLoader loader) {
        String name = cd.getConfigName();
        try {
            return new TisSolrConfig(loader, cd, null);
        } catch (Exception e) {
            String resource;
            if (loader instanceof ZkSolrResourceLoader) {
                resource = name;
            } else {
                resource = loader.getConfigDir() + name;
            }
            throw new SolrException(ErrorCode.SERVER_ERROR, "Error loading solr config from " + resource, e);
        }
    // return SolrConfig.readFromResourceLoader(loader, cd.getConfigName());
    }
}
