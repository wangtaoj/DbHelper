package com.wangtao.dbhelper.reflection.wrapper;

import com.wangtao.dbhelper.reflection.MetaObject;
import com.wangtao.dbhelper.reflection.factory.ObjectFactory;
import com.wangtao.dbhelper.reflection.property.PropertyTokenizer;

import java.util.HashMap;
import java.util.Map;

/**
 * @author wangtao
 * Created at 2019/1/27 16:50
 */
public class MapWrapper extends BaseWrapper {

    private final Map<String, Object> map;

    public MapWrapper(MetaObject metaObject, Map<String, Object> map) {
        super(metaObject, map);
        this.map = map;
    }

    @Override
    public Object get(PropertyTokenizer tokenizer) {
        return super.get(tokenizer);
    }

    @Override
    protected Object getPropertyValue(String propName) {
        return map.get(propName);
    }

    protected void setPropertyValue(String propName, Object value) {
        map.put(propName, value);
    }

    @Override
    public void set(PropertyTokenizer tokenizer, Object value) {
        super.set(tokenizer, value);
    }

    @Override
    public String[] getSetterNames() {
        return map.keySet().toArray(new String[0]);
    }

    @Override
    public String[] getGetterNames() {
        return map.keySet().toArray(new String[0]);
    }

    @Override
    public boolean hasSetter(String name) {
        PropertyTokenizer tokenizer = new PropertyTokenizer(name);
        if(tokenizer.hasNext()) {
            Object currentValue = metaObject.getValue(tokenizer.getIndexName());
            if(currentValue == null) {
                return true;
            }
            MetaObject metaObject = MetaObject.forObject(currentValue);
            return metaObject.hasSetter(tokenizer.getChildren());
        } else {
            return true;
        }
    }

    @Override
    public boolean hasGetter(String name) {
        PropertyTokenizer tokenizer = new PropertyTokenizer(name);
        if (tokenizer.hasNext()) {
            Object currentValue = metaObject.getValue(tokenizer.getIndexName());
            if (currentValue == null) {
                return map.containsKey(tokenizer.getName());
            } else {
                MetaObject metaObject = MetaObject.forObject(currentValue);
                return metaObject.hasGetter(tokenizer.getChildren());
            }
        } else {
            return map.containsKey(tokenizer.getName());
        }
    }

    @Override
    public String findProperty(String name, boolean useCamelCaseMapping) {
        return name;
    }

    @Override
    public Object instantiateProperty(String propName, ObjectFactory objectFactory) {
        Map<String, Object> newMap = new HashMap<>();
        set(new PropertyTokenizer(propName), newMap);
        return newMap;
    }

    @Override
    public Class<?> getSetterType(String name) {
        PropertyTokenizer tokenizer = new PropertyTokenizer(name);
        if (tokenizer.hasNext()) {
            Object currentValue = metaObject.getValue(tokenizer.getIndexName());
            if (currentValue == null) {
                return Object.class;
            }
            MetaObject metaObject = MetaObject.forObject(currentValue);
            return metaObject.getSetterType(tokenizer.getChildren());
        } else {
            Object value = get(tokenizer);
            if (value != null) {
                return value.getClass();
            }
            return Object.class;
        }
    }

    @Override
    public Class<?> getGetterType(String name) {
        PropertyTokenizer tokenizer = new PropertyTokenizer(name);
        if (tokenizer.hasNext()) {
            Object currentValue = metaObject.getValue(tokenizer.getIndexName());
            if (currentValue == null) {
                return Object.class;
            }
            MetaObject metaObject = MetaObject.forObject(currentValue);
            return metaObject.getGetterType(tokenizer.getChildren());
        } else {
            Object value = get(tokenizer);
            if (value != null) {
                return value.getClass();
            }
            return Object.class;
        }
    }

    @Override
    public boolean isCollection() {
        return false;
    }
}
