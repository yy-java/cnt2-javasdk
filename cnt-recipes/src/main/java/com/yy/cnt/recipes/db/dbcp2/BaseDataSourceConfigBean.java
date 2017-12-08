package com.yy.cnt.recipes.db.dbcp2;

import java.util.List;
import java.util.Set;

import com.yy.cnt.recipes.db.common.BaseConfig;
import org.apache.commons.pool2.impl.AbandonedConfig;
import org.apache.commons.pool2.impl.BaseObjectPoolConfig;
import org.apache.commons.pool2.impl.GenericKeyedObjectPoolConfig;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;


public class BaseDataSourceConfigBean extends BaseConfig {

    private Boolean defaultAutoCommit = null;
    private transient Boolean defaultReadOnly = null;
    private volatile int defaultTransactionIsolation = -1;
    private Integer defaultQueryTimeout = null;
    private volatile String defaultCatalog = null;
    private boolean cacheState = true;
    private String driverClassName = null;
    private boolean lifo = BaseObjectPoolConfig.DEFAULT_LIFO;
    private int maxTotal = GenericObjectPoolConfig.DEFAULT_MAX_TOTAL;
    private int maxIdle = GenericObjectPoolConfig.DEFAULT_MAX_IDLE;
    private int minIdle = GenericObjectPoolConfig.DEFAULT_MIN_IDLE;
    private int initialSize = 0;
    private long maxWaitMillis = BaseObjectPoolConfig.DEFAULT_MAX_WAIT_MILLIS;
    private boolean poolPreparedStatements = false;
    private int maxOpenPreparedStatements = GenericKeyedObjectPoolConfig.DEFAULT_MAX_TOTAL;
    private boolean testOnCreate = false;
    private boolean testOnBorrow = true;
    private boolean testOnReturn = false;
    private long timeBetweenEvictionRunsMillis = BaseObjectPoolConfig.DEFAULT_TIME_BETWEEN_EVICTION_RUNS_MILLIS;
    private int numTestsPerEvictionRun = BaseObjectPoolConfig.DEFAULT_NUM_TESTS_PER_EVICTION_RUN;
    private long minEvictableIdleTimeMillis = BaseObjectPoolConfig.DEFAULT_MIN_EVICTABLE_IDLE_TIME_MILLIS;
    private long softMinEvictableIdleTimeMillis = BaseObjectPoolConfig.DEFAULT_SOFT_MIN_EVICTABLE_IDLE_TIME_MILLIS;
    private String evictionPolicyClassName = BaseObjectPoolConfig.DEFAULT_EVICTION_POLICY_CLASS_NAME;
    private boolean testWhileIdle = false;
    private volatile String password = null;
    private String url = null;
    private String username = null;
    private volatile String validationQuery = null;
    private volatile int validationQueryTimeout = -1;
    private volatile List<String> connectionInitSqls;
    private boolean accessToUnderlyingConnectionAllowed = false;
    private boolean logExpiredConnections = true;
    private long maxConnLifetimeMillis = -1;
    private String jmxName = null;
    private boolean enableAutoCommitOnReturn = true;
    private boolean rollbackOnReturn = true;
    private volatile Set<String> disconnectionSqlCodes;
    private AbandonedConfig abandonedConfig;
    private boolean fastFailValidation;

    public Boolean getDefaultAutoCommit() {
        return defaultAutoCommit;
    }

    public void setDefaultAutoCommit(Boolean defaultAutoCommit) {
        this.defaultAutoCommit = defaultAutoCommit;
    }

    public Boolean getDefaultReadOnly() {
        return defaultReadOnly;
    }

    public void setDefaultReadOnly(Boolean defaultReadOnly) {
        this.defaultReadOnly = defaultReadOnly;
    }

    public int getDefaultTransactionIsolation() {
        return defaultTransactionIsolation;
    }

    public void setDefaultTransactionIsolation(int defaultTransactionIsolation) {
        this.defaultTransactionIsolation = defaultTransactionIsolation;
    }

    public Integer getDefaultQueryTimeout() {
        return defaultQueryTimeout;
    }

    public void setDefaultQueryTimeout(Integer defaultQueryTimeout) {
        this.defaultQueryTimeout = defaultQueryTimeout;
    }

    public String getDefaultCatalog() {
        return defaultCatalog;
    }

    public void setDefaultCatalog(String defaultCatalog) {
        this.defaultCatalog = defaultCatalog;
    }

    public boolean isCacheState() {
        return cacheState;
    }

    public void setCacheState(boolean cacheState) {
        this.cacheState = cacheState;
    }

