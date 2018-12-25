package com.wangtao.dbhelper.transaction;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by wangtao at 2018/12/25 15:31
 */
public interface Transaction {

    /**
     * 获取连接
     */
    Connection getConnection() throws SQLException;

    /**
     * 提交事务
     */
    void commit() throws SQLException;

    /**
     * 回滚事务
     */
    void rollback() throws SQLException;

    /**
     * 关闭连接
     */
    void close() throws SQLException;
}
