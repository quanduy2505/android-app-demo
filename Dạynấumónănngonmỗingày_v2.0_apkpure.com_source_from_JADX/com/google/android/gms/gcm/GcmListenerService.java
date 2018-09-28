package com.google.android.gms.gcm;

import android.annotation.TargetApi;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import com.facebook.internal.NativeProtocol;
import com.google.android.gms.common.ConnectionResult;
import java.util.Iterator;
import rx.internal.operators.OnSubscribeConcatMap;
import rx.internal.schedulers.NewThreadWorker;

public abstract class GcmListenerService extends Service {
    private int zzbfI;
    private int zzbfJ;
    private final Object zzrN;

    /* renamed from: com.google.android.gms.gcm.GcmListenerService.1 */
    class C06121 implements Runnable {
        final /* synthetic */ Intent val$intent;
        final /* synthetic */ GcmListenerService zzbfK;

        C06121(GcmListenerService gcmListenerService, Intent intent) {
            this.zzbfK = gcmListenerService;
            this.val$intent = intent;
        }

        public void run() {
            this.zzbfK.zzm(this.val$intent);
        }
    }

    /* renamed from: com.google.android.gms.gcm.GcmListenerService.2 */
    class C06132 extends AsyncTask<Void, Void, Void> {
        final /* synthetic */ Intent val$intent;
        final /* synthetic */ GcmListenerService zzbfK;

        C06132(GcmListenerService gcmListenerService, Intent intent) {
            this.zzbfK = gcmListenerService;
            this.val$intent = intent;
        }

        protected Void doInBackground(Void... voidArr) {
            this.zzbfK.zzm(this.val$intent);
            return null;
        }
    }

    public GcmListenerService() {
        this.zzrN = new Object();
        this.zzbfJ = 0;
    }

    static void zzD(Bundle bundle) {
        Iterator it = bundle.keySet().iterator();
        while (it.hasNext()) {
            String str = (String) it.next();
            if (str != null && str.startsWith("google.c.")) {
                it.remove();
            }
        }
    }

    private void zzGa() {
        synchronized (this.zzrN) {
            this.zzbfJ--;
            if (this.zzbfJ == 0) {
                zzjr(this.zzbfI);
            }
        }
    }

