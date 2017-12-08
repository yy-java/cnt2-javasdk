package com.yy.cnt2.store.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.io.Files;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.yy.cnt2.store.ConfigValue;
import com.yy.cnt2.store.KVStore;
import com.yy.cnt2.store.KVStorePersister;
import com.yy.cnt2.util.Json;
import com.yy.cnt2.util.PathHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import static java.util.concurrent.Executors.*;

/**
 * Write or load to/from file(JSON format)
 *
 * @author xlg
 * @since 2017/7/7
 */
public class KVStorePersisterFileImpl implements KVStorePersister {
    private final Logger logger = LoggerFactory.getLogger(KVStorePersisterFileImpl.class);

    private static final Charset UTF8 = Charset.forName("UTF-8");

    private final ExecutorService executorService = newFixedThreadPool(5,
            new ThreadFactoryBuilder().setDaemon(true).setNameFormat("cnt2-kvstore-persist-pool-%s").build());
    private final TypeReference<Map<String, ConfigValue>> typeReference = new TypeReference<Map<String, ConfigValue>>() {
    };
    private File file;

    public KVStorePersisterFileImpl(File file) {
        this.file = file;

        if (!file.exists()) {
            try {
                File parent = file.getParentFile();
                if (!parent.exists()) {
                    parent.mkdirs();
                }
                file.createNewFile();
            } catch (IOException e) {
                logger.error("Create file failed! File:{}", file);
            }
        }
    }

    @Override
    public void load(KVStore kvStore) {
        String data = null;
        try {
            List<String> lines = Files.readLines(file, UTF8);
            data = Joiner.on(PathHelper.EMPTY_STRING).join(lines);
            if (Strings.isNullOrEmpty(data)) {
                return;
            }
            kvStore.putAll(Json.strToObj(data, typeReference));
        } catch (Exception e) {
            logger.warn("Load file failed! File:{} Data:{}", file, data);
        }
    }

    @Override
    public synchronized void write(KVStore kvStore) {
        String str = Json.ObjToStr(kvStore.getAll());
        try {
            Files.write(str, file, UTF8);
        } catch (IOException e) {
            logger.warn("Write file failed! File:{}", file);
        }
    }

    @Override
    public Future<Void> asyncWrite(final KVStore kvStore) {
        return executorService.submit(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                write(kvStore);
                return null;
            }
        });
    }
}
