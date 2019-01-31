package com.wangtao.dbhelper.reflection.wrapper;

import com.wangtao.dbhelper.reflection.MetaClass;
import com.wangtao.dbhelper.reflection.MetaObject;
import com.wangtao.dbhelper.reflection.ReflectionException;
import com.wangtao.dbhelper.reflection.factory.ObjectFactory;
import com.wangtao.dbhelper.reflection.property.PropertyTokenizer;

import java.lang.reflect.InvocationTargetException;

/**
 * @author wangtao
 * Created at 2019/1/27 16:50
 */
public class BeanWrapper extends BaseWrapper {

    private final MetaClass metaClass;

    public BeanWrapper(MetaObject metaObject, Object object) {
        super(metaObject, object);
        this.metaClass = MetaClass.forClass(object.getClass());
    }

    @Override
    public Object get(PropertyTokenizer tokenizer) {
        return super.get(tokenizer);
    }

    @Override
    public void set(PropertyTokenizer tokenizer, Object value) {
        super.set(tokenizer, value);
    }

    @Override
    public String[] getSetterNames() {
        return metaClass.getSetterNames();
    }

    @Override
    public String[] getGetterNames() {
        return metaClass.getGetterNames();
    }

    @Override
    public boolean hasSetter(String name) {
        return metaClass.hasSetter(name);
    }

    @Override
    public boolean hasGetter(String name) {
        return metaClass.hasGetter(name);
    }

    @Override
    public Object instantiateProperty(String propName, ObjectFactory objectFactory) {
        Class<?> type = getSetterType(propName);
        Object newObject = objectFactory.create(type);
        set(new PropertyTokenizer(propName), newObject);
        return newObject;
    }

    @Override
    public String findProperty(String name, boolean useCamelCaseMapping) {
        return metaClass.findPropName(name, useCamelCaseMapping);
    }

    @Override
    public Class<?> getSetterType(String name) {
        return metaClass.getSetterType(name);
    }

    @Override
    public Class<?> getGetterType(String name) {
        return metaClass.getGetterType(name);
    }

    @Override
    public boolean isCollection() {
        return false;
    }

    @Override
    protected Object getPropertyValue(String propName) {
        try {
            return metaClass.getGetter(propName).invoke(object, null);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new ReflectionException("不能从" + object.getClass() + "中获取" + propName + "属性.", e);
        }
    }

    @Override
    protected void setPropertyValue(String propName, Object value) {
        try {
            metaClass.getSetter(propName).invoke(object, new Object[]{value});
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new ReflectionException("不能设置属性值到" + object + "对象的" + propName + "中.", e);
        }
    }

}
