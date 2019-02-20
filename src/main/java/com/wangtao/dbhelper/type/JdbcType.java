package com.wangtao.dbhelper.type;

import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by wangtao at 2019/1/2 16:21
 */
public enum JdbcType {

    INTEGER(Types.INTEGER),
    BIGINT(Types.BIGINT),
    BOOLEAN(Types.BOOLEAN),
    VARCHAR(Types.VARCHAR),
    DECIMAL(Types.DECIMAL),
    DATE(Types.DATE),
    TIME(Types.TIME),
    TIMESTAMP(Types.TIMESTAMP),
    OBJECT(Types.JAVA_OBJECT),
    OTHER(Types.OTHER);

    private final int code;
    private static Map<Integer, JdbcType> cacheMap = new HashMap<>();

    static {
        for(JdbcType jdbcType : values()) {
            cacheMap.put(jdbcType.code, jdbcType);
        }
    }

    JdbcType(int code) {
        this.code = code;
    }

    public static JdbcType forCode(int code) {
        return cacheMap.get(code);
    }

    public int getCode() {
        return code;
    }
}
