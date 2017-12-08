package com.yy.cnt.recipes.db.hikaricp;

import java.util.Properties;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;

import javax.sql.DataSource;

import com.yy.cnt.recipes.db.common.BaseConfig;
import com.zaxxer.hikari.metrics.MetricsTrackerFactory;

public class HikariCPConfigBean extends BaseConfig {
    private static Boolean unitTest;

    // Properties changeable at runtime through the MBean
    //
    private Long connectionTimeout;
    private Long validationTimeout;
    private Long idleTimeout;
    private Long leakDetectionThreshold;
    private Long maxLifetime;
    private Integer maxPoolSize;
    private Integer minIdle;

    // Properties NOT changeable at runtime
    //
    private String catalog;
    private String connectionInitSql;
    private String connectionTestQuery;
    private String dataSourceClassName;
    private String dataSourceJndiName;
    private String driverClassName;
    private String jdbcUrl;
    private String password;
    private String poolName;
    private String transactionIsolationName;
    private String username;
    private Boolean isAutoCommit;
    private Boolean isReadOnly;
    private Boolean isInitializationFailFast;
    private Boolean isIsolateInternalQueries;
    private Boolean isRegisterMbeans;
    private Boolean isAllowPoolSuspension;
    private Properties dataSourceProperties;
    private ThreadFactory threadFactory;
    private MetricsTrackerFactory metricsTrackerFactory;
    private Object metricRegistry;
    private Object healthCheckRegistry;
    private Properties healthCheckProperties;

    public static Boolean isUnitTest() {
        return unitTest;
    }

    public static void setUnitTest(Boolean unitTest) {
        HikariCPConfigBean.unitTest = unitTest;
    }

    public Long getConnectionTimeout() {
        return connectionTimeout;
    }

    public void setConnectionTimeout(Long connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public Long getValidationTimeout() {
        return validationTimeout;
    }

    public void setValidationTimeout(Long validationTimeout) {
        this.validationTimeout = validationTimeout;
    }

    public Long getIdleTimeout() {
        return idleTimeout;
    }

    public void setIdleTimeout(Long idleTimeout) {
        this.idleTimeout = idleTimeout;
    }

    public Long getLeakDetectionThreshold() {
        return leakDetectionThreshold;
    }

    public void setLeakDetectionThreshold(Long leakDetectionThreshold) {
        this.leakDetectionThreshold = leakDetectionThreshold;
    }

    public Long getMaxLifetime() {
        return maxLifetime;
    }

    public void setMaxLifetime(Long maxLifetime) {
        this.maxLifetime = maxLifetime;
    }

    public Integer getMaxPoolSize() {
        return maxPoolSize;
    }

    public void setMaxPoolSize(Integer maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
    }

    public Integer getMinIdle() {
        return minIdle;
    }

    public void setMinIdle(Integer minIdle) {
        this.minIdle = minIdle;
    }

    public String getCatalog() {
        return catalog;
    }

    public void setCatalog(String catalog) {
        this.catalog = catalog;
    }

    public String getConnectionInitSql() {
        return connectionInitSql;
    }

    public void setConnectionInitSql(String connectionInitSql) {
        this.connectionInitSql = connectionInitSql;
    }

    public String getConnectionTestQuery() {
        return connectionTestQuery;
    }

    public void setConnectionTestQuery(String connectionTestQuery) {
        this.connectionTestQuery = connectionTestQuery;
    }

    public String getDataSourceClassName() {
        return dataSourceClassName;
    }

    public void setDataSourceClassName(String dataSourceClassName) {
        this.dataSourceClassName = dataSourceClassName;
    }

    public String getDataSourceJndiName() {
        return dataSourceJndiName;
    }

    public void setDataSourceJndiName(String dataSourceJndiName) {
        this.dataSourceJndiName = dataSourceJndiName;
    }

    public String getDriverClassName() {
        return driverClassName;
    }

    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }

    public String getJdbcUrl() {
        return jdbcUrl;
    }

    public void setJdbcUrl(String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPoolName() {
        return poolName;
    }

    public void setPoolName(String poolName) {
        this.poolName = poolName;
    }

    public String getTransactionIsolationName() {
        return transactionIsolationName;
    }

    public void setTransactionIsolationName(String transactionIsolationName) {
        this.transactionIsolationName = transactionIsolationName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Boolean isAutoCommit() {
        return isAutoCommit;
    }

    public void setAutoCommit(Boolean isAutoCommit) {
        this.isAutoCommit = isAutoCommit;
    }

    public Boolean isReadOnly() {
        return isReadOnly;
    }

    public void setReadOnly(Boolean isReadOnly) {
        this.isReadOnly = isReadOnly;
    }

    public Boolean isInitializationFailFast() {
        return isInitializationFailFast;
    }

    public void setInitializationFailFast(Boolean isInitializationFailFast) {
        this.isInitializationFailFast = isInitializationFailFast;
    }

    public Boolean isIsolateInternalQueries() {
        return isIsolateInternalQueries;
    }

    public void setIsolateInternalQueries(Boolean isIsolateInternalQueries) {
        this.isIsolateInternalQueries = isIsolateInternalQueries;
    }

    public Boolean isRegisterMbeans() {
        return isRegisterMbeans;
    }

    public void setRegisterMbeans(Boolean isRegisterMbeans) {
        this.isRegisterMbeans = isRegisterMbeans;
    }

    public Boolean isAllowPoolSuspension() {
        return isAllowPoolSuspension;
    }

    public void setAllowPoolSuspension(Boolean isAllowPoolSuspension) {
        this.isAllowPoolSuspension = isAllowPoolSuspension;
    }

    public Properties getDataSourceProperties() {
        return dataSourceProperties;
    }

    public void setDataSourceProperties(Properties dataSourceProperties) {
        this.dataSourceProperties = dataSourceProperties;
    }

    public ThreadFactory getThreadFactory() {
        return threadFactory;
    }

    public void setThreadFactory(ThreadFactory threadFactory) {
        this.threadFactory = threadFactory;
    }

    public MetricsTrackerFactory getMetricsTrackerFactory() {
        return metricsTrackerFactory;
    }

    public void setMetricsTrackerFactory(MetricsTrackerFactory metricsTrackerFactory) {
        this.metricsTrackerFactory = metricsTrackerFactory;
    }

    public Object getMetricRegistry() {
        return metricRegistry;
    }

    public void setMetricRegistry(Object metricRegistry) {
        this.metricRegistry = metricRegistry;
    }

    public Object getHealthCheckRegistry() {
        return healthCheckRegistry;
    }

    public void setHealthCheckRegistry(Object healthCheckRegistry) {
        this.healthCheckRegistry = healthCheckRegistry;
    }

    public Properties getHealthCheckProperties() {
        return healthCheckProperties;
    }

    public void setHealthCheckProperties(Properties healthCheckProperties) {
        this.healthCheckProperties = healthCheckProperties;
    }

    @Override
    public BaseConfig changeBackupConfig() {
        String backup = this.getBackupJdbcUrl();
        String current = this.getJdbcUrl();
        this.setJdbcUrl(backup);
        this.setBackupJdbcUrl(current);
        return this;
    }

    @Override
    public String toString() {
        return "HikariCPConfigBean [url=" + jdbcUrl + "]";
    }

    @Override
    public String getMasterJdbcUrl() {
        return this.jdbcUrl;
    }
}
