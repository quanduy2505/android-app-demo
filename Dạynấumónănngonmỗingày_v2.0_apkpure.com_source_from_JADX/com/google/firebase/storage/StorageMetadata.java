package com.google.firebase.storage;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import com.facebook.share.internal.ShareConstants;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.internal.zzbqx;
import com.google.android.gms.internal.zzbrb;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.json.JSONException;
import org.json.JSONObject;
import rx.android.BuildConfig;

public class StorageMetadata {
    private String mPath;
    private String zzand;
    private String zzckA;
    private String zzckB;
    private String zzckC;
    private String zzckD;
    private String zzckE;
    private long zzckF;
    private String zzckG;
    private String zzckH;
    private String zzckI;
    private String zzckJ;
    private String zzckK;
    private Map<String, String> zzckL;
    private String[] zzckM;
    private StorageReference zzcki;
    private FirebaseStorage zzckz;

    public static class Builder {
        StorageMetadata zzckN;
        boolean zzckO;

        public Builder() {
            this.zzckN = new StorageMetadata();
        }

        public Builder(StorageMetadata storageMetadata) {
            this.zzckN = new StorageMetadata(false, null);
        }

        Builder(JSONObject jSONObject) throws JSONException {
            this.zzckN = new StorageMetadata();
            if (jSONObject != null) {
                zzu(jSONObject);
                this.zzckO = true;
            }
        }

        Builder(JSONObject jSONObject, StorageReference storageReference) throws JSONException {
            this(jSONObject);
            this.zzckN.zzcki = storageReference;
        }

        private void zzu(JSONObject jSONObject) throws JSONException {
            this.zzckN.zzckB = jSONObject.optString("generation");
            this.zzckN.mPath = jSONObject.optString(ShareConstants.WEB_DIALOG_PARAM_NAME);
            this.zzckN.zzckA = jSONObject.optString("bucket");
            this.zzckN.zzckC = jSONObject.optString("metageneration");
            this.zzckN.zzckD = jSONObject.optString("timeCreated");
            this.zzckN.zzckE = jSONObject.optString("updated");
            this.zzckN.zzckF = jSONObject.optLong("size");
            this.zzckN.zzckG = jSONObject.optString("md5Hash");
            this.zzckN.zzjI(jSONObject.optString("downloadTokens"));
            setContentType(jSONObject.optString("contentType"));
            if (jSONObject.has("metadata")) {
                JSONObject jSONObject2 = jSONObject.getJSONObject("metadata");
                Iterator keys = jSONObject2.keys();
                while (keys.hasNext()) {
                    String str = (String) keys.next();
                    setCustomMetadata(str, jSONObject2.getString(str));
                }
            }
            setCacheControl(jSONObject.optString("cacheControl"));
            setContentDisposition(jSONObject.optString("contentDisposition"));
            setContentEncoding(jSONObject.optString("'contentEncoding"));
            setContentLanguage(jSONObject.optString("'contentLanguage"));
        }

        public StorageMetadata build() {
            return new StorageMetadata(this.zzckO, null);
        }

        public Builder setCacheControl(String str) {
            this.zzckN.zzckH = str;
            return this;
        }

        public Builder setContentDisposition(String str) {
            this.zzckN.zzckI = str;
            return this;
        }

        public Builder setContentEncoding(String str) {
            this.zzckN.zzckJ = str;
            return this;
        }

        public Builder setContentLanguage(String str) {
            this.zzckN.zzckK = str;
            return this;
        }

        public Builder setContentType(String str) {
            this.zzckN.zzand = str;
            return this;
        }

        public Builder setCustomMetadata(String str, String str2) {
            if (this.zzckN.zzckL == null) {
                this.zzckN.zzckL = new HashMap();
            }
            this.zzckN.zzckL.put(str, str2);
            return this;
        }
    }

    public StorageMetadata() {
        this.mPath = null;
        this.zzckz = null;
        this.zzcki = null;
        this.zzckA = null;
        this.zzckB = null;
        this.zzand = null;
        this.zzckC = null;
        this.zzckD = null;
        this.zzckE = null;
        this.zzckG = null;
        this.zzckH = null;
        this.zzckI = null;
        this.zzckJ = null;
        this.zzckK = null;
        this.zzckL = null;
        this.zzckM = null;
    }

    private StorageMetadata(@NonNull StorageMetadata storageMetadata, boolean z) {
        this.mPath = null;
        this.zzckz = null;
        this.zzcki = null;
        this.zzckA = null;
        this.zzckB = null;
        this.zzand = null;
        this.zzckC = null;
        this.zzckD = null;
        this.zzckE = null;
        this.zzckG = null;
        this.zzckH = null;
        this.zzckI = null;
        this.zzckJ = null;
        this.zzckK = null;
        this.zzckL = null;
        this.zzckM = null;
        zzac.zzw(storageMetadata);
        this.mPath = storageMetadata.mPath;
        this.zzckz = storageMetadata.zzckz;
        this.zzcki = storageMetadata.zzcki;
        this.zzckA = storageMetadata.zzckA;
        this.zzand = storageMetadata.zzand;
        this.zzckH = storageMetadata.zzckH;
        this.zzckI = storageMetadata.zzckI;
        this.zzckJ = storageMetadata.zzckJ;
        this.zzckK = storageMetadata.zzckK;
        if (storageMetadata.zzckL != null) {
            this.zzckL = new HashMap(storageMetadata.zzckL);
        }
        this.zzckM = storageMetadata.zzckM;
        if (z) {
            this.zzckG = storageMetadata.zzckG;
            this.zzckF = storageMetadata.zzckF;
            this.zzckE = storageMetadata.zzckE;
            this.zzckD = storageMetadata.zzckD;
            this.zzckC = storageMetadata.zzckC;
            this.zzckB = storageMetadata.zzckB;
        }
    }

