package com.facebook.internal;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Looper;
import android.os.Parcel;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;
import com.facebook.FacebookException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import java.lang.reflect.Method;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicBoolean;

public class AttributionIdentifiers {
    private static final String ANDROID_ID_COLUMN_NAME = "androidid";
    private static final String ATTRIBUTION_ID_COLUMN_NAME = "aid";
    private static final String ATTRIBUTION_ID_CONTENT_PROVIDER = "com.facebook.katana.provider.AttributionIdProvider";
    private static final String ATTRIBUTION_ID_CONTENT_PROVIDER_WAKIZASHI = "com.facebook.wakizashi.provider.AttributionIdProvider";
    private static final int CONNECTION_RESULT_SUCCESS = 0;
    private static final long IDENTIFIER_REFRESH_INTERVAL_MILLIS = 3600000;
    private static final String LIMIT_TRACKING_COLUMN_NAME = "limit_tracking";
    private static final String TAG;
    private static AttributionIdentifiers recentlyFetchedIdentifiers;
    private String androidAdvertiserId;
    private String androidInstallerPackage;
    private String attributionId;
    private long fetchTime;
    private boolean limitTracking;

    private static final class GoogleAdInfo implements IInterface {
        private static final int FIRST_TRANSACTION_CODE = 1;
        private static final int SECOND_TRANSACTION_CODE = 2;
        private IBinder binder;

        GoogleAdInfo(IBinder binder) {
            this.binder = binder;
        }

        public IBinder asBinder() {
            return this.binder;
        }

        public String getAdvertiserId() throws RemoteException {
            Parcel data = Parcel.obtain();
            Parcel reply = Parcel.obtain();
            try {
                data.writeInterfaceToken("com.google.android.gms.ads.identifier.internal.IAdvertisingIdService");
                this.binder.transact(FIRST_TRANSACTION_CODE, data, reply, AttributionIdentifiers.CONNECTION_RESULT_SUCCESS);
                reply.readException();
                String id = reply.readString();
                return id;
            } finally {
                reply.recycle();
                data.recycle();
            }
        }

        public boolean isTrackingLimited() throws RemoteException {
            boolean limitAdTracking = true;
            Parcel data = Parcel.obtain();
            Parcel reply = Parcel.obtain();
            try {
                data.writeInterfaceToken("com.google.android.gms.ads.identifier.internal.IAdvertisingIdService");
                data.writeInt(FIRST_TRANSACTION_CODE);
                this.binder.transact(SECOND_TRANSACTION_CODE, data, reply, AttributionIdentifiers.CONNECTION_RESULT_SUCCESS);
                reply.readException();
                if (reply.readInt() == 0) {
                    limitAdTracking = false;
                }
                reply.recycle();
                data.recycle();
                return limitAdTracking;
            } catch (Throwable th) {
                reply.recycle();
                data.recycle();
            }
        }
    }

    private static final class GoogleAdServiceConnection implements ServiceConnection {
        private AtomicBoolean consumed;
        private final BlockingQueue<IBinder> queue;

        private GoogleAdServiceConnection() {
            this.consumed = new AtomicBoolean(false);
            this.queue = new LinkedBlockingDeque();
        }

        public void onServiceConnected(ComponentName name, IBinder service) {
            try {
                this.queue.put(service);
            } catch (InterruptedException e) {
            }
        }

        public void onServiceDisconnected(ComponentName name) {
        }

        public IBinder getBinder() throws InterruptedException {
            if (!this.consumed.compareAndSet(true, true)) {
                return (IBinder) this.queue.take();
            }
            throw new IllegalStateException("Binder already consumed");
        }
    }

    static {
        TAG = AttributionIdentifiers.class.getCanonicalName();
    }

    private static AttributionIdentifiers getAndroidId(Context context) {
        AttributionIdentifiers identifiers = getAndroidIdViaReflection(context);
        if (identifiers != null) {
            return identifiers;
        }
        identifiers = getAndroidIdViaService(context);
        if (identifiers == null) {
            return new AttributionIdentifiers();
        }
        return identifiers;
    }

    private static AttributionIdentifiers getAndroidIdViaReflection(Context context) {
        try {
            if (Looper.myLooper() == Looper.getMainLooper()) {
                throw new FacebookException("getAndroidId cannot be called on the main thread.");
            }
            Method isGooglePlayServicesAvailable = Utility.getMethodQuietly("com.google.android.gms.common.GooglePlayServicesUtil", "isGooglePlayServicesAvailable", Context.class);
            if (isGooglePlayServicesAvailable == null) {
                return null;
            }
            Object connectionResult = Utility.invokeMethodQuietly(null, isGooglePlayServicesAvailable, context);
            if (!(connectionResult instanceof Integer) || ((Integer) connectionResult).intValue() != 0) {
                return null;
            }
            Method getAdvertisingIdInfo = Utility.getMethodQuietly("com.google.android.gms.ads.identifier.AdvertisingIdClient", "getAdvertisingIdInfo", Context.class);
            if (getAdvertisingIdInfo == null) {
                return null;
            }
            Object advertisingInfo = Utility.invokeMethodQuietly(null, getAdvertisingIdInfo, context);
            if (advertisingInfo == null) {
                return null;
            }
            Method getId = Utility.getMethodQuietly(advertisingInfo.getClass(), "getId", new Class[CONNECTION_RESULT_SUCCESS]);
            Method isLimitAdTrackingEnabled = Utility.getMethodQuietly(advertisingInfo.getClass(), "isLimitAdTrackingEnabled", new Class[CONNECTION_RESULT_SUCCESS]);
            if (getId == null || isLimitAdTrackingEnabled == null) {
                return null;
            }
            AttributionIdentifiers identifiers = new AttributionIdentifiers();
            identifiers.androidAdvertiserId = (String) Utility.invokeMethodQuietly(advertisingInfo, getId, new Object[CONNECTION_RESULT_SUCCESS]);
            identifiers.limitTracking = ((Boolean) Utility.invokeMethodQuietly(advertisingInfo, isLimitAdTrackingEnabled, new Object[CONNECTION_RESULT_SUCCESS])).booleanValue();
            return identifiers;
        } catch (Exception e) {
            Utility.logd("android_id", e);
            return null;
        }
    }

