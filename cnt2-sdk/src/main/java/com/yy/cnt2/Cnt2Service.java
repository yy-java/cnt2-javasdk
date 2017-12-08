package com.yy.cnt2;

import com.coreos.jetcd.Client;
import com.coreos.jetcd.ClientBuilder;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Maps;
import com.yy.cnt.api.IControlCenterService;
import com.yy.cnt.api.event.ConfigEventHandler;
import com.yy.cnt.api.event.EventHandler;
import com.yy.cnt2.client.ConfigCenterClient;
import com.yy.cnt2.client.ConfigCenterClientBuilder;
import com.yy.cnt2.domain.ConfigAppInfo;
import com.yy.cnt2.event.AbstractEventHandler;
import com.yy.cnt2.event.EventHandlerRegistry;
import com.yy.cnt2.event.EventWatcher;
import com.yy.cnt2.grpc.api.ResponseMessage;
import com.yy.cnt2.register.ConfigCenterRegister;
import com.yy.cnt2.store.ConfigValue;
import com.yy.cnt2.store.KVStore;
import com.yy.cnt2.store.KVStorePersister;
import com.yy.cnt2.store.impl.KVStoreImpl;
import com.yy.cnt2.store.impl.KVStorePersisterFileImpl;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static java.lang.System.currentTimeMillis;

/**
 * 流程：
 * <p>
 * 1.到ConfigCenter注册，拿到nodeId
 * <p>
 * 2.到etcd注册临时节点(/${appName}/nodes/${nodeId})
 * <p>
 * 3.到ConfigCenter查询当前profile下所有配置,写入本地副本
 * <p>
 * 4.订阅节点变化(/${appName}/profiles/${profile})，在服务端配置发生变化时更新本地副本
 * <p>
 * 5.配置发生变化是通知业务
 * <p>
 * 6.如果注册失败则去读本地文件
 *
 * @author xlg
 * @since 2017/7/7
 */
public class Cnt2Service implements Closeable, IControlCenterService {
    private final Logger logger = LoggerFactory.getLogger(Cnt2Service.class);

    public static final String COMMA_SEPARATOR = ",";
    public static final String DEFAULT_PROFILE = "default";
    public static final String DEFAULT_LOCAL_FILE_PATH = "/data/cache/cnt2";
    public static final String DEFAULT_ETCD_ENDPOINTS = "http://localhost:2379";
    public static final int DEFAULT_ETCD_LEASE_TTL = 10;// Seconds

    private static final Set<String> RESERVE_KEYS = new HashSet<>();

    private String appName;
    private String profile = DEFAULT_PROFILE;
    private String localFilePath = DEFAULT_LOCAL_FILE_PATH;
    private String etcdEndpoints = DEFAULT_ETCD_ENDPOINTS;
    private Integer etcdLeaseTtl = DEFAULT_ETCD_LEASE_TTL;
    private String configCenterEndpoints;
    private List<AbstractEventHandler> eventHandlers;

    private EventHandlerRegistry handlerRegistry = new EventHandlerRegistry();

    private Client etcdClient;
    private ConfigCenterClient configCenterClient;
    private KVStore kvStore = new KVStoreImpl();
    private KVStorePersister kvStorePersister;
    private EventWatcher eventWatcher;
    private ConfigCenterRegister configCenterRegister;

    @VisibleForTesting
    ConfigAppInfo configAppInfo;

    static {
        RESERVE_KEYS.add("grpcservers");
    }

    /**
     * Cnt2Service
     *
     * @param appName               应用名称
     * @param profile               Profile，默认：default
     * @param localFilePath         本地配置路径（可用于加载/存储远端配置），默认：/data/cache/cnt2
     * @param etcdEndpoints         etcd地址，多个可用逗号分开（例如：localhost:2379,localhost:2380）
     * @param etcdLeaseTtl          多久没有心跳就把本节点从配置中心移除，单位：秒，默认：10
     * @param configCenterEndpoints 配置中心地址，如果不配则从etcd中自己查找
     * @param eventHandlers         自定义事件处理器
     */
    public Cnt2Service(String appName, String profile, String localFilePath, String etcdEndpoints, Integer etcdLeaseTtl,
            String configCenterEndpoints, List<AbstractEventHandler> eventHandlers) {
        this.appName = appName;
        if (null != profile) {
            this.profile = profile;
        }
        if (null != localFilePath) {
            this.localFilePath = localFilePath;
        }
        if (null != etcdEndpoints) {
            this.etcdEndpoints = etcdEndpoints;
        }
        if (null != etcdLeaseTtl) {
            this.etcdLeaseTtl = etcdLeaseTtl;
        }

        this.configCenterEndpoints = configCenterEndpoints;
        this.eventHandlers = eventHandlers;

        init();
    }

