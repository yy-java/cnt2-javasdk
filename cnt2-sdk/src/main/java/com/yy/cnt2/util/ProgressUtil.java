package com.yy.cnt2.util;

import org.apache.commons.lang3.StringUtils;

import javax.management.*;
import java.lang.management.ManagementFactory;
import java.util.*;

import static com.yy.cnt2.util.PathHelper.PATH_SEPARATOR;

public class ProgressUtil {

    private static final MBeanServer MBEAN_SERVER = ManagementFactory.getPlatformMBeanServer();

    static MBeanServer getPlatformMBeanServer() {
        return MBEAN_SERVER;
    }

    /**
     * 获取进程信息
     */
    public static String getProgressNameInfo() {
        String t = ManagementFactory.getRuntimeMXBean().getName();

        if (StringUtils.isNotBlank(t)) {
            return t.replaceAll(PATH_SEPARATOR, "_");
        }
        return t;
    }

    /**
     * 获取潜龙部署相关信息
     */

    public static Map<String, String> getDragonInfo() {
        Map<String, String> ret = new HashMap<>();
        try {
            Properties properties = System.getProperties();
            for (Object keyObj : properties.keySet()) {
                String key = keyObj.toString();
                if (key.startsWith("dragon.")) {
                    String rkey = key.replace("dragon.", "");
                    ret.put(rkey, properties.getProperty(key));
                }
            }
        } catch (Exception e) {

        }
        return ret;
    }

    /**
     * 获取进程Id
     */
    public static int getPid() {
        String jvmName = ManagementFactory.getRuntimeMXBean().getName();
        return Integer.parseInt(jvmName.split("@")[0]);
    }

    /**
     * 根据name查询Mbean
     */
    public static Set<ObjectName> queryMBean(String nameExp) throws MalformedObjectNameException {
        if (null == MBEAN_SERVER) {
            return new HashSet<>();
        }
        return MBEAN_SERVER.queryNames(new ObjectName(nameExp), null);
    }

    /**
     * 查询MbeanAttribute
     */
    public static Object getMBeanAttribute(ObjectName objName, String attribute)
            throws AttributeNotFoundException, InstanceNotFoundException, MBeanException, ReflectionException {
        if (null == MBEAN_SERVER) {
            return null;
        }
        return MBEAN_SERVER.getAttribute(objName, attribute);
    }

    public static void main(String[] args) {
        System.out.println(getProgressNameInfo());
        System.out.println(getDragonInfo());
        System.setProperty("dragon.ip", "127.0.0.1");
        System.out.println(getDragonInfo());
        System.out.println(getPid());
        try {
            Set<ObjectName> oname = queryMBean("*:type=Memory*");

            System.out.println(oname);
            for (ObjectName name : oname) {
                System.out.println(getMBeanAttribute(name, "HeapMemoryUsage"));
            }
        } catch (AttributeNotFoundException | InstanceNotFoundException | MalformedObjectNameException | MBeanException | ReflectionException e) {
            e.printStackTrace();
        }
    }
}
