package com.wangtao.dbhelper.datasource;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * Created by wangtao at 2018/12/24 15:50
 */
public class SimpleDataSourceFactory implements DataSourceFactory {

    private SimpleDataSourceFactory() {}

    @Override
    public DataSource getDataSource(Properties properties) {
        return new SimpleDataSource(properties);
    }

    public static SimpleDataSourceFactory instance() {
        return Holder.getInstance();
    }

    private static class Holder {
        private static SimpleDataSourceFactory factory = new SimpleDataSourceFactory();

        static SimpleDataSourceFactory getInstance() {
            return factory;
        }
    }
}
