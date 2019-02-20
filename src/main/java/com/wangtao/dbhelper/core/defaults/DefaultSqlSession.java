package com.wangtao.dbhelper.core.defaults;

import com.wangtao.dbhelper.core.Configuration;
import com.wangtao.dbhelper.core.ParamMap;
import com.wangtao.dbhelper.core.RowBounds;
import com.wangtao.dbhelper.core.SqlSession;
import com.wangtao.dbhelper.exception.TooManyResultException;
import com.wangtao.dbhelper.executor.Executor;
import com.wangtao.dbhelper.executor.ExecutorException;
import com.wangtao.dbhelper.mapping.MappedStatement;

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

    public DefaultSqlSession(Configuration configuration, Executor executor) {
        this.configuration = configuration;
        this.executor = executor;
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
        MappedStatement ms = configuration.getMappedStatement(statement);
        try {
            return executor.query(ms, wrapCollection(parameter), rowBounds);
        } catch (Exception e) {
            throw new ExecutorException("Error query database.", e);
        }
    }

    @Override
    public int update(String statement, Object parameter) {
        MappedStatement ms = configuration.getMappedStatement(statement);
        try {
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
    public void commit() {
        try {
            executor.commit();
        } catch (SQLException e) {
            throw new ExecutorException("commit transaction fail.", e);
        }
    }

    @Override
    public void rollback() {
        try {
            executor.rollback();
        } catch (SQLException e) {
            throw new ExecutorException("rollback transaction fail.", e);
        }
    }

    @Override
    public <T> T getMapper(Class<T> type) {
        return null;
    }

    @Override
    public void close() {
        try {
            executor.close();
        } catch (SQLException e) {
            throw new ExecutorException("close session fail.", e);
        }
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    private Object wrapCollection(Object parameter) {
        if(parameter instanceof Collection) {
            ParamMap map = new ParamMap();
            map.put("collection", parameter);
            if (parameter instanceof List) {
                map.put("list", parameter);
            }
            return map;
        } else if(parameter != null && parameter.getClass().isArray()) {
            ParamMap map = new ParamMap();
            map.put("array", parameter);
            return map;
        } else {
            return parameter;
        }
    }
}
