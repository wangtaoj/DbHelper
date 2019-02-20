package com.wangtao.dbhelper.executor.resultset;

import com.wangtao.dbhelper.core.Configuration;
import com.wangtao.dbhelper.core.RowBounds;
import com.wangtao.dbhelper.executor.ExecutorException;
import com.wangtao.dbhelper.mapping.MappedStatement;
import com.wangtao.dbhelper.mapping.ResultMap;
import com.wangtao.dbhelper.mapping.ResultMapping;
import com.wangtao.dbhelper.reflection.MetaClass;
import com.wangtao.dbhelper.reflection.MetaObject;
import com.wangtao.dbhelper.reflection.factory.ObjectFactory;
import com.wangtao.dbhelper.type.JdbcType;
import com.wangtao.dbhelper.type.TypeHandler;
import com.wangtao.dbhelper.type.TypeHandlerRegistry;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

/**
 * @author wangtao
 * Created at 2019/2/14 10:28
 */
public class DefaultResultSetHandler implements ResultSetHandler {

    protected final Configuration configuration;

    protected final MappedStatement ms;

    protected final RowBounds rowBounds;

    protected final ObjectFactory objectFactory;

    protected final TypeHandlerRegistry typeHandlerRegistry;

    /**
     * 暂存自动映射的UnMappedColumnAutoMapping集合.
     * 下一行数据映射时不用再重新创建.
     * key: resultmap id.
     */
    protected final Map<String, List<UnMappedColumnAutoMapping>> autoMappingCache = new HashMap<>();

