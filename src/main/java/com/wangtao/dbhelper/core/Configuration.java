package com.wangtao.dbhelper.core;

import com.wangtao.dbhelper.type.JdbcType;
import com.wangtao.dbhelper.type.TypeAliasRegistry;
import com.wangtao.dbhelper.type.TypeHandlerRegistry;

import java.util.Properties;

/**
 * @author wangtao
 * Created at 2019/1/10 13:45
 */
public class Configuration {

    /**
     * 配置文件中配置的属性值
     */
    protected Properties variables = new Properties();

    /** 下划线转驼峰 **/
    protected boolean mapUnderscoreToCamelCase;

    protected JdbcType jdbcTypeForNull = JdbcType.OTHER;

    /** 从数据库拿到为null的值时是否调用setter方法, 这对于Map作为返回值时会影响是否调用put方法 **/
    protected boolean callSettersOnNulls;

    private TypeHandlerRegistry typeHandlerRegistry = new TypeHandlerRegistry();

    private TypeAliasRegistry typeAliasRegistry = new TypeAliasRegistry();

    public Properties getVariables() {
        return variables;
    }

    public void setVariables(Properties variables) {
        this.variables = variables;
    }

    public boolean isMapUnderscoreToCamelCase() {
        return mapUnderscoreToCamelCase;
    }

    public void setMapUnderscoreToCamelCase(boolean mapUnderscoreToCamelCase) {
        this.mapUnderscoreToCamelCase = mapUnderscoreToCamelCase;
    }

    public JdbcType getJdbcTypeForNull() {
        return jdbcTypeForNull;
    }

    public void setJdbcTypeForNull(JdbcType jdbcTypeForNull) {
        this.jdbcTypeForNull = jdbcTypeForNull;
    }

    public boolean isCallSettersOnNulls() {
        return callSettersOnNulls;
    }

    public void setCallSettersOnNulls(boolean callSettersOnNulls) {
        this.callSettersOnNulls = callSettersOnNulls;
    }

    public TypeHandlerRegistry getTypeHandlerRegistry() {
        return typeHandlerRegistry;
    }

    public TypeAliasRegistry getTypeAliasRegistry() {
        return typeAliasRegistry;
    }
}
