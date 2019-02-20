package com.wangtao.dbhelper.executor.statement;

import com.wangtao.dbhelper.core.Configuration;
import com.wangtao.dbhelper.core.RowBounds;
import com.wangtao.dbhelper.executor.keygen.JDBCKeyGenerator;
import com.wangtao.dbhelper.executor.resultset.ResultSetHandler;
import com.wangtao.dbhelper.mapping.BoundSql;
import com.wangtao.dbhelper.mapping.MappedStatement;
import com.wangtao.dbhelper.mapping.ResultSetType;

import java.sql.*;
import java.util.List;

/**
 * @author wangtao
 * Created at 2019/2/14 10:24
 */
public class PreparedStatementHandler extends BaseStatementHandler {

    protected ResultSetHandler resultSetHandler;

    protected Configuration configuration;

    protected MappedStatement ms;

    protected BoundSql boundSql;

    public PreparedStatementHandler(MappedStatement ms, RowBounds rowBounds, BoundSql boundSql) {
        super(ms, rowBounds, boundSql);
    }

    @Override
    public <E> List<E> query(Statement statement) throws SQLException {
        PreparedStatement ps = (PreparedStatement) statement;
        ps.executeQuery();
        return resultSetHandler.handleResultSet(ps);
    }

    @Override
    public Statement initStatement(Connection connection) throws SQLException {
        String sql = boundSql.getSql();
        if (ms.getKeyGenerator() instanceof JDBCKeyGenerator) {
            if (ms.getKeyColumns() != null && ms.getKeyColumns().length > 0) {
                return connection.prepareStatement(sql, ms.getKeyColumns());
            } else {
                return connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            }
        } else if (ms.getResultSetType() == ResultSetType.DEFAULT) {
            return connection.prepareStatement(sql);
        } else {
            return connection.prepareStatement(sql, ms.getResultSetType().getValue(), ResultSet.CONCUR_READ_ONLY);
        }
    }
}
