package com.wangtao.dbhelper.mapper;

import com.wangtao.dbhelper.core.Resources;
import com.wangtao.dbhelper.core.SqlSession;
import com.wangtao.dbhelper.core.SqlSessionFactory;
import com.wangtao.dbhelper.core.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.Reader;

/**
 * @author wangtao
 * Created at 2019/2/21 9:41
 */
public class Utils {

    static SqlSessionFactory sqlSessionFactory;

    static {
        try (Reader reader = Resources.getResourceAsReader("com/wangtao/dbhelper/mapper/config/mybatis-config.xml")) {
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static SqlSession getSqlSession() {
        return sqlSessionFactory.openSqlSession();
    }
}
