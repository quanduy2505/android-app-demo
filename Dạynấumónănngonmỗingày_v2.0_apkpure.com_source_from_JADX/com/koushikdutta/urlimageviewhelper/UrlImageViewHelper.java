package com.koushikdutta.urlimageviewhelper;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.os.Looper;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;
import com.facebook.internal.Utility;
import com.fasterxml.jackson.core.util.MinimalPrettyPrinter;
import com.koushikdutta.urlimageviewhelper.UrlDownloader.UrlDownloaderCallback;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import org.apache.http.NameValuePair;
import rx.android.BuildConfig;

public final class UrlImageViewHelper {
    static final /* synthetic */ boolean $assertionsDisabled;
    public static final int CACHE_DURATION_FIVE_DAYS = 432000000;
    public static final int CACHE_DURATION_FOUR_DAYS = 345600000;
    public static final int CACHE_DURATION_INFINITE = Integer.MAX_VALUE;
    public static final int CACHE_DURATION_ONE_DAY = 86400000;
    public static final int CACHE_DURATION_ONE_WEEK = 604800000;
    public static final int CACHE_DURATION_SIX_DAYS = 518400000;
    public static final int CACHE_DURATION_THREE_DAYS = 259200000;
    public static final int CACHE_DURATION_TWO_DAYS = 172800000;
    private static HashSet<Bitmap> mAllCache;
    private static AssetUrlDownloader mAssetDownloader;
    private static ContactContentUrlDownloader mContactDownloader;
    private static ContentUrlDownloader mContentDownloader;
    private static LruBitmapCache mDeadCache;
    private static ArrayList<UrlDownloader> mDownloaders;
    private static FileUrlDownloader mFileDownloader;
    private static boolean mHasCleaned;
    private static HttpUrlDownloader mHttpDownloader;
    private static DrawableCache mLiveCache;
    static DisplayMetrics mMetrics;
    private static Hashtable<String, ArrayList<ImageView>> mPendingDownloads;
    private static Hashtable<ImageView, String> mPendingViews;
    private static RequestPropertiesCallback mRequestPropertiesCallback;
    static Resources mResources;
    private static boolean mUseBitmapScaling;

    /* renamed from: com.koushikdutta.urlimageviewhelper.UrlImageViewHelper.2 */
    static class C07352 implements Runnable {
        static final /* synthetic */ boolean $assertionsDisabled;
        final /* synthetic */ UrlImageViewCallback val$callback;
        final /* synthetic */ Drawable val$defaultDrawable;
        final /* synthetic */ ArrayList val$downloads;
        final /* synthetic */ ImageView val$imageView;
        final /* synthetic */ Loader val$loader;
        final /* synthetic */ String val$url;

        static {
            $assertionsDisabled = !UrlImageViewHelper.class.desiredAssertionStatus() ? true : UrlImageViewHelper.$assertionsDisabled;
        }

        C07352(Loader loader, String str, Drawable drawable, UrlImageViewCallback urlImageViewCallback, ImageView imageView, ArrayList arrayList) {
            this.val$loader = loader;
            this.val$url = str;
            this.val$defaultDrawable = drawable;
            this.val$callback = urlImageViewCallback;
            this.val$imageView = imageView;
            this.val$downloads = arrayList;
        }

