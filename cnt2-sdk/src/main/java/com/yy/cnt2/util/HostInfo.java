package com.yy.cnt2.util;

import java.util.HashMap;
import java.util.Map;

public class HostInfo {
    private String areaId;
    private String cityId;
    private Map<NetType, IpInfo> ipList;

    public String getAreaId() {
        return areaId;
    }

    public String getCityId() {
        return cityId;
    }

    public Map<NetType, IpInfo> getIpList() {
        return ipList;
    }

    public void setAreaId(String areaId) {
        this.areaId = areaId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    public void setIpList(Map<NetType, IpInfo> ipList) {
        this.ipList = ipList;
    }

    public static HostInfo parse(OriginHostInfo orig) {
        if (null == orig) {
            return null;
        }
        HostInfo hi = new HostInfo();
        hi.setAreaId(orig.getArea_id());
        hi.setCityId(orig.getCity_id());
        String ipInfos = orig.getIp_isp_list();
        try {
            hi.setIpList(new HashMap<NetType, IpInfo>());
            String[] ips = ipInfos.split(",");
            for (String ip : ips) {
                String[] info = ip.split(":");
                NetType nt = null;
                try {
                    nt = NetType.valueOf(info[1]);
                } catch (Exception e) {
                }
                if (null != nt) {
                    hi.getIpList().put(nt, new IpInfo(info[0], nt));
                }
            }
        } catch (Exception e) {
            return null;
        }
        return hi;
    }

    @Override
    public String toString() {
        return "HostInfo [areaId=" + areaId + ", cityId=" + cityId + ", ipList=" + ipList + "]";
    }

}