    public Cnt2Service(String appName, String profile, String localFilePath, String etcdEndpoints,
            Integer etcdLeaseTtl) {
        this(appName, profile, localFilePath, etcdEndpoints, etcdLeaseTtl, null, null);
    }

    private void init() {
        logger.info("Config center service starting");
        checkConfig();

        configAppInfo = new ConfigAppInfo(appName, profile, null);

        initKVStorePersister();

        StringBuilder sb = new StringBuilder(512);

        try {
            // 初始化EtchClient
            long startTime = currentTimeMillis();
            initEtcdClient();
            sb.append("initEtchClient cost:").append(currentTimeMillis() - startTime);

            // 初始化ConfigCenterClient
            startTime = currentTimeMillis();
            initConfigCenterClient();
            sb.append(" initConfigCenterClient cost:").append(currentTimeMillis() - startTime);

            // 注册节点
            startTime = currentTimeMillis();
            registerNode();
            sb.append(" registerNode cost:").append(currentTimeMillis() - startTime);

            // 加载服务端配置
            startTime = currentTimeMillis();
            loadKVStoreFromConfigCenter();
            sb.append(" loadKVStoreFromConfigCenter cost:").append(currentTimeMillis() - startTime);

            // 添加配置变更处理器
            startTime = currentTimeMillis();
            addCustomEventHandler();
            sb.append(" addCustomEventHandler cost:").append(currentTimeMillis() - startTime);

            // 初始化变更事件监听器
            startTime = currentTimeMillis();
            initEtcdEventWatcher();
            sb.append(" initEtcdEventWatcher cost:").append(currentTimeMillis() - startTime);

            logger.info("Config center service started! Node:{} \n {}", configAppInfo.getNodeId(), sb);

        } catch (Throwable throwable) {
            logger.warn("Init failed! Try to load local config!", throwable);

            loadKVStoreFromLocal();
        }
    }

    /**
     * 注册节点
     */
    private void registerNode() throws Exception {
        configCenterRegister = new ConfigCenterRegister(etcdClient, configCenterClient);
        String nodeId = configCenterRegister.doRegister(appName, profile, etcdLeaseTtl);

        configAppInfo.setNodeId(nodeId);
    }

    /**
     * 初始化配置中心客户端
     */
    private void initConfigCenterClient() throws Exception {
        if (null != configCenterClient) {
            return;
        }

        ConfigCenterClientBuilder clientBuilder = ConfigCenterClientBuilder.newBuilder();
        if (StringUtils.isNotBlank(this.configCenterEndpoints)) {
            clientBuilder.setEndpoints(configCenterEndpoints.split(COMMA_SEPARATOR));
        } else {
            initEtcdClient();
            clientBuilder.setEtcdClient(etcdClient);
        }
        configCenterClient = clientBuilder.build();
    }

    /**
     * 初始化Etcd客户端
     */
    private void initEtcdClient() throws Exception {
        if (null != etcdClient) {
            return;
        }
        etcdClient = ClientBuilder.newBuilder().setEndpoints(etcdEndpoints.split(COMMA_SEPARATOR)).build();
    }

    /**
     * 加载本地配置
     */
    private void loadKVStoreFromLocal() {
        if (null == kvStorePersister) {
            initKVStorePersister();
        }

        kvStorePersister.load(kvStore);
    }

    /**
     * 加载配置中心配置
     */
    private void loadKVStoreFromConfigCenter() {
        Map<String, ResponseMessage> messageMap = configCenterClient.queryAll(appName, profile);
        for (Map.Entry<String, ResponseMessage> entry : messageMap.entrySet()) {
            kvStore.put(entry.getKey(), new ConfigValue(entry.getValue().getValue(), entry.getValue().getVersion()));
        }

        kvStorePersister.asyncWrite(kvStore);
    }


    private void initKVStorePersister() {
        File folder = new File(this.localFilePath);
        try {
            if (!folder.exists()) {
                folder.mkdirs();
            }
        } catch (Exception e) {
            throw new RuntimeException("Create local file path failed!", e);
        }

        kvStorePersister = new KVStorePersisterFileImpl(new File(folder, appName + "_" + profile + ".data"));
    }

