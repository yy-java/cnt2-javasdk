package com.yy.cnt.recipes.db.dbcp;

import com.yy.cnt.recipes.db.common.BaseConfig;
import org.apache.commons.pool.impl.GenericKeyedObjectPool;
import org.apache.commons.pool.impl.GenericObjectPool;



public class BasicDataSourceConfigBean extends BaseConfig {
    protected boolean defaultAutoCommit = true;
    protected Boolean defaultReadOnly = false;
    protected int defaultTransactionIsolation = -1;
    protected String defaultCatalog = null;
    protected String driverClassName = null;
    protected int maxActive = GenericObjectPool.DEFAULT_MAX_ACTIVE;
    protected int maxIdle = GenericObjectPool.DEFAULT_MAX_IDLE;
    protected int minIdle = GenericObjectPool.DEFAULT_MIN_IDLE;
    protected int initialSize = 0;
    protected long maxWait = GenericObjectPool.DEFAULT_MAX_WAIT;
    protected boolean poolPreparedStatements = false;
    protected int maxOpenPreparedStatements = GenericKeyedObjectPool.DEFAULT_MAX_TOTAL;
    protected boolean testOnBorrow = true;
    protected boolean testOnReturn = false;
    protected long timeBetweenEvictionRunsMillis = GenericObjectPool.DEFAULT_TIME_BETWEEN_EVICTION_RUNS_MILLIS;
    protected int numTestsPerEvictionRun = GenericObjectPool.DEFAULT_NUM_TESTS_PER_EVICTION_RUN;
    protected long minEvictableIdleTimeMillis = GenericObjectPool.DEFAULT_MIN_EVICTABLE_IDLE_TIME_MILLIS;
    protected boolean testWhileIdle = false;
    protected String password = null;
    protected String url = null;
    protected String username = null;
    protected String validationQuery = null;
    protected int validationQueryTimeout = -1;

    public boolean isDefaultAutoCommit() {
        return this.defaultAutoCommit;
    }

    public Boolean getDefaultReadOnly() {
        return this.defaultReadOnly;
    }

    public int getDefaultTransactionIsolation() {
        return this.defaultTransactionIsolation;
    }

    public String getDefaultCatalog() {
        return this.defaultCatalog;
    }

    public String getDriverClassName() {
        return this.driverClassName;
    }

    public int getMaxActive() {
        return this.maxActive;
    }

    public int getMaxIdle() {
        return this.maxIdle;
    }

    public int getMinIdle() {
        return this.minIdle;
    }

    public int getInitialSize() {
        return this.initialSize;
    }

    public long getMaxWait() {
        return this.maxWait;
    }

    public boolean isPoolPreparedStatements() {
        return this.poolPreparedStatements;
    }

    public int getMaxOpenPreparedStatements() {
        return this.maxOpenPreparedStatements;
    }

    public boolean isTestOnBorrow() {
        return this.testOnBorrow;
    }

    public boolean isTestOnReturn() {
        return this.testOnReturn;
    }

    public long getTimeBetweenEvictionRunsMillis() {
        return this.timeBetweenEvictionRunsMillis;
    }

    public int getNumTestsPerEvictionRun() {
        return this.numTestsPerEvictionRun;
    }

    public long getMinEvictableIdleTimeMillis() {
        return this.minEvictableIdleTimeMillis;
    }

    public boolean isTestWhileIdle() {
        return this.testWhileIdle;
    }

    public String getPassword() {
        return this.password;
    }

    public String getUrl() {
        return this.url;
    }

    public String getUsername() {
        return this.username;
    }

    public String getValidationQuery() {
        return this.validationQuery;
    }

    public int getValidationQueryTimeout() {
        return this.validationQueryTimeout;
    }

    public void setDefaultAutoCommit(boolean defaultAutoCommit) {
        this.defaultAutoCommit = defaultAutoCommit;
    }

    public void setDefaultReadOnly(Boolean defaultReadOnly) {
        this.defaultReadOnly = defaultReadOnly;
    }

    public void setDefaultTransactionIsolation(int defaultTransactionIsolation) {
        this.defaultTransactionIsolation = defaultTransactionIsolation;
    }

    public void setDefaultCatalog(String defaultCatalog) {
        this.defaultCatalog = defaultCatalog;
    }

    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }

    public void setMaxActive(int maxActive) {
        this.maxActive = maxActive;
    }

    public void setMaxIdle(int maxIdle) {
        this.maxIdle = maxIdle;
    }

    public void setMinIdle(int minIdle) {
        this.minIdle = minIdle;
    }

    public void setInitialSize(int initialSize) {
        this.initialSize = initialSize;
    }

    public void setMaxWait(long maxWait) {
        this.maxWait = maxWait;
    }

    public void setPoolPreparedStatements(boolean poolPreparedStatements) {
        this.poolPreparedStatements = poolPreparedStatements;
    }

    public void setMaxOpenPreparedStatements(int maxOpenPreparedStatements) {
        this.maxOpenPreparedStatements = maxOpenPreparedStatements;
    }

    public void setTestOnBorrow(boolean testOnBorrow) {
        this.testOnBorrow = testOnBorrow;
    }

    public void setTestOnReturn(boolean testOnReturn) {
        this.testOnReturn = testOnReturn;
    }

    public void setTimeBetweenEvictionRunsMillis(long timeBetweenEvictionRunsMillis) {
        this.timeBetweenEvictionRunsMillis = timeBetweenEvictionRunsMillis;
    }

    public void setNumTestsPerEvictionRun(int numTestsPerEvictionRun) {
        this.numTestsPerEvictionRun = numTestsPerEvictionRun;
    }

    public void setMinEvictableIdleTimeMillis(long minEvictableIdleTimeMillis) {
        this.minEvictableIdleTimeMillis = minEvictableIdleTimeMillis;
    }

    public void setTestWhileIdle(boolean testWhileIdle) {
        this.testWhileIdle = testWhileIdle;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setValidationQuery(String validationQuery) {
        this.validationQuery = validationQuery;
    }

    public void setValidationQueryTimeout(int validationQueryTimeout) {
        this.validationQueryTimeout = validationQueryTimeout;
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
