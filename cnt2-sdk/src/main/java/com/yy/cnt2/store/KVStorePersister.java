package com.yy.cnt2.store;

import java.util.concurrent.Future;

/**
 * KVStore持久化服务
 *
 * @author xlg
 * @since 2017/7/7
 */
public interface KVStorePersister {
    /**
     * Load kv from somewhere
     *
     * @param kvStore kvStore The kvStore
     */
    void load(KVStore kvStore);

    /**
     * Write kv to somewhere
     *
     * @param kvStore The kvStore
     */
    void write(KVStore kvStore);

    /**
     * Async write kv to somewhere
     *
     * @param kvStore
     */
    Future<Void> asyncWrite(KVStore kvStore);
}
