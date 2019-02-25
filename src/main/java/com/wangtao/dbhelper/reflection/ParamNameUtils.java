package com.wangtao.dbhelper.reflection;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author wangtao
 * Created at 2019/2/25 16:35
 */
public class ParamNameUtils {

    private ParamNameUtils() {

    }

    /**
     * 获取方法参数名字
     * 如果使用JDK1.8编译, 并且加上编译参数-parameters, 那么返回源码文件上的参数名称.
     * 否则返回arg0, arg1...
     * @param method 方法对象
     * @return 参数名字列表
     */
    public static List<String> getParameterNames(Method method) {
        Parameter[] parameters = method.getParameters();
        return Arrays.stream(parameters).map(Parameter::getName).collect(Collectors.toList());
    }
}
