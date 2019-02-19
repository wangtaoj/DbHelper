package com.wangtao.dbhelper.core;

import java.util.HashMap;

/**
 * @author wangtao
 * Created at 2019/2/14 10:01
 */
public class ParamMap extends HashMap<String, Object> {

    private static final long serialVersionUID = 6789635831404232953L;

    @Override
    public Object get(Object key) {
        if(!super.containsKey(key)) {
            throw new IllegalArgumentException("Parameter '" + key + "' not found. Available parameters are "
                    + this.keySet());
        }
        return super.get(key);
    }
}
