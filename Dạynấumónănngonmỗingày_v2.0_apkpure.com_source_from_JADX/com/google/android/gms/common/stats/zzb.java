package com.google.android.gms.common.stats;

import com.google.android.gms.internal.zzabs;
import rx.android.BuildConfig;

public final class zzb {
    public static zzabs<Integer> zzaGb;
    public static zzabs<Integer> zzaGc;

    public static final class zza {
        public static zzabs<Integer> zzaGd;
        public static zzabs<String> zzaGe;
        public static zzabs<String> zzaGf;
        public static zzabs<String> zzaGg;
        public static zzabs<String> zzaGh;
        public static zzabs<Long> zzaGi;

        static {
            zzaGd = zzabs.zza("gms:common:stats:connections:level", Integer.valueOf(zzc.LOG_LEVEL_OFF));
            zzaGe = zzabs.zzA("gms:common:stats:connections:ignored_calling_processes", BuildConfig.VERSION_NAME);
            zzaGf = zzabs.zzA("gms:common:stats:connections:ignored_calling_services", BuildConfig.VERSION_NAME);
            zzaGg = zzabs.zzA("gms:common:stats:connections:ignored_target_processes", BuildConfig.VERSION_NAME);
            zzaGh = zzabs.zzA("gms:common:stats:connections:ignored_target_services", "com.google.android.gms.auth.GetToken");
            zzaGi = zzabs.zza("gms:common:stats:connections:time_out_duration", Long.valueOf(600000));
        }
    }

    static {
        zzaGb = zzabs.zza("gms:common:stats:max_num_of_events", Integer.valueOf(100));
        zzaGc = zzabs.zza("gms:common:stats:max_chunk_size", Integer.valueOf(100));
    }
}
