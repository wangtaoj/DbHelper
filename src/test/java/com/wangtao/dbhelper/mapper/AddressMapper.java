package com.wangtao.dbhelper.mapper;

import com.wangtao.dbhelper.core.SqlSession;
import com.wangtao.dbhelper.domian.AddressVO;

/**
 * @author wangtao
 * Created at 2019/2/21 9:40
 */
public class AddressMapper {

    public AddressVO findById(Integer id) {
        SqlSession sqlSession = Utils.getSqlSession();
        try {
            AddressVO addressVO = sqlSession.selectOne("com.wangtao.dbhelper.mapper.AddressMapper.findById", id);
            sqlSession.commit();
            return addressVO;
        } catch (Exception e) {
            e.printStackTrace();
            sqlSession.rollback();
        } finally {
            sqlSession.close();
        }
        return null;
    }

    public AddressVO findByIdAutoMapping(Integer id) {
        SqlSession sqlSession = Utils.getSqlSession();
        try {
            AddressVO addressVO = sqlSession.selectOne("com.wangtao.dbhelper.mapper.AddressMapper.findByIdAutoMapping", id);
            sqlSession.commit();
            return addressVO;
        } catch (Exception e) {
            e.printStackTrace();
            sqlSession.rollback();
        } finally {
            sqlSession.close();
        }
        return null;
    }
}
