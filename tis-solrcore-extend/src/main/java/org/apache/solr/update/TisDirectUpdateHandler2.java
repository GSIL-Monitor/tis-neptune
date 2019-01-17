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

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CodecReader;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.index.SlowCodecReaderWrapper;
import org.apache.lucene.index.Term;
import org.apache.lucene.queries.function.ValueSource;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.solr.common.SolrException;
import org.apache.solr.common.params.ModifiableSolrParams;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.common.util.SimpleOrderedMap;
import org.apache.solr.core.PluginInfo;
import org.apache.solr.core.SolrConfig.UpdateHandlerInfo;
import org.apache.solr.core.SolrCore;
import org.apache.solr.request.LocalSolrQueryRequest;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.request.SolrRequestInfo;
import org.apache.solr.response.SolrQueryResponse;
import org.apache.solr.schema.IndexSchema;
import org.apache.solr.schema.SchemaField;
import org.apache.solr.search.FunctionRangeQuery;
import org.apache.solr.search.QParser;
import org.apache.solr.search.QueryUtils;
import org.apache.solr.search.SolrIndexSearcher;
import org.apache.solr.search.SyntaxError;
import org.apache.solr.search.function.ValueSourceRangeFilter;
import org.apache.solr.util.RefCounted;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/* *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class TisDirectUpdateHandler2 extends UpdateHandler implements SolrCoreState.IndexWriterCloser {

    private static final Logger log = LoggerFactory.getLogger(TisDirectUpdateHandler2.class);

    protected final SolrCoreState solrCoreState;

    public static final String ROOT_FIELD = "_root_";

    // stats
    AtomicLong addCommands = new AtomicLong();

    AtomicLong addCommandsCumulative = new AtomicLong();

    AtomicLong deleteByIdCommands = new AtomicLong();

    AtomicLong deleteByIdCommandsCumulative = new AtomicLong();

    AtomicLong deleteByQueryCommands = new AtomicLong();

    AtomicLong deleteByQueryCommandsCumulative = new AtomicLong();

    AtomicLong expungeDeleteCommands = new AtomicLong();

    AtomicLong mergeIndexesCommands = new AtomicLong();

    AtomicLong commitCommands = new AtomicLong();

    AtomicLong optimizeCommands = new AtomicLong();

    AtomicLong rollbackCommands = new AtomicLong();

    AtomicLong numDocsPending = new AtomicLong();

    AtomicLong numErrors = new AtomicLong();

    AtomicLong numErrorsCumulative = new AtomicLong();

    // tracks when auto-commit should occur
    protected final CommitTracker commitTracker;

    protected final CommitTracker softCommitTracker;

    protected boolean commitWithinSoftCommit;

    protected boolean indexWriterCloseWaitsForMerges;

    public TisDirectUpdateHandler2(SolrCore core) {
        // super(core);
        super(core, new TisUpdateLog());
        // 百岁修改 20150929
        PluginInfo ulogPluginInfo = core.getSolrConfig().getPluginInfo(UpdateLog.class.getName());
        this.ulog.init(ulogPluginInfo);
        this.ulog.init(this, core);
        // 百岁修改 20150929 end
        solrCoreState = core.getSolrCoreState();
        UpdateHandlerInfo updateHandlerInfo = core.getSolrConfig().getUpdateHandlerInfo();
        // getInt("updateHandler/autoCommit/maxDocs",
        int docsUpperBound = updateHandlerInfo.autoCommmitMaxDocs;
        // -1);
        // getInt("updateHandler/autoCommit/maxTime",
        int timeUpperBound = updateHandlerInfo.autoCommmitMaxTime;
        // -1);
        commitTracker = new CommitTracker("Hard", core, docsUpperBound, timeUpperBound, updateHandlerInfo.openSearcher, false);
        // getInt("updateHandler/autoSoftCommit/maxDocs",
        int softCommitDocsUpperBound = updateHandlerInfo.autoSoftCommmitMaxDocs;
        // -1);
        // getInt("updateHandler/autoSoftCommit/maxTime",
        int softCommitTimeUpperBound = updateHandlerInfo.autoSoftCommmitMaxTime;
        // -1);
        softCommitTracker = new CommitTracker("Soft", core, softCommitDocsUpperBound, softCommitTimeUpperBound, true, true);
        commitWithinSoftCommit = updateHandlerInfo.commitWithinSoftCommit;
        indexWriterCloseWaitsForMerges = updateHandlerInfo.indexWriterCloseWaitsForMerges;
    }

    public TisDirectUpdateHandler2(SolrCore core, UpdateHandler updateHandler) {
        super(core, updateHandler.getUpdateLog());
        solrCoreState = core.getSolrCoreState();
        UpdateHandlerInfo updateHandlerInfo = core.getSolrConfig().getUpdateHandlerInfo();
        // getInt("updateHandler/autoCommit/maxDocs",
        int docsUpperBound = updateHandlerInfo.autoCommmitMaxDocs;
        // -1);
        // getInt("updateHandler/autoCommit/maxTime",
        int timeUpperBound = updateHandlerInfo.autoCommmitMaxTime;
        // -1);
        commitTracker = new CommitTracker("Hard", core, docsUpperBound, timeUpperBound, updateHandlerInfo.openSearcher, false);
        // getInt("updateHandler/autoSoftCommit/maxDocs",
        int softCommitDocsUpperBound = updateHandlerInfo.autoSoftCommmitMaxDocs;
        // -1);
        // getInt("updateHandler/autoSoftCommit/maxTime",
        int softCommitTimeUpperBound = updateHandlerInfo.autoSoftCommmitMaxTime;
        // -1);
        softCommitTracker = new CommitTracker("Soft", core, softCommitDocsUpperBound, softCommitTimeUpperBound, updateHandlerInfo.openSearcher, true);
        commitWithinSoftCommit = updateHandlerInfo.commitWithinSoftCommit;
        indexWriterCloseWaitsForMerges = updateHandlerInfo.indexWriterCloseWaitsForMerges;
        UpdateLog existingLog = updateHandler.getUpdateLog();
        if (this.ulog != null && this.ulog == existingLog) {
            // If we are reusing the existing update log, inform the log that
            // its update handler has changed.
            // We do this as late as possible.
            this.ulog.init(this, core);
        }
    }

    private void deleteAll() throws IOException {
        log.info(core.getLogId() + "REMOVING ALL DOCUMENTS FROM INDEX");
        RefCounted<IndexWriter> iw = solrCoreState.getIndexWriter(core);
        try {
            iw.get().deleteAll();
        } finally {
            iw.decref();
        }
    }

    protected void rollbackWriter() throws IOException {
        numDocsPending.set(0);
        solrCoreState.rollbackIndexWriter(core);
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
     * This is the implementation of {@link #addDoc0(AddUpdateCommand)}. It is
     * factored out to allow an exception handler to decorate RuntimeExceptions
     * with information about the document being handled.
     *
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
                        // normal update
                        Term updateTerm;
                        Term idTerm;
                        // Term idTerm = new Term(
                        // cmd.isBlock() ? "_root_" : idField.getName(),
                        // cmd.getIndexedId());
                        Document luceneDocument = cmd.getLuceneDocument();
                        // 通过有没有root来判断去更新一条记录还是整个block
                        if (luceneDocument.getField(ROOT_FIELD) == null) {
                            idTerm = new Term(idField.getName(), cmd.getIndexedId());
                        } else {
                            idTerm = new Term(ROOT_FIELD, cmd.getIndexedId());
                        }
                        boolean del = false;
                        if (cmd.updateTerm == null) {
                            updateTerm = idTerm;
                        } else {
                            // this is only used by the dedup update processor
                            del = true;
                            updateTerm = cmd.updateTerm;
                        }
                        if (cmd.isBlock()) {
                            writer.updateDocuments(updateTerm, cmd);
                        } else {
                            // Document luceneDocument = cmd.getLuceneDocument();
                            writer.updateDocument(updateTerm, luceneDocument);
                        }
                        if (del) {
                            // ensure id remains unique
                            BooleanQuery.Builder bq = new BooleanQuery.Builder();
                            bq.add(new BooleanClause(new TermQuery(updateTerm), Occur.MUST_NOT));
                            bq.add(new BooleanClause(new TermQuery(idTerm), Occur.MUST));
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

    private void updateDeleteTrackers(DeleteUpdateCommand cmd) {
        if ((cmd.getFlags() & UpdateCommand.IGNORE_AUTOCOMMIT) == 0) {
            if (commitWithinSoftCommit) {
                softCommitTracker.deletedDocument(cmd.commitWithin);
            } else {
                commitTracker.deletedDocument(cmd.commitWithin);
            }
            if (commitTracker.getTimeUpperBound() > 0) {
                commitTracker.scheduleCommitWithin(commitTracker.getTimeUpperBound());
            }
            if (softCommitTracker.getTimeUpperBound() > 0) {
                softCommitTracker.scheduleCommitWithin(softCommitTracker.getTimeUpperBound());
            }
        }
    }

    // we don't return the number of docs deleted because it's not always
    // possible to quickly know that info.
    @Override
    public void delete(DeleteUpdateCommand cmd) throws IOException {
        deleteByIdCommands.incrementAndGet();
        deleteByIdCommandsCumulative.incrementAndGet();
        Term deleteTerm = new Term(idField.getName(), cmd.getIndexedId());
        // SolrCore.verbose("deleteDocuments",deleteTerm,writer);
        RefCounted<IndexWriter> iw = solrCoreState.getIndexWriter(core);
        try {
            iw.get().deleteDocuments(deleteTerm);
        } finally {
            iw.decref();
        }
        if (ulog != null)
            ulog.delete(cmd);
        updateDeleteTrackers(cmd);
    }

    public void clearIndex() throws IOException {
        deleteAll();
        if (ulog != null) {
            ulog.deleteAll();
        }
    }

    protected Query getQuery(DeleteUpdateCommand cmd) {
        Query q;
        try {
            // move this higher in the stack?
            QParser parser = QParser.getParser(cmd.getQuery(), "lucene", cmd.req);
            q = parser.getQuery();
            q = QueryUtils.makeQueryable(q);
            // Make sure not to delete newer versions
            if (ulog != null && cmd.getVersion() != 0 && cmd.getVersion() != -Long.MAX_VALUE) {
                BooleanQuery.Builder bq = new BooleanQuery.Builder();
                bq.add(q, Occur.MUST);
                SchemaField sf = ulog.getVersionInfo().getVersionField();
                ValueSource vs = sf.getType().getValueSource(sf, null);
                ValueSourceRangeFilter filt = new ValueSourceRangeFilter(vs, null, Long.toString(Math.abs(cmd.getVersion())), true, true);
                FunctionRangeQuery range = new FunctionRangeQuery(filt);
                bq.add(range, Occur.MUST);
                q = bq.build();
            }
            return q;
        } catch (SyntaxError e) {
            throw new SolrException(SolrException.ErrorCode.BAD_REQUEST, e);
        }
    }

    // we don't return the number of docs deleted because it's not always
    // possible to quickly know that info.
    @Override
    public void deleteByQuery(DeleteUpdateCommand cmd) throws IOException {
        deleteByQueryCommands.incrementAndGet();
        deleteByQueryCommandsCumulative.incrementAndGet();
        boolean madeIt = false;
        try {
            Query q = getQuery(cmd);
            boolean delAll = MatchAllDocsQuery.class == q.getClass();
            // log, etc
            if (delAll && cmd.getVersion() == -Long.MAX_VALUE) {
                synchronized (solrCoreState.getUpdateLock()) {
                    deleteAll();
                    ulog.deleteAll();
                    return;
                }
            }
            // 
            synchronized (solrCoreState.getUpdateLock()) {
                if (delAll) {
                    deleteAll();
                } else {
                    RefCounted<IndexWriter> iw = solrCoreState.getIndexWriter(core);
                    try {
                        iw.get().deleteDocuments(new DeleteByQueryWrapper(q, core.getLatestSchema()));
                    } finally {
                        iw.decref();
                    }
                }
                if (ulog != null)
                    ulog.deleteByQuery(cmd);
            }
            madeIt = true;
            updateDeleteTrackers(cmd);
        } finally {
            if (!madeIt) {
                numErrors.incrementAndGet();
                numErrorsCumulative.incrementAndGet();
            }
        }
    }

    /**
     * Add a document execute the deletes as atomically as possible
     */
    protected void addAndDelete(AddUpdateCommand cmd, List<Query> dbqList) throws IOException {
        Document luceneDocument = cmd.getLuceneDocument();
        Term idTerm = new Term(idField.getName(), cmd.getIndexedId());
        // see comment in deleteByQuery
        synchronized (solrCoreState.getUpdateLock()) {
            RefCounted<IndexWriter> iw = solrCoreState.getIndexWriter(core);
            try {
                IndexWriter writer = iw.get();
                writer.updateDocument(idTerm, luceneDocument);
                for (Query q : dbqList) {
                    writer.deleteDocuments(new DeleteByQueryWrapper(q, core.getLatestSchema()));
                }
            } finally {
                iw.decref();
            }
            if (ulog != null)
                ulog.add(cmd, true);
        }
    }

    @Override
    public int mergeIndexes(MergeIndexesCommand cmd) throws IOException {
        mergeIndexesCommands.incrementAndGet();
        int rc;
        log.info("start " + cmd);
        List<DirectoryReader> readers = cmd.readers;
        if (readers != null && readers.size() > 0) {
            List<CodecReader> mergeReaders = new ArrayList<>();
            for (DirectoryReader reader : readers) {
                for (LeafReaderContext leaf : reader.leaves()) {
                    mergeReaders.add(SlowCodecReaderWrapper.wrap(leaf.reader()));
                }
            }
            RefCounted<IndexWriter> iw = solrCoreState.getIndexWriter(core);
            try {
                iw.get().addIndexes(mergeReaders.toArray(new CodecReader[mergeReaders.size()]));
            } finally {
                iw.decref();
            }
            rc = 1;
        } else {
            rc = 0;
        }
        log.info("end_mergeIndexes");
        // TODO: consider soft commit issues
        if (rc == 1 && commitTracker.getTimeUpperBound() > 0) {
            commitTracker.scheduleCommitWithin(commitTracker.getTimeUpperBound());
        } else if (rc == 1 && softCommitTracker.getTimeUpperBound() > 0) {
            softCommitTracker.scheduleCommitWithin(softCommitTracker.getTimeUpperBound());
        }
        return rc;
    }

    public void prepareCommit(CommitUpdateCommand cmd) throws IOException {
        boolean error = true;
        try {
            log.info("start " + cmd);
            RefCounted<IndexWriter> iw = solrCoreState.getIndexWriter(core);
            try {
                final Map<String, String> commitData = new HashMap<>();
                commitData.put(SolrIndexWriter.COMMIT_TIME_MSEC_KEY, String.valueOf(System.currentTimeMillis()));
                iw.get().setCommitData(commitData);
                iw.get().prepareCommit();
            } finally {
                iw.decref();
            }
            log.info("end_prepareCommit");
            error = false;
        } finally {
            if (error)
                numErrors.incrementAndGet();
        }
    }

    @Override
    public void commit(CommitUpdateCommand cmd) throws IOException {
        if (cmd.prepareCommit) {
            prepareCommit(cmd);
            return;
        }
        if (cmd.optimize) {
            optimizeCommands.incrementAndGet();
        } else {
            commitCommands.incrementAndGet();
            if (cmd.expungeDeletes)
                expungeDeleteCommands.incrementAndGet();
        }
        Future[] waitSearcher = null;
        if (cmd.waitSearcher) {
            waitSearcher = new Future[1];
        }
        boolean error = true;
        try {
            // only allow one hard commit to proceed at once
            if (!cmd.softCommit) {
                solrCoreState.getCommitLock().lock();
            }
            log.info("start " + cmd);
            if (cmd.openSearcher) {
                // we can cancel any pending soft commits if this commit will
                // open a new searcher
                softCommitTracker.cancelPendingCommit();
            }
            if (!cmd.softCommit && (cmd.openSearcher || !commitTracker.getOpenSearcher())) {
                // cancel a pending hard commit if this commit is of equal or
                // greater "strength"...
                // If the autoCommit has openSearcher=true, then this commit
                // must have openSearcher=true
                // to cancel.
                commitTracker.cancelPendingCommit();
            }
            RefCounted<IndexWriter> iw = solrCoreState.getIndexWriter(core);
            try {
                IndexWriter writer = iw.get();
                if (cmd.optimize) {
                    writer.forceMerge(cmd.maxOptimizeSegments);
                } else if (cmd.expungeDeletes) {
                    writer.forceMergeDeletes();
                }
                if (!cmd.softCommit) {
                    synchronized (solrCoreState.getUpdateLock()) {
                        // postSoft... see postSoft comments.
                        if (ulog != null)
                            ulog.preCommit(cmd);
                    }
                    if (writer.hasUncommittedChanges()) {
                        final Map<String, String> commitData = new HashMap<>();
                        commitData.put(SolrIndexWriter.COMMIT_TIME_MSEC_KEY, String.valueOf(System.currentTimeMillis()));
                        writer.setCommitData(commitData);
                        writer.commit();
                    } else {
                        log.info("No uncommitted changes. Skipping IW.commit.");
                    }
                    // SolrCore.verbose("writer.commit() end");
                    numDocsPending.set(0);
                    callPostCommitCallbacks();
                }
            } finally {
                iw.decref();
            }
            if (cmd.optimize) {
                callPostOptimizeCallbacks();
            }
            if (cmd.softCommit) {
                // ulog.preSoftCommit();
                synchronized (solrCoreState.getUpdateLock()) {
                    if (ulog != null)
                        ulog.preSoftCommit(cmd);
                    core.getSearcher(true, false, waitSearcher, true);
                    if (ulog != null)
                        ulog.postSoftCommit(cmd);
                }
                callPostSoftCommitCallbacks();
            } else {
                synchronized (solrCoreState.getUpdateLock()) {
                    if (ulog != null)
                        ulog.preSoftCommit(cmd);
                    if (cmd.openSearcher) {
                        core.getSearcher(true, false, waitSearcher);
                    } else {
                        // force open a new realtime searcher so realtime-get
                        // and versioning code can see the latest
                        RefCounted<SolrIndexSearcher> searchHolder = core.openNewSearcher(true, true);
                        searchHolder.decref();
                    }
                    if (ulog != null)
                        ulog.postSoftCommit(cmd);
                }
                if (ulog != null)
                    // postCommit currently means new
                    ulog.postCommit(cmd);
            // searcher has
            // also been opened
            }
            if (cmd.softCommit) {
                softCommitTracker.didCommit();
            } else {
                commitTracker.didCommit();
            }
            log.info("end_commit_flush");
            error = false;
        } finally {
            if (!cmd.softCommit) {
                solrCoreState.getCommitLock().unlock();
            }
            addCommands.set(0);
            deleteByIdCommands.set(0);
            deleteByQueryCommands.set(0);
            if (error)
                numErrors.incrementAndGet();
        }
        // proceed.
        if (waitSearcher != null && waitSearcher[0] != null) {
            try {
                waitSearcher[0].get();
            } catch (InterruptedException | ExecutionException e) {
                SolrException.log(log, e);
            }
        }
    }

    @Override
    public void newIndexWriter(boolean rollback) throws IOException {
        solrCoreState.newIndexWriter(core, rollback);
    }

    /**
     * @since Solr 1.4
     */
    @Override
    public void rollback(RollbackUpdateCommand cmd) throws IOException {
        if (core.getCoreDescriptor().getCoreContainer().isZooKeeperAware()) {
            throw new UnsupportedOperationException("Rollback is currently not supported in SolrCloud mode. (SOLR-4895)");
        }
        rollbackCommands.incrementAndGet();
        boolean error = true;
        try {
            log.info("start " + cmd);
            rollbackWriter();
            // callPostRollbackCallbacks();
            // reset commit tracking
            commitTracker.didRollback();
            softCommitTracker.didRollback();
            log.info("end_rollback");
            error = false;
        } finally {
            addCommandsCumulative.set(addCommandsCumulative.get() - addCommands.getAndSet(0));
            deleteByIdCommandsCumulative.set(deleteByIdCommandsCumulative.get() - deleteByIdCommands.getAndSet(0));
            deleteByQueryCommandsCumulative.set(deleteByQueryCommandsCumulative.get() - deleteByQueryCommands.getAndSet(0));
            if (error)
                numErrors.incrementAndGet();
        }
    }

    @Override
    public UpdateLog getUpdateLog() {
        return ulog;
    }

    @Override
    public void close() throws IOException {
        log.info("closing " + this);
        commitTracker.close();
        softCommitTracker.close();
        numDocsPending.set(0);
    }

    // TODO: make this a real config
    public static boolean commitOnClose = true;

    // option?
    // IndexWriterCloser interface method - called from
    // solrCoreState.decref(this)
    @Override
    public void closeWriter(IndexWriter writer) throws IOException {
        boolean clearRequestInfo = false;
        solrCoreState.getCommitLock().lock();
        try {
            SolrQueryRequest req = new LocalSolrQueryRequest(core, new ModifiableSolrParams());
            SolrQueryResponse rsp = new SolrQueryResponse();
            if (SolrRequestInfo.getRequestInfo() == null) {
                clearRequestInfo = true;
                // important
                SolrRequestInfo.setRequestInfo(new SolrRequestInfo(req, rsp));
            // for
            // debugging
            }
            if (!commitOnClose) {
                if (writer != null) {
                    writer.rollback();
                }
                // means we can't delete them on windows (needed for tests)
                if (ulog != null)
                    ulog.close(false);
                return;
            }
            // do a commit before we quit?
            boolean tryToCommit = writer != null && ulog != null && ulog.hasUncommittedChanges() && ulog.getState() == UpdateLog.State.ACTIVE;
            try {
                if (tryToCommit) {
                    log.info("Committing on IndexWriter close.");
                    CommitUpdateCommand cmd = new CommitUpdateCommand(req, false);
                    cmd.openSearcher = false;
                    cmd.waitSearcher = false;
                    cmd.softCommit = false;
                    synchronized (solrCoreState.getUpdateLock()) {
                        ulog.preCommit(cmd);
                    }
                    // todo: refactor this shared code (or figure out why a real
                    // CommitUpdateCommand can't be used)
                    final Map<String, String> commitData = new HashMap<>();
                    commitData.put(SolrIndexWriter.COMMIT_TIME_MSEC_KEY, String.valueOf(System.currentTimeMillis()));
                    writer.setCommitData(commitData);
                    writer.commit();
                    synchronized (solrCoreState.getUpdateLock()) {
                        ulog.postCommit(cmd);
                    }
                }
            } catch (Throwable th) {
                log.error("Error in final commit", th);
                if (th instanceof OutOfMemoryError) {
                    throw (OutOfMemoryError) th;
                }
            }
            // cap any ulog files.
            try {
                if (ulog != null)
                    ulog.close(false);
            } catch (Throwable th) {
                log.error("Error closing log files", th);
                if (th instanceof OutOfMemoryError) {
                    throw (OutOfMemoryError) th;
                }
            }
            if (writer != null) {
                writer.close();
            }
        } finally {
            solrCoreState.getCommitLock().unlock();
            if (clearRequestInfo)
                SolrRequestInfo.clearRequestInfo();
        }
    }

    @Override
    public void split(SplitIndexCommand cmd) throws IOException {
        commit(new CommitUpdateCommand(cmd.req, false));
        SolrIndexSplitter splitter = new SolrIndexSplitter(cmd);
        splitter.split();
    }

    // ///////////////////////////////////////////////////////////////////
    // SolrInfoMBean stuff: Statistics and Module Info
    // ///////////////////////////////////////////////////////////////////
    @Override
    public String getName() {
        return DirectUpdateHandler2.class.getName();
    }

    @Override
    public String getVersion() {
        return SolrCore.version;
    }

    @Override
    public String getDescription() {
        return "Update handler that efficiently directly updates the on-disk main lucene index";
    }

    @Override
    public Category getCategory() {
        return Category.UPDATEHANDLER;
    }

    @Override
    public String getSource() {
        return null;
    }

    @Override
    public URL[] getDocs() {
        return null;
    }

    @Override
    public NamedList getStatistics() {
        NamedList lst = new SimpleOrderedMap();
        lst.add("commits", commitCommands.get());
        if (commitTracker.getDocsUpperBound() > 0) {
            lst.add("autocommit maxDocs", commitTracker.getDocsUpperBound());
        }
        if (commitTracker.getTimeUpperBound() > 0) {
            lst.add("autocommit maxTime", "" + commitTracker.getTimeUpperBound() + "ms");
        }
        lst.add("autocommits", commitTracker.getCommitCount());
        if (softCommitTracker.getDocsUpperBound() > 0) {
            lst.add("soft autocommit maxDocs", softCommitTracker.getDocsUpperBound());
        }
        if (softCommitTracker.getTimeUpperBound() > 0) {
            lst.add("soft autocommit maxTime", "" + softCommitTracker.getTimeUpperBound() + "ms");
        }
        lst.add("soft autocommits", softCommitTracker.getCommitCount());
        lst.add("optimizes", optimizeCommands.get());
        lst.add("rollbacks", rollbackCommands.get());
        lst.add("expungeDeletes", expungeDeleteCommands.get());
        lst.add("docsPending", numDocsPending.get());
        // pset.size() not synchronized, but it should be fine to access.
        // lst.add("deletesPending", pset.size());
        lst.add("adds", addCommands.get());
        lst.add("deletesById", deleteByIdCommands.get());
        lst.add("deletesByQuery", deleteByQueryCommands.get());
        lst.add("errors", numErrors.get());
        lst.add("cumulative_adds", addCommandsCumulative.get());
        lst.add("cumulative_deletesById", deleteByIdCommandsCumulative.get());
        lst.add("cumulative_deletesByQuery", deleteByQueryCommandsCumulative.get());
        lst.add("cumulative_errors", numErrorsCumulative.get());
        if (this.ulog != null) {
            lst.add("transaction_logs_total_size", ulog.getTotalLogsSize());
            lst.add("transaction_logs_total_number", ulog.getTotalLogsNumber());
        }
        return lst;
    }

    @Override
    public String toString() {
        return "DirectUpdateHandler2" + getStatistics();
    }

    @Override
    public SolrCoreState getSolrCoreState() {
        return solrCoreState;
    }

    // allow access for tests
    public CommitTracker getCommitTracker() {
        return commitTracker;
    }

    // allow access for tests
    public CommitTracker getSoftCommitTracker() {
        return softCommitTracker;
    }
}
