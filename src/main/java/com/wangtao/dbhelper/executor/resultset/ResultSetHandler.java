package com.wangtao.dbhelper.executor.resultset;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * @author wangtao
 * Created at 2019/2/14 10:27
 */
public interface ResultSetHandler {

    <E> List<E> handleResultSet(Statement statement) throws SQLException;
}