        public void run() {
            if ($assertionsDisabled || Looper.myLooper().equals(Looper.getMainLooper())) {
                Bitmap bitmap = this.val$loader.result;
                Drawable usableResult = null;
                if (bitmap != null) {
                    usableResult = new ZombieDrawable(this.val$url, UrlImageViewHelper.mResources, bitmap);
                }
                if (usableResult == null) {
                    UrlImageViewHelper.clog("No usable result, defaulting " + this.val$url, new Object[0]);
                    usableResult = this.val$defaultDrawable;
                    UrlImageViewHelper.mLiveCache.put(this.val$url, usableResult);
                }
                UrlImageViewHelper.mPendingDownloads.remove(this.val$url);
                if (this.val$callback != null && this.val$imageView == null) {
                    this.val$callback.onLoaded(null, this.val$loader.result, this.val$url, UrlImageViewHelper.$assertionsDisabled);
                }
                int waitingCount = 0;
                Iterator i$ = this.val$downloads.iterator();
                while (i$.hasNext()) {
                    ImageView iv = (ImageView) i$.next();
                    String pendingUrl = (String) UrlImageViewHelper.mPendingViews.get(iv);
                    if (this.val$url.equals(pendingUrl)) {
                        waitingCount++;
                        UrlImageViewHelper.mPendingViews.remove(iv);
                        if (usableResult != null) {
                            iv.setImageDrawable(usableResult);
                        }
                        if (this.val$callback != null && iv == this.val$imageView) {
                            this.val$callback.onLoaded(iv, this.val$loader.result, this.val$url, UrlImageViewHelper.$assertionsDisabled);
                        }
                    } else {
                        UrlImageViewHelper.clog("Ignoring out of date request to update view for " + this.val$url + MinimalPrettyPrinter.DEFAULT_ROOT_VALUE_SEPARATOR + pendingUrl + MinimalPrettyPrinter.DEFAULT_ROOT_VALUE_SEPARATOR + iv, new Object[0]);
                    }
                }
                UrlImageViewHelper.clog("Populated: " + waitingCount, new Object[0]);
                return;
            }
            throw new AssertionError();
        }
    }

    /* renamed from: com.koushikdutta.urlimageviewhelper.UrlImageViewHelper.3 */
    static class C07363 extends AsyncTask<Void, Void, Void> {
        final /* synthetic */ Runnable val$completion;
        final /* synthetic */ String val$filename;
        final /* synthetic */ Loader val$loader;

        C07363(Loader loader, String str, Runnable runnable) {
            this.val$loader = loader;
            this.val$filename = str;
            this.val$completion = runnable;
        }

        protected Void doInBackground(Void... params) {
            this.val$loader.onDownloadComplete(null, null, this.val$filename);
            return null;
        }

        protected void onPostExecute(Void result) {
            this.val$completion.run();
        }
    }

    public interface RequestPropertiesCallback {
        ArrayList<NameValuePair> getHeadersForRequest(Context context, String str);
    }

    private static class ZombieDrawable extends BitmapDrawable {
        Brains mBrains;
        String mUrl;

        private static class Brains {
            boolean mHeadshot;
            int mRefCounter;

            private Brains() {
            }
        }

        public ZombieDrawable(String url, Resources resources, Bitmap bitmap) {
            this(url, resources, bitmap, new Brains());
        }

        private ZombieDrawable(String url, Resources resources, Bitmap bitmap, Brains brains) {
            super(resources, bitmap);
            this.mUrl = url;
            this.mBrains = brains;
            UrlImageViewHelper.mAllCache.add(bitmap);
            UrlImageViewHelper.mDeadCache.remove(url);
            UrlImageViewHelper.mLiveCache.put(url, this);
            Brains brains2 = this.mBrains;
            brains2.mRefCounter++;
        }

        public ZombieDrawable clone(Resources resources) {
            return new ZombieDrawable(this.mUrl, resources, getBitmap(), this.mBrains);
        }

        protected void finalize() throws Throwable {
            super.finalize();
            Brains brains = this.mBrains;
            brains.mRefCounter--;
            if (this.mBrains.mRefCounter == 0) {
                if (!this.mBrains.mHeadshot) {
                    UrlImageViewHelper.mDeadCache.put(this.mUrl, getBitmap());
                }
                UrlImageViewHelper.mAllCache.remove(getBitmap());
                UrlImageViewHelper.mLiveCache.remove(this.mUrl);
                UrlImageViewHelper.clog("Zombie GC event " + this.mUrl, new Object[0]);
            }
        }

