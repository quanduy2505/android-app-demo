package com.google.android.gms.common.stats;

import android.content.ComponentName;
import com.google.android.gms.common.GooglePlayServicesUtil;

public final class zzc {
    public static int LOG_LEVEL_OFF;
    public static final ComponentName zzaGj;
    public static int zzaGk;
    public static int zzaGl;
    public static int zzaGm;
    public static int zzaGn;
    public static int zzaGo;
    public static int zzaGp;
    public static int zzaGq;

    static {
        zzaGj = new ComponentName(GooglePlayServicesUtil.GOOGLE_PLAY_SERVICES_PACKAGE, "com.google.android.gms.common.stats.GmsCoreStatsService");
        LOG_LEVEL_OFF = 0;
        zzaGk = 1;
        zzaGl = 2;
        zzaGm = 4;
        zzaGn = 8;
        zzaGo = 16;
        zzaGp = 32;
        zzaGq = 1;
    }
}
