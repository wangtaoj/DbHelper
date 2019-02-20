package com.wangtao.dbhelper.executor.parameter;

import java.sql.PreparedStatement;

/**
 * @author wangtao
 * Created at 2019/2/20 15:36
 */
public interface ParameterHandler {

    /**
     * 获取参数对象
     * @return 返回参数对象
     */
    Object getParameterObject();

    /**
     * 设置参数信息.
     * @param ps PreparedStatement对象
     */
    void setParameters(PreparedStatement ps);
}
