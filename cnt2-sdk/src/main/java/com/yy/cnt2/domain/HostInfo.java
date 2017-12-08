package com.yy.cnt2.domain;

import com.google.common.collect.Maps;
import com.yy.cs.base.hostinfo.IpInfo;
import com.yy.cs.base.hostinfo.NetType;
import com.yy.cs.base.hostinfo.OriginHostInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;

/**
 *
 * Copy from {@link com.yy.cs.base.hostinfo.HostInfo} & {@link com.yy.cs.base.hostinfo.HostInfoHelper}
 *
 * @author xlg
 * @since 2017/7/12
 */
public class HostInfo {
    private static final Logger log = LoggerFactory.getLogger(HostInfo.class);

    private String areaId;
    private String cityId;
    private Map<NetType, IpInfo> ipList;
    private String priGroupId;



    private static HostInfo INSTANCE = new HostInfo();
    private static Properties properties;
    private static OriginHostInfo origInfo;

    private static final String DEFAULT_FILE_PATH = "/home/dspeak/yyms/hostinfo.ini";

    private HostInfo() {
    }

    static {
        init();
        if (null != origInfo) {
            INSTANCE.areaId = origInfo.getArea_id();
            INSTANCE.cityId = origInfo.getCity_id();
            INSTANCE.priGroupId = origInfo.getPri_group_id();
            INSTANCE.ipList = Collections.unmodifiableMap(parseIpInfos(origInfo.getIp_isp_list()));
        }
    }

    public static HostInfo get() {
        return INSTANCE;
    }

    private static void init() {
        File file = new File(DEFAULT_FILE_PATH);
        if (file.exists() && file.isFile() && file.canRead()) {
            try (BufferedReader in = new BufferedReader(new FileReader(file))) {
                properties = new Properties();
                properties.load(in);
                if (log.isDebugEnabled()) {
                    for (Object s : properties.keySet()) {
                        log.debug("property " + s + ":" + properties.getProperty((String) s));
                    }
                }
                parseProperties();
            } catch (Exception e) {

                log.error("read data info error:", e);
            }
        }
    }

    private static void parseProperties() {
        origInfo = new OriginHostInfo();
        origInfo.setArea_id(properties.getProperty("area_id"));
        origInfo.setDnips(properties.getProperty("dnips"));
        origInfo.setVid(properties.getProperty("vid"));
        origInfo.setCity_id(properties.getProperty("city_id"));
        origInfo.setIp_isp_list(properties.getProperty("ip_isp_list"));
        origInfo.setGuid(properties.getProperty("guid"));
        origInfo.setFlock_id(properties.getProperty("flock_id"));
        origInfo.setServer_id(properties.getProperty("server_id"));
        origInfo.setVm_type(properties.getProperty("vm_type"));
        origInfo.setServer_level(properties.getProperty("server_level"));
        origInfo.setIdc_id(properties.getProperty("idc_id"));
        origInfo.setServiceName(properties.getProperty("serviceName"));
        origInfo.setRoom_id(properties.getProperty("room_id"));
        origInfo.setSysopResponsibleAdmin_dw(properties.getProperty("sysopResponsibleAdmin_dw"));
        origInfo.setResponsibleAdmin(properties.getProperty("responsibleAdmin"));
        origInfo.setStatus(properties.getProperty("status"));
        origInfo.setServer_type(properties.getProperty("server_type"));
        origInfo.setBuss_name(properties.getProperty("buss_name"));
        origInfo.setSysopResponsibleAdmin(properties.getProperty("sysopResponsibleAdmin"));
        origInfo.setResponsibleAdmin_dw(properties.getProperty("responsibleAdmin_dw"));
        origInfo.setRoom(properties.getProperty("room"));
        origInfo.setPri_group_id(properties.getProperty("pri_group_id"));
        origInfo.setDept(properties.getProperty("dept"));
        origInfo.setGroup_id(properties.getProperty("group_id"));
    }

    private static Map<NetType, IpInfo> parseIpInfos(String ipInfos) {
        Map<NetType, IpInfo> map = Maps.newHashMap();
        if (null == ipInfos) {
            return map;
        }
        String[] ips = ipInfos.split(",");
        for (String ip : ips) {
            String[] info = ip.split(":");
            NetType nt = null;
            try {
                nt = NetType.valueOf(info[1]);
            } catch (Exception e) {
            }
            if (null != nt) {
                map.put(nt, new IpInfo(info[0], nt));
            }
        }
        return map;
    }

    public String getAreaId() {
        return areaId;
    }

    public String getCityId() {
        return cityId;
    }

    public Map<NetType, IpInfo> getIpList() {
        return ipList;
    }

    public String getPriGroupId() {
        return priGroupId;
    }
}
