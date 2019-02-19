package com.wangtao.dbhelper.logging.jdbc;

import com.wangtao.dbhelper.logging.Log;

import java.util.*;

/**
 * @author wangtao
 * Created at 2019/2/19 15:42
 */
public abstract class BaseJdbcLogger {

    protected static final Set<String> SET_METHODS = new HashSet<>();

    protected static final Set<String> EXECUTE_METHODS = new HashSet<>();

    protected final Map<Object, Object> COLUMN_MAP = new HashMap<>();

    static {
        SET_METHODS.add("setString");
        SET_METHODS.add("setNString");
        SET_METHODS.add("setInt");
        SET_METHODS.add("setByte");
        SET_METHODS.add("setShort");
        SET_METHODS.add("setLong");
        SET_METHODS.add("setDouble");
        SET_METHODS.add("setFloat");
        SET_METHODS.add("setTimestamp");
        SET_METHODS.add("setDate");
        SET_METHODS.add("setTime");
        SET_METHODS.add("setArray");
        SET_METHODS.add("setBigDecimal");
        SET_METHODS.add("setAsciiStream");
        SET_METHODS.add("setBinaryStream");
        SET_METHODS.add("setBlob");
        SET_METHODS.add("setBoolean");
        SET_METHODS.add("setBytes");
        SET_METHODS.add("setCharacterStream");
        SET_METHODS.add("setNCharacterStream");
        SET_METHODS.add("setClob");
        SET_METHODS.add("setNClob");
        SET_METHODS.add("setObject");
        SET_METHODS.add("setNull");

        EXECUTE_METHODS.add("execute");
        EXECUTE_METHODS.add("executeUpdate");
        EXECUTE_METHODS.add("executeQuery");
        EXECUTE_METHODS.add("addBatch");
    }

    protected Log statementLog;

    public BaseJdbcLogger(Log statementLog) {
        this.statementLog = statementLog;
    }

    /**
     * 移除字符串中多余的空白符(空格, 制表符, 回车换行符.)
     */
    protected String removeBreakingWhitespace(String original) {
        StringTokenizer whitespaceStripper = new StringTokenizer(original);
        StringBuilder builder = new StringBuilder();
        while (whitespaceStripper.hasMoreTokens()) {
            builder.append(whitespaceStripper.nextToken());
            builder.append(" ");
        }
        return builder.toString();
    }

    protected String getParameterAsString() {
        StringBuilder sb = new StringBuilder();
        for(Map.Entry<Object, Object> entry : COLUMN_MAP.entrySet()) {
            Object value = entry.getValue();
            if(value == null) {
                sb.append("null, ");
            } else {
                sb.append(String.valueOf(value)).append('(')
                        .append(value.getClass().getSimpleName())
                        .append("), ");
            }
        }
        if(sb.length() == 0)
            return sb.toString();
        return sb.toString().substring(0, sb.length() - 1);
    }
}
