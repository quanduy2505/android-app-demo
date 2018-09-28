package com.app.tuan88291.testapp;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;

public class Save extends Fragment {
    static List<Data_save> data;
    public static RecyclerView list;
    public static Adapter_save mAdapter;
    public static LinearLayoutManager mLayoutManager;
    public static Context thiscontext;
    private ContentView ctv;
    DBhelper db;

    /* renamed from: com.app.tuan88291.testapp.Save.1 */
    class C09651 extends OnScrollListener {
        C09651() {
        }

        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            if (dy > 0) {
                Save.this.ctv;
                ContentView.hidenfil();
            }
        }
    }

    static {
        data = new ArrayList();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootview = inflater.inflate(C0336R.layout.save_layout, null);
        list = (RecyclerView) rootview.findViewById(C0336R.id.list);
        mLayoutManager = new LinearLayoutManager(thiscontext);
        thiscontext = container.getContext();
        this.db = new DBhelper(thiscontext);
        this.ctv = new ContentView();
        ContentView contentView = this.ctv;
        ContentView.puttitle("M\u00f3n \u0103n \u0111\u00e3 l\u01b0u");
        contentView = this.ctv;
        ContentView.setgone(0);
        contentView = this.ctv;
        ContentView.hidebt();
        contentView = this.ctv;
        ContentView.showsave();
        data = this.db.getall();
        mAdapter = new Adapter_save(thiscontext, data);
        list.setAdapter(mAdapter);
        list.setLayoutManager(mLayoutManager);
        if (data.size() <= 0) {
            Toast.makeText(thiscontext, "b\u1ea1n ch\u01b0a l\u01b0u m\u00f3n \u0103n n\u00e0o!", 0).show();
        }
        this.ctv.filtersave();
        list.addOnScrollListener(new C09651());
        return rootview;
    }
}
