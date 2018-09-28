package com.koushikdutta.urlimageviewhelper;

import android.content.Context;
import android.os.AsyncTask;
import com.koushikdutta.urlimageviewhelper.UrlDownloader.UrlDownloaderCallback;
import java.io.File;
import java.net.URI;

public class FileUrlDownloader implements UrlDownloader {

    /* renamed from: com.koushikdutta.urlimageviewhelper.FileUrlDownloader.1 */
    class C07331 extends AsyncTask<Void, Void, Void> {
        final /* synthetic */ UrlDownloaderCallback val$callback;
        final /* synthetic */ Runnable val$completion;
        final /* synthetic */ String val$url;

        C07331(UrlDownloaderCallback urlDownloaderCallback, String str, Runnable runnable) {
            this.val$callback = urlDownloaderCallback;
            this.val$url = str;
            this.val$completion = runnable;
        }

        protected Void doInBackground(Void... params) {
            try {
                this.val$callback.onDownloadComplete(FileUrlDownloader.this, null, new File(new URI(this.val$url)).getAbsolutePath());
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
        UrlImageViewHelper.executeTask(new C07331(callback, url, completion));
    }

    public boolean allowCache() {
        return false;
    }

    public boolean canDownloadUrl(String url) {
        return url.startsWith("file:/");
    }
}
