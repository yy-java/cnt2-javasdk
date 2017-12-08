package com.yy.cnt2.domain;

import java.util.List;

/**
 * ConfigPublishInfo
 *
 * @author xlg
 * @since 2017/7/7
 */
public class ConfigPublishInfo {
    private String publishId;
    private String key;
    private long version;
    private List<String> publishNodes;

    public ConfigPublishInfo() {
    }

    public ConfigPublishInfo(String publishId, String key, long version, List<String> publishNodes) {

        this.publishId = publishId;
        this.key = key;
        this.version = version;
        this.publishNodes = publishNodes;
    }

    public String getPublishId() {
        return publishId;
    }

    public void setPublishId(String publishId) {
        this.publishId = publishId;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public List<String> getPublishNodes() {
        return publishNodes;
    }

    public void setPublishNodes(List<String> publishNodes) {
        this.publishNodes = publishNodes;
    }

    @Override
    public String toString() {
        return "ConfigPublishInfo{" + "publishId='" + publishId + '\'' + ", key='" + key + '\'' + ", version=" + version
                + ", publishNodes=" + publishNodes + '}';
    }
}
