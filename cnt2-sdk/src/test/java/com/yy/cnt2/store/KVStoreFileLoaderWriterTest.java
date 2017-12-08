package com.yy.cnt2.store;

import com.yy.cnt2.store.impl.KVStorePersisterFileImpl;
import com.yy.cnt2.store.impl.KVStoreImpl;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

/**
 * @author xlg
 * @since 2017/7/7
 */
public class KVStoreFileLoaderWriterTest {
    KVStorePersisterFileImpl kvStoreFileLoaderWriter;
    @Before
    public void init() {
        kvStoreFileLoaderWriter = new KVStorePersisterFileImpl(new File("D:\\Users\\Administrator\\Desktop\\test\\data.json"));
    }

    @Test
    public void load() throws Exception {
        KVStore kvStore = new KVStoreImpl();
        kvStoreFileLoaderWriter.load(kvStore);
        System.out.println(kvStore);
    }

    @Test
    public void write() throws Exception {
        KVStore kvStore = new KVStoreImpl();
        kvStore.put("Name",new ConfigValue("Henry",1));
        kvStoreFileLoaderWriter.write(kvStore);
    }

}