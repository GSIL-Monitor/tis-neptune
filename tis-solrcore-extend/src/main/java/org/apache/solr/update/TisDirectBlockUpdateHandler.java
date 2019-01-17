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
package org.apache.solr.update;

import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.solr.common.SolrException;
import org.apache.solr.core.SolrCore;
import org.apache.solr.util.RefCounted;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/*
 * Created by Qinjiu on 2016/12/19.
 * 用来给有父子block的更新，每次更新都以整个块为更新对象，所有父doc都包含_root_字段
 *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class TisDirectBlockUpdateHandler extends TisDirectUpdateHandler2 {

    private static final Logger log = LoggerFactory.getLogger(TisDirectUpdateHandler2.class);

    public TisDirectBlockUpdateHandler(SolrCore core) {
        super(core);
    }

    public TisDirectBlockUpdateHandler(SolrCore core, UpdateHandler updateHandler) {
        super(core, updateHandler);
    }

    @Override
    public int addDoc(AddUpdateCommand cmd) throws IOException {
        try {
            return addDoc0(cmd);
        } catch (SolrException e) {
            throw e;
        } catch (RuntimeException t) {
            throw new SolrException(SolrException.ErrorCode.BAD_REQUEST, String.format(Locale.ROOT, "Exception writing document id %s to the index; possible analysis error.", cmd.getPrintableId()), t);
        }
    }

    /**
     * @param cmd the command.
     * @return the count.
     */
    private int addDoc0(AddUpdateCommand cmd) throws IOException {
        int rc = -1;
        RefCounted<IndexWriter> iw = solrCoreState.getIndexWriter(core);
        try {
            IndexWriter writer = iw.get();
            addCommands.incrementAndGet();
            addCommandsCumulative.incrementAndGet();
            // if there is no ID field, don't overwrite
            if (idField == null) {
                cmd.overwrite = false;
            }
            try {
                if (cmd.overwrite) {
                    // Check for delete by query commands newer (i.e.
                    // reordered). This
                    // should always be null on a leader
                    List<UpdateLog.DBQ> deletesAfter = null;
                    if (ulog != null && cmd.version > 0) {
                        deletesAfter = ulog.getDBQNewer(cmd.version);
                    }
                    if (deletesAfter != null) {
                        log.info("Reordered DBQs detected.  Update=" + cmd + " DBQs=" + deletesAfter);
                        List<Query> dbqList = new ArrayList<>(deletesAfter.size());
                        for (UpdateLog.DBQ dbq : deletesAfter) {
                            try {
                                DeleteUpdateCommand tmpDel = new DeleteUpdateCommand(cmd.req);
                                tmpDel.query = dbq.q;
                                tmpDel.version = -dbq.version;
                                dbqList.add(getQuery(tmpDel));
                            } catch (Exception e) {
                                log.error("Exception parsing reordered query : " + dbq, e);
                            }
                        }
                        addAndDelete(cmd, dbqList);
                    } else {
                        Term updateTerm;
                        // 通过要更新的数据是否包含子记录来确定去更新整块还是更新单条记录
                        // update 2017-03-02所有的更新都更新整块
                        Term idTerm = new Term(ROOT_FIELD, cmd.getIndexedId());
                        boolean del = false;
                        if (cmd.updateTerm == null) {
                            updateTerm = idTerm;
                        } else {
                            // this is only used by the dedup update processor
                            del = true;
                            updateTerm = cmd.updateTerm;
                        }
                        writer.updateDocuments(updateTerm, cmd);
                        if (del) {
                            // ensure id remains unique
                            BooleanQuery.Builder bq = new BooleanQuery.Builder();
                            bq.add(new BooleanClause(new TermQuery(updateTerm), BooleanClause.Occur.MUST_NOT));
                            bq.add(new BooleanClause(new TermQuery(idTerm), BooleanClause.Occur.MUST));
                            writer.deleteDocuments(new DeleteByQueryWrapper(bq.build(), core.getLatestSchema()));
                        }
                        // log version was definitely committed.
                        if (ulog != null)
                            ulog.add(cmd);
                    }
                } else {
                    // allow duplicates
                    if (cmd.isBlock()) {
                        writer.addDocuments(cmd);
                    } else {
                        writer.addDocument(cmd.getLuceneDocument());
                    }
                    if (ulog != null)
                        ulog.add(cmd);
                }
                if ((cmd.getFlags() & UpdateCommand.IGNORE_AUTOCOMMIT) == 0) {
                    if (commitWithinSoftCommit) {
                        commitTracker.addedDocument(-1);
                        softCommitTracker.addedDocument(cmd.commitWithin);
                    } else {
                        softCommitTracker.addedDocument(-1);
                        commitTracker.addedDocument(cmd.commitWithin);
                    }
                }
                rc = 1;
            } finally {
                if (rc != 1) {
                    numErrors.incrementAndGet();
                    numErrorsCumulative.incrementAndGet();
                } else {
                    numDocsPending.incrementAndGet();
                }
            }
        } finally {
            iw.decref();
        }
        return rc;
    }
}
