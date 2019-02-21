package com.wangtao.dbhelper.mapper;
import com.wangtao.dbhelper.domian.User;
import org.junit.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * @author wangtao
 * Created at 2019/2/21 10:22
 */
public class UserMapperTest {

    UserMapper userMapper = new UserMapper();

    @Test
    public void findById() {
        User user = userMapper.findById(1);
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
     * 返回map对象
     */
    @Test
    public void findToMapById() {
        Map<String, Object> map = userMapper.findToMapById(1);
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
        User user = userMapper.findByIdAutoMapping(1);
        assertNotNull(user);
        assertEquals(Integer.valueOf(1), user.getId());
        assertEquals("wangtao", user.getUsername());
        assertEquals("123456", user.getPassword());
        assertEquals(20, user.getAge().intValue());
        assertEquals(LocalDate.of(1997, 5, 3), user.getBirthday());
        String updateTime = user.getUpdateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        assertEquals("2018-10-27 12:17:11", updateTime);
    }

    @Test
    public void findByEntity() {
        User entity = new User();
        entity.setAge(21);
        entity.setGender(1);
        List<User> users = userMapper.findByEntity(entity);
        assertEquals(2, users.size());
    }

    @Test
    public void findByMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("age", 21);
        List<User> users = userMapper.findByMap(map);
        assertTrue(users.isEmpty());
    }

    @Test
    public void count() {
        int count = userMapper.count();
        assertEquals(12, count);
    }

    @Test
    public void findAgeById() {
        Integer age = userMapper.findAgeById(1);
        assertEquals(20, age.intValue());
    }

    @Test
    public void findBirthdayById() {
        LocalDate birthday = userMapper.findBirthdayById(1);
        assertEquals(LocalDate.of(1997, 5, 3), birthday);
    }

    @Test
    public void insertAndReturnKey() {
        User user = new User();
        user.setUsername("test");
        user.setPassword("123456");
        user.setAge(21);
        user.setGender(1);
        user.setBirthday(LocalDate.of(1997, 5, 3));
        int rows = userMapper.insertAndReturnKey(user);
        assertEquals(1, rows);
        rows = userMapper.delete(user.getId());
        assertEquals(1, rows);
    }
}
