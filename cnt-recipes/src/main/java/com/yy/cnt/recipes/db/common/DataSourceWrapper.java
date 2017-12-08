package com.yy.cnt.recipes.db.common;

import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

import javax.sql.DataSource;

import com.yy.cnt.recipes.utils.ReflectUtils;

public class DataSourceWrapper<T extends DataSource> implements DataSource {

    private volatile T currentDataSource;

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return currentDataSource.getLogWriter();
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        currentDataSource.setLogWriter(out);
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        currentDataSource.setLoginTimeout(seconds);
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return currentDataSource.getLoginTimeout();
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return currentDataSource.getParentLogger();
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return currentDataSource.unwrap(iface);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return currentDataSource.isWrapperFor(iface);
    }

    @Override
    public Connection getConnection() throws SQLException {
        return currentDataSource.getConnection();
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return currentDataSource.getConnection(username, password);
    }

    public T getCurrentDataSource() {
        return currentDataSource;
    }

    public void setCurrentDataSource(T currentDataSource) {
        this.currentDataSource = currentDataSource;
    }

    public void close(T targetDataSource) throws Exception {
        try {
            Method method = ReflectUtils.getClassMethod(targetDataSource.getClass(), "close", true);
            if (null != method) {
                method.invoke(targetDataSource);
            }
        } catch (Exception e) {
            throw e;
        }
    }

}
