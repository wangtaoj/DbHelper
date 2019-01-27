package com.wangtao.dbhelper.reflection.factory;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

/**
 * @author wangtao
 * Created at 2019/1/27 14:35
 */
public class DefaultObjectFactoryTest {

    ObjectFactory factory;

    @Before
    public void before() {
        factory = new DefaultObjectFactory();
    }

    @Test
    public void create() {
        Bean bean = factory.create(Bean.class);
        Assert.assertEquals(1, bean.id);
        Assert.assertEquals("wangtao", bean.name);
    }

    @Test
    public void createWithArg() {
        List<Class<?>> constructorClasses = Arrays.asList(int.class, String.class);
        List<Object> constructorArgs = Arrays.asList(2, "waston");
        Bean bean = factory.create(Bean.class, constructorClasses, constructorArgs);
        Assert.assertEquals(2, bean.id);
        Assert.assertEquals("waston", bean.name);
    }


    private static class Bean {

        int id;
        String name;

        public Bean(int id, String name) {
            this.id = id;
            this.name = name;
        }

        private Bean() {
            this.id = 1;
            this.name = "wangtao";
        }
    }
}
