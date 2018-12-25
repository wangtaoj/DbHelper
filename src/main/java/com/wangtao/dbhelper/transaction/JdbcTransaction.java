package com.wangtao.dbhelper.transaction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by wangtao at 2018/12/25 15:34
 */
public class JdbcTransaction implements Transaction {

    private static Logger logger = LoggerFactory.getLogger(JdbcTransaction.class);

    /**
     * 是否自动提交
     **/
    private boolean autoCommit;

    /**
     * 隔离级别
     **/
    private TransactionIsolationLevel level;

    private DataSource dataSource;

    private Connection connection;

    public JdbcTransaction(DataSource dataSource, boolean autoCommit, TransactionIsolationLevel level) {
        this.dataSource = dataSource;
        this.autoCommit = autoCommit;
        this.level = level;
    }

    @Override
    public Connection getConnection() throws SQLException {
        if (connection != null) {
            connection = dataSource.getConnection();
            if (level != null)
                connection.setTransactionIsolation(level.getLevel());
            connection.setAutoCommit(autoCommit);
        }
        return connection;
    }

    @Override
    public void commit() throws SQLException {
        if (connection != null && !connection.getAutoCommit()) {
            connection.commit();
            if (logger.isDebugEnabled()) {
                logger.debug("commit JDBC Connection{}", connection.hashCode());
            }
        }
    }

    @Override
    public void rollback() throws SQLException {
        if (connection != null && !connection.getAutoCommit()) {
            connection.rollback();
            if (logger.isDebugEnabled()) {
                logger.debug("rollback JDBC Connection{}", connection.hashCode());
            }
        }
    }

    @Override
    public void close() throws SQLException {
        if (connection != null) {
            if (logger.isDebugEnabled()) {
                logger.debug("Closing JDBC Connection{}", connection.hashCode());
            }
            connection.close();
        }
    }
}