    private void addCustomEventHandler() {
        if (null == eventHandlers) {
            return;
        }
        for (AbstractEventHandler eventHandler : eventHandlers) {
            if (null == eventHandler.getKey()) {
                continue;
            }
            this.registerEventHandler(eventHandler);
        }
    }

    private void initEtcdEventWatcher() {
        eventWatcher = new EventWatcher(etcdClient, configCenterClient, handlerRegistry, kvStore, kvStorePersister,
                configAppInfo);
        eventWatcher.startWatch();
    }

    private void checkConfig() {
        checkNotNull(appName, "Filed appName is null!");
        checkState(!RESERVE_KEYS.contains(appName), appName + " is reserved key!");
        checkNotNull(localFilePath, "Filed localFilePath is null!");
        checkNotNull(profile, "Filed profile is null!");
        checkNotNull(etcdEndpoints, "Filed etcdEndpoints is null!");
    }

    /**
     * 注册监听器
     *
     * @param eventHandler The event handler of some key
     */
    @Override
    public void registerEventHandler(EventHandler eventHandler) {
        if (null == eventHandler || null == eventHandler.getKey()) {
            return;
        }
        handlerRegistry.registerCustomHandler(eventHandler.getKey(), eventHandler);
    }

    /**
     * 移除监听器
     *
     * @param eventHandler The registered event handler of some key
     */
    @Override
    public void removeEventHandler(EventHandler eventHandler) {
        if (null == eventHandler || null == eventHandler.getKey()) {
            return;
        }
        handlerRegistry.unregisterCustomHandler(eventHandler.getKey(), eventHandler);
    }

    /**
     * 注册监听器
     *
     * @param eventHandler The event handler of some key
     */
    @Override
    public <T extends ConfigEventHandler> void registerEventHandler(String key, T eventHandler) {
        if (null == eventHandler || null == key || "".equals(key.trim())) {
            return;
        }
        handlerRegistry.registerCustomHandler(key, eventHandler);
    }

    /**
     * 移除监听器
     *
     * @param eventHandler The registered event handler of some key
     */
    @Override
    public <T extends ConfigEventHandler> void removeEventHandler(String key, T eventHandler) {
        if (null == eventHandler || null == key || "".equals(key.trim())) {
            return;
        }
        handlerRegistry.unregisterCustomHandler(key, eventHandler);
    }

    /**
     * 获取配置信息中某个Key的值
     *
     * @param key The key
     * @return The value of the key
     */
    @Override
    public String getValue(String key) {
        ConfigValue configValue = kvStore.get(key);
        return null == configValue ? null : configValue.getValue();
    }

    /**
     * 获取配置信息中某个Key的值，如果为空则返回默认值
     *
     * @param key          The key
     * @param defaultValue The default value
     * @return The value of the key
     */
    @Override
    public String getValue(String key, String defaultValue) {
        ConfigValue configValue = kvStore.get(key);
        return null == configValue ? defaultValue : configValue.getValue();
    }

    /**
     * 批量获取配置信息
     *
     * @param keys The config keys
     * @return Matched config info
     */
    @Override
    public Map<String, String> batchGetValue(String... keys) {
        return transform(kvStore.batchGet(keys));
    }

    /**
     * 获取全部配置信息
     *
     * @return All config info
     */
    @Override
    public Map<String, String> getAllValue() {
        return transform(kvStore.getAll());
    }

    private Map<String, String> transform(Map<String, ConfigValue> values) {
        Map<String, String> result = Maps.newHashMap();
        for (Map.Entry<String, ConfigValue> entry : values.entrySet()) {
            result.put(entry.getKey(), entry.getValue().getValue());
        }
        return result;
    }

    @Override
    public void close() throws IOException {
        if (null != configCenterRegister) {
            try {
                configCenterRegister.close();
            } catch (Exception e) {
            }
        }
        if (null != configCenterClient) {
            try {
                configCenterClient.close();
            } catch (Exception e) {

            }
        }
        if (null != eventWatcher) {
            try {
                eventWatcher.close();
            } catch (Exception e) {
            }
        }
        if (null != etcdClient) {
            try {
                etcdClient.close();
            } catch (Exception e) {
            }
        }
    }
}
