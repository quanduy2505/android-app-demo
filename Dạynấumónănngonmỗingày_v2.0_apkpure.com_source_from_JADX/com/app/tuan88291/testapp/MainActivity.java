package com.app.tuan88291.testapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.CallbackManager.Factory;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.internal.NativeProtocol;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import rx.android.BuildConfig;

public class MainActivity extends AppCompatActivity {
    private AccessTokenTracker accessTokenTracker;
    private CallbackManager callbackManager;
    private DBhelper db;
    private LoginButton login;
    ProgressDialog pDialog;
    private ProfileTracker profileTracker;
    private TextView tit;
    private Button vote;
    private Button web;

    /* renamed from: com.app.tuan88291.testapp.MainActivity.1 */
    class C03251 implements OnClickListener {
        C03251() {
        }

        public void onClick(View v) {
            Intent i = new Intent("android.intent.action.VIEW");
            i.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.app.tuan88291.testapp"));
            MainActivity.this.startActivity(i);
        }
    }

    /* renamed from: com.app.tuan88291.testapp.MainActivity.2 */
    class C03262 implements OnClickListener {
        C03262() {
        }

        public void onClick(View v) {
            Intent i = new Intent("android.intent.action.VIEW");
            i.setData(Uri.parse("https://www.facebook.com/vantuan88291"));
            MainActivity.this.startActivity(i);
        }
    }

    private class loadingdk extends AsyncTask<String, Void, String> {
        private loadingdk() {
        }

        protected String doInBackground(String... params) {
            HttpResponse response = null;
            try {
                response = new DefaultHttpClient().execute(new HttpGet("http://cook.audition2.com/user.php?idfb=" + MainActivity.this.db.idfb()));
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e2) {
                e2.printStackTrace();
            }
            String html = BuildConfig.VERSION_NAME;
            InputStream in = null;
            try {
                in = response.getEntity().getContent();
            } catch (IllegalStateException e3) {
                e3.printStackTrace();
            } catch (IOException e22) {
                e22.printStackTrace();
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder str = new StringBuilder();
            while (true) {
                try {
                    String line = reader.readLine();
                    if (line != null) {
                        str.append(line);
                    }
                } catch (IOException e222) {
                    e222.printStackTrace();
                }
                try {
                    break;
                } catch (IOException e2222) {
                    e2222.printStackTrace();
                }
            }
            in.close();
            return str.toString();
        }

        protected void onPreExecute() {
            super.onPreExecute();
            MainActivity.this.pDialog = new ProgressDialog(MainActivity.this);
            MainActivity.this.pDialog.setMessage("loading ...");
            MainActivity.this.pDialog.setIndeterminate(false);
            MainActivity.this.pDialog.setCancelable(true);
            MainActivity.this.pDialog.show();
        }

        protected void onPostExecute(String result) {
            if (result.equals(NativeProtocol.BRIDGE_ARG_ERROR_BUNDLE)) {
                MainActivity.this.db.delete("user");
                Toast.makeText(MainActivity.this, "Kh\u00f4ng th\u1ec3 \u0111\u0103ng nh\u1eadp", 0).show();
            } else {
                Home hm = new Home();
                Home.idtype = BuildConfig.VERSION_NAME;
                Home.page = 1;
                Home.pastVisiblesItems = 0;
                MainActivity.this.startActivity(new Intent(MainActivity.this, ContentView.class));
            }
            MainActivity.this.pDialog.dismiss();
            super.onPostExecute(result);
        }

        protected void onProgressUpdate(Void... values) {
        }
    }

    /* renamed from: com.app.tuan88291.testapp.MainActivity.3 */
    class C09623 implements FacebookCallback<LoginResult> {

        /* renamed from: com.app.tuan88291.testapp.MainActivity.3.1 */
        class C09601 extends ProfileTracker {
            C09601() {
            }

            protected void onCurrentProfileChanged(Profile oldProfile, Profile newProfile) {
                if (MainActivity.this.islogin()) {
                    MainActivity.this.db.insertuser("user", newProfile.getId(), newProfile.getName());
                    new loadingdk(null).execute(new String[]{BuildConfig.VERSION_NAME});
                    return;
                }
                Toast.makeText(MainActivity.this, "\u0110\u00e3 tho\u00e1t", 1).show();
            }
        }

        /* renamed from: com.app.tuan88291.testapp.MainActivity.3.2 */
        class C09612 extends AccessTokenTracker {
            C09612() {
            }

            protected void onCurrentAccessTokenChanged(AccessToken oldToken, AccessToken newToken) {
            }
        }

        C09623() {
        }

        public void onSuccess(LoginResult loginResult) {
            MainActivity.this.profileTracker = new C09601();
            MainActivity.this.accessTokenTracker = new C09612();
        }

        public void onCancel() {
            Toast.makeText(MainActivity.this, "Kh\u00f4ng th\u1ec3 k\u1ebft n\u1ed1i facebook", 1).show();
        }

        public void onError(FacebookException exception) {
        }
    }

    /* renamed from: com.app.tuan88291.testapp.MainActivity.4 */
    class C09634 extends AccessTokenTracker {
        C09634() {
        }

        protected void onCurrentAccessTokenChanged(AccessToken oldToken, AccessToken newToken) {
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(1);
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        getWindow().setFlags(AccessibilityNodeInfoCompat.ACTION_NEXT_HTML_ELEMENT, AccessibilityNodeInfoCompat.ACTION_NEXT_HTML_ELEMENT);
        this.db = new DBhelper(this);
        if (this.db.countuser() > 0) {
            startActivity(new Intent(this, ContentView.class));
            return;
        }
        setContentView((int) C0336R.layout.activity_main);
        this.callbackManager = Factory.create();
        this.login = (LoginButton) findViewById(C0336R.id.login_button);
        this.tit = (TextView) findViewById(C0336R.id.tit);
        this.vote = (Button) findViewById(C0336R.id.vote);
        this.web = (Button) findViewById(C0336R.id.web);
        this.vote.setOnClickListener(new C03251());
        this.web.setOnClickListener(new C03262());
        Typeface face = Typeface.createFromAsset(getAssets(), "font/to.otf");
        this.tit.setTypeface(face);
        this.tit.setTypeface(face);
        this.login.registerCallback(this.callbackManager, new C09623());
        this.accessTokenTracker = new C09634();
    }

    public boolean islogin() {
        return AccessToken.getCurrentAccessToken() != null;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        this.callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
