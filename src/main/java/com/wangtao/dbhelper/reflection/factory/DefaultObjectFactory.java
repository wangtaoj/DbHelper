package com.wangtao.dbhelper.reflection.factory;

import com.wangtao.dbhelper.reflection.ReflectionException;

import java.lang.reflect.Constructor;
import java.util.*;

/**
 * @author wangtao
 * Created at 2019/1/27 13:13
 */
public class DefaultObjectFactory implements ObjectFactory {

    @Override
    public <T> T create(Class<T> clazz) {
        return create(clazz, null, null);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T create(Class<T> clazz, List<Class<?>> constructorClasses, List<Object> constructorArgs) {
        Class<?> clazzToCreate = resloveInterface(clazz);
        return (T) instantiateClass(clazzToCreate, constructorClasses, constructorArgs);
    }

    private <T> T instantiateClass(Class<T> clazz, List<Class<?>> constructorClasses, List<Object> constructorArgs) {
        try {
            // 获取无参构造方法
            if (constructorClasses == null || constructorArgs == null) {
                Constructor<T> constructor = clazz.getDeclaredConstructor();
                if (!constructor.isAccessible()) {
                    constructor.setAccessible(true);
                }
                return constructor.newInstance();
            } else {
                // 获取指定构造方法
                Constructor<T> constructor = clazz.getDeclaredConstructor(constructorClasses.toArray(new Class<?>[0]));
                return constructor.newInstance(constructorArgs.toArray());
            }
        } catch (Exception e) {
            StringBuilder classesBuilder = new StringBuilder();
            if (constructorClasses != null) {
                for (Class<?> type : constructorClasses) {
                    classesBuilder.append(type.getSimpleName()).append(',');
                }
                classesBuilder.deleteCharAt(classesBuilder.length() - 1);
            }
            StringBuilder argsBuilder = new StringBuilder();
            if (constructorArgs != null) {
                for (Object arg : constructorArgs) {
                    argsBuilder.append(String.valueOf(arg)).append(',');
                }
                argsBuilder.deleteCharAt(argsBuilder.length() - 1);
            }
            throw new ReflectionException("错误的实例化" + clazz.getName() + "对象, 可能涉及无效的构造方法参数类型[" +
                    classesBuilder.toString() + "] 或者构造方法参数[" + argsBuilder + "].", e);
        }
    }

    private Class<?> resloveInterface(Class<?> clazz) {
        Class<?> result;
        if (clazz == List.class || clazz == Collection.class) {
            result = ArrayList.class;
        } else if (clazz == SortedSet.class) {
            result = TreeSet.class;
        } else if (clazz == Set.class) {
            result = HashSet.class;
        } else if (clazz == Map.class) {
            result = HashMap.class;
        } else {
            result = clazz;
        }
        return result;
    }
}
