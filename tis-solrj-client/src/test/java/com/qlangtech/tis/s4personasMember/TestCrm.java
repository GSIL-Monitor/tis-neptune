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
package com.qlangtech.tis.s4personasMember;

import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.io.Tuple;
import org.apache.solr.client.solrj.io.stream.TupleStream;
import com.qlangtech.tis.solrj.extend.InOptimizeClientAgent;
import com.qlangtech.tis.solrj.extend.InOptimizeClientAgent.SortField;
import junit.framework.TestCase;

/* *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class TestCrm extends TestCase {

    // private InOptimizeClientAgent agent;
    final String memberCollection = "search4personasMember";

    String entityId = "00074346";

    String[] sortFields = new String[] { "card_pay", "recency", "frequency", "monetary" };

    int fIdx = 0;

    String baseQuery = "*:*";

    private int pageSize = 20;

    String zkHost = "zk1.2dfire-daily.com:2181,zk2.2dfire-daily.com:2181,zk3.2dfire-daily.com:2181/tis/cloud";

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    // public void xxxStreamExport() throws Exception {
    // 
    // final StreamFactory streamFactory =	InOptimizeClientAgent.streamFactory;
    // 
    // String query = ""
    // +"top(n="+pageSize+",\n"
    // +"innerJoin(\n"
    // +"searchExtend("+memberCollection+", qt=/export, _route_="+ entityId + ", q=_query_:\"{!topNField rowCount=50 sort=" + sortFields[fIdx] + ",asc,float}entity_id:" + entityId + "\", fl=\"id,customer_register_id,"+sortFields[fIdx]+",docid:[docid f=docid]\", sort=customer_register_id asc)\n"
    // + ",unique(searchExtend(search4personasBase, qt=/export, _route_=0, q=" + baseQuery+ ", fl=customer_register_id, sort=\"customer_register_id asc\"), over=customer_register_id)\n"
    // + ",on=customer_register_id=customer_register_id)\n"
    // + ",sort=" + sortFields[fIdx] + " asc\n"
    // + ")";
    // System.out.println(query);
    // System.out.println("=========================================================");
    // 
    // //		String expr =""
    // //		+"top(n=20,\n"
    // //        + "innerJoin(\n"
    // //		+ "searchExtend("+memberCollection+", qt=/export, _route_="+ entityId + ", q=_query_:\"{!topNField rowCount=50 sort=" + sortFields[fIdx] + ",asc,long}entity_id:" + entityId + "\", fl=\"customer_register_id,docid:[docid f=docid]\", sort=customer_register_id asc),\n"
    // //		+ "unique(searchExtend(search4personasBase, qt=/export, _route_=0, q=" + baseQuery+ ", fl=customer_register_id, sort=\"customer_register_id asc\"), over=customer_register_id),\n"
    // //		+ "on=customer_register_id=customer_register_id),\n"
    // //		+ "sort=" + sortFields[fIdx] + " asc\n"
    // //		+ ")";
    // 
    // //idAndDocIds.clear();
    // TupleStream joinStream = streamFactory.constructStream(query);
    // try {
    // joinStream.open();
    // long start = System.currentTimeMillis();
    // while (true) {
    // Tuple tuple = joinStream.read();
    // if (tuple.EOF) {
    // break;
    // } else {
    // 
    // String sortFieldVal = tuple.getString(sortFields[fIdx]);
    // String docid = tuple.getString("docid");
    // 
    // System.out.println( tuple.getString("id")+ ",crId:"+ tuple.getString("customer_register_id") );
    // }
    // }
    // 
    // 
    // System.out.println((System.currentTimeMillis() - start)+"ms");
    // 
    // } finally {
    // try {
    // joinStream.close();}catch(Throwable e) {}
    // }
    // 
    // }
    /**
     * //
     */
    // public void testStreamJoinPaging() throws Exception {
    // 
    // //		String[] sortFields = new String[] { "card_pay", "recency", "frequency", "monetary" };
    // //		int fIdx = 0;
    // 
    // 
    // 
    // //	String baseQuery = "*:*";
    // 
    // // ,url,third_open_id,authorizer_app_id,member_level,is_receive_card,frequency,monetary,card_pay
    // 
    // 
    // 
    // InOptimizeClientAgent	agent = new InOptimizeClientAgent(zkHost);
    // 
    // long allMatchedCount =	count(agent);
    // System.out.println("allMatchedCount:"+allMatchedCount);
    // 
    // //		System.out.println("======================================================================");
    // //		System.out.println(expr);
    // //		System.out.println("======================================================================");
    // 
    // 
    // agent.setSharedKey(entityId);// 索引分库键值
    // agent.setCollection(memberCollection);// 主索引名称
    // agent.setFields(new String[] {"id", "customer_register_id", "nick_name", "mobile", "url", "third_open_id",
    // "authorizer_app_id", "member_level", "is_receive_card", "frequency", "monetary", "card_pay" });// 需要返回的fields
    // agent.setPrimaryField("id");// 反查时的主键
    // agent.setRows(pageSize);// 最后返回结果的条数
    // int lastDocId =  -1;
    // int docid ;
    // String lastSortFieldVal =  StringUtils.EMPTY;
    // String lastNick = null;
    // 
    // while(true) {
    // 
    // System.out.println("lastDocId:"+lastDocId+",lastNick:"+lastNick+",lastsort:"+ lastSortFieldVal);
    // InOptimizeClientAgent.StreamResultList<Member> memberList = queryPage(lastDocId, lastSortFieldVal,agent );
    // docid =   Integer.parseInt(memberList.getField("docid"));
    // if(docid == lastDocId ) {
    // // 已经到最后一页啦，没啦
    // return ;
    // }
    // 
    // lastDocId = Integer.parseInt(memberList.getField("docid"));
    // lastSortFieldVal =	memberList.getField(sortFields[fIdx]);
    // lastNick = memberList.getField("nick_name");
    // 
    // for (Member m : memberList) {
    // System.out.println("nickname:" + m.getNickName()+",sortf:"+ m.getCardPay()+",docid:"+m.getDocId());
    // }
    // 
    // }
    // // 如果需要使用翻页的docId和value的话，用下面这种方式获取就可以了
    // // String docId = list.getField("docId");
    // // String createTime = list.getField("create_time");
    // 
    // }
    /**
     * 统计结果，这个统计比较费时
     * @param agent
     * @return
     * @throws Exception
     */
    private long count(InOptimizeClientAgent agent) throws Exception {
        String expr = "count(\n" + "innerJoin(\n" + "search(" + memberCollection + ", qt=/export, _route_=" + entityId + ", q=_query_:\"entity_id:" + entityId + "\", fl=\"id,customer_register_id\", sort=customer_register_id asc)\n" + ",unique(searchExtend(search4personasBase, qt=/export, _route_=0, q=" + baseQuery + ", fl=customer_register_id, sort=\"customer_register_id asc\"), over=customer_register_id)\n" + ",on=customer_register_id=customer_register_id)\n" + ")";
        long resultCount = 0;
        TupleStream joinStream = InOptimizeClientAgent.streamFactory.constructStream(expr);
        try {
            joinStream.open();
            long start = System.currentTimeMillis();
            while (true) {
                Tuple tuple = joinStream.read();
                if (tuple.EOF) {
                    break;
                } else {
                    // String sortFieldVal = tuple.getString(sortFields[fIdx]);
                    // String docid = tuple.getString("docid");
                    // System.out.println(tuple);
                    resultCount = tuple.getLong("count");
                // System.out.println( tuple.getString("id")+ ",crId:"+ tuple.getString("customer_register_id") );
                }
            }
            System.out.println((System.currentTimeMillis() - start) + "ms");
        } finally {
            try {
                joinStream.close();
            } catch (Throwable e) {
            }
        }
        return resultCount;
    }

    // private InOptimizeClientAgent.StreamResultList<Member> queryPage( int last, String lastSortFieldVal,InOptimizeClientAgent	agent) throws Exception {
    // 
    // String after= StringUtils.EMPTY;
    // 
    // if(last>-1) {
    // after=" afterId="+last+" afterValue="+lastSortFieldVal+" ";
    // }
    // 
    // String expr = ""
    // +"top(n="+pageSize+",\n"
    // +"innerJoin(\n"
    // +"searchExtend("+memberCollection+", qt=/export, _route_="+ entityId + ", q=_query_:\"{!topNField rowCount=50 "+after+" sort=" + sortFields[fIdx] + ",asc,float}entity_id:" + entityId + "\", fl=\"id,customer_register_id,"+sortFields[fIdx]+",docid:[docid f=docid]\", sort=customer_register_id asc)\n"
    // + ",unique(searchExtend(search4personasBase, qt=/export, _route_=0, q=" + baseQuery+ ", fl=customer_register_id, sort=\"customer_register_id asc\"), over=customer_register_id)\n"
    // + ",on=customer_register_id=customer_register_id)\n"
    // + ",sort=\"" + sortFields[fIdx] + " asc,docid asc\"\n"
    // + ")";
    // agent.setQuery(expr);
    // System.out.println("======================================================================");
    // System.out.println(expr);
    // System.out.println("======================================================================");
    // 
    // SortField[] sort = new SortField[] { new SortField(sortFields[fIdx],SolrQuery.ORDER.asc) };
    // 
    // InOptimizeClientAgent.StreamResultList<Member> memberList = agent.query(Member.class,sort);
    // return memberList;
    // }
    public static void main(String[] args) {
    }
}
