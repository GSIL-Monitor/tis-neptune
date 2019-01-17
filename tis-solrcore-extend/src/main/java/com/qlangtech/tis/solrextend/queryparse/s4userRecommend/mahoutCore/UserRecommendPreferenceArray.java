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

import java.io.Serializable;

/* *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public interface UserRecommendPreferenceArray extends Cloneable, Serializable, Iterable<UserRecommendPreference> {

    /**
     * @return size of length of the "array"
     */
    int length();

    UserRecommendPreference get(int i);

    void set(int i, UserRecommendPreference pref);

    /**
     * @param i
     *          index
     * @return user ID from preference at i
     */
    long getUserID(int i);

    /**
     * Sets user ID for preference at i.
     *
     * @param i
     *          index
     * @param userID
     *          new user ID
     */
    void setUserID(int i, long userID);

    /**
     * @param i
     *          index
     * @return item ID from preference at i
     */
    long getItemID(int i);

    /**
     * Sets item ID for preference at i.
     *
     * @param i
     *          index
     * @param itemID
     *          new item ID
     */
    void setItemID(int i, long itemID);

    /**
     * @return all user or item IDs
     */
    long[] getIDs();

    /**
     * @param i
     *          index
     * @return preference value from preference at i
     */
    float getValue(int i);

    /**
     * Sets preference value for preference at i.
     *
     * @param i
     *          index
     * @param value
     *          new preference value
     */
    void setValue(int i, float value);

    /**
     * @return independent copy of this object
     */
    UserRecommendPreferenceArray clone();

    /**
     * Sorts underlying array by user ID, ascending.
     */
    void sortByUser();

    /**
     * Sorts underlying array by item ID, ascending.
     */
    void sortByItem();

    /**
     * Sorts underlying array by preference value, ascending.
     */
    void sortByValue();

    /**
     * Sorts underlying array by preference value, descending.
     */
    void sortByValueReversed();

    /**
     * @param userID
     *          user ID
     * @return true if array contains a preference with given user ID
     */
    boolean hasPrefWithUserID(long userID);

    /**
     * @param itemID
     *          item ID
     * @return true if array contains a preference with given item ID
     */
    boolean hasPrefWithItemID(long itemID);
}
