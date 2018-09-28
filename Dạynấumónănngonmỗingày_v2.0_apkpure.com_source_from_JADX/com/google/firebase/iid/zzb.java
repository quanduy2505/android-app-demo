package com.google.firebase.iid;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.VisibleForTesting;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;
import com.google.android.gms.iid.MessengerCompat;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class zzb extends Service {
    @VisibleForTesting
    final ExecutorService zzbFy;
    private int zzbfI;
    private int zzbfJ;
    MessengerCompat zzbhh;
    private final Object zzrN;

    /* renamed from: com.google.firebase.iid.zzb.1 */
    class C07191 extends Handler {
        final /* synthetic */ zzb zzciN;

        C07191(zzb com_google_firebase_iid_zzb, Looper looper) {
            this.zzciN = com_google_firebase_iid_zzb;
            super(looper);
        }

        public void handleMessage(Message message) {
            int zzc = MessengerCompat.zzc(message);
            zzf.zzbi(this.zzciN);
            this.zzciN.getPackageManager();
            if (zzc == zzf.zzbhs || zzc == zzf.zzbhr) {
                this.zzciN.zzm((Intent) message.obj);
                return;
            }
            int i = zzf.zzbhr;
            Log.w("FirebaseInstanceId", "Message from unexpected caller " + zzc + " mine=" + i + " appid=" + zzf.zzbhs);
        }
    }

    /* renamed from: com.google.firebase.iid.zzb.2 */
    class C07202 implements Runnable {
        final /* synthetic */ Intent val$intent;
        final /* synthetic */ zzb zzciN;
        final /* synthetic */ Intent zzciO;

        C07202(zzb com_google_firebase_iid_zzb, Intent intent, Intent intent2) {
            this.zzciN = com_google_firebase_iid_zzb;
            this.val$intent = intent;
            this.zzciO = intent2;
        }

        public void run() {
            this.zzciN.zzm(this.val$intent);
            this.zzciN.zzG(this.zzciO);
        }
    }

    public zzb() {
        this.zzbhh = new MessengerCompat(new C07191(this, Looper.getMainLooper()));
        this.zzbFy = Executors.newSingleThreadExecutor();
        this.zzrN = new Object();
        this.zzbfJ = 0;
    }

    private void zzG(Intent intent) {
        if (intent != null) {
            WakefulBroadcastReceiver.completeWakefulIntent(intent);
        }
        synchronized (this.zzrN) {
            this.zzbfJ--;
            if (this.zzbfJ == 0) {
                zzjr(this.zzbfI);
            }
        }
    }

    public final IBinder onBind(Intent intent) {
        return (intent == null || !"com.google.firebase.INSTANCE_ID_EVENT".equals(intent.getAction())) ? null : this.zzbhh.getBinder();
    }

    public final int onStartCommand(Intent intent, int i, int i2) {
        synchronized (this.zzrN) {
            this.zzbfI = i2;
            this.zzbfJ++;
        }
        Intent zzF = zzF(intent);
        if (zzF == null) {
            zzG(intent);
            return 2;
        } else if (zzH(zzF)) {
            zzG(intent);
            return 2;
        } else {
            this.zzbFy.execute(new C07202(this, zzF, intent));
            return 3;
        }
    }

    protected abstract Intent zzF(Intent intent);

    public boolean zzH(Intent intent) {
        return false;
    }

    boolean zzjr(int i) {
        return stopSelfResult(i);
    }

    public abstract void zzm(Intent intent);
}
