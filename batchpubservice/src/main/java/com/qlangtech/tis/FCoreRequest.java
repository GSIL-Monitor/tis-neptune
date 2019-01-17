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
package com.qlangtech.tis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import com.qlangtech.tis.cloud.ipc.parameter.CoreRequest;

/* *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class FCoreRequest {

    protected final CoreRequest request;

    private boolean valid = false;

    protected final Collection<String> ips = new ArrayList<String>(0);

    // 已经分配到的组数
    private final int assigndGroup;

    // 标识每个组内副本个数
    private final short[] replicCount;

    public void addNodeIps(int group, String ip) {
        ips.add(ip);
        request.addNodeIps(String.valueOf(group), ip);
        replicCount[group]++;
    }

    public short[] getReplicCount() {
        return Arrays.copyOfRange(replicCount, assigndGroup, replicCount.length);
    }

    public String[] getIps() {
        return ips.toArray(new String[ips.size()]);
    }

    public FCoreRequest(CoreRequest request, int groupCount) {
        this(request, groupCount, 0);
    }

    /**
     * @param request
     * @param groupCount
     *            现在应用的总组数
     */
    public FCoreRequest(CoreRequest request, int groupCount, int assigndGroup) {
        super();
        this.request = request;
        if (groupCount < 1) {
            throw new IllegalArgumentException("groupCount can not be null");
        }
        this.replicCount = new short[groupCount];
        this.assigndGroup = assigndGroup;
    }

    /**
     * @param valid
     *            the valid to set
     */
    public void setValid(boolean valid) {
        this.valid = valid;
    }

    /**
     * @return the valid
     */
    public boolean isValid() {
        return valid;
    }

    public CoreRequest getRequest() {
        return request;
    }
}
