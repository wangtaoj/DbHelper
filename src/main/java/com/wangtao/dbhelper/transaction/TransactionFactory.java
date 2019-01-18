package com.wangtao.dbhelper.transaction;

import javax.sql.DataSource;

/**
 * @author wangtao
 * Created at 2019/1/17 19:46
 */
public interface TransactionFactory {

    JdbcTransaction newTransaction(DataSource dataSource, boolean autoCommit, TransactionIsolationLevel level);
}
