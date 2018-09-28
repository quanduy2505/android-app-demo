package com.google.android.gms.internal;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.ArrayMap;
import com.google.android.gms.common.internal.zzac;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuthProvider;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class zzbkh extends FirebaseUser {
    private zzbrl zzbVW;
    private zzbjp zzbWK;
    private zzbkf zzbWL;
    private String zzbWM;
    private String zzbWN;
    private List<zzbkf> zzbWO;
    private List<String> zzbWP;
    private Map<String, zzbkf> zzbWQ;
    private String zzbWR;
    private boolean zzbWS;

    public zzbkh(@NonNull FirebaseApp firebaseApp, @NonNull List<? extends UserInfo> list) {
        zzac.zzw(firebaseApp);
        this.zzbWM = firebaseApp.getName();
        this.zzbVW = zzbiz.zzUg();
        this.zzbWN = "com.google.firebase.auth.internal.DefaultFirebaseUser";
        this.zzbWR = "2";
        zzR(list);
    }

    @Nullable
    public String getDisplayName() {
        return this.zzbWL.getDisplayName();
    }

    @Nullable
    public String getEmail() {
        return this.zzbWL.getEmail();
    }

    @Nullable
    public Uri getPhotoUrl() {
        return this.zzbWL.getPhotoUrl();
    }

    @NonNull
    public List<? extends UserInfo> getProviderData() {
        return this.zzbWO;
    }

    @NonNull
    public String getProviderId() {
        return this.zzbWL.getProviderId();
    }

    @Nullable
    public List<String> getProviders() {
        return this.zzbWP;
    }

    @NonNull
    public String getUid() {
        return this.zzbWL.getUid();
    }

    public boolean isAnonymous() {
        return this.zzbWS;
    }

    public boolean isEmailVerified() {
        return this.zzbWL.isEmailVerified();
    }

    @NonNull
    public FirebaseUser zzR(@NonNull List<? extends UserInfo> list) {
        zzac.zzw(list);
        this.zzbWO = new ArrayList(list.size());
        this.zzbWP = new ArrayList(list.size());
        this.zzbWQ = new ArrayMap();
        for (int i = 0; i < list.size(); i++) {
            UserInfo userInfo = (UserInfo) list.get(i);
            if (userInfo.getProviderId().equals(FirebaseAuthProvider.PROVIDER_ID)) {
                this.zzbWL = (zzbkf) userInfo;
            } else {
                this.zzbWP.add(userInfo.getProviderId());
            }
            this.zzbWO.add((zzbkf) userInfo);
            this.zzbWQ.put(userInfo.getProviderId(), (zzbkf) userInfo);
        }
        if (this.zzbWL == null) {
            this.zzbWL = (zzbkf) this.zzbWO.get(0);
        }
        return this;
    }

    @NonNull
    public FirebaseApp zzTV() {
        return FirebaseApp.getInstance(this.zzbWM);
    }

    @NonNull
    public zzbjp zzTW() {
        return this.zzbWK;
    }

    @NonNull
    public String zzTX() {
        return this.zzbVW.zzaI(this.zzbWK);
    }

    @NonNull
    public String zzTY() {
        return zzTW().getAccessToken();
    }

    public List<zzbkf> zzUG() {
        return this.zzbWO;
    }

    public void zza(@NonNull zzbjp com_google_android_gms_internal_zzbjp) {
        this.zzbWK = (zzbjp) zzac.zzw(com_google_android_gms_internal_zzbjp);
    }

    public /* synthetic */ FirebaseUser zzaT(boolean z) {
        return zzaU(z);
    }

    public zzbkh zzaU(boolean z) {
        this.zzbWS = z;
        return this;
    }

    public zzbkh zziA(@NonNull String str) {
        this.zzbWR = str;
        return this;
    }
}
