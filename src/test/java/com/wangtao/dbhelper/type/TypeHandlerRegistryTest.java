package com.wangtao.dbhelper.type;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by wangtao at 2019/1/3 17:17
 */
public class TypeHandlerRegistryTest {

    @Test
    public void getTypeHandler() {
        TypeHandlerRegistry typeHandlerRegistry = new TypeHandlerRegistry();
        TypeHandler<Object> handler = typeHandlerRegistry.getTypeHandler(Object.class);
        Assert.assertTrue(handler instanceof UnknownTypeHandler);
    }
}
