package com.wangtao.dbhelper.core;

import com.wangtao.dbhelper.binding.MapperRegistry;
import com.wangtao.dbhelper.executor.parameter.DefaultParameterHandler;
import com.wangtao.dbhelper.executor.parameter.ParameterHandler;
import com.wangtao.dbhelper.executor.resultset.DefaultResultSetHandler;
import com.wangtao.dbhelper.executor.resultset.ResultSetHandler;
import com.wangtao.dbhelper.executor.statement.PreparedStatementHandler;
import com.wangtao.dbhelper.executor.statement.StatementHandler;
import com.wangtao.dbhelper.mapping.BoundSql;
import com.wangtao.dbhelper.mapping.Environment;
import com.wangtao.dbhelper.mapping.MappedStatement;
import com.wangtao.dbhelper.mapping.ResultMap;
import com.wangtao.dbhelper.parser.XNode;
import com.wangtao.dbhelper.reflection.MetaObject;
import com.wangtao.dbhelper.reflection.factory.DefaultObjectFactory;
import com.wangtao.dbhelper.reflection.factory.ObjectFactory;
import com.wangtao.dbhelper.type.JdbcType;
import com.wangtao.dbhelper.type.TypeAliasRegistry;
import com.wangtao.dbhelper.type.TypeHandlerRegistry;

import java.util.*;

/**
 * @author wangtao
 * Created at 2019/1/10 13:45
 */
public class Configuration {

    protected TypeHandlerRegistry typeHandlerRegistry = new TypeHandlerRegistry();

    protected TypeAliasRegistry typeAliasRegistry = new TypeAliasRegistry();

    protected MapperRegistry mapperRegistry = new MapperRegistry();

    /**
     * 配置文件中配置的属性值
     */
    protected Properties variables = new Properties();

    /**
     * 下划线转驼峰
     **/
    protected boolean mapUnderscoreToCamelCase;

    /**
     * 参数值未null时默认的JdbcType类型
     */
    protected JdbcType jdbcTypeForNull = JdbcType.OTHER;

    /**
     * 从数据库拿到为null的值时是否调用setter方法, 这对于Map作为返回值时会影响是否调用put方法
     **/
    protected boolean callSettersOnNulls;

    /**
     * 当返回行的所有列都是空时，默认返回null。 当开启这个设置时，那么会返回一个空实例
     */
    protected boolean returnInstanceForEmptyRow;

    /**
     * 允许使用方法签名中的名称作为语句参数名称
     * 必须使用JDK8编译, 并且编译时加上-parameters参数
     */
    protected boolean useActualParamName = true;

    protected Environment environment;

    protected ObjectFactory objectFactory = new DefaultObjectFactory();

    /**
     * Mapper文件集合, 用于判断是否解析过此Mapper文件.
     * 存放的是完整路径, 相对类路径而言.
     */
    protected Set<String> loadedResources = new HashSet<>();

    /**
     * 用于存放<resultMap>节点信息, key是namespace + "." + id
     */
    protected Map<String, ResultMap> resultMaps = new StrictMap<>("Result Maps Collection");

    /**
     * 用于存放<sql>节点
     */
    protected Map<String, XNode> sqlNodes = new StrictMap<>("Sqls Collection");

    protected Map<String, MappedStatement> mappedStatements = new StrictMap<>("Mapped Statements Collection");

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

    public ResultMap getResultMap(String resultMapId) {
        return resultMaps.get(resultMapId);
    }

    public void addMappedStatement(MappedStatement mappedStatement) {
        mappedStatements.put(mappedStatement.getId(), mappedStatement);
    }

    public MappedStatement getMappedStatement(String statementId) {
        return mappedStatements.get(statementId);
    }

    public boolean hasMappedStatement(String statementId) {
        return mappedStatements.containsKey(statementId);
    }

    public boolean hasMapper(Class<?> mapperInterface) {
        return mapperRegistry.hasMapper(mapperInterface);
    }

    public <T> T getMapper(Class<T> mapperInterface, SqlSession sqlSession) {
        return mapperRegistry.getMapper(mapperInterface, sqlSession);
    }

    public <T> void addMapper(Class<T> mapperInterface) {
        mapperRegistry.addMapper(mapperInterface);
    }

    public MetaObject newMetaObject(Object object) {
        return MetaObject.forObject(object);
    }

    public ResultSetHandler newResultSetHandler(MappedStatement ms, RowBounds rowBounds) {
        return new DefaultResultSetHandler(ms, rowBounds);
    }

    public StatementHandler newStatementHandler(MappedStatement ms, RowBounds rowBounds, Object parameter) {
        switch (ms.getStatementType()) {
            case PREPARED:
                return new PreparedStatementHandler(ms, rowBounds, parameter);
            default:
                throw new IllegalArgumentException("we only support PreparedStatement now.");
        }

    }

    public ParameterHandler newParameterHandler(BoundSql boundSql) {
        return new DefaultParameterHandler(this, boundSql);
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

    public boolean isReturnInstanceForEmptyRow() {
        return returnInstanceForEmptyRow;
    }

    public void setReturnInstanceForEmptyRow(boolean returnInstanceForEmptyRow) {
        this.returnInstanceForEmptyRow = returnInstanceForEmptyRow;
    }

    public boolean isUseActualParamName() {
        return useActualParamName;
    }

    public void setUseActualParamName(boolean useActualParamName) {
        this.useActualParamName = useActualParamName;
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

    public ObjectFactory getObjectFactory() {
        return objectFactory;
    }

    public void setObjectFactory(ObjectFactory objectFactory) {
        this.objectFactory = objectFactory;
    }

    public Map<String, XNode> getSqlNodes() {
        return sqlNodes;
    }

    protected static class StrictMap<K, V> extends HashMap<K, V> {

        private static final long serialVersionUID = -2472336574571533492L;

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
