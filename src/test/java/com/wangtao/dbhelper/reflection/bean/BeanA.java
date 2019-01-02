package com.wangtao.dbhelper.reflection.bean;

/**
 * Created by wangtao at 2018/12/26 9:56
 */
public class BeanA extends SuperBean {

    public String name;

    public Integer age;

    private String password;

    public void setPassword(String passowrd) {
        this.password = passowrd;
    }

    public String getPassword() {
        return password;
    }
}
