package com.yy.cnt2.util;

import com.google.common.base.Joiner;

/**
 * Path helper
 *
 * @author xlg
 * @since 2017/7/7
 */
public class PathHelper {
    public static final String PATH_SEPARATOR = "/";
    public static final String PATH_SEGMENT_NODES = "nodes";
    public static final String PATH_SEGMENT_PROFILES = "profiles";
    public static final String EMPTY_STRING = "";

    /**
     * 合并路径
     */
    public static String joinPath(String... paths) {
        return Joiner.on(PATH_SEPARATOR).join(paths);
    }

    /**
     * 创建节点注册路径
     */
    public static String createRegisterPath(String appName, String profile, String nodeId) {
        return joinPath(EMPTY_STRING, appName, PATH_SEGMENT_NODES, profile, nodeId);
    }

    /**
     * 创建Profile路径
     */
    public static String createProfilePath(String appName, String profile) {
        return joinPath(EMPTY_STRING, appName, PATH_SEGMENT_PROFILES, profile);
    }

    /**
     * 将配置名从路径中提取出来
     */
    public static String extractKey(String path, String basePath) {
        if (null == path || null == basePath) {
            return null;
        }
        if (path.indexOf(basePath) != 0) {
            return null;
        }

        String substring = path.substring(basePath.length()).replaceAll("^/*(.*)$", "$1").trim();
        return substring.length() == 0 ? null : substring;
    }

    public static void main(String[] args) {
        System.out.println(extractKey("/app1/profiles/product/timeout", "/app1/profiles/product"));
        System.out.println(extractKey("/app1/profiles/product/", "/app1/profiles/product"));
        System.out.println(extractKey("/app1/profiles/product", "/app1/profiles/product"));

        System.out.println(extractKey("/app1/profiles/product/timeout", "/app1/profiles/product/"));
        System.out.println(extractKey("/app1/profiles/product/", "/app1/profiles/product/"));
        System.out.println(extractKey("/app1/profiles/product", "/app1/profiles/product/"));
    }
}
