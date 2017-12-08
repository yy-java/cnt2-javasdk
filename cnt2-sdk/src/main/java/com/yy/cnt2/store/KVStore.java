package com.yy.cnt2.store;

import java.util.Map;

/**
 * KVStore
 *
 * @author xlg
 * @since 2017/7/7
 */
public interface KVStore {
    /**
     * Put k-v map into kv-store
     *
     * @param values The map of k-v
     */
    void putAll(Map<String, ConfigValue> values);

    /**
     * Put single k-v  into kv-store
     *
     * @param key   The key
     * @param value The value
     */
    void put(String key, ConfigValue value);

    /**
     * Get value by key
     *
     * @param key The key
     * @return Value of the key
     */
    ConfigValue get(String key);

    /**
     * Batch get values by keys
     *
     * @param keys The keys
     * @return K-V map of the keys
     */
    Map<String, ConfigValue> batchGet(String... keys);

    /**
     * Get all k-v
     *
     * @return All k-v
     */
    Map<String,  ConfigValue> getAll();

    /**
     * Delete value by key
     *
     * @param key
     */
    void delete(String key);
}
