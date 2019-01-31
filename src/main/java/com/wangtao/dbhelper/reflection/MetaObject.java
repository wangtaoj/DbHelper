package com.wangtao.dbhelper.reflection;

import com.wangtao.dbhelper.reflection.factory.DefaultObjectFactory;
import com.wangtao.dbhelper.reflection.factory.ObjectFactory;
import com.wangtao.dbhelper.reflection.property.PropertyTokenizer;
import com.wangtao.dbhelper.reflection.wrapper.BeanWrapper;
import com.wangtao.dbhelper.reflection.wrapper.CollectionWrapper;
import com.wangtao.dbhelper.reflection.wrapper.MapWrapper;
import com.wangtao.dbhelper.reflection.wrapper.ObjectWrapper;

import java.util.Collection;
import java.util.Map;

/**
 * @author wangtao
 * Created at 2019/1/27 16:45
 */
public class MetaObject {

    private Object originalObject;

    private ObjectWrapper objectWrapper;

    private ObjectFactory objectFactory;

    @SuppressWarnings("unchecked")
    private MetaObject(Object object, ObjectFactory objectFactory) {
        this.originalObject = object;
        this.objectFactory = objectFactory;
        if (object instanceof Map) {
            objectWrapper = new MapWrapper(this, (Map<String, Object>) object);
        } else if(object instanceof Collection) {
            objectWrapper = new CollectionWrapper((Collection)object);
        } else {
            objectWrapper = new BeanWrapper(this, object);
        }
    }

    public static MetaObject forObject(Object object, ObjectFactory objectFactory) {
        return new MetaObject(object, objectFactory);
    }

    public static MetaObject forObject(Object object) {
        return forObject(object, new DefaultObjectFactory());
    }

    /**
     * 获取属性值, 支持点操作符进行对象导航, 支持[]语法获取数组、集合、map中的元素.
     * 注:
     * 1) 如果对象是java bean, 当对象中没有此属性时, 会抛出异常.
     * 2) 如果对象是map, map中没有此属性时返回null.
     * 3) 不管对象是java bean还是map, 只要对象存在此属性, 无论此属性值是否为null, 都返回原值.
     * @param name 属性名
     * @return 属性值
     * @throws ReflectionException 导航属性时, 对象不存在此属性时将抛出反射异常.
     */
    public Object getValue(String name) {
        PropertyTokenizer tokenizer = new PropertyTokenizer(name);
        if (tokenizer.hasNext()) {
            Object currentValue = getValue(tokenizer.getIndexName());
            // 中间值不为null, 继续寻找
            if (currentValue != null) {
                MetaObject metaObject = MetaObject.forObject(currentValue);
                return metaObject.getValue(tokenizer.getChildren());
            } else {
                return null;
            }
        } else {
            return objectWrapper.get(tokenizer);
        }
    }

    /**
     * 给指定属性赋值, 支持点操作符进行对象导航, 支持[]语法给数组、集合、map中的元素赋值.
     * 1) 如果对象是java bean, 当对象中没有此属性时, 会抛出异常.
     * 2) 如果对象是map, 当对象中没有此属性时, 会调用map.put(key, value)存放此属性值.
     * 3) 在赋值时, 中间属性(对象存在此属性)为null的情况.
     *    3.1 对象为map, 因为中间属性为null, 不知道类型, 将中间属性初始化一个map, 然后存放接下来的值.
     *    3.2 对象为java bean, 将根据此属性的类型初始化.
     *    3.3 对数组元素或者List元素赋值时, 但是数组或者List本身为null, 将抛出异常.
     * @param name  属性名
     * @param value 属性值
     */
    public void setValue(String name, Object value) {
        PropertyTokenizer tokenizer = new PropertyTokenizer(name);
        if (tokenizer.hasNext()) {
            Object currentValue = getValue(tokenizer.getIndexName());
            // currentValue = null, 先初始化
            if (currentValue == null && value != null) {
                currentValue = objectWrapper.instantiateProperty(tokenizer.getName(), objectFactory);
            }
            // 当value = null, currentValue也许还是null, 再做一个判断
            if(currentValue != null) {
                MetaObject metaObject = MetaObject.forObject(currentValue);
                metaObject.setValue(tokenizer.getChildren(), value);
            }
        } else {
            objectWrapper.set(tokenizer, value);
        }
    }

    /**
     * 获取可读属性集合.
     * @return 可读属性数组
     */
    public String[] getGetterNames() {
        return objectWrapper.getGetterNames();
    }

    /**
     * 获取可写属性集合.
     * @return 可写属性数组
     */
    public String[] getSetterNames() {
        return objectWrapper.getSetterNames();
    }

    /**
     * 是否可以对指定属性赋值.
     * 支持点操作符导航对象, 支持数组语法.
     * @param name 属性表达式
     * @return true/false
     */
    public boolean hasSetter(String name) {
        return objectWrapper.hasSetter(name);
    }

    /**
     * 是否可以获取指定属性.
     * 支持点操作符导航对象, 支持数组语法.
     * @param name 属性表达式.
     * @return true/false
     */
    public boolean hasGetter(String name) {
        return objectWrapper.hasGetter(name);
    }

    /**
     * 获取指定属性的getter方法返回值类型.
     * 支持点操作符导航对象, 支持数组语法.
     * @param name 属性表达式
     * @return 返回getter方法返回值.
     */
    public Class<?> getGetterType(String name) {
        return objectWrapper.getGetterType(name);
    }

    /**
     * 获取指定属性的setter方法参数类型.
     * 支持点操作符导航对象, 支持数组语法.
     * @param name 属性表达式
     * @return 返回setter方法参数类型.
     */
    public Class<?> getSetterType(String name) {
        return objectWrapper.getSetterType(name);
    }

    public Object getOriginalObject() {
        return originalObject;
    }

    public ObjectFactory getObjectFactory() {
        return objectFactory;
    }
}
