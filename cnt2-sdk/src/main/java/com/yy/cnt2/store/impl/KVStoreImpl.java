package com.yy.cnt2.store.impl;

import com.yy.cnt2.store.ConfigValue;
import com.yy.cnt2.store.KVStore;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.google.common.collect.Maps.newHashMap;

/**
 * KVStoreImpl
 *
 * @author xlg
 * @since 2017/7/7
 */
public class KVStoreImpl implements KVStore {
    private Map<String, ConfigValue> store = new ConcurrentHashMap<>();

    @Override
    public void putAll(Map<String, ConfigValue> values) {
        store.putAll(values);
    }

    @Override
    public void put(String key, ConfigValue value) {
        store.put(key, value);
    }

    @Override
    public ConfigValue get(String key) {
        return store.get(key);
    }

    @Override
    public Map<String, ConfigValue> batchGet(String... keys) {
        Map<String, ConfigValue> values = newHashMap();
        if (null == keys) {
            return values;
        }
        for (String key : keys) {
            values.put(key, store.get(key));
        }
        return values;
    }

    @Override
    public Map<String, ConfigValue> getAll() {
        return newHashMap(this.store);
    }

    @Override
    public void delete(String key) {
        store.remove(key);
    }

    @Override
    public String toString() {
        return "KVStore{" + "store=" + store + '}';
    }
}
