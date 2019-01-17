package com.wangtao.dbhelper.parser;

import org.junit.Assert;
import org.junit.Test;

import java.util.Properties;

/**
 * @author wangtao
 * Created at 2019/1/17 16:52
 */
public class PropertyParserTest {

    @Test
    public void parse() {
        Properties variables = new Properties();
        variables.put("username", "root");
        variables.put("password", "123456");
        Assert.assertEquals("root", PropertyParser.parse("${username}", variables));
        Assert.assertEquals("123456", PropertyParser.parse("${password}", variables));
        Assert.assertEquals("0123456789", PropertyParser.parse("0${password}789", variables));
    }
}
