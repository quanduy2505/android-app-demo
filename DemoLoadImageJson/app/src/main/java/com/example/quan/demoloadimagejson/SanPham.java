package com.example.quan.demoloadimagejson;

/**
 * Created by quan on 24/06/2017.
 */

public class SanPham {
    public String id;
    public String name;
    public String image;
    public String content;
    public String user_id;
    public String category_id;
    public String views;
    public String created_at;
    public String updated_at;

    public SanPham(String id, String name, String image, String content, String user_id, String category_id, String views, String created_at, String updated_at) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.content = content;
        this.user_id = user_id;
        this.category_id = category_id;
        this.views = views;
        this.created_at = created_at;
        this.updated_at = updated_at;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public String getViews() {
        return views;
    }

    public void setViews(String views) {
        this.views = views;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }
}
