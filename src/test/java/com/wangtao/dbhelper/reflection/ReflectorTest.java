package com.wangtao.dbhelper.reflection;

import com.wangtao.dbhelper.reflection.bean.BeanA;
import com.wangtao.dbhelper.reflection.invoker.Invoker;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by wangtao at 2018/12/26 13:46
 */
public class ReflectorTest {

    /**
     * 测试公共属性
     */
    @Test
    public void findForClass() throws Exception{
        Reflector reflector = new DefaultReflectorFactory().findForClass(BeanA.class);
        boolean res = reflector.hasGetter("name");
        Assert.assertTrue(res);
        Assert.assertTrue(reflector.hasGetter("password"));
        Assert.assertTrue(reflector.hasGetter("gender"));

        BeanA beanA = new BeanA();
        Invoker invoker = reflector.getSetInvoker("gender");
        invoker.invoke(beanA, new Object[]{"male"});

    }
}
