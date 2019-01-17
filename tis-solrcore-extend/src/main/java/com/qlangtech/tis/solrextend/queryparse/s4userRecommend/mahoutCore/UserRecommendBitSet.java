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
import java.util.Arrays;

/*
 A simplified and streamlined version of {@link java.util.BitSet}. 
 *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
final class UserRecommendBitSet implements Serializable, Cloneable {

    private final long[] bits;

    UserRecommendBitSet(int numBits) {
        int numLongs = numBits >>> 6;
        if ((numBits & 0x3F) != 0) {
            numLongs++;
        }
        bits = new long[numLongs];
    }

    private UserRecommendBitSet(long[] bits) {
        this.bits = bits;
    }

    boolean get(int index) {
        // skipping range check for speed
        return (bits[index >>> 6] & 1L << (index & 0x3F)) != 0L;
    }

    void set(int index) {
        // skipping range check for speed
        bits[index >>> 6] |= 1L << (index & 0x3F);
    }

    void clear(int index) {
        // skipping range check for speed
        bits[index >>> 6] &= ~(1L << (index & 0x3F));
    }

    void clear() {
        int length = bits.length;
        for (int i = 0; i < length; i++) {
            bits[i] = 0L;
        }
    }

    @Override
    public UserRecommendBitSet clone() {
        return new UserRecommendBitSet(bits.clone());
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(bits);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof UserRecommendBitSet)) {
            return false;
        }
        UserRecommendBitSet other = (UserRecommendBitSet) o;
        return Arrays.equals(bits, other.bits);
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder(64 * bits.length);
        for (long l : bits) {
            for (int j = 0; j < 64; j++) {
                result.append((l & 1L << j) == 0 ? '0' : '1');
            }
            result.append(' ');
        }
        return result.toString();
    }
}