    private static AttributionIdentifiers getAndroidIdViaService(Context context) {
        GoogleAdServiceConnection connection = new GoogleAdServiceConnection();
        Intent intent = new Intent("com.google.android.gms.ads.identifier.service.START");
        intent.setPackage(GooglePlayServicesUtil.GOOGLE_PLAY_SERVICES_PACKAGE);
        if (context.bindService(intent, connection, 1)) {
            try {
                GoogleAdInfo adInfo = new GoogleAdInfo(connection.getBinder());
                AttributionIdentifiers identifiers = new AttributionIdentifiers();
                identifiers.androidAdvertiserId = adInfo.getAdvertiserId();
                identifiers.limitTracking = adInfo.isTrackingLimited();
                return identifiers;
            } catch (Exception exception) {
                Utility.logd("android_id", exception);
            } finally {
                context.unbindService(connection);
            }
        }
        return null;
    }

    public static AttributionIdentifiers getAttributionIdentifiers(Context context) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            Log.e(TAG, "getAttributionIdentifiers should not be called from the main thread");
        }
        if (recentlyFetchedIdentifiers != null && System.currentTimeMillis() - recentlyFetchedIdentifiers.fetchTime < IDENTIFIER_REFRESH_INTERVAL_MILLIS) {
            return recentlyFetchedIdentifiers;
        }
        AttributionIdentifiers identifiers = getAndroidId(context);
        Cursor c = null;
        try {
            String[] projection = new String[]{ATTRIBUTION_ID_COLUMN_NAME, ANDROID_ID_COLUMN_NAME, LIMIT_TRACKING_COLUMN_NAME};
            Uri providerUri = null;
            if (context.getPackageManager().resolveContentProvider(ATTRIBUTION_ID_CONTENT_PROVIDER, CONNECTION_RESULT_SUCCESS) != null) {
                providerUri = Uri.parse("content://com.facebook.katana.provider.AttributionIdProvider");
            } else if (context.getPackageManager().resolveContentProvider(ATTRIBUTION_ID_CONTENT_PROVIDER_WAKIZASHI, CONNECTION_RESULT_SUCCESS) != null) {
                providerUri = Uri.parse("content://com.facebook.wakizashi.provider.AttributionIdProvider");
            }
            String installerPackageName = getInstallerPackageName(context);
            if (installerPackageName != null) {
                identifiers.androidInstallerPackage = installerPackageName;
            }
            AttributionIdentifiers cacheAndReturnIdentifiers;
            if (providerUri == null) {
                cacheAndReturnIdentifiers = cacheAndReturnIdentifiers(identifiers);
                if (c == null) {
                    return cacheAndReturnIdentifiers;
                }
                c.close();
                return cacheAndReturnIdentifiers;
            }
            c = context.getContentResolver().query(providerUri, projection, null, null, null);
            if (c == null || !c.moveToFirst()) {
                cacheAndReturnIdentifiers = cacheAndReturnIdentifiers(identifiers);
                if (c == null) {
                    return cacheAndReturnIdentifiers;
                }
                c.close();
                return cacheAndReturnIdentifiers;
            }
            int attributionColumnIndex = c.getColumnIndex(ATTRIBUTION_ID_COLUMN_NAME);
            int androidIdColumnIndex = c.getColumnIndex(ANDROID_ID_COLUMN_NAME);
            int limitTrackingColumnIndex = c.getColumnIndex(LIMIT_TRACKING_COLUMN_NAME);
            identifiers.attributionId = c.getString(attributionColumnIndex);
            if (androidIdColumnIndex > 0 && limitTrackingColumnIndex > 0 && identifiers.getAndroidAdvertiserId() == null) {
                identifiers.androidAdvertiserId = c.getString(androidIdColumnIndex);
                identifiers.limitTracking = Boolean.parseBoolean(c.getString(limitTrackingColumnIndex));
            }
            if (c != null) {
                c.close();
            }
            return cacheAndReturnIdentifiers(identifiers);
        } catch (Exception e) {
            Log.d(TAG, "Caught unexpected exception in getAttributionId(): " + e.toString());
            if (c == null) {
                return null;
            }
            c.close();
            return null;
        } catch (Throwable th) {
            if (c != null) {
                c.close();
            }
        }
    }

    private static AttributionIdentifiers cacheAndReturnIdentifiers(AttributionIdentifiers identifiers) {
        identifiers.fetchTime = System.currentTimeMillis();
        recentlyFetchedIdentifiers = identifiers;
        return identifiers;
    }

    public String getAttributionId() {
        return this.attributionId;
    }

    public String getAndroidAdvertiserId() {
        return this.androidAdvertiserId;
    }

    public String getAndroidInstallerPackage() {
        return this.androidInstallerPackage;
    }

    public boolean isTrackingLimited() {
        return this.limitTracking;
    }

    @Nullable
    private static String getInstallerPackageName(Context context) {
        PackageManager packageManager = context.getPackageManager();
        if (packageManager != null) {
            return packageManager.getInstallerPackageName(context.getPackageName());
        }
        return null;
    }
}
