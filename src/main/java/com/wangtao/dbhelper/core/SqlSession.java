package com.wangtao.dbhelper.core;

import com.wangtao.dbhelper.exception.TooManyResultException;

import java.io.Closeable;
import java.util.List;

/**
 * 操纵数据库的接口
 * Created by wangtao at 2018/12/20 16:18
 */
public interface SqlSession extends Closeable {


    default <T> T selectOne(String statement) {
        return selectOne(statement, null);
    }

    /**
     * 查询单条记录
     * @param statement (namespace + id)
     * @param parameter 参数
     * @param <T> 返回值类型
     * @return 结果对象
     */
    default <T> T selectOne(String statement, Object parameter) {
        List<T> list = selectList(statement, parameter);
        if (list.size() == 1) {
            return list.get(0);
        }
        if(list.isEmpty()) {
            return null;
        }
        throw new TooManyResultException(String.format("期待的是单个记录, 实际有%d条", list.size()));
    }

    default <T> List<T> selectList(String statement) {
        return selectList(statement, null);
    }

    default <T> List<T> selectList(String statement, Object parameter) {
        return selectList(statement, parameter, RowBounds.DEFAULT);
    }

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
     * 执行更新语句(insert, update, select)
     * @param statement (namespace + id)
     * @param paramter 参数
     * @return 返回受影响的行数
     */
    int update(String statement, Object paramter);

    void commit();

    void rollback();

    /**
     * @param type Mapper接口
     * @param <T> Mapper接口类型
     * @return 返回Mapper接口实例
     */
    <T> T getMapper(Class<T> type);
}
