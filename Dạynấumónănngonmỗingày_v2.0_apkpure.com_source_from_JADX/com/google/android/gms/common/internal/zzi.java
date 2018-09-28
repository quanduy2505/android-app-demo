package com.google.android.gms.common.internal;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import com.google.android.gms.internal.zzaax;

public abstract class zzi implements OnClickListener {

    /* renamed from: com.google.android.gms.common.internal.zzi.1 */
    class C11321 extends zzi {
        final /* synthetic */ Activity val$activity;
        final /* synthetic */ Intent val$intent;
        final /* synthetic */ int val$requestCode;

        C11321(Intent intent, Activity activity, int i) {
            this.val$intent = intent;
            this.val$activity = activity;
            this.val$requestCode = i;
        }

        public void zzxm() {
            if (this.val$intent != null) {
                this.val$activity.startActivityForResult(this.val$intent, this.val$requestCode);
            }
        }
    }

    /* renamed from: com.google.android.gms.common.internal.zzi.2 */
    class C11332 extends zzi {
        final /* synthetic */ Fragment val$fragment;
        final /* synthetic */ Intent val$intent;
        final /* synthetic */ int val$requestCode;

        C11332(Intent intent, Fragment fragment, int i) {
            this.val$intent = intent;
            this.val$fragment = fragment;
            this.val$requestCode = i;
        }

        public void zzxm() {
            if (this.val$intent != null) {
                this.val$fragment.startActivityForResult(this.val$intent, this.val$requestCode);
            }
        }
    }

    /* renamed from: com.google.android.gms.common.internal.zzi.3 */
    class C11343 extends zzi {
        final /* synthetic */ Intent val$intent;
        final /* synthetic */ int val$requestCode;
        final /* synthetic */ zzaax zzaEh;

        C11343(Intent intent, zzaax com_google_android_gms_internal_zzaax, int i) {
            this.val$intent = intent;
            this.zzaEh = com_google_android_gms_internal_zzaax;
            this.val$requestCode = i;
        }

        @TargetApi(11)
        public void zzxm() {
            if (this.val$intent != null) {
                this.zzaEh.startActivityForResult(this.val$intent, this.val$requestCode);
            }
        }
    }

    public static zzi zza(Activity activity, Intent intent, int i) {
        return new C11321(intent, activity, i);
    }

    public static zzi zza(@NonNull Fragment fragment, Intent intent, int i) {
        return new C11332(intent, fragment, i);
    }

    public static zzi zza(@NonNull zzaax com_google_android_gms_internal_zzaax, Intent intent, int i) {
        return new C11343(intent, com_google_android_gms_internal_zzaax, i);
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        try {
            zzxm();
        } catch (Throwable e) {
            Log.e("DialogRedirect", "Failed to start resolution intent", e);
        } finally {
            dialogInterface.dismiss();
        }
    }

    protected abstract void zzxm();
}
