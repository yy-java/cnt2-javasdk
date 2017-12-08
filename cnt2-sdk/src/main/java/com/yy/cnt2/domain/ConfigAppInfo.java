package com.yy.cnt2.domain;

import static com.yy.cnt2.util.PathHelper.createProfilePath;

/**
 * ConfigAppInfo
 *
 * @author xlg
 * @since 2017/10/21
 */
public class ConfigAppInfo {
    private String appName;
    private String profile;
    private String nodeId;
    private String basePath;

    public ConfigAppInfo() {
    }

    public ConfigAppInfo(String appName, String profile, String nodeId) {
        this.appName = appName;
        this.profile = profile;
        this.nodeId = nodeId;
        this.basePath = createProfilePath(appName, profile);
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public String getBasePath() {
        return basePath;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    @Override
    public String toString() {
        return "ConfigAppInfo{" + "appName='" + appName + '\'' + ", profile='" + profile + '\'' + ", nodeId='" + nodeId
                + '\'' + ", basePath='" + basePath + '\'' + '}';
    }
}
