package com.app.tuan88291.testapp;

public class Data_info_head {
    private String idfb;
    private String link;
    private String name;
    private String tt;

    public Data_info_head(String link, String tt, String name, String idfb) {
        this.link = link;
        this.tt = tt;
        this.name = name;
        this.idfb = idfb;
    }

    public String getLink() {
        return this.link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getTt() {
        return this.tt;
    }

    public void setTt(String tt) {
        this.tt = tt;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIdfb() {
        return this.idfb;
    }

    public void setIdfb(String idfb) {
        this.idfb = idfb;
    }
}
