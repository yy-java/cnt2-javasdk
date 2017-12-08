package com.yy.cnt.recipes.autovalue.event;

import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yy.cnt.api.IControlCenterService;
import com.yy.cnt.api.event.ConfigEventHandler;
import com.yy.cnt.api.event.EventType;
import com.yy.cnt.recipes.autovalue.holder.BeanPropertyHolder;
import com.yy.cnt.recipes.autovalue.holder.ConfigRegister;

public class PropertyKeyChangeCallback implements ConfigEventHandler {

    private static final Logger log = LoggerFactory.getLogger(PropertyKeyChangeCallback.class);

    private IControlCenterService controlCenterService;
    public ConfigRegister register;

    public PropertyKeyChangeCallback(IControlCenterService controlCenterService, ConfigRegister register) {
        this.controlCenterService = controlCenterService;
        this.register = register;
    }

    private Set<BeanPropertyHolder> getRelativeHolders(String propertyKey, IControlCenterService controlCenterService) {
        Map<IControlCenterService, Set<BeanPropertyHolder>> subWatchers = this.register.getRegister(propertyKey);
        return subWatchers.get(controlCenterService);
    }

    @Override
    public void handle(String key, EventType event, String value) {
        Set<BeanPropertyHolder> holders = getRelativeHolders(key, this.controlCenterService);
        if (null == holders || holders.size() == 0) {
            return;
        }
        for (BeanPropertyHolder holder : holders) {
            String v = value;
            if (EventType.DELETE == event) {
                v = holder.getDefaultValue();
            }
            try {
                holder.setFieldValue(v);
            } catch (Exception e) {
                log.error("update bean[" + holder.getBean().getClass().getName() + "].field["
                        + holder.getField().getName() + "] value with propertyKey[" + key + "] error ", e);
            }
        }
    }

}
