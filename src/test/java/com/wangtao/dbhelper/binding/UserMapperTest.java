package com.wangtao.dbhelper.binding;

import com.wangtao.dbhelper.core.SqlSession;
import com.wangtao.dbhelper.domian.User;
import com.wangtao.dbhelper.mapper.Utils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * @author wangtao
 * Created at 2019/2/26 16:08
 */
public class UserMapperTest {

    private SqlSession sqlSession;

    private UserMapper userMapper;

    @Before
    public void before() {
        sqlSession = Utils.getSqlSession();
        userMapper = sqlSession.getMapper(UserMapper.class);
    }

    @After
    public void after() {
        sqlSession.close();
    }

    /**
     * 简单参数测试, 返回实体对象
     */
    @Test
    public void findById() {
        User user = null;
        try {
            user = userMapper.findById(1);
            sqlSession.commit();
        } catch (Exception e) {
            sqlSession.rollback();
            e.printStackTrace();
        }
        assertNotNull(user);
        assertEquals(1, user.getId().intValue());
        assertEquals("wangtao", user.getUsername());
        assertEquals("123456", user.getPassword());
        assertEquals(20, user.getAge().intValue());
        assertEquals(LocalDate.of(1997, 5, 3), user.getBirthday());
        String updateTime = user.getUpdateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        assertEquals("2018-10-27 12:17:11", updateTime);
    }

    /**
     * 简单参数测试, 返回map对象
     */
    @Test
    public void findToMapById() {
        Map<String, Object> map = null;
        try {
            map = userMapper.findToMapById(1);
            sqlSession.commit();
        } catch (Exception e) {
            sqlSession.rollback();
            e.printStackTrace();
        }
        assertNotNull(map);
        assertEquals(1, map.get("id"));
        assertEquals("wangtao", map.get("username"));
        assertEquals("123456", map.get("password"));
        assertEquals(20, map.get("age"));
        assertEquals(java.sql.Date.valueOf("1997-05-03"), map.get("birthday"));
        assertEquals(java.sql.Timestamp.valueOf("2018-10-27 12:17:11"), map.get("update_time"));
    }

    /**
     * 自动映射
     */
    @Test
    public void findByIdAutoMapping() {
        User user = null;
        try {
            user = userMapper.findByIdAutoMapping(1);
            sqlSession.commit();
        } catch (Exception e) {
            e.printStackTrace();
            sqlSession.rollback();
        }
        assertNotNull(user);
        assertEquals(Integer.valueOf(1), user.getId());
        assertEquals("wangtao", user.getUsername());
        assertEquals("123456", user.getPassword());
        assertEquals(20, user.getAge().intValue());
        assertEquals(LocalDate.of(1997, 5, 3), user.getBirthday());
        String updateTime = user.getUpdateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        assertEquals("2018-10-27 12:17:11", updateTime);
    }

    /**
     * POJO参数测试
     */
    @Test
    public void findByEntity() {
        User entity = new User();
        entity.setAge(21);
        entity.setGender(1);
        List<User> users = null;
        try {
            users = userMapper.findByEntity(entity);
            sqlSession.commit();
        } catch (Exception e) {
            e.printStackTrace();
            sqlSession.rollback();
        }
        assertNotNull(users);
        assertEquals(2, users.size());
    }

    /**
     * 参数为Map
     */
    @Test
    public void findByMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("age", 21);
        List<User> users = null;
        try {
            users = userMapper.findByMap(map);
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertNotNull(users);
        assertTrue(users.isEmpty());
    }

    /**
     * 返回单结果.
     */
    @Test
    public void count() {
        int count = 0;
        try {
            count = userMapper.count();
            sqlSession.commit();
        } catch (Exception e) {
            e.printStackTrace();
            sqlSession.rollback();
        }
        assertEquals(12, count);
    }

    /**
     * 返回单结果.
     */
    @Test
    public void findAgeById() {
        Integer age = null;
        try {
            age = userMapper.findAgeById(1);
            sqlSession.commit();
        } catch (Exception e) {
            e.printStackTrace();
            sqlSession.rollback();
        }
        assertNotNull(age);
        assertEquals(20, age.intValue());
    }

    /**
     * 返回单结果.
     */
    @Test
    public void findBirthdayById() {
        LocalDate birthday = null;
        try {
            birthday = userMapper.findBirthdayById(1);
            sqlSession.commit();
        } catch (Exception e) {
            e.printStackTrace();
            sqlSession.rollback();
        }
        assertEquals(LocalDate.of(1997, 5, 3), birthday);
    }

    /**
     * 插入并返回自增主键.
     */
    @Test
    public void insertAndReturnKey() {
        User user = new User();
        user.setUsername("test");
        user.setPassword("123456");
        user.setAge(21);
        user.setGender(1);
        user.setBirthday(LocalDate.of(1997, 5, 3));
        int rows;
        try {
            rows = userMapper.insertAndReturnKey(user);
            assertEquals(1, rows);
            rows = userMapper.delete(user.getId());
            assertEquals(1, rows);
            sqlSession.commit();
        } catch (Exception e) {
            e.printStackTrace();
            sqlSession.rollback();
        }

    }

    /**
     * 动态SQL(foreach)
     */
    @Test
    public void findByAgeIn() {
        List<Integer> ages = Arrays.asList(20, 21);
        List<User> users = null;
        try {
            users = userMapper.findByAgeIn(ages);
            sqlSession.commit();
        } catch (Exception e) {
            e.printStackTrace();
            sqlSession.rollback();
        }
        assertNotNull(users);
        assertEquals(3, users.size());
    }

    /**
     * 动态SQL(where, if)
     */
    @Test
    public void findByCondition() {
        try {
            User user = new User();
            List<User> users = userMapper.findByCondition(user);
            assertNotNull(users);
            assertEquals(12, users.size());
            user.setUsername("wangtao");
            user.setPassword("123456");
            users = userMapper.findByCondition(user);
            assertEquals(1, users.size());
            user = users.get(0);
            assertEquals(1, user.getId().intValue());
            assertEquals("wangtao", user.getUsername());
            assertEquals("123456", user.getPassword());
            assertEquals(20, user.getAge().intValue());
            assertEquals(LocalDate.of(1997, 5, 3), user.getBirthday());
            String updateTime = user.getUpdateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            assertEquals("2018-10-27 12:17:11", updateTime);
            sqlSession.commit();
        } catch (Exception e) {
            e.printStackTrace();
            sqlSession.rollback();
        }
    }


}
