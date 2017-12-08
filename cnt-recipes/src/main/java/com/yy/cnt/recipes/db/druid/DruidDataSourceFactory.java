package com.yy.cnt.recipes.db.druid;

import com.alibaba.druid.pool.DruidDataSource;
import com.yy.cnt.recipes.db.common.AbstractDataSourceFactory;

public class DruidDataSourceFactory extends AbstractDataSourceFactory<DruidDataSource, DruidConfigBean> {

    @Override
    public DruidDataSource newDataSourceInstance() {
        return new DruidDataSource();
    }

    @Override
    public DruidConfigBean newConfigBeanInstance() {
        return new DruidConfigBean();
    }

}