    public DefaultResultSetHandler(MappedStatement ms, RowBounds rowBounds) {
        this.configuration = ms.getConfiguration();
        this.ms = ms;
        this.rowBounds = rowBounds;
        this.objectFactory = configuration.getObjectFactory();
        this.typeHandlerRegistry = configuration.getTypeHandlerRegistry();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <E> List<E> handleResultSet(Statement statement) throws SQLException {
        ResultMap resultMap = ms.getResultMap();
        ResultSet rs = getFirstResultSet(statement);
        if (resultMap == null && rs != null) {
            throw new ExecutorException("This is a query statement, but we not found a resultMap in MappedStatement '"
                    + ms.getId() + "'. Please specified a resultMap or resultType!");
        }
        List<Object> results = new ArrayList<>();
        if (rs != null) {
            try {
                // 返回false, 说明要查找的数据行已超过resultset的结果行数, 返回空集合.
                if (skipRows(rs, rowBounds)) {
                    while (rs.next() && results.size() < rowBounds.getLimit()) {
                        Object result = handleRowValue(new ResultSetWrapper(rs), resultMap);
                        results.add(result);
                    }
                }
            } finally {
                closeResultSet(rs);
            }
        }
        return (List<E>) results;
    }

    /**
     * 处理单行数据.
     */
    public Object handleRowValue(ResultSetWrapper rsw, ResultMap resultMap) {
        Object result;
        boolean foundValue = false;
        Class<?> resultType = resultMap.getType();
        result = createResultObject(resultMap, rsw);
        if (result != null && !hasTypeHandlerForResultObject(resultType, rsw)) {
            MetaObject metaObject = MetaObject.forObject(result);
            foundValue = applyAutoMappingProperty(rsw, resultMap, metaObject);
            foundValue = applyMappedProperty(resultMap, metaObject, rsw) || foundValue;
        }
        return foundValue ? result : null;
    }

    private boolean applyAutoMappingProperty(ResultSetWrapper rsw, ResultMap resultMap, MetaObject metaObject) {
        List<UnMappedColumnAutoMapping> unMappedColumnAutoMappings = createAutoMappingColumns(rsw, resultMap, metaObject);
        boolean foundValue = false;
        for (UnMappedColumnAutoMapping autoMappingColumn : unMappedColumnAutoMappings) {
            String column = autoMappingColumn.column;
            String property = autoMappingColumn.property;
            TypeHandler<?> typeHandler = autoMappingColumn.typeHandler;
            Object value = typeHandler.getResult(rsw.getResultSet(), column);
            if (value != null) {
                foundValue = true;
            }
            if (value != null || (configuration.isCallSettersOnNulls() && !autoMappingColumn.primitive)) {
                metaObject.setValue(property, value);
            }
        }
        return foundValue;
    }

    private List<UnMappedColumnAutoMapping> createAutoMappingColumns(ResultSetWrapper rsw, ResultMap resultMap, MetaObject metaObject) {
        List<UnMappedColumnAutoMapping> unMappedColumnAutoMappings = autoMappingCache.get(resultMap.getId());
        if (unMappedColumnAutoMappings != null) {
            return unMappedColumnAutoMappings;
        }
        unMappedColumnAutoMappings = new ArrayList<>();
        List<String> unMappedColumn = rsw.getUnMappedColumnNames(resultMap);
        for (String column : unMappedColumn) {
            String property = metaObject.findProperty(column, configuration.isMapUnderscoreToCamelCase());
            if (property != null && metaObject.hasSetter(property)) {
                Class<?> propertyType = metaObject.getSetterType(property);
                JdbcType jdbcType = rsw.getJdbcType(column);
                if (typeHandlerRegistry.hasTypeHandler(propertyType, jdbcType)) {
                    TypeHandler<?> typeHandler = typeHandlerRegistry.getTypeHandler(propertyType, jdbcType);
                    unMappedColumnAutoMappings.add(new UnMappedColumnAutoMapping(column, property, typeHandler,
                            propertyType.isPrimitive()));
                }
            }
        }
        autoMappingCache.put(resultMap.getId(), unMappedColumnAutoMappings);
        return unMappedColumnAutoMappings;
    }

    private boolean applyMappedProperty(ResultMap resultMap, MetaObject metaObject, ResultSetWrapper rsw) {
        List<ResultMapping> resultMappings = resultMap.getResultMappings();
        List<String> mappedColumnNames = rsw.getMappedColumnNames(resultMap);
        boolean foundValue = false;
        for (ResultMapping resultMapping : resultMappings) {
            String column = resultMapping.getColumn();
            String property = resultMapping.getProperty();
            if (mappedColumnNames.contains(column.toUpperCase(Locale.ENGLISH))) {
                TypeHandler<?> typeHandler = resultMapping.getTypeHandler();
                Class<?> propertyType = metaObject.getSetterType(property);
                Object value = typeHandler.getResult(rsw.getResultSet(), column);
                if (value != null) {
                    foundValue = true;
                }
                if (value != null || (configuration.isCallSettersOnNulls() && !propertyType.isPrimitive())) {
                    metaObject.setValue(property, value);
                }
            }
        }
        return foundValue;
    }

    /**
     * 获取第一个结果集.
     * 判断没有更多结果集的代码:
     * (stmt.getMoreResults() == false) && (stmt.getUpdateCount() == -1)
     * @param statement statement对象
     * @return 返回第一个结果集(ResultSet)对象
     */
    private ResultSet getFirstResultSet(Statement statement) throws SQLException {
        ResultSet rs = statement.getResultSet();
        while (rs == null) {
            if (statement.getMoreResults()) {
                rs = statement.getResultSet();
            } else if (statement.getUpdateCount() == -1) {
                break;
            }
        }
        return rs;
    }

    /**
     * 获取下一个结果集.
     */
    private ResultSet getNextResultSet(Statement statement) throws SQLException {
        while (hasNextResultSet(statement)) {
            ResultSet rs = statement.getResultSet();
            if (rs != null) {
                return rs;
            }
        }
        return null;
    }

    private boolean hasNextResultSet(Statement statement) throws SQLException {
        return !(statement.getUpdateCount() == -1 && !statement.getMoreResults());
    }

    // 跳到指定的行
    private boolean skipRows(ResultSet rs, RowBounds rowBounds) throws SQLException {
        if (rowBounds.getOffset() != RowBounds.NO_OFFSET) {
            // 默认类型, 只能迭代一次, 一行一行向前移动, 不能使用absolute方法定位.
            if (rs.getType() != ResultSet.TYPE_FORWARD_ONLY) {
                return rs.absolute(rowBounds.getOffset());
            } else {
                for (int i = 0; i < rowBounds.getOffset(); i++) {
                    if (!rs.next()) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * 创建数据库结果对象
     */
    private Object createResultObject(ResultMap resultMap, ResultSetWrapper rsw) {
        Class<?> resultType = resultMap.getType();
        MetaClass metaClass = MetaClass.forClass(resultType);
        if (hasTypeHandlerForResultObject(resultType, rsw)) {
            return createPrimitiveResultObject(resultMap, rsw);
        } else if (metaClass.hasDefaultConstructor()) {
            return objectFactory.create(resultType);
        }
        throw new ExecutorException("we don't know how to create an instance of " + resultType);
    }

    private Object createPrimitiveResultObject(ResultMap resultMap, ResultSetWrapper rsw) {
        Class<?> resultType = resultMap.getType();
        List<ResultMapping> resultMappings = resultMap.getResultMappings();
        String columnName;
        if (resultMappings.isEmpty()) {
            columnName = rsw.getColumnNames().get(0);
        } else {
            columnName = resultMappings.get(0).getColumn();
        }
        TypeHandler<?> typeHandler = typeHandlerRegistry.getTypeHandler(resultType, rsw.getJdbcType(columnName));
        return typeHandler.getResult(rsw.getResultSet(), columnName);
    }

    private void closeResultSet(ResultSet rs) throws SQLException {
        if (rs != null) {
            rs.close();
        }
    }

    private boolean hasTypeHandlerForResultObject(Class<?> resultType, ResultSetWrapper rsw) {
        if (rsw.getColumnNames().size() == 1) {
            return typeHandlerRegistry.hasTypeHandler(resultType, rsw.getJdbcType(rsw.getColumnNames().get(0)));
        }
        return typeHandlerRegistry.hasTypeHandler(resultType);
    }

    private static class UnMappedColumnAutoMapping {
        final String column;
        final String property;
        final TypeHandler<?> typeHandler;
        final boolean primitive;

        public UnMappedColumnAutoMapping(String column, String property, TypeHandler<?> typeHandler, boolean primitive) {
            this.column = column;
            this.property = property;
            this.typeHandler = typeHandler;
            this.primitive = primitive;
        }
    }
}