    @TargetApi(11)
    private void zzl(Intent intent) {
        if (VERSION.SDK_INT >= 11) {
            AsyncTask.THREAD_POOL_EXECUTOR.execute(new C06121(this, intent));
        } else {
            new C06132(this, intent).execute(new Void[0]);
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void zzm(android.content.Intent r5) {
        /*
        r4 = this;
        r1 = r5.getAction();	 Catch:{ all -> 0x003d }
        r0 = -1;
        r2 = r1.hashCode();	 Catch:{ all -> 0x003d }
        switch(r2) {
            case 366519424: goto L_0x002f;
            default: goto L_0x000c;
        };	 Catch:{ all -> 0x003d }
    L_0x000c:
        switch(r0) {
            case 0: goto L_0x0039;
            default: goto L_0x000f;
        };	 Catch:{ all -> 0x003d }
    L_0x000f:
        r1 = "GcmListenerService";
        r2 = "Unknown intent action: ";
        r0 = r5.getAction();	 Catch:{ all -> 0x003d }
        r0 = java.lang.String.valueOf(r0);	 Catch:{ all -> 0x003d }
        r3 = r0.length();	 Catch:{ all -> 0x003d }
        if (r3 == 0) goto L_0x0042;
    L_0x0021:
        r0 = r2.concat(r0);	 Catch:{ all -> 0x003d }
    L_0x0025:
        android.util.Log.d(r1, r0);	 Catch:{ all -> 0x003d }
    L_0x0028:
        r4.zzGa();	 Catch:{ all -> 0x003d }
        android.support.v4.content.WakefulBroadcastReceiver.completeWakefulIntent(r5);
        return;
    L_0x002f:
        r2 = "com.google.android.c2dm.intent.RECEIVE";
        r1 = r1.equals(r2);	 Catch:{ all -> 0x003d }
        if (r1 == 0) goto L_0x000c;
    L_0x0037:
        r0 = 0;
        goto L_0x000c;
    L_0x0039:
        r4.zzn(r5);	 Catch:{ all -> 0x003d }
        goto L_0x0028;
    L_0x003d:
        r0 = move-exception;
        android.support.v4.content.WakefulBroadcastReceiver.completeWakefulIntent(r5);
        throw r0;
    L_0x0042:
        r0 = new java.lang.String;	 Catch:{ all -> 0x003d }
        r0.<init>(r2);	 Catch:{ all -> 0x003d }
        goto L_0x0025;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.gcm.GcmListenerService.zzm(android.content.Intent):void");
    }

    private void zzn(Intent intent) {
        String stringExtra = intent.getStringExtra("message_type");
        if (stringExtra == null) {
            stringExtra = GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE;
        }
        Object obj = -1;
        switch (stringExtra.hashCode()) {
            case -2062414158:
                if (stringExtra.equals(GoogleCloudMessaging.MESSAGE_TYPE_DELETED)) {
                    obj = 1;
                    break;
                }
                break;
            case 102161:
                if (stringExtra.equals(GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE)) {
                    obj = null;
                    break;
                }
                break;
            case 814694033:
                if (stringExtra.equals(GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR)) {
                    obj = 3;
                    break;
                }
                break;
            case 814800675:
                if (stringExtra.equals(GoogleCloudMessaging.MESSAGE_TYPE_SEND_EVENT)) {
                    obj = 2;
                    break;
                }
                break;
        }
        switch (obj) {
            case NewThreadWorker.PURGE_FREQUENCY /*0*/:
                zzo(intent);
            case OnSubscribeConcatMap.BOUNDARY /*1*/:
                onDeletedMessages();
            case OnSubscribeConcatMap.END /*2*/:
                onMessageSent(intent.getStringExtra("google.message_id"));
            case ConnectionResult.SERVICE_DISABLED /*3*/:
                onSendError(zzp(intent), intent.getStringExtra(NativeProtocol.BRIDGE_ARG_ERROR_BUNDLE));
            default:
                String str = "GcmListenerService";
                String str2 = "Received message with unknown type: ";
                stringExtra = String.valueOf(stringExtra);
                Log.w(str, stringExtra.length() != 0 ? str2.concat(stringExtra) : new String(str2));
        }
    }

    private void zzo(Intent intent) {
        Bundle extras = intent.getExtras();
        extras.remove("message_type");
        extras.remove("android.support.content.wakelockid");
        if (zza.zzE(extras)) {
            if (zza.zzbc(this)) {
                zza.zzF(extras);
            } else {
                zza.zzbb(this).zzG(extras);
                return;
            }
        }
        String string = extras.getString("from");
        extras.remove("from");
        zzD(extras);
        onMessageReceived(string, extras);
    }

    private String zzp(Intent intent) {
        String stringExtra = intent.getStringExtra("google.message_id");
        return stringExtra == null ? intent.getStringExtra("message_id") : stringExtra;
    }

    public final IBinder onBind(Intent intent) {
        return null;
    }

    public void onDeletedMessages() {
    }

    public void onMessageReceived(String str, Bundle bundle) {
    }

    public void onMessageSent(String str) {
    }

    public void onSendError(String str, String str2) {
    }

    public final int onStartCommand(Intent intent, int i, int i2) {
        synchronized (this.zzrN) {
            this.zzbfI = i2;
            this.zzbfJ++;
        }
        if (intent == null) {
            zzGa();
            return 2;
        }
        zzl(intent);
        return 3;
    }

    boolean zzjr(int i) {
        return stopSelfResult(i);
    }
}
