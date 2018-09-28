package com.google.android.gms.dynamic;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.VisibleForTesting;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.internal.zzh;
import java.util.Iterator;
import java.util.LinkedList;

public abstract class zza<T extends LifecycleDelegate> {
    private T zzaQd;
    private Bundle zzaQe;
    private LinkedList<zza> zzaQf;
    private final zzf<T> zzaQg;

    /* renamed from: com.google.android.gms.dynamic.zza.5 */
    class C06055 implements OnClickListener {
        final /* synthetic */ Intent zzaQm;
        final /* synthetic */ Context zztd;

        C06055(Context context, Intent intent) {
            this.zztd = context;
            this.zzaQm = intent;
        }

        public void onClick(View view) {
            try {
                this.zztd.startActivity(this.zzaQm);
            } catch (Throwable e) {
                Log.e("DeferredLifecycleHelper", "Failed to start resolution intent", e);
            }
        }
    }

    private interface zza {
        int getState();

        void zzb(LifecycleDelegate lifecycleDelegate);
    }

    /* renamed from: com.google.android.gms.dynamic.zza.1 */
    class C11371 implements zzf<T> {
        final /* synthetic */ zza zzaQh;

        C11371(zza com_google_android_gms_dynamic_zza) {
            this.zzaQh = com_google_android_gms_dynamic_zza;
        }

        public void zza(T t) {
            this.zzaQh.zzaQd = t;
            Iterator it = this.zzaQh.zzaQf.iterator();
            while (it.hasNext()) {
                ((zza) it.next()).zzb(this.zzaQh.zzaQd);
            }
            this.zzaQh.zzaQf.clear();
            this.zzaQh.zzaQe = null;
        }
    }

    /* renamed from: com.google.android.gms.dynamic.zza.2 */
    class C11382 implements zza {
        final /* synthetic */ Activity val$activity;
        final /* synthetic */ zza zzaQh;
        final /* synthetic */ Bundle zzaQi;
        final /* synthetic */ Bundle zzxd;

        C11382(zza com_google_android_gms_dynamic_zza, Activity activity, Bundle bundle, Bundle bundle2) {
            this.zzaQh = com_google_android_gms_dynamic_zza;
            this.val$activity = activity;
            this.zzaQi = bundle;
            this.zzxd = bundle2;
        }

        public int getState() {
            return 0;
        }

        public void zzb(LifecycleDelegate lifecycleDelegate) {
            this.zzaQh.zzaQd.onInflate(this.val$activity, this.zzaQi, this.zzxd);
        }
    }

    /* renamed from: com.google.android.gms.dynamic.zza.3 */
    class C11393 implements zza {
        final /* synthetic */ zza zzaQh;
        final /* synthetic */ Bundle zzxd;

        C11393(zza com_google_android_gms_dynamic_zza, Bundle bundle) {
            this.zzaQh = com_google_android_gms_dynamic_zza;
            this.zzxd = bundle;
        }

        public int getState() {
            return 1;
        }

        public void zzb(LifecycleDelegate lifecycleDelegate) {
            this.zzaQh.zzaQd.onCreate(this.zzxd);
        }
    }

    /* renamed from: com.google.android.gms.dynamic.zza.4 */
    class C11404 implements zza {
        final /* synthetic */ zza zzaQh;
        final /* synthetic */ FrameLayout zzaQj;
        final /* synthetic */ LayoutInflater zzaQk;
        final /* synthetic */ ViewGroup zzaQl;
        final /* synthetic */ Bundle zzxd;

        C11404(zza com_google_android_gms_dynamic_zza, FrameLayout frameLayout, LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
            this.zzaQh = com_google_android_gms_dynamic_zza;
            this.zzaQj = frameLayout;
            this.zzaQk = layoutInflater;
            this.zzaQl = viewGroup;
            this.zzxd = bundle;
        }

        public int getState() {
            return 2;
        }

        public void zzb(LifecycleDelegate lifecycleDelegate) {
            this.zzaQj.removeAllViews();
            this.zzaQj.addView(this.zzaQh.zzaQd.onCreateView(this.zzaQk, this.zzaQl, this.zzxd));
        }
    }

    /* renamed from: com.google.android.gms.dynamic.zza.6 */
    class C11416 implements zza {
        final /* synthetic */ zza zzaQh;

        C11416(zza com_google_android_gms_dynamic_zza) {
            this.zzaQh = com_google_android_gms_dynamic_zza;
        }

        public int getState() {
            return 4;
        }

        public void zzb(LifecycleDelegate lifecycleDelegate) {
            this.zzaQh.zzaQd.onStart();
        }
    }