        public void headshot() {
            UrlImageViewHelper.clog("BOOM! Headshot: " + this.mUrl, new Object[0]);
            this.mBrains.mHeadshot = true;
            UrlImageViewHelper.mLiveCache.remove(this.mUrl);
            UrlImageViewHelper.mAllCache.remove(getBitmap());
        }
    }

    private static abstract class Loader implements UrlDownloaderCallback {
        Bitmap result;

        private Loader() {
        }
    }

    /* renamed from: com.koushikdutta.urlimageviewhelper.UrlImageViewHelper.1 */
    static class C13391 extends Loader {
        static final /* synthetic */ boolean $assertionsDisabled;
        final /* synthetic */ Context val$context;
        final /* synthetic */ String val$filename;
        final /* synthetic */ int val$targetHeight;
        final /* synthetic */ int val$targetWidth;
        final /* synthetic */ String val$url;

        static {
            $assertionsDisabled = !UrlImageViewHelper.class.desiredAssertionStatus() ? true : UrlImageViewHelper.$assertionsDisabled;
        }

        C13391(String str, Context context, String str2, int i, int i2) {
            this.val$filename = str;
            this.val$context = context;
            this.val$url = str2;
            this.val$targetWidth = i;
            this.val$targetHeight = i2;
            super();
        }

        public void onDownloadComplete(UrlDownloader downloader, InputStream in, String existingFilename) {
            Throwable th;
            try {
                if (!$assertionsDisabled && in != null && existingFilename != null) {
                    throw new AssertionError();
                } else if (in != null || existingFilename != null) {
                    String targetFilename = this.val$filename;
                    if (in != null) {
                        InputStream in2 = new BufferedInputStream(in, Utility.DEFAULT_STREAM_BUFFER_SIZE);
                        try {
                            OutputStream fout = new BufferedOutputStream(new FileOutputStream(this.val$filename), Utility.DEFAULT_STREAM_BUFFER_SIZE);
                            UrlImageViewHelper.copyStream(in2, fout);
                            fout.close();
                            in = in2;
                        } catch (Exception e) {
                            in = in2;
                            try {
                                new File(this.val$filename).delete();
                                if (downloader != null) {
                                    return;
                                }
                            } catch (Throwable th2) {
                                th = th2;
                                if (!(downloader == null || downloader.allowCache())) {
                                    new File(this.val$filename).delete();
                                }
                                throw th;
                            }
                        } catch (Throwable th3) {
                            th = th3;
                            in = in2;
                            new File(this.val$filename).delete();
                            throw th;
                        }
                    }
                    targetFilename = existingFilename;
                    this.result = UrlImageViewHelper.loadBitmapFromStream(this.val$context, this.val$url, targetFilename, this.val$targetWidth, this.val$targetHeight);
                    if (downloader != null && !downloader.allowCache()) {
                        new File(this.val$filename).delete();
                    }
                } else if (downloader != null && !downloader.allowCache()) {
                    new File(this.val$filename).delete();
                }
            } catch (Exception e2) {
                new File(this.val$filename).delete();
                if (downloader != null && !downloader.allowCache()) {
                    new File(this.val$filename).delete();
                }
            }
        }
    }

    static {
        boolean z;
        if (UrlImageViewHelper.class.desiredAssertionStatus()) {
            z = $assertionsDisabled;
        } else {
            z = true;
        }
        $assertionsDisabled = z;
        mUseBitmapScaling = true;
        mHasCleaned = $assertionsDisabled;
        mHttpDownloader = new HttpUrlDownloader();
        mContentDownloader = new ContentUrlDownloader();
        mContactDownloader = new ContactContentUrlDownloader();
        mAssetDownloader = new AssetUrlDownloader();
        mFileDownloader = new FileUrlDownloader();
        mDownloaders = new ArrayList();
        mDownloaders.add(mHttpDownloader);
        mDownloaders.add(mContactDownloader);
        mDownloaders.add(mContentDownloader);
        mDownloaders.add(mAssetDownloader);
        mDownloaders.add(mFileDownloader);
        mLiveCache = DrawableCache.getInstance();
        mAllCache = new HashSet();
        mPendingViews = new Hashtable();
        mPendingDownloads = new Hashtable();
    }

