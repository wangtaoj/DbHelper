package com.wangtao.dbhelper.mapper;

import com.wangtao.dbhelper.core.SqlSession;
import com.wangtao.dbhelper.domian.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * @author wangtao
 * Created at 2019/2/21 9:40
 */
public class UserMapper {

    public User findById(Integer id) {
        SqlSession sqlSession = Utils.getSqlSession();
        try {
            User user = sqlSession.selectOne("com.wangtao.dbhelper.binding.UserMapper.findById", id);
            sqlSession.commit();
            return user;
        } catch (Exception e) {
            e.printStackTrace();
            sqlSession.rollback();
        } finally {
            sqlSession.close();
        }
        return null;
    }

    public int count() {
        SqlSession sqlSession = Utils.getSqlSession();
        try {
            int rows = sqlSession.selectOne("com.wangtao.dbhelper.binding.UserMapper.count");
            sqlSession.commit();
            return rows;
        } catch (Exception e) {
            e.printStackTrace();
            sqlSession.rollback();
        } finally {
            sqlSession.close();
        }
        return 0;
    }

    public Map<String, Object> findToMapById(Integer id) {
        SqlSession sqlSession = Utils.getSqlSession();
        try {
            Map<String, Object> result = sqlSession.selectOne("com.wangtao.dbhelper.binding.UserMapper.findToMapById", id);
            sqlSession.commit();
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            sqlSession.rollback();
        } finally {
            sqlSession.close();
        }
        return null;
    }

    public User findByIdAutoMapping(Integer id) {
        SqlSession sqlSession = Utils.getSqlSession();
        try {
            User user = sqlSession.selectOne("com.wangtao.dbhelper.binding.UserMapper.findByIdAutoMapping", id);
            sqlSession.commit();
            return user;
        } catch (Exception e) {
            e.printStackTrace();
            sqlSession.rollback();
        } finally {
            sqlSession.close();
        }
        return null;
    }

    public List<User> findByEntity(User entity) {
        SqlSession sqlSession = Utils.getSqlSession();
        try {
            List<User> user = sqlSession.selectList("com.wangtao.dbhelper.binding.UserMapper.findByEntity", entity);
            sqlSession.commit();
            return user;
        } catch (Exception e) {
            e.printStackTrace();
            sqlSession.rollback();
        } finally {
            sqlSession.close();
        }
        return null;
    }

    public List<User> findByMap(Map<String, Object> params) {
        SqlSession sqlSession = Utils.getSqlSession();
        try {
            List<User> users = sqlSession.selectList("com.wangtao.dbhelper.binding.UserMapper.findByMap", params);
            sqlSession.commit();
            return users;
        } catch (Exception e) {
            e.printStackTrace();
            sqlSession.rollback();
        } finally {
            sqlSession.close();
        }
        return null;
    }

    public Integer findAgeById(Integer id) {
        SqlSession sqlSession = Utils.getSqlSession();
        try {
            Integer age = sqlSession.selectOne("com.wangtao.dbhelper.binding.UserMapper.findAgeById", id);
            sqlSession.commit();
            return age;
        } catch (Exception e) {
            e.printStackTrace();
            sqlSession.rollback();
        } finally {
            sqlSession.close();
        }
        return null;
    }

    public LocalDate findBirthdayById(Integer id) {
        SqlSession sqlSession = Utils.getSqlSession();
        try {
            LocalDate birthday = sqlSession.selectOne("com.wangtao.dbhelper.binding.UserMapper.findBirthdayById", id);
            sqlSession.commit();
            return birthday;
        } catch (Exception e) {
            e.printStackTrace();
            sqlSession.rollback();
        } finally {
            sqlSession.close();
        }
        return null;
    }

    public int insert(User user) {
        SqlSession sqlSession = Utils.getSqlSession();
        try {
            int rows = sqlSession.insert("com.wangtao.dbhelper.binding.UserMapper.insert", user);
            sqlSession.commit();
            return rows;
        } catch (Exception e) {
            e.printStackTrace();
            sqlSession.rollback();
        } finally {
            sqlSession.close();
        }
        return 0;
    }

    public int insertAndReturnKey(User user) {
        SqlSession sqlSession = Utils.getSqlSession();
        try {
            int rows = sqlSession.insert("com.wangtao.dbhelper.binding.UserMapper.insertAndReturnKey", user);
            sqlSession.commit();
            return rows;
        } catch (Exception e) {
            e.printStackTrace();
            sqlSession.rollback();
        } finally {
            sqlSession.close();
        }
        return 0;
    }

    public int delete(Integer id) {
        SqlSession sqlSession = Utils.getSqlSession();
        try {
            int rows = sqlSession.insert("com.wangtao.dbhelper.binding.UserMapper.delete", id);
            sqlSession.commit();
            return rows;
        } catch (Exception e) {
            e.printStackTrace();
            sqlSession.rollback();
        } finally {
            sqlSession.close();
        }
        return 0;
    }

    /*
     * 动态SQL(foreach)
     */
    public List<User> findByAgeIn(List<Integer> ages) {
        SqlSession sqlSession = Utils.getSqlSession();
        try {
            List<User> users = sqlSession.selectList("com.wangtao.dbhelper.binding.UserMapper.findByAgeIn", ages);
            sqlSession.commit();
            return users;
        } catch (RuntimeException e) {
            sqlSession.rollback();
            throw e;
        } finally {
            sqlSession.close();
        }
    }

    /*
     * 动态SQL(where, if)
     */
    public List<User> findByCondition(User condition) {
        SqlSession sqlSession = Utils.getSqlSession();
        try {
            List<User> users = sqlSession.selectList("com.wangtao.dbhelper.binding.UserMapper.findByCondition", condition);
            sqlSession.commit();
            return users;
        } catch (RuntimeException e) {
            sqlSession.rollback();
            throw e;
        } finally {
            sqlSession.close();
        }
    }

}
