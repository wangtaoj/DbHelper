package com.wangtao.dbhelper.executor.statement;

import com.wangtao.dbhelper.core.Configuration;
import com.wangtao.dbhelper.core.RowBounds;
import com.wangtao.dbhelper.executor.resultset.ResultSetHandler;
import com.wangtao.dbhelper.mapping.BoundSql;
import com.wangtao.dbhelper.mapping.MappedStatement;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author wangtao
 * Created at 2019/2/20 11:07
 */
public abstract class BaseStatementHandler implements StatementHandler {

    protected ResultSetHandler resultSetHandler;

    protected Configuration configuration;

    protected MappedStatement ms;

    protected BoundSql boundSql;

    public BaseStatementHandler(MappedStatement ms, RowBounds rowBounds, BoundSql boundSql) {
        this.ms = ms;
        this.configuration = ms.getConfiguration();
        this.resultSetHandler = configuration.newResultSetHandler(ms, rowBounds);
        this.boundSql = boundSql;
    }

    @Override
    public Statement prepare(Connection connection) throws SQLException{
        Statement statement = initStatement(connection);
        Integer fetchSize = ms.getFetchSize();
        Integer queryTimeout = ms.getTimeout();
        if(fetchSize != null && fetchSize > 0) {
            statement.setFetchSize(fetchSize);
        }
        if(queryTimeout != null && queryTimeout > 0) {
            statement.setQueryTimeout(queryTimeout);
        }
        return statement;
    }

    protected abstract Statement initStatement(Connection connection) throws SQLException;
}
