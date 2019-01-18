package com.wangtao.dbhelper.bean;

public class Address {
    private Integer id;

    private String province;

    private String city;

    private String address;

    private User user;


    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getProvince() {
        return this.province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return this.city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAddress() {
        return this.address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public User getUserId() {
        return this.user;
    }

    public void setUserId(User user) {
        this.user = user;
    }

}

