package com.wangtao.dbhelper.logging.jdbc;

import com.wangtao.dbhelper.logging.Log;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * @author wangtao
 * Created at 2019/2/19 16:08
 */
public final class PreparedStatementLogger extends BaseJdbcLogger implements InvocationHandler {

    private PreparedStatement ps;

    public PreparedStatementLogger(PreparedStatement ps, Log statementLog) {
        super(statementLog);
        this.ps = ps;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (SET_METHODS.contains(method.getName())) {
            if (method.getName().equals("setNull")) {
                COLUMN_MAP.put(args[0], null);
            } else {
                COLUMN_MAP.put(args[0], args[1]);
            }
            return method.invoke(ps, args);
        } else if (EXECUTE_METHODS.contains(method.getName())) {
            if (statementLog.isDebugEnabled()) {
                statementLog.debug("Parameters: " + getParameterAsString());
            }
            COLUMN_MAP.clear();
            if ("executeQuery".equals(method.getName())) {
                ResultSet rs = (ResultSet) method.invoke(ps, args);
                return rs == null ? null : ResultSetLogger.newInstance(rs, statementLog);
            } else {
                return method.invoke(ps, args);
            }
        } else if ("getResultSet".equals(method.getName())) {
            ResultSet rs = (ResultSet) method.invoke(ps, args);
            return rs == null ? null : ResultSetLogger.newInstance(rs, statementLog);
        } else if ("getUpdateCount".equals(method.getName())) {
            int updateCount = (Integer) method.invoke(ps, args);
            if (updateCount != -1 && statementLog.isDebugEnabled()) {
                statementLog.debug("Updates: " + updateCount);
            }
            return updateCount;
        } else {
            return method.invoke(ps, args);
        }
    }

    public static PreparedStatement newInstance(PreparedStatement ps, Log statementLog) {
        PreparedStatementLogger psLogger = new PreparedStatementLogger(ps, statementLog);
        return (PreparedStatement) Proxy.newProxyInstance(PreparedStatement.class.getClassLoader(),
                new Class<?>[]{PreparedStatement.class}, psLogger);
    }
}
