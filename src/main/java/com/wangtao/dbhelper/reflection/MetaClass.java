package com.wangtao.dbhelper.reflection;

import com.wangtao.dbhelper.reflection.invoker.Invoker;

/**
 * @author wangtao
 * Created at 2019/1/16 16:46
 */
public class MetaClass {

    private ReflectorFactory factory;

    private Reflector reflector;

    private final Class<?> type;

    private MetaClass(Class<?> type, ReflectorFactory factory) {
        this.type = type;
        this.factory = factory;
        this.reflector = factory.findForClass(type);
    }

    /**
     * 实列化MetaClass对象
     * @param type 类型
     * @param factory 反射工具工厂
     * @return MetaClass对象
     */
    public static MetaClass forClass(Class<?> type, ReflectorFactory factory) {
        return new MetaClass(type, factory);
    }

    /**
     * 查找类中真正的属性名
     * @param propName 属性名, 忽略大小写
     * @return 真正的属性名, 区分大小写
     */
    public String findPropName(String propName) {
        return findPropName(propName, false);
    }

    /**
     * 查找类中真正的属性名
     * @param propName 属性名, 忽略大小写
     * @param useCamelCaseMapping 是否开启下划线转驼峰
     * @return 真正的属性名, 区分大小写
     */
    public String findPropName(String propName, boolean useCamelCaseMapping) {
        if(useCamelCaseMapping) {
            propName = propName.replace("_", "");
        }
        return reflector.findPropName(propName);
    }

    public boolean hasSetter(String propName) {
        return reflector.hasSetter(propName);
    }

    public boolean hasGetter(String propName) {
        return reflector.hasGetter(propName);
    }

    public Invoker getSetter(String propName) {
        return reflector.getSetInvoker(propName);
    }

    public Invoker getGetter(String propName) {
        return reflector.getGetInvoker(propName);
    }
}
