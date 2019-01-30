package com.wangtao.dbhelper.reflection;

import com.wangtao.dbhelper.reflection.bean.BeanA;
import com.wangtao.dbhelper.reflection.invoker.Invoker;
import org.junit.Assert;
import org.junit.Test;

import java.io.Serializable;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by wangtao at 2018/12/26 13:46
 */
public class ReflectorTest {

    /**
     * 测试公共属性
     */
    @Test
    public void findForClass() throws Exception {
        Reflector reflector = new DefaultReflectorFactory().findForClass(BeanA.class);
        boolean res = reflector.hasGetter("name");
        Assert.assertTrue(res);
        Assert.assertTrue(reflector.hasGetter("password"));
        Assert.assertTrue(reflector.hasGetter("gender"));

        BeanA beanA = new BeanA();
        Invoker invoker = reflector.getSetInvoker("gender");
        invoker.invoke(beanA, new Object[]{"male"});
    }

    @Test
    public void testGetSetterType() {
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Reflector reflector = reflectorFactory.findForClass(Section.class);
        Assert.assertEquals(Long.class, reflector.getSetterParamType("id"));
    }

    @Test
    public void testGetGetterType() {
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Reflector reflector = reflectorFactory.findForClass(Section.class);
        Assert.assertEquals(Long.class, reflector.getGetterReturnType("id"));
    }

    @Test
    public void shouldNotGetClass() {
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Reflector reflector = reflectorFactory.findForClass(Section.class);
        Assert.assertFalse(reflector.hasGetter("class"));
    }

    interface Entity<T> {
        T getId();

        void setId(T id);
    }

    static abstract class AbstractEntity implements Entity<Long> {

        private Long id;

        @Override
        public Long getId() {
            return id;
        }

        @Override
        public void setId(Long id) {
            this.id = id;
        }
    }

    static class Section extends AbstractEntity implements Entity<Long> {
    }

    static abstract class Parent<T extends Serializable> {
        protected T id;
        protected List<T> list;
        protected T[] array;
        @SuppressWarnings("unused")
        private T fld;
        @SuppressWarnings("unused")
        public T pubFld;

        public T getId() {
            return id;
        }

        public void setId(T id) {
            this.id = id;
        }

        public List<T> getList() {
            return list;
        }

        public void setList(List<T> list) {
            this.list = list;
        }

        public T[] getArray() {
            return array;
        }

        public void setArray(T[] array) {
            this.array = array;
        }

        public T getFld() {
            return fld;
        }
    }

    static class Child extends Parent<String> {
    }

    @Test
    public void shouldResolveSetterParam() {
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Reflector reflector = reflectorFactory.findForClass(Child.class);
        assertEquals(String.class, reflector.getSetterParamType("id"));
    }

    @Test
    public void shouldResolveParameterizedSetterParam() {
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Reflector reflector = reflectorFactory.findForClass(Child.class);
        assertEquals(List.class, reflector.getSetterParamType("list"));
    }

    @Test
    public void shouldResolveArraySetterParam() {
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Reflector reflector = reflectorFactory.findForClass(Child.class);
        Class<?> clazz = reflector.getSetterParamType("array");
        assertTrue(clazz.isArray());
        assertEquals(String.class, clazz.getComponentType());
    }

    @Test
    public void shouldResolveGetterType() {
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Reflector reflector = reflectorFactory.findForClass(Child.class);
        assertEquals(String.class, reflector.getGetterReturnType("id"));
    }

    @Test
    public void shouldResolveSetterTypeFromPrivateField() {
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Reflector reflector = reflectorFactory.findForClass(Child.class);
        assertEquals(String.class, reflector.getSetterParamType("fld"));
    }

    @Test
    public void shouldResolveGetterTypeFromPublicField() {
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Reflector reflector = reflectorFactory.findForClass(Child.class);
        assertEquals(String.class, reflector.getGetterReturnType("pubFld"));
    }

    @Test
    public void shouldResolveParameterizedGetterType() {
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Reflector reflector = reflectorFactory.findForClass(Child.class);
        assertEquals(List.class, reflector.getGetterReturnType("list"));
    }

    @Test
    public void shouldResolveArrayGetterType() {
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Reflector reflector = reflectorFactory.findForClass(Child.class);
        Class<?> clazz = reflector.getGetterReturnType("array");
        assertTrue(clazz.isArray());
        assertEquals(String.class, clazz.getComponentType());
    }

    @Test
    public void shouldResoleveReadonlySetterWithOverload() {
        class BeanClass implements BeanInterface<String> {
            @Override
            public void setId(String id) {
                // Do nothing
            }
        }
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Reflector reflector = reflectorFactory.findForClass(BeanClass.class);
        assertEquals(String.class, reflector.getSetterParamType("id"));
    }

    interface BeanInterface<T> {
        void setId(T id);
    }

    @Test(expected = ReflectionException.class)
    public void shouldSettersWithUnrelatedArgTypesThrowException() {
        @SuppressWarnings("unused")
        class BeanClass {
            public void setTheProp(String arg) {
            }

            public void setTheProp(Integer arg) {
            }
        }

        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        reflectorFactory.findForClass(BeanClass.class);
    }

    @Test
    public void shouldAllowTwoBooleanGetters() throws Exception {
        @SuppressWarnings("unused")
        class Bean {
            // JavaBean Spec allows this (see #906)
            public boolean isBool() {
                return true;
            }

            public boolean getBool() {
                return false;
            }

            public void setBool(boolean bool) {
            }
        }
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Reflector reflector = reflectorFactory.findForClass(Bean.class);
        assertTrue((Boolean) reflector.getGetInvoker("bool").invoke(new Bean(), new Byte[0]));
    }
}
