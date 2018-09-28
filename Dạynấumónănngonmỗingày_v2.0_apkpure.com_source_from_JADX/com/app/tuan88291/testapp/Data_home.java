package com.app.tuan88291.testapp;

public class Data_home {
    String date;
    String id_type;
    String idbv;
    String idfb;
    String imgtype;
    String like;
    String name;
    String noidung;
    String stt;
    String theloai;
    String tit;
    String type;
    String url;

    public Data_home(String idbv, String noidung, String idfb, String type, String date, String name, String like, String tit, String url, String stt, String imgtype, String theloai, String id_type) {
        this.idbv = idbv;
        this.noidung = noidung;
        this.idfb = idfb;
        this.type = type;
        this.date = date;
        this.name = name;
        this.like = like;
        this.tit = tit;
        this.url = url;
        this.stt = stt;
        this.imgtype = imgtype;
        this.theloai = theloai;
        this.id_type = id_type;
    }

    public String getIdbv() {
        return this.idbv;
    }

    public void setIdbv(String idbv) {
        this.idbv = idbv;
    }

    public String getNoidung() {
        return this.noidung;
    }

    public void setNoidung(String noidung) {
        this.noidung = noidung;
    }

    public String getIdfb() {
        return this.idfb;
    }

    public void setIdfb(String idfb) {
        this.idfb = idfb;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDate() {
        return this.date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLike() {
        return this.like;
    }

    public void setLike(String like) {
        this.like = like;
    }

    public String getTit() {
        return this.tit;
    }

    public void setTit(String tit) {
        this.tit = tit;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getStt() {
        return this.stt;
    }

    public void setStt(String stt) {
        this.stt = stt;
    }

    public String getImgtype() {
        return this.imgtype;
    }

    public void setImgtype(String imgtype) {
        this.imgtype = imgtype;
    }

    public String getTheloai() {
        return this.theloai;
    }

    public void setTheloai(String theloai) {
        this.theloai = theloai;
    }

    public String getId_type() {
        return this.id_type;
    }

    public void setId_type(String id_type) {
        this.id_type = id_type;
    }
}
