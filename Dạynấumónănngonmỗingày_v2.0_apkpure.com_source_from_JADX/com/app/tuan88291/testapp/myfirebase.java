package com.app.tuan88291.testapp;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat.Builder;
import android.util.Log;
import com.facebook.internal.NativeProtocol;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class myfirebase extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMsgService";

    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        Log.d(TAG, "Notification Message Body: " + remoteMessage.getNotification().getBody());
        Log.d(TAG, "title key: " + remoteMessage.getNotification().getTitle());
        Log.d(TAG, "title key1: " + ((String) remoteMessage.getData().get("thu")));
        sendNotification(remoteMessage.getNotification().getBody(), (String) remoteMessage.getData().get(NativeProtocol.WEB_DIALOG_URL));
    }

    private void sendNotification(String messageBody, String url) {
        Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(url));
        intent.addFlags(67108864);
        ((NotificationManager) getSystemService("notification")).notify(0, new Builder(this).setSmallIcon(C0336R.mipmap.ic_launcher).setContentTitle("Th\u00f4ng b\u00e1o m\u1edbi").setContentText(messageBody).setAutoCancel(true).setSound(RingtoneManager.getDefaultUri(2)).setContentIntent(PendingIntent.getActivity(this, 0, intent, 1073741824)).build());
    }
}
