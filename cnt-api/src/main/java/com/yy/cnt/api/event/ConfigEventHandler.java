package com.yy.cnt.api.event;

public interface ConfigEventHandler {
    /**
     *  handle event
     *
     * @param key   The key
     * @param event The event
     * @param value The new value
     */
    void handle(String key, EventType event, String value);
}
