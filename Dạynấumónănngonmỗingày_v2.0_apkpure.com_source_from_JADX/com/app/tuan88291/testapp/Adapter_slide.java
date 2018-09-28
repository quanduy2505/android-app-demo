package com.app.tuan88291.testapp;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.facebook.share.internal.ShareConstants;
import java.util.ArrayList;

public class Adapter_slide extends ArrayAdapter<Data_detail> {
    private Context context;
    ArrayList<Data_detail> data;
    int layoutResourceId;

    static class RecordHolder {
        SubsamplingScaleImageView img;

        RecordHolder() {
        }
    }

    /* renamed from: com.app.tuan88291.testapp.Adapter_slide.1 */
    class C15871 extends SimpleTarget<Bitmap> {
        final /* synthetic */ RecordHolder val$finalHolder1;

        C15871(RecordHolder recordHolder) {
            this.val$finalHolder1 = recordHolder;
        }

        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
            this.val$finalHolder1.img.setImage(ImageSource.bitmap(resource));
        }
    }

    /* renamed from: com.app.tuan88291.testapp.Adapter_slide.2 */
    class C15882 extends SimpleTarget<Bitmap> {
        final /* synthetic */ RecordHolder val$finalHolder;

        C15882(RecordHolder recordHolder) {
            this.val$finalHolder = recordHolder;
        }

        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
            this.val$finalHolder.img.setImage(ImageSource.bitmap(resource));
        }
    }

    public Adapter_slide(Context context, int layoutResourceId, ArrayList<Data_detail> data) {
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
            holder.img = (SubsamplingScaleImageView) row.findViewById(C0336R.id.img);
            row.setTag(holder);
        } else {
            holder = (RecordHolder) row.getTag();
        }
        Data_detail item = (Data_detail) this.data.get(index);
        if (item.getType().equals("img")) {
            Glide.with(this.context).load("http://cook.audition2.com/photos/" + item.getIdbv() + "/" + item.getUrl()).asBitmap().into(new C15871(holder));
        } else if (item.getType().equals(ShareConstants.WEB_DIALOG_PARAM_LINK)) {
            Glide.with(this.context).load(item.getUrl()).asBitmap().into(new C15882(holder));
        }
        return row;
    }
}
