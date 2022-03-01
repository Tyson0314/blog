package com.dabin.entity;

public class BlogTag {
    private Integer id;

    private Integer tagId;

    private Integer blogId;

    public BlogTag(Integer id, Integer tagId, Integer blogId) {
        this.id = id;
        this.tagId = tagId;
        this.blogId = blogId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getTagId() {
        return tagId;
    }

    public void setTagId(Integer tagId) {
        this.tagId = tagId;
    }

    public Integer getBlogId() {
        return blogId;
    }

    public void setBlogId(Integer blogId) {
        this.blogId = blogId;
    }
}
