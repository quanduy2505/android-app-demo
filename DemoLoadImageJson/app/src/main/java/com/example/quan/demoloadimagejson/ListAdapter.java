package com.example.quan.demoloadimagejson;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import static com.example.quan.demoloadimagejson.R.id.imageView;

/**
 * Created by quan on 24/06/2017.
 */

public class ListAdapter extends ArrayAdapter<SanPham> {

    public ListAdapter(Context context, int resource, List<SanPham> items) {
        super(context, resource, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;
        if (v == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            v =  inflater.inflate(R.layout.activity_dong_san_pham, null);
        }
        SanPham sp = getItem(position);
        if (sp != null) {
            // Anh xa + Gan gia tri
            TextView txt = (TextView) v.findViewById(R.id.name);
            txt.setText(sp.getName());

            ImageView imgView = (ImageView) v.findViewById(imageView);
            Picasso.with(getContext()).load(sp.getImage()).into(imgView);




        }
        return v;
    }

}