    static void clog(String format, Object... args) {
        String log;
        if (args.length == 0) {
            log = format;
        } else {
            log = String.format(format, args);
        }
    }

    public static int copyStream(InputStream input, OutputStream output) throws IOException {
        byte[] stuff = new byte[Utility.DEFAULT_STREAM_BUFFER_SIZE];
        int total = 0;
        while (true) {
            int read = input.read(stuff);
            if (read == -1) {
                return total;
            }
            output.write(stuff, 0, read);
            total += read;
        }
    }

    private static void prepareResources(Context context) {
        if (mMetrics == null) {
            mMetrics = new DisplayMetrics();
            ((WindowManager) context.getSystemService("window")).getDefaultDisplay().getMetrics(mMetrics);
            mResources = new Resources(context.getAssets(), mMetrics, context.getResources().getConfiguration());
        }
    }

    public static void setUseBitmapScaling(boolean useBitmapScaling) {
        mUseBitmapScaling = useBitmapScaling;
    }

    public static boolean getUseBitmapScaling() {
        return mUseBitmapScaling;
    }

    private static Bitmap loadBitmapFromStream(Context context, String url, String filename, int targetWidth, int targetHeight) {
        Bitmap bitmap;
        Throwable th;
        prepareResources(context);
        InputStream stream = null;
        clog("Decoding: " + url + MinimalPrettyPrinter.DEFAULT_ROOT_VALUE_SEPARATOR + filename, new Object[0]);
        Options o = null;
        try {
            InputStream stream2;
            if (mUseBitmapScaling) {
                Options o2 = new Options();
                try {
                    o2.inJustDecodeBounds = true;
                    stream2 = new BufferedInputStream(new FileInputStream(filename), Utility.DEFAULT_STREAM_BUFFER_SIZE);
                } catch (IOException e) {
                    o = o2;
                    bitmap = null;
                    if (stream != null) {
                        try {
                            stream.close();
                        } catch (IOException e2) {
                            Log.w(Constants.LOGTAG, "Failed to close FileInputStream", e2);
                        }
                    }
                    return bitmap;
                } catch (Throwable th2) {
                    th = th2;
                    o = o2;
                    if (stream != null) {
                        try {
                            stream.close();
                        } catch (IOException e22) {
                            Log.w(Constants.LOGTAG, "Failed to close FileInputStream", e22);
                        }
                    }
                    throw th;
                }
                try {
                    BitmapFactory.decodeStream(stream2, null, o2);
                    stream2.close();
                    int scale = 0;
                    while (true) {
                        if ((o2.outWidth >> scale) <= targetWidth && (o2.outHeight >> scale) <= targetHeight) {
                            break;
                        }
                        scale++;
                    }
                    o = new Options();
                    try {
                        o.inSampleSize = 1 << scale;
                    } catch (IOException e3) {
                        stream = stream2;
                        bitmap = null;
                        if (stream != null) {
                            stream.close();
                        }
                        return bitmap;
                    } catch (Throwable th3) {
                        th = th3;
                        stream = stream2;
                        if (stream != null) {
                            stream.close();
                        }
                        throw th;
                    }
                } catch (IOException e4) {
                    o = o2;
                    stream = stream2;
                    bitmap = null;
                    if (stream != null) {
                        stream.close();
                    }
                    return bitmap;
                } catch (Throwable th4) {
                    th = th4;
                    o = o2;
                    stream = stream2;
                    if (stream != null) {
                        stream.close();
                    }
                    throw th;
                }
            }
            stream2 = null;
            stream = new BufferedInputStream(new FileInputStream(filename), Utility.DEFAULT_STREAM_BUFFER_SIZE);
            bitmap = BitmapFactory.decodeStream(stream, null, o);
            clog(String.format("Loaded bitmap (%dx%d).", new Object[]{Integer.valueOf(bitmap.getWidth()), Integer.valueOf(bitmap.getHeight())}), new Object[0]);
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e222) {
                    Log.w(Constants.LOGTAG, "Failed to close FileInputStream", e222);
                }
            }
        } catch (IOException e5) {
            bitmap = null;
            if (stream != null) {
                stream.close();
            }
            return bitmap;
        } catch (Throwable th5) {
            th = th5;
            if (stream != null) {
                stream.close();
            }
            throw th;
        }
        return bitmap;
    }

    public static void setUrlDrawable(ImageView imageView, String url, int defaultResource) {
        setUrlDrawable(imageView.getContext(), imageView, url, defaultResource, 259200000);
    }

    public static void setUrlDrawable(ImageView imageView, String url) {
        setUrlDrawable(imageView.getContext(), imageView, url, null, 259200000, null);
    }

    public static void loadUrlDrawable(Context context, String url) {
        setUrlDrawable(context, null, url, null, 259200000, null);
    }

    public static void setUrlDrawable(ImageView imageView, String url, Drawable defaultDrawable) {
        setUrlDrawable(imageView.getContext(), imageView, url, defaultDrawable, 259200000, null);
    }

    public static void setUrlDrawable(ImageView imageView, String url, int defaultResource, long cacheDurationMs) {
        setUrlDrawable(imageView.getContext(), imageView, url, defaultResource, cacheDurationMs);
    }

    public static void loadUrlDrawable(Context context, String url, long cacheDurationMs) {
        setUrlDrawable(context, null, url, null, cacheDurationMs, null);
    }

    public static void setUrlDrawable(ImageView imageView, String url, Drawable defaultDrawable, long cacheDurationMs) {
        setUrlDrawable(imageView.getContext(), imageView, url, defaultDrawable, cacheDurationMs, null);
    }

    private static void setUrlDrawable(Context context, ImageView imageView, String url, int defaultResource, long cacheDurationMs) {
        Drawable d = null;
        if (defaultResource != 0) {
            d = imageView.getResources().getDrawable(defaultResource);
        }
        setUrlDrawable(context, imageView, url, d, cacheDurationMs, null);
    }

    public static void setUrlDrawable(ImageView imageView, String url, int defaultResource, UrlImageViewCallback callback) {
        setUrlDrawable(imageView.getContext(), imageView, url, defaultResource, 259200000, callback);
    }

    public static void setUrlDrawable(ImageView imageView, String url, UrlImageViewCallback callback) {
        setUrlDrawable(imageView.getContext(), imageView, url, null, 259200000, callback);
    }

    public static void loadUrlDrawable(Context context, String url, UrlImageViewCallback callback) {
        setUrlDrawable(context, null, url, null, 259200000, callback);
    }

    public static void setUrlDrawable(ImageView imageView, String url, Drawable defaultDrawable, UrlImageViewCallback callback) {
        setUrlDrawable(imageView.getContext(), imageView, url, defaultDrawable, 259200000, callback);
    }

    public static void setUrlDrawable(ImageView imageView, String url, int defaultResource, long cacheDurationMs, UrlImageViewCallback callback) {
        setUrlDrawable(imageView.getContext(), imageView, url, defaultResource, cacheDurationMs, callback);
    }

    public static void loadUrlDrawable(Context context, String url, long cacheDurationMs, UrlImageViewCallback callback) {
        setUrlDrawable(context, null, url, null, cacheDurationMs, callback);
    }

    public static void setUrlDrawable(ImageView imageView, String url, Drawable defaultDrawable, long cacheDurationMs, UrlImageViewCallback callback) {
        setUrlDrawable(imageView.getContext(), imageView, url, defaultDrawable, cacheDurationMs, callback);
    }

    private static void setUrlDrawable(Context context, ImageView imageView, String url, int defaultResource, long cacheDurationMs, UrlImageViewCallback callback) {
        Drawable d = null;
        if (defaultResource != 0) {
            d = imageView.getResources().getDrawable(defaultResource);
        }
        setUrlDrawable(context, imageView, url, d, cacheDurationMs, callback);
    }

    private static boolean isNullOrEmpty(CharSequence s) {
        return (s == null || s.equals(BuildConfig.VERSION_NAME) || s.equals("null") || s.equals("NULL")) ? true : $assertionsDisabled;
    }

    public static String getFilenameForUrl(String url) {
        return url.hashCode() + ".urlimage";
    }

    public static void cleanup(Context context, long age) {
        if (!mHasCleaned) {
            mHasCleaned = true;
            try {
                String[] files = context.getFilesDir().list();
                if (files != null) {
                    for (String file : files) {
                        if (file.endsWith(".urlimage")) {
                            File f = new File(context.getFilesDir().getAbsolutePath() + '/' + file);
                            if (System.currentTimeMillis() > f.lastModified() + age) {
                                f.delete();
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void cleanup(Context context) {
        cleanup(context, 604800000);
    }

    private static boolean checkCacheDuration(File file, long cacheDurationMs) {
        return (cacheDurationMs == 2147483647L || System.currentTimeMillis() < file.lastModified() + cacheDurationMs) ? true : $assertionsDisabled;
    }

    public static Bitmap getCachedBitmap(String url) {
        if (url == null) {
            return null;
        }
        Bitmap ret = null;
        if (mDeadCache != null) {
            ret = (Bitmap) mDeadCache.get(url);
        }
        if (ret != null) {
            return ret;
        }
        if (mLiveCache != null) {
            Drawable drawable = (Drawable) mLiveCache.get(url);
            if (drawable instanceof ZombieDrawable) {
                return ((ZombieDrawable) drawable).getBitmap();
            }
        }
        return null;
    }

    private static void setUrlDrawable(Context context, ImageView imageView, String url, Drawable defaultDrawable, long cacheDurationMs, UrlImageViewCallback callback) {
        if ($assertionsDisabled || Looper.getMainLooper().getThread() == Thread.currentThread()) {
            cleanup(context);
            if (!isNullOrEmpty(url)) {
                if (mMetrics == null) {
                    prepareResources(context);
                }
                int tw = mMetrics.widthPixels;
                int th = mMetrics.heightPixels;
                String filename = context.getFileStreamPath(getFilenameForUrl(url)).getAbsolutePath();
                File file = new File(filename);
                if (mDeadCache == null) {
                    mDeadCache = new LruBitmapCache(getHeapSize(context) / 8);
                }
                Drawable drawable = null;
                Bitmap bitmap = (Bitmap) mDeadCache.remove(url);
                if (bitmap != null) {
                    clog("zombie load: " + url, new Object[0]);
                } else {
                    drawable = (Drawable) mLiveCache.get(url);
                }
                if (!(drawable == null && bitmap == null)) {
                    clog("Cache hit on: " + url, new Object[0]);
                    if (!file.exists() || checkCacheDuration(file, cacheDurationMs)) {
                        clog("Using cached: " + url, new Object[0]);
                    } else {
                        clog("Cache hit, but file is stale. Forcing reload: " + url, new Object[0]);
                        if (drawable != null && (drawable instanceof ZombieDrawable)) {
                            ((ZombieDrawable) drawable).headshot();
                        }
                        drawable = null;
                        bitmap = null;
                    }
                }
                if (drawable == null && bitmap == null) {
                    clog("Waiting for " + url + MinimalPrettyPrinter.DEFAULT_ROOT_VALUE_SEPARATOR + imageView, new Object[0]);
                    if (imageView != null) {
                        imageView.setImageDrawable(defaultDrawable);
                        mPendingViews.put(imageView, url);
                    }
                    ArrayList<ImageView> currentDownload = (ArrayList) mPendingDownloads.get(url);
                    if (currentDownload == null) {
                        int targetWidth;
                        int targetHeight;
                        ArrayList<ImageView> downloads = new ArrayList();
                        if (imageView != null) {
                            downloads.add(imageView);
                        }
                        mPendingDownloads.put(url, downloads);
                        if (tw <= 0) {
                            targetWidth = CACHE_DURATION_INFINITE;
                        } else {
                            targetWidth = tw;
                        }
                        if (th <= 0) {
                            targetHeight = CACHE_DURATION_INFINITE;
                        } else {
                            targetHeight = th;
                        }
                        Loader loader = new C13391(filename, context, url, targetWidth, targetHeight);
                        Runnable completion = new C07352(loader, url, defaultDrawable, callback, imageView, downloads);
                        if (file.exists()) {
                            try {
                                if (checkCacheDuration(file, cacheDurationMs)) {
                                    clog("File Cache hit on: " + url + ". " + (System.currentTimeMillis() - file.lastModified()) + "ms old.", new Object[0]);
                                    executeTask(new C07363(loader, filename, completion));
                                    return;
                                }
                                clog("File cache has expired. Refreshing.", new Object[0]);
                            } catch (Exception e) {
                            }
                        }
                        Iterator i$ = mDownloaders.iterator();
                        while (i$.hasNext()) {
                            UrlDownloader downloader = (UrlDownloader) i$.next();
                            if (downloader.canDownloadUrl(url)) {
                                downloader.download(context, url, filename, loader, completion);
                                return;
                            }
                        }
                        imageView.setImageDrawable(defaultDrawable);
                        return;
                    } else if (imageView != null) {
                        currentDownload.add(imageView);
                        return;
                    } else {
                        return;
                    }
                }
                if (imageView != null) {
                    mPendingViews.remove(imageView);
                    if (drawable instanceof ZombieDrawable) {
                        ZombieDrawable zombieDrawable = (ZombieDrawable) drawable;
                        drawable = r25.clone(mResources);
                    } else if (bitmap != null) {
                        Drawable zombieDrawable2 = new ZombieDrawable(url, mResources, bitmap);
                    }
                    imageView.setImageDrawable(drawable);
                }
                if (callback != null) {
                    if (bitmap == null && (drawable instanceof ZombieDrawable)) {
                        bitmap = ((ZombieDrawable) drawable).getBitmap();
                    }
                    callback.onLoaded(imageView, bitmap, url, true);
                    return;
                }
                return;
            } else if (imageView != null) {
                mPendingViews.remove(imageView);
                imageView.setImageDrawable(defaultDrawable);
                return;
            } else {
                return;
            }
        }
        throw new AssertionError("setUrlDrawable and loadUrlDrawable should only be called from the main thread.");
    }

    public static ArrayList<UrlDownloader> getDownloaders() {
        return mDownloaders;
    }

    public static RequestPropertiesCallback getRequestPropertiesCallback() {
        return mRequestPropertiesCallback;
    }

    public static void setRequestPropertiesCallback(RequestPropertiesCallback callback) {
        mRequestPropertiesCallback = callback;
    }

    private static int getHeapSize(Context context) {
        return (((ActivityManager) context.getSystemService("activity")).getMemoryClass() * AccessibilityNodeInfoCompat.ACTION_NEXT_HTML_ELEMENT) * AccessibilityNodeInfoCompat.ACTION_NEXT_HTML_ELEMENT;
    }

    public static Bitmap remove(String url) {
        new File(getFilenameForUrl(url)).delete();
        Drawable drawable = (Drawable) mLiveCache.remove(url);
        if (!(drawable instanceof ZombieDrawable)) {
            return null;
        }
        ZombieDrawable zombie = (ZombieDrawable) drawable;
        Bitmap ret = zombie.getBitmap();
        zombie.headshot();
        return ret;
    }

    static void executeTask(AsyncTask<Void, Void, Void> task) {
        if (VERSION.SDK_INT < 11) {
            task.execute(new Void[0]);
        } else {
            executeTaskHoneycomb(task);
        }
    }

    @TargetApi(11)
    private static void executeTaskHoneycomb(AsyncTask<Void, Void, Void> task) {
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
    }

    public static int getPendingDownloads() {
        return mPendingDownloads.size();
    }
}
