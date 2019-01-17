package com.wangtao.dbhelper.core;

/**
 * @author wangtao
 * Created at 2019/1/16 15:41
 */
public interface SqlSessionFactory {

    /**
     * 创建SqlSession对象
     * @return SqlSession实例
     */
    SqlSession openSqlSession();
}
