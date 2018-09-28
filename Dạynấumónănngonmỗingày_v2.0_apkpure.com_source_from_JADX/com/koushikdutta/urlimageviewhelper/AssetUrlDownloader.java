package com.koushikdutta.urlimageviewhelper;

import android.content.Context;
import android.os.AsyncTask;
import com.koushikdutta.urlimageviewhelper.UrlDownloader.UrlDownloaderCallback;
import rx.android.BuildConfig;

public class AssetUrlDownloader implements UrlDownloader {

    /* renamed from: com.koushikdutta.urlimageviewhelper.AssetUrlDownloader.1 */
    class C07301 extends AsyncTask<Void, Void, Void> {
        final /* synthetic */ UrlDownloaderCallback val$callback;
        final /* synthetic */ Runnable val$completion;
        final /* synthetic */ Context val$context;
        final /* synthetic */ String val$url;

        C07301(String str, Context context, UrlDownloaderCallback urlDownloaderCallback, Runnable runnable) {
            this.val$url = str;
            this.val$context = context;
            this.val$callback = urlDownloaderCallback;
            this.val$completion = runnable;
        }

        protected Void doInBackground(Void... params) {
            try {
                this.val$callback.onDownloadComplete(AssetUrlDownloader.this, this.val$context.getAssets().open(this.val$url.replaceFirst("file:///android_asset/", BuildConfig.VERSION_NAME)), null);
            } catch (Throwable e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(Void result) {
            this.val$completion.run();
        }
    }

    public void download(Context context, String url, String filename, UrlDownloaderCallback callback, Runnable completion) {
        UrlImageViewHelper.executeTask(new C07301(url, context, callback, completion));
    }

    public boolean allowCache() {
        return false;
    }

    public boolean canDownloadUrl(String url) {
        return url.startsWith("file:///android_asset/");
    }
}
