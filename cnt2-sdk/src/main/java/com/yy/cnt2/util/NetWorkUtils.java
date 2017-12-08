package com.yy.cnt2.util;


import org.apache.commons.lang3.StringUtils;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Map;

/**
 * Created by luozhixin on 14/12/9.
 */
public class NetWorkUtils {

    private static final String ETHO0 = "eth0";

    public static boolean containsIp(String data, String ip) {
        if (StringUtils.isBlank(data) || StringUtils.isBlank(ip)) {
            return false;
        }
        String[] ips = data.split("\n");
        for (String i : ips) {
            if (ip.equals(i)) {
                return true;
            }
        }
        return false;
    }

    public static String getAppServerIp() {
        HostInfo hostInfo = HostInfoHelper.getHostInfo();
        if (null != hostInfo) {
            Map<NetType, IpInfo> nets = hostInfo.getIpList();
            for (NetType type : NetType.values()) {
                if (nets.containsKey(type)) {
                    return nets.get(type).getIp();
                }
            }
        }
        return getLocalIP();
    }

    public static String getAppServerIp2() {
        NetworkInterface ni = null;
        try {
            ni = NetworkInterface.getByName(ETHO0);
        } catch (SocketException e) {
            return "";
        }
        String ip = "";
        Enumeration<InetAddress> ias = ni.getInetAddresses();
        for (; ias.hasMoreElements(); ) {
            InetAddress ia = ias.nextElement();
            if (ia instanceof InetAddress && ia instanceof Inet4Address) {
                ip = ia.getHostAddress();
            }
        }
        return ip;
    }

    public static boolean isWindowsOS() {
        boolean isWindowsOS = false;
        String osName = System.getProperty("os.name");
        if (osName.toLowerCase().indexOf("windows") > -1) {
            isWindowsOS = true;
        }
        return isWindowsOS;
    }

    public static boolean isHostInfoFileExists() {
        boolean exists = false;
        HostInfo hostInfo = HostInfoHelper.getHostInfo();
        exists = hostInfo != null;
        return exists;
    }

    public static NetType toNetType(int code) {
        for (NetType netType : NetType.values()) {
            if (netType.getValue() == code) {
                return netType;
            }
        }
        return null;
    }

    /**
     * 获取本机ip地址，并自动区分Windows还是linux操作系统
     *
     * @return String
     */
    public static String getLocalIP() {
        String sIP = "";
        InetAddress ip = null;
        try {
            // 如果是Windows操作系统
            if (isWindowsOS()) {
                ip = InetAddress.getLocalHost();
            }
            // 如果是Linux操作系统
            else {
                boolean bFindIP = false;
                HostInfo hostInfo = HostInfoHelper.getHostInfo();
                if (null != hostInfo && null != hostInfo.getIpList() && hostInfo.getIpList().size() > 0) {
                    IpInfo ipInfo = null;
                    if (hostInfo.getIpList().containsKey(NetType.CTL)) {
                        ipInfo = hostInfo.getIpList().get(NetType.CTL);
                    } else if (hostInfo.getIpList().containsKey(NetType.CNC)) {
                        ipInfo = hostInfo.getIpList().get(NetType.CNC);
                    }
                    if (null != ipInfo) {
                        bFindIP = true;
                        return ipInfo.getIp();
                    }
                }

                Enumeration<NetworkInterface> netInterfaces = (Enumeration<NetworkInterface>) NetworkInterface
                        .getNetworkInterfaces();
                while (netInterfaces.hasMoreElements()) {
                    if (bFindIP) {
                        break;
                    }
                    NetworkInterface ni = (NetworkInterface) netInterfaces.nextElement();
                    // ----------特定情况，可以考虑用ni.getName判断
                    // 遍历所有ip
                    Enumeration<InetAddress> ips = ni.getInetAddresses();
                    while (ips.hasMoreElements()) {
                        ip = (InetAddress) ips.nextElement();
                        if (ip.isSiteLocalAddress() && !ip.isLoopbackAddress() // 127.开头的都是lookback地址
                                && ip.getHostAddress().indexOf(":") == -1) {
                            bFindIP = true;
                            break;
                        }
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (null != ip) {
            sIP = ip.getHostAddress();
        }
        return sIP;
    }
}
