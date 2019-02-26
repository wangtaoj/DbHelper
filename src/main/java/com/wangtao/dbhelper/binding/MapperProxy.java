package com.wangtao.dbhelper.binding;

import com.wangtao.dbhelper.core.SqlSession;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * 代理逻辑, 会调用MapperMethod的execute方法执行真正的SQL方法.
 * @author wangtao
 * Created at 2019/2/26 14:19
 */
public class MapperProxy implements InvocationHandler {

    private final SqlSession sqlSession;

    /**
     * 绑定接口类
     */
    private final Class<?> mapperInteface;

    /**
     * 一个方法缓存(ConcurrentHashMap对象), 从外部传入.
     */
    private final Map<Method, MapperMethod> methodCache;

    public MapperProxy(SqlSession sqlSession, Class<?> mapperInteface, Map<Method, MapperMethod> methodCache) {
        this.sqlSession = sqlSession;
        this.mapperInteface = mapperInteface;
        this.methodCache = methodCache;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        /*
         * 参数不能为proxy, 否则无限递归调用
         * proxy为生成的代理对象, 会继承Proxy类.
         * equals方法内部实现是super.h.invoke(this, method, args)
         * super: Proxy对象
         * h: InvocationHandler对象(MapperProxy)
         */
        if (method.getDeclaringClass() == Object.class) {
            // 执行Object中的方法
            return method.invoke(this, args);
        } else if (method.isDefault()) {
            // 执行默认方法
            return method.invoke(proxy, args);
        }
        MapperMethod mapperMethod = methodCache.computeIfAbsent(method,
                k -> new MapperMethod(mapperInteface, k, sqlSession.getConfiguration()));
        return mapperMethod.execute(sqlSession, args);
    }

}
