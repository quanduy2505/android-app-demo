package com.google.firebase.messaging;

import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.WorkerThread;
import android.util.Log;
import com.facebook.appevents.AppEventsConstants;
import com.facebook.internal.NativeProtocol;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.firebase.iid.zzb;
import com.google.firebase.iid.zzg;
import java.util.Iterator;
import rx.internal.operators.OnSubscribeConcatMap;
import rx.internal.schedulers.NewThreadWorker;

public class FirebaseMessagingService extends zzb {
    static void zzD(Bundle bundle) {
        Iterator it = bundle.keySet().iterator();
        while (it.hasNext()) {
            String str = (String) it.next();
            if (str != null && str.startsWith("google.c.")) {
                it.remove();
            }
        }
    }

    private void zzK(Intent intent) {
        PendingIntent pendingIntent = (PendingIntent) intent.getParcelableExtra("pending_intent");
        if (pendingIntent != null) {
            try {
                pendingIntent.send();
            } catch (CanceledException e) {
                Log.e("FirebaseMessaging", "Notification pending intent canceled");
            }
        }
        if (zzW(intent.getExtras())) {
            zzb.zzj(this, intent);
        }
    }

    static boolean zzW(Bundle bundle) {
        return bundle == null ? false : AppEventsConstants.EVENT_PARAM_VALUE_YES.equals(bundle.getString("google.c.a.e"));
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
                if (zzW(intent.getExtras())) {
                    zzb.zzi(this, intent);
                }
                zzo(intent);
            case OnSubscribeConcatMap.BOUNDARY /*1*/:
                onDeletedMessages();
            case OnSubscribeConcatMap.END /*2*/:
                onMessageSent(intent.getStringExtra("google.message_id"));
            case ConnectionResult.SERVICE_DISABLED /*3*/:
                onSendError(zzp(intent), new SendException(intent.getStringExtra(NativeProtocol.BRIDGE_ARG_ERROR_BUNDLE)));
            default:
                String str = "FirebaseMessaging";
                String str2 = "Received message with unknown type: ";
                stringExtra = String.valueOf(stringExtra);
                Log.w(str, stringExtra.length() != 0 ? str2.concat(stringExtra) : new String(str2));
        }
    }

    private void zzo(Intent intent) {
        Bundle extras = intent.getExtras();
        extras.remove("android.support.content.wakelockid");
        if (zza.zzE(extras)) {
            if (!zza.zzbc(this)) {
                zza.zzca(this).zzT(extras);
                return;
            } else if (zzW(extras)) {
                zzb.zzl(this, intent);
            }
        }
        onMessageReceived(new RemoteMessage(extras));
    }

    private String zzp(Intent intent) {
        String stringExtra = intent.getStringExtra("google.message_id");
        return stringExtra == null ? intent.getStringExtra("message_id") : stringExtra;
    }

    @WorkerThread
    public void onDeletedMessages() {
    }

    @WorkerThread
    public void onMessageReceived(RemoteMessage remoteMessage) {
    }

    @WorkerThread
    public void onMessageSent(String str) {
    }

    @WorkerThread
    public void onSendError(String str, Exception exception) {
    }

    protected Intent zzF(Intent intent) {
        return zzg.zzaaj().zzaal();
    }

    public boolean zzH(Intent intent) {
        if (!"com.google.firebase.messaging.NOTIFICATION_OPEN".equals(intent.getAction())) {
            return false;
        }
        zzK(intent);
        return true;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void zzm(android.content.Intent r5) {
        /*
        r4 = this;
        r0 = r5.getAction();
        if (r0 != 0) goto L_0x0008;
    L_0x0006:
        r0 = "";
    L_0x0008:
        r1 = -1;
        r2 = r0.hashCode();
        switch(r2) {
            case 75300319: goto L_0x0038;
            case 366519424: goto L_0x002e;
            default: goto L_0x0010;
        };
    L_0x0010:
        r0 = r1;
    L_0x0011:
        switch(r0) {
            case 0: goto L_0x0042;
            case 1: goto L_0x0046;
            default: goto L_0x0014;
        };
    L_0x0014:
        r1 = "FirebaseMessaging";
        r2 = "Unknown intent action: ";
        r0 = r5.getAction();
        r0 = java.lang.String.valueOf(r0);
        r3 = r0.length();
        if (r3 == 0) goto L_0x0054;
    L_0x0026:
        r0 = r2.concat(r0);
    L_0x002a:
        android.util.Log.d(r1, r0);
    L_0x002d:
        return;
    L_0x002e:
        r2 = "com.google.android.c2dm.intent.RECEIVE";
        r0 = r0.equals(r2);
        if (r0 == 0) goto L_0x0010;
    L_0x0036:
        r0 = 0;
        goto L_0x0011;
    L_0x0038:
        r2 = "com.google.firebase.messaging.NOTIFICATION_DISMISS";
        r0 = r0.equals(r2);
        if (r0 == 0) goto L_0x0010;
    L_0x0040:
        r0 = 1;
        goto L_0x0011;
    L_0x0042:
        r4.zzn(r5);
        goto L_0x002d;
    L_0x0046:
        r0 = r5.getExtras();
        r0 = zzW(r0);
        if (r0 == 0) goto L_0x002d;
    L_0x0050:
        com.google.firebase.messaging.zzb.zzk(r4, r5);
        goto L_0x002d;
    L_0x0054:
        r0 = new java.lang.String;
        r0.<init>(r2);
        goto L_0x002a;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.firebase.messaging.FirebaseMessagingService.zzm(android.content.Intent):void");
    }
}
