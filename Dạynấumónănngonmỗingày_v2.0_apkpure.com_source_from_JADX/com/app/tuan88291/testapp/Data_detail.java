package com.app.tuan88291.testapp;

public class Data_detail {
    String idbv;
    String type;
    String url;

    public Data_detail(String url, String idbv, String type) {
        this.url = url;
        this.idbv = idbv;
        this.type = type;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getIdbv() {
        return this.idbv;
    }

    public void setIdbv(String idbv) {
        this.idbv = idbv;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