    /* renamed from: com.google.android.gms.dynamic.zza.7 */
    class C11427 implements zza {
        final /* synthetic */ zza zzaQh;

        C11427(zza com_google_android_gms_dynamic_zza) {
            this.zzaQh = com_google_android_gms_dynamic_zza;
        }

        public int getState() {
            return 5;
        }

        public void zzb(LifecycleDelegate lifecycleDelegate) {
            this.zzaQh.zzaQd.onResume();
        }
    }

    public zza() {
        this.zzaQg = new C11371(this);
    }

    private void zza(Bundle bundle, zza com_google_android_gms_dynamic_zza_zza) {
        if (this.zzaQd != null) {
            com_google_android_gms_dynamic_zza_zza.zzb(this.zzaQd);
            return;
        }
        if (this.zzaQf == null) {
            this.zzaQf = new LinkedList();
        }
        this.zzaQf.add(com_google_android_gms_dynamic_zza_zza);
        if (bundle != null) {
            if (this.zzaQe == null) {
                this.zzaQe = (Bundle) bundle.clone();
            } else {
                this.zzaQe.putAll(bundle);
            }
        }
        zza(this.zzaQg);
    }

    @VisibleForTesting
    static void zza(FrameLayout frameLayout, GoogleApiAvailability googleApiAvailability) {
        Context context = frameLayout.getContext();
        int isGooglePlayServicesAvailable = googleApiAvailability.isGooglePlayServicesAvailable(context);
        CharSequence zzi = zzh.zzi(context, isGooglePlayServicesAvailable);
        CharSequence zzk = zzh.zzk(context, isGooglePlayServicesAvailable);
        View linearLayout = new LinearLayout(frameLayout.getContext());
        linearLayout.setOrientation(1);
        linearLayout.setLayoutParams(new LayoutParams(-2, -2));
        frameLayout.addView(linearLayout);
        View textView = new TextView(frameLayout.getContext());
        textView.setLayoutParams(new LayoutParams(-2, -2));
        textView.setText(zzi);
        linearLayout.addView(textView);
        Intent zzb = googleApiAvailability.zzb(context, isGooglePlayServicesAvailable, null);
        if (zzb != null) {
            View button = new Button(context);
            button.setId(16908313);
            button.setLayoutParams(new LayoutParams(-2, -2));
            button.setText(zzk);
            linearLayout.addView(button);
            button.setOnClickListener(new C06055(context, zzb));
        }
    }

    public static void zzb(FrameLayout frameLayout) {
        zza(frameLayout, GoogleApiAvailability.getInstance());
    }

    private void zzgk(int i) {
        while (!this.zzaQf.isEmpty() && ((zza) this.zzaQf.getLast()).getState() >= i) {
            this.zzaQf.removeLast();
        }
    }

    public void onCreate(Bundle bundle) {
        zza(bundle, new C11393(this, bundle));
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        FrameLayout frameLayout = new FrameLayout(layoutInflater.getContext());
        zza(bundle, new C11404(this, frameLayout, layoutInflater, viewGroup, bundle));
        if (this.zzaQd == null) {
            zza(frameLayout);
        }
        return frameLayout;
    }

    public void onDestroy() {
        if (this.zzaQd != null) {
            this.zzaQd.onDestroy();
        } else {
            zzgk(1);
        }
    }

    public void onDestroyView() {
        if (this.zzaQd != null) {
            this.zzaQd.onDestroyView();
        } else {
            zzgk(2);
        }
    }

    public void onInflate(Activity activity, Bundle bundle, Bundle bundle2) {
        zza(bundle2, new C11382(this, activity, bundle, bundle2));
    }

    public void onLowMemory() {
        if (this.zzaQd != null) {
            this.zzaQd.onLowMemory();
        }
    }

    public void onPause() {
        if (this.zzaQd != null) {
            this.zzaQd.onPause();
        } else {
            zzgk(5);
        }
    }

    public void onResume() {
        zza(null, new C11427(this));
    }

    public void onSaveInstanceState(Bundle bundle) {
        if (this.zzaQd != null) {
            this.zzaQd.onSaveInstanceState(bundle);
        } else if (this.zzaQe != null) {
            bundle.putAll(this.zzaQe);
        }
    }

    public void onStart() {
        zza(null, new C11416(this));
    }

    public void onStop() {
        if (this.zzaQd != null) {
            this.zzaQd.onStop();
        } else {
            zzgk(4);
        }
    }

    public T zzAY() {
        return this.zzaQd;
    }

    protected void zza(FrameLayout frameLayout) {
        zzb(frameLayout);
    }

    protected abstract void zza(zzf<T> com_google_android_gms_dynamic_zzf_T);
}
