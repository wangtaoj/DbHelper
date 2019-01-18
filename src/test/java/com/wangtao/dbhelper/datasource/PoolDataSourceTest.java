package com.wangtao.dbhelper.datasource;

import com.wangtao.dbhelper.core.Resources;
import org.junit.Assert;
import org.junit.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.Properties;

/**
 * Created by wangtao at 2018/12/25 14:09
 */
public class PoolDataSourceTest {

    @Test
    public void connection() throws Exception {
        Properties properties = new Properties();
        properties.load(Resources.getResourceAsReader("db.properties"));
        DataSource dataSource = new PoolDataSourceFactory().getDataSource(properties);
        Connection connection = dataSource.getConnection();
        Assert.assertNotNull(connection);
        Assert.assertTrue(connection.getAutoCommit());
        connection.close();
    }
}
