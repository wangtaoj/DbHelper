package com.wangtao.dbhelper.reflection;

import com.wangtao.dbhelper.annotations.Param;
import com.wangtao.dbhelper.core.Configuration;
import com.wangtao.dbhelper.core.ParamMap;
import com.wangtao.dbhelper.core.RowBounds;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * @author wangtao
 * Created at 2019/2/25 16:40
 */
public class ParamNameResolver {

    /**
     * key: 参数索引.
     * value: 参数名字.
     * 注:
     * 1. 如果参数指定了@Param注解, 那么value = 该注解指定的名字.
     * 2. 如果useActualParamName = true, value = 源码文件书写的参数名称.
     * 3. value = 参数索引.
     * 优先级: 1 > 2 > 3
     * 对于第二个需要JDK1.8, 并且开启-parameters编译参数, 否则结果是arg0, arg1...
     */
    private SortedMap<Integer, String> names;

    private boolean hasAnnotation;

    public ParamNameResolver(Configuration configuration, Method method) {
        final Class<?>[] parameterTypes = method.getParameterTypes();
        final int parameterCount = method.getParameterCount();
        final Annotation[][] paramAnnotations = method.getParameterAnnotations();
        SortedMap<Integer, String> map = new TreeMap<>();
        for (int i = 0; i < parameterCount; i++) {
            if (isSpecialParameter(parameterTypes[i])) {
                continue;
            }
            String name = null;
            for (Annotation annotation : paramAnnotations[i]) {
                if (annotation instanceof Param) {
                    name = ((Param) annotation).value();
                    hasAnnotation = true;
                    break;
                }
            }
            if (name == null && configuration.isUseActualParamName()) {
                name = ParamNameUtils.getParameterNames(method).get(i);
            }
            if (name == null) {
                name = String.valueOf(i);
            }
            map.put(i, name);
        }
        this.names = Collections.unmodifiableSortedMap(map);
    }

    /**
     * 获取所有参数名字
     * @return 参数列表
     */
    public String[] getNames() {
        return names.values().toArray(new String[0]);
    }

    /**
     * 将参数名字和参数值一一对应起来
     * 如果是单参数并且没有使用Param注解, 返回参数本身, 否则返回ParamMap
     * @param args 参数数组
     * @return 如果是单参数并且没有使用Param注解, 返回参数本身, 否则返回ParamMap
     */
    public Object getNamedParams(Object[] args) {
        int parameterCount = names.size();
        if (args == null || parameterCount == 0) {
            return null;
        } else if (!hasAnnotation && parameterCount == 1) {
            return args[0];
        } else {
            ParamMap map = new ParamMap();
            for (Map.Entry<Integer, String> entry : names.entrySet()) {
                map.put(entry.getValue(), args[entry.getKey()]);
            }
            return map;
        }
    }

    private boolean isSpecialParameter(Class<?> parameterType) {
        return parameterType.isAssignableFrom(RowBounds.class);
    }
}
