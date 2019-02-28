package com.wangtao.dbhelper.executor;

import com.wangtao.dbhelper.core.RowBounds;
import com.wangtao.dbhelper.mapping.MappedStatement;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * @author wangtao
 * Created at 2019/2/13 16:18
 */
public interface Executor {

    /**
     * 执行查询语句
     * @param ms MappedStatement对象, 获取SQL信息及其它配置
     * @param parameter 参数
     * @param rowBounds 分页参数, 逻辑分页, 在内存中返回指定行记录
     * @param <E> 泛型参数
     * @return 结果列表
     * @throws SQLException 执行查询语句发生错误抛出异常
     */
    <E> List<E> query(MappedStatement ms, Object parameter, RowBounds rowBounds) throws SQLException;

    /**
     * 执行修改操作(insert, update, delete)
     * @param ms MappedStatement对象, 获取SQL信息及其它配置
     * @param parameter 参数
     * @return 返回受影响的行数
     * @throws SQLException 执行修改语句发生错误抛出异常
     */
    int update(MappedStatement ms, Object parameter) throws SQLException;

    /**
     * 获取连接对象
     * @return Connection对象
     * @throws SQLException 获取连接失败抛出异常
     */
    Connection getConnection() throws SQLException;

    /**
     * required为true时才会提交事务
     * @param required 是否需要提交事务
     * @throws SQLException 关闭事务失败抛出异常
     */
    void commit(boolean required) throws SQLException;

    /**
     * required为true时才会回滚事务
     * @param required 是否需要回滚事务
     * @throws SQLException 回滚事务失败抛出异常
     */
    void rollback(boolean required) throws SQLException;

    /**
     * 在关闭连接的时候, 如果isNeddRollback为true, 那么在关闭连接之前先回滚事务
     * @param isNeddRollback 关闭连接前是否需要回滚事务
     * @throws SQLException 关闭连接失败抛出异常
     */
    void close(boolean isNeddRollback) throws SQLException;
}
