package com.yy.cnt2.register;

import com.coreos.jetcd.Client;
import com.coreos.jetcd.KV;
import com.coreos.jetcd.Lease;
import com.coreos.jetcd.Lease.KeepAliveListener;
import com.coreos.jetcd.Watch;
import com.coreos.jetcd.exception.EtcdException;
import com.coreos.jetcd.lease.LeaseKeepAliveResponse;
import com.coreos.jetcd.options.PutOption;
import com.coreos.jetcd.options.WatchOption;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.yy.cnt2.client.ConfigCenterClient;
import com.yy.cnt2.domain.RegisterNode;
import com.yy.cs.base.json.Json;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.coreos.jetcd.data.ByteSequence.fromString;
import static com.yy.cnt2.util.NetWorkUtils.getAppServerIp;
import static com.yy.cnt2.util.PathHelper.createRegisterPath;
import static com.yy.cnt2.util.ProgressUtil.getPid;
import static java.lang.System.currentTimeMillis;
import static java.util.concurrent.Executors.newCachedThreadPool;

/**
 * 注册服务到ConfigCenter & Etcd
 *
 * @author xlg
 * @since 2017/7/11
 */
public class ConfigCenterRegister implements Closeable {
    private final Logger logger = LoggerFactory.getLogger(ConfigCenterRegister.class);

    public static final int RECONNECT_DELAY_SECONDS = 5;

    private ExecutorService executorService = newCachedThreadPool(
            new ThreadFactoryBuilder().setNameFormat("register-pool-%s").setDaemon(true).build());

    private AtomicBoolean reconnecting = new AtomicBoolean(false);
    private Client etcdClient;
    private ConfigCenterClient configCenterClient;

    public ConfigCenterRegister(Client etcdClient, ConfigCenterClient configCenterClient) {
        this.etcdClient = etcdClient;
        this.configCenterClient = configCenterClient;
    }

    /**
     * 节点注册
     * <pre>
     * 1.注册到ConfigCenter获取节点Id
     * 2.将节点注册到Etcd
     * </pre>
     *
     * @param appName  App名称
     * @param profile  Profile名称
     * @param leaseTtl 租约Ttl
     * @return 节点Id
     * @throws Exception 如果注册失败则抛出异常
     */
    public String doRegister(String appName, String profile, long leaseTtl) throws Exception {

        String nodeId = registerToConfigCenter(appName, profile);

        registerToEtcd(nodeId, appName, profile, leaseTtl);

        return nodeId;
    }

    /**
     * 注册到配置中心
     */
    private String registerToConfigCenter(String appName, String profile) {
        return configCenterClient.registerClient(appName, profile);
    }

    /**
     * 注册到Etcd
     */
    private void registerToEtcd(String nodeId, String appName, String profile, long leaseTtl) throws Exception {
        //创建租约
        long leaseId = createLease(nodeId, appName, profile, leaseTtl);

        //创建租约节点
        createLeaseNode(nodeId, appName, profile, leaseId);

        //监听节点删除事件
        listenNodeDelete(nodeId, appName, profile, leaseTtl);
    }

    /**
     * 关创建租约节点
     */
    private void createLeaseNode(String nodeId, String appName, String profile, long leaseId) throws Exception {
        KV kvClient = etcdClient.getKVClient();

        int pid = getPid();
        String ip = getAppServerIp();
        long now = currentTimeMillis();

        String path = createRegisterPath(appName, profile, nodeId);
        String node = Json.ObjToStr(new RegisterNode(nodeId, appName, profile, pid, ip, now));
        PutOption option = PutOption.newBuilder().withLeaseId(leaseId).build();

        kvClient.put(fromString(path), fromString(node), option).get();
    }

    /**
     * 创建租约
     * <pre>
     * 1.获取租约
     * 2.租约保活
     * </pre>
     */
    private long createLease(final String nodeId, final String appName, final String profile, final long leaseTtl)
            throws Exception {

        Lease leaseClient = etcdClient.getLeaseClient();

        //获取租约Id
        long leaseId = leaseClient.grant(leaseTtl).get().getID();

        final KeepAliveListener keepAliveListener = leaseClient.keepAlive(leaseId);

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        LeaseKeepAliveResponse response = keepAliveListener.listen();

                        if (logger.isDebugEnabled()) {
                            logger.debug("LeaseKeepAlive => Lease:{} TTL:{}", response.getID(), response.getTTL());
                        }

                    } catch (InterruptedException e) {
                        break;
                    } catch (Exception e) {

                        logger.warn("Error happens when keep alive lease!,try to reconnect!", e);

                        keepAliveListener.close();

                        //reconnect(nodeId, appName, profile, leaseTtl);
                        break;
                    }
                }
            }
        });

        return leaseId;
    }

    /**
     * 重连
     */
    private void reconnect(final String nodeId, final String appName, final String profile, final long leaseTtl) {

        if (reconnecting.compareAndSet(false, true)) {

            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    while (!Thread.currentThread().isInterrupted()) {
                        try {
                            TimeUnit.SECONDS.sleep(RECONNECT_DELAY_SECONDS);

                            registerToEtcd(nodeId, appName, profile, leaseTtl);

                            reconnecting.set(false);

                            break;
                        } catch (InterruptedException e) {
                            break;
                        } catch (Exception e) {
                            logger.warn("Reconnect failed,try again! Node:{} App:{} Profile:{}", nodeId, appName,
                                    profile, e);
                        }
                    }
                }
            });
        }
    }

    /**
     * 监听节点删除事件，如果节点被删除则自动重连
     */
    private void listenNodeDelete(final String nodeId, final String appName, final String profile,
            final long leaseTtl) {

        Watch watchClient = etcdClient.getWatchClient();

        String path = createRegisterPath(appName, profile, nodeId);
        WatchOption option = WatchOption.newBuilder().withNoPut(true).build();

        final Watch.Watcher watcher = watchClient.watch(fromString(path), option);

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        watcher.listen();

                        logger.info("Node is deleted,try to register again! Node:{} App:{} Profile:{}", nodeId, appName,
                                profile);

                        //reconnect(nodeId, appName, profile, leaseTtl);

                    } catch (Exception e) {
                        boolean isInterrupted = e instanceof InterruptedException || (e instanceof EtcdException && e
                                .getCause() instanceof InterruptedException);

                        if (isInterrupted) {
                            break;
                        } else {
                            logger.warn("Watch node error! Node:{} App:{} Profile:{}", nodeId, appName, profile, e);
                        }
                    }
                }
            }
        });
    }

    @Override
    public void close() throws IOException {
        MoreExecutors.shutdownAndAwaitTermination(this.executorService, 3, TimeUnit.SECONDS);
    }
}
