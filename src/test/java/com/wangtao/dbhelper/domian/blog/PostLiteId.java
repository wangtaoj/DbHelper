package com.wangtao.dbhelper.domian.blog;

public class PostLiteId {
    private int id;

    public PostLiteId() {

    }

    public void setId(int id) {
        this.id = id;
    }

    public PostLiteId(int aId) {
        id = aId;
    }

    public int getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final PostLiteId that = (PostLiteId) o;

        return id == that.id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
