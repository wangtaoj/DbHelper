package com.wangtao.dbhelper.core;

import com.wangtao.dbhelper.builder.xml.XMLConfigBuilder;
import com.wangtao.dbhelper.core.defaults.DefaultSqlSessionFactory;

import java.io.Reader;

/**
 * @author wangtao
 * Created at 2019/1/16 15:43
 */
public class SqlSessionFactoryBuilder {

    public SqlSessionFactory build(Reader reader) {
        XMLConfigBuilder parser = new XMLConfigBuilder(reader);
        Configuration configuration = parser.parse();
        return build(configuration);
    }

    public SqlSessionFactory build(Configuration configuration) {
        return new DefaultSqlSessionFactory(configuration);
    }
}
