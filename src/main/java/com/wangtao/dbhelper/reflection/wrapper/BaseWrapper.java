package com.wangtao.dbhelper.reflection.wrapper;

import com.wangtao.dbhelper.reflection.MetaObject;
import com.wangtao.dbhelper.reflection.ReflectionException;
import com.wangtao.dbhelper.reflection.property.PropertyTokenizer;

import java.util.List;
import java.util.Map;

/**
 * @author wangtao
 * Created at 2019/1/27 17:22
 */
public abstract class BaseWrapper implements ObjectWrapper {

    protected final MetaObject metaObject;

    protected final Object object;

    protected BaseWrapper(MetaObject metaObject, Object object) {
        this.metaObject = metaObject;
        this.object = object;
    }

    // 模板方法模式.
    @Override
    public Object get(PropertyTokenizer tokenizer) {
        // 存在数组语法
        if (tokenizer.getIndex() != null) {
            // 获取集合、数组或者map对象
            Object colletionTemp = resolveCollection(tokenizer, object);
            if (colletionTemp == null) {
                return null;
                /*throw new ReflectionException("当使用数组语法获取属性中的元素时, 此属性本身值为null, 属性名为"
                        + tokenizer.getName() + ", 存在于'" + tokenizer.getFullname() + "'这个表达式中.");*/
            }
            return getValueFromCollection(tokenizer, colletionTemp);
        } else {
            return getPropertyValue(tokenizer.getName());
        }
    }

    @Override
    public void set(PropertyTokenizer tokenizer, Object value) {
        // 存在数组语法
        if (tokenizer.getIndex() != null) {
            Object colletionTemp = resolveCollection(tokenizer, object);
            if (colletionTemp == null) {
                throw new UnsupportedOperationException("在给数组或者List元素赋值时, 数组或者元素本身为null, 属性为:" +
                        tokenizer.getName());
            }
            setCollectionValue(tokenizer, colletionTemp, value);
        } else {
            setPropertyValue(tokenizer.getName(), value);
        }
    }

    protected abstract Object getPropertyValue(String propName);

    protected abstract void setPropertyValue(String propName, Object value);

    protected Object resolveCollection(PropertyTokenizer tokenizer, Object object) {
        // 属性名为空, 返回原对象. 如[key], name便是空串, 访问的是当前对象的key属性
        if (tokenizer.getName().isEmpty()) {
            return object;
        }
        // 返回属性值
        return getPropertyValue(tokenizer.getName());
    }

    /**
     * 数组语法[]获取属性
     */
    protected Object getValueFromCollection(PropertyTokenizer tokenizer, Object collection) {
        String index = tokenizer.getIndex();
        if (collection instanceof Map) {
            return ((Map) collection).get(index);
        } else {
            int i = Integer.parseInt(index);
            if (collection instanceof Object[]) {
                return ((Object[]) collection)[i];
            } else if (collection instanceof List) {
                return ((List) collection).get(i);
            } else if (collection instanceof byte[]) {
                return ((byte[]) collection)[i];
            } else if (collection instanceof short[]) {
                return ((short[]) collection)[i];
            } else if (collection instanceof int[]) {
                return ((int[]) collection)[i];
            } else if (collection instanceof long[]) {
                return ((long[]) collection)[i];
            } else if (collection instanceof float[]) {
                return ((float[]) collection)[i];
            } else if (collection instanceof double[]) {
                return ((double[]) collection)[i];
            } else if (collection instanceof boolean[]) {
                return ((boolean[]) collection)[i];
            } else if (collection instanceof char[]) {
                return ((char[]) collection)[i];
            } else {
                throw new ReflectionException("在解析" + tokenizer.getFullname() +
                        "发生错误," + "'属性" + tokenizer.getName() + "' 不是一个array或者list或者map");
            }
        }
    }

    @SuppressWarnings("unchecked")
    protected void setCollectionValue(PropertyTokenizer tokenizer, Object collection, Object value) {
        if (collection instanceof Map) {
            ((Map) collection).put(tokenizer.getIndex(), value);
        } else {
            int i = Integer.parseInt(tokenizer.getIndex());
            if (collection instanceof List) {
                ((List) collection).set(i, value);
            } else if (collection instanceof Object[]) {
                ((Object[]) collection)[i] = value;
            } else if (collection instanceof byte[]) {
                ((byte[]) collection)[i] = (byte) value;
            } else if (collection instanceof double[]) {
                ((double[]) collection)[i] = (double) value;
            } else if (collection instanceof float[]) {
                ((float[]) collection)[i] = (float) value;
            } else if (collection instanceof int[]) {
                ((int[]) collection)[i] = (int) value;
            } else if (collection instanceof long[]) {
                ((long[]) collection)[i] = (long) value;
            } else if (collection instanceof short[]) {
                ((short[]) collection)[i] = (short) value;
            } else if (collection instanceof char[]) {
                ((char[]) collection)[i] = (char) value;
            } else if (collection instanceof boolean[]) {
                ((boolean[]) collection)[i] = (boolean) value;
            } else {
                throw new ReflectionException("在解析" + tokenizer.getFullname() +
                        "发生错误," + "'属性" + tokenizer.getName() + "' 不是一个array或者list或者map");
            }
        }
    }

    /**
     * 判断一个类型是否可以使用数组语法.
     * @param clazz 类型
     * @return 可以使用返回true, 否则返回false.
     */
    protected boolean isCanUseArraySyntax(Class<?> clazz) {
        return clazz.isArray() || Map.class.isAssignableFrom(clazz) || List.class.isAssignableFrom(clazz);
    }

}
