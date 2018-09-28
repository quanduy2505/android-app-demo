package com.google.firebase.auth;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import com.google.android.gms.common.internal.safeparcel.zza;

public class UserProfileChangeRequest extends zza {
    public static final Creator<UserProfileChangeRequest> CREATOR;
    public final int mVersionCode;
    private String zzaPq;
    private String zzaiX;
    private boolean zzbVK;
    private boolean zzbVL;
    private Uri zzbVM;

    public static class Builder {
        private String zzaiX;
        private boolean zzbVK;
        private boolean zzbVL;
        private Uri zzbVM;

        public UserProfileChangeRequest build() {
            return new UserProfileChangeRequest(1, this.zzaiX, this.zzbVM == null ? null : this.zzbVM.toString(), this.zzbVK, this.zzbVL);
        }

        public Builder setDisplayName(@Nullable String str) {
            if (str == null) {
                this.zzbVK = true;
            } else {
                this.zzaiX = str;
            }
            return this;
        }

        public Builder setPhotoUri(@Nullable Uri uri) {
            if (uri == null) {
                this.zzbVL = true;
            } else {
                this.zzbVM = uri;
            }
            return this;
        }
    }

    static {
        CREATOR = new zza();
    }

    UserProfileChangeRequest(int i, String str, String str2, boolean z, boolean z2) {
        this.mVersionCode = i;
        this.zzaiX = str;
        this.zzaPq = str2;
        this.zzbVK = z;
        this.zzbVL = z2;
        this.zzbVM = TextUtils.isEmpty(str2) ? null : Uri.parse(str2);
    }

    @Nullable
    public String getDisplayName() {
        return this.zzaiX;
    }

    @Nullable
    public Uri getPhotoUri() {
        return this.zzbVM;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zza.zza(this, parcel, i);
    }

    public String zzUb() {
        return this.zzaPq;
    }

    public boolean zzUc() {
        return this.zzbVK;
    }

    public boolean zzUd() {
        return this.zzbVL;
    }
}
