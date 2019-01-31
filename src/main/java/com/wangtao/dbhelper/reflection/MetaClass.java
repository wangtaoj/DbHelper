package com.wangtao.dbhelper.reflection;

import com.wangtao.dbhelper.reflection.invoker.GetFieldInvoker;
import com.wangtao.dbhelper.reflection.invoker.Invoker;
import com.wangtao.dbhelper.reflection.invoker.MethodInvoker;
import com.wangtao.dbhelper.reflection.property.PropertyTokenizer;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;

/**
 * 所有的结果都是根据编译时的类型分析得到的.
 * 此类并没有setValue, getValue方法, 获取值的操作应该使用MetaObject.
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
     * 暂不支持[]语法
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
     * 支持数组, Collection, Map的数组语法[]
     * 如果以[]语法结尾, 如user.arr[0], 或者arr[0], 那么将查找的是arr.
     * @param propName 属性名
     * @return 存在setter ? true : false
     */
    public boolean hasSetter(String propName) {
        PropertyTokenizer tokenizer = new PropertyTokenizer(propName);
        if (tokenizer.hasNext()) {
            if (reflector.hasSetter(tokenizer.getName())) {
                MetaClass metaClass = metaClassForProperty(tokenizer.getIndexName());
                return metaClass.hasSetter(tokenizer.getChildren());
            } else {
                return false;
            }
        } else {
            // 如果以数组语法结尾, 会去掉数组语法. arr[0] -> arr
            return reflector.hasSetter(tokenizer.getName());
        }
    }

    /**
     * 判断属性表达式是否有对应的getter, 可使用点操作符嵌套查找
     * 支持数组, Collection, Map的数组语法[].
     * 如果以[]语法结尾, 如user.arr[0], 或者arr[0], 那么将查找的是arr.
     * @param propName 属性名
     * @return 存在getter ? true : false
     */
    public boolean hasGetter(String propName) {
        PropertyTokenizer tokenizer = new PropertyTokenizer(propName);
        if (tokenizer.hasNext()) {
            if (reflector.hasGetter(tokenizer.getName())) {
                MetaClass metaClass = metaClassForProperty(tokenizer.getIndexName());
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
     * 获取属性的setter方法参数类型, 可使用点操作符嵌套查找.
     * 支持数组, Collection, Map的数组语法[].
     * @param propName 属性名
     * @return setter方法参数类型
     */
    public Class<?> getSetterType(String propName) {
        PropertyTokenizer tokenizer = new PropertyTokenizer(propName);
        if (tokenizer.hasNext()) {
            MetaClass metaClass = metaClassForProperty(tokenizer.getIndexName());
            return metaClass.getSetterType(tokenizer.getChildren());
        }
        // 存在数组语法, 解析元素类型
        if (tokenizer.getIndex() != null) {
            return resolveElementTypeOfArrayOrCollection(tokenizer);
        } else {
            return reflector.getSetterParamType(propName);
        }
    }

    /**
     * 获取属性的getter方法返回值类型, 支持点操作符.
     * 支持数组, Collection, Map的数组语法[].
     * @param propName 属性名
     * @return getter方法返回值类型
     */
    public Class<?> getGetterType(String propName) {
        PropertyTokenizer tokenizer = new PropertyTokenizer(propName);
        if (tokenizer.hasNext()) {
            MetaClass metaClass = metaClassForProperty(tokenizer.getIndexName());
            return metaClass.getSetterType(tokenizer.getChildren());
        }
        // 存在数组语法, 解析元素类型
        if (tokenizer.getIndex() != null) {
            return resolveElementTypeOfArrayOrCollection(tokenizer);
        } else {
            return reflector.getGetterReturnType(propName);
        }
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

    /**
     * 解析带有数组语法的元素类型.
     * 1. 如果集合(Collection)使用的是原生类型, 返回Object.class.
     * 2. 如果Map使用的是原生类型, 返回Object.class.
     * <pre>{@code
     * class User {
     *     public String name;
     *     public List<Address> addressList;
     *     public Address[] addresses;
     *     public List items;
     * }
     * class Address {
     *     public String name;
     * }
     * MetaClass metaClass = MetaClass.forClass(User.class);
     * metaClass.resolveElementTypeOfArrayOrCollection(new PropertyTokenizer("addresses[0]"));
     * the result is Address.class
     *
     * metaClass.resolveElementTypeOfArrayOrCollection(new PropertyTokenizer("addressList[0]"));
     * the result is Address.class
     *
     * metaClass.resolveElementTypeOfArrayOrCollection(new PropertyTokenizer("item[0]"));
     * the result is Object.class
     * }</pre>
     * @param tokenizer 属性分析器
     * @return 返回数组元素或者集合元素的类型.
     */
    private Class<?> resolveElementTypeOfArrayOrCollection(PropertyTokenizer tokenizer) {
        String propName = tokenizer.getName();
        Type arrayType = getGenericGetterType(propName);
        // 是原始类型
        if (arrayType instanceof Class<?>) {
            Class<?> arrayClass = (Class<?>) arrayType;
            if (arrayClass.isArray()) {
                // String[], 则返回String.
                return arrayClass.getComponentType();
            } else if (Collection.class.isAssignableFrom(arrayClass)) {
                // 集合使用的是原始类型, 没有带泛型, 集合元素类型返回Object
                return Object.class;
            } else if (Map.class.isAssignableFrom(arrayClass)) {
                // Map使用的是原始类型, 没有带泛型, Value类型返回Object.
                return Object.class;
            }
        } else if (arrayType instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) arrayType;
            Class<?> rawType = (Class<?>) parameterizedType.getRawType();
            if (Collection.class.isAssignableFrom(rawType)) {
                return (Class<?>) parameterizedType.getActualTypeArguments()[0];
            } else if (Map.class.isAssignableFrom(rawType)) {
                return (Class<?>) parameterizedType.getActualTypeArguments()[1];
            }
        }
        throw new ReflectionException("我们期待此属性(" + propName + ")的类型是一个数组, Collection, Map. 实际上是"
                + arrayType.getClass());
    }

    private MetaClass metaClassForProperty(String propName) {
        PropertyTokenizer tokenizer = new PropertyTokenizer(propName);
        Class<?> propertyType;
        if (tokenizer.getIndex() != null) {
            // 存在数组语法, 需要解析属性类型并且带泛型信息.
            propertyType = resolveElementTypeOfArrayOrCollection(tokenizer);
        } else {
            propertyType = reflector.getGetterReturnType(tokenizer.getName());
        }
        return MetaClass.forClass(propertyType, reflectorFactory);
    }

    private Type getGenericGetterType(String propName) {
        // 可通过Invoker实现类来获取此属性对应的getter method或者field.
        Invoker invoker = reflector.getGetInvoker(propName);
        try {
            if (invoker instanceof MethodInvoker) {
                Field _method = ((MethodInvoker) invoker).getClass().getDeclaredField("method");
                _method.setAccessible(true);
                Method getter = (Method) _method.get(invoker);
                return TypeParameterResolver.resolveReturnType(getter, type);
            } else {
                Field _field = ((GetFieldInvoker) invoker).getClass().getDeclaredField("field");
                _field.setAccessible(true);
                Field realField = (Field) _field.get(invoker);
                return TypeParameterResolver.resolveFieldType(realField, type);
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new ReflectionException("", e);
        }
    }
}
