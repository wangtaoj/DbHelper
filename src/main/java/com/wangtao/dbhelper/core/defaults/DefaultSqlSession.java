package com.wangtao.dbhelper.core.defaults;

import com.wangtao.dbhelper.core.Configuration;
import com.wangtao.dbhelper.core.RowBounds;
import com.wangtao.dbhelper.core.SqlSession;
import com.wangtao.dbhelper.exception.TooManyResultException;

import java.io.IOException;
import java.util.List;

/**
 * @author wangtao
 * Created at 2019/1/16 15:46
 */
public class DefaultSqlSession implements SqlSession {

    private final Configuration configuration;

    public DefaultSqlSession(Configuration configuration) {
        this.configuration = configuration;
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
        if(list.isEmpty()) {
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
        return null;
    }

    @Override
    public int update(String statement, Object paramter) {
        return 0;
    }

    @Override
    public void commit() {

    }

    @Override
    public void rollback() {

    }

    @Override
    public <T> T getMapper(Class<T> type) {
        return null;
    }

    @Override
    public void close() throws IOException {

    }

    public Configuration getConfiguration() {
        return configuration;
    }
}
