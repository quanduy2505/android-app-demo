package com.google.android.gms.internal;

import android.support.annotation.NonNull;
import com.google.android.gms.common.internal.zzac;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthCredential;
import com.google.firebase.auth.GithubAuthCredential;
import com.google.firebase.auth.GoogleAuthCredential;
import com.google.firebase.auth.TwitterAuthCredential;

public class zzbkc {
    @NonNull
    public static zzbjz zza(@NonNull AuthCredential authCredential) {
        zzac.zzw(authCredential);
        if (GoogleAuthCredential.class.isAssignableFrom(authCredential.getClass())) {
            return GoogleAuthCredential.zza((GoogleAuthCredential) authCredential);
        }
        if (FacebookAuthCredential.class.isAssignableFrom(authCredential.getClass())) {
            return FacebookAuthCredential.zza((FacebookAuthCredential) authCredential);
        }
        if (TwitterAuthCredential.class.isAssignableFrom(authCredential.getClass())) {
            return TwitterAuthCredential.zza((TwitterAuthCredential) authCredential);
        }
        if (GithubAuthCredential.class.isAssignableFrom(authCredential.getClass())) {
            return GithubAuthCredential.zza((GithubAuthCredential) authCredential);
        }
        throw new IllegalArgumentException("Unsupported credential type.");
    }
}
