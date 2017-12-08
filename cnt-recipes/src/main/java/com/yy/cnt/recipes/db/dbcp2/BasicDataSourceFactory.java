package com.yy.cnt.recipes.db.dbcp2;


import com.yy.cnt.recipes.db.common.AbstractDataSourceFactory;
import com.yy.cnt.recipes.db.dbcp.BasicDataSourceConfigBean;
import org.apache.commons.dbcp2.BasicDataSource;



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
