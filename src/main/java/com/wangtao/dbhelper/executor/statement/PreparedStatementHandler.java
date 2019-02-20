package com.wangtao.dbhelper.executor.statement;

import com.wangtao.dbhelper.core.RowBounds;
import com.wangtao.dbhelper.executor.keygen.JDBCKeyGenerator;
import com.wangtao.dbhelper.executor.keygen.KeyGenerator;
import com.wangtao.dbhelper.mapping.MappedStatement;
import com.wangtao.dbhelper.mapping.ResultSetType;

import java.sql.*;
import java.util.List;

/**
 * @author wangtao
 * Created at 2019/2/14 10:24
 */
public class PreparedStatementHandler extends BaseStatementHandler {

    public PreparedStatementHandler(MappedStatement ms, RowBounds rowBounds, Object parameter) {
        super(ms, rowBounds, parameter);
    }

    @Override
    public <E> List<E> query(Statement statement) throws SQLException {
        PreparedStatement ps = (PreparedStatement) statement;
        ps.execute();
        return resultSetHandler.handleResultSet(ps);
    }

    @Override
    public int update(Statement statement) throws SQLException {
        PreparedStatement ps = (PreparedStatement) statement;
        ps.execute();
        int rows = ps.getUpdateCount();
        KeyGenerator keyGenerator = ms.getKeyGenerator();
        keyGenerator.processAfter(ms, statement, boundSql.getParameter());
        return rows;
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

    @Override
    public void parameterize(Statement statement) {
        parameterHandler.setParameters((PreparedStatement) statement);
    }
}
