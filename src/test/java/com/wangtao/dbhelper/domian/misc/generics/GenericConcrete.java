package com.wangtao.dbhelper.domian.misc.generics;

public class GenericConcrete extends GenericSubclass implements GenericInterface<Long> {

    private Long id;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(String id) {
        this.id = Long.valueOf(id);
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public void setId(Integer id) {
        this.id = (long) id;
    }
}
