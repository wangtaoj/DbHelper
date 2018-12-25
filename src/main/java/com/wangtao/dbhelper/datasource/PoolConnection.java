package com.wangtao.dbhelper.datasource;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;

/**
 * Created by wangtao at 2018/12/20 19:25
 */
public class PoolConnection implements InvocationHandler {

    private static final String CLOSE = "close";

    private static final Class<?>[] interfaces = new Class<?>[]{Connection.class};

    /**
     * 真实连接对象
     **/
    private Connection realConnection;

    /**
     * 代理对象
     **/
    private Connection proxyConnection;

    private PoolDataSource dataSource;

    public PoolConnection(Connection connection, PoolDataSource dataSource) {
        this.realConnection = connection;
        this.dataSource = dataSource;
        this.proxyConnection = (Connection) Proxy.newProxyInstance(Connection.class.getClassLoader(),
                interfaces, this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getName().equals(CLOSE)) {
            dataSource.pushConnection(this);
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

    public Connection getProxyConnection() {
        return proxyConnection;
    }

    public void setProxyConnection(Connection proxyConnection) {
        this.proxyConnection = proxyConnection;
    }
}
