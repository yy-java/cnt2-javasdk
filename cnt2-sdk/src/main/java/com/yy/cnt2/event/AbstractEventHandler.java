package com.yy.cnt2.event;

import com.yy.cnt.api.event.EventHandler;
import com.yy.cnt.api.event.EventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 事件处理抽象类
 *
 * @author xlg
 * @since 2017/7/7
 */
public abstract class AbstractEventHandler implements EventHandler {
    private final Logger logger = LoggerFactory.getLogger(AbstractEventHandler.class);

    @Override
    public final void handle(String key, EventType event, String value) {
        switch (event) {
        case DELETE:
            handleDeleteEvent(key, value);
            break;
        case PUT:
            handlePutEvent(key, value);
            break;
        default:
            return;
        }

    }

    public void handleDeleteEvent(String key, String value) {
    }

    public void handlePutEvent(String key, String value) {
    }

}
