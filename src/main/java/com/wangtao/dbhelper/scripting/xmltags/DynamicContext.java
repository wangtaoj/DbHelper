package com.wangtao.dbhelper.scripting.xmltags;

import com.wangtao.dbhelper.core.Configuration;
import com.wangtao.dbhelper.reflection.MetaObject;
import ognl.OgnlContext;
import ognl.OgnlRuntime;
import ognl.PropertyAccessor;

import java.util.HashMap;
import java.util.Map;

/**
 * @author wangtao
 * Created at 2019/1/22 16:14
 */
public class DynamicContext {

    public static final String PARAMETER = "_parameter";

    static {
        OgnlRuntime.setPropertyAccessor(ContextMap.class, new ContextMapAccessor());
    }

    private ContextMap bindings;

    public DynamicContext(Configuration configuration, Object parameter) {
        if(parameter != null && !(parameter instanceof Map)) {
            bindings = new ContextMap(configuration.newMetaObject(parameter));
        } else {
            bindings = new ContextMap(null);
        }
        bindings.put(PARAMETER, parameter);
    }

    private StringBuffer sqlBuilder = new StringBuffer();

    public void bind(String name, Object value) {
        bindings.put(name, value);
    }

    public void appendSql(String sql) {
        sqlBuilder.append(sql);
        sqlBuilder.append(" ");
    }

    public String getSql() {
        return sqlBuilder.toString().trim();
    }

    public ContextMap getBindings() {
        return bindings;
    }

    public static class ContextMap extends HashMap<String, Object> {

        private static final long serialVersionUID = 6913043070411004656L;

        MetaObject metaObject;

        public ContextMap(MetaObject metaObject) {
            this.metaObject = metaObject;
        }

        @Override
        public Object get(Object key) {
            if(super.containsKey(key))
                return super.get(key);
            if(metaObject != null)
                return metaObject.getValue((String) key);
            return null;
        }
    }

    private static class ContextMapAccessor implements PropertyAccessor {
        @Override
        public Object getProperty(Map context, Object target, Object name) {
            Map contextMap = (Map) target;
            Object value = contextMap.get(name);
            if(contextMap.containsKey(name) || value != null) {
                return value;
            }
            Object parameter = contextMap.get(PARAMETER);
            if(parameter instanceof Map) {
                return ((Map) parameter).get(name);
            }
            return null;
        }

        @Override
        public void setProperty(Map context, Object target, Object name, Object value) {
            ContextMap contextMap = (ContextMap) target;
            contextMap.put((String) name, value);
        }

        @Override
        public String getSourceAccessor(OgnlContext context, Object target, Object index) {
            return null;
        }

        @Override
        public String getSourceSetter(OgnlContext context, Object target, Object index) {
            return null;
        }
    }
}
