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
package com.qlangtech.tis.indexbuilder.bean;

import com.aliyun.odps.PartitionSpec;
import com.aliyun.odps.TableSchema;
import com.aliyun.odps.data.RecordWriter;
import com.aliyun.odps.tunnel.TableTunnel;
import com.aliyun.odps.tunnel.TableTunnel.UploadSession;

/* *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class OdpsTunnelContext {

    private TableTunnel tunnel;

    private PartitionSpec partitionSpec;

    private UploadSession uploadSession;

    private TableSchema schema;

    private RecordWriter recordWriter;

    public TableTunnel getTunnel() {
        return tunnel;
    }

    public void setTunnel(TableTunnel tunnel) {
        this.tunnel = tunnel;
    }

    public PartitionSpec getPartitionSpec() {
        return partitionSpec;
    }

    public void setPartitionSpec(PartitionSpec partitionSpec) {
        this.partitionSpec = partitionSpec;
    }

    public UploadSession getUploadSession() {
        return uploadSession;
    }

    public void setUploadSession(UploadSession uploadSession) {
        this.uploadSession = uploadSession;
    }

    public TableSchema getSchema() {
        return schema;
    }

    public void setSchema(TableSchema schema) {
        this.schema = schema;
    }

    public RecordWriter getRecordWriter() {
        return recordWriter;
    }

    public void setRecordWriter(RecordWriter recordWriter) {
        this.recordWriter = recordWriter;
    }
}
