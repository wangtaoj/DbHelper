package com.wangtao.dbhelper.binding;

import com.wangtao.dbhelper.core.Configuration;
import com.wangtao.dbhelper.core.RowBounds;
import com.wangtao.dbhelper.reflection.ParamNameResolver;
import com.wangtao.dbhelper.reflection.TypeParameterResolver;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;

/**
 * 方法签名
 * @author wangtao
 * Created at 2019/2/25 14:46
 */
public class MethodSignature {

    /**
     * 方法没有返回值.
     */
    private final boolean returnVoid;

    /**
     * 方法返回值是集合(Collection)或者数组(Array)
     */
    private final boolean returnMany;

    /**
     * 方法返回值.
     */
    private final Class<?> returnType;

    /**
     * RowBounds参数的位置.
     */
    private final Integer rowBoundsIndex;


    private ParamNameResolver paramNameResolver;


    public MethodSignature(Configuration configuration, Class<?> mapperInteface, Method method) {
        Type resolveReturnType = TypeParameterResolver.resolveReturnType(method, mapperInteface);
        if (resolveReturnType instanceof Class<?>) {
            this.returnType = (Class<?>) resolveReturnType;
        } else if (resolveReturnType instanceof ParameterizedType) {
            this.returnType = (Class<?>) (((ParameterizedType) resolveReturnType).getRawType());
        } else {
            // 泛型数组
            this.returnType = method.getReturnType();
        }
        this.returnVoid = void.class == returnType;
        this.returnMany = Collection.class.isAssignableFrom(returnType) || returnType.isArray();
        this.rowBoundsIndex = getRowBoundsIndex(method, RowBounds.class);
        this.paramNameResolver = new ParamNameResolver(configuration, method);
    }

    public Object convertArgsToSqlCommandParam(Object[] args) {
        return paramNameResolver.getNamedParams(args);
    }

    private Integer getRowBoundsIndex(Method method, Class<?> specialArgClass) {
        Integer result = null;
        Class<?>[] parameterTypes = method.getParameterTypes();
        for (int i = 0; i < method.getParameterCount(); i++) {
            if(specialArgClass.isAssignableFrom(parameterTypes[i])) {
                if(result == null) {
                    result = i;
                } else {
                    throw new BindingException("The method '" + method.getName() + "' in " +
                            method.getDeclaringClass().getName() + " can only one parameter of '"
                            + specialArgClass.getName() + "'.");
                }
            }
        }
        return null;
    }

    public boolean isReturnVoid() {
        return returnVoid;
    }

    public boolean isReturnMany() {
        return returnMany;
    }

    public Class<?> getReturnType() {
        return returnType;
    }

    public Integer getRowBoundsIndex() {
        return rowBoundsIndex;
    }
}
