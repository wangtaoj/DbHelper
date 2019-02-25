package com.wangtao.dbhelper.mapping;
import com.wangtao.dbhelper.builder.xml.XMLScriptBuilder;
import com.wangtao.dbhelper.core.Configuration;
import com.wangtao.dbhelper.core.ParamMap;
import com.wangtao.dbhelper.core.Resources;
import com.wangtao.dbhelper.domian.User;
import com.wangtao.dbhelper.parser.DtdEntityResolver;
import com.wangtao.dbhelper.parser.XNode;
import com.wangtao.dbhelper.parser.XpathParser;
import com.wangtao.dbhelper.reflection.MetaObject;
import com.wangtao.dbhelper.scripting.xmltags.ForeachSqlNode;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.io.Reader;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

import static org.junit.Assert.*;

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
            reader = Resources.getResourceAsReader("com/wangtao/dbhelper/mapping/DynamicElement.xml");
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
        if (buffer.length() == 0) {
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
        assertNotEquals(0, sql.length());
        assertEquals("UPDATE user SET username = ?, password = ?, update_time = now() WHERE id = ?", sql);
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
        assertNotEquals(0, sql.length());
        assertEquals("INSERT INTO user ( id, username, password ) VALUES ( ?, ?, ? )", sql);
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
        assertNotEquals(0, sql.length());
        assertEquals("SELECT id, username, password, age, gender, birthday, update_time FROM user WHERE"
                + " username = ? AND password = ?", sql);
        user = new User();
        boundSql = sqlSource.getBoundSql(user);
        sql = removeWhitespace(boundSql.getSql());
        assertEquals("SELECT id, username, password, age, gender, birthday, update_time FROM user", sql);
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
        assertNotEquals(0, sql.length());
        assertEquals("UPDATE user SET username = ?, password = ?, update_time = now() WHERE id = ?", sql);
    }

    @Test
    public void chooseElement() {
        XNode context = root.evalNode("select[@id = 'findByChoose']");
        SqlSource sqlSource = new XMLScriptBuilder(configuration, context).createSqlSource();
        User user = new User();
        user.setGender(1);
        BoundSql boundSql = sqlSource.getBoundSql(user);
        String sql = removeWhitespace(boundSql.getSql());
        assertEquals("SELECT * FROM user WHERE gender = '1'", sql);

        user.setGender(2);
        boundSql = sqlSource.getBoundSql(user);
        sql = removeWhitespace(boundSql.getSql());
        assertEquals("SELECT * FROM user WHERE gender = '2'", sql);

        user.setGender(null);
        boundSql = sqlSource.getBoundSql(user);
        sql = removeWhitespace(boundSql.getSql());
        assertEquals("SELECT * FROM user WHERE gender = '0'", sql);
    }

    @Test
    public void foreachElement() {
        XNode context = root.evalNode("select[@id = 'findByAgeIn']");
        SqlSource sqlSource = new XMLScriptBuilder(configuration, context).createSqlSource();
        int[] ages = {20, 22, 21};
        ParamMap map = new ParamMap();
        map.put("list", ages);
        BoundSql boundSql = sqlSource.getBoundSql(map);
        String sql = removeWhitespace(boundSql.getSql());
        Assert.assertTrue(sqlSource instanceof DynamicSqlSource);
        assertEquals("SELECT * FROM user WHERE age IN ( ? , ? , ? )", sql);
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        assertEquals(3, parameterMappings.size());
        for (int i = 0; i < parameterMappings.size(); i++) {
            ParameterMapping parameterMapping = parameterMappings.get(i);
            String property = ForeachSqlNode.ADDITIONAL_PARAMETER_PREFIX + "item_" + i;
            assertEquals(property, parameterMapping.getProperty());
            assertTrue(boundSql.hasAdditionalParameter(property));
            assertEquals(ages[i], boundSql.getAdditionalParameter(property));
        }

        List<Integer> ageList = Arrays.asList(20, 22, 21);
        map.put("list", ageList);
        boundSql = sqlSource.getBoundSql(map);
        sql = removeWhitespace(boundSql.getSql());
        assertEquals("SELECT * FROM user WHERE age IN ( ? , ? , ? )", sql);
        parameterMappings = boundSql.getParameterMappings();
        assertEquals(3, parameterMappings.size());
        for (int i = 0; i < parameterMappings.size(); i++) {
            ParameterMapping parameterMapping = parameterMappings.get(i);
            String property = ForeachSqlNode.ADDITIONAL_PARAMETER_PREFIX + "item_" + i;
            assertEquals(property, parameterMapping.getProperty());
            assertTrue(boundSql.hasAdditionalParameter(property));
            assertEquals(ageList.get(i), boundSql.getAdditionalParameter(property));
        }
    }

    @Test
    public void foreachElementV2() {
        XNode context = root.evalNode("insert[@id = 'insertList']");

        // 设置参数
        User user = new User();
        user.setUsername("wangtao");
        user.setPassword("123456");
        user.setAge(20);
        user.setGender(1);
        LocalDate birthday = LocalDate.of(1997, 5, 3);
        user.setBirthday(birthday);
        LocalDateTime updateTime = LocalDateTime.of(2019, 2, 25, 11, 2, 20);
        user.setUpdateTime(updateTime);
        List<User> users = Arrays.asList(user, user);
        ParamMap map = new ParamMap();
        map.put("users", users);

        String[] propertys = {"id", "username", "password", "age", "gender", "birthday", "updateTime"};

        SqlSource sqlSource = new XMLScriptBuilder(configuration, context).createSqlSource();
        BoundSql boundSql = sqlSource.getBoundSql(map);
        String sql = removeWhitespace(boundSql.getSql());
        Assert.assertTrue(sqlSource instanceof DynamicSqlSource);
        assertEquals("INSERT INTO user ( id, username, password, age, gender, birthday, update_time ) " +
                "VALUES ( ?, ?, ?, ?, ?, ?, ? ) , ( ?, ?, ?, ?, ?, ?, ? )", sql);
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        assertEquals(14, parameterMappings.size());
        for(int i = 0; i < 2; i ++) {
            MetaObject metaObject = configuration.newMetaObject(users.get(i));
            for (int j = 0; j < 7; j++) {
                int k = i * 7 + j;
                // 真实属性名
                String propertyName = propertys[j];
                // foreach处理后加序号的属性名
                String property = (ForeachSqlNode.ADDITIONAL_PARAMETER_PREFIX + "user_" + i) + "." + propertyName;
                ParameterMapping parameterMapping = parameterMappings.get(k);
                assertEquals(property, parameterMapping.getProperty());
                assertTrue(boundSql.hasAdditionalParameter(property));
                assertEquals(metaObject.getValue(propertyName), boundSql.getAdditionalParameter(property));
            }
        }

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
