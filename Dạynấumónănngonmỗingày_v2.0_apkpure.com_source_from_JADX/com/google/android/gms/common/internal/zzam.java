package com.google.android.gms.common.internal;

import android.content.Context;
import android.content.res.Resources;
import com.google.android.gms.C0598R;

public class zzam {
    private final Resources zzaFn;
    private final String zzaFo;

    public zzam(Context context) {
        zzac.zzw(context);
        this.zzaFn = context.getResources();
        this.zzaFo = this.zzaFn.getResourcePackageName(C0598R.string.common_google_play_services_unknown_issue);
    }

    public String getString(String str) {
        int identifier = this.zzaFn.getIdentifier(str, "string", this.zzaFo);
        return identifier == 0 ? null : this.zzaFn.getString(identifier);
    }
}
