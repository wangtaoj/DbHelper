package com.wangtao.dbhelper.reflection.typeparam;

public class Calculator<T> {

    protected T id;

    @SuppressWarnings("unused")
    private T fld;

    @SuppressWarnings("unused")
    protected T attribute;

    public T getId() {
        return id;
    }

    public void setId(T id) {
        this.id = id;
    }

    public static class SubCalculator extends Calculator<String> {
    }
}
