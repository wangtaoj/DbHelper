package com.wangtao.dbhelper.transaction;

import javax.sql.DataSource;

/**
 * Created by wangtao at 2018/12/25 15:54
 */
public class JdbcTransactionFactory implements TransactionFactory {

    public JdbcTransaction newTransaction(DataSource dataSource, boolean autoCommit, TransactionIsolationLevel level) {
        return new JdbcTransaction(dataSource, autoCommit, level);
    }
}
