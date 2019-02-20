package com.wangtao.dbhelper.executor.keygen;

import com.wangtao.dbhelper.mapping.MappedStatement;

import java.sql.Statement;

/**
 * @author wangtao
 * Created at 2019/2/19 15:09
 */
public class NoKeyGenerator implements KeyGenerator {

    public static final KeyGenerator INSTANCE = new NoKeyGenerator();

    @Override
    public void processAfter(MappedStatement ms, Statement statement, Object parameter) {
        // do nothing.
    }
}
