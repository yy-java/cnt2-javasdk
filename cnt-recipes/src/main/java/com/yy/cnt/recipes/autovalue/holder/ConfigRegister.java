package com.yy.cnt.recipes.autovalue.holder;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.yy.cnt.api.IControlCenterService;
import com.yy.cnt.api.event.ConfigEventHandler;
import com.yy.cnt.recipes.autovalue.event.PropertyKeyChangeCallback;

public class ConfigRegister {

    /**
     * propertyName:
     * |---- controlCenter:
     * |---- ---- Set<BeanPropertyHolder>
     */
    private ConcurrentHashMap<String, Map<IControlCenterService, Set<BeanPropertyHolder>>> beanPropsHolder = new ConcurrentHashMap<>();

    private Map<IControlCenterService, ConfigEventHandler> watchers = new HashMap<>();

    public synchronized void register(String propertyKey, IControlCenterService controlCenterService,
            BeanPropertyHolder holder) {
        Map<IControlCenterService, Set<BeanPropertyHolder>> subWatcher = this.getRegister(propertyKey);

        Set<BeanPropertyHolder> holders = subWatcher.get(controlCenterService);
        if (null == holders) {
            holders = new HashSet<BeanPropertyHolder>();
            subWatcher.put(controlCenterService, holders);
        }

        holders.add(holder);
        ConfigEventHandler callback = watchers.get(controlCenterService);
        if (null == callback) {
            callback = new PropertyKeyChangeCallback(controlCenterService, this);
            watchers.put(controlCenterService, callback);
        }
        controlCenterService.registerEventHandler(propertyKey, callback);
    }

    public Map<IControlCenterService, Set<BeanPropertyHolder>> getRegister(String propertyKey) {
        if (beanPropsHolder.containsKey(propertyKey)) {
            return beanPropsHolder.get(propertyKey);
        }
        Map<IControlCenterService, Set<BeanPropertyHolder>> child = new ConcurrentHashMap<>();
        Map<IControlCenterService, Set<BeanPropertyHolder>> older = beanPropsHolder.putIfAbsent(propertyKey, child);
        if (null != older) {
            return older;
        }
        return child;
    }
}
