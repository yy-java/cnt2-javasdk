package com.yy.cnt2.event;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yy.cnt.api.event.ConfigEventHandler;
import com.yy.cnt.api.event.EventHandler;

/**
 * EventHandler注册表
 *
 * @author xlg
 * @since 2017/7/7
 */
@SuppressWarnings("unchecked")
public class EventHandlerRegistry {
    private final Map<String, List<? extends ConfigEventHandler>> registry = Maps.newHashMap();

    /**
     * 注册业务自定义的事件处理器
     */
    public synchronized <T extends ConfigEventHandler> void registerCustomHandler(String key, T eventHandler) {
        List<ConfigEventHandler> list = (List<ConfigEventHandler>) registry.get(key);
        if (null == list) {
            list = Lists.newArrayList();
            registry.put(key, list);
        }

        list.add(eventHandler);
    }

    /**
     * 注销业务自定义事件处理器
     */
    public synchronized <T extends ConfigEventHandler> void unregisterCustomHandler(String key, T eventHandler) {
        List<ConfigEventHandler> list = (List<ConfigEventHandler>) registry.get(key);
        if (null == list) {
            return;
        }
        list.remove(eventHandler);
    }

    /**
     * 获取用来处理某个Key的所有业务自定义处理器
     */
    public List<ConfigEventHandler> getCustomHandlers(String key) {
        List<ConfigEventHandler> list = (List<ConfigEventHandler>) registry.get(key);
        if (null == list) {
            return Collections.emptyList();
        }

        return ImmutableList.copyOf(list);
    }

}
