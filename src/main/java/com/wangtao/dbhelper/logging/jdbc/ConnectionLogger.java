package com.wangtao.dbhelper.logging.jdbc;

import com.wangtao.dbhelper.logging.Log;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.PreparedStatement;

/**
 * @author wangtao
 * Created at 2019/2/19 15:42
 */
public final class ConnectionLogger extends BaseJdbcLogger implements InvocationHandler {

    private Connection connection;

    public ConnectionLogger(Connection connection, Log statementLog) {
        super(statementLog);
        this.connection = connection;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if("prepareStatement".equals(method.getName())) {
            // 打印SQL语句
            if(statementLog.isDebugEnabled()) {
                statementLog.debug("Preparing: " + removeBreakingWhitespace((String) args[0]));
            }
            PreparedStatement ps = (PreparedStatement) method.invoke(connection, args);
            return PreparedStatementLogger.newInstance(ps, statementLog);
        }
        return method.invoke(connection, args);
    }

    public static Connection newInstance(Connection connection, Log statementLog) {
        ConnectionLogger connectionLogger = new ConnectionLogger(connection, statementLog);
        return (Connection) Proxy.newProxyInstance(Connection.class.getClassLoader(),
                new Class<?>[]{Connection.class}, connectionLogger);
    }
}
