package com.app.tuan88291.testapp;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import java.io.ByteArrayOutputStream;
import me.wangyuwei.loadingview.C0801R;

public class Show_slide extends AppCompatActivity {
    private ImageView anh;
    private TextView im;
    private String tim;

    /* renamed from: com.app.tuan88291.testapp.Show_slide.1 */
    class C15911 extends SimpleTarget<Bitmap> {
        C15911() {
        }

        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
            Show_slide.this.tim = Show_slide.this.getStringImage(resource);
            Glide.with(Show_slide.this).load(Base64.decode(Show_slide.this.tim.substring(Show_slide.this.tim.indexOf(",") + 1), 0)).crossFade().fitCenter().into(Show_slide.this.anh);
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(1);
        super.onCreate(savedInstanceState);
        getWindow().setFlags(AccessibilityNodeInfoCompat.ACTION_NEXT_HTML_ELEMENT, AccessibilityNodeInfoCompat.ACTION_NEXT_HTML_ELEMENT);
        setContentView((int) C0336R.layout.slide_layout);
        this.anh = (ImageView) findViewById(C0801R.id.image);
        this.im = (TextView) findViewById(C0336R.id.im);
        Glide.with((FragmentActivity) this).load("http://hinhanhdepvip.com/wp-content/uploads/2016/07/avatar-cap-anime-dep-nhat.jpg").asBitmap().into(new C15911());
    }

    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(CompressFormat.JPEG, 5, baos);
        return Base64.encodeToString(baos.toByteArray(), 0);
    }
}
