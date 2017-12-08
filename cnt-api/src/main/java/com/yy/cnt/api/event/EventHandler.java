package com.yy.cnt.api.event;

/**
 * 事件处理器
 *
 * @author xlg
 * @since 2017/7/7
 */
public interface EventHandler extends ConfigEventHandler {

    /**
     * The key you want monitor
     */
    String getKey();
}
