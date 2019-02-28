package com.wangtao.dbhelper.core;

import java.io.Closeable;
import java.sql.Connection;
import java.util.List;

/**
 * 操纵数据库的接口
 * Created by wangtao at 2018/12/20 16:18
 */
public interface SqlSession extends Closeable {


    <T> T selectOne(String statement);

    /**
     * 查询单条记录
     * @param statement (namespace + id)
     * @param parameter 参数
     * @param <T> 返回值类型
     * @return 结果对象
     */
    <T> T selectOne(String statement, Object parameter);

    <T> List<T> selectList(String statement);

    <T> List<T> selectList(String statement, Object parameter);

    /**
     * 查询多条记录
     * @param statement (namespace + id)
     * @param parameter 参数
     * @param rowBounds 分页对象
     * @param <T> 每行记录的类型
     * @return 结果列表
     */
    <T> List<T> selectList(String statement, Object parameter, RowBounds rowBounds);

    /**
     * 执行更新语句(update)
     * @param statement (namespace + id)
     * @param parameter 参数
     * @return 返回受影响的行数
     */
    int update(String statement, Object parameter);

    int update(String statement);

    /**
     * 执行插入语句(insert)
     * @param statement (namespace + id)
     * @param parameter 参数
     * @return 返回受影响的行数
     */
    int insert(String statement, Object parameter);

    int insert(String statement);

    /**
     * 执行删除语句(delete)
     * @param statement (namespace + id)
     * @param parameter 参数
     * @return 返回受影响的行数
     */
    int delete(String statement, Object parameter);

    int delete(String statement);

    void commit();

    void rollback();

    /**
     * @param type Mapper接口
     * @param <T> Mapper接口类型
     * @return 返回Mapper接口实例
     */
    <T> T getMapper(Class<T> type);

    /**
     * 获取连接对象
     * @return Connection对象
     */
    Connection getConnection();

    /**
     * 关闭会话.
     */
    void close();

    /**
     * 获取全局配置对象
     * @return Configuration对象
     */
    Configuration getConfiguration();
}
