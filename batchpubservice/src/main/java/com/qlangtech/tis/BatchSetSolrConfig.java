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
package com.qlangtech.tis;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.sql.DataSource;
import com.qlangtech.tis.manage.client.CoreManagerClient;
import com.qlangtech.tis.pubhook.common.RunEnvironment;

/* *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class BatchSetSolrConfig {

    private DataSource datasource;

    private static final RunEnvironment runtime;

    static {
        runtime = RunEnvironment.getEnum(System.getProperty("runtime"));
    }

    private static final String SQL_SELECT_publish_snapshot_id_FROM_SERVER_GROUP = "select publish_snapshot_id ,runt_environment,group_index from server_group g inner join application a on (a.app_id = g.app_id)" + " where project_name = ? and runt_environment=" + runtime.getId() + " and group_index = 0";

    private static final String SQL_SELECT_SET_SOLR_RESOURCE = "update snapshot set res_solr_id = ? where sn_id = ?";

    public void setSolrConfig(String[] indexNames, Integer uploadResourceId) throws Exception {
        if (uploadResourceId < 1) {
            throw new IllegalArgumentException("uploadResourceId can not small than 1");
        }
        final String centerNodeAddress = System.getProperty("centerNode");
        CoreManagerClient coreManagerclient = PubService.createCoreManagerClient(centerNodeAddress);
        Connection conn = null;
        try {
            conn = datasource.getConnection();
            PreparedStatement statement = null;
            ResultSet result = null;
            for (String name : indexNames) {
                Integer publishedSnapshotId = null;
                statement = conn.prepareStatement(SQL_SELECT_publish_snapshot_id_FROM_SERVER_GROUP);
                statement.setString(1, name);
                result = statement.executeQuery();
                if (result.next()) {
                    publishedSnapshotId = result.getInt(1);
                }
                result.close();
                statement.close();
                if (publishedSnapshotId < 1) {
                    throw new IllegalStateException("app:" + name + " has not set publish snapshot");
                }
                statement = conn.prepareStatement(SQL_SELECT_SET_SOLR_RESOURCE);
                statement.setInt(1, uploadResourceId);
                statement.setInt(2, publishedSnapshotId);
                statement.executeUpdate();
                statement.close();
                // 
                // coreManagerclient.coreConfigChange(name);
                System.out.println(" has successful set index:" + name + ",snapshotid:" + publishedSnapshotId + ",resid:" + uploadResourceId);
                System.in.read();
            }
        } finally {
            conn.close();
        }
    }

    private static DataSource createDataSource() {
        // datasource.init();
        return null;
    }

    public static void main(String[] args) throws Exception {
        Integer resid = Integer.parseInt(System.getProperty("resid"));
        final BatchSetSolrConfig batchSetSolrConfig = new BatchSetSolrConfig();
        batchSetSolrConfig.setDatasource(createDataSource());
        System.out.println("has create datasource");
        String[] coreNames = { "search4realwjbSellerTraderate", "search4realwjbSmsLog", "search4realwjbFieldTextData", "search4realwjbFieldIntData", "search4realwjbFieldDateData", "search4realwjbEmailSent", "search4realwjbactivityInfo", "search4wangjubaoorder", "search4wangjubaotrade" };
        batchSetSolrConfig.setSolrConfig(coreNames, resid);
    }

    public DataSource getDatasource() {
        return datasource;
    }

    public void setDatasource(DataSource datasource) {
        this.datasource = datasource;
    }
}
