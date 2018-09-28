package com.app.tuan88291.testapp;

import android.util.Log;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class firebase extends FirebaseInstanceIdService {
    private static final String TAG = "MyFirebaseIIDService";

    public void onTokenRefresh() {
        Log.d(TAG, "Refreshed token: " + FirebaseInstanceId.getInstance().getToken());
    }

    private void sendRegistrationToServer(String token) {
    }
}
