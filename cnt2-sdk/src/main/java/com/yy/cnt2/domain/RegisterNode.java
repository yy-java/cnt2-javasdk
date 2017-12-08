package com.yy.cnt2.domain;

/**
 * Register Info
 *
 * @author xlg
 * @since 2017/7/8
 */
public class RegisterNode {
    private String nodeId;
    private String app;
    private String profile;
    private int pid;
    private String sip;
    private Long registerTime;

    public RegisterNode() {
    }

    public RegisterNode(String nodeId, String app, String profile, int pid,String sip, Long registerTime) {
        this.nodeId = nodeId;
        this.app = app;
        this.profile = profile;
        this.pid = pid;
        this.sip = sip;
        this.registerTime = registerTime;
    }

    public String getSip() {
		return sip;
	}

	public void setSip(String sip) {
		this.sip = sip;
	}

	public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
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

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public Long getRegisterTime() {
        return registerTime;
    }

    public void setRegisterTime(Long registerTime) {
        this.registerTime = registerTime;
    }

    @Override
    public String toString() {
        return "RegisterNode{" + "nodeId='" + nodeId + '\'' + ", app='" + app + '\'' + ", profile='" + profile + '\''
                + ", pid=" + pid + ", registerTime=" + registerTime + '}';
    }
}
