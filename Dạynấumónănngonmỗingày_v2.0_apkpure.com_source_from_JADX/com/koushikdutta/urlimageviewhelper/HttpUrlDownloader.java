package com.koushikdutta.urlimageviewhelper;

import android.content.Context;
import android.os.AsyncTask;
import com.koushikdutta.urlimageviewhelper.UrlDownloader.UrlDownloaderCallback;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper.RequestPropertiesCallback;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpHost;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;

public class HttpUrlDownloader implements UrlDownloader {
    private RequestPropertiesCallback mRequestPropertiesCallback;

    /* renamed from: com.koushikdutta.urlimageviewhelper.HttpUrlDownloader.1 */
    class C07341 extends AsyncTask<Void, Void, Void> {
        final /* synthetic */ UrlDownloaderCallback val$callback;
        final /* synthetic */ Runnable val$completion;
        final /* synthetic */ Context val$context;
        final /* synthetic */ String val$url;

        C07341(String str, Context context, UrlDownloaderCallback urlDownloaderCallback, Runnable runnable) {
            this.val$url = str;
            this.val$context = context;
            this.val$callback = urlDownloaderCallback;
            this.val$completion = runnable;
        }

        protected Void doInBackground(Void... params) {
            try {
                HttpURLConnection urlConnection;
                String thisUrl = this.val$url;
                while (true) {
                    urlConnection = (HttpURLConnection) new URL(thisUrl).openConnection();
                    urlConnection.setInstanceFollowRedirects(true);
                    if (HttpUrlDownloader.this.mRequestPropertiesCallback != null) {
                        ArrayList<NameValuePair> props = HttpUrlDownloader.this.mRequestPropertiesCallback.getHeadersForRequest(this.val$context, this.val$url);
                        if (props != null) {
                            Iterator i$ = props.iterator();
                            while (i$.hasNext()) {
                                NameValuePair pair = (NameValuePair) i$.next();
                                urlConnection.addRequestProperty(pair.getName(), pair.getValue());
                            }
                        }
                    }
                    if (urlConnection.getResponseCode() != HttpStatus.SC_MOVED_TEMPORARILY && urlConnection.getResponseCode() != HttpStatus.SC_MOVED_PERMANENTLY) {
                        break;
                    }
                    thisUrl = urlConnection.getHeaderField(HttpHeaders.LOCATION);
                }
                if (urlConnection.getResponseCode() != HttpStatus.SC_OK) {
                    UrlImageViewHelper.clog("Response Code: " + urlConnection.getResponseCode(), new Object[0]);
                } else {
                    this.val$callback.onDownloadComplete(HttpUrlDownloader.this, urlConnection.getInputStream(), null);
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(Void result) {
            this.val$completion.run();
        }
    }

    public RequestPropertiesCallback getRequestPropertiesCallback() {
        return this.mRequestPropertiesCallback;
    }

    public void setRequestPropertiesCallback(RequestPropertiesCallback callback) {
        this.mRequestPropertiesCallback = callback;
    }

    public void download(Context context, String url, String filename, UrlDownloaderCallback callback, Runnable completion) {
        UrlImageViewHelper.executeTask(new C07341(url, context, callback, completion));
    }

    public boolean allowCache() {
        return true;
    }

    public boolean canDownloadUrl(String url) {
        return url.startsWith(HttpHost.DEFAULT_SCHEME_NAME);
    }
}
