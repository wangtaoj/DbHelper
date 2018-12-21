package com.wangtao.dbhelper.core;

/**
 * Created by wangtao at 2018/12/20 16:27
 */
public class RowBounds {

    private static final int NO_OFFSET = -1;
    private static final int NO_LIMIT = Integer.MAX_VALUE;
    public static final RowBounds DEFAULT = new RowBounds(NO_OFFSET, NO_LIMIT);

    /**
     * 数据起始行
     */
    private int offset;

    /**
     * 需查询的总共条数
     */
    private int limit;

    public RowBounds(int offset, int limit) {
        this.offset = offset;
        this.limit = limit;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }
}
