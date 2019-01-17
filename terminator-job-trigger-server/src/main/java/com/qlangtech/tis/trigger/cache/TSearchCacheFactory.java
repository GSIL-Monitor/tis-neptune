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
package com.qlangtech.tis.trigger.cache;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import com.qlangtech.tis.trigger.cache.impl.TSearchCache;

/* *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class TSearchCacheFactory implements FactoryBean<ITSearchCache>, InitializingBean {

    private ITSearchCache cache;

    private String configID;

    private boolean dynamicConfig;

    @Override
    public void afterPropertiesSet() throws Exception {
        // <bean id="mdbTairManager"
        // class="com.taobao.tair.impl.mc.MultiClusterTairManager"
        // init-method="init">
        // <property name="configID" value="market1-daily"></property>
        // <property name="dynamicConfig" value="true"></property>
        // </bean>
        // MultiClusterTairManager tairManager = new MultiClusterTairManager();
        // 
        // tairManager.setConfigID(this.getConfigID());
        // tairManager.setDynamicConfig(this.isDynamicConfig());
        // tairManager.init();
        TSearchCache tcache = new TSearchCache();
        // tcache.setMdbTairManager(tairManager);
        this.cache = tcache;
    }

    // private static class MockCache implements ITSearchCache {
    // private ConcurrentHashMap<Serializable, Serializable> repository = new
    // ConcurrentHashMap<Serializable, Serializable>();
    // 
    // @Override
    // @SuppressWarnings("all")
    // public <T> T getObj(Serializable key) {
    // return (T) repository.get(key);
    // }
    // 
    // @Override
    // public boolean put(Serializable key, Serializable obj) {
    // repository.put(key, obj);
    // return true;
    // }
    // 
    // @Override
    // public boolean put(Serializable key, Serializable obj, int expir) {
    // repository.put(key, obj);
    // return true;
    // }
    // 
    // @Override
    // public boolean invalid(Serializable key) {
    // repository.remove(key);
    // return true;
    // }
    // 
    // }
    public String getConfigID() {
        return configID;
    }

    public void setConfigID(String configID) {
        this.configID = configID;
    }

    public boolean isDynamicConfig() {
        return dynamicConfig;
    }

    public void setDynamicConfig(boolean dynamicConfig) {
        this.dynamicConfig = dynamicConfig;
    }

    @Override
    public ITSearchCache getObject() throws Exception {
        return cache;
    }

    @Override
    public Class<ITSearchCache> getObjectType() {
        return ITSearchCache.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
