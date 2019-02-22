package com.wangtao.dbhelper.scripting;

import com.wangtao.dbhelper.builder.xml.XMLScriptBuilder;
import com.wangtao.dbhelper.core.Configuration;
import com.wangtao.dbhelper.core.Resources;
import com.wangtao.dbhelper.domian.User;
import com.wangtao.dbhelper.mapping.BoundSql;
import com.wangtao.dbhelper.mapping.DynamicSqlSource;
import com.wangtao.dbhelper.mapping.SqlSource;
import com.wangtao.dbhelper.parser.DtdEntityResolver;
import com.wangtao.dbhelper.parser.XNode;
import com.wangtao.dbhelper.parser.XpathParser;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.io.Reader;
import java.util.StringTokenizer;

/**
 * @author wangtao
 * Created at 2019/2/22 17:07
 */
public class DynamicSqlSourceTest {

    private static Reader reader;

    private static XNode root;

    private static Configuration configuration;

    @BeforeClass
    public static void beforeClass() {
        try {
            reader = Resources.getResourceAsReader("com/wangtao/dbhelper/scripting/DynamicElement.xml");
            XpathParser parser = new XpathParser.Builder()
                    .reader(reader).validating(true)
                    .entityResolver(new DtdEntityResolver())
                    .build();
            root = parser.evalNode("/mapper");
            configuration = new Configuration();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String removeWhitespace(String text) {
        StringTokenizer tokenizer = new StringTokenizer(text);
        StringBuilder buffer = new StringBuilder();
        while (tokenizer.hasMoreTokens()) {
            buffer.append(tokenizer.nextToken()).append(" ");
        }
        if(buffer.length() == 0) {
            return buffer.toString();
        }
        return buffer.delete(buffer.length() - 1, buffer.length()).toString();
    }

    @Test
    public void ifElement() {
        XNode context = root.evalNode("update[@id = 'updateByPrimaryKeySelective']");
        SqlSource sqlSource = new XMLScriptBuilder(configuration, context).createSqlSource();
        User user = new User();
        user.setUsername("wangtao");
        user.setPassword("123456");
        user.setId(1);
        BoundSql boundSql = sqlSource.getBoundSql(user);
        String sql = boundSql.getSql();
        sql = removeWhitespace(sql);
        Assert.assertNotEquals(0, sql.length());
        Assert.assertEquals("UPDATE user SET username = ?, password = ?, update_time = now() WHERE id = ?", sql);
    }

    @Test
    public void trimElement() {
        XNode context = root.evalNode("insert[@id = 'insertSelective']");
        SqlSource sqlSource = new XMLScriptBuilder(configuration, context).createSqlSource();
        Assert.assertTrue(sqlSource instanceof DynamicSqlSource);
        User user = new User();
        user.setUsername("wangtao");
        user.setPassword("123456");
        user.setId(1);
        BoundSql boundSql = sqlSource.getBoundSql(user);
        String sql = boundSql.getSql();
        sql = removeWhitespace(sql);
        Assert.assertNotEquals(0, sql.length());
        Assert.assertEquals("INSERT INTO user ( id, username, password ) VALUES ( ?, ?, ? )", sql);
    }

    @Test
    public void WhereElement() {
        XNode context = root.evalNode("select[@id = 'findByCondition']");
        SqlSource sqlSource = new XMLScriptBuilder(configuration, context).createSqlSource();
        Assert.assertTrue(sqlSource instanceof DynamicSqlSource);
        User user = new User();
        user.setUsername("wangtao");
        user.setPassword("123456");
        BoundSql boundSql = sqlSource.getBoundSql(user);
        String sql = boundSql.getSql();
        sql = removeWhitespace(sql);
        Assert.assertNotEquals(0, sql.length());
        Assert.assertEquals("SELECT id, username, password, age, gender, birthday, update_time FROM user WHERE"
                + " username = ? AND password = ?", sql);
        user = new User();
        boundSql = sqlSource.getBoundSql(user);
        sql = removeWhitespace(boundSql.getSql());
        Assert.assertEquals("SELECT id, username, password, age, gender, birthday, update_time FROM user", sql);
    }

    @Test
    public void setElement() {
        XNode context = root.evalNode("update[@id = 'update']");
        SqlSource sqlSource = new XMLScriptBuilder(configuration, context).createSqlSource();
        User user = new User();
        user.setUsername("wangtao");
        user.setPassword("123456");
        user.setId(1);
        BoundSql boundSql = sqlSource.getBoundSql(user);
        String sql = boundSql.getSql();
        sql = removeWhitespace(sql);
        Assert.assertNotEquals(0, sql.length());
        Assert.assertEquals("UPDATE user SET username = ?, password = ?, update_time = now() WHERE id = ?", sql);
    }

    @Test
    public void chooseElement() {
        XNode context = root.evalNode("select[@id = 'findByChoose']");
        SqlSource sqlSource = new XMLScriptBuilder(configuration, context).createSqlSource();
        User user = new User();
        user.setGender(1);
        BoundSql boundSql = sqlSource.getBoundSql(user);
        String sql = removeWhitespace(boundSql.getSql());
        Assert.assertEquals("SELECT * FROM user WHERE gender = '1'", sql);

        user.setGender(2);
        boundSql = sqlSource.getBoundSql(user);
        sql = removeWhitespace(boundSql.getSql());
        Assert.assertEquals("SELECT * FROM user WHERE gender = '2'", sql);

        user.setGender(null);
        boundSql = sqlSource.getBoundSql(user);
        sql = removeWhitespace(boundSql.getSql());
        Assert.assertEquals("SELECT * FROM user WHERE gender = '0'", sql);
    }

    @AfterClass
    public static void afterClass() {
        if (reader != null) {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
