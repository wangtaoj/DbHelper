package com.wangtao.dbhelper.reflection.property;

import com.wangtao.dbhelper.reflection.ReflectionException;

import java.util.Locale;

/**
 * @author wangtao
 * Created at 2019/1/28 10:03
 */
public class PropertyNamer {

    private PropertyNamer() {

    }

    /**
     * 判断此方法名是不是一个getter方法
     * getXXXX, isXXXX
     * @param methodName 方法名
     * @return 是getter方法 ? true : false
     */
    public static boolean isGetter(String methodName) {
        boolean flag = methodName != null && methodName.startsWith("get") && methodName.length() > 3;
        flag = flag || (methodName != null && methodName.startsWith("is") && methodName.length() > 2);
        return flag;
    }

    /**
     * 判断此方法名是不是一个setter方法
     * setXXXX
     * @param methodName 方法名
     * @return 是setter方法 ? true : false
     */
    public static boolean isSetter(String methodName) {
        return methodName != null && methodName.startsWith("set") && methodName.length() > 3;
    }

    /**
     * 从getter/setter方法名中获取属性名字
     * 注:
     * 1. 如果属性首字母小写, 第二个字母大写时. uName对应的是setuName, getuName
     * 2. 如果属性首字母以及第二个字母都大写时. URL对应的是setURL, getURL
     * @param methodName 方法名字
     * @return 属性名字
     */
    public static String methodToProperty(String methodName) {
        String propertyName;
        if (methodName.startsWith("is")) {
            propertyName = methodName.substring(2);
        } else if (methodName.startsWith("get") || methodName.startsWith("set")) {
            propertyName = methodName.substring(3);
        } else {
            throw new ReflectionException("解析属性名失败, " + methodName + "不是以set, get, is开头.");
        }
        if(propertyName.isEmpty()) {
            throw new ReflectionException("解析" + methodName + "的属性名为空, 请遵循javaBean标准来命令setter, getter方法");
        }
        if (propertyName.length() == 1 || (!Character.isUpperCase(propertyName.charAt(1)))) {
            propertyName = propertyName.substring(0, 1).toLowerCase(Locale.ENGLISH) + propertyName.substring(1);
        }
        return propertyName;
    }
}
