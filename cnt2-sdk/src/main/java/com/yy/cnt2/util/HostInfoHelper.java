package com.yy.cnt2.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HostInfoHelper {

    private static final Logger log = LoggerFactory.getLogger(HostInfoHelper.class);
    private static final String DEFAULT_FILE_PATH = "/home/dspeak/yyms/hostinfo.ini";

    private static Properties properties;
    private static OriginHostInfo origInfo;

    static {
        readFileData();
    }

    private static void readFileData() {
        File file = new File(DEFAULT_FILE_PATH);
        if(file.exists() && file.isFile() && file.canRead()){
            BufferedReader in = null;
            try {
                in = new BufferedReader(new FileReader(file));
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
            } finally {
                if (null != in) {
                    try {
                        in.close();
                    } catch (Exception e) {

                    }
                }
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

    public static HostInfo getHostInfo() {
        return HostInfo.parse(origInfo);
    }

    public static void main(String[] args) {
        System.out.println(HostInfoHelper.getHostInfo());
    }

}
