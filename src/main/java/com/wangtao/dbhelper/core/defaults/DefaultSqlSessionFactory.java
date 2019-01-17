package com.wangtao.dbhelper.core.defaults;

import com.wangtao.dbhelper.core.Configuration;
import com.wangtao.dbhelper.core.SqlSession;
import com.wangtao.dbhelper.core.SqlSessionFactory;

/**
 * @author wangtao
 * Created at 2019/1/16 15:46
 */
public class DefaultSqlSessionFactory implements SqlSessionFactory {

    private final Configuration configuration;

    public DefaultSqlSessionFactory(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public SqlSession openSqlSession() {
        return new DefaultSqlSession(configuration);
    }

    public Configuration getConfiguration() {
        return configuration;
    }
}
