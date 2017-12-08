package com.yy.cnt2.event;

import com.coreos.jetcd.Client;
import com.coreos.jetcd.Watch;
import com.coreos.jetcd.data.ByteSequence;
import com.coreos.jetcd.exception.EtcdException;
import com.coreos.jetcd.options.WatchOption;
import com.coreos.jetcd.watch.WatchEvent;
import com.coreos.jetcd.watch.WatchResponse;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.yy.cnt.api.event.ConfigEventHandler;
import com.yy.cnt.api.event.EventType;
import com.yy.cnt2.client.ConfigCenterClient;
import com.yy.cnt2.domain.ConfigAppInfo;
import com.yy.cnt2.domain.ConfigNode;
import com.yy.cnt2.domain.ConfigPublishInfo;
import com.yy.cnt2.grpc.api.ResponseMessage;
import com.yy.cnt2.store.ConfigValue;
import com.yy.cnt2.store.KVStore;
import com.yy.cnt2.store.KVStorePersister;
import com.yy.cnt2.util.Json;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;

import static com.coreos.jetcd.data.ByteSequence.fromString;
import static com.google.common.util.concurrent.MoreExecutors.shutdownAndAwaitTermination;
import static com.yy.cnt.api.event.EventType.DELETE;
import static com.yy.cnt.api.event.EventType.PUT;
import static com.yy.cnt2.util.PathHelper.extractKey;
import static java.util.concurrent.Executors.newFixedThreadPool;
import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * 监听etcd事件节点变化并加载最新配置，然后通知事件处理器
 *
 * @author xlg
 * @since 2017/7/7
 */
public class EventWatcher implements Closeable {
    private final Logger logger = LoggerFactory.getLogger(EventWatcher.class);

    private final ExecutorService watcherExecutorService = newFixedThreadPool(2,
            new ThreadFactoryBuilder().setDaemon(true).setNameFormat("cnt2-watcher-pool-%s").build());

    private final Client etcdClient;
    private final ConfigCenterClient configCenterClient;
    private final EventHandlerRegistry eventHandlerRegistry;
    private final KVStore kvStore;
    private final KVStorePersister kvStorePersister;
    private ConfigAppInfo configAppInfo;
    private Watch.Watcher watcher;

    public EventWatcher(Client etcdClient, ConfigCenterClient configCenterClient,
            EventHandlerRegistry eventHandlerRegistry, KVStore kvStore, KVStorePersister kvStorePersister,
            ConfigAppInfo appInfo) {
        this.etcdClient = etcdClient;
        this.configCenterClient = configCenterClient;
        this.eventHandlerRegistry = eventHandlerRegistry;
        this.configAppInfo = appInfo;
        this.kvStore = kvStore;
        this.kvStorePersister = kvStorePersister;
    }

    /**
     * 停止监听配置变更事件
     */
    public void stopWatch() {

        if (null == watcher) {
            return;
        }
        try {
            watcher.close();
        } catch (Exception e) {
        }

        shutdownAndAwaitTermination(watcherExecutorService, 3, SECONDS);
    }

    /**
     * 开始监听配置变更事件
     */
    public synchronized void startWatch() {

        if (null != watcher) {
            return;

        }

        Watch watchClient = etcdClient.getWatchClient();

        ByteSequence path = fromString(configAppInfo.getBasePath());
        WatchOption option = WatchOption.newBuilder().withPrefix(path).build();

        this.watcher = watchClient.watch(path, option);

        watcherExecutorService.execute(new EventProcessWorker());

        logger.info("Watch config path:{}", configAppInfo.getBasePath());
    }

    @Override
    public void close() throws IOException {
        stopWatch();
    }

    class EventProcessWorker implements Runnable {
        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    WatchResponse listen = watcher.listen();

