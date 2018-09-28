package com.app.tuan88291.testapp;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.helper.ItemTouchHelper.Callback;
import android.util.Base64;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import java.util.ArrayList;

public class Adapter_detail_save extends ArrayAdapter<Data_detail> {
    private Context context;
    ArrayList<Data_detail> data;
    DBhelper db;
    int layoutResourceId;

    /* renamed from: com.app.tuan88291.testapp.Adapter_detail_save.1 */
    class C02551 implements OnClickListener {
        final /* synthetic */ Data_detail val$item;

        C02551(Data_detail data_detail) {
            this.val$item = data_detail;
        }

        public void onClick(View v) {
            new Detail().show(this.val$item.getUrl(), this.val$item.getIdbv(), this.val$item.getType(), Adapter_detail_save.this.context);
        }
    }

    static class RecordHolder {
        ImageView img;
        ImageButton yt;

        RecordHolder() {
        }
    }

    public Adapter_detail_save(Context context, int layoutResourceId, ArrayList<Data_detail> data) {
        super(context, layoutResourceId, data);
        this.data = new ArrayList();
        this.context = context;
        this.data = data;
        this.layoutResourceId = layoutResourceId;
    }

    public int getCount() {
        return this.data.size();
    }

    public long getItemId(int position) {
        return (long) position;
    }

    public View getView(int index, View view, ViewGroup viewGroup) {
        RecordHolder holder;
        View row = view;
        this.db = new DBhelper(this.context);
        if (row == null) {
            row = ((Activity) this.context).getLayoutInflater().inflate(this.layoutResourceId, viewGroup, false);
            holder = new RecordHolder();
            holder.img = (ImageView) row.findViewById(C0336R.id.img);
            holder.yt = (ImageButton) row.findViewById(C0336R.id.youtube);
            row.setTag(holder);
        } else {
            holder = (RecordHolder) row.getTag();
        }
        Data_detail item = (Data_detail) this.data.get(index);
        if (this.db.checkimg(item.getIdbv()) <= 0) {
            holder.img.setImageResource(C0336R.drawable.resize);
        } else if (item.getUrl().indexOf("youtube.com") < 0) {
            Glide.with(this.context).load(Base64.decode(item.getUrl().substring(this.db.getlink(item.getIdbv()).indexOf(",") + 1), 0)).override(350, (int) Callback.DEFAULT_SWIPE_ANIMATION_DURATION).into(holder.img);
        }
        holder.yt.setOnClickListener(new C02551(item));
        return row;
    }
}
