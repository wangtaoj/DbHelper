package com.wangtao.dbhelper.core;

import com.wangtao.dbhelper.mapping.Environment;
import com.wangtao.dbhelper.mapping.ResultMap;
import com.wangtao.dbhelper.parser.XNode;
import com.wangtao.dbhelper.type.JdbcType;
import com.wangtao.dbhelper.type.TypeAliasRegistry;
import com.wangtao.dbhelper.type.TypeHandlerRegistry;

import java.util.*;

/**
 * @author wangtao
 * Created at 2019/1/10 13:45
 */
public class Configuration {

    private TypeHandlerRegistry typeHandlerRegistry = new TypeHandlerRegistry();

    private TypeAliasRegistry typeAliasRegistry = new TypeAliasRegistry();

    /**
     * 配置文件中配置的属性值
     */
    protected Properties variables = new Properties();

    /**
     * 下划线转驼峰
     **/
    protected boolean mapUnderscoreToCamelCase;

    protected JdbcType jdbcTypeForNull = JdbcType.OTHER;

    /**
     * 从数据库拿到为null的值时是否调用setter方法, 这对于Map作为返回值时会影响是否调用put方法
     **/
    protected boolean callSettersOnNulls;

    protected Environment environment;

    /**
     * Mapper文件集合, 用于判断是否解析过此Mapper文件.
     * 存放的是完整路径, 相对类路径而言.
     */
    protected Set<String> loadedResources = new HashSet<>();

    protected Map<String, ResultMap> resultMaps = new StrictMap<>("ResultMap Collection");

    protected Map<String, XNode> sqlNodes = new StrictMap<>("sql元素集合");

    /**
     * 添加解析过的Mapper文件资源
     * @param resource Mapper文件路径
     */
    public void addLoadedResource(String resource) {
        loadedResources.add(resource);
    }

    /**
     * 判断指定资源是否解析过.
     * @param resource Mapper文件路径
     * @return 解析过返回true, 否则返回false.
     */
    public boolean isLoadedResource(String resource) {
        return loadedResources.contains(resource);
    }

    /**
     * 添加resultMap元素, 键 = namespace + "." + id
     * @param resultMap ResultMap实列
     */
    public void addResultMap(ResultMap resultMap) {
        resultMaps.put(resultMap.getId(), resultMap);
    }

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

    public Environment getEnvironment() {
        return environment;
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    public Map<String, XNode> getSqlNodes() {
        return sqlNodes;
    }

    protected static class StrictMap<K, V> extends HashMap<K, V> {

        private String name;

        public StrictMap(String name) {
            this.name = name;
        }

        public V put(K key, V value) {
            if (containsKey(key)) {
                throw new IllegalArgumentException(name + " already contains value for " + key);
            }
            return super.put(key, value);
        }

        public V get(Object key) {
            V value = super.get(key);
            if (value == null) {
                throw new IllegalArgumentException(name + " does not contain value for " + key);
            }
            return value;
        }

        public String getName() {
            return name;
        }
    }
}
