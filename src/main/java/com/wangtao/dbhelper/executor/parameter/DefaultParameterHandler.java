package com.wangtao.dbhelper.executor.parameter;

import com.wangtao.dbhelper.core.Configuration;
import com.wangtao.dbhelper.mapping.BoundSql;
import com.wangtao.dbhelper.mapping.ParameterMapping;
import com.wangtao.dbhelper.reflection.MetaObject;
import com.wangtao.dbhelper.type.JdbcType;
import com.wangtao.dbhelper.type.TypeException;
import com.wangtao.dbhelper.type.TypeHandler;
import com.wangtao.dbhelper.type.TypeHandlerRegistry;

import java.sql.PreparedStatement;
import java.util.List;

/**
 * @author wangtao
 * Created at 2019/2/20 15:38
 */
public class DefaultParameterHandler implements ParameterHandler {

    protected Object parameter;

    protected BoundSql boundSql;

    protected final Configuration configuration;

    protected TypeHandlerRegistry typeHandlerRegistry;

    public DefaultParameterHandler(Configuration configuration, BoundSql boundSql) {
        this.configuration = configuration;
        this.typeHandlerRegistry = configuration.getTypeHandlerRegistry();
        this.boundSql = boundSql;
        this.parameter = boundSql.getParameter();
    }

    @Override
    public Object getParameterObject() {
        return parameter;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void setParameters(PreparedStatement ps) {
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        for (int i = 0; i < parameterMappings.size(); i++) {
            ParameterMapping parameterMapping = parameterMappings.get(i);
            Object value;
            String property = parameterMapping.getProperty();
            if (boundSql.hasAdditionalParameter(property)) {
                value = boundSql.getAdditionalParameter(property);
            } else if (parameter == null) {
                value = null;
            } else if (typeHandlerRegistry.hasTypeHandler(parameter.getClass())) {
                value = parameter;
            } else {
                MetaObject metaObject = configuration.newMetaObject(parameter);
                value = metaObject.getValue(property);
            }
            TypeHandler typeHandler = parameterMapping.getTypeHandler();
            JdbcType jdbcType = parameterMapping.getJdbcType();
            if (value == null && jdbcType == null) {
                jdbcType = configuration.getJdbcTypeForNull();
            }
            Class<?> javaType = value == null ? Object.class : value.getClass();
            if (typeHandler == null) {
                typeHandler = typeHandlerRegistry.getTypeHandler(javaType, jdbcType);
            }
            if (typeHandler == null) {
                throw new TypeException("we can't find a suitable type handler to set parameter for value '"
                        + value + "'.");
            }
            typeHandler.setParameter(ps, i + 1, value, jdbcType);
        }
    }
}
