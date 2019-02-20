package com.wangtao.dbhelper.transaction;

import com.wangtao.dbhelper.logging.Log;
import com.wangtao.dbhelper.logging.LogFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author wangtao
 * Created at 2018/12/25 15:34
 */
public class JdbcTransaction implements Transaction {

    private static Log logger = LogFactory.getLogger(JdbcTransaction.class);

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
        if (connection == null) {
            connection = dataSource.getConnection();
            if (logger.isDebugEnabled()) {
                logger.debug("Opening JDBC Connection");
            }
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
                logger.debug("Commit JDBC Connection[" + connection.hashCode() + "].");
            }
        }
    }

    @Override
    public void rollback() throws SQLException {
        if (connection != null && !connection.getAutoCommit()) {
            connection.rollback();
            if (logger.isDebugEnabled()) {
                logger.debug("Rollback JDBC Connection[" + connection.hashCode() + "].");
            }
        }
    }

    @Override
    public void close() throws SQLException {
        if (connection != null) {
            resetAutoCommit();
            connection.close();
            if (logger.isDebugEnabled()) {
                logger.debug("Closing JDBC Connection[" + connection.hashCode() + "]");
            }
        }
    }

    private void setAutoCommit() {
        try {
            if (connection.getAutoCommit() != autoCommit) {
                connection.setAutoCommit(autoCommit);
                if (logger.isDebugEnabled()) {
                    logger.debug("Setting value of autocommit to" + autoCommit +
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
                    logger.debug("Reset value of autocommit to true on connection[" + connection.hashCode() + "].");
                }
            }
        } catch (SQLException e) {
            throw new TransactionException("设置事务自动提交属性失败, 驱动不支持!", e);
        }
    }
}
