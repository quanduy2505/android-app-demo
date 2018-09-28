package com.google.android.gms.flags.impl;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.android.gms.internal.zzaps;
import java.util.concurrent.Callable;

public class zzb {
    private static SharedPreferences zzaWS;

    /* renamed from: com.google.android.gms.flags.impl.zzb.1 */
    class C06111 implements Callable<SharedPreferences> {
        final /* synthetic */ Context zztd;

        C06111(Context context) {
            this.zztd = context;
        }

        public /* synthetic */ Object call() throws Exception {
            return zzCU();
        }

        public SharedPreferences zzCU() {
            return this.zztd.getSharedPreferences("google_sdk_flags", 1);
        }
    }

    static {
        zzaWS = null;
    }

    public static SharedPreferences zzm(Context context) {
        SharedPreferences sharedPreferences;
        synchronized (SharedPreferences.class) {
            if (zzaWS == null) {
                zzaWS = (SharedPreferences) zzaps.zzb(new C06111(context));
            }
            sharedPreferences = zzaWS;
        }
        return sharedPreferences;
    }
}
