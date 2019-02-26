package com.wangtao.dbhelper.binding;

import com.wangtao.dbhelper.core.SqlSession;

import java.util.HashMap;
import java.util.Map;

/**
 * @author wangtao
 * Created at 2019/2/26 15:13
 */
public class MapperRegistry {

    private final Map<Class<?>, MapperProxyFactory<?>> mappers = new HashMap<>();

    @SuppressWarnings("unchecked")
    public <T> T getMapper(Class<T> mapperInterface, SqlSession sqlSession) {
        MapperProxyFactory<T> mapperProxyFactory = (MapperProxyFactory<T>) mappers.get(mapperInterface);
        if (mapperProxyFactory == null) {
            throw new BindingException("The interface '" + mapperInterface.getName() + "' is not found in MapperRegistry");
        }
        try {
            return mapperProxyFactory.newInstance(sqlSession);
        } catch (RuntimeException e) {
            throw new BindingException("Error get the instance of mapper interface '" + mapperInterface + "'.", e);
        }
    }

    public boolean hasMapper(Class<?> mapperInterface) {
        return mappers.containsKey(mapperInterface);
    }

    public <T> void addMapper(Class<T> mapperInterface) {
        if (mapperInterface.isInterface()) {
            if (hasMapper(mapperInterface)) {
                throw new BindingException("The interface '" + mapperInterface.getName() + "' have already been binded.");
            }
            mappers.put(mapperInterface, new MapperProxyFactory<>(mapperInterface));
        } else {
            throw new BindingException("The class '" + mapperInterface.getName() + "' is not a interface.");
        }
    }
}
