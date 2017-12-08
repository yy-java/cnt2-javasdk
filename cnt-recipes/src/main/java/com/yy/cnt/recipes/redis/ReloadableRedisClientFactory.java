package com.yy.cnt.recipes.redis;

import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import com.yy.cnt.api.IControlCenterService;
import com.yy.cnt.api.event.EventHandler;
import com.yy.cnt.api.event.EventType;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yy.cs.base.redis.CsRedisRuntimeException;
import com.yy.cs.base.redis.RedisClientFactory;

public class ReloadableRedisClientFactory extends RedisClientFactory {
    private static final Logger log = LoggerFactory.getLogger(ReloadableRedisClientFactory.class);
    private IControlCenterService controlCenterService;
    private String propertyKey;
    private String oldConfigData;

    // 配置错误或没配置，都不抛异常
    private boolean silence = false;

    public void init() {
        try {
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
                        try {
                            if (event == EventType.PUT) {
                                //Node node = controlCenterService.getConfig(propertyKey);
                                //log.info(propertyKey + "Change new Data :" + node.getValue() + ", old Data :"
                                //        + oldConfigData);
                                //if (!oldConfigData.equals(node)) {
                                //    parseConfig(node.getValue());
                                //    ReloadableRedisClientFactory.super.init();
                                //}
                                parseConfig(value);
                                ReloadableRedisClientFactory.super.init();
                            }
                        } catch (Exception e) {
                            log.error("node change refresh datasource error :", e);
                        }
                    }

                    @Override
                    public String getKey() {
                        return propertyKey;
                    }
                });
            } else {
                throw new Exception("controlCenterService is null or propertyKey is null, please check!");
            }
            super.init();
        } catch (Exception e) {
            log.error("init redis for config propertyKey " + propertyKey + " error ", e);
            if (!silence) {
                throw new CsRedisRuntimeException(e);
            }
        }
    }

    private void parseConfig(String node) throws IOException, IllegalAccessException, InvocationTargetException {
        Properties property = new Properties();
        oldConfigData = node;
        StringReader sr = new StringReader(node);
        property.load(sr);
        sr.close();
        String servers = property.getProperty("redisServers");
        property.remove("redisServers");
        BeanUtils.copyProperties(this, property); // 更新zookeeper配置的属性
        // redisServers
        String[] serverInfos = servers.split(";");
        List<String> t = Arrays.asList(serverInfos);
        if (null != t && t.size() > 0) {
            this.setRedisServers(t);
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

    public boolean isSilence() {
        return silence;
    }

    public void setSilence(boolean silence) {
        this.silence = silence;
    }

}