                    for (WatchEvent event : listen.getEvents()) {
                        try {
                            processEvent(event);
                        } catch (Exception e) {
                            logger.warn("Process config event failed! Event:{}", event, e);
                        }
                    }

                } catch (Exception e) {
                    boolean isInterrupted = e instanceof InterruptedException || (e instanceof EtcdException && e
                            .getCause() instanceof InterruptedException);

                    if (isInterrupted) {
                        break;
                    } else {
                        logger.warn("Watch config failed! Path:{}", configAppInfo.getBasePath(), e);
                    }
                }
            }
        }
    }

    /**
     * 配置事件处理
     */
    private void processEvent(WatchEvent watchEvent) {
        String fullPath = watchEvent.getKeyValue().getKey().toStringUtf8();
        String value = watchEvent.getKeyValue().getValue().toStringUtf8();
        EventType eventType = convertEventType(watchEvent.getEventType());

        if (logger.isDebugEnabled()) {
            logger.debug("Process event! Path:{} Event:{} Value:{}", fullPath, eventType, value);
        }

        // Extract key
        String key = extractKey(fullPath, configAppInfo.getBasePath());

        if (null == key) {
            logger.warn("Key is null! Path:{} Base:{}", fullPath, configAppInfo.getBasePath());
            return;
        }

        if (eventType == DELETE) {
            kvStore.delete(key);
            kvStorePersister.asyncWrite(kvStore);

            customHandle(key, eventType, null);
        } else if (eventType == PUT) {
            boolean processResult = false;
            boolean needNotify = true;
            ConfigNode configNode = null;
            ConfigPublishInfo publishInfo = null;

            try {
                configNode = parseConfigNode(value);
                publishInfo = configNode.getPublishInfo();

                if (isGrayPublish(publishInfo) && !isGrayPublishTarget(publishInfo)) {
                    needNotify = false;
                    return;
                }

                ConfigValue localValue = kvStore.get(key);
                if (localValue != null && configNode.getVersion() <= localValue.getVersion()) {
                    needNotify = false;
                    return;
                }
                // Query new value
                String appName = configAppInfo.getAppName();
                String profile = configAppInfo.getProfile();
                com.yy.cnt2.grpc.api.ResponseMessage message = configCenterClient.queryKey(appName, profile, key, publishInfo.getVersion());

                String newValue = message.getValue();

                kvStore.put(key, new ConfigValue(newValue, message.getVersion()));
                kvStorePersister.asyncWrite(kvStore);

                processResult = customHandle(key, eventType, newValue);

            } catch (Exception e) {
                logger.warn("Process PUT event failed! Key:{} Value:{}", key, configNode);
            } finally {
                if (needNotify) {
                    notifyValueChangeResult(key, publishInfo.getPublishId(), configNode.getVersion(), processResult);
                }
            }
        }
    }

    /**
     * 配置更新结果上报
     */
    private void notifyValueChangeResult(String key, String deployId, long version, boolean success) {
        String nodeId = configAppInfo.getNodeId();
        String appName = configAppInfo.getAppName();
        String profile = configAppInfo.getProfile();

        configCenterClient.valueChangeResultNotify(nodeId, appName, profile, key, deployId, version, success);
    }

    /**
     * 配置变更处理
     */
    private boolean customHandle(String key, EventType event, String value) {
        boolean success = true;
        for (ConfigEventHandler eventHandler : eventHandlerRegistry.getCustomHandlers(key)) {
            try {
                eventHandler.handle(key, event, value);
            } catch (Exception e) {
                logger.warn("failed to process event handler:{} for key:{} with event:{}, error:{}",
                        eventHandler.getClass().getSimpleName(), key, event, e.getMessage());
                success = false;
            }
        }
        return success;
    }

    /**
     * 本次发布是不是灰度发布
     */
    private boolean isGrayPublish(ConfigPublishInfo publishInfo) {
        List<String> publishNodes = publishInfo.getPublishNodes();

        return null != publishNodes && !publishNodes.isEmpty();
    }

    /**
     * 本节点是否本次灰度发布的目标
     */
    private boolean isGrayPublishTarget(ConfigPublishInfo publishInfo) {
        String nodeId = configAppInfo.getNodeId();
        return publishInfo.getPublishNodes().contains(nodeId);
    }

    /**
     * 解析配置节点数据
     */
    private ConfigNode parseConfigNode(String value) {
        return Json.strToObj(value, ConfigNode.class);
    }

    /**
     * 事件转换
     */
    private EventType convertEventType(WatchEvent.EventType eventType) {
        switch (eventType) {
        case PUT:
            return PUT;
        case DELETE:
            return DELETE;
        default:
            return null;
        }
    }

}
