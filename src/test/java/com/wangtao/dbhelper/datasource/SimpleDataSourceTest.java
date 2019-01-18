package com.wangtao.dbhelper.datasource;

import com.wangtao.dbhelper.core.Resources;
import org.junit.Assert;
import org.junit.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.Properties;

/**
 * Created by wangtao at 2018/12/24 15:54
 */
public class SimpleDataSourceTest {

    @Test
    public void getConnection() throws Exception {
        Properties properties = new Properties();
        properties.load(Resources.getResourceAsReader("db.properties"));
        DataSource dataSource = new SimpleDataSourceFactory().getDataSource(properties);
        Connection connection = dataSource.getConnection();
        Assert.assertNotNull(connection);
        Assert.assertTrue(connection.getAutoCommit());
        Assert.assertEquals(Connection.TRANSACTION_REPEATABLE_READ, connection.getTransactionIsolation());
    }
}
