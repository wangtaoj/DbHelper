package com.wangtao.dbhelper.core.defaults;

import com.wangtao.dbhelper.core.Configuration;
import com.wangtao.dbhelper.core.SqlSession;
import com.wangtao.dbhelper.core.SqlSessionFactory;
import com.wangtao.dbhelper.executor.Executor;
import com.wangtao.dbhelper.executor.SimpleExecutor;
import com.wangtao.dbhelper.mapping.Environment;
import com.wangtao.dbhelper.transaction.Transaction;
import com.wangtao.dbhelper.transaction.TransactionIsolationLevel;

import javax.sql.DataSource;

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
        return openSqlSession(false, null);
    }

    @Override
    public SqlSession openSqlSession(boolean autoCommit) {
        return openSqlSession(autoCommit, null);
    }

    @Override
    public SqlSession openSqlSession(boolean autoCommit, TransactionIsolationLevel level) {
        return null;
    }

    private SqlSession openSqlSessionFromDataSource(boolean autoCommit, TransactionIsolationLevel level) {
        Environment environment = configuration.getEnvironment();
        DataSource dataSource = environment.getDataSource();
        Transaction transaction = environment.getTransactionFactory().newTransaction(dataSource, autoCommit, level);
        Executor executor = new SimpleExecutor(transaction);
        return new DefaultSqlSession(configuration, executor);
    }

    public Configuration getConfiguration() {
        return configuration;
    }
}
