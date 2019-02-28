package com.wangtao.dbhelper.executor;

import com.wangtao.dbhelper.core.Configuration;
import com.wangtao.dbhelper.core.RowBounds;
import com.wangtao.dbhelper.executor.statement.StatementHandler;
import com.wangtao.dbhelper.logging.Log;
import com.wangtao.dbhelper.logging.jdbc.ConnectionLogger;
import com.wangtao.dbhelper.mapping.MappedStatement;
import com.wangtao.dbhelper.transaction.Transaction;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * @author wangtao
 * Created at 2019/2/13 16:20
 */
public class SimpleExecutor implements Executor {

    protected Transaction transaction;

    protected boolean closed;

    protected final Configuration configuration;

    public SimpleExecutor(Configuration configuration, Transaction transaction) {
        this.configuration = configuration;
        this.transaction = transaction;
        this.closed = false;
    }

    @Override
    public <E> List<E> query(MappedStatement ms, Object parameter, RowBounds rowBounds) throws SQLException {
        if (closed) {
            throw new ExecutorException("executor is closed!");
        }
        StatementHandler statementHandler = configuration.newStatementHandler(ms, rowBounds, parameter);
        try (Statement statement = prepareStatement(statementHandler, ms)) {
            return statementHandler.query(statement);
        }
    }

    @Override
    public int update(MappedStatement ms, Object parameter) throws SQLException {
        if (closed) {
            throw new ExecutorException("executor is closed!");
        }
        StatementHandler statementHandler = configuration.newStatementHandler(ms, null, parameter);
        try (Statement statement = prepareStatement(statementHandler, ms)) {
            return statementHandler.update(statement);
        }
    }

    @Override
    public Connection getConnection() throws SQLException {
        return transaction.getConnection();
    }

    private Statement prepareStatement(StatementHandler handler, MappedStatement ms) {
        try {
            Connection connection = getConnection(ms);
            Statement statement = handler.prepare(connection);
            handler.parameterize(statement);
            return statement;
        } catch (SQLException e) {
            throw new ExecutorException("Getting the statement occur error.", e);
        }
    }

    private Connection getConnection(MappedStatement ms) throws SQLException {
        Connection connection = transaction.getConnection();
        Log statementLog = ms.getStatementLog();
        if (statementLog.isDebugEnabled()) {
            return ConnectionLogger.newInstance(connection, statementLog);
        }
        return connection;
    }

    @Override
    public void commit(boolean required) throws SQLException {
        if (!closed && required) {
            transaction.commit();
        }
    }

    @Override
    public void rollback(boolean required) throws SQLException {
        if (!closed && required) {
            transaction.rollback();
        }
    }

    @Override
    public void close(boolean isNeedRollback) throws SQLException {
        if (!closed) {
            rollback(isNeedRollback);
            transaction.close();
            closed = true;
            transaction = null;
        }
    }
}
