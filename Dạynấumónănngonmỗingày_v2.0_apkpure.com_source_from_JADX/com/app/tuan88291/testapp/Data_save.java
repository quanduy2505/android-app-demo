package com.app.tuan88291.testapp;

public class Data_save {
    String idbv;
    String noidung;
    String theloai;
    String tit;

    public Data_save(String tit, String noidung, String theloai, String idbv) {
        this.idbv = idbv;
        this.noidung = noidung;
        this.tit = tit;
        this.theloai = theloai;
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

    public String getTit() {
        return this.tit;
    }

    public void setTit(String tit) {
        this.tit = tit;
    }

    public String getTheloai() {
        return this.theloai;
    }

    public void setTheloai(String theloai) {
        this.theloai = theloai;
    }
}
