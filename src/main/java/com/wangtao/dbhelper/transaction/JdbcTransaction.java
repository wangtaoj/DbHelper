package com.wangtao.dbhelper.transaction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author wangtao
 * Created at 2018/12/25 15:34
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
    private Integer isolationLevel;

    private DataSource dataSource;

    private Connection connection;

    public JdbcTransaction(DataSource dataSource, boolean autoCommit, TransactionIsolationLevel level) {
        this.dataSource = dataSource;
        this.autoCommit = autoCommit;
        this.isolationLevel = level != null ? level.getLevel() : null;
    }

    @Override
    public Connection getConnection() throws SQLException {
        if (connection != null) {
            connection = dataSource.getConnection();
            if (isolationLevel != null)
                connection.setTransactionIsolation(isolationLevel);
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
            resetAutoCommit();
            connection.close();
            if (logger.isDebugEnabled()) {
                logger.debug("Closing JDBC Connection{}", connection.hashCode());
            }
        }
    }

    private void setAutoCommit() {
        try {
            if (connection.getAutoCommit() != autoCommit) {
                connection.setAutoCommit(autoCommit);
                if (logger.isDebugEnabled()) {
                    logger.debug("setting value of autocommit to" + autoCommit +
                            " on connection[" + connection.hashCode() + "].");
                }
            }
        } catch (SQLException e) {
            throw new TransactionException("设置事务自动提交属性失败, 驱动不支持!", e);
        }
    }

    private void resetAutoCommit() {
        try {
            if (!connection.getAutoCommit()) {
                connection.setAutoCommit(true);
                if(logger.isDebugEnabled()) {
                    logger.debug("reset value of autocommit to true on connection[" + connection.hashCode() + "].");
                }
            }
        } catch (SQLException e) {
            throw new TransactionException("设置事务自动提交属性失败, 驱动不支持!", e);
        }
    }
}
