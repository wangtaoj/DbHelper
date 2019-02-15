package com.wangtao.dbhelper.builder;

import com.wangtao.dbhelper.core.Configuration;
import com.wangtao.dbhelper.type.*;

/**
 * @author wangtao
 * Created at 2019/1/16 16:09
 */
public abstract class BaseBuilder {

    protected final Configuration configuration;

    protected final TypeHandlerRegistry typeHandlerRegistry;

    protected final TypeAliasRegistry typeAliasRegistry;

    protected BaseBuilder(Configuration configuration) {
        this.configuration = configuration;
        this.typeHandlerRegistry = configuration.getTypeHandlerRegistry();
        this.typeAliasRegistry = configuration.getTypeAliasRegistry();
    }

    protected Boolean booleanOfValue(String value, boolean defaultValue) {
        return value == null ? defaultValue : Boolean.valueOf(value);
    }

    protected Integer IntegerOfValue(String value, int defaultValue) {
        return value == null ? defaultValue : Integer.valueOf(value);
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    protected <T> Class<? extends T> resolveAlias(String alias) {
        return typeAliasRegistry.resolveAlias(alias);
    }

    protected <T> Class<? extends T> resolveClass(String clazz) {
        if (clazz == null) {
            return null;
        }
        return resolveAlias(clazz);
    }

    protected JdbcType resolveJdbc(String name) {
        if (name == null) {
            return null;
        }
        try {
            return JdbcType.valueOf(name);
        } catch (IllegalArgumentException e) {
            throw new BuilderException("错误的解析JdbcType, 请确保拼写正确.", e);
        }
    }

    protected TypeHandler<?> resolveTypeHandler(String typeHandlerName) {
        Class<? extends TypeHandler> typeHandlerClass = resolveClass(typeHandlerName);
        try {
            return typeHandlerClass.newInstance();
        } catch (Exception e) {
            throw new TypeException("错误的解析TypeHandler.", e);
        }
    }

    protected TypeHandler<?> getTypeHandler(Class<?> javaType, JdbcType jdbcType) {
        TypeHandler<?> typeHandler = typeHandlerRegistry.getTypeHandler(javaType, jdbcType);
        if (typeHandler == null) {
            throw new TypeException("we can't find the suitable type handler by '" +
                    javaType + "' and '" + jdbcType + "'.");
        }
        return typeHandler;
    }
}
