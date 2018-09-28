package com.facebook.appevents;

import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.util.Log;
import com.facebook.FacebookSdk;
import com.facebook.appevents.internal.AppEventUtility;
import java.util.concurrent.locks.ReentrantReadWriteLock;

class AnalyticsUserIDStore {
    private static final String ANALYTICS_USER_ID_KEY = "com.facebook.appevents.AnalyticsUserIDStore.userID";
    private static final String TAG;
    private static volatile boolean initialized;
    private static ReentrantReadWriteLock lock;
    private static String userID;

    /* renamed from: com.facebook.appevents.AnalyticsUserIDStore.1 */
    static class C03811 implements Runnable {
        C03811() {
        }

        public void run() {
            AnalyticsUserIDStore.initAndWait();
        }
    }

    /* renamed from: com.facebook.appevents.AnalyticsUserIDStore.2 */
    static class C03822 implements Runnable {
        final /* synthetic */ String val$id;

        C03822(String str) {
            this.val$id = str;
        }

        public void run() {
            AnalyticsUserIDStore.lock.writeLock().lock();
            try {
                AnalyticsUserIDStore.userID = this.val$id;
                Editor editor = PreferenceManager.getDefaultSharedPreferences(FacebookSdk.getApplicationContext()).edit();
                editor.putString(AnalyticsUserIDStore.ANALYTICS_USER_ID_KEY, AnalyticsUserIDStore.userID);
                editor.apply();
            } finally {
                AnalyticsUserIDStore.lock.writeLock().unlock();
            }
        }
    }

    AnalyticsUserIDStore() {
    }

    static {
        TAG = AnalyticsUserIDStore.class.getSimpleName();
        lock = new ReentrantReadWriteLock();
        initialized = false;
    }

    public static void initStore() {
        if (!initialized) {
            AppEventsLogger.getAnalyticsExecutor().execute(new C03811());
        }
    }

    public static void setUserID(String id) {
        AppEventUtility.assertIsNotMainThread();
        if (!initialized) {
            Log.w(TAG, "initStore should have been called before calling setUserID");
            initAndWait();
        }
        AppEventsLogger.getAnalyticsExecutor().execute(new C03822(id));
    }

    public static String getUserID() {
        if (!initialized) {
            Log.w(TAG, "initStore should have been called before calling setUserID");
            initAndWait();
        }
        lock.readLock().lock();
        try {
            String str = userID;
            return str;
        } finally {
            lock.readLock().unlock();
        }
    }

    private static void initAndWait() {
        if (!initialized) {
            lock.writeLock().lock();
            try {
                if (!initialized) {
                    userID = PreferenceManager.getDefaultSharedPreferences(FacebookSdk.getApplicationContext()).getString(ANALYTICS_USER_ID_KEY, null);
                    initialized = true;
                    lock.writeLock().unlock();
                }
            } finally {
                lock.writeLock().unlock();
            }
        }
    }
}
