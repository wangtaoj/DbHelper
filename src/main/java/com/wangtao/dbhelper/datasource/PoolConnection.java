package com.wangtao.dbhelper.datasource;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.sql.Connection;

/**
 * Created by wangtao at 2018/12/20 19:25
 */
public class PoolConnection implements InvocationHandler {

    private Connection realConnection;

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return null;
    }
}
