package com.wangtao.dbhelper.logging.jdbc;

import com.wangtao.dbhelper.logging.Log;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.ResultSet;

/**
 * @author wangtao
 * Created at 2019/2/19 17:03
 */
public final class ResultSetLogger extends BaseJdbcLogger implements InvocationHandler {

    private ResultSet rs;

    private int rows;

    public ResultSetLogger(ResultSet rs, Log statementLog) {
        super(statementLog);
        this.rs = rs;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object result = method.invoke(rs, args);
        if (method.getName().equals("next")) {
            if((Boolean) result) {
                rows++;
            } else {
                if(statementLog.isDebugEnabled()) {
                    statementLog.debug("Total: " + rows);
                }
            }
        }
        return result;
    }

    public static ResultSet newInstance(ResultSet rs, Log statementLog) {
        ResultSetLogger resultSetLogger = new ResultSetLogger(rs, statementLog);
        return (ResultSet) Proxy.newProxyInstance(ResultSet.class.getClassLoader(),
                new Class<?>[]{ResultSet.class}, resultSetLogger);
    }
}
