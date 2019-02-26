package com.wangtao.dbhelper.binding;

import com.wangtao.dbhelper.core.Configuration;
import com.wangtao.dbhelper.mapping.MappedStatement;
import com.wangtao.dbhelper.mapping.SqlCommandType;

import java.lang.reflect.Method;

/**
 * 此类用来保存statement id 以及SQL的类型.
 * @author wangtao
 * Created at 2019/2/25 14:14
 */
public class SqlCommand {

    /**
     * 接口全限定名 + 方法名字
     * 注: 也有可能是父接口名字
     */
    private final String statementId;

    /**
     * SQL类型(INSERT, UPDATE, DELETE, SELECT)
     */
    private final SqlCommandType type;

    public SqlCommand(Configuration configuration, Class<?> mapperInterface, Method method) {
        final String methodName = method.getName();
        final Class<?> declaringInterface = method.getDeclaringClass();
        MappedStatement ms = resolveMappedStatement(mapperInterface, methodName, declaringInterface, configuration);
        if(ms != null) {
            statementId = ms.getId();
            type = ms.getSqlCommandType();
        } else {
            throw new BindingException("Invalid statement id '" +
                    mapperInterface.getName() + "." + methodName + "'.");
        }
    }

    private MappedStatement resolveMappedStatement(Class<?> mapperInterface, String methodName, Class<?> declaringInterface,
                                                   Configuration configuration) {
        String statementId = mapperInterface.getName() + "." + methodName;
        if (configuration.hasMappedStatement(statementId)) {
            return configuration.getMappedStatement(statementId);
        } else if (mapperInterface == declaringInterface) {
            return null;
        }
        for (Class<?> superInterface : mapperInterface.getInterfaces()) {
            if (declaringInterface.isAssignableFrom(superInterface)) {
                MappedStatement ms = resolveMappedStatement(superInterface, methodName, declaringInterface, configuration);
                if (ms != null) {
                    return ms;
                }
            }
        }
        return null;
    }

    public String getStatementId() {
        return statementId;
    }

    public SqlCommandType getType() {
        return type;
    }
}
