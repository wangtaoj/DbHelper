package com.wangtao.dbhelper.binding;

import com.wangtao.dbhelper.core.SqlSession;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author wangtao
 * Created at 2019/2/26 14:59
 */
public class MapperProxyFactory<T> {

    private Class<T> mapperInterface;

    /**
     * 缓存方法信息, 因为方法信息是不变的.
     * 没有缓存代理对象, 因为代理对象可能不需要单例, 有需要可在应用层缓存.
     */
    private Map<Method, MapperMethod> methodCache;

    public MapperProxyFactory(Class<T> mapperInterface) {
        this.mapperInterface = mapperInterface;
        this.methodCache = new ConcurrentHashMap<>();
    }

    /**
     * 创建代理对象.
     * @param sqlSession 会话
     * @return 返回代理对象
     */
    @SuppressWarnings("unchecked")
    public T newInstance(SqlSession sqlSession) {
        final MapperProxy mapperProxy = new MapperProxy(sqlSession, mapperInterface, methodCache);
        return (T) Proxy.newProxyInstance(mapperInterface.getClassLoader(),
                new Class<?>[]{mapperInterface}, mapperProxy);
    }
}
