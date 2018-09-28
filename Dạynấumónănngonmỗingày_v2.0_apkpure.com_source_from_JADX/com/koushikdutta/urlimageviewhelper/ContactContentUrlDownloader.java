package com.koushikdutta.urlimageviewhelper;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.ContactsContract.Contacts;
import com.koushikdutta.urlimageviewhelper.UrlDownloader.UrlDownloaderCallback;

public class ContactContentUrlDownloader implements UrlDownloader {

    /* renamed from: com.koushikdutta.urlimageviewhelper.ContactContentUrlDownloader.1 */
    class C07311 extends AsyncTask<Void, Void, Void> {
        final /* synthetic */ UrlDownloaderCallback val$callback;
        final /* synthetic */ Runnable val$completion;
        final /* synthetic */ Context val$context;
        final /* synthetic */ String val$url;

        C07311(Context context, String str, UrlDownloaderCallback urlDownloaderCallback, Runnable runnable) {
            this.val$context = context;
            this.val$url = str;
            this.val$callback = urlDownloaderCallback;
            this.val$completion = runnable;
        }

        protected Void doInBackground(Void... params) {
            try {
                this.val$callback.onDownloadComplete(ContactContentUrlDownloader.this, Contacts.openContactPhotoInputStream(this.val$context.getContentResolver(), Uri.parse(this.val$url)), null);
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
        UrlImageViewHelper.executeTask(new C07311(context, url, callback, completion));
    }

    public boolean allowCache() {
        return false;
    }

    public boolean canDownloadUrl(String url) {
        return url.startsWith(Contacts.CONTENT_URI.toString());
    }
}
