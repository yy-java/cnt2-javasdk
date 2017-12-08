package com.yy.cnt2.domain;

import java.util.Map;

/**
 * GrpcServerRegisterInfo
 *
 * @author xlg
 * @since 2017/7/12
 */
public class GrpcServerRegisterInfo {
    private Map<String, String> serverIP;
    private int port;
    private int groupId;

    public Map<String, String> getServerIP() {
        return serverIP;
    }

    public void setServerIP(Map<String, String> serverIP) {
        this.serverIP = serverIP;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    @Override
    public String toString() {
        return "GrpcServerRegisterInfo{" + "serverIP=" + serverIP + ", port=" + port + ", groupId=" + groupId + '}';
    }
}
