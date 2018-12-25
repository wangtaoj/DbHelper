package com.wangtao.dbhelper.datasource;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * Created by wangtao at 2018/12/25 14:10
 */
public class PoolDataSourceFactory implements DataSourceFactory {

    @Override
    public DataSource getDataSource(Properties properties) {
        return new PoolDataSource(properties);
    }

    public static DataSourceFactory instance() {
        return Holder.getInstance();
    }

    private static class Holder {
        private static DataSourceFactory dataSourceFactory = new PoolDataSourceFactory();

        static DataSourceFactory getInstance() {
            return dataSourceFactory;
        }
    }
}
