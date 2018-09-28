package com.google.firebase.storage;

import android.net.Uri;
import android.net.Uri.Builder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.internal.zzbrb;
import com.google.firebase.FirebaseApp;
import java.util.HashMap;
import java.util.Map;

public class FirebaseStorage {
    static final /* synthetic */ boolean $assertionsDisabled;
    private static final Map<FirebaseApp, FirebaseStorage> zzckq;
    private final FirebaseApp zzciR;
    private long zzckr;
    private long zzcks;
    private long zzckt;

    static {
        $assertionsDisabled = !FirebaseStorage.class.desiredAssertionStatus();
        zzckq = new HashMap();
    }

    private FirebaseStorage(@NonNull FirebaseApp firebaseApp) {
        this.zzckr = 600000;
        this.zzcks = 600000;
        this.zzckt = 120000;
        this.zzciR = firebaseApp;
    }

    @NonNull
    public static FirebaseStorage getInstance() {
        FirebaseApp instance = FirebaseApp.getInstance();
        zzac.zzb(instance != null, (Object) "You must call FirebaseApp.initialize() first.");
        if ($assertionsDisabled || instance != null) {
            return getInstance(instance);
        }
        throw new AssertionError();
    }

    @NonNull
    public static FirebaseStorage getInstance(@NonNull FirebaseApp firebaseApp) {
        FirebaseStorage firebaseStorage;
        zzac.zzb(firebaseApp != null, (Object) "Null is not a valid value for the FirebaseApp.");
        synchronized (zzckq) {
            firebaseStorage = (FirebaseStorage) zzckq.get(firebaseApp);
            if (firebaseStorage == null) {
                firebaseStorage = new FirebaseStorage(firebaseApp);
                zzckq.put(firebaseApp, firebaseStorage);
            }
        }
        return firebaseStorage;
    }

    @Nullable
    private String zzaaL() {
        return this.zzciR.getOptions().getStorageBucket();
    }

    @NonNull
    private StorageReference zzz(@NonNull Uri uri) {
        zzac.zzb((Object) uri, (Object) "uri must not be null");
        Object zzaaL = zzaaL();
        boolean z = TextUtils.isEmpty(zzaaL) || uri.getAuthority().equalsIgnoreCase(zzaaL);
        zzac.zzb(z, (Object) "The supplied bucketname is not available to this project.");
        return new StorageReference(uri, this);
    }

    @NonNull
    public FirebaseApp getApp() {
        return this.zzciR;
    }

    public long getMaxDownloadRetryTimeMillis() {
        return this.zzcks;
    }

    public long getMaxOperationRetryTimeMillis() {
        return this.zzckt;
    }

    public long getMaxUploadRetryTimeMillis() {
        return this.zzckr;
    }

    @NonNull
    public StorageReference getReference() {
        if (!TextUtils.isEmpty(zzaaL())) {
            return zzz(new Builder().scheme("gs").authority(zzaaL()).path("/").build());
        }
        throw new IllegalStateException("FirebaseApp was not initialized with a bucket name.");
    }

    @NonNull
    public StorageReference getReference(@NonNull String str) {
        zzac.zzb(!TextUtils.isEmpty(str), (Object) "location must not be null or empty");
        String toLowerCase = str.toLowerCase();
        if (!toLowerCase.startsWith("gs://") && !toLowerCase.startsWith("https://") && !toLowerCase.startsWith("http://")) {
            return getReference().child(str);
        }
        throw new IllegalArgumentException("location should not be a full URL.");
    }

    @NonNull
    public StorageReference getReferenceFromUrl(@NonNull String str) {
        zzac.zzb(!TextUtils.isEmpty(str), (Object) "location must not be null or empty");
        String toLowerCase = str.toLowerCase();
        if (toLowerCase.startsWith("gs://") || toLowerCase.startsWith("https://") || toLowerCase.startsWith("http://")) {
            try {
                Uri zzg = zzbrb.zzg(this.zzciR, str);
                if (zzg != null) {
                    return zzz(zzg);
                }
                throw new IllegalArgumentException("The storage Uri could not be parsed.");
            } catch (Throwable e) {
                Throwable th = e;
                String str2 = "FirebaseStorage";
                String str3 = "Unable to parse location:";
                toLowerCase = String.valueOf(str);
                Log.e(str2, toLowerCase.length() != 0 ? str3.concat(toLowerCase) : new String(str3), th);
                throw new IllegalArgumentException("The storage Uri could not be parsed.");
            }
        }
        throw new IllegalArgumentException("The storage Uri could not be parsed.");
    }

    public void setMaxDownloadRetryTimeMillis(long j) {
        this.zzcks = j;
    }

    public void setMaxOperationRetryTimeMillis(long j) {
        this.zzckt = j;
    }

    public void setMaxUploadRetryTimeMillis(long j) {
        this.zzckr = j;
    }
}
