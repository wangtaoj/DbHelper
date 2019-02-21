package com.wangtao.dbhelper.executor.keygen;

import com.wangtao.dbhelper.core.Configuration;
import com.wangtao.dbhelper.core.ParamMap;
import com.wangtao.dbhelper.executor.ExecutorException;
import com.wangtao.dbhelper.mapping.MappedStatement;
import com.wangtao.dbhelper.reflection.MetaObject;
import com.wangtao.dbhelper.type.JdbcType;
import com.wangtao.dbhelper.type.TypeHandler;
import com.wangtao.dbhelper.type.TypeHandlerRegistry;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * @author wangtao
 * Created at 2019/2/19 13:36
 */
public class JDBCKeyGenerator implements KeyGenerator {

    public static final KeyGenerator INSTANCE = new JDBCKeyGenerator();

    private JDBCKeyGenerator() {

    }

    @Override
    public void processAfter(MappedStatement ms, Statement statement, Object parameter) {
        String[] keyPropertys = ms.getKeyPropertys();
        if (keyPropertys == null || keyPropertys.length == 0) {
            return;
        }
        try (ResultSet rs = statement.getGeneratedKeys()) {
            ResultSetMetaData metaData = rs.getMetaData();
            if (metaData.getColumnCount() >= keyPropertys.length) {
                parameter = getSoleParameter(parameter, keyPropertys);
                if (parameter != null) {
                    assignKeyToParameter(rs, ms, parameter);
                }
            } else {
                throw new ExecutorException("The counts of the parimary key column must be greater than the counts of "
                        + "key property. But the counts of the parimary key column is '" + metaData.getColumnCount()
                        + "', the counts of key property is '" + keyPropertys.length + "'.");
            }
        } catch (SQLException e) {
            throw new ExecutorException("error get the generated key or set value to parameter.", e);
        }
    }

    /**
     * 找到需要设置主键值的参数, 可能存在多个参数.
     * 存在多个参数时会修正keyPropertys数组元素值, 去掉前缀.
     * @param parameter    参数
     * @param keyPropertys 将要设置主键值的属性
     * @return 返回需要设置主键值的参数
     */
    private Object getSoleParameter(Object parameter, String[] keyPropertys) {
        if (parameter instanceof ParamMap) {
            Map<String, Object> map = (ParamMap) parameter;
            if (map.size() == 1) {
                return map.get(map.keySet().toArray()[0].toString());
            }
            String firstKeyProperty = keyPropertys[0];
            int index = firstKeyProperty.indexOf(".");
            if (index == -1) {
                throw new ExecutorException("we can't determine the generated key is assigned to which parameter." +
                        "The available parameters are " + map.keySet());
            }
            String paramPrefix = firstKeyProperty.substring(0, index);
            Object soleParameter;
            if (map.containsKey(paramPrefix)) {
                soleParameter = map.get(paramPrefix);
            } else {
                throw new ExecutorException("we can't find the parameter '" + paramPrefix + "'." +
                        "The available parameters are " + map.keySet());
            }
            // update param.id -> id
            for (int i = 0; i < keyPropertys.length; i++) {
                if (keyPropertys[i].charAt(index) == '.' && keyPropertys[i].startsWith(paramPrefix)) {
                    keyPropertys[i] = keyPropertys[i].substring(index + 1);
                } else {
                    throw new ExecutorException("不支持将自增主键赋值给多个参数对象, keyProperty前缀不一致.");
                }
            }
            return soleParameter;
        }
        return parameter;
    }

    void assignKeyToParameter(ResultSet rs, MappedStatement ms, Object parameter) throws SQLException {
        // 将参数转换成Collection
        Collection<?> collection;
        if ((parameter instanceof Object[])) {
            collection = Arrays.asList((Object[]) parameter);
        } else if (!(parameter instanceof Collection)) {
            collection = Collections.singletonList(parameter);
        } else {
            collection = (Collection) parameter;
        }
        MetaObject metaObject = null;
        String[] keyPropertys = ms.getKeyPropertys();
        for (Object element : collection) {
            metaObject = metaObject == null ? ms.getConfiguration().newMetaObject(element) : metaObject;
            if (rs.next()) {
                ResultSetMetaData metaData = rs.getMetaData();
                for (int i = 1; i <= metaData.getColumnCount(); i++) {
                    Class<?> clazz = metaObject.getSetterType(keyPropertys[i - 1]);
                    JdbcType jdbcType = JdbcType.forCode(metaData.getColumnType(i));
                    TypeHandler<?> typeHandler = getTypeHandler(ms.getConfiguration(), clazz, jdbcType);
                    metaObject.setValue(keyPropertys[i - 1], typeHandler.getResult(rs, metaData.getColumnLabel(i)));
                }
            }
        }
    }

    private TypeHandler<?> getTypeHandler(Configuration configuration, Class<?> clazz, JdbcType jdbcType) {
        TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
        return typeHandlerRegistry.getTypeHandler(clazz, jdbcType);
    }
}
