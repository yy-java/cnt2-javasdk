package com.yy.cnt2.client.resolver;

import com.coreos.jetcd.Client;
import com.coreos.jetcd.KV;
import com.coreos.jetcd.Watch;
import com.coreos.jetcd.data.ByteSequence;
import com.coreos.jetcd.data.KeyValue;
import com.coreos.jetcd.options.GetOption;
import com.coreos.jetcd.options.WatchOption;
import com.coreos.jetcd.watch.WatchEvent;
import com.coreos.jetcd.watch.WatchResponse;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.yy.cnt2.client.Consts;
import com.yy.cnt2.domain.GrpcServerRegisterInfo;
import com.yy.cs.base.hostinfo.NetType;
import io.grpc.Attributes;
import io.grpc.EquivalentAddressGroup;
import io.grpc.internal.SharedResourceHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import static com.yy.cnt2.util.NetWorkUtils.toNetType;
import static com.yy.cs.base.json.Json.strToObj;
import static java.util.concurrent.Executors.newFixedThreadPool;

/**
 * 服务端注册格式：
 * <p>
 * /grpcservers/127.0.0.1:50051 {"serverIP":{"1":"127.0.0.1"},"port":50051,"groupId":196}
 *
 * @author xlg
 * @since 2017/7/11
 */
public class EtcdConfigCenterNameResolver extends AbstractConfigCenterNameResolver {
    private final Logger logger = LoggerFactory.getLogger(EtcdConfigCenterNameResolver.class);

    public static final String SERVER_REG_PATH = "/grpcservers/";
    // Ip:port => GrpcServerRegisterInfo

    private final ExecutorService executorService = newFixedThreadPool(1,
            new ThreadFactoryBuilder().setDaemon(true).setNameFormat("cnt2-name-resolver-pool-%s").build());

    private Client client;
    private List<GrpcServerRegisterInfo> grpcServerRegisterInfoList = Lists.newLinkedList();

    public EtcdConfigCenterNameResolver(String name, SharedResourceHolder.Resource<ExecutorService> executorResource,
            Client client) {

        super(name, executorResource);

        this.client = client;

        try {
            init();
        } catch (Exception e) {
            throw new RuntimeException("Init EtcdConfigCenterNameResolver failed!", e);
        }
    }

    public void init() throws Exception {
        loadServiceMetaInfo();

        startWatch();
    }

    private void startWatch() {
        ByteSequence path = ByteSequence.fromString(SERVER_REG_PATH);
        Watch watchClient = client.getWatchClient();
        final Watch.Watcher watcher = watchClient.watch(path, WatchOption.newBuilder().withPrefix(path).build());

        executorService.execute(new Runnable() {
            @Override
            public void run() {

                while (!shutdown) {
                    try {
                        WatchResponse listen = watcher.listen();

                        List<WatchEvent> events = listen.getEvents();

                        if (events.size() > 0) {
                            try {
                                loadServiceMetaInfo();
                                refresh();
                            } catch (Exception e) {
                                logger.warn("Refresh server info failed!", e);
                            }
                        }
                    } catch (Exception e) {
                        logger.warn("Watch server info error! Message:{}", e.getMessage());

                        try {
                            TimeUnit.SECONDS.sleep(1);
                        } catch (InterruptedException e1) {
                        }
                    }
                }
                watcher.close(); // if shutdown resolver ,stop watch
            }
        });
    }

    public synchronized void loadServiceMetaInfo() throws Exception {
        KV kvClient = client.getKVClient();

        ByteSequence path = ByteSequence.fromString(SERVER_REG_PATH);
        GetOption getOption = GetOption.newBuilder().withPrefix(path).build();
        List<KeyValue> keyValueList = kvClient.get(path, getOption).get().getKvs();

        List<GrpcServerRegisterInfo> temp = Lists.newLinkedList();
        for (KeyValue keyValue : keyValueList) {
            temp.add(parseGrpcServerRegisterInfo(keyValue.getValue().toStringUtf8()));
        }
        List<GrpcServerRegisterInfo> old = grpcServerRegisterInfoList;
        grpcServerRegisterInfoList = temp;
        old.clear();
    }

    private GrpcServerRegisterInfo parseGrpcServerRegisterInfo(String registerInfo) {
        return strToObj(registerInfo, GrpcServerRegisterInfo.class);
    }

    @Override
    protected List<EquivalentAddressGroup> getEquivalentAddressGroups() {
        List<EquivalentAddressGroup> list = Lists.newLinkedList();
        Map<String, NetType> netTypeMap = new HashMap<>(8);

        for (GrpcServerRegisterInfo registerInfo : this.grpcServerRegisterInfoList) {
            List<SocketAddress> socketAddressList = Lists.newLinkedList();
            Attributes.Builder attributes = Attributes.newBuilder();

            for (Map.Entry<String, String> entry : registerInfo.getServerIP().entrySet()) {
                netTypeMap.put(entry.getValue(), toNetType(Integer.parseInt(entry.getKey())));
                socketAddressList.add(new InetSocketAddress(entry.getValue(), registerInfo.getPort()));
            }

            attributes.set(Consts.IP_NET_TYPE_MAP_KEY, netTypeMap);
            attributes.set(Consts.SERVICE_GROUP_ID_KEY, registerInfo.getGroupId());

            list.add(new EquivalentAddressGroup(socketAddressList, attributes.build()));
        }

        return list;
    }

    @Override
    protected List<SocketAddress> getServers() {
        return Collections.EMPTY_LIST;
    }

    @Override
    protected Attributes getAttributes() {
        return Attributes.EMPTY;
    }

}
