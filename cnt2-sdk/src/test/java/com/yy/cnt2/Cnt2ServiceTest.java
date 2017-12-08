package com.yy.cnt2;

import com.coreos.jetcd.Client;
import com.coreos.jetcd.ClientBuilder;
import com.coreos.jetcd.exception.AuthFailedException;
import com.coreos.jetcd.exception.ConnectException;
import com.google.common.base.Strings;
import com.yy.cnt2.client.ConfigCenterClient;
import com.yy.cnt2.domain.ConfigNode;
import com.yy.cnt2.domain.ConfigPublishInfo;
import com.yy.cnt2.event.AbstractEventHandler;
import com.yy.cnt2.server.ConfigCenterTestServer;
import org.junit.Before;
import org.junit.Test;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static com.coreos.jetcd.data.ByteSequence.fromString;
import static com.yy.cnt2.util.PathHelper.createProfilePath;
import static com.yy.cs.base.json.Json.ObjToStr;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author xlg
 * @since 2017/7/7
 */
public class Cnt2ServiceTest {
    Cnt2Service cnt2Service;
    ScheduledExecutorService executorService = Executors.newScheduledThreadPool(10);
    Client etcdClient;
    String appName = "testApp";
    String profile = "production";
    String localFilePath = "D:\\Users\\Administrator\\Desktop\\test\\";
    String configCenterEndpoints = "localhost:8888,localhost:9999";
    ConfigCenterClient configCenterClient;

    @Before
    public void init() throws AuthFailedException, ConnectException {
        ConfigCenterTestServer.startServer(8888, 9999);

        cnt2Service = new Cnt2Service(appName, profile, localFilePath, null, null,
                configCenterEndpoints, null);

        etcdClient = ClientBuilder.newBuilder().setEndpoints("http://localhost:2379").build();

    }

    @Test
    public void testRun() throws InterruptedException {

        block(20);
    }

    String[] keys = { "configKey1", "configKey2", "configKey3" };

    @Test
    public void testGrayPublishSetKeyValue() throws ExecutionException, InterruptedException {
        TestEventHandler handler = new TestEventHandler();
        cnt2Service.registerEventHandler(handler);
        String path = createProfilePath(appName, profile) + "/" + handler.getKey();

        ConfigNode configNode = new ConfigNode(appName, profile, handler.getKey(), 999,
                new ConfigPublishInfo("PublishId", handler.getKey(), 999, asList(cnt2Service.configAppInfo.getNodeId())));

        //Gray target
        etcdClient.getKVClient().put(fromString(path), fromString(ObjToStr(configNode))).get();

        //Not Gray target
        configNode.getPublishInfo().setPublishNodes(asList("not exists"));
        etcdClient.getKVClient().put(fromString(path), fromString(ObjToStr(configNode))).get();

        block(10);
    }

    @Test
    public void testSetMultiKeyValue() throws InterruptedException {
        cnt2Service.registerEventHandler(new TestEventHandler());

        final Random random = new Random();
        final AtomicInteger version = new AtomicInteger(100);

        executorService.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                String key = keys[random.nextInt(keys.length)];
                ConfigNode configNode = new ConfigNode(appName, profile, key, version.incrementAndGet(),
                        new ConfigPublishInfo(UUID.randomUUID().toString(), key, version.get(), null));
                String path = createProfilePath(appName, profile) + "/" + key;
                try {
                    System.out.println("Put:" + path + " Data:" + ObjToStr(configNode));
                    etcdClient.getKVClient().put(fromString(path), fromString(ObjToStr(configNode))).get();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 1, 5, TimeUnit.SECONDS);

        block(100);
    }

    private void block(long time) {
        synchronized (this) {
            try {
                wait(time * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    class TestEventHandler extends AbstractEventHandler {
        @Override
        public void handleDeleteEvent(String key, String newValue) {
            assertEquals(getKey(), key);
            assertTrue(null == cnt2Service.getValue(key));

            System.out.printf("Delete: Key=>%s NewV=>%s\n", key, newValue);
            System.out.println(cnt2Service.getAllValue());
        }

        @Override
        public void handlePutEvent(String key, String newValue) {
            assertEquals(getKey(), key);
            assertTrue(!Strings.isNullOrEmpty(cnt2Service.getValue(key)));
            assertEquals(cnt2Service.getValue(key), newValue);
            System.out.printf("Update: Key=>%s NewV=>%s\n", key, newValue);
            System.out.println(cnt2Service.getAllValue());
        }

        @Override
        public String getKey() {
            return keys[2];
        }
    }

}