package com.yy.cnt.recipes.db.c3p0;

import com.yy.cnt.recipes.db.common.BaseConfig;

public class ComboPooledDataSourceConfigBean extends BaseConfig {
    protected String description;
    protected String driverClass;
    protected String jdbcUrl;
    protected String user;
    protected String password;
    protected Integer checkoutTimeout;
    protected Integer acquireIncrement;
    protected Integer acquireRetryAttempts;
    protected Integer acquireRetryDelay;
    protected Boolean autoCommitOnClose;
    protected String connectionTesterClassName;
    protected String automaticTestTable;
    protected Boolean forceIgnoreUnresolvedTransactions;
    protected Integer idleConnectionTestPeriod;
    protected Integer initialPoolSize;
    protected Integer maxIdleTime;
    protected Integer maxPoolSize;
    protected Integer maxStatements;
    protected Integer maxStatementsPerConnection;
    protected Integer minPoolSize;
    protected String overrideDefaultUser;
    protected String overrideDefaultPassword;
    protected Integer propertyCycle;
    protected Boolean breakAfterAcquireFailure;
    protected Boolean testConnectionOnCheckout;
    protected Boolean testConnectionOnCheckin;
    protected Boolean usesTraditionalReflectiveProxies;
    protected String preferredTestQuery;
    protected String userOverridesAsString;
    protected Integer maxAdministrativeTaskTime;
    protected Integer maxIdleTimeExcessConnections;
    protected Integer maxConnectionAge;
    protected String connectionCustomizerClassName;
    protected Integer unreturnedConnectionTimeout;
    protected Boolean debugUnreturnedConnectionStackTraces;
    protected String factoryClassLocation;

    public String getDescription() {
        return description;
    }

    public String getDriverClass() {
        return driverClass;
    }

