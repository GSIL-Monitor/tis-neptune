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
package com.qlangtech.tis.solrextend.queryparse.s4supplyStatementAudit;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import org.apache.lucene.index.DocValues;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.index.NumericDocValues;
import org.apache.lucene.index.SortedDocValues;
import org.apache.lucene.search.IndexSearcher;
import org.apache.solr.search.DelegatingCollector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.qlangtech.tis.solrextend.queryparse.BasicPostFilterQuery;

/*
 * http://k.2dfire.net/pages/viewpage.action?pageId=473006169<br>
 *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class SupplyStatementAuditQuery extends BasicPostFilterQuery implements ISupplyStatementAuditConstant {

    private static final Logger logger = LoggerFactory.getLogger(SupplyStatementAuditQuery.class);

    private Map<String, Accumulator> /* groupKey */
    amountAccumulator;

    private final boolean needSummary;

    private final String groupKey;

    private final Set<Integer> filterTypes;

    public SupplyStatementAuditQuery(// 
    Set<Integer> filterTypes, // 
    boolean needSummary, // 
    String groupKey, // 
    Map<String, /* groupKey */
    Accumulator> amountAccumulator) throws IOException {
        this.filterTypes = filterTypes;
        this.amountAccumulator = amountAccumulator;
        this.needSummary = needSummary;
        this.groupKey = groupKey;
    }

    public DelegatingCollector getFilterCollector(IndexSearcher indexSearcher) {
        return new DelegatingCollector() {

            private NumericDocValues typeDV;

            private NumericDocValues statusDV;

            private NumericDocValues amountDV;

            private SortedDocValues groupColDV;

            @Override
            protected void doSetNextReader(LeafReaderContext context) throws IOException {
                super.doSetNextReader(context);
                this.amountDV = DocValues.getNumeric(context.reader(), FIELD_KEY_AMOUNT);
                if (needSummary) {
                    this.statusDV = DocValues.getNumeric(context.reader(), FIELD_KEY_STATUS);
                }
                this.typeDV = DocValues.getNumeric(context.reader(), FIELD_KEY_TYPE);
                this.groupColDV = getGroupColDV(groupKey, context);
            }

            @Override
            public void collect(int doc) throws IOException {
                long type = typeDV.get(doc);
                String groupKey = this.groupColDV.get(doc).utf8ToString();
                long amount = this.amountDV.get(doc);
                if (needSummary) {
                    addAmount(filterTypes, (int) type, amount, amountAccumulator.get(KEY_ALL_SUMMARY_TYPE_3_AND_4_SUM));
                    Accumulator acc = amountAccumulator.get(KEY_ALL_SUMMARY_Status3_AND_Not_COUNT);
                    acc.increaseStatusCount(statusDV.get(doc));
                }
                Accumulator accumulator = amountAccumulator.get(groupKey);
                if (addAmount(filterTypes, (int) type, amount, accumulator)) {
                    super.collect(doc);
                }
            }
        };
    }

    /**
     * @param context
     * @return
     * @throws IOException
     */
    public static SortedDocValues getGroupColDV(String groupKey, LeafReaderContext context) throws IOException {
        return DocValues.getSorted(context.reader(), groupKey);
    }

    private boolean addAmount(Set<Integer> filterTypes, int type, long amount, Accumulator accumulator) {
        boolean shallCollect = false;
        // }
        if (filterTypes.contains(TYPE_IN_STORAGE_2) && TYPE_IN_STORAGE_2 == type) {
            shallCollect = accumulator.addType2Amount(amount);
        } else if (filterTypes.contains(TYPE_IN_STORAGE_3) && TYPE_IN_STORAGE_3 == type) {
            shallCollect = accumulator.addType3Amount(amount);
        } else if (filterTypes.contains(TYPE_REFUND_4) && TYPE_REFUND_4 == type) {
            shallCollect = accumulator.addType4Amount(amount);
        }
        if (shallCollect) {
            accumulator.tagOld();
        }
        return shallCollect;
    }

    public static class Accumulator {

        private long type3SumAmount;

        private long type2SumAmount;

        private long type4SumAmount;

        private int type3CountAmount;

        private int type2CountAmount;

        private int type4CountAmount;

        private long status3Count;

        private long statusNot3Count;

        // storage type3吗
        private boolean type3;

        public Accumulator(boolean type3) {
            this.type3 = type3;
        }

        private boolean _new = true;

        public void tagOld() {
            this._new = false;
        }

        public void increaseStatusCount(long status) {
            if (STATUS_IN_STORAGE_3 == status) {
                this.status3Count++;
            } else {
                this.statusNot3Count++;
            }
        }

        public long getStatus3Count() {
            return this.status3Count;
        }

        public long getStatusNot3Count() {
            return this.statusNot3Count;
        }

        public long getDifference() {
            if (type3) {
                return this.type3SumAmount - this.type4SumAmount;
            } else {
                return this.type2SumAmount - this.type4SumAmount;
            }
        }

        public boolean addType2Amount(long incr) {
            if (type3) {
                throw new IllegalStateException("not support");
            }
            this.type2CountAmount++;
            this.type2SumAmount += incr;
            return this._new;
        }

        public long getType2SumAmount() {
            return this.type2SumAmount;
        }

        public boolean addType3Amount(long incr) {
            if (!type3) {
                throw new IllegalStateException("not support");
            }
            this.type3CountAmount++;
            this.type3SumAmount += incr;
            return this._new;
        }

        public boolean addType4Amount(long incr) {
            this.type4CountAmount++;
            this.type4SumAmount += incr;
            return this._new;
        }

        public int getType3CountAmount() {
            return this.type3CountAmount;
        }

        public void setType3CountAmount(int type3CountAmount) {
            this.type3CountAmount = type3CountAmount;
        }

        public int getType2CountAmount() {
            return this.type2CountAmount;
        }

        public void setType2CountAmount(int type2CountAmount) {
            this.type2CountAmount = type2CountAmount;
        }

        public int getType4CountAmount() {
            return this.type4CountAmount;
        }

        public void setType4CountAmount(int type4CountAmount) {
            this.type4CountAmount = type4CountAmount;
        }

        public long getType3SumAmount() {
            return this.type3SumAmount;
        }

        public long getType4SumAmount() {
            return this.type4SumAmount;
        }
    }
}
