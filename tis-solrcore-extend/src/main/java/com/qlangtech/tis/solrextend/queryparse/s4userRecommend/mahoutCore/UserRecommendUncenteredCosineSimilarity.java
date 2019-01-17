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

import com.google.common.base.Preconditions;

/* *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public final class UserRecommendUncenteredCosineSimilarity {

    private final boolean weighted;

    private final boolean centerData;

    private int cachedNumItems;

    private int cachedNumUsers;

    private final UserRecommendFastByIDMap<UserRecommendPreferenceArray> dataModel;

    public UserRecommendUncenteredCosineSimilarity(UserRecommendFastByIDMap<UserRecommendPreferenceArray> dataModel) throws Exception {
        this(dataModel, false);
    }

    public UserRecommendUncenteredCosineSimilarity(UserRecommendFastByIDMap<UserRecommendPreferenceArray> dataModel, boolean weighted) throws Exception {
        Preconditions.checkArgument(dataModel != null, "dataModel is null");
        this.dataModel = dataModel;
        this.weighted = weighted;
        this.centerData = false;
    // this.cachedNumItems = dataModel.getNumItems();
    // this.cachedNumUsers = dataModel.getNumUsers();
    // Preconditions.checkArgument(dataModel.hasPreferenceValues(), "DataModel doesn't have preference values");
    }

    final double normalizeWeightResult(double result, int count, int num) {
        double normalizedResult = result;
        if (weighted) {
            double scaleFactor = 1.0 - (double) count / (double) (num + 1);
            if (normalizedResult < 0.0) {
                normalizedResult = -1.0 + scaleFactor * (1.0 + normalizedResult);
            } else {
                normalizedResult = 1.0 - scaleFactor * (1.0 - normalizedResult);
            }
        }
        // Make sure the result is not accidentally a little outside [-1.0, 1.0] due to rounding:
        if (normalizedResult < -1.0) {
            normalizedResult = -1.0;
        } else if (normalizedResult > 1.0) {
            normalizedResult = 1.0;
        }
        return normalizedResult;
    }

    public double userSimilarity(long index1, long index2) throws Exception {
        UserRecommendFastByIDMap<UserRecommendPreferenceArray> dataModel = this.dataModel;
        UserRecommendPreferenceArray xPrefs = dataModel.get(index1);
        UserRecommendPreferenceArray yPrefs = dataModel.get(index2);
        int xLength = xPrefs.length();
        int yLength = yPrefs.length();
        if (xLength == 0 || yLength == 0) {
            return Double.NaN;
        }
        long xIndex = xPrefs.getItemID(0);
        long yIndex = yPrefs.getItemID(0);
        int xPrefIndex = 0;
        int yPrefIndex = 0;
        double sumX = 0.0;
        double sumX2 = 0.0;
        double sumY = 0.0;
        double sumY2 = 0.0;
        double sumXY = 0.0;
        double sumXYdiff2 = 0.0;
        int count = 0;
        boolean hasInferrer = false;
        while (true) {
            int compare = xIndex < yIndex ? -1 : xIndex > yIndex ? 1 : 0;
            if (hasInferrer || compare == 0) {
                double x;
                double y;
                // if (xIndex == yIndex) {
                // Both users expressed a preference for the item
                x = xPrefs.getValue(xPrefIndex);
                y = yPrefs.getValue(yPrefIndex);
                // }
                // else {
                // // Only one user expressed a preference, but infer the other one's preference and tally
                // // as if the other user expressed that preference
                // if (compare < 0) {
                // // X has a value; infer Y's
                // x = xPrefs.getValue(xPrefIndex);
                // y = inferrer.inferPreference(userID2, xIndex);
                // } else {
                // // compare > 0
                // // Y has a value; infer X's
                // x = inferrer.inferPreference(userID1, yIndex);
                // y = yPrefs.getValue(yPrefIndex);
                // }
                // }
                sumXY += x * y;
                sumX += x;
                sumX2 += x * x;
                sumY += y;
                sumY2 += y * y;
                double diff = x - y;
                sumXYdiff2 += diff * diff;
                count++;
            }
            if (compare <= 0) {
                if (++xPrefIndex >= xLength) {
                    if (hasInferrer) {
                        // Must count other Ys; pretend next X is far away
                        if (yIndex == Long.MAX_VALUE) {
                            // ... but stop if both are done!
                            break;
                        }
                        xIndex = Long.MAX_VALUE;
                    } else {
                        break;
                    }
                } else {
                    xIndex = xPrefs.getItemID(xPrefIndex);
                }
            }
            if (compare >= 0) {
                if (++yPrefIndex >= yLength) {
                    if (hasInferrer) {
                        // Must count other Xs; pretend next Y is far away
                        if (xIndex == Long.MAX_VALUE) {
                            // ... but stop if both are done!
                            break;
                        }
                        yIndex = Long.MAX_VALUE;
                    } else {
                        break;
                    }
                } else {
                    yIndex = yPrefs.getItemID(yPrefIndex);
                }
            }
        }
        // "Center" the data. If my math is correct, this'll do it.
        double result;
        result = computeResult(count, sumXY, sumX2, sumY2, sumXYdiff2);
        if (!Double.isNaN(result)) {
            result = normalizeWeightResult(result, count, cachedNumItems);
        }
        return result;
    }

    double computeResult(int n, double sumXY, double sumX2, double sumY2, double sumXYdiff2) {
        if (n == 0) {
            return Double.NaN;
        }
        double denominator = Math.sqrt(sumX2) * Math.sqrt(sumY2);
        if (denominator == 0.0) {
            // can't really say much similarity under this measure
            return Double.NaN;
        }
        return sumXY / denominator;
    }
}
