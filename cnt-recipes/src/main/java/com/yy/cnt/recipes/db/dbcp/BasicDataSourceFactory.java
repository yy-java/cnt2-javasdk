package com.yy.cnt.recipes.db.dbcp;

import com.yy.cnt.recipes.db.common.AbstractDataSourceFactory;
import org.apache.commons.dbcp.BasicDataSource;



public class BasicDataSourceFactory extends AbstractDataSourceFactory<BasicDataSource, BasicDataSourceConfigBean> {

    @Override
    public BasicDataSource newDataSourceInstance() {
        return new BasicDataSource();
    }

    @Override
    public BasicDataSourceConfigBean newConfigBeanInstance() {
        return new BasicDataSourceConfigBean();
    }

}
