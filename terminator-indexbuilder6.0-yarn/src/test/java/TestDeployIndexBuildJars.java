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
import java.util.List;
import org.apache.commons.cli.CommandLine;
import com.qlangtech.tis.build.yarn.BuildNodeMaster;
import com.qlangtech.tis.manage.common.IndexBuildParam;
import junit.framework.TestCase;

/* *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class TestDeployIndexBuildJars extends TestCase {

    public void test() {
        String[] params = new String[] { "-indexing_outputpath", "/user/admin/search4_fat_instance/all/0/output/20160622001000", "-indexing_servicename", "search4_fat_instance", "-job_solrversion", "solr6", "-indexing_sourcetype", "HDFS", "-indexing_groupnum", "0", "-indexing_delimiter", "char001", "-indexing_username", "admin", "-indexing_sourcepath", "/user/hive/warehouse/solr.db/kuan/pt=20160620/pmod=0", "-indexing_corename", "search4_fat_instance-0", "-indexing_incrtime", "20160622001000", "-indexing_sourcefsname", "hdfs://10.1.7.25:8020", "-indexing_buildtabletitleitems", "instance_id,in_order_id,in_kind_menu_id,kindmenu_name,in_menu_id,in_parent_id,account_num,in_fee,ratio_fee,ratio,entity_id,in_name,kind,unit,account_unit,price,member_price,original_price,pricemode,is_ratio,spec_detail_name,in_op_time,modify_time,load_time,in_last_ver,is_buynumber_changed,seat_id,in_is_valid,in_status,km_kindmenu_id,km_kindmenu_name,km_sort_code,group_kind_id,group_or_kind_id,group_or_kind_name,km_is_valid,od_curr_date,open_time,end_time,people_count,od_is_valid,od_status,od_is_hide,mwh_menuorkind_id,mwh_id,mwh_warehouse_id,mwh_type,mwh_ratio,mwh_create_time,mwh_op_time,wh_name,wh_sort_code,wh_parent_id,wh_is_check", // "-job_name", "search4_fat_instance-0-indexBuildJob",
        "-indexing_maxNumSegments", "1", "-indexing_schemapath", "/user/admin/search4_fat_instance-0/schema/schema.xml" };
        CommandLine commandLine = BuildNodeMaster.parseCommandLine(params);
        List<String> fields = IndexBuildParam.getAllFieldName();
        for (String f : fields) {
            System.out.println(f + ":" + commandLine.getOptionValue(f));
        }
    }
}
