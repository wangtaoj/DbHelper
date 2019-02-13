package com.wangtao.dbhelper.core;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

/**
 * @author wangtao
 * Created at 2019/1/18 10:58
 */
public class SqlSessionFactoryTest {

    @Test
    public void build() throws IOException {
        SqlSessionFactory sqlSessionFactory =  new SqlSessionFactoryBuilder()
                .build(Resources.getResourceAsReader("com/wangtao/dbhelper/builder/mybatis-config.xml"));
        Assert.assertNotNull(sqlSessionFactory);
    }
}
