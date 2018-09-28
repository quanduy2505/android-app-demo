package com.app.tuan88291.testapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class About extends Fragment {
    private ContentView ctv;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootview = inflater.inflate(C0336R.layout.about_layout, null);
        this.ctv = new ContentView();
        ContentView contentView = this.ctv;
        ContentView.puttitle("Th\u00f4ng tin");
        contentView = this.ctv;
        ContentView.setgone(0);
        contentView = this.ctv;
        ContentView.hidenall();
        contentView = this.ctv;
        ContentView.hidebt();
        return rootview;
    }
}
