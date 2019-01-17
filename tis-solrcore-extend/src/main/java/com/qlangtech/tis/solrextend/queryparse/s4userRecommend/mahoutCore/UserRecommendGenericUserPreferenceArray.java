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
package com.qlangtech.tis.solrextend.queryparse.s4userRecommend.mahoutCore;

import com.google.common.base.Function;
import com.google.common.collect.Iterators;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/* *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public final class UserRecommendGenericUserPreferenceArray implements UserRecommendPreferenceArray {

    private static final int ITEM = 1;

    private static final int VALUE = 2;

    private static final int VALUE_REVERSED = 3;

    private final long[] ids;

    private long id;

    private final float[] values;

    public UserRecommendGenericUserPreferenceArray(int size) {
        this.ids = new long[size];
        values = new float[size];
        // as a sort of 'unspecified' value
        this.id = Long.MIN_VALUE;
    }

    public UserRecommendGenericUserPreferenceArray(List<? extends UserRecommendPreference> prefs) {
        this(prefs.size());
        int size = prefs.size();
        long userID = Long.MIN_VALUE;
        for (int i = 0; i < size; i++) {
            UserRecommendPreference pref = prefs.get(i);
            if (i == 0) {
                userID = pref.getUserID();
            } else {
                if (userID != pref.getUserID()) {
                    throw new IllegalArgumentException("Not all user IDs are the same");
                }
            }
            ids[i] = pref.getItemID();
            values[i] = pref.getValue();
        }
        id = userID;
    }

    /**
     * This is a private copy constructor for clone().
     */
    private UserRecommendGenericUserPreferenceArray(long[] ids, long id, float[] values) {
        this.ids = ids;
        this.id = id;
        this.values = values;
    }

    public int length() {
        return ids.length;
    }

    public UserRecommendPreference get(int i) {
        return new UserRecommendPreferenceView(i);
    }

    public void set(int i, UserRecommendPreference pref) {
        id = pref.getUserID();
        ids[i] = pref.getItemID();
        values[i] = pref.getValue();
    }

    public long getUserID(int i) {
        return id;
    }

    /**
     * {@inheritDoc}
     *
     * Note that this method will actually set the user ID for <em>all</em> preferences.
     */
    public void setUserID(int i, long userID) {
        id = userID;
    }

    public long getItemID(int i) {
        return ids[i];
    }

    public void setItemID(int i, long itemID) {
        ids[i] = itemID;
    }

    /**
     * @return all item IDs
     */
    public long[] getIDs() {
        return ids;
    }

    public float getValue(int i) {
        return values[i];
    }

    public void setValue(int i, float value) {
        values[i] = value;
    }

    public void sortByUser() {
    }

    public void sortByItem() {
        lateralSort(ITEM);
    }

    public void sortByValue() {
        lateralSort(VALUE);
    }

    public void sortByValueReversed() {
        lateralSort(VALUE_REVERSED);
    }

    public boolean hasPrefWithUserID(long userID) {
        return id == userID;
    }

    public boolean hasPrefWithItemID(long itemID) {
        for (long id : ids) {
            if (itemID == id) {
                return true;
            }
        }
        return false;
    }

    private void lateralSort(int type) {
        // Comb sort: http://en.wikipedia.org/wiki/Comb_sort
        int length = length();
        int gap = length;
        boolean swapped = false;
        while (gap > 1 || swapped) {
            if (gap > 1) {
                // = 1 / (1 - 1/e^phi)
                gap /= 1.247330950103979;
            }
            swapped = false;
            int max = length - gap;
            for (int i = 0; i < max; i++) {
                int other = i + gap;
                if (isLess(other, i, type)) {
                    swap(i, other);
                    swapped = true;
                }
            }
        }
    }

    private boolean isLess(int i, int j, int type) {
        switch(type) {
            case ITEM:
                return ids[i] < ids[j];
            case VALUE:
                return values[i] < values[j];
            case VALUE_REVERSED:
                return values[i] > values[j];
            default:
                throw new IllegalStateException();
        }
    }

    private void swap(int i, int j) {
        long temp1 = ids[i];
        float temp2 = values[i];
        ids[i] = ids[j];
        values[i] = values[j];
        ids[j] = temp1;
        values[j] = temp2;
    }

    @Override
    public UserRecommendGenericUserPreferenceArray clone() {
        return new UserRecommendGenericUserPreferenceArray(ids.clone(), id, values.clone());
    }

    @Override
    public int hashCode() {
        return (int) (id >> 32) ^ (int) id ^ Arrays.hashCode(ids) ^ Arrays.hashCode(values);
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof UserRecommendGenericUserPreferenceArray)) {
            return false;
        }
        UserRecommendGenericUserPreferenceArray otherArray = (UserRecommendGenericUserPreferenceArray) other;
        return id == otherArray.id && Arrays.equals(ids, otherArray.ids) && Arrays.equals(values, otherArray.values);
    }

    public Iterator<UserRecommendPreference> iterator() {
        return Iterators.transform(new UserRecommendCountingIterator(length()), new Function<Integer, UserRecommendPreference>() {

            public UserRecommendPreference apply(Integer from) {
                return new UserRecommendPreferenceView(from);
            }
        });
    }

    @Override
    public String toString() {
        if (ids == null || ids.length == 0) {
            return "GenericUserPreferenceArray[{}]";
        }
        StringBuilder result = new StringBuilder(20 * ids.length);
        result.append("GenericUserPreferenceArray[userID:");
        result.append(id);
        result.append(",{");
        for (int i = 0; i < ids.length; i++) {
            if (i > 0) {
                result.append(',');
            }
            result.append(ids[i]);
            result.append('=');
            result.append(values[i]);
        }
        result.append("}]");
        return result.toString();
    }

    private final class UserRecommendPreferenceView implements UserRecommendPreference {

        private final int i;

        private UserRecommendPreferenceView(int i) {
            this.i = i;
        }

        public long getUserID() {
            return UserRecommendGenericUserPreferenceArray.this.getUserID(i);
        }

        public long getItemID() {
            return UserRecommendGenericUserPreferenceArray.this.getItemID(i);
        }

        public float getValue() {
            return values[i];
        }

        public void setValue(float value) {
            values[i] = value;
        }
    }
}
