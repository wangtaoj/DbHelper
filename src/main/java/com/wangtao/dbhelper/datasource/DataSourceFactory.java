package com.wangtao.dbhelper.datasource;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * Created by wangtao at 2018/12/24 15:42
 */
public interface DataSourceFactory {
    /**
     * 获取数据库连接池
     * @return 连接池
     */
    DataSource getDataSource(Properties properties);
}
