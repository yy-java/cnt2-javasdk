package com.yy.cnt.api;

import java.io.Closeable;
import java.util.Map;

import com.yy.cnt.api.event.ConfigEventHandler;
import com.yy.cnt.api.event.EventHandler;

/**
 * @author xlg
 * @since 2017/7/14
 */
public interface IControlCenterService extends Closeable {
    void registerEventHandler(EventHandler eventHandler);

    void removeEventHandler(EventHandler eventHandler);
    
    <T extends ConfigEventHandler> void registerEventHandler(String key ,T eventHandler);
    
    <T extends ConfigEventHandler> void removeEventHandler(String key ,T eventHandler);

    String getValue(String key);

    String getValue(String key, String defaultValue);

    Map<String, String> batchGetValue(String... keys);

    Map<String, String> getAllValue();

}
