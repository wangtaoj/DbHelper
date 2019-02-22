package com.wangtao.dbhelper.builder.xml;

import com.wangtao.dbhelper.builder.BaseBuilder;
import com.wangtao.dbhelper.builder.BuilderException;
import com.wangtao.dbhelper.core.Configuration;
import com.wangtao.dbhelper.core.Resources;
import com.wangtao.dbhelper.datasource.DataSourceFactory;
import com.wangtao.dbhelper.mapping.Environment;
import com.wangtao.dbhelper.parser.DtdEntityResolver;
import com.wangtao.dbhelper.parser.XNode;
import com.wangtao.dbhelper.parser.XpathParser;
import com.wangtao.dbhelper.reflection.DefaultReflectorFactory;
import com.wangtao.dbhelper.reflection.MetaClass;
import com.wangtao.dbhelper.reflection.ReflectorFactory;
import com.wangtao.dbhelper.transaction.TransactionFactory;
import com.wangtao.dbhelper.type.TypeAliasRegistry;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

/**
 * @author wangtao
 * Created at 2019/1/16 15:58
 */
public class XMLConfigBuilder extends BaseBuilder {

    private XpathParser parser;

    private boolean parsed;

    private ReflectorFactory reflectorFactory = new DefaultReflectorFactory();

    public XMLConfigBuilder(Reader reader) {
        this(reader, null);
    }

    public XMLConfigBuilder(Reader reader, Properties variables) {
        super(new Configuration());
        this.parsed = false;
        this.parser = new XpathParser.Builder()
                .reader(reader).validating(true)
                .entityResolver(new DtdEntityResolver())
                .variables(variables).build();
    }

    public Configuration parse() {
        if (parsed) {
            throw new BuilderException("每一个XmlConfigBuilder实例只能使用一次");
        }
        parsed = true;
        parseConfiguration(parser.evalNode("/configuration"));
        return configuration;
    }

    private void parseConfiguration(XNode root) {
        try {
            propertiesElement(root.evalNode("properties"));
            Properties properties = settingsAsProperties(root.evalNode("settings"));
            settingsElement(properties);
            typeAliasesElement(root.evalNode("typeAliases"));
            environmentsElement(root.evalNode("environments"));
            mappersElement(root.evalNode("mappers"));
        } catch (Exception e) {
            throw new BuilderException("解析config文件出现严重错误.", e);
        }
    }

    private void propertiesElement(XNode properties) {
        if (properties != null) {
            Properties variables = properties.getChildrenAsProperties();
            configuration.getVariables().putAll(variables);
            parser.setVariables(configuration.getVariables());
        }
    }

    private Properties settingsAsProperties(XNode settingsElement) {
        if (settingsElement != null) {
            Properties settings = settingsElement.getChildrenAsProperties();
            MetaClass metaClass = MetaClass.forClass(Configuration.class, reflectorFactory);
            for (Object key : settings.keySet()) {
                String propName = (String) key;
                if (!metaClass.hasSetter(propName)) {
                    throw new BuilderException("这个设置 " + propName + "不存在, 请保证拼写正确!");
                }
            }
            return settings;
        }
        return new Properties();
    }

    private void settingsElement(Properties properties) {
        configuration.setMapUnderscoreToCamelCase(booleanOfValue(properties.getProperty("mapUnderscoreToCamelCase"), false));
        configuration.setCallSettersOnNulls(booleanOfValue(properties.getProperty("callSettersOnNulls"), false));
    }

    private void typeAliasesElement(XNode typeAliases) {
        if (typeAliases != null) {
            List<XNode> children = typeAliases.getChildren();
            for (XNode typeAlias : children) {
                String alias = typeAlias.getStringAttribute("alias");
                String type = typeAlias.getStringAttribute("type");
                if (type == null) {
                    throw new BuilderException("typeAlias元素需要一个type属性, 用来告知为哪个类配置别名");
                }
                try {
                    Class<?> clazz = Resources.classForName(type);
                    TypeAliasRegistry typeAliasRegistry = configuration.getTypeAliasRegistry();
                    if (alias != null) {
                        typeAliasRegistry.register(alias, clazz);
                    } else {
                        typeAliasRegistry.register(clazz);
                    }
                } catch (ClassNotFoundException e) {
                    throw new BuilderException("注册别名时, 找不到类" + type + ". 原因: " + e);
                }
            }
        }
    }

    private void environmentsElement(XNode environments) {
        if (environments != null) {
            try {
                String defaultEnv = environments.getStringAttribute("default");
                List<XNode> children = environments.evalNodes("environment");
                XNode environmentNode = specifyEnvironment(children, defaultEnv);
                TransactionFactory transactionFactory = transactionManagerElement(environmentNode.
                        evalNode("transactionManager"));
                XNode dataSourceNode = environmentNode.evalNode("dataSource");
                DataSourceFactory dataSourceFactory = dataSourceElement(dataSourceNode);
                Properties properties = dataSourceNode.getChildrenAsProperties();
                DataSource dataSource = dataSourceFactory.getDataSource(properties);
                Environment environment = Environment.newEnvironment(defaultEnv, transactionFactory, dataSource);
                configuration.setEnvironment(environment);
            } catch (Exception e) {
                throw new BuilderException("解析environments元素出现错误. 原因: " + e);
            }
        } else {
            throw new BuilderException("必须指定一个环境, environments元素是必须的.");
        }
    }

    private XNode specifyEnvironment(List<XNode> environments, String defaultEnv) {
        if (defaultEnv == null) {
            throw new BuilderException("environments元素需要一个defaultEnv属性.");
        }
        if (environments.isEmpty()) {
            throw new BuilderException("必须指定一个环境, environments元素至少有一个environment子元素.");
        }
        for (XNode environment : environments) {
            if (Objects.equals(environment.getStringAttribute("id"), defaultEnv)) {
                return environment;
            }
        }
        throw new BuilderException("没有找到指定环境, 指定值:" + defaultEnv);
    }

    private TransactionFactory transactionManagerElement(XNode transactionManager) throws Exception {
        if (transactionManager != null) {
            String type = transactionManager.getStringAttribute("type");
            Class<? extends TransactionFactory> clazz = resolveAlias(type);
            return clazz.newInstance();
        }
        throw new BuilderException("environment元素必须有一个transactionManager子元素.");
    }

    private DataSourceFactory dataSourceElement(XNode context) throws Exception {
        if (context != null) {
            String type = context.getStringAttribute("type");
            return (DataSourceFactory) resolveAlias(type).newInstance();
        }
        throw new BuilderException("environment元素必须有一个dataSource子元素.");
    }

    private void mappersElement(XNode context) throws IOException {
        if (context != null) {
            List<XNode> mapperElements = context.getChildren();
            for (XNode mapperElement : mapperElements) {
                String resource = mapperElement.getStringAttribute("resource");
                new XMLMapperBuilder(configuration, Resources.getResourceAsReader(resource), resource).parse();
            }
        }
    }
}
