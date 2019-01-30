package com.wangtao.dbhelper.reflection;

import com.wangtao.dbhelper.reflection.invoker.Invoker;
import com.wangtao.dbhelper.reflection.property.PropertyTokenizer;

/**
 * @author wangtao
 * Created at 2019/1/16 16:46
 */
public class MetaClass {

    private final ReflectorFactory reflectorFactory;

    private final Reflector reflector;

    private final Class<?> type;

    private MetaClass(Class<?> type, ReflectorFactory reflectorFactory) {
        this.type = type;
        this.reflectorFactory = reflectorFactory;
        this.reflector = reflectorFactory.findForClass(type);
    }

    /**
     * 实列化MetaClass对象
     * @param type    类型
     * @param factory 反射工具工厂
     * @return MetaClass对象
     */
    public static MetaClass forClass(Class<?> type, ReflectorFactory factory) {
        return new MetaClass(type, factory);
    }

    public static MetaClass forClass(Class<?> type) {
        return forClass(type, new DefaultReflectorFactory());
    }

    public MetaClass metaClassForProperty(String propName) {
        Class<?> propertyType = reflector.getGetterReturnType(propName);
        return MetaClass.forClass(propertyType, reflectorFactory);
    }

    /**
     * 获取可读属性数组
     * @return 包含可读属性的数组
     */
    public String[] getGetterNames() {
        return reflector.getReadablePropertyNames();
    }

    /**
     * 获取可写属性数组
     * @return 包含可写属性的数组
     */
    public String[] getSetterNames() {
        return reflector.getWriteablePropertyName();
    }

    /**
     * 查找类中真正的属性名, 可以使用点操作符嵌套查找.
     * @param propName 属性名, 忽略大小写
     * @return 真正的属性名, 区分大小写
     */
    public String findPropName(String propName) {
        StringBuilder propBuilder = buildProperty(propName);
        return propBuilder.length() == 0 ? null : propBuilder.toString();
    }

    /**
     * 查找类中真正的属性名
     * @param propName            属性名, 忽略大小写
     * @param useCamelCaseMapping 是否开启下划线转驼峰
     * @return 真正的属性名, 区分大小写
     */
    public String findPropName(String propName, boolean useCamelCaseMapping) {
        if (useCamelCaseMapping) {
            propName = propName.replace("_", "");
        }
        return reflector.findPropName(propName);
    }

    /**
     * 判断属性表达式是否有对应的setter, 可以使用点操作符嵌套查找.
     * 对于数组"arr[0]"表达式, 只要对象里有arr属性, 也返回true.
     * @param propName 属性名
     * @return 存在setter ? true : false
     */
    public boolean hasSetter(String propName) {
        PropertyTokenizer tokenizer = new PropertyTokenizer(propName);
        if (tokenizer.hasNext()) {
            if (reflector.hasSetter(tokenizer.getName())) {
                MetaClass metaClass = metaClassForProperty(tokenizer.getName());
                return metaClass.hasSetter(tokenizer.getChildren());
            } else {
                return false;
            }
        } else {
            // 传tokenizer.getName()而不是propName
            // arr[1] tokenizer.getName() = arr, propName = arr[1]
            return reflector.hasSetter(tokenizer.getName());
        }
    }

    /**
     * 判断属性表达式是否有对应的getter, 可使用点操作符嵌套查找
     * 对于数组"arr[0]"表达式, 只要对象里有arr属性, 也返回true.
     * @param propName 属性名
     * @return 存在getter ? true : false
     */
    public boolean hasGetter(String propName) {
        PropertyTokenizer tokenizer = new PropertyTokenizer(propName);
        if (tokenizer.hasNext()) {
            if (reflector.hasGetter(tokenizer.getName())) {
                MetaClass metaClass = metaClassForProperty(tokenizer.getName());
                return metaClass.hasGetter(tokenizer.getChildren());
            } else {
                return false;
            }
        } else {
            return reflector.hasGetter(tokenizer.getName());
        }
    }

    public Invoker getSetter(String propName) {
        return reflector.getSetInvoker(propName);
    }

    public Invoker getGetter(String propName) {
        return reflector.getGetInvoker(propName);
    }

    /**
     * 获取属性的setter方法参数类型, 支持点操作符.
     * 如果使用了数组语法arr[1], 返回的是arr属性对应的setter方法参数类型.
     * @param propName 属性名
     * @return setter方法参数类型
     */
    public Class<?> getSetterType(String propName) {
        PropertyTokenizer tokenizer = new PropertyTokenizer(propName);
        if (tokenizer.hasNext()) {
            MetaClass metaClass = metaClassForProperty(tokenizer.getName());
            return metaClass.getSetterType(tokenizer.getChildren());
        }
        return reflector.getSetterParamType(tokenizer.getName());
    }

    /**
     * 获取属性的getter方法返回值类型, 支持点操作符.
     * 如果使用了数组语法arr[1], 返回的是arr属性对应的getter方法返回值类型.
     * @param propName 属性名
     * @return getter方法返回值类型
     */
    public Class<?> getGetterType(String propName) {
        PropertyTokenizer tokenizer = new PropertyTokenizer(propName);
        if (tokenizer.hasNext()) {
            MetaClass metaClass = metaClassForProperty(tokenizer.getName());
            return metaClass.getSetterType(tokenizer.getChildren());
        }
        return reflector.getGetterReturnType(tokenizer.getName());
    }

    /**
     * 检查是否存在无参构造函数.
     * @return 存在返回true, 否则返回false
     */
    public boolean hasDefaultConstructor() {
        return reflector.getDefaultConstructor() != null;
    }

    public Class<?> getType() {
        return type;
    }

    public ReflectorFactory getReflectorFactory() {
        return reflectorFactory;
    }

    private StringBuilder buildProperty(String name) {
        PropertyTokenizer tokenizer = new PropertyTokenizer(name);
        if (tokenizer.hasNext()) {
            String propName = reflector.findPropName(tokenizer.getName());
            if (propName == null) {
                return new StringBuilder();
            } else {
                // 递归查找
                StringBuilder propBuilder = new StringBuilder(propName + '.');
                MetaClass metaClass = metaClassForProperty(propName);
                StringBuilder temp = metaClass.buildProperty(tokenizer.getChildren());
                // 中间属性不存在, 返回空.
                if (temp.length() == 0) {
                    return new StringBuilder();
                }
                return propBuilder.append(temp);
            }
        } else {
            String propName = reflector.findPropName(name);
            if (propName != null) {
                return new StringBuilder().append(propName);
            } else {
                return new StringBuilder();
            }
        }
    }
}
