package com.wangtao.dbhelper.mapping;

import com.wangtao.dbhelper.transaction.TransactionFactory;

import javax.sql.DataSource;

/**
 * @author wangtao
 * Created at 2019/1/18 10:29
 */
public class Environment {

    private final String id;

    private final TransactionFactory transactionFactory;

    private final DataSource dataSource;

    private Environment(String id, TransactionFactory transactionFactory, DataSource dataSource) {
        this.dataSource = dataSource;
        this.id = id;
        this.transactionFactory = transactionFactory;
    }

    public static Environment newEnvironment(String id, TransactionFactory transactionFactory, DataSource dataSource) {
        return new Environment(id, transactionFactory, dataSource);
    }

    public String getId() {
        return id;
    }

    public TransactionFactory getTransactionFactory() {
        return transactionFactory;
    }

    public DataSource getDataSource() {
        return dataSource;
    }
}
