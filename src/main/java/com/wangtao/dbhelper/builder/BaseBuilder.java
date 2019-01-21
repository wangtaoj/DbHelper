package com.wangtao.dbhelper.builder;

import com.wangtao.dbhelper.core.Configuration;

/**
 * @author wangtao
 * Created at 2019/1/16 16:09
 */
public abstract class BaseBuilder {

    protected final Configuration configuration;

    public BaseBuilder(Configuration configuration) {
        this.configuration = configuration;
    }

    public Boolean booleanOfValue(String value, boolean defaultValue) {
        return value == null ? defaultValue : Boolean.valueOf(value);
    }

    public Integer IntegerOfValue(String value, int defaultValue) {
        return value == null ? defaultValue : Integer.valueOf(value);
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public <T> Class<? extends T> resolveAlias(String alias) {
        return configuration.getTypeAliasRegistry().resolveAlias(alias);
    }

    public <T> Class<? extends T> resolveClass(String clazz) {
        return resolveAlias(clazz);
    }
}
