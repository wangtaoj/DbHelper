package com.wangtao.dbhelper.reflection.wrapper;

import com.wangtao.dbhelper.reflection.factory.ObjectFactory;
import com.wangtao.dbhelper.reflection.property.PropertyTokenizer;

import java.util.Collection;

/**
 * @author wangtao
 * Created at 2019/1/28 9:45
 */
public class CollectionWrapper implements ObjectWrapper {

    private final Collection collection;

    public CollectionWrapper(Collection collection) {
        this.collection = collection;
    }

    @Override
    public Object get(PropertyTokenizer tokenizer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void set(PropertyTokenizer tokenizer, Object value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean hasSetter(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean hasGetter(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String[] getSetterNames() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String[] getGetterNames() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String findProperty(String name, boolean useCamelCaseMapping) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object instantiateProperty(String propName, ObjectFactory objectFactory) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Class<?> getSetterType(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Class<?> getGetterType(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isCollection() {
        return true;
    }

    public Collection getCollection() {
        return collection;
    }
}
