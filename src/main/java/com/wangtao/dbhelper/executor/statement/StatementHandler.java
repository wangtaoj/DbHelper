package com.wangtao.dbhelper.executor.statement;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * @author wangtao
 * Created at 2019/2/14 10:22
 */
public interface StatementHandler {

    <E> List<E> query(Statement statement) throws SQLException;

    int update(Statement statement) throws SQLException;

    Statement prepare(Connection connection) throws SQLException;

    void parameterize(Statement statement) throws SQLException;
}
