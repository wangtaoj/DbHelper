package com.wangtao.dbhelper.binding;

import com.wangtao.dbhelper.core.Configuration;
import com.wangtao.dbhelper.core.RowBounds;
import com.wangtao.dbhelper.core.SqlSession;
import com.wangtao.dbhelper.reflection.factory.ObjectFactory;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;

/**
 * 代理接口方法的执行.
 * @author wangtao
 * Created at 2019/2/25 14:11
 */
public class MapperMethod {

    private final SqlCommand sqlCommand;

    private final MethodSignature method;

    public MapperMethod(Class<?> mapperInterface, Method method, Configuration configuration) {
        this.sqlCommand = new SqlCommand(configuration, mapperInterface, method);
        this.method = new MethodSignature(configuration, mapperInterface, method);
    }

    public Object execute(SqlSession sqlSession, Object[] args) {
        Object result;
        switch (sqlCommand.getType()) {
            case INSERT: {
                Object parameter = method.convertArgsToSqlCommandParam(args);
                result = rowCountResult(sqlSession.insert(sqlCommand.getStatementId(), parameter));
                break;
            }
            case UPDATE: {
                Object parameter = method.convertArgsToSqlCommandParam(args);
                result = rowCountResult(sqlSession.update(sqlCommand.getStatementId(), parameter));
                break;
            }
            case DELETE: {
                Object parameter = method.convertArgsToSqlCommandParam(args);
                result = rowCountResult(sqlSession.delete(sqlCommand.getStatementId(), parameter));
                break;
            }
            case SELECT: {
                if (method.isReturnMany()) {
                    result = executeForMany(sqlSession, args);
                } else {
                    Object parameter = method.convertArgsToSqlCommandParam(args);
                    result = sqlSession.selectOne(sqlCommand.getStatementId(), parameter);
                }
                break;
            }
            default: {
                throw new BindingException("Unknown execute method for '" + sqlCommand.getStatementId() + "'.");
            }
        }
        // 结果为null, 方法返回值是原始类型(void也是原始类型)
        if(result == null && method.getReturnType().isPrimitive() && !method.isReturnVoid()) {
            throw new BindingException("The method '" + sqlCommand.getStatementId() +
                    "' attempted to assign null to a primitive return type.");
        }
        return result;
    }

    Object rowCountResult(int updateCount) {
        Class<?> returnType = method.getReturnType();
        if (method.isReturnVoid()) {
            return null;
        } else if (returnType == Integer.class || returnType == int.class) {
            return updateCount;
        } else if (returnType == Long.class || returnType == long.class) {
            return updateCount;
        } else if (returnType == Boolean.class || returnType == boolean.class) {
            return updateCount > 0;
        } else {
            throw new BindingException("The method '" + sqlCommand.getStatementId() +
                    "' has a unsupported return type '" + returnType.getName());
        }
    }

    @SuppressWarnings("unchecked")
    private <E> Object executeForMany(SqlSession sqlSession, Object[] args) {
        Object parameter = method.convertArgsToSqlCommandParam(args);
        List<E> result;
        if (method.getRowBoundsIndex() == null) {
            result = sqlSession.selectList(sqlCommand.getStatementId(), parameter);
        } else {
            RowBounds rowBounds = (RowBounds) args[method.getRowBoundsIndex()];
            result = sqlSession.selectList(sqlCommand.getStatementId(), parameter, rowBounds);
        }
        // 数组和其它Collection类型
        Class<?> returnType = method.getReturnType();
        if (!returnType.isAssignableFrom(result.getClass())) {
            ObjectFactory objectFactory = sqlSession.getConfiguration().getObjectFactory();
            if (returnType.isArray()) {
                return convertToAarray(objectFactory, result);
            } else if (Collection.class.isAssignableFrom(returnType)) {
                Object collection = objectFactory.create(returnType);
                ((Collection<E>) collection).addAll(result);
                return collection;
            } else {
                throw new BindingException("The method '" + sqlCommand.getStatementId() +
                        "' has a unsupported return type '" + returnType.getName());
            }
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private <E> Object convertToAarray(ObjectFactory objectFactory, List<E> list) {
        Class<?> componentType = method.getReturnType().getComponentType();
        if (componentType.isPrimitive()) {
            Object arr = objectFactory.createArray(componentType, list.size());
            for (int i = 0; i < list.size(); i++) {
                Array.set(arr, i, list.get(i));
            }
            return arr;
        } else {
            return list.toArray((E[]) objectFactory.createArray(componentType, 0));
        }
    }
}
