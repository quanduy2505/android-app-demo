package com.example.quan.demojsonkp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by quan on 06/06/2017.
 */
public class ListAdapter extends ArrayAdapter<SanPham> {

    public ListAdapter(Context context, int resource, List<SanPham> items) {
        super(context, resource, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;
        if (v == null) {
            LayoutInflater vi;
         vi   = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.activity_dong_san_pham, null);
        }
        SanPham p = getItem(position);
        if (p != null) {
            // Anh xa + Gan gia tri
            TextView tt1 = (TextView) v.findViewById(R.id.id);
            tt1.setText(p.id);
            TextView tt2 = (TextView) v.findViewById(R.id.name);
            tt2.setText(p.name);
            TextView tt3 = (TextView) v.findViewById(R.id.content);
            tt3.setText(p.content);
            TextView tt4 = (TextView) v.findViewById(R.id.user_id);
            tt4.setText(p.user_id);
            TextView tt5 = (TextView) v.findViewById(R.id.category_id);
            tt5.setText(p.category_id);
            TextView tt6 = (TextView) v.findViewById(R.id.nviews);
            tt6.setText(p.nviews);
            TextView tt7 = (TextView) v.findViewById(R.id.created_date);
            tt7.setText(p.created_date);
            TextView tt8 = (TextView) v.findViewById(R.id.updated_date);
            tt8.setText(p.updated_date);
        }
        return v;
    }

}