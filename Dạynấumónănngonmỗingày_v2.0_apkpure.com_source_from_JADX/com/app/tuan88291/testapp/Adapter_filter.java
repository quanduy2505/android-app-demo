package com.app.tuan88291.testapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.Collections;
import java.util.List;

public class Adapter_filter extends Adapter<ViewHolder> {
    private Context context;
    Data_home current;
    int currentPos;
    List<Data_filter> data;
    private DBhelper db;
    private LayoutInflater inflater;

    class MyHolder extends ViewHolder {
        TextView name;

        public MyHolder(View itemView) {
            super(itemView);
            this.name = (TextView) itemView.findViewById(C0336R.id.item);
        }
    }

    public Adapter_filter(Context context, List<Data_filter> data) {
        this.data = Collections.emptyList();
        this.currentPos = 0;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.data = data;
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyHolder(this.inflater.inflate(C0336R.layout.custom_filter, parent, false));
    }

    public void onBindViewHolder(ViewHolder holder, int position) {
        ((MyHolder) holder).name.setText(((Data_filter) this.data.get(position)).name);
    }

    public int getItemCount() {
        return this.data.size();
    }
}