    public String getJdbcUrl() {
        return jdbcUrl;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public Integer getCheckoutTimeout() {
        return checkoutTimeout;
    }

    public Integer getAcquireIncrement() {
        return acquireIncrement;
    }

    public Integer getAcquireRetryAttempts() {
        return acquireRetryAttempts;
    }

    public Integer getAcquireRetryDelay() {
        return acquireRetryDelay;
    }

    public Boolean isAutoCommitOnClose() {
        return autoCommitOnClose;
    }

    public String getConnectionTesterClassName() {
        return connectionTesterClassName;
    }

    public String getAutomaticTestTable() {
        return automaticTestTable;
    }

    public Boolean isForceIgnoreUnresolvedTransactions() {
        return forceIgnoreUnresolvedTransactions;
    }

    public Integer getIdleConnectionTestPeriod() {
        return idleConnectionTestPeriod;
    }

    public Integer getInitialPoolSize() {
        return initialPoolSize;
    }

    public Integer getMaxIdleTime() {
        return maxIdleTime;
    }

    public Integer getMaxPoolSize() {
        return maxPoolSize;
    }

    public Integer getMaxStatements() {
        return maxStatements;
    }

    public Integer getMaxStatementsPerConnection() {
        return maxStatementsPerConnection;
    }

    public Integer getMinPoolSize() {
        return minPoolSize;
    }

    public String getOverrideDefaultUser() {
        return overrideDefaultUser;
    }

    public String getOverrideDefaultPassword() {
        return overrideDefaultPassword;
    }

    public Integer getPropertyCycle() {
        return propertyCycle;
    }

    public Boolean isBreakAfterAcquireFailure() {
        return breakAfterAcquireFailure;
    }

    public Boolean isTestConnectionOnCheckout() {
        return testConnectionOnCheckout;
    }

    public Boolean isTestConnectionOnCheckin() {
        return testConnectionOnCheckin;
    }

    public Boolean isUsesTraditionalReflectiveProxies() {
        return usesTraditionalReflectiveProxies;
    }

    public String getPreferredTestQuery() {
        return preferredTestQuery;
    }

    public String getUserOverridesAsString() {
        return userOverridesAsString;
    }

    public Integer getMaxAdministrativeTaskTime() {
        return maxAdministrativeTaskTime;
    }

    public Integer getMaxIdleTimeExcessConnections() {
        return maxIdleTimeExcessConnections;
    }

    public Integer getMaxConnectionAge() {
        return maxConnectionAge;
    }

    public String getConnectionCustomizerClassName() {
        return connectionCustomizerClassName;
    }

    public Integer getUnreturnedConnectionTimeout() {
        return unreturnedConnectionTimeout;
    }

    public Boolean isDebugUnreturnedConnectionStackTraces() {
        return debugUnreturnedConnectionStackTraces;
    }

    public String getFactoryClassLocation() {
        return factoryClassLocation;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDriverClass(String driverClass) {
        this.driverClass = driverClass;
    }

    public void setJdbcUrl(String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setCheckoutTimeout(Integer checkoutTimeout) {
        this.checkoutTimeout = checkoutTimeout;
    }

    public void setAcquireIncrement(Integer acquireIncrement) {
        this.acquireIncrement = acquireIncrement;
    }

    public void setAcquireRetryAttempts(Integer acquireRetryAttempts) {
        this.acquireRetryAttempts = acquireRetryAttempts;
    }

    public void setAcquireRetryDelay(Integer acquireRetryDelay) {
        this.acquireRetryDelay = acquireRetryDelay;
    }

    public void setAutoCommitOnClose(Boolean autoCommitOnClose) {
        this.autoCommitOnClose = autoCommitOnClose;
    }

    public void setConnectionTesterClassName(String connectionTesterClassName) {
        this.connectionTesterClassName = connectionTesterClassName;
    }

    public void setAutomaticTestTable(String automaticTestTable) {
        this.automaticTestTable = automaticTestTable;
    }

    public void setForceIgnoreUnresolvedTransactions(Boolean forceIgnoreUnresolvedTransactions) {
        this.forceIgnoreUnresolvedTransactions = forceIgnoreUnresolvedTransactions;
    }

    public void setIdleConnectionTestPeriod(Integer idleConnectionTestPeriod) {
        this.idleConnectionTestPeriod = idleConnectionTestPeriod;
    }

    public void setInitialPoolSize(Integer initialPoolSize) {
        this.initialPoolSize = initialPoolSize;
    }

    public void setMaxIdleTime(Integer maxIdleTime) {
        this.maxIdleTime = maxIdleTime;
    }

    public void setMaxPoolSize(Integer maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
    }

    public void setMaxStatements(Integer maxStatements) {
        this.maxStatements = maxStatements;
    }

    public void setMaxStatementsPerConnection(Integer maxStatementsPerConnection) {
        this.maxStatementsPerConnection = maxStatementsPerConnection;
    }

    public void setMinPoolSize(Integer minPoolSize) {
        this.minPoolSize = minPoolSize;
    }

    public void setOverrideDefaultUser(String overrideDefaultUser) {
        this.overrideDefaultUser = overrideDefaultUser;
    }

    public void setOverrideDefaultPassword(String overrideDefaultPassword) {
        this.overrideDefaultPassword = overrideDefaultPassword;
    }

    public void setPropertyCycle(Integer propertyCycle) {
        this.propertyCycle = propertyCycle;
    }

    public void setBreakAfterAcquireFailure(Boolean breakAfterAcquireFailure) {
        this.breakAfterAcquireFailure = breakAfterAcquireFailure;
    }

    public void setTestConnectionOnCheckout(Boolean testConnectionOnCheckout) {
        this.testConnectionOnCheckout = testConnectionOnCheckout;
    }

    public void setTestConnectionOnCheckin(Boolean testConnectionOnCheckin) {
        this.testConnectionOnCheckin = testConnectionOnCheckin;
    }

    public void setUsesTraditionalReflectiveProxies(Boolean usesTraditionalReflectiveProxies) {
        this.usesTraditionalReflectiveProxies = usesTraditionalReflectiveProxies;
    }

    public void setPreferredTestQuery(String preferredTestQuery) {
        this.preferredTestQuery = preferredTestQuery;
    }

    public void setUserOverridesAsString(String userOverridesAsString) {
        this.userOverridesAsString = userOverridesAsString;
    }

    public void setMaxAdministrativeTaskTime(Integer maxAdministrativeTaskTime) {
        this.maxAdministrativeTaskTime = maxAdministrativeTaskTime;
    }

    public void setMaxIdleTimeExcessConnections(Integer maxIdleTimeExcessConnections) {
        this.maxIdleTimeExcessConnections = maxIdleTimeExcessConnections;
    }

    public void setMaxConnectionAge(Integer maxConnectionAge) {
        this.maxConnectionAge = maxConnectionAge;
    }

    public void setConnectionCustomizerClassName(String connectionCustomizerClassName) {
        this.connectionCustomizerClassName = connectionCustomizerClassName;
    }

    public void setUnreturnedConnectionTimeout(Integer unreturnedConnectionTimeout) {
        this.unreturnedConnectionTimeout = unreturnedConnectionTimeout;
    }

    public void setDebugUnreturnedConnectionStackTraces(Boolean debugUnreturnedConnectionStackTraces) {
        this.debugUnreturnedConnectionStackTraces = debugUnreturnedConnectionStackTraces;
    }

    public void setFactoryClassLocation(String factoryClassLocation) {
        this.factoryClassLocation = factoryClassLocation;
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
        return "ComboPooledDataSourceConfigBean [jdbcUrl=" + jdbcUrl + "]";
    }

    @Override
    public String getMasterJdbcUrl() {
        return this.jdbcUrl;
    }

}