    public String getDriverClassName() {
        return driverClassName;
    }

    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }

    public boolean isLifo() {
        return lifo;
    }

    public void setLifo(boolean lifo) {
        this.lifo = lifo;
    }

    public int getMaxTotal() {
        return maxTotal;
    }

    public void setMaxTotal(int maxTotal) {
        this.maxTotal = maxTotal;
    }

    public int getMaxIdle() {
        return maxIdle;
    }

    public void setMaxIdle(int maxIdle) {
        this.maxIdle = maxIdle;
    }

    public int getMinIdle() {
        return minIdle;
    }

    public void setMinIdle(int minIdle) {
        this.minIdle = minIdle;
    }

    public int getInitialSize() {
        return initialSize;
    }

    public void setInitialSize(int initialSize) {
        this.initialSize = initialSize;
    }

    public long getMaxWaitMillis() {
        return maxWaitMillis;
    }

    public void setMaxWaitMillis(long maxWaitMillis) {
        this.maxWaitMillis = maxWaitMillis;
    }

    public boolean isPoolPreparedStatements() {
        return poolPreparedStatements;
    }

    public void setPoolPreparedStatements(boolean poolPreparedStatements) {
        this.poolPreparedStatements = poolPreparedStatements;
    }

    public int getMaxOpenPreparedStatements() {
        return maxOpenPreparedStatements;
    }

    public void setMaxOpenPreparedStatements(int maxOpenPreparedStatements) {
        this.maxOpenPreparedStatements = maxOpenPreparedStatements;
    }

    public boolean isTestOnCreate() {
        return testOnCreate;
    }

    public void setTestOnCreate(boolean testOnCreate) {
        this.testOnCreate = testOnCreate;
    }

    public boolean isTestOnBorrow() {
        return testOnBorrow;
    }

    public void setTestOnBorrow(boolean testOnBorrow) {
        this.testOnBorrow = testOnBorrow;
    }

    public boolean isTestOnReturn() {
        return testOnReturn;
    }

    public void setTestOnReturn(boolean testOnReturn) {
        this.testOnReturn = testOnReturn;
    }

    public long getTimeBetweenEvictionRunsMillis() {
        return timeBetweenEvictionRunsMillis;
    }

    public void setTimeBetweenEvictionRunsMillis(long timeBetweenEvictionRunsMillis) {
        this.timeBetweenEvictionRunsMillis = timeBetweenEvictionRunsMillis;
    }

    public int getNumTestsPerEvictionRun() {
        return numTestsPerEvictionRun;
    }

    public void setNumTestsPerEvictionRun(int numTestsPerEvictionRun) {
        this.numTestsPerEvictionRun = numTestsPerEvictionRun;
    }

    public long getMinEvictableIdleTimeMillis() {
        return minEvictableIdleTimeMillis;
    }

    public void setMinEvictableIdleTimeMillis(long minEvictableIdleTimeMillis) {
        this.minEvictableIdleTimeMillis = minEvictableIdleTimeMillis;
    }

    public long getSoftMinEvictableIdleTimeMillis() {
        return softMinEvictableIdleTimeMillis;
    }

    public void setSoftMinEvictableIdleTimeMillis(long softMinEvictableIdleTimeMillis) {
        this.softMinEvictableIdleTimeMillis = softMinEvictableIdleTimeMillis;
    }

    public String getEvictionPolicyClassName() {
        return evictionPolicyClassName;
    }

    public void setEvictionPolicyClassName(String evictionPolicyClassName) {
        this.evictionPolicyClassName = evictionPolicyClassName;
    }

    public boolean isTestWhileIdle() {
        return testWhileIdle;
    }

    public void setTestWhileIdle(boolean testWhileIdle) {
        this.testWhileIdle = testWhileIdle;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getValidationQuery() {
        return validationQuery;
    }

    public void setValidationQuery(String validationQuery) {
        this.validationQuery = validationQuery;
    }

    public int getValidationQueryTimeout() {
        return validationQueryTimeout;
    }

    public void setValidationQueryTimeout(int validationQueryTimeout) {
        this.validationQueryTimeout = validationQueryTimeout;
    }

    public List<String> getConnectionInitSqls() {
        return connectionInitSqls;
    }

    public void setConnectionInitSqls(List<String> connectionInitSqls) {
        this.connectionInitSqls = connectionInitSqls;
    }

    public boolean isAccessToUnderlyingConnectionAllowed() {
        return accessToUnderlyingConnectionAllowed;
    }

    public void setAccessToUnderlyingConnectionAllowed(boolean accessToUnderlyingConnectionAllowed) {
        this.accessToUnderlyingConnectionAllowed = accessToUnderlyingConnectionAllowed;
    }

    public boolean isLogExpiredConnections() {
        return logExpiredConnections;
    }

    public void setLogExpiredConnections(boolean logExpiredConnections) {
        this.logExpiredConnections = logExpiredConnections;
    }

    public long getMaxConnLifetimeMillis() {
        return maxConnLifetimeMillis;
    }

    public void setMaxConnLifetimeMillis(long maxConnLifetimeMillis) {
        this.maxConnLifetimeMillis = maxConnLifetimeMillis;
    }

    public String getJmxName() {
        return jmxName;
    }

    public void setJmxName(String jmxName) {
        this.jmxName = jmxName;
    }

    public boolean isEnableAutoCommitOnReturn() {
        return enableAutoCommitOnReturn;
    }

    public void setEnableAutoCommitOnReturn(boolean enableAutoCommitOnReturn) {
        this.enableAutoCommitOnReturn = enableAutoCommitOnReturn;
    }

    public boolean isRollbackOnReturn() {
        return rollbackOnReturn;
    }

    public void setRollbackOnReturn(boolean rollbackOnReturn) {
        this.rollbackOnReturn = rollbackOnReturn;
    }

    public Set<String> getDisconnectionSqlCodes() {
        return disconnectionSqlCodes;
    }

    public void setDisconnectionSqlCodes(Set<String> disconnectionSqlCodes) {
        this.disconnectionSqlCodes = disconnectionSqlCodes;
    }

    public AbandonedConfig getAbandonedConfig() {
        return abandonedConfig;
    }

    public void setAbandonedConfig(AbandonedConfig abandonedConfig) {
        this.abandonedConfig = abandonedConfig;
    }

    public boolean isFastFailValidation() {
        return fastFailValidation;
    }

    public void setFastFailValidation(boolean fastFailValidation) {
        this.fastFailValidation = fastFailValidation;
    }

    @Override
    public BaseConfig changeBackupConfig() {
        String backup = this.getBackupJdbcUrl();
        String current = this.getUrl();
        this.setUrl(backup);
        this.setBackupJdbcUrl(current);
        return this;
    }

    @Override
    public String toString() {
        return "BasicDataSourceConfigBean [url=" + url + "]";
    }

    @Override
    public String getMasterJdbcUrl() {
        return this.url;
    }
}
