package com.wangtao.dbhelper.core;

import com.wangtao.dbhelper.builder.xml.XmlConfigBuilder;
import com.wangtao.dbhelper.core.defaults.DefaultSqlSessionFactory;

import java.io.Reader;

/**
 * @author wangtao
 * Created at 2019/1/16 15:43
 */
public class SqlSessionFactoryBuilder {

    public SqlSessionFactory build(Reader reader) {
        XmlConfigBuilder parser = new XmlConfigBuilder(reader);
        Configuration configuration = parser.parse();
        return build(configuration);
    }

    public SqlSessionFactory build(Configuration configuration) {
        return new DefaultSqlSessionFactory(configuration);
    }
}
