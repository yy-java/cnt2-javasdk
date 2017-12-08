package com.yy.cnt2.domain;

/**
 * ConfigNode
 *
 * @author xlg
 * @since 2017/7/12
 */
public class ConfigNode {
    private String app;
    private String profile;
    private String key;
    private long version;
    private ConfigPublishInfo publishInfo;

    public ConfigNode() {
    }

    public ConfigNode(String app, String profile, String key, long version, ConfigPublishInfo publishInfo) {
        this.app = app;
        this.profile = profile;
        this.key = key;
        this.version = version;
        this.publishInfo = publishInfo;
    }

    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
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

    public ConfigPublishInfo getPublishInfo() {
        return publishInfo;
    }

    public void setPublishInfo(ConfigPublishInfo publishInfo) {
        this.publishInfo = publishInfo;
    }

    @Override
    public String toString() {
        return "ConfigNode{" + "app='" + app + '\'' + ", profile='" + profile + '\'' + ", key='" + key + '\''
                + ", version=" + version + ", publishInfo=" + publishInfo + '}';
    }
}
