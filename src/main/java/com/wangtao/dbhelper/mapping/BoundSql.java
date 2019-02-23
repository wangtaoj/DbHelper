package com.wangtao.dbhelper.mapping;

import com.wangtao.dbhelper.core.Configuration;
import com.wangtao.dbhelper.reflection.MetaObject;
import com.wangtao.dbhelper.reflection.property.PropertyTokenizer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author wangtao
 * Created at 2019/1/22 10:57
 */
public class BoundSql {

    private final Configuration configuration;

    /**
     * 完整的SQL
     */
    private final String sql;

    /**
     * 参数映射集合(#占位符)
     */
    private final List<ParameterMapping> parameterMappings;

    /**
     * 真实参数
     */
    private final Object parameter;

    /**
     * 额外参数, 动态标签(foreach)生成.
     */
    private final Map<String, Object> additionalParameterMap;

    /**
     * 额外参数对应的MetaObject.
     */
    private final MetaObject metaAdditionalParameter;

    public BoundSql(Configuration configuration, String sql, List<ParameterMapping> parameterMappings, Object parameter) {
        this.configuration = configuration;
        this.sql = sql;
        this.parameterMappings = parameterMappings;
        this.parameter = parameter;
        this.additionalParameterMap = new HashMap<>();
        this.metaAdditionalParameter = configuration.newMetaObject(this.additionalParameterMap);
    }

    /**
     * 判断参数映射集合里的参数名称对应的值在不在额外参数中.
     * @param propName 属性名
     * @return 额外参数存在此属性返回true, 否则返回false
     */
    public boolean hasAdditionalParameter(String propName) {
        PropertyTokenizer tokenizer = new PropertyTokenizer(propName);
        return additionalParameterMap.containsKey(tokenizer.getName());
    }

    /**
     * 设置参数值到additionalParameterMap中.
     * @param name  属性名
     * @param value 属性值
     */
    public void setAdditionalParameter(String name, Object value) {
        metaAdditionalParameter.setValue(name, value);
    }

    /**
     * 从additionalParameterMap获取对应的属性值.
     * @param name 属性名
     * @return 返回对应的属性值
     */
    public Object getAdditionalParameter(String name) {
        return metaAdditionalParameter.getValue(name);
    }

    public String getSql() {
        return sql;
    }

    public List<ParameterMapping> getParameterMappings() {
        return parameterMappings;
    }

    public Object getParameter() {
        return parameter;
    }

    public Configuration getConfiguration() {
        return configuration;
    }
}
