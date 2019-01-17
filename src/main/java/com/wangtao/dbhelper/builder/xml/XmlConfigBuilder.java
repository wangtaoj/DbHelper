package com.wangtao.dbhelper.builder.xml;

import com.wangtao.dbhelper.builder.BaseBuilder;
import com.wangtao.dbhelper.builder.BuilderException;
import com.wangtao.dbhelper.core.Configuration;
import com.wangtao.dbhelper.parser.DtdEntityResolver;
import com.wangtao.dbhelper.parser.XNode;
import com.wangtao.dbhelper.parser.XpathParser;
import com.wangtao.dbhelper.reflection.DefaultReflectorFactory;
import com.wangtao.dbhelper.reflection.MetaClass;
import com.wangtao.dbhelper.reflection.ReflectorFactory;

import java.io.Reader;
import java.util.Properties;

/**
 * @author wangtao
 * Created at 2019/1/16 15:58
 */
public class XmlConfigBuilder extends BaseBuilder {

    private XpathParser parser;

    private boolean parsed;

    private ReflectorFactory reflectorFactory = new DefaultReflectorFactory();

    public XmlConfigBuilder(Reader reader) {
        super(new Configuration());
        this.parsed = false;
        this.parser = new XpathParser.Builder().
                reader(reader).validating(true)
                .entityResolver(new DtdEntityResolver())
                .build();
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
        propertiesElements(root.evalNode("properties"));
        Properties properties = settingsAsProperties(root.evalNode("settings"));
        settingsElement(properties);
    }

    private void propertiesElements(XNode properties) {
        if (properties != null) {
            Properties variables = properties.getChildrenAsProperties();
            configuration.getVariables().putAll(variables);
            parser.setVariables(configuration.getVariables());
        }
    }

    private Properties settingsAsProperties(XNode settingsElement) {
        if(settingsElement != null) {
            Properties settings = settingsElement.getChildrenAsProperties();
            MetaClass metaClass = MetaClass.forClass(Configuration.class, reflectorFactory);
            for(Object key : settings.keySet()) {
                String propName = (String) key;
                if(!metaClass.hasSetter(propName)) {
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
}
