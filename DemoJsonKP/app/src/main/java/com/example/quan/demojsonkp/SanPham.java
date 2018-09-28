package com.example.quan.demojsonkp;

/**
 * Created by quan on 05/06/2017.
 */

public class SanPham {
    String id;
    String name;
    String content;
    String user_id;
    String category_id;
    String nviews;
    String created_date;
    String updated_date;
    String image;

    public SanPham(String id, String name, String content, String user_id, String category_id, String nviews, String created_date, String updated_date, String image) {
        this.id = id;
        this.name = name;
        this.content = content;
        this.user_id = user_id;
        this.category_id = category_id;
        this.nviews = nviews;
        this.created_date = created_date;
        this.updated_date = updated_date;
        this.image = image;
    }

}
