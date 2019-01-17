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

import com.qlangtech.tis.solrextend.listener.UserRecommendListener;
import com.qlangtech.tis.solrextend.queryparse.BitQuery;
import com.qlangtech.tis.solrextend.queryparse.s4userRecommend.mahoutCore.*;
import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.index.*;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.FixedBitSet;
import org.apache.solr.common.SolrException;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.core.SolrCore;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.search.LuceneQParserPlugin;
import org.apache.solr.search.QParser;
import org.apache.solr.search.SolrIndexSearcher;
import org.apache.solr.search.SyntaxError;
import org.slf4j.Logger;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/* *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class UserRecommendQParserPlugin extends LuceneQParserPlugin {

    private static final Logger log = SolrCore.requestLog;

    private static String termFieldName;

    private static int targetUserId = 0;

    private AtomicInteger userIndex = null;

    private static LinkedList<Long> termOrdList;

    private static LinkedList<String> termNameList;

    public static Map<String, Integer> term2Ord = null;

    public static Map<Long, Double> id2Score = null;

    private static String score;

    private static final long[] NO_IDS = new long[0];

    @Override
    public QParser createParser(String qstr, SolrParams localParams, SolrParams params, SolrQueryRequest req) {
        try {
            log.info("starting UserRecommend QParser");
            // init args
            termFieldName = params.get("termField", "termcount");
            score = params.get("score", "Max");
            termFieldName = params.get("termField", "termcount");
            termOrdList = null;
            // mahout user index
            userIndex = new AtomicInteger(0);
            // 初始化order列表
            term2Ord = UserRecommendListener.term2Ord;
            if (term2Ord.size() == 0) {
                synchronized (termFieldName) {
                    if (term2Ord.size() == 0) {
                        warmUpTermList(req.getSearcher().getIndexReader());
                    }
                }
            }
            final FixedBitSet bitSet = new FixedBitSet(req.getSearcher().getIndexReader().maxDoc());
            /**
             * 1:得到指定的用户id的docId,从而得到过往点菜记录
             */
            QParser userIdQParser = super.createParser(qstr + " AND type:p", localParams, params, req);
            Query userIdQuery = userIdQParser.parse();
            TopDocs topDocs = req.getSearcher().search(userIdQuery, 1);
            log.info("get userIdQuery :" + qstr + " AND type:p");
            ScoreDoc[] scoreDocs = topDocs.scoreDocs;
            targetUserId = scoreDocs[0].doc;
            log.info("get targetUserId:  " + targetUserId);
            Map<Long, Integer> /**
             * order,payload
             */
            Ord2Payload = getTermsAndPayloadsByUserId(req, targetUserId);
            /**
             * 2:通过得到的过往点菜记录,拼成一条查询语句，结合用户所在的坐标点，查到附近d km 内与其最可能相似的其他用户
             * query example: (termcount:冰 OR termcount:听) AND {!userParent score=None}
             */
            StringBuilder stringBuilder = new StringBuilder("");
            for (String term : termNameList) {
                if (stringBuilder.length() == 0) {
                    stringBuilder.append(" (termcount:" + term);
                } else {
                    stringBuilder.append(" OR termcount:" + term);
                }
            }
            if (Ord2Payload.size() != 0) {
                stringBuilder.append(")");
            }
            stringBuilder.append(" AND {!userParent score=" + score + "}");
            log.info("curl a query : " + stringBuilder.toString());
            QParser termQParser = super.createParser(stringBuilder.toString(), localParams, params, req);
            Query termQuery = termQParser.parse();
            TopDocs h = req.getSearcher().search(termQuery, 200);
            ScoreDoc[] scoreDocs1 = h.scoreDocs;
            LinkedList<Integer> nearlyUser = new LinkedList<Integer>();
            LinkedList<Long> nearlyEntityId = new LinkedList<Long>();
            /**
             * 3:通过得到对疑似用户中进行相似度匹配，得到最相近的几个用户
             */
            for (ScoreDoc scoreDoc : scoreDocs1) {
                nearlyUser.add(scoreDoc.doc);
            // nearlyEntityId.add(entityID.valueAt(scoreDoc.doc));
            }
            req.getContext().put("nearlyUser", nearlyEntityId);
            getNeighborhoodByUserID(targetUserId, nearlyUser, bitSet, req);
            int hash = qstr.hashCode() + params.get("pt", "0.0,0.0").hashCode() + params.get("d", "100").hashCode();
            final UserRecommendBitQuery userRecommendBitQuery = new UserRecommendBitQuery(bitSet, hash, id2Score);
            log.info("return a bitquery : " + userRecommendBitQuery.toString());
            QParser qParser = new QParser(qstr, localParams, params, req) {

                @Override
                public Query parse() throws SyntaxError {
                    return userRecommendBitQuery;
                }
            };
            return qParser;
        } catch (Exception e) {
            return new QParser(qstr, localParams, params, req) {

                @Override
                public Query parse() throws SyntaxError {
                    return new BitQuery(new FixedBitSet(0));
                }
            };
        // throw new SolrException(SolrException.ErrorCode.SERVER_ERROR,e);
        }
    }

    private void warmUpTermList(DirectoryReader indexReader) {
        term2Ord = new ConcurrentHashMap<>();
        TermsEnum termEnum = null;
        AtomicInteger order = new AtomicInteger(0);
        try {
            termEnum = MultiFields.getTerms(indexReader, termFieldName).iterator();
            while (termEnum.next() != null) {
                BytesRef term = termEnum.term();
                term2Ord.put(term.utf8ToString(), order.getAndIncrement());
            }
        } catch (Exception e) {
            throw new SolrException(SolrException.ErrorCode.INVALID_STATE, e);
        }
    }

    private void getNeighborhoodByUserID(int finalUserId, LinkedList<Integer> nearlyUser, FixedBitSet bitSet, SolrQueryRequest req) throws Exception {
        UserRecommendFastByIDMap<UserRecommendPreferenceArray> preferences = new UserRecommendFastByIDMap<UserRecommendPreferenceArray>();
        processUser(preferences, finalUserId, req);
        for (Integer userId : nearlyUser) {
            processUser(preferences, userId, req);
        }
        UserRecommendUncenteredCosineSimilarity similarity = new UserRecommendUncenteredCosineSimilarity(preferences);
        long[] neighs = userCF(preferences, finalUserId, 20, similarity);
        for (long l : neighs) {
            bitSet.set((int) l);
        }
    }

    private long[] userCF(UserRecommendFastByIDMap<UserRecommendPreferenceArray> preferences, long userId, int num, UserRecommendUncenteredCosineSimilarity similarity) throws Exception {
        Queue<UserRecommendSimilarUser> topUsers = new PriorityQueue<UserRecommendSimilarUser>(num + 1, Collections.reverseOrder());
        id2Score = new HashMap<Long, Double>();
        double lowestTopValue = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < preferences.size(); i++) {
            UserRecommendPreferenceArray userRecommendpreferenceArray = preferences.get(i);
            long userId2 = userRecommendpreferenceArray.getUserID(0);
            if (userId2 == userId) {
                continue;
            }
            boolean full = false;
            double score;
            try {
                score = similarity.userSimilarity(0, i);
            } catch (Exception nsue) {
                continue;
            }
            if (!Double.isNaN(score) && (!full || score > lowestTopValue)) {
                topUsers.add(new UserRecommendSimilarUser(userId2, score));
                if (full) {
                    topUsers.poll();
                } else if (topUsers.size() > num) {
                    full = true;
                    topUsers.poll();
                }
                lowestTopValue = topUsers.peek().getSimilarity();
            }
        }
        int size = topUsers.size();
        if (size == 0) {
            return NO_IDS;
        }
        List<UserRecommendSimilarUser> sorted = Lists.newArrayListWithCapacity(size);
        sorted.addAll(topUsers);
        Collections.sort(sorted);
        long[] result = new long[size];
        int i = 0;
        for (UserRecommendSimilarUser similarUser : sorted) {
            id2Score.put(similarUser.getUserID(), similarUser.getSimilarity());
            log.info("userId:[" + similarUser.getUserID() + "] match a score:[" + similarUser.getSimilarity() + "]");
            result[i++] = similarUser.getUserID();
        }
        return result;
    }

    private void processUser(UserRecommendFastByIDMap<UserRecommendPreferenceArray> preferences, int userId, SolrQueryRequest req) throws IOException {
        Map<Long, Integer> /**
         * order,payload
         */
        termsMap = getTermsAndPayloadsByUserId(req, userId);
        LinkedList<Long> targetTerms = new LinkedList<Long>(termOrdList);
        UserRecommendPreferenceArray prefsForUser = new UserRecommendGenericUserPreferenceArray(targetTerms.size());
        prefsForUser.setUserID(userIndex.get(), userId);
        Collections.sort(targetTerms);
        int i = 0;
        for (long key : targetTerms) {
            prefsForUser.setItemID(i, key);
            if (termsMap.keySet().contains(key)) {
                prefsForUser.setValue(i, (float) termsMap.get(key));
            } else {
                prefsForUser.setValue(i, 0.0f);
            }
            i++;
        }
        preferences.put(userIndex.getAndIncrement(), prefsForUser);
    }

    private Map<Long, Integer> getTermsAndPayloadsByUserId(SolrQueryRequest req, int docId) throws IOException {
        Map<Long, Integer> termsMap = new HashMap<Long, Integer>();
        SolrIndexSearcher solrIndexSearcher = req.getSearcher();
        DirectoryReader directoryReader = solrIndexSearcher.getIndexReader();
        Fields fields = directoryReader.getTermVectors(docId);
        Terms terms = fields.terms(termFieldName);
        TermsEnum termsEnum = terms.iterator();
        if (docId == targetUserId) {
            termOrdList = new LinkedList<Long>();
            termNameList = new LinkedList<String>();
        }
        while (termsEnum != null && termsEnum.next() != null) {
            PostingsEnum postingsEnum = termsEnum.postings(null);
            postingsEnum.nextDoc();
            postingsEnum.nextPosition();
            String term = termsEnum.term().utf8ToString();
            if (StringUtils.equals(term, "-1")) {
                continue;
            }
            long termOrd = term2Ord.get(termsEnum.term().utf8ToString());
            if (docId == targetUserId) {
                termOrdList.add(termOrd);
                termNameList.add(term);
            }
            BytesRef pt = postingsEnum.getPayload();
            int payloads = encodePayloads(pt);
            termsMap.put(termOrd, payloads);
        }
        return termsMap;
    }

    public int encodePayloads(BytesRef bytesRef) {
        int offset = bytesRef.offset;
        int length = bytesRef.length;
        byte[] bytes = bytesRef.bytes;
        StringBuilder sb = new StringBuilder();
        final int end = offset + length;
        for (int i = offset; i < end; i++) {
            sb.append(Integer.toHexString(bytes[i] & 0xff));
        }
        return Integer.valueOf(sb.toString(), 16);
    }
}
