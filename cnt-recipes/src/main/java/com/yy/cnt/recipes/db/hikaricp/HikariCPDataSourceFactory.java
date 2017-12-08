package com.yy.cnt.recipes.db.hikaricp;


import com.yy.cnt.recipes.db.common.AbstractDataSourceFactory;
import com.zaxxer.hikari.HikariDataSource;

public class HikariCPDataSourceFactory extends AbstractDataSourceFactory<HikariDataSource, HikariCPConfigBean> {

    @Override
    public HikariDataSource newDataSourceInstance() {
        return new HikariDataSource();
    }

    @Override
    public HikariCPConfigBean newConfigBeanInstance() {
        return new HikariCPConfigBean();
    }

}
