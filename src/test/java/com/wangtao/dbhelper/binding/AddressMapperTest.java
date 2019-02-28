package com.wangtao.dbhelper.binding;

import com.wangtao.dbhelper.core.SqlSession;
import com.wangtao.dbhelper.domian.Address;
import com.wangtao.dbhelper.mapper.Utils;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author wangtao
 * Created at 2019/2/28 14:38
 */
public class AddressMapperTest {

    /**
     * 如果未显示提交事务, 那么close方法会对修改语句(insert, update, delete)进行回滚
     * 注: 3个语句分别都是独立的事务, 因为重新创建SqlSession, 并且SqlSession被正常关闭
     */
    @Test
    public void insertRollbackByClose() {
        // 查询插入前的记录条数
        int countInsertBefore;
        try (SqlSession session = Utils.getSqlSession()) {
            AddressMapper addressMapper = session.getMapper(AddressMapper.class);
            countInsertBefore = addressMapper.count();
        }

        // 插入数据
        try (SqlSession session = Utils.getSqlSession()) {
            AddressMapper addressMapper = session.getMapper(AddressMapper.class);
            Address address = new Address();
            address.setProvince("神州大地");
            address.setCity("中原");
            address.setAddress("鬼王宗");
            address.setUserId(10);
            addressMapper.insert(address);
        }

        // 查询插入后的数据
        int countInsertAfter;
        try (SqlSession session = Utils.getSqlSession()) {
            AddressMapper addressMapper = session.getMapper(AddressMapper.class);
            countInsertAfter = addressMapper.count();
        }
        assertEquals(countInsertBefore, countInsertAfter);
    }

    /**
     * 手动提交事务
     * 如果执行SQL发生异常, 那么也就是说commit方法不会被执行.
     * 但是finally是一定要执行的, 因此session会被关闭, 同样发生回滚
     */
    @Test
    public void insertRoolbackIfException() {
        // 查询插入前的记录条数
        int countInsertBefore;
        try (SqlSession session = Utils.getSqlSession()) {
            AddressMapper addressMapper = session.getMapper(AddressMapper.class);
            countInsertBefore = addressMapper.count();
        }

        // 插入数据
        try (SqlSession session = Utils.getSqlSession()) {
            AddressMapper addressMapper = session.getMapper(AddressMapper.class);
            Address address = new Address();
            address.setProvince("神州大地");
            address.setCity("中原");
            address.setAddress("鬼王宗");
            address.setUserId(10);
            addressMapper.insert(address);
            //noinspection divzero,NumericOverflow, 模拟异常
            System.out.println(1 / 0);
            fail("出现异常, 回滚");
            session.commit();
        } catch (Exception e) {
            assertTrue(e instanceof ArithmeticException);
        }

        // 查询插入后的数据
        int countInsertAfter;
        try (SqlSession session = Utils.getSqlSession()) {
            AddressMapper addressMapper = session.getMapper(AddressMapper.class);
            countInsertAfter = addressMapper.count();
        }
        assertEquals(countInsertBefore, countInsertAfter);
    }

    /**
     * 手动提交事务
     */
    @Test
    public void insertAndCommit() {

        // 查询插入前的记录条数
        int countInsertBefore;
        try (SqlSession session = Utils.getSqlSession()) {
            AddressMapper addressMapper = session.getMapper(AddressMapper.class);
            countInsertBefore = addressMapper.count();
        }

        // 插入数据
        try (SqlSession session = Utils.getSqlSession()) {
            AddressMapper addressMapper = session.getMapper(AddressMapper.class);
            Address address = new Address();
            address.setProvince("神州大地");
            address.setCity("中原");
            address.setAddress("鬼王宗");
            address.setUserId(10);
            addressMapper.insert(address);
            session.commit();
        }

        // 查询插入后的数据
        int countInsertAfter;
        try (SqlSession session = Utils.getSqlSession()) {
            AddressMapper addressMapper = session.getMapper(AddressMapper.class);
            countInsertAfter = addressMapper.count();
        }

        assertEquals(countInsertBefore + 1, countInsertAfter);
    }
}
