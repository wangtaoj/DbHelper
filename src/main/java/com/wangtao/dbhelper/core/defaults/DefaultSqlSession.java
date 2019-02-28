package com.wangtao.dbhelper.core.defaults;

import com.wangtao.dbhelper.core.Configuration;
import com.wangtao.dbhelper.core.ParamMap;
import com.wangtao.dbhelper.core.RowBounds;
import com.wangtao.dbhelper.core.SqlSession;
import com.wangtao.dbhelper.exception.PersistenceException;
import com.wangtao.dbhelper.exception.TooManyResultException;
import com.wangtao.dbhelper.executor.Executor;
import com.wangtao.dbhelper.executor.ExecutorException;
import com.wangtao.dbhelper.mapping.MappedStatement;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

/**
 * @author wangtao
 * Created at 2019/1/16 15:46
 */
public class DefaultSqlSession implements SqlSession {

    private final Configuration configuration;

    private final Executor executor;

    private final boolean autoCommit;

    /**
     * 初始值为false, 当update, delete, insert方法被调用时变成true
     * rollback, commit, close方法会将值重置为false
     *
     * 前提: 事务不是自动提交的情况, 即conn.setAutoCommit(false)
     * 1. 对于事务的回滚和提交:
     * 如果事务中有update操作(update, delete, insert), 如果没有手动提交
     * 那么session.close()会在关闭连接之前会先回滚事务, 如果手动调用了commit或者rollback方法
     * 那么只会关闭连接, 不会去管事务.
     *
     * 2. 对于事务中只有select操作, 如果没有手动提交或者回滚事务, close方法也只是关闭连接
     * 但是在close真正关闭前, 会重置连接得自动提交属性为true
     * 即调用conn.setAutoCommit(true), 此方法也会提交事务.
     *
     * 总结:
     * 对于setAutoCommit方法得解析, 如果此方法在事务期间被调用, 并且自动提交模式被改变, 那么会提交事务
     * 因此对于查询操作, 虽然没有手动commit, rollback, 但是事务会被自动提交.
     * 而对于update操作, 没有手动commit, rollback, 但是close方法回去回滚事务, 因此事务也是正常结束.
     * 最后调用setAutoCommit(true), 也只是重置连接而已, 事务已经结束.
     */
    private boolean update;

    public DefaultSqlSession(Configuration configuration, Executor executor, boolean autoCommit) {
        this.configuration = configuration;
        this.executor = executor;
        this.autoCommit = autoCommit;
        this.update = false;
    }

    @Override
    public <T> T selectOne(String statement) {
        return selectOne(statement, null);
    }

    @Override
    public <T> T selectOne(String statement, Object parameter) {
        List<T> list = selectList(statement, parameter);
        if (list.size() == 1) {
            return list.get(0);
        }
        if (list.isEmpty()) {
            return null;
        }
        throw new TooManyResultException(String.format("期待的是单个记录, 实际有%d条", list.size()));
    }

    @Override
    public <T> List<T> selectList(String statement) {
        return selectList(statement, null);
    }

    @Override
    public <T> List<T> selectList(String statement, Object parameter) {
        return selectList(statement, parameter, RowBounds.DEFAULT);
    }

    @Override
    public <T> List<T> selectList(String statement, Object parameter, RowBounds rowBounds) {
        try {
            MappedStatement ms = configuration.getMappedStatement(statement);
            return executor.query(ms, wrapCollection(parameter), rowBounds);
        } catch (Exception e) {
            throw new ExecutorException("Error query database.", e);
        }
    }

    @Override
    public int update(String statement, Object parameter) {
        try {
            update = true;
            MappedStatement ms = configuration.getMappedStatement(statement);
            return executor.update(ms, wrapCollection(parameter));
        } catch (Exception e) {
            throw new ExecutorException("Error update database.", e);
        }
    }

    @Override
    public int update(String statement) {
        return update(statement, null);
    }

    @Override
    public int insert(String statement, Object parameter) {
        return update(statement, parameter);
    }

    @Override
    public int insert(String statement) {
        return insert(statement, null);
    }

    @Override
    public int delete(String statement, Object parameter) {
        return update(statement, parameter);
    }

    @Override
    public int delete(String statement) {
        return delete(statement, null);
    }

    @Override
    public <T> T getMapper(Class<T> type) {
        return configuration.getMapper(type, this);
    }

    @Override
    public Connection getConnection() {
        try {
            return executor.getConnection();
        } catch (SQLException e) {
            throw new PersistenceException("Error getting connection." + e);
        }
    }

    @Override
    public void commit() {
        try {
            executor.commit(rollbackOrCommitNeed());
            update = false;
        } catch (SQLException e) {
            throw new ExecutorException("commit transaction fail.", e);
        }
    }

    @Override
    public void rollback() {
        try {
            executor.rollback(rollbackOrCommitNeed());
            update = false;
        } catch (SQLException e) {
            throw new ExecutorException("rollback transaction fail.", e);
        }
    }

    @Override
    public void close() {
        try {
            executor.close(rollbackOrCommitNeed());
            update = false;
        } catch (SQLException e) {
            throw new ExecutorException("close session fail.", e);
        }
    }

    @Override
    public Configuration getConfiguration() {
        return configuration;
    }

    private boolean rollbackOrCommitNeed() {
        return (!autoCommit && update);
    }

    private Object wrapCollection(Object parameter) {
        if (parameter instanceof Collection) {
            ParamMap map = new ParamMap();
            map.put("collection", parameter);
            if (parameter instanceof List) {
                map.put("list", parameter);
            }
            return map;
        } else if (parameter != null && parameter.getClass().isArray()) {
            ParamMap map = new ParamMap();
            map.put("array", parameter);
            return map;
        } else {
            return parameter;
        }
    }
}
