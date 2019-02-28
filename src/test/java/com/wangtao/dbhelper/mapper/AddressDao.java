package com.wangtao.dbhelper.mapper;

import com.wangtao.dbhelper.core.SqlSession;
import com.wangtao.dbhelper.domian.AddressVO;

/**
 * @author wangtao
 * Created at 2019/2/21 9:40
 */
public class AddressDao {

    public AddressVO findById(Integer id) {
        SqlSession sqlSession = Utils.getSqlSession();
        try {
            AddressVO addressVO = sqlSession.selectOne("com.wangtao.dbhelper.binding.AddressMapper.findById", id);
            sqlSession.commit();
            return addressVO;
        } catch (RuntimeException e) {
            sqlSession.rollback();
            throw e;
        } finally {
            sqlSession.close();
        }
    }

    public AddressVO findByIdAutoMapping(Integer id) {
        SqlSession sqlSession = Utils.getSqlSession();
        try {
            AddressVO addressVO = sqlSession.selectOne("com.wangtao.dbhelper.binding.AddressMapper.findByIdAutoMapping", id);
            sqlSession.commit();
            return addressVO;
        } catch (RuntimeException e) {
            sqlSession.rollback();
            throw e;
        } finally {
            sqlSession.close();
        }
    }
}
