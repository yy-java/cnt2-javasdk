package com.yy.cnt.recipes.redis.sentinel;

import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import com.yy.cnt.api.IControlCenterService;
import com.yy.cnt.api.event.EventHandler;
import com.yy.cnt.api.event.EventType;
import com.yy.cs.base.redis.sentinel.RedisSentinelFactory;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReloadableRedisSentinelFactory extends RedisSentinelFactory {
    private static final Logger log = LoggerFactory.getLogger(ReloadableRedisSentinelFactory.class);
    private IControlCenterService controlCenterService;
    private String propertyKey;
    private String oldConfigData;

    public void init() {
        if (null != controlCenterService && StringUtils.isNotBlank(propertyKey)) {
            // 加载设置
            String node = controlCenterService.getValue(propertyKey);
            try {
                parseConfig(node);
            } catch (Exception e1) {
                log.error("remote config load error:", e1);
            }
            // 添加监听
            controlCenterService.registerEventHandler(new EventHandler() {
                @Override
                public void handle(String key, EventType event, String value) {
                    if (event != EventType.PUT) {
                        return;
                    }
                    try {
                        // Node node = controlCenterService.getConfig(propertyKey);
                        // log.info(propertyKey + "Change new Data :" + node.getValue() + ", old Data :" + oldConfigData);
                        // if (!oldConfigData.equals(node)) {
                        //  parseConfig(node.getValue());
                        //  ReloadableRedisSentinelFactory.super.destroy();    // 销毁之前的连接
                        //  ReloadableRedisSentinelFactory.super.init();       // 重建连接
                        // }
                        parseConfig(controlCenterService.getValue(propertyKey));
                        ReloadableRedisSentinelFactory.super.destroy();    // 销毁之前的连接
                        ReloadableRedisSentinelFactory.super.init();       // 重建连接

                    } catch (Exception e) {
                        log.error("node change refresh datasource error :", e);
                    }
                }

                @Override
                public String getKey() {
                    return propertyKey;
                }

            });
        }
        super.init();
    }

    private void parseConfig(String node) throws IOException, IllegalAccessException, InvocationTargetException {
        Properties property = new Properties();
//        oldConfigData = node;
        StringReader sr = new StringReader(node);
        property.load(sr);
        sr.close();
        String servers = property.getProperty("redisSentinelServers");
        property.remove("redisSentinelServers");
        BeanUtils.copyProperties(this, property); // 更新zookeeper配置的属性
        // redisServers
        String[] serverInfos = servers.split(";");
        Set<String> t = new HashSet<String>(Arrays.asList(serverInfos));
        if (null != t && t.size() > 0) {
            this.setServers(t);
        }
    }

    public IControlCenterService getControlCenterService() {
        return controlCenterService;
    }

    public String getPropertyKey() {
        return propertyKey;
    }

    public void setControlCenterService(IControlCenterService controlCenterService) {
        this.controlCenterService = controlCenterService;
    }

    public void setPropertyKey(String propertyKey) {
        this.propertyKey = propertyKey;
    }

}
