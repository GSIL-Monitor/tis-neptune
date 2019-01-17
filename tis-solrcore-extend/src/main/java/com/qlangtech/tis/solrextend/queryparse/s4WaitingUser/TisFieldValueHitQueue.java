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
package com.qlangtech.tis.solrextend.queryparse.s4WaitingUser;

/**
 * @author 百岁（baisui@2dfire.com）
 *
 * @date 2017年6月13日
 */
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.search.FieldComparator;
import org.apache.lucene.search.FieldDoc;
import org.apache.lucene.search.FieldValueHitQueue.Entry;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.LeafFieldComparator;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.util.PriorityQueue;

/*
 * Expert: A hit queue for sorting by hits by terms in more than one field.
 * @lucene.experimental
 * @since 2.9
 * @see IndexSearcher#search(Query,int,Sort)
 *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public abstract class TisFieldValueHitQueue<T extends Entry> extends PriorityQueue<T> {

    // 百岁修改 20170613 ▼▼▼▼▼▼
    // 记录已经在堆里面的记录
    protected final Set<Integer> slots = new HashSet<Integer>();

    public T insertWithOverflow(T element) {
        boolean alreadyContain = this.slots.contains(element.slot);
        if (alreadyContain) {
            return element;
        }
        return super.insertWithOverflow(element);
    }

    /**
     * An implementation of {@link TisFieldValueHitQueue} which is optimized in
     * case there is just one comparator.
     */
    private static final class OneComparatorFieldValueHitQueue<T extends Entry> extends TisFieldValueHitQueue<T> {

        private final int oneReverseMul;

        private final FieldComparator<?> oneComparator;

        public OneComparatorFieldValueHitQueue(SortField[] fields, int size) throws IOException {
            super(fields, size);
            assert fields.length == 1;
            oneComparator = comparators[0];
            oneReverseMul = reverseMul[0];
        }

        /**
         * Returns whether <code>hitA</code> is less relevant than
         * <code>hitB</code>.
         *
         * @param hitA
         *            Entry
         * @param hitB
         *            Entry
         * @return <code>true</code> if document <code>hitA</code> should be
         *         sorted after document <code>hitB</code>.
         */
        @Override
        protected boolean lessThan(final Entry hitA, final Entry hitB) {
            assert hitA != hitB;
            assert hitA.slot != hitB.slot;
            final int c = oneReverseMul * oneComparator.compare(hitA.slot, hitB.slot);
            if (c != 0) {
                if (c > 0) {
                    return true;
                } else {
                    this.slots.add(hitA.slot);
                    return false;
                }
            }
            // #31241):
            if (hitA.doc > hitB.doc) {
                return true;
            } else {
                this.slots.add(hitA.slot);
                return false;
            }
        }
    }

    // prevent instantiation and extension.
    private TisFieldValueHitQueue(SortField[] fields, int size) throws IOException {
        super(size);
        // When we get here, fields.length is guaranteed to be > 0, therefore no
        // need to check it again.
        // All these are required by this class's API - need to return arrays.
        // Therefore even in the case of a single comparator, create an array
        // anyway.
        this.fields = fields;
        int numComparators = fields.length;
        comparators = new FieldComparator<?>[numComparators];
        reverseMul = new int[numComparators];
        for (int i = 0; i < numComparators; ++i) {
            SortField field = fields[i];
            reverseMul[i] = field.getReverse() ? -1 : 1;
            comparators[i] = field.getComparator(size, i);
        }
    }

    /**
     * Creates a hit queue sorted by the given list of fields.
     *
     * <p>
     * <b>NOTE</b>: The instances returned by this method pre-allocate a full
     * array of length <code>numHits</code>.
     *
     * @param fields
     *            SortField array we are sorting by in priority order (highest
     *            priority first); cannot be <code>null</code> or empty
     * @param size
     *            The number of hits to retain. Must be greater than zero.
     * @throws IOException
     *             if there is a low-level IO error
     */
    public static <T extends Entry> TisFieldValueHitQueue<T> create(SortField[] fields, int size) throws IOException {
        if (fields.length == 0) {
            throw new IllegalArgumentException("Sort must contain at least one field");
        }
        if (fields.length == 1) {
            return new OneComparatorFieldValueHitQueue<>(fields, size);
        } else {
            // return new MultiComparatorsFieldValueHitQueue<>(fields, size);
            throw new IllegalStateException("fields.length must be 1");
        }
    }

    public FieldComparator<?>[] getComparators() {
        return comparators;
    }

    public int[] getReverseMul() {
        return reverseMul;
    }

    public LeafFieldComparator[] getComparators(LeafReaderContext context) throws IOException {
        LeafFieldComparator[] comparators = new LeafFieldComparator[this.comparators.length];
        for (int i = 0; i < comparators.length; ++i) {
            comparators[i] = this.comparators[i].getLeafComparator(context);
        }
        return comparators;
    }

    /**
     * Stores the sort criteria being used.
     */
    protected final SortField[] fields;

    protected final FieldComparator<?>[] comparators;

    protected final int[] reverseMul;

    @Override
    protected abstract boolean lessThan(final Entry a, final Entry b);

    /**
     * Given a queue Entry, creates a corresponding FieldDoc that contains the
     * values used to sort the given document. These values are not the raw
     * values out of the index, but the internal representation of them. This is
     * so the given search hit can be collated by a MultiSearcher with other
     * search hits.
     *
     * @param entry
     *            The Entry used to create a FieldDoc
     * @return The newly created FieldDoc
     * @see IndexSearcher#search(Query,int,Sort)
     */
    FieldDoc fillFields(final Entry entry) {
        final int n = comparators.length;
        final Object[] fields = new Object[n];
        for (int i = 0; i < n; ++i) {
            fields[i] = comparators[i].value(entry.slot);
        }
        // if (maxscore > 1.0f) doc.score /= maxscore; // normalize scores
        return new FieldDoc(entry.doc, entry.score, fields);
    }

    /**
     * Returns the SortFields being used by this hit queue.
     */
    SortField[] getFields() {
        return fields;
    }
}
