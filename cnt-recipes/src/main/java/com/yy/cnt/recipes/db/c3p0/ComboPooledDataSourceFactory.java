package com.yy.cnt.recipes.db.c3p0;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.yy.cnt.recipes.db.common.AbstractDataSourceFactory;

public class ComboPooledDataSourceFactory extends
        AbstractDataSourceFactory<ComboPooledDataSource, ComboPooledDataSourceConfigBean> {

    @Override
    public ComboPooledDataSource newDataSourceInstance() {
        return new ComboPooledDataSource();
    }

    @Override
    public ComboPooledDataSourceConfigBean newConfigBeanInstance() {
        return new ComboPooledDataSourceConfigBean();
    }

}
