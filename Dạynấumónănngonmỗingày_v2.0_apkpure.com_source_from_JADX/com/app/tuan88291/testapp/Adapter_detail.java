package com.app.tuan88291.testapp;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.helper.ItemTouchHelper.Callback;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.facebook.internal.AnalyticsEvents;
import com.facebook.share.internal.ShareConstants;
import java.util.ArrayList;
import rx.android.BuildConfig;

public class Adapter_detail extends ArrayAdapter<Data_detail> {
    private Context context;
    ArrayList<Data_detail> data;
    int layoutResourceId;

    /* renamed from: com.app.tuan88291.testapp.Adapter_detail.1 */
    class C02541 implements OnClickListener {
        final /* synthetic */ Data_detail val$item;

        C02541(Data_detail data_detail) {
            this.val$item = data_detail;
        }

        public void onClick(View v) {
            new Detail().show(this.val$item.getUrl(), this.val$item.getIdbv(), this.val$item.getType(), Adapter_detail.this.context);
        }
    }

    static class RecordHolder {
        ImageView img;
        ImageButton yt;

        RecordHolder() {
        }
    }

    public Adapter_detail(Context context, int layoutResourceId, ArrayList<Data_detail> data) {
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
        if (item.getType().equals("img")) {
            Glide.with(this.context).load("http://cook.audition2.com/photos/" + item.getIdbv() + "/" + item.getUrl()).override(350, (int) Callback.DEFAULT_SWIPE_ANIMATION_DURATION).placeholder((int) C0336R.drawable.resize).into(holder.img);
        } else if (item.getType().equals(ShareConstants.WEB_DIALOG_PARAM_LINK)) {
            Glide.with(this.context).load(item.getUrl()).placeholder((int) C0336R.drawable.resize).override(350, (int) Callback.DEFAULT_SWIPE_ANIMATION_DURATION).into(holder.img);
        } else if (item.getType().equals(AnalyticsEvents.PARAMETER_SHARE_DIALOG_CONTENT_VIDEO)) {
            holder.yt.setVisibility(0);
            Glide.with(this.context).load("https://i.ytimg.com/vi/" + item.getUrl().replace(item.getUrl().split("[?v=]")[0] + "?v=", BuildConfig.VERSION_NAME).replace("&feature=youtu.be", BuildConfig.VERSION_NAME) + "/sddefault.jpg").placeholder((int) C0336R.drawable.resize).override(350, (int) Callback.DEFAULT_SWIPE_ANIMATION_DURATION).into(holder.img);
        }
        holder.yt.setOnClickListener(new C02541(item));
        return row;
    }
}
