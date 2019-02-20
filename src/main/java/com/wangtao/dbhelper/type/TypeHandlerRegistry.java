package com.wangtao.dbhelper.type;

import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by wangtao at 2019/1/3 9:20
 */
public class TypeHandlerRegistry {

    /**
     * 存放TypeHandler
     * 一个Java类型可能对应对个TypeHandler, 一个JdbcType对应一个TypeHandler
     * 例如java.util.Date对应TIMESTAMP(DateHandler)、TIME(TimeOnlyHandler)、DATE(DateOnlyHandler)
     */
    private final Map<Class<?>, Map<JdbcType, TypeHandler<?>>> TYPE_HANDLER_MAP = new ConcurrentHashMap<>();

    /**
     * JdbcType与TypeHandler一一对应
     */
    private final Map<JdbcType, TypeHandler<?>> JDBC_TYPE_HANDLER_MAP = new ConcurrentHashMap<>();

    public TypeHandlerRegistry() {
        register(Boolean.class, new BooleanTypeHandler());
        register(boolean.class, new BooleanTypeHandler());
        register(JdbcType.BOOLEAN, new BooleanTypeHandler());

        register(Integer.class, new IntegerTypeHandler());
        register(int.class, new IntegerTypeHandler());
        register(JdbcType.INTEGER, new IntegerTypeHandler());

        register(Long.class, new LongTypeHandler());
        register(long.class, new LongTypeHandler());
        register(JdbcType.BIGINT, new LongTypeHandler());

        register(String.class, new StringTypeHandler());
        register(JdbcType.VARCHAR, new StringTypeHandler());

        register(java.util.Date.class, new DateTypeHandler());
        register(java.util.Date.class, JdbcType.DATE, new DateOnlyTypeHandler());
        register(java.util.Date.class, JdbcType.TIME, new TimeOnlyTypeHandler());

        register(Timestamp.class, new SqlTimestampTypeHandler());
        register(JdbcType.TIMESTAMP, new SqlTimestampTypeHandler());

        register(Time.class, new SqlTimeTypeHandler());
        register(JdbcType.TIME, new SqlTimeTypeHandler());

        register(java.sql.Date.class, new SqlDateTypeHandler());
        register(JdbcType.DATE, new SqlDateTypeHandler());

        register(LocalDateTime.class, new LocalDateTimeTypeHandler());
        register(LocalDate.class, new LocalDateHandler());
        register(LocalTime.class, new LocalTimeTypeHandler());

        final TypeHandler<Object> UNKNOWN_TYPE_HANDLER = new UnknownTypeHandler(this);
        register(Object.class, UNKNOWN_TYPE_HANDLER);
        register(Object.class, JdbcType.OTHER, UNKNOWN_TYPE_HANDLER);
        register(JdbcType.OTHER, UNKNOWN_TYPE_HANDLER);
    }

    public void register(Class<?> type, TypeHandler<?> typeHandler) {
        register(type, null, typeHandler);
    }

    /**
     * 注册TypeHandler
     * @param type        Java类型
     * @param jdbcType    对应的数据库类型
     * @param typeHandler 类型处理器
     */
    public void register(Class<?> type, JdbcType jdbcType, TypeHandler<?> typeHandler) {
        Map<JdbcType, TypeHandler<?>> jdbcHandlerMap = TYPE_HANDLER_MAP.
                computeIfAbsent(type, key -> new HashMap<>());
        jdbcHandlerMap.putIfAbsent(jdbcType, typeHandler);
    }

    public void register(JdbcType jdbcType, TypeHandler<?> typeHandler) {
        JDBC_TYPE_HANDLER_MAP.putIfAbsent(jdbcType, typeHandler);
    }

    /**
     * 根据Java类型以及jdbcType查找对应的类型处理器.
     * 对于一个类型对应一个Handler时直接返回
     * 如果一个类型对应多个Handler, 则会根据jdbcType查找, 没找到则返回默认的Handler
     * 一个类型对应的默认Handler是key=null所对应的
     * @param type     Java类型
     * @param jdbcType jdbcType
     * @param <T>      泛型参数
     * @return 类型处理器
     */
    @SuppressWarnings("unchecked")
    public <T> TypeHandler<T> getTypeHandler(Class<T> type, JdbcType jdbcType) {
        Map<JdbcType, TypeHandler<?>> jdbcHandlerMap = TYPE_HANDLER_MAP.get(type);
        if (jdbcHandlerMap != null) {
            TypeHandler<?> typeHandler = jdbcHandlerMap.get(jdbcType);
            if (typeHandler == null) {
                typeHandler = jdbcHandlerMap.get(null);
            }
            if (typeHandler != null) {
                return (TypeHandler<T>) typeHandler;
            }
        }
        return null;
    }

    public <T> TypeHandler<T> getTypeHandler(Class<T> type) {
        return getTypeHandler(type, null);
    }

    @SuppressWarnings("unchecked")
    public <T> TypeHandler<T> getTypeHandler(JdbcType jdbcType) {
        TypeHandler<?> typeHandler = JDBC_TYPE_HANDLER_MAP.get(jdbcType);
        return (TypeHandler<T>) typeHandler;
    }

    public boolean hasTypeHandler(Class<?> type) {
        return hasTypeHandler(type, null);
    }

    /**
     * 判断java类型以及jdbc类型是否存在对应的TypeHandler
     * @param type java类型
     * @param jdbcType jdbc类型
     * @return 存在返回true, 否则返回false.
     */
    public boolean hasTypeHandler(Class<?> type, JdbcType jdbcType) {
        return type != null && getTypeHandler(type, jdbcType) != null;
    }

    public boolean hasTypeHandler(JdbcType jdbcType) {
        return jdbcType != null && getTypeHandler(jdbcType) != null;
    }
}
