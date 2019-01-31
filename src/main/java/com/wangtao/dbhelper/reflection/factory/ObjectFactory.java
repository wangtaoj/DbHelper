package com.wangtao.dbhelper.reflection.factory;

import java.util.List;

/**
 * 对象工厂, 对于新的对象, 将使用这个工厂类实例化对象.
 * @author wangtao
 * Created at 2019/1/27 13:07
 */
public interface ObjectFactory {

    /**
     * 使用默认的无参构造方法实例化对象.
     * @param clazz Class对象
     * @param <T> 泛型参数
     * @return 对应的Java对象
     */
    <T> T create(Class<T> clazz);

    /**
     * 创建一个指定大小的数组或者List.
     * @param clazz 数组的component type 或者 List.class
     * @param length 数组长度或者List的大小.
     * @return 返回一个数组或者一个List.
     */
    Object create(Class<?> clazz, int length);

    /**
     * 根据指定的构造方法实例化对象.
     * @param clazz Class对象
     * @param constructorClasses 构造方法的参数类型, 用于获取指定的构造方法
     * @param constructorArgs 构造方法参数
     * @param <T> 泛型参数
     * @return 对象的Java对象
     */
    <T> T create(Class<T> clazz, List<Class<?>> constructorClasses, List<Object> constructorArgs);
}
