package com.wangtao.dbhelper.scripting.xmltags;

import com.wangtao.dbhelper.core.Configuration;

import java.util.Collections;

/**
 * @author wangtao
 * Created at 2019/2/22 16:52
 */
public class SetSqlNode extends TrimSqlNode {

    public SetSqlNode(Configuration configuration, SqlNode contents) {
        super(configuration, contents, "set", null, Collections.singletonList(","), null);
    }
}
