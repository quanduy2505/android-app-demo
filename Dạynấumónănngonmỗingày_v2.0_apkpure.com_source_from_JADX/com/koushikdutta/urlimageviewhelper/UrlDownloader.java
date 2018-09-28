package com.koushikdutta.urlimageviewhelper;

import android.content.Context;
import java.io.InputStream;

public interface UrlDownloader {

    public interface UrlDownloaderCallback {
        void onDownloadComplete(UrlDownloader urlDownloader, InputStream inputStream, String str);
    }

    boolean allowCache();

    boolean canDownloadUrl(String str);

    void download(Context context, String str, String str2, UrlDownloaderCallback urlDownloaderCallback, Runnable runnable);
}
