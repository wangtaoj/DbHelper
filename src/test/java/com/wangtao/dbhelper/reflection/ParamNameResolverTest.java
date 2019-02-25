package com.wangtao.dbhelper.reflection;

import com.wangtao.dbhelper.annotations.Param;
import com.wangtao.dbhelper.core.Configuration;
import com.wangtao.dbhelper.core.ParamMap;
import com.wangtao.dbhelper.core.RowBounds;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Method;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author wangtao
 * Created at 2019/2/25 17:16
 */
public class ParamNameResolverTest {

    interface Mapper {

        void findAll();

        void findById(Integer id);

        void findByIdWithParam(@Param("id") Integer id);

        void findByPage(Integer id, RowBounds rowBounds);

        void findByUsernameAndPassword(String username, RowBounds rowBounds, String password);

        void findByUsernameAndPasswordWithParam(@Param("username") String username, RowBounds rowBounds,
                                                @Param("password") String password);
    }

    private static Configuration configuration = new Configuration();

    /**
     * 空参数
     */
    @Test
    public void findAll() throws NoSuchMethodException {
        Method method = Mapper.class.getMethod("findAll");
        ParamNameResolver resolver = new ParamNameResolver(configuration, method);
        Object result = resolver.getNamedParams(null);
        Assert.assertNull(result);
        result = resolver.getNamedParams(new Object[]{RowBounds.DEFAULT});
        Assert.assertNull(result);
    }

    /**
     * 单参数不带@Param
     */
    @Test
    public void findById() throws NoSuchMethodException {
        Method method = Mapper.class.getMethod("findById", Integer.class);
        ParamNameResolver resolver = new ParamNameResolver(configuration, method);
        Object result = resolver.getNamedParams(new Object[]{1});
        assertTrue(result instanceof Integer);
        assertEquals(1, result);
    }

    /**
     * 单参数带@Param
     */
    @Test
    public void findByIdWithParam() throws NoSuchMethodException {
        Method method = Mapper.class.getMethod("findByIdWithParam", Integer.class);
        ParamNameResolver resolver = new ParamNameResolver(configuration, method);
        Object result = resolver.getNamedParams(new Object[]{1});
        assertTrue(result instanceof ParamMap);
        ParamMap paramMap = (ParamMap) result;
        assertEquals(1, paramMap.size());
        assertEquals(1, paramMap.get("id"));
    }

    /**
     * 带有特殊参数.
     */
    @Test
    public void findByPage() throws NoSuchMethodException {
        Method method = Mapper.class.getMethod("findByPage", Integer.class, RowBounds.class);
        ParamNameResolver resolver = new ParamNameResolver(configuration, method);
        Object result = resolver.getNamedParams(new Object[]{1, RowBounds.DEFAULT});
        assertTrue(result instanceof Integer);
        assertEquals(1, result);
    }

    /**
     * 多参数不带@Param
     */
    @Test
    public void findByUsernameAndPassword() throws NoSuchMethodException {
        Method method = Mapper.class.getMethod("findByUsernameAndPassword", String.class, RowBounds.class, String.class);
        ParamNameResolver resolver = new ParamNameResolver(configuration, method);
        Object result = resolver.getNamedParams(new Object[]{"wangtao", RowBounds.DEFAULT, "123456"});
        assertTrue(result instanceof ParamMap);
        ParamMap map = (ParamMap) result;
        assertEquals("wangtao", map.get("arg0"));
        assertEquals("123456", map.get("arg2"));
    }

    /**
     * 多参数带@Param
     */
    @Test
    public void findByUsernameAndPasswordWithParam() throws NoSuchMethodException {
        Method method = Mapper.class.getMethod("findByUsernameAndPasswordWithParam", String.class, RowBounds.class, String.class);
        ParamNameResolver resolver = new ParamNameResolver(configuration, method);
        Object result = resolver.getNamedParams(new Object[]{"wangtao", RowBounds.DEFAULT, "123456"});
        assertTrue(result instanceof ParamMap);
        ParamMap map = (ParamMap) result;
        assertEquals("wangtao", map.get("username"));
        assertEquals("123456", map.get("password"));
    }
}
