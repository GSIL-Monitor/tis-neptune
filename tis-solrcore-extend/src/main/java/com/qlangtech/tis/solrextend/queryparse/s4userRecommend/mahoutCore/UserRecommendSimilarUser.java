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

import com.google.common.primitives.Longs;

/*
 Simply encapsulates a user and a similarity value. 
 *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public final class UserRecommendSimilarUser implements Comparable<UserRecommendSimilarUser> {

    private final long userID;

    private final double similarity;

    public UserRecommendSimilarUser(long userID, double similarity) {
        this.userID = userID;
        this.similarity = similarity;
    }

    public long getUserID() {
        return userID;
    }

    public double getSimilarity() {
        return similarity;
    }

    @Override
    public int hashCode() {
        return (int) userID ^ hashDouble(similarity);
    }

    /**
     * @return what {@link Double#hashCode()} would return for the same value
     */
    public static int hashDouble(double value) {
        return Longs.hashCode(Double.doubleToLongBits(value));
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof UserRecommendSimilarUser)) {
            return false;
        }
        UserRecommendSimilarUser other = (UserRecommendSimilarUser) o;
        return userID == other.getUserID() && similarity == other.getSimilarity();
    }

    @Override
    public String toString() {
        return "SimilarUser[user:" + userID + ", similarity:" + similarity + ']';
    }

    /**
     * Defines an ordering from most similar to least similar.
     */
    public int compareTo(UserRecommendSimilarUser other) {
        double otherSimilarity = other.getSimilarity();
        if (similarity > otherSimilarity) {
            return -1;
        }
        if (similarity < otherSimilarity) {
            return 1;
        }
        long otherUserID = other.getUserID();
        if (userID < otherUserID) {
            return -1;
        }
        if (userID > otherUserID) {
            return 1;
        }
        return 0;
    }
}
