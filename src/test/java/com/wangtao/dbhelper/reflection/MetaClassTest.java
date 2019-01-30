package com.wangtao.dbhelper.reflection;

import com.wangtao.dbhelper.domian.misc.RichType;
import com.wangtao.dbhelper.domian.misc.generics.GenericConcrete;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * @author wangtao
 * Created at 2019/1/28 14:00
 */
public class MetaClassTest {

    RichType rich = new RichType();
    Map<String, RichType> map = new HashMap<>();

    @Before
    public void before() {
        rich.setRichType(new RichType());
        map.put("richType", rich);
    }

    @SuppressWarnings("unused")
    private static class Address {
        public String name;
    }

    @SuppressWarnings("unused")
    private static class User {
        public String name;
        public List<Address> addressList;
        public Address[] addresses;
        public List items;
        public Map<String, Address> addressMap;
    }

    @Test
    public void shouldCheckHasGetterByArraySyntax() {
        MetaClass metaClass = MetaClass.forClass(User.class);
        assertTrue(metaClass.hasGetter("addressList"));
        assertTrue(metaClass.hasGetter("addressList[0]"));
        assertTrue(metaClass.hasGetter("addressList[0].name"));
        assertFalse(metaClass.hasGetter("addressList[0].desp"));
        assertTrue(metaClass.hasGetter("addresses"));
        assertTrue(metaClass.hasGetter("addresses[0]"));
        assertTrue(metaClass.hasGetter("addresses[0].name"));
        assertFalse(metaClass.hasGetter("addresses[0].desp"));
        assertTrue(metaClass.hasGetter("addressMap"));
        assertTrue(metaClass.hasGetter("addressMap[key]"));
        assertTrue(metaClass.hasGetter("addressMap[key].name"));
        assertFalse(metaClass.hasGetter("addressMap[key].desp"));
        assertTrue(metaClass.hasGetter("items"));
        assertFalse(metaClass.hasGetter("items[0].name"));
    }

       @Test
    public void shouldCheckHasSetterByArraySyntax() {
        MetaClass metaClass = MetaClass.forClass(User.class);
        assertTrue(metaClass.hasSetter("addressList"));
        assertTrue(metaClass.hasSetter("addressList[0]"));
        assertTrue(metaClass.hasSetter("addressList[0].name"));
        assertFalse(metaClass.hasSetter("addressList[0].desp"));
        assertTrue(metaClass.hasSetter("addresses"));
        assertTrue(metaClass.hasSetter("addresses[0]"));
        assertTrue(metaClass.hasSetter("addresses[0].name"));
        assertFalse(metaClass.hasSetter("addresses[0].desp"));
        assertTrue(metaClass.hasSetter("addressMap"));
        assertTrue(metaClass.hasSetter("addressMap[key]"));
        assertTrue(metaClass.hasSetter("addressMap[key].name"));
        assertFalse(metaClass.hasSetter("addressMap[key].desp"));
        assertTrue(metaClass.hasSetter("items"));
        assertFalse(metaClass.hasSetter("items[0].name"));
    }

    @Test
    public void shouldCheckGetterTypeByArraySyntax() {
        MetaClass metaClass = MetaClass.forClass(User.class);
        assertEquals(List.class, metaClass.getGetterType("addressList"));
        assertEquals(Address.class, metaClass.getGetterType("addressList[0]"));
        assertEquals(String.class, metaClass.getGetterType("addressList[0].name"));
        try {
            metaClass.getGetterType("addressList[0].desp");
            fail("没有对应的getter");
        } catch (ReflectionException e) {
            // just ignore
        }
        assertEquals(Address[].class, metaClass.getGetterType("addresses"));
        assertEquals(Address.class, metaClass.getGetterType("addresses[0]"));
        assertEquals(String.class, metaClass.getGetterType("addresses[0].name"));
        assertEquals(Map.class, metaClass.getGetterType("addressMap"));
        assertEquals(Address.class, metaClass.getGetterType("addressMap[key]"));
        assertEquals(String.class, metaClass.getGetterType("addressMap[key].name"));
        assertEquals(List.class, metaClass.getGetterType("items"));
    }


    @Test
    public void shouldTestDataTypeOfGenericMethod() {
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        MetaClass meta = MetaClass.forClass(GenericConcrete.class, reflectorFactory);
        assertEquals(Long.class, meta.getGetterType("id"));
        assertEquals(Long.class, meta.getSetterType("id"));
    }

    @Test
    public void shouldTestFindBestSetter() {
        class Father<T> {
            public T id;

            public void setId(T id) {
                this.id = id;
            }
        }

        class Son extends Father<Integer> {
            @Override
            public void setId(Integer id) {
                this.id = id;
            }
        }
        MetaClass metaClass = MetaClass.forClass(Son.class);
        assertEquals(Integer.class, metaClass.getSetterType("id"));
    }

