package com.wangtao.dbhelper.builder;

import com.wangtao.dbhelper.builder.xml.XMLConfigBuilder;
import com.wangtao.dbhelper.core.Configuration;
import com.wangtao.dbhelper.core.Resources;
import com.wangtao.dbhelper.datasource.PoolDataSource;
import com.wangtao.dbhelper.domian.Address;
import com.wangtao.dbhelper.domian.User;
import com.wangtao.dbhelper.mapping.Environment;
import com.wangtao.dbhelper.type.JdbcType;
import com.wangtao.dbhelper.type.TypeAliasRegistry;
import org.junit.Test;

import java.io.IOException;
import java.io.Reader;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author wangtao
 * Created at 2019/2/13 14:35
 */
public class XMLConfigBuilderTest {

    @Test
    public void parseSimpleConfig() throws IOException {
        String resource = "com/wangtao/dbhelper/builder/simple-mybatis-config.xml";
        try (Reader reader = Resources.getResourceAsReader(resource)) {
            XMLConfigBuilder builder = new XMLConfigBuilder(reader);
            Configuration configuration = builder.parse();
            assertThat(configuration).isNotNull();

            assertThat(configuration.getJdbcTypeForNull()).isEqualTo(JdbcType.OTHER);
            assertThat(configuration.isCallSettersOnNulls()).isFalse();
            assertThat(configuration.isMapUnderscoreToCamelCase()).isFalse();
            assertThat(configuration.isReturnInstanceForEmptyRow()).isFalse();
            assertThat(configuration.isUseActualParamName()).isTrue();

            assertThat(configuration.getVariables().size()).isEqualTo(4);
            assertThat(configuration.getVariables()).containsKeys("driver", "url", "username", "password");
            assertThat(configuration.getVariables().getProperty("driver")).isEqualTo("com.mysql.jdbc.Driver");

            Environment environment = configuration.getEnvironment();
            assertNotNull(environment);
            assertEquals("mysql_development", environment.getId());
            assertThat(environment.getDataSource()).isNotNull().isInstanceOf(PoolDataSource.class);
        }
    }

    @Test
    public void parseMybatisConfig() throws IOException {
        String resource = "com/wangtao/dbhelper/builder/complex-mybatis-config.xml";
        try (Reader reader = Resources.getResourceAsReader(resource)) {
            XMLConfigBuilder builder = new XMLConfigBuilder(reader);
            Configuration configuration = builder.parse();
            assertThat(configuration).isNotNull();
            assertThat(configuration.getJdbcTypeForNull()).isEqualTo(JdbcType.OBJECT);
            assertThat(configuration.isCallSettersOnNulls()).isTrue();
            assertThat(configuration.isMapUnderscoreToCamelCase()).isTrue();
            assertThat(configuration.isReturnInstanceForEmptyRow()).isTrue();
            assertThat(configuration.isUseActualParamName()).isFalse();

            assertThat(configuration.getVariables().size()).isEqualTo(4);
            assertThat(configuration.getVariables()).containsKeys("driver", "url", "username", "password");
            assertThat(configuration.getVariables().getProperty("driver")).isEqualTo("com.mysql.jdbc.Driver");

            TypeAliasRegistry typeAliasRegistry = configuration.getTypeAliasRegistry();
            assertThat(typeAliasRegistry.resolveAlias("user")).isEqualTo(User.class);
            assertThat(typeAliasRegistry.resolveAlias("address")).isEqualTo(Address.class);

            Environment environment = configuration.getEnvironment();
            assertNotNull(environment);
            assertEquals("mysql_development", environment.getId());
            assertThat(environment.getDataSource()).isNotNull().isInstanceOf(PoolDataSource.class);
        }
    }
}
