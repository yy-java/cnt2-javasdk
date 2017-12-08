package com.yy.cnt2.util;

public class IpInfo {

    private String ip;
    private NetType type;

    public IpInfo(String ip, NetType type) {
        this.ip = ip;
        this.type = type;
    }

    public String getIp() {
        return ip;
    }

    public NetType getType() {
        return type;
    }

    @Override
    public String toString() {
        return "IpInfo [ip=" + ip + ", type=" + type + "]";
    }

}
