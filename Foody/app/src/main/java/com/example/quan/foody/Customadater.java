package com.example.quan.foody;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by quan on 21/05/2017.
 */

public class Customadater extends ArrayAdapter<DocTin> {

    public Customadater(Context context, int resource, List<DocTin> items) {
        super(context, resource, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            view =  inflater.inflate(R.layout.dong_layout_listview, null);
        }
        DocTin p = getItem(position);
        if (p != null) {
            Toast.makeText(parent.getContext(), "sdfs", Toast.LENGTH_SHORT).show();
            // Anh xa + Gan gia tri
            TextView txttitle = (TextView) view.findViewById(R.id.textviewtitle);
            txttitle.setText(p.title);

            ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
            Picasso.with(getContext()).load(p.image).into(imageView);

        }
        return view;
    }

}