    @Test
    public void shouldThrowReflectionExceptionGetGetterType() {
        try {
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            meta.getGetterType("aString");
            org.junit.Assert.fail("should have thrown ReflectionException");
        } catch (ReflectionException expected) {

        }
    }

    @Test
    public void shouldCheckTypeForEachGetter() {
        MetaClass meta = MetaClass.forClass(RichType.class);
        assertEquals(String.class, meta.getGetterType("richField"));
        assertEquals(String.class, meta.getGetterType("richProperty"));
        assertEquals(List.class, meta.getGetterType("richList"));
        assertEquals(Map.class, meta.getGetterType("richMap"));
        assertEquals(String.class, meta.getGetterType("richList[0]"));

        assertEquals(RichType.class, meta.getGetterType("richType"));
        assertEquals(String.class, meta.getGetterType("richType.richField"));
        assertEquals(String.class, meta.getGetterType("richType.richProperty"));
        assertEquals(List.class, meta.getGetterType("richType.richList"));
        assertEquals(Map.class, meta.getGetterType("richType.richMap"));
        assertEquals(String.class, meta.getGetterType("richType.richList[0]"));
    }

    @Test
    public void shouldCheckTypeForEachSetter() {
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
        assertEquals(String.class, meta.getSetterType("richField"));
        assertEquals(String.class, meta.getSetterType("richProperty"));
        assertEquals(List.class, meta.getSetterType("richList"));
        assertEquals(Map.class, meta.getSetterType("richMap"));
        assertEquals(String.class, meta.getSetterType("richList[0]"));

        assertEquals(RichType.class, meta.getSetterType("richType"));
        assertEquals(String.class, meta.getSetterType("richType.richField"));
        assertEquals(String.class, meta.getSetterType("richType.richProperty"));
        assertEquals(List.class, meta.getSetterType("richType.richList"));
        assertEquals(Map.class, meta.getSetterType("richType.richMap"));
        assertEquals(String.class, meta.getSetterType("richType.richList[0]"));
    }

    @Test
    public void shouldCheckGetterAndSetterNames() {
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
        assertEquals(5, meta.getGetterNames().length);
        assertEquals(5, meta.getSetterNames().length);
    }

    @Test
    public void shouldCheckGetterExistance() {
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
        assertTrue(meta.hasGetter("richField"));
        assertTrue(meta.hasGetter("richProperty"));
        assertTrue(meta.hasGetter("richList"));
        assertTrue(meta.hasGetter("richMap"));
        assertTrue(meta.hasGetter("richList[0]"));

        assertTrue(meta.hasGetter("richType"));
        assertTrue(meta.hasGetter("richType.richField"));
        assertTrue(meta.hasGetter("richType.richProperty"));
        assertTrue(meta.hasGetter("richType.richList"));
        assertTrue(meta.hasGetter("richType.richMap"));
        assertTrue(meta.hasGetter("richType.richList[0]"));

        assertFalse(meta.hasGetter("richType.hhh"));

        assertEquals("richType.richProperty", meta.findPropName("richType.richProperty"));

        assertFalse(meta.hasGetter("[0]"));
    }

    @Test
    public void shouldCheckSetterExistance() {
        MetaClass meta = MetaClass.forClass(RichType.class);
        assertTrue(meta.hasSetter("richField"));
        assertTrue(meta.hasSetter("richProperty"));
        assertTrue(meta.hasSetter("richList"));
        assertTrue(meta.hasSetter("richMap"));
        assertTrue(meta.hasSetter("richList[0]"));

        assertTrue(meta.hasSetter("richType"));
        assertTrue(meta.hasSetter("richType.richField"));
        assertTrue(meta.hasSetter("richType.richProperty"));
        assertTrue(meta.hasSetter("richType.richList"));
        assertTrue(meta.hasSetter("richType.richMap"));
        assertTrue(meta.hasSetter("richType.richList[0]"));

        assertFalse(meta.hasSetter("richType.hhh"));
        assertFalse(meta.hasSetter("[0]"));
    }

    @Test
    public void shouldFindPropertyName() {
        MetaClass meta = MetaClass.forClass(RichType.class);
        assertEquals("richField", meta.findPropName("RICHfield"));
        assertEquals("richField", meta.findPropName("richField"));
        assertEquals("richMap", meta.findPropName("richMap"));
        assertEquals("richType", meta.findPropName("richType"));
        assertEquals("richType.richProperty", meta.findPropName("richType.richProperty"));
        assertNull(meta.findPropName("richType.hhhh"));
        assertNull(meta.findPropName("hhh"));
    }
}