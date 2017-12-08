package com.yy.cnt.recipes.db.serivce;

import com.yy.cnt.api.event.EventHandler;
import com.yy.cnt.api.event.EventType;
import com.yy.cnt.recipes.db.common.AbstractDataSourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

public class PublicConfigChangeCallback implements EventHandler {
    private static final Logger LOG = LoggerFactory.getLogger(PublicConfigChangeCallback.class);
    private PublicConfigService publicService;
    private String key;

    public PublicConfigChangeCallback(PublicConfigService publicService, String key) {
        this.publicService = publicService;
        this.key = key;
    }

    @Override
    public void handle(String key, EventType event, String value) {
        Set<AbstractDataSourceFactory<?, ?>> datasources = publicService.getFollows(key);
        if (null == datasources || datasources.isEmpty()) {
            return;
        }
        for (AbstractDataSourceFactory<?, ?> datasource : datasources) {
            try {
                datasource.init();
            } catch (Exception e) {
                LOG.error("reinit Datasource : " + datasource.getPropertyKey() + " error:", e);
            }
        }
    }

    @Override
    public String getKey() {
        return key;
    }
}
