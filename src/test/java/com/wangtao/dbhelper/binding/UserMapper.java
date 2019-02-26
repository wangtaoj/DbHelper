package com.wangtao.dbhelper.binding;

import com.wangtao.dbhelper.domian.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * @author wangtao
 * Created at 2019/2/26 16:04
 */
interface UserMapper {

    User findById(Integer id);

    int count();

    Map<String, Object> findToMapById(Integer id);


    User findByIdAutoMapping(Integer id);

    List<User> findByEntity(User entity);

    List<User> findByMap(Map<String, Object> params);

    Integer findAgeById(Integer id);

    LocalDate findBirthdayById(Integer id);

    int insert(User user);

    int insertAndReturnKey(User user);

    int delete(Integer id);

    /*
     * 动态SQL(foreach)
     */
    List<User> findByAgeIn(List<Integer> ages);

    /*
     * 动态SQL(where, if)
     */
    List<User> findByCondition(User condition);
}
