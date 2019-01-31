package com.wangtao.dbhelper.domian.blog;

import java.util.List;

public class BlogLite {

    private int id;
    private List<PostLite> posts;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<PostLite> getPosts() {
        return posts;
    }

    public void setPosts(List<PostLite> posts) {
        this.posts = posts;
    }
}
