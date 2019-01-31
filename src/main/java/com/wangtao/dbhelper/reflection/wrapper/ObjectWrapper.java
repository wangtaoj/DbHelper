package com.wangtao.dbhelper.reflection.wrapper;

import com.wangtao.dbhelper.reflection.factory.ObjectFactory;
import com.wangtao.dbhelper.reflection.property.PropertyTokenizer;

/**
 * 声明对对象进行一系列的操作集合.
 * @author wangtao
 * Created at 2019/1/27 16:42
 */
public interface ObjectWrapper {

    /**
     * 获取属性值
     * 不能使用点操作符, 进行深层次访问属性. 如果需要请使用MetaObject
     * 可以使用[]语法获取map属性中的值或者数组集合中的元素
     * 注:
     * 1) 如果对象是java bean, 当对象中没有此属性时, 会抛出异常.
     * 2) 如果对象是map, map中没有此属性时返回null.
     * 3) 不管对象是java bean还是map, 只要对象存在此属性, 无论此属性值是否为null, 都返回原值.
     * 例子:
     * class: User
     * field: String name, Map map, List<Address> addresses
     * 表达式 "name"             获取    name属性
     * 表达式 "map[key]"         获取    map字段中的key对应的value
     * 表达式 "addresses[0]"     获取    addresses集合的第一个元素
     * @param tokenizer 属性分析器
     * @return 属性值
     */
    Object get(PropertyTokenizer tokenizer);

    /**
     * 设置属性值, 语法同上
     * @param tokenizer 属性分析器
     * @param value 属性值
     */
    void set(PropertyTokenizer tokenizer, Object value);

    /**
     * 判断指定属性是否有对应的setter
     * @param name 属性名
     * @return 存在setter ? true : false
     */
    boolean hasSetter(String name);

    /**
     * 判断指定属性是否有对应的getter
     * @param name 属性名
     * @return 存在getter ? true : false
     */
    boolean hasGetter(String name);

    String[] getSetterNames();

    String[] getGetterNames();

    /**
     * 查找真正的属性名
     * @param name 属性名, 忽视大小写
     * @param useCamelCaseMapping 是否开启下划线转驼峰
     * @return 返回属性名
     */
    String findProperty(String name, boolean useCamelCaseMapping);

    /**
     * 给指定属性赋值, 并返回属性值.
     * MetaObject当使用set赋值时, 出现中间属性为null时, 需要先初始化这个中间属性.
     * 然后继续赋值.
     * @param propName 属性分析器
     * @param objectFactory 对象工厂
     * @return 返回新产生的属性值
     */
    Object instantiateProperty(String propName, ObjectFactory objectFactory);

    /**
     * 获取setter方法参数类型
     * @param name 属性名
     * @return setter参数类型
     */
    Class<?> getSetterType(String name);

    /**
     * 获取getter方法返回值类型
     * @param name 属性名
     * @return getter方法返回值类型
     */
    Class<?> getGetterType(String name);

    /**
     * 是不是集合
     * @return 是集合返回true, 否则返回false
     */
    boolean isCollection();
}
