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
package com.qlangtech.tis.coredefine.biz.impl;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Map;
import org.apache.hadoop.io.MapWritable;

/* *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class ExtCoreNodeInfo {
    // private final CoreNodeInfo node;
    // 
    // private final boolean master;
    // private final boolean realtime;
    // 
    // public ExtCoreNodeInfo(CoreNodeInfo node, boolean master, boolean realtime) {
    // super();
    // this.node = node;
    // this.master = master;
    // this.realtime = realtime;
    // }
    // 
    // public boolean getCanDelete() {
    // return !(master );//&& this.realtime);
    // }
    // 
    // public int compareTo(CoreNodeID o) {
    // return node.compareTo(o);
    // }
    // 
    // public boolean equals(Object o) {
    // return node.equals(o);
    // }
    // 
    // public AdminStates getAdminState() {
    // return node.getAdminState();
    // }
    // 
    // public String getArch() {
    // return node.getArch();
    // }
    // 
    // public int getAvailableProcessors() {
    // return node.getAvailableProcessors();
    // }
    // 
    // public long getCapacity() {
    // return node.getCapacity();
    // }
    // 
    // public long getCommittedVirtualMemorySize() {
    // return node.getCommittedVirtualMemorySize();
    // }
    // 
    // public MapWritable getCoreGroup() {
    // return node.getCoreGroup();
    // }
    // 
    // public int getCoreNum() {
    // return node.getCoreNum();
    // }
    // 
    // public long getCoreUsed() {
    // return node.getCoreUsed();
    // }
    // 
    // public String getHost() {
    // return node.getHost();
    // }
    // 
    // public String getHostName() {
    // return node.getHostName();
    // }
    // 
    // public int getInfoPort() {
    // return node.getInfoPort();
    // }
    // 
    // public int getIpcPort() {
    // return node.getIpcPort();
    // }
    // 
    // public long getJvmFreeMem() {
    // return node.getJvmFreeMem();
    // }
    // 
    // public long getJvmMaxMem() {
    // return node.getJvmMaxMem();
    // }
    // 
    // public String getJvmName() {
    // return node.getJvmName();
    // }
    // 
    // public long getJvmTotalMem() {
    // return node.getJvmTotalMem();
    // }
    // 
    // public long getJvmUsedMem() {
    // return node.getJvmUsedMem();
    // }
    // 
    // public String getJvmVersion() {
    // return node.getJvmVersion();
    // }
    // 
    // public long getLastUpdate() {
    // return node.getLastUpdate();
    // }
    // 
    // public String getLuceneImplVersion() {
    // return node.getLuceneImplVersion();
    // }
    // 
    // public String getLuceneSpecVersion() {
    // return node.getLuceneSpecVersion();
    // }
    // 
    // public int getMaxFileDescriptorCount() {
    // return node.getMaxFileDescriptorCount();
    // }
    // 
    // public String getName() {
    // return node.getName();
    // }
    // 
    // public String getNodeID() {
    // return node.getNodeID();
    // }
    // 
    // public long getNumDocs() {
    // return node.getNumDocs();
    // }
    // 
    // public int getOpenFileDescriptorCount() {
    // return node.getOpenFileDescriptorCount();
    // }
    // 
    // public String getOsName() {
    // return node.getOsName();
    // }
    // 
    // public String getOsVersion() {
    // return node.getOsVersion();
    // }
    // 
    // public double getPercentJvmUsedMem() {
    // return node.getPercentJvmUsedMem();
    // }
    // 
    // public int getPort() {
    // return node.getPort();
    // }
    // 
    // public long getProcessCpuTime() {
    // return node.getProcessCpuTime();
    // }
    // 
    // public int getProcessors() {
    // return node.getProcessors();
    // }
    // 
    // public long getRemaining() {
    // return node.getRemaining();
    // }
    // 
    // public int getSolrCoreCount() {
    // return node.getSolrCoreCount();
    // }
    // 
    // public Map<String, SolrCoreInfo> getSolrCoreInfo() {
    // return node.getSolrCoreInfo();
    // }
    // 
    // public String getSolrImplVersion() {
    // return node.getSolrImplVersion();
    // }
    // 
    // public String getSolrSpecVersion() {
    // return node.getSolrSpecVersion();
    // }
    // 
    // public double getSystemLoadAverage() {
    // return node.getSystemLoadAverage();
    // }
    // 
    // public long getTotalPhysicalMemorySize() {
    // return node.getTotalPhysicalMemorySize();
    // }
    // 
    // public long getTotalSwapSpaceSize() {
    // return node.getTotalSwapSpaceSize();
    // }
    // 
    // public int getTransCount() {
    // return node.getTransCount();
    // }
    // 
    // public int hashCode() {
    // return node.hashCode();
    // }
    // 
    // public boolean isDecommissioned() {
    // return node.isDecommissioned();
    // }
    // 
    // public boolean isDecommissionInProgress() {
    // return node.isDecommissionInProgress();
    // }
    // 
    // public void readFields(DataInput arg0) throws IOException {
    // node.readFields(arg0);
    // }
    // 
    // public void setArch(String arch) {
    // node.setArch(arch);
    // }
    // 
    // public void setAvailableProcessors(int availableProcessors) {
    // node.setAvailableProcessors(availableProcessors);
    // }
    // 
    // public void setCapacity(long capacity) {
    // node.setCapacity(capacity);
    // }
    // 
    // public void setCommittedVirtualMemorySize(long committedVirtualMemorySize) {
    // node.setCommittedVirtualMemorySize(committedVirtualMemorySize);
    // }
    // 
    // public void setCoreNum(int coreNum) {
    // node.setCoreNum(coreNum);
    // }
    // 
    // public void setCoreUsed(long coreUsed) {
    // node.setCoreUsed(coreUsed);
    // }
    // 
    // public void setDecommissioned() {
    // node.setDecommissioned();
    // }
    // 
    // public void setHostName(String hostName) {
    // node.setHostName(hostName);
    // }
    // 
    // public void setJvmFreeMem(long jvmFreeMem) {
    // node.setJvmFreeMem(jvmFreeMem);
    // }
    // 
    // public void setJvmMaxMem(long jvmMaxMem) {
    // node.setJvmMaxMem(jvmMaxMem);
    // }
    // 
    // public void setJvmName(String jvmName) {
    // node.setJvmName(jvmName);
    // }
    // 
    // public void setJvmTotalMem(long jvmTotalMem) {
    // node.setJvmTotalMem(jvmTotalMem);
    // }
    // 
    // public void setJvmUsedMem(long jvmUsedMem) {
    // node.setJvmUsedMem(jvmUsedMem);
    // }
    // 
    // public void setJvmVersion(String jvmVersion) {
    // node.setJvmVersion(jvmVersion);
    // }
    // 
    // public void setLastUpdate(long lastUpdate) {
    // node.setLastUpdate(lastUpdate);
    // }
    // 
    // public void setLuceneImplVersion(String luceneImplVersion) {
    // node.setLuceneImplVersion(luceneImplVersion);
    // }
    // 
    // public void setLuceneSpecVersion(String luceneSpecVersion) {
    // node.setLuceneSpecVersion(luceneSpecVersion);
    // }
    // 
    // public void setMaxFileDescriptorCount(int maxFileDescriptorCount) {
    // node.setMaxFileDescriptorCount(maxFileDescriptorCount);
    // }
    // 
    // public void setNodeID(String nodeId) {
    // node.setNodeID(nodeId);
    // }
    // 
    // public void setNumDocs(long numDocs) {
    // node.setNumDocs(numDocs);
    // }
    // 
    // public void setOpenFileDescriptorCount(int openFileDescriptorCount) {
    // node.setOpenFileDescriptorCount(openFileDescriptorCount);
    // }
    // 
    // public void setOsName(String osName) {
    // node.setOsName(osName);
    // }
    // 
    // public void setOsVersion(String osVersion) {
    // node.setOsVersion(osVersion);
    // }
    // 
    // public void setPercentJvmUsedMem(double percentJvmUsedMem) {
    // node.setPercentJvmUsedMem(percentJvmUsedMem);
    // }
    // 
    // public void setProcessCpuTime(long processCpuTime) {
    // node.setProcessCpuTime(processCpuTime);
    // }
    // 
    // public void setProcessors(int processors) {
    // node.setProcessors(processors);
    // }
    // 
    // public void setRemaining(long remaining) {
    // node.setRemaining(remaining);
    // }
    // 
    // public void setSolrImplVersion(String solrImplVersion) {
    // node.setSolrImplVersion(solrImplVersion);
    // }
    // 
    // public void setSolrSpecVersion(String solrSpecVersion) {
    // node.setSolrSpecVersion(solrSpecVersion);
    // }
    // 
    // public void setSystemLoadAverage(double systemLoadAverage) {
    // node.setSystemLoadAverage(systemLoadAverage);
    // }
    // 
    // public void setTotalPhysicalMemorySize(long totalPhysicalMemorySize) {
    // node.setTotalPhysicalMemorySize(totalPhysicalMemorySize);
    // }
    // 
    // public void setTotalSwapSpaceSize(long totalSwapSpaceSize) {
    // node.setTotalSwapSpaceSize(totalSwapSpaceSize);
    // }
    // 
    // public void setTransCount(int transCount) {
    // node.setTransCount(transCount);
    // }
    // 
    // public void startDecommission() {
    // node.startDecommission();
    // }
    // 
    // public void stopDecommission() {
    // node.stopDecommission();
    // }
    // 
    // public String toString() {
    // return node.toString();
    // }
    // 
    // public void updateRegInfo(CoreNodeID nodeReg) {
    // node.updateRegInfo(nodeReg);
    // }
    // 
    // public void write(DataOutput arg0) throws IOException {
    // node.write(arg0);
    // }
}
