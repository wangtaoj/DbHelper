package com.wangtao.dbhelper.builder;

import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

/**
 * @author wangtao
 * Created at 2019/1/23 14:34
 */
public class ParameterExpressionTest {

    @Test
    public void simple() {
        ParameterExpression parser = new ParameterExpression("id");
        Map<String, String> map = parser.parse();
        Assert.assertEquals("id", map.get("property"));
    }

    @Test
    public void simpleWithWhitespace() {
        ParameterExpression parser = new ParameterExpression("  id  ");
        Map<String, String> map = parser.parse();
        Assert.assertEquals("id", map.get("property"));
    }

    @Test
    public void simpleWithJdbc() {
        ParameterExpression parser = new ParameterExpression("id, jdbcType = INTEGER");
        Map<String, String> map = parser.parse();
        Assert.assertEquals("id", map.get("property"));
        Assert.assertEquals("INTEGER", map.get("jdbcType"));
    }
}
