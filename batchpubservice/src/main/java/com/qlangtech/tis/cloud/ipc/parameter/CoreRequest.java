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
package com.qlangtech.tis.cloud.ipc.parameter;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/* *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class CoreRequest {

    private String serviceName;

    // private int replication;
    // private int coreNums;
    private int coreNumsPerCoreNode;

    /**
     * @return the coreNumsPerCoreNode
     */
    public int getCoreNumsPerCoreNode() {
        return coreNumsPerCoreNode;
    }

    // 基准版本
    private String luceneVersion = "3.4.0";

    public String getLuceneVersion() {
        return luceneVersion;
    }

    public void setLuceneVersion(String luceneVersion) {
        this.luceneVersion = luceneVersion;
    }

    /**
     * @param coreNumsPerCoreNode the coreNumsPerCoreNode to set
     */
    public void setCoreNumsPerCoreNode(int coreNumsPerCoreNode) {
        this.coreNumsPerCoreNode = coreNumsPerCoreNode;
    }

    public void addNodeIps(String groupNums, String ip) {
    }

    private int virtualGroup;

    // 配置文件的名称
    private String[] configFiles;

    // 配置文件保存的副本数目
    private int configFileBackUpNum;

    // 后台URL
    private String terminatorUrl = "";

    // 环境 日常、预发、线上
    private int runEnv;

    // /**
    // * @return the runEnv
    // */
    public int getRunEnv() {
        return runEnv;
    }

    /**
     * @param runEnv the runEnv to set
     */
    public void setRunEnv(int runEnv) {
        this.runEnv = runEnv;
    }

    private int dumpType;

    // 需要发布机器的IP列表
    private String[] includeIps;

    // 排除在外的机器IP列表
    private String[] excudeIps;

    private boolean isAddInc;

    private boolean isAddExc;

    /**
     * @return the dumpType
     */
    public int getDumpType() {
        return dumpType;
    }

    /**
     * @param dumpType the dumpType to set
     */
    public void setDumpType(int dumpType) {
        this.dumpType = dumpType;
    }

    /**
     * @return the isAddInc
     */
    public boolean isAddInc() {
        return isAddInc;
    }

    /**
     * @param isAddInc the isAddInc to set
     */
    public void setAddInc(boolean isAddInc) {
        this.isAddInc = isAddInc;
    }

    /**
     * @return the isAddExc
     */
    public boolean isAddExc() {
        return isAddExc;
    }

    /**
     * @param isAddExc the isAddExc to set
     */
    public void setAddExc(boolean isAddExc) {
        this.isAddExc = isAddExc;
    }

    /**
     * @return the includeIps
     */
    public String[] getIncludeIps() {
        return includeIps;
    }

    /**
     * @param includeIps the includeIps to set
     */
    @Deprecated
    public void setIncludeIps(String[] includeIps) {
        this.includeIps = includeIps;
    }

    /**
     * @return the excudeIps
     */
    public String[] getExcudeIps() {
        return excudeIps;
    }

    /**
     * @param excudeIps the excudeIps to set
     */
    @Deprecated
    public void setExcudeIps(String[] excudeIps) {
        this.excudeIps = excudeIps;
    }

    /**
     * @return the virtualGroup
     */
    public int getVirtualGroup() {
        return virtualGroup;
    }

    /**
     * @param virtualGroup the virtualGroup to set
     */
    public void setVirtualGroup(int virtualGroup) {
        this.virtualGroup = virtualGroup;
    }

    /**
     * @return the configFiles
     */
    public String[] getConfigFiles() {
        return configFiles;
    }

    /**
     * @param configFiles the configFiles to set
     */
    public void setConfigFiles(String[] configFiles) {
        this.configFiles = configFiles;
    }

    /**
     * @return the configFileBackUpNum
     */
    public int getConfigFileBackUpNum() {
        return configFileBackUpNum;
    }

    /**
     * @param configFileBackUpNum the configFileBackUpNum to set
     */
    public void setConfigFileBackUpNum(int configFileBackUpNum) {
        this.configFileBackUpNum = configFileBackUpNum;
    }

    /**
     * @return the terminatorUrl
     */
    public String getTerminatorUrl() {
        return terminatorUrl;
    }

    /**
     * @param terminatorUrl the terminatorUrl to set
     */
    public void setTerminatorUrl(String terminatorUrl) {
        this.terminatorUrl = terminatorUrl;
    }

    /**
     * @return the serviceName
     */
    public String getServiceName() {
        return serviceName;
    }

    /**
     * @param serviceName the serviceName to set
     */
    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    private boolean _isMonopolized = Boolean.FALSE;

    public boolean isMonopolized() {
        return _isMonopolized;
    }

    public void setMonopolized(boolean monopoly) {
        this._isMonopolized = monopoly;
    }
    // /*
    // * (non-Javadoc)
    // *
    // * @see org.apache.hadoop.io.Writable#readFields(java.io.DataInput)
    // */
    // @Override
    // public void readFields(DataInput in) throws IOException {
    // this.serviceName = Text.readString(in);
    // this.luceneVersion = Text.readString(in);
    // this.virtualGroup = in.readInt();
    // int configSize = in.readInt();
    // String[] configs = new String[configSize];
    // for (int i = 0; i < configSize; i++) {
    // configs[i] = Text.readString(in);
    // }
    // this.configFiles = configs;
    // this.configFileBackUpNum = in.readInt();
    // this.terminatorUrl = Text.readString(in);
    // this.dumpType = in.readInt();
    // 
    // int includeSize = in.readInt();
    // if (includeSize > 0) {
    // String[] include = new String[includeSize];
    // for (int i = 0; i < includeSize; i++) {
    // include[i] = Text.readString(in);
    // }
    // this.includeIps = include;
    // }
    // int excudeSize = in.readInt();
    // if (excudeSize > 0) {
    // String[] excude = new String[excudeSize];
    // for (int i = 0; i < excudeSize; i++) {
    // excude[i] = Text.readString(in);
    // }
    // this.excudeIps = excude;
    // }
    // this.isAddInc = in.readBoolean();
    // this.isAddExc = in.readBoolean();
    // this.coreNumsPerCoreNode = in.readInt();
    // int env = in.readInt();
    // 
    // this.runEnv = env & 15;
    // mapWritable.readFields(in);
    // int flag = env >> 7;
    // if (flag == 1) {
    // try {
    // this._isMonopolized = in.readBoolean();
    // } catch (Exception e) {
    // }
    // }
    // 
    // pullProcessInfosMapWritable.readFields( in );
    // if(pullProcessInfosMapWritable.size()>0)
    // {
    // for ( Entry<Writable, Writable> kv : pullProcessInfosMapWritable.entrySet() )
    // {
    // pullProcessInfos.put( kv.getKey().toString() , kv.getValue().toString() );
    // }
    // }
    // 
    // }
    // /*
    // * (non-Javadoc)
    // *
    // * @see org.apache.hadoop.io.Writable#write(java.io.DataOutput)
    // */
    // @Override
    // public void write(DataOutput out) throws IOException {
    // Text.writeString(out, serviceName);
    // Text.writeString(out, luceneVersion);
    // out.writeInt(virtualGroup);
    // if (configFiles != null) {
    // out.writeInt(configFiles.length);
    // for (int i = 0; i < configFiles.length; i++) {
    // Text.writeString(out, configFiles[i]);
    // }
    // } else {
    // out.writeInt(0);
    // }
    // out.writeInt(configFileBackUpNum);
    // Text.writeString(out, terminatorUrl);
    // out.writeInt(dumpType);
    // if (includeIps != null) {
    // out.writeInt(includeIps.length);
    // for (int i = 0; i < includeIps.length; i++) {
    // Text.writeString(out, includeIps[i]);
    // }
    // } else {
    // out.writeInt(0);
    // }
    // if (excudeIps != null) {
    // out.writeInt(excudeIps.length);
    // for (int i = 0; i < excudeIps.length; i++) {
    // Text.writeString(out, excudeIps[i]);
    // }
    // } else {
    // out.writeInt(0);
    // }
    // out.writeBoolean(isAddInc);
    // out.writeBoolean(isAddExc);
    // out.writeInt(this.coreNumsPerCoreNode);
    // for (Entry<Text, List<Text>> entry : nodesIpsMap.entrySet()) {
    // Text groupNums = entry.getKey();
    // List<Text> ipsList = entry.getValue();
    // Text[] ipsTexts = ipsList.toArray(new Text[ipsList.size()]);
    // ArrayTextWritable listWritable = new ArrayTextWritable(ipsTexts);
    // mapWritable.put(groupNums, listWritable);
    // }
    // out.writeInt((1 << 7) + this.runEnv);
    // mapWritable.write(out);
    // 
    // out.writeBoolean(_isMonopolized);
    // // out.writeInt(runEnv);
    // pullProcessInfosMapWritable.write(out);
    // }
    // 
    // private Map<String,String> pullProcessInfos = new HashMap<String,String>();
    // private MapWritable pullProcessInfosMapWritable = new MapWritable();
    // 
    // 
    // public void addProperty(String key, String value)
    // {
    // pullProcessInfosMapWritable.put( new Text(key) , new Text(value) );
    // }
    // public String getProperty(String key)
    // {
    // return pullProcessInfos.get( key );
    // }
    // 
    // public Set<String> getPropertyNames()
    // {
    // return pullProcessInfos.keySet();
    // }
}
