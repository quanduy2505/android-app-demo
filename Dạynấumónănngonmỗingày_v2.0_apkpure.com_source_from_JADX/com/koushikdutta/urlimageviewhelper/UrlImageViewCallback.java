package com.koushikdutta.urlimageviewhelper;

import android.graphics.Bitmap;
import android.widget.ImageView;

public interface UrlImageViewCallback {
    void onLoaded(ImageView imageView, Bitmap bitmap, String str, boolean z);
}
