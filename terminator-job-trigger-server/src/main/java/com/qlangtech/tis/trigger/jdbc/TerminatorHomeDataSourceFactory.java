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
package com.qlangtech.tis.trigger.jdbc;

import java.sql.Connection;
import java.sql.ResultSet;
import javax.sql.DataSource;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import com.qlangtech.tis.manage.common.Config;

/* *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class TerminatorHomeDataSourceFactory implements FactoryBean<DataSource>, InitializingBean {

    private static final long serialVersionUID = 1L;

    // private com.taobao.tddl.jdbc.group.TGroupDataSource dataSource;
    @Override
    public void afterPropertiesSet() throws Exception {
    // <bean id="endSearchDatasource"
    // class="com.taobao.tddl.jdbc.group.TGroupDataSource"
    // init-method="init">
    // <property name="appName" value="DAILY_T_HOME_NEW" />
    // <property name="dbGroupKey" value="TERMINATORHOMENEW_GROUP" />
    // </bean>
    // dataSource = new TGroupDataSource();
    // dataSource.setAppName(Config.getTermiantorDsConfig().getAppName());
    // dataSource.setDbGroupKey(Config.getTermiantorDsConfig().getGroupName());
    // dataSource.init();
    }

    @Override
    public DataSource getObject() throws Exception {
        // return this.dataSource;
        return null;
    }

    @Override
    public Class<DataSource> getObjectType() {
        return DataSource.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    public static void main(String[] arg) throws Exception {
    // System.out.println("appname:"
    // + Config.getTermiantorDsConfig().getAppName());
    // System.out.println("group:"
    // + Config.getTermiantorDsConfig().getGroupName());
    // TerminatorHomeDataSourceFactory factory = new TerminatorHomeDataSourceFactory();
    // factory.afterPropertiesSet();
    // 
    // Connection conn = factory.dataSource.getConnection();
    // 
    // 
    // 
    // ResultSet result = conn.createStatement().executeQuery("select 1");
    // 
    // if (result.next()) {
    // System.out.println(result.getInt(1));
    // }
    }
}
