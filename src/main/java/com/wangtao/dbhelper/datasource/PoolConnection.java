package com.wangtao.dbhelper.datasource;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.sql.Connection;

/**
 * Created by wangtao at 2018/12/20 19:25
 */
public class PoolConnection implements InvocationHandler {

    private static final String CLOSE = "close";

    private Connection realConnection;

    private PoolDataSource dataSource;

    public PoolConnection(Connection connection, PoolDataSource dataSource) {
        this.realConnection = connection;
        this.dataSource = dataSource;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if(method.getName().equals(CLOSE)) {
            Connection connection = (Connection) proxy;
            dataSource.pushConnection(connection);
            return null;
        }
        return method.invoke(realConnection, args);
    }

    public Connection getRealConnection() {
        return realConnection;
    }

    public void setRealConnection(Connection realConnection) {
        this.realConnection = realConnection;
    }
}
