package com.app.tuan88291.testapp;

public class list_grid {
    String idbv;
    String tenfile;
    String type;

    public list_grid(String idbv, String tenfile, String type) {
        this.idbv = idbv;
        this.tenfile = tenfile;
        this.type = type;
    }

    public String getIdbv() {
        return this.idbv;
    }

    public void setIdbv(String idbv) {
        this.idbv = idbv;
    }

    public String getTenfile() {
        return this.tenfile;
    }

    public void setTenfile(String tenfile) {
        this.tenfile = tenfile;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
