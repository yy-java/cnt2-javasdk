package com.yy.cnt2.client;

import com.coreos.jetcd.Client;
import com.coreos.jetcd.ClientBuilder;
import com.coreos.jetcd.KV;
import com.coreos.jetcd.exception.AuthFailedException;
import com.coreos.jetcd.exception.ConnectException;
import com.google.common.collect.ImmutableMap;
import com.yy.cnt2.domain.GrpcServerRegisterInfo;
import com.yy.cnt2.grpc.api.ResponseMessage;
import com.yy.cnt2.server.ConfigCenterTestServer;
import com.yy.cs.base.hostinfo.NetType;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.coreos.jetcd.data.ByteSequence.fromString;
import static com.yy.cnt2.client.resolver.EtcdConfigCenterNameResolver.SERVER_REG_PATH;
import static com.yy.cs.base.json.Json.ObjToStr;

/**
 * @author xlg
 * @since 2017/7/10
 */
public class ConfigCenterClientTest {
    private final String app = "app";
    private final String profile = "production";
    ConfigCenterClient configCenterClient;
    Client etcdClient = null;
    ScheduledExecutorService executorService = Executors.newScheduledThreadPool(5);

    @Before
    public void init() throws Exception {
        etcdClient = ClientBuilder.newBuilder().setEndpoints("http://localhost:2379").build();

        ConfigCenterTestServer.startServer(8888, 9999);

        regServerToEtcd();

    }

    private void regServerToEtcd() throws Exception {
        KV kvClient = etcdClient.getKVClient();

        GrpcServerRegisterInfo grpcServerRegisterInfo = new GrpcServerRegisterInfo();
        grpcServerRegisterInfo.setGroupId(123);
        grpcServerRegisterInfo.setPort(8888);
        grpcServerRegisterInfo.setServerIP(ImmutableMap.of(String.valueOf(NetType.CTL.getValue()), "127.0.0.1"));

        kvClient.put(fromString(SERVER_REG_PATH + "service1"), fromString(ObjToStr(grpcServerRegisterInfo)));

        grpcServerRegisterInfo = new GrpcServerRegisterInfo();
        grpcServerRegisterInfo.setGroupId(456);
        grpcServerRegisterInfo.setPort(9999);
        grpcServerRegisterInfo.setServerIP(ImmutableMap.of(String.valueOf(NetType.CTL.getValue()), "127.0.0.1"));

        kvClient.put(fromString(SERVER_REG_PATH + "service2"), fromString(ObjToStr(grpcServerRegisterInfo)));

    }

    public void initFromEtcd() throws AuthFailedException, ConnectException {
        configCenterClient = ConfigCenterClientBuilder.newBuilder().setEtcdClient(etcdClient).build();
    }

    public void initFromLocal() {
        configCenterClient = ConfigCenterClientBuilder.newBuilder().setEndpoints("61.147.187.150:50051").build();
    }

    @Test
    public void testDiscoverFromEtcd() throws AuthFailedException, ConnectException {
        initFromEtcd();

        while (true) {
            String node = configCenterClient.registerClient(app, profile);
            System.out.println(node);

            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    @Test
    public void registerClient() throws Exception {
        initFromLocal();

        executorService.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                String node = configCenterClient.registerClient(app, profile);
                System.out.println(node);
            }
        }, 1, 5, TimeUnit.SECONDS);

        synchronized (this) {
            this.wait();
        }
    }

    @Test
    public void queryAll() throws Exception {
        initFromLocal();
        Map<String, ResponseMessage> map = configCenterClient.queryAll(app, profile);
        System.out.println(map);
        synchronized (this) {
            this.wait();
        }
    }

    @Test
    public void queryKey() throws Exception {
        initFromLocal();
        String value = configCenterClient.queryKey(app, profile, "configKey2", 0).getValue();
        System.out.println(value);
    }

    @Test
    public void valueChangeResultNotify() throws Exception {
        initFromLocal();
        String nodeId = configCenterClient.registerClient(app, profile);
        boolean notify = configCenterClient.valueChangeResultNotify(nodeId, app, profile, "configKey3", "deployId",1,
                true);
        System.out.println(notify);
    }

}