    private void zzjI(@Nullable String str) {
        if (!TextUtils.isEmpty(str)) {
            this.zzckM = str.split(",");
        }
    }

    @Nullable
    public String getBucket() {
        return this.zzckA;
    }

    @Nullable
    public String getCacheControl() {
        return this.zzckH;
    }

    @Nullable
    public String getContentDisposition() {
        return this.zzckI;
    }

    @Nullable
    public String getContentEncoding() {
        return this.zzckJ;
    }

    @Nullable
    public String getContentLanguage() {
        return this.zzckK;
    }

    public String getContentType() {
        return this.zzand;
    }

    public long getCreationTimeMillis() {
        return zzbrb.zzjM(this.zzckD);
    }

    public String getCustomMetadata(@NonNull String str) {
        return (this.zzckL == null || TextUtils.isEmpty(str)) ? null : (String) this.zzckL.get(str);
    }

    @NonNull
    public Set<String> getCustomMetadataKeys() {
        return this.zzckL == null ? Collections.emptySet() : this.zzckL.keySet();
    }

    @Nullable
    public Uri getDownloadUrl() {
        List downloadUrls = getDownloadUrls();
        return (downloadUrls == null || downloadUrls.size() <= 0) ? null : (Uri) downloadUrls.get(0);
    }

    @Nullable
    public List<Uri> getDownloadUrls() {
        List arrayList = new ArrayList();
        if (!(this.zzckM == null || this.zzcki == null)) {
            try {
                Object zzA = this.zzcki.zzaaN().zzA(this.zzcki.zzaaO());
                if (!TextUtils.isEmpty(zzA)) {
                    for (Object obj : this.zzckM) {
                        if (!TextUtils.isEmpty(obj)) {
                            arrayList.add(Uri.parse(new StringBuilder((String.valueOf(zzA).length() + 17) + String.valueOf(obj).length()).append(zzA).append("?alt=media&token=").append(obj).toString()));
                        }
                    }
                }
            } catch (Throwable e) {
                Log.e("StorageMetadata", "Unexpected error getting DownloadUrls.", e);
            }
        }
        return arrayList;
    }

    @Nullable
    public String getGeneration() {
        return this.zzckB;
    }

    @Nullable
    public String getMd5Hash() {
        return this.zzckG;
    }

    @Nullable
    public String getMetadataGeneration() {
        return this.zzckC;
    }

    @Nullable
    public String getName() {
        String path = getPath();
        if (TextUtils.isEmpty(path)) {
            return null;
        }
        int lastIndexOf = path.lastIndexOf(47);
        return lastIndexOf != -1 ? path.substring(lastIndexOf + 1) : path;
    }

    @NonNull
    public String getPath() {
        return this.mPath != null ? this.mPath : BuildConfig.VERSION_NAME;
    }

    @Nullable
    public StorageReference getReference() {
        if (this.zzcki != null || this.zzckz == null) {
            return this.zzcki;
        }
        String bucket = getBucket();
        String path = getPath();
        if (TextUtils.isEmpty(bucket) || TextUtils.isEmpty(path)) {
            return null;
        }
        try {
            return new StorageReference(new android.net.Uri.Builder().scheme("gs").authority(bucket).encodedPath(zzbqx.zzjJ(path)).build(), this.zzckz);
        } catch (Throwable e) {
            Log.e("StorageMetadata", new StringBuilder((String.valueOf(bucket).length() + 38) + String.valueOf(path).length()).append("Unable to create a valid default Uri. ").append(bucket).append(path).toString(), e);
            throw new IllegalStateException(e);
        }
    }

    public long getSizeBytes() {
        return this.zzckF;
    }

    public long getUpdatedTimeMillis() {
        return zzbrb.zzjM(this.zzckE);
    }

    @NonNull
    JSONObject zzaaM() throws JSONException {
        JSONObject jSONObject = new JSONObject();
        if (getContentType() != null) {
            jSONObject.put("contentType", getContentType());
        }
        if (this.zzckL != null) {
            jSONObject.put("metadata", new JSONObject(this.zzckL));
        }
        if (getCacheControl() != null) {
            jSONObject.put("cacheControl", getCacheControl());
        }
        if (getContentDisposition() != null) {
            jSONObject.put("contentDisposition", getContentDisposition());
        }
        if (getContentEncoding() != null) {
            jSONObject.put("'contentEncoding", getContentEncoding());
        }
        if (getContentLanguage() != null) {
            jSONObject.put("'contentLanguage", getContentLanguage());
        }
        return jSONObject;
    }
}
