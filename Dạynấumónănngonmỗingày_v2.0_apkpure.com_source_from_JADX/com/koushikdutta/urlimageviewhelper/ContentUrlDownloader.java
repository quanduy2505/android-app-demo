package com.koushikdutta.urlimageviewhelper;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import com.koushikdutta.urlimageviewhelper.UrlDownloader.UrlDownloaderCallback;

public class ContentUrlDownloader implements UrlDownloader {

    /* renamed from: com.koushikdutta.urlimageviewhelper.ContentUrlDownloader.1 */
    class C07321 extends AsyncTask<Void, Void, Void> {
        final /* synthetic */ UrlDownloaderCallback val$callback;
        final /* synthetic */ Runnable val$completion;
        final /* synthetic */ Context val$context;
        final /* synthetic */ String val$url;

        C07321(Context context, String str, UrlDownloaderCallback urlDownloaderCallback, Runnable runnable) {
            this.val$context = context;
            this.val$url = str;
            this.val$callback = urlDownloaderCallback;
            this.val$completion = runnable;
        }

        protected Void doInBackground(Void... params) {
            try {
                this.val$callback.onDownloadComplete(ContentUrlDownloader.this, this.val$context.getContentResolver().openInputStream(Uri.parse(this.val$url)), null);
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
        UrlImageViewHelper.executeTask(new C07321(context, url, callback, completion));
    }

    public boolean allowCache() {
        return false;
    }

    public boolean canDownloadUrl(String url) {
        return url.startsWith("content");
    }
}
