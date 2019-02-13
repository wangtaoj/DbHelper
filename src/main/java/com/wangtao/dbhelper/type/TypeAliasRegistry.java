package com.wangtao.dbhelper.type;

import com.wangtao.dbhelper.core.Resources;
import com.wangtao.dbhelper.datasource.PoolDataSourceFactory;
import com.wangtao.dbhelper.datasource.SimpleDataSourceFactory;
import com.wangtao.dbhelper.transaction.JdbcTransactionFactory;

import java.util.*;

/**
 * @author wangtao
 * Created at 2019/1/10 14:21
 */
public class TypeAliasRegistry {

    private final Map<String, Class<?>> ALIAS_TYPE_MAP = new HashMap<>();

    public TypeAliasRegistry() {
        register("_int", int.class);
        register("_long", long.class);
        register("integer", Integer.class);
        register("long", Long.class);
        register("_boolean", boolean.class);
        register("boolean", Boolean.class);
        register("string", String.class);
        register("hashmap", HashMap.class);
        register("map", Map.class);
        register("arraylist", ArrayList.class);
        register("list", List.class);

        register("JDBC", JdbcTransactionFactory.class);
        register("POOLED", PoolDataSourceFactory.class);
        register("UNPOOLED", SimpleDataSourceFactory.class);
    }

    public void register(String alias, Class<?> type) {
        if(alias == null) {
            throw new NullPointerException("配置的别名不能为null");
        }
        if (ALIAS_TYPE_MAP.containsKey(alias) && ALIAS_TYPE_MAP.get(alias) != null) {
            throw new TypeException("该别名:" + alias + "已经映射到" + ALIAS_TYPE_MAP.get(alias) + "类了");
        }
        ALIAS_TYPE_MAP.put(alias, type);
    }

    public void register(Class<?> type) {
        String alias = type.getSimpleName().toLowerCase(Locale.ENGLISH);
        register(alias, type);
    }

    @SuppressWarnings("unchecked")
    public <T> Class<? extends T> resolveAlias(String alias) {
        if(ALIAS_TYPE_MAP.containsKey(alias)) {
            return (Class<T>) ALIAS_TYPE_MAP.get(alias);
        }
        try {
            return (Class<T>) Resources.classForName(alias);
        } catch (ClassNotFoundException e) {
            throw new TypeException("不能处理" +  alias + "这个别名或者完全限定名, 找不到对应的类.", e);
        }
    }
}
