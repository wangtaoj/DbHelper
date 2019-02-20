package com.wangtao.dbhelper.executor;

import com.wangtao.dbhelper.core.RowBounds;
import com.wangtao.dbhelper.mapping.MappedStatement;

import java.sql.SQLException;
import java.util.List;

/**
 * @author wangtao
 * Created at 2019/2/13 16:18
 */
public interface Executor {

    <E> List<E> query(MappedStatement ms, Object parameter, RowBounds rowBounds) throws SQLException;

    int update(MappedStatement ms, Object parameter) throws SQLException;

    void commit() throws SQLException;

    void rollback() throws SQLException;

    void close() throws SQLException;
}
