package com.app.tuan88291.testapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.support.v7.app.AppCompatActivity;
import rx.android.BuildConfig;

public class Splash extends AppCompatActivity {
    DBhelper db;

    /* renamed from: com.app.tuan88291.testapp.Splash.1 */
    class C03381 extends Thread {
        C03381() {
        }

        public void run() {
            try {
                C03381.sleep(1000);
                if (Splash.this.getSharedPreferences("scook", 0).getString("stt", BuildConfig.VERSION_NAME).equals(BuildConfig.VERSION_NAME)) {
                    Splash.this.startActivity(new Intent(Splash.this, IntroActivity.class));
                    return;
                }
                Splash.this.startActivity(new Intent(Splash.this, ContentView.class));
            } catch (InterruptedException e) {
                e.printStackTrace();
                if (Splash.this.getSharedPreferences("scook", 0).getString("stt", BuildConfig.VERSION_NAME).equals(BuildConfig.VERSION_NAME)) {
                    Splash.this.startActivity(new Intent(Splash.this, IntroActivity.class));
                    return;
                }
                Splash.this.startActivity(new Intent(Splash.this, ContentView.class));
            } catch (Throwable th) {
                if (Splash.this.getSharedPreferences("scook", 0).getString("stt", BuildConfig.VERSION_NAME).equals(BuildConfig.VERSION_NAME)) {
                    Splash.this.startActivity(new Intent(Splash.this, IntroActivity.class));
                } else {
                    Splash.this.startActivity(new Intent(Splash.this, ContentView.class));
                }
            }
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(1);
        super.onCreate(savedInstanceState);
        getWindow().setFlags(AccessibilityNodeInfoCompat.ACTION_NEXT_HTML_ELEMENT, AccessibilityNodeInfoCompat.ACTION_NEXT_HTML_ELEMENT);
        setContentView((int) C0336R.layout.plash);
        this.db = new DBhelper(this);
        new C03381().start();
    }

    protected void onPause() {
        super.onPause();
        finish();
    }
}
