package com.wangtao.dbhelper.scripting.xmltags;

import com.wangtao.dbhelper.core.Configuration;

import java.util.Arrays;
import java.util.List;

/**
 * @author wangtao
 * Created at 2019/2/22 16:41
 */
public class WhereSqlNode extends TrimSqlNode {

    private static List<String> prefixOverrides = Arrays.asList("AND ","OR ","AND\n", "OR\n", "AND\r", "OR\r", "AND\t", "OR\t");

    public WhereSqlNode(Configuration configuration, SqlNode contents) {
        super(configuration, contents, "WHERE", null, prefixOverrides, null);
    }
}
