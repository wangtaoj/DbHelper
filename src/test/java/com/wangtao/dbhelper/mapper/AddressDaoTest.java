package com.wangtao.dbhelper.mapper;

import com.wangtao.dbhelper.domian.AddressVO;
import com.wangtao.dbhelper.domian.User;
import org.junit.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author wangtao
 * Created at 2019/2/21 16:13
 */
public class AddressDaoTest {

    private AddressDao addressDao = new AddressDao();

    @Test
    public void findById() {
        AddressVO vo = addressDao.findById(1);
        assertNotNull(vo);
        assertEquals(1, vo.getId().intValue());
        assertEquals("湖南", vo.getProvince());
        assertEquals("长沙", vo.getCity());
        assertEquals("中南林业科技大学", vo.getAddress());
        User user = vo.getUser();
        assertNotNull(user);
        assertEquals(1, user.getId().intValue());
        assertEquals("wangtao", user.getUsername());
        assertEquals("123456", user.getPassword());
        assertEquals(20, user.getAge().intValue());
        assertEquals(LocalDate.of(1997, 5, 3), user.getBirthday());
        String updateTime = user.getUpdateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        assertEquals("2018-10-27 12:17:11", updateTime);
    }

    @Test
    public void findByIdAutoMapping() {
        AddressVO vo = addressDao.findByIdAutoMapping(1);
        assertNotNull(vo);
        assertEquals(1, vo.getId().intValue());
        assertEquals("湖南", vo.getProvince());
        assertEquals("长沙", vo.getCity());
        assertEquals("中南林业科技大学", vo.getAddress());
        User user = vo.getUser();
        assertNotNull(user);
        assertEquals(1, user.getId().intValue());
        assertEquals("wangtao", user.getUsername());
        assertEquals("123456", user.getPassword());
        assertEquals(20, user.getAge().intValue());
        assertEquals(LocalDate.of(1997, 5, 3), user.getBirthday());
        String updateTime = user.getUpdateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        assertEquals("2018-10-27 12:17:11", updateTime);
    }
}
