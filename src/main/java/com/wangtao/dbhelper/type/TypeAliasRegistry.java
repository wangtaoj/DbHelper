package com.wangtao.dbhelper.type;

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
}
