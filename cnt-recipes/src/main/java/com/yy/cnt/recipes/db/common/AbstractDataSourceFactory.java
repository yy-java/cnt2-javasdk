package com.yy.cnt.recipes.db.common;

import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import javax.sql.DataSource;

import com.yy.cnt.api.IControlCenterService;
import com.yy.cnt.api.event.EventHandler;
import com.yy.cnt.api.event.EventType;
import com.yy.cnt.recipes.db.serivce.PublicConfigService;
import com.yy.cnt.recipes.db.tester.ConnectionTester;
import com.yy.cnt.recipes.db.tester.mysql.MySQLConnectionTester;
import com.yy.cnt.recipes.utils.BeanCopyUtils;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.ThreadFactoryBuilder;


public abstract class AbstractDataSourceFactory<T extends DataSource, V>
        implements FactoryBean<DataSource>, InitializingBean {

    public static final int DEFAULT_COUNT_BACKUP_URL_SWITCH = 2;

    protected static final Logger LOG = LoggerFactory.getLogger(AbstractDataSourceFactory.class);
    protected static final Class<DataSource> objectType = DataSource.class;

    protected V config;
    protected final DataSourceWrapper<T> container = new DataSourceWrapper<>();
    protected T oldDataSource;
    protected IControlCenterService controlCenterService;
    protected String propertyKey;
    private String oldConfigData;
    protected volatile boolean destroyed = false;
    protected boolean lazyInitialize = false;
    protected volatile boolean initFinished = false;

    protected ReentrantLock initLock = new ReentrantLock();
    protected Runnable healthCommand;
    protected volatile ScheduledFuture<?> healthScheduledFuture;

    protected int backupSwitchAfterFails = DEFAULT_COUNT_BACKUP_URL_SWITCH;

    protected V defaultConfig;

    protected PublicConfigService publicConfigSerivce;

    private static final ScheduledThreadPoolExecutor checkTaskExecutor = new ScheduledThreadPoolExecutor(2,
            new ThreadFactoryBuilder().setDaemon(true).setNameFormat("datasource-check-pool-%d").build());

    private ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat("datasource-check-pool-%d").build();
    private ExecutorService parentExecutor = Executors.newCachedThreadPool(threadFactory);
    private ListeningExecutorService executorService = MoreExecutors.listeningDecorator(parentExecutor);

    private static final ConnectionTester CONN_TESTER = new MySQLConnectionTester();
    private static final int HEALTH_CHECK_PERIOD = 60 * 1000;

    public void destroy() throws Exception {
        destroyed = true;
        if (null != container.getCurrentDataSource()) {
            container.close(container.getCurrentDataSource());
        }
        if (null != oldDataSource) {
            container.close(oldDataSource);
        }
    }

    public abstract T newDataSourceInstance();

    public abstract V newConfigBeanInstance();

    public boolean isValid(int wait) {
        boolean valid = true;
        ListenableFuture<Boolean> b = executorService.submit(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                boolean testError = false;
                try {
                    Connection c = container.getConnection();
                    testError = !CONN_TESTER.isActivation(c);
                    c.close();
                } catch (Exception e) {
                    testError = true;
                }
                return !testError;
            }

        });
        try {
            valid = b.get(wait, TimeUnit.SECONDS);
        } catch (Exception e) {
            valid = false;
        }
        return valid;
    }

    protected void init(V config) throws IllegalAccessException, InvocationTargetException {
        initLock.lock();
        try {
            if (null != container.getCurrentDataSource()) {
                oldDataSource = container.getCurrentDataSource();
            }
            T init = newDataSourceInstance();
            BeanCopyUtils.copyNoneNullProperties(init, config);

            try {
                // 快速初始化，让连接池先建立一部分连接后，再替换
                Connection c = init.getConnection();
                c.close();
            } catch (SQLException e1) {
                LOG.error("init database connection error " + config.toString(), e1);
            }

            if (this.config != config) {
                this.config = config;
            }
            container.setCurrentDataSource(init);
            try {
                if (null != oldDataSource) {
                    container.close(oldDataSource);
                }
            } catch (Exception e) {
                LOG.warn("close oldDataSource error  :", e);
            }

            if (this.config instanceof BaseConfig) {
                if (StringUtils.isNotBlank(((BaseConfig) this.config).getBackupJdbcUrl())) {
                    if (null == healthCommand) {
                        healthCommand = new DataSourceHealthChecker(this);
                    }
                    healthScheduledFuture = checkTaskExecutor.scheduleAtFixedRate(healthCommand, HEALTH_CHECK_PERIOD,
                            HEALTH_CHECK_PERIOD, TimeUnit.SECONDS);
                } else if (healthScheduledFuture != null && !healthScheduledFuture.isCancelled()) {
                    try {
                        healthScheduledFuture.cancel(true);
                        checkTaskExecutor.purge();
                    } catch (Exception e) {

                    }
                }
            }

        } finally {
            initLock.unlock();
        }
    }

    public void init() throws Exception {
        if (null == controlCenterService || null == propertyKey) {
            throw new IllegalArgumentException("null of controlCenterService or null of propertyKey is illegal!!");
        }

        publicConfigSerivce = PublicConfigService.getService(controlCenterService);

        if (null == config) {
            String node = controlCenterService.getValue(propertyKey);
            this.config = parseConfig(node);
            if (null == config) {
                throw new IllegalArgumentException("no config for " + propertyKey);
            }
            oldConfigData = node;
        }
        controlCenterService.registerEventHandler(new ConfigChangeCallback());
        if (!lazyInitialize) {
            init(this.config);
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        init();
    }

    private class ConfigChangeCallback implements EventHandler {
        @Override
        public void handle(String key, EventType event, String value) {
            if (destroyed) {
                return;
            }
            if (event != EventType.PUT) {
                return;
            }
            try {
//                Node node = controlCenterService.getConfig(propertyKey);
//                if (null != oldConfigData && oldConfigData.equals(node.getValue())) {
//                    return;
//                }
//                LOG.info("new Data :" + node.getValue() + ", old Data :" + oldConfigData);
                String newValue = controlCenterService.getValue(propertyKey);
                V config = parseConfig(newValue);
                init(config);
            } catch (Exception e) {
                LOG.error("node change refresh datasource error :", e);
            }
        }

        @Override
        public String getKey() {
            return  propertyKey;
        }
    }

    private V parseConfig(String node) throws IOException, IllegalAccessException, InvocationTargetException {
        V bean = newConfigBeanInstance();
        if (null != config) {
            BeanUtils.copyProperties(bean, config); // 保持已设置的属性不变化
        } else if (null != defaultConfig) {
            BeanUtils.copyProperties(bean, defaultConfig); // 注入默认配置
        }

        String output = publicConfigSerivce.parsePublicConfig(node, this);
        Properties property = new Properties();
        StringReader sr = new StringReader(output);
        property.load(sr);
        sr.close();

        LOG.debug("parsed config:" + property);
        BeanUtils.copyProperties(bean, property); // 更新zookeeper配置的属性

        return bean;
    }

    @Override
    public DataSource getObject() throws Exception {
        if (lazyInitialize && !initFinished) {
            init(this.config);
        }
        return container;
    }

    @Override
    public Class<?> getObjectType() {
        return objectType;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    public IControlCenterService getControlCenterService() {
        return controlCenterService;
    }

    public String getPropertyKey() {
        return propertyKey;
    }

    public void setControlCenterService(IControlCenterService controlCenterService) {
        this.controlCenterService = controlCenterService;
    }

    public void setPropertyKey(String propertyKey) {
        this.propertyKey = propertyKey;
    }

    public V getConfig() {
        return config;
    }

    public V getDefaultConfig() {
        return defaultConfig;
    }

    public void setDefaultConfig(V defaultConfig) {
        this.defaultConfig = defaultConfig;
    }

    public int getBackupSwitchAfterFails() {
        return backupSwitchAfterFails;
    }

    public void setBackupSwitchAfterFails(int backupSwitchAfterFails) {
        this.backupSwitchAfterFails = backupSwitchAfterFails;
    }

    public boolean isLazyInitialize() {
        return lazyInitialize;
    }

    public void setLazyInitialize(boolean lazyInitialize) {
        this.lazyInitialize = lazyInitialize;
    }

}
