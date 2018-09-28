package com.app.tuan88291.testapp;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.design.widget.NavigationView.OnNavigationItemSelectedListener;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;
import com.facebook.AccessToken;
import com.facebook.appevents.AppEventsConstants;
import com.facebook.login.LoginManager;
import com.facebook.share.internal.ShareConstants;
import com.google.android.gms.C0598R;
import com.google.android.gms.common.ConnectionResult;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import com.nightonke.boommenu.BoomButtons.ButtonPlaceEnum;
import com.nightonke.boommenu.BoomButtons.OnBMClickListener;
import com.nightonke.boommenu.BoomButtons.TextOutsideCircleButton.Builder;
import com.nightonke.boommenu.BoomMenuButton;
import com.nightonke.boommenu.ButtonEnum;
import com.nightonke.boommenu.Piece.PiecePlaceEnum;
import com.tapadoo.alerter.Alerter;
import com.tapadoo.alerter.OnHideAlertListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import me.wangyuwei.loadingview.C0801R;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import rx.android.BuildConfig;
import rx.internal.operators.OnSubscribeConcatMap;
import rx.internal.schedulers.NewThreadWorker;

public class ContentView extends AppCompatActivity implements OnNavigationItemSelectedListener {
    static final /* synthetic */ boolean $assertionsDisabled;
    public static BoomMenuButton bmb;
    public static EditText filsave;
    public static ImageButton filter;
    public static AutoCompleteTextView mt;
    public static ImageButton search;
    public static ImageButton searchsave;
    public static EditText tim;
    public static TextView title;
    ArrayList<Data_search> adap;
    Adapter_search adapter;
    private ImageView avatar;
    List<Data_filter> data_f;
    List<Data_save> data_s;
    private DBhelper db;
    private RecyclerView list;
    private Button login;
    LinearLayoutManager mLayoutManager;
    Adapter_filter madapter;
    private TextView nickname;
    ProgressDialog pDialog;
    ProgressBar pro;
    Adapter_save svadapter;

    /* renamed from: com.app.tuan88291.testapp.ContentView.1 */
    class C02981 implements OnClickListener {
        C02981() {
        }

        public void onClick(View v) {
            if (ContentView.mt.getText().toString().equals(BuildConfig.VERSION_NAME)) {
                ContentView.search.setVisibility(8);
                ContentView.mt.setVisibility(0);
                ContentView.filter.setVisibility(0);
                ContentView.mt.requestFocus();
                ContentView.this.showkey();
            }
        }
    }

    /* renamed from: com.app.tuan88291.testapp.ContentView.2 */
    class C02992 implements OnClickListener {
        C02992() {
        }

        public void onClick(View v) {
            ContentView.hideSoftKeyboard(ContentView.this);
            new loadingdk(null).execute(new String[]{ContentView.mt.getText().toString().toLowerCase()});
        }
    }

    /* renamed from: com.app.tuan88291.testapp.ContentView.3 */
    class C03003 implements OnEditorActionListener {
        C03003() {
        }

        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId != 6) {
                return false;
            }
            ContentView.hideSoftKeyboard(ContentView.this);
            new loadingdk(null).execute(new String[]{ContentView.mt.getText().toString().toLowerCase()});
            return true;
        }
    }

    /* renamed from: com.app.tuan88291.testapp.ContentView.4 */
    class C03014 implements OnClickListener {
        C03014() {
        }

        public void onClick(View v) {
            ContentView.filsave.setVisibility(0);
            ContentView.filsave.requestFocus();
            ContentView.this.showkey();
        }
    }

    /* renamed from: com.app.tuan88291.testapp.ContentView.5 */
    class C03025 implements OnItemClickListener {
        C03025() {
        }

        public void onItemClick(AdapterView<?> adapterView, View v, int pos, long id) {
            Data_search item = (Data_search) ContentView.this.adap.get(pos);
            new get_bv(null).execute(new String[]{item.idbv});
        }
    }

    /* renamed from: com.app.tuan88291.testapp.ContentView.6 */
    class C03036 implements OnClickListener {
        C03036() {
        }

        public void onClick(View v) {
            if (ContentView.this.isconnect()) {
                Intent scd = new Intent(ContentView.this, Info_user.class);
                scd.putExtra("idfb", ContentView.this.db.idfb());
                scd.putExtra(ShareConstants.WEB_DIALOG_PARAM_NAME, ContentView.this.db.ten());
                ContentView.this.startActivity(scd);
                return;
            }
            Toast.makeText(ContentView.this, "B\u1ea1n ch\u01b0a k\u1ebft n\u1ed1i internet", 0).show();
        }
    }

    /* renamed from: com.app.tuan88291.testapp.ContentView.7 */
    class C03047 implements OnClickListener {
        C03047() {
        }

        public void onClick(View v) {
            if (ContentView.this.isconnect()) {
                Intent scd = new Intent(ContentView.this, Info_user.class);
                scd.putExtra("idfb", ContentView.this.db.idfb());
                scd.putExtra(ShareConstants.WEB_DIALOG_PARAM_NAME, ContentView.this.db.ten());
                ContentView.this.startActivity(scd);
                return;
            }
            Toast.makeText(ContentView.this, "B\u1ea1n ch\u01b0a k\u1ebft n\u1ed1i internet", 0).show();
        }
    }

    /* renamed from: com.app.tuan88291.testapp.ContentView.8 */
    class C03058 implements OnClickListener {
        C03058() {
        }

        public void onClick(View v) {
            ContentView.this.startActivity(new Intent(ContentView.this, MainActivity.class));
        }
    }

    /* renamed from: com.app.tuan88291.testapp.ContentView.9 */
    class C03089 implements Runnable {

        /* renamed from: com.app.tuan88291.testapp.ContentView.9.1 */
        class C03071 implements Runnable {

            /* renamed from: com.app.tuan88291.testapp.ContentView.9.1.2 */
            class C03062 implements OnClickListener {
                C03062() {
                }

                public void onClick(View view) {
                }
            }

            /* renamed from: com.app.tuan88291.testapp.ContentView.9.1.1 */
            class C09521 implements OnHideAlertListener {
                C09521() {
                }

                public void onHide() {
                }
            }

            C03071() {
            }

            public void run() {
                Alerter.create(ContentView.this).setTitle("Xin ch\u00e0o").setText("H\u00e3y \u0111\u0103ng nh\u1eadp \u0111\u1ec3 s\u1eed d\u1ee5ng \u0111\u01b0\u1ee3c nhi\u1ec1u ch\u1ee9c n\u0103ng nh\u00e9").setBackgroundColor(C0336R.color.first_slide_background).setIcon(C0336R.drawable.cooker).setDuration(5000).setOnClickListener(new C03062()).setOnHideListener(new C09521()).show();
            }
        }

        C03089() {
        }

        public void run() {
            ContentView.this.runOnUiThread(new C03071());
        }
    }

    private class get_bv extends AsyncTask<String, Void, String> {
        private get_bv() {
        }

        protected String doInBackground(String... params) {
            String id = params[0];
            HttpClient client = new DefaultHttpClient();
            HttpPost request = new HttpPost("http://cook.audition2.com/search.php");
            List<NameValuePair> nameValuePair = new ArrayList(1);
            nameValuePair.add(new BasicNameValuePair(ShareConstants.WEB_DIALOG_PARAM_ID, "2"));
            nameValuePair.add(new BasicNameValuePair("idfb", ContentView.this.db.idfb()));
            nameValuePair.add(new BasicNameValuePair("idbv", id));
            try {
                request.setEntity(new UrlEncodedFormEntity(nameValuePair, HTTP.UTF_8));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            HttpResponse response = null;
            try {
                response = client.execute(request);
            } catch (ClientProtocolException e2) {
                e2.printStackTrace();
            } catch (IOException e3) {
                e3.printStackTrace();
            }
            String html = BuildConfig.VERSION_NAME;
            InputStream in = null;
            try {
                in = response.getEntity().getContent();
            } catch (IllegalStateException e4) {
                e4.printStackTrace();
            } catch (IOException e32) {
                e32.printStackTrace();
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder str = new StringBuilder();
            while (true) {
                try {
                    String line = reader.readLine();
                    if (line != null) {
                        str.append(line);
                    }
                } catch (IOException e322) {
                    e322.printStackTrace();
                }
                try {
                    break;
                } catch (IOException e3222) {
                    e3222.printStackTrace();
                }
            }
            in.close();
            return str.toString();
        }

        protected void onPreExecute() {
            super.onPreExecute();
            ContentView.this.pDialog = new ProgressDialog(ContentView.this);
            ContentView.this.pDialog.setMessage("\u0110ang load ...");
            ContentView.this.pDialog.setIndeterminate(false);
            ContentView.this.pDialog.setCancelable(true);
            ContentView.this.pDialog.show();
        }

        protected void onPostExecute(String result) {
            JSONException e;
            if (result.equals("over")) {
                Toast.makeText(ContentView.this, "l\u1ed7i kh\u00f4ng x\u00e1c \u0111\u1ecbnh!", 0).show();
            } else {
                try {
                    JSONArray jArray = new JSONArray(result);
                    try {
                        Intent scd = new Intent(ContentView.this, Detail.class);
                        for (int i = 0; i < jArray.length(); i++) {
                            JSONObject json_data = jArray.getJSONObject(i);
                            scd.putExtra("idbv", json_data.getString("idbv"));
                            scd.putExtra("noidung", json_data.getString("noidung"));
                            scd.putExtra("idfb", json_data.getString("idfb"));
                            scd.putExtra("date", json_data.getString("date"));
                            scd.putExtra(ShareConstants.WEB_DIALOG_PARAM_NAME, json_data.getString(ShareConstants.WEB_DIALOG_PARAM_NAME));
                            scd.putExtra("like", json_data.getString("like"));
                            scd.putExtra("tit", json_data.getString("tit"));
                            scd.putExtra("stt", json_data.getString("stt"));
                            scd.putExtra("theloai", json_data.getString("theloai"));
                        }
                        ContentView.this.startActivity(scd);
                    } catch (JSONException e2) {
                        e = e2;
                        JSONArray jSONArray = jArray;
                        e.printStackTrace();
                        ContentView.this.pDialog.dismiss();
                        super.onPostExecute(result);
                    }
                } catch (JSONException e3) {
                    e = e3;
                    e.printStackTrace();
                    ContentView.this.pDialog.dismiss();
                    super.onPostExecute(result);
                }
            }
            ContentView.this.pDialog.dismiss();
            super.onPostExecute(result);
        }

        protected void onProgressUpdate(Void... values) {
        }
    }

    private class gettype extends AsyncTask<String, Void, String> {
        private gettype() {
        }

        protected String doInBackground(String... params) {
            HttpResponse response = null;
            try {
                response = new DefaultHttpClient().execute(new HttpGet("http://cook.audition2.com/type.php"));
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
        }

        protected void onPostExecute(String result) {
            JSONException e;
            if (!result.equals("over")) {
                ContentView.this.clear_f();
                try {
                    JSONArray jArray = new JSONArray(result);
                    int i = 0;
                    while (i < jArray.length()) {
                        try {
                            JSONObject json_data = jArray.getJSONObject(i);
                            ContentView.this.data_f.add(new Data_filter(json_data.getString(ShareConstants.WEB_DIALOG_PARAM_NAME), json_data.getString(ShareConstants.WEB_DIALOG_PARAM_ID)));
                            i++;
                        } catch (JSONException e2) {
                            e = e2;
                            JSONArray jSONArray = jArray;
                        }
                    }
                    ContentView.this.madapter = new Adapter_filter(ContentView.this, ContentView.this.data_f);
                    ContentView.this.list.setAdapter(ContentView.this.madapter);
                    ContentView.this.list.setLayoutManager(ContentView.this.mLayoutManager);
                } catch (JSONException e3) {
                    e = e3;
                    e.printStackTrace();
                    super.onPostExecute(result);
                }
            }
            super.onPostExecute(result);
        }

        protected void onProgressUpdate(Void... values) {
        }
    }

    private class loadingdk extends AsyncTask<String, Void, String> {
        private loadingdk() {
        }

        protected String doInBackground(String... params) {
            String key = params[0];
            HttpClient client = new DefaultHttpClient();
            HttpPost request = new HttpPost("http://cook.audition2.com/search.php");
            List<NameValuePair> nameValuePair = new ArrayList(1);
            nameValuePair.add(new BasicNameValuePair(ShareConstants.WEB_DIALOG_PARAM_ID, AppEventsConstants.EVENT_PARAM_VALUE_YES));
            nameValuePair.add(new BasicNameValuePair(ShareConstants.WEB_DIALOG_PARAM_NAME, key));
            try {
                request.setEntity(new UrlEncodedFormEntity(nameValuePair, HTTP.UTF_8));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            HttpResponse response = null;
            try {
                response = client.execute(request);
            } catch (ClientProtocolException e2) {
                e2.printStackTrace();
            } catch (IOException e3) {
                e3.printStackTrace();
            }
            String html = BuildConfig.VERSION_NAME;
            InputStream in = null;
            try {
                in = response.getEntity().getContent();
            } catch (IllegalStateException e4) {
                e4.printStackTrace();
            } catch (IOException e32) {
                e32.printStackTrace();
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder str = new StringBuilder();
            while (true) {
                try {
                    String line = reader.readLine();
                    if (line != null) {
                        str.append(line);
                    }
                } catch (IOException e322) {
                    e322.printStackTrace();
                }
                try {
                    break;
                } catch (IOException e3222) {
                    e3222.printStackTrace();
                }
            }
            in.close();
            return str.toString();
        }

        protected void onPreExecute() {
            super.onPreExecute();
            ContentView.this.pro.setVisibility(0);
        }

        protected void onPostExecute(String result) {
            JSONException e;
            if (result.equals("over") || result.equals("[]")) {
                Toast.makeText(ContentView.this, "Kh\u00f4ng t\u00ecm th\u1ea5y", 0).show();
            } else {
                ContentView.this.clearData();
                try {
                    JSONArray jArray = new JSONArray(result);
                    try {
                        int dem;
                        if (jArray.length() > 10) {
                            dem = 10;
                        } else {
                            dem = jArray.length();
                        }
                        for (int i = 0; i < dem; i++) {
                            JSONObject json_data = jArray.getJSONObject(i);
                            ContentView.this.adap.add(new Data_search(json_data.getString("tit"), json_data.getString("idbv")));
                        }
                        ContentView.this.adapter = new Adapter_search(ContentView.this, C0336R.layout.custom_search, ContentView.this.adap);
                        ContentView.mt.setAdapter(ContentView.this.adapter);
                        ContentView.this.adapter.notifyDataSetChanged();
                        ContentView.mt.showDropDown();
                    } catch (JSONException e2) {
                        e = e2;
                        JSONArray jSONArray = jArray;
                        e.printStackTrace();
                        ContentView.this.pro.setVisibility(8);
                        super.onPostExecute(result);
                    }
                } catch (JSONException e3) {
                    e = e3;
                    e.printStackTrace();
                    ContentView.this.pro.setVisibility(8);
                    super.onPostExecute(result);
                }
            }
            ContentView.this.pro.setVisibility(8);
            super.onPostExecute(result);
        }

        protected void onProgressUpdate(Void... values) {
        }
    }

    /* renamed from: com.app.tuan88291.testapp.ContentView.14 */
    class AnonymousClass14 implements RecyclerItemClickListener.OnItemClickListener {
        final /* synthetic */ Dialog val$dialog;

        AnonymousClass14(Dialog dialog) {
            this.val$dialog = dialog;
        }

        public void onItemClick(View view, int position) {
            Home hm = new Home();
            Home.idtype = ((Data_filter) ContentView.this.data_f.get(position)).id;
            Home.gettype();
            ContentView.puttitle(((Data_filter) ContentView.this.data_f.get(position)).name);
            this.val$dialog.dismiss();
        }

        public void onLongItemClick(View view, int position) {
        }
    }

    static {
        $assertionsDisabled = !ContentView.class.desiredAssertionStatus();
    }

    public ContentView() {
        this.adap = new ArrayList();
        this.data_f = new ArrayList();
        this.data_s = new ArrayList();
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) C0336R.layout.content);
        Toolbar toolbar = (Toolbar) findViewById(C0598R.id.toolbar);
        setSupportActionBar(toolbar);
        this.db = new DBhelper(this);
        if (this.db.countuser() <= 0) {
            showart();
        }
        SharedPreferences share = getSharedPreferences("scook", 0);
        if (share.getString("stt", BuildConfig.VERSION_NAME).equals(BuildConfig.VERSION_NAME)) {
            Editor editor = share.edit();
            editor.putString("stt", "yes");
            editor.commit();
        }
        bmb = (BoomMenuButton) findViewById(C0336R.id.bmb);
        if ($assertionsDisabled || bmb != null) {
            bmb.setButtonEnum(ButtonEnum.TextOutsideCircle);
            bmb.setPiecePlaceEnum(PiecePlaceEnum.DOT_4_1);
            bmb.setButtonPlaceEnum(ButtonPlaceEnum.SC_4_1);
            bmb.setShowDelay(150);
            bmb.setShowDuration(250);
            for (int i = 0; i < bmb.getPiecePlaceEnum().pieceNumber(); i++) {
                addBuilder();
            }
            search = (ImageButton) findViewById(C0336R.id.search);
            filter = (ImageButton) findViewById(C0336R.id.filter);
            this.pro = (ProgressBar) findViewById(C0336R.id.pro);
            title = (TextView) findViewById(C0801R.id.title);
            mt = (AutoCompleteTextView) findViewById(C0336R.id.key);
            filsave = (EditText) findViewById(C0336R.id.filsv);
            searchsave = (ImageButton) findViewById(C0336R.id.searchsave);
            mt.setThreshold(1);
            search.setOnClickListener(new C02981());
            filter.setOnClickListener(new C02992());
            mt.setOnEditorActionListener(new C03003());
            searchsave.setOnClickListener(new C03014());
            mt.setOnItemClickListener(new C03025());
            DrawerLayout drawer = (DrawerLayout) findViewById(C0336R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, C0336R.string.navigation_drawer_open, C0336R.string.navigation_drawer_close);
            drawer.setDrawerListener(toggle);
            toggle.syncState();
            NavigationView navigationView = (NavigationView) findViewById(C0336R.id.nav_view);
            View hView = navigationView.getHeaderView(0);
            if (this.db.countuser() <= 0) {
                navigationView.getMenu().getItem(3).setVisible(false);
            }
            this.nickname = (TextView) hView.findViewById(C0336R.id.nickname);
            this.avatar = (ImageView) hView.findViewById(C0336R.id.avatar);
            this.login = (Button) hView.findViewById(C0336R.id.login);
            if (this.db.countuser() > 0) {
                this.nickname.setText(this.db.ten());
                this.nickname.setSelected(true);
                UrlImageViewHelper.setUrlDrawable(this.avatar, "http://graph.facebook.com/" + this.db.idfb() + "/picture?type=large");
                this.avatar.setOnClickListener(new C03036());
                this.nickname.setOnClickListener(new C03047());
            } else {
                this.login.setVisibility(0);
                this.avatar.setImageResource(C0336R.drawable.userdefault);
                this.nickname.setVisibility(8);
                this.login.setOnClickListener(new C03058());
            }
            navigationView.setNavigationItemSelectedListener(this);
            Fragment fragment;
            FragmentTransaction ft;
            if (isconnect()) {
                fragment = new Home();
                ft = getSupportFragmentManager().beginTransaction();
                ft.replace(C0336R.id.containerView, fragment);
                ft.commit();
                title.setText("Scook");
                return;
            }
            fragment = new Save();
            ft = getSupportFragmentManager().beginTransaction();
            ft.replace(C0336R.id.containerView, fragment);
            ft.commit();
            title.setText("M\u00f3n \u0103n \u0111\u00e3 l\u01b0u");
            return;
        }
        throw new AssertionError();
    }

    public void showkey() {
        ((InputMethodManager) getSystemService("input_method")).toggleSoftInput(2, 0);
    }

    public void showart() {
        new Handler().postDelayed(new C03089(), 5000);
    }

    public static void hideSoftKeyboard(Activity activity) {
        ((InputMethodManager) activity.getSystemService("input_method")).hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

    public static void puttitle(String tit) {
        title.setText(tit);
    }

    public void clearData() {
        if (!this.adap.isEmpty()) {
            this.adap.clear();
            this.adapter.notifyDataSetChanged();
        }
    }

    public void clear_f() {
        if (!this.data_f.isEmpty()) {
            this.data_f.clear();
            this.madapter.notifyDataSetChanged();
        }
    }

    public static void setmt() {
        mt.setText(BuildConfig.VERSION_NAME);
        mt.setVisibility(8);
        filter.setVisibility(8);
        search.setVisibility(0);
    }

    public static void hidebt() {
        search.setVisibility(8);
        mt.setVisibility(8);
    }

    public static void showsave() {
        searchsave.setVisibility(0);
    }

    public static void hidenfil() {
        filsave.setVisibility(8);
    }

    public static void hidenall() {
        filsave.setText(BuildConfig.VERSION_NAME);
        filsave.setVisibility(8);
        searchsave.setVisibility(8);
    }

    public static void setgone(int so) {
        if (so == 0) {
            bmb.setVisibility(8);
        } else {
            bmb.setVisibility(0);
        }
    }

    public void addTextListener() {
        tim.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence query, int start, int before, int count) {
                query = query.toString().toLowerCase();
                List<Data_filter> data_f1 = new ArrayList();
                for (int i = 0; i < ContentView.this.data_f.size(); i++) {
                    if (((Data_filter) ContentView.this.data_f.get(i)).name.toLowerCase().contains(query)) {
                        data_f1.add(new Data_filter(((Data_filter) ContentView.this.data_f.get(i)).name, ((Data_filter) ContentView.this.data_f.get(i)).id));
                    }
                }
                ContentView.this.list.setLayoutManager(new LinearLayoutManager(ContentView.this));
                ContentView.this.madapter = new Adapter_filter(ContentView.this, data_f1);
                ContentView.this.list.setAdapter(ContentView.this.madapter);
                ContentView.this.madapter.notifyDataSetChanged();
            }
        });
    }

    public void filtersave() {
        filsave.addTextChangedListener(new TextWatcher() {
            Save svg;

            {
                this.svg = new Save();
            }

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence query, int start, int before, int count) {
                query = query.toString().toLowerCase();
                List<Data_save> datta = new ArrayList();
                int i = 0;
                while (true) {
                    Save save = this.svg;
                    if (i < Save.data.size()) {
                        save = this.svg;
                        if (((Data_save) Save.data.get(i)).getTit().toLowerCase().contains(query)) {
                            save = this.svg;
                            String tit = ((Data_save) Save.data.get(i)).getTit();
                            save = this.svg;
                            String noidung = ((Data_save) Save.data.get(i)).getNoidung();
                            save = this.svg;
                            String theloai = ((Data_save) Save.data.get(i)).getTheloai();
                            save = this.svg;
                            datta.add(new Data_save(tit, noidung, theloai, ((Data_save) Save.data.get(i)).getIdbv()));
                        }
                        i++;
                    } else {
                        save = this.svg;
                        RecyclerView recyclerView = Save.list;
                        Save save2 = this.svg;
                        recyclerView.setLayoutManager(Save.mLayoutManager);
                        save = this.svg;
                        save2 = this.svg;
                        Save.mAdapter = new Adapter_save(Save.thiscontext, datta);
                        save = this.svg;
                        recyclerView = Save.list;
                        save2 = this.svg;
                        recyclerView.setAdapter(Save.mAdapter);
                        save = this.svg;
                        Save.mAdapter.notifyDataSetChanged();
                        return;
                    }
                }
            }
        });
    }

    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(C0336R.id.drawer_layout);
        if (drawer.isDrawerOpen((int) GravityCompat.START)) {
            drawer.closeDrawer((int) GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void addBuilder() {
        bmb.addBuilder(new Builder().normalImageRes(Data_boom.getImageResource()).normalText(Data_boom.getdongtext()).normalTextColor(-1).pieceColor(-1).listener(new OnBMClickListener() {

            /* renamed from: com.app.tuan88291.testapp.ContentView.12.1 */
            class C02911 implements Runnable {

                /* renamed from: com.app.tuan88291.testapp.ContentView.12.1.1 */
                class C02901 implements Runnable {
                    C02901() {
                    }

                    public void run() {
                        if (ContentView.this.db.countuser() > 0) {
                            Intent scd = new Intent(ContentView.this, New_post.class);
                            scd.putExtra("idfb", ContentView.this.db.idfb());
                            scd.putExtra(ShareConstants.WEB_DIALOG_PARAM_NAME, ContentView.this.db.ten());
                            ContentView.this.startActivity(scd);
                            return;
                        }
                        ContentView.this.dangnhap();
                    }
                }

                C02911() {
                }

                public void run() {
                    ContentView.this.runOnUiThread(new C02901());
                }
            }

            /* renamed from: com.app.tuan88291.testapp.ContentView.12.2 */
            class C02932 implements Runnable {

                /* renamed from: com.app.tuan88291.testapp.ContentView.12.2.1 */
                class C02921 implements Runnable {
                    C02921() {
                    }

                    public void run() {
                        if (ContentView.this.db.countuser() > 0) {
                            Intent scd = new Intent(ContentView.this, Info_user.class);
                            scd.putExtra("idfb", ContentView.this.db.idfb());
                            scd.putExtra(ShareConstants.WEB_DIALOG_PARAM_NAME, ContentView.this.db.ten());
                            ContentView.this.startActivity(scd);
                            return;
                        }
                        ContentView.this.dangnhap();
                    }
                }

                C02932() {
                }

                public void run() {
                    ContentView.this.runOnUiThread(new C02921());
                }
            }

            /* renamed from: com.app.tuan88291.testapp.ContentView.12.3 */
            class C02953 implements Runnable {

                /* renamed from: com.app.tuan88291.testapp.ContentView.12.3.1 */
                class C02941 implements Runnable {
                    C02941() {
                    }

                    public void run() {
                        Fragment fsave = new Save();
                        FragmentTransaction ftsave = ContentView.this.getSupportFragmentManager().beginTransaction();
                        ftsave.replace(C0336R.id.containerView, fsave);
                        ftsave.commit();
                    }
                }

                C02953() {
                }

                public void run() {
                    ContentView.this.runOnUiThread(new C02941());
                }
            }

            /* renamed from: com.app.tuan88291.testapp.ContentView.12.4 */
            class C02974 implements Runnable {

                /* renamed from: com.app.tuan88291.testapp.ContentView.12.4.1 */
                class C02961 implements Runnable {
                    C02961() {
                    }

                    public void run() {
                        ContentView.this.chuyenmuc();
                    }
                }

                C02974() {
                }

                public void run() {
                    ContentView.this.runOnUiThread(new C02961());
                }
            }

            public void onBoomButtonClick(int index) {
                switch (index) {
                    case NewThreadWorker.PURGE_FREQUENCY:
                        new Handler().postDelayed(new C02911(), 450);
                    case OnSubscribeConcatMap.BOUNDARY /*1*/:
                        new Handler().postDelayed(new C02932(), 450);
                    case OnSubscribeConcatMap.END /*2*/:
                        new Handler().postDelayed(new C02953(), 450);
                    case ConnectionResult.SERVICE_DISABLED /*3*/:
                        new Handler().postDelayed(new C02974(), 450);
                    default:
                }
            }
        }));
    }

    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case C0801R.id.home /*2131623940*/:
                if (!isconnect()) {
                    Toast.makeText(this, "M\u00e1y b\u1ea1n ch\u01b0a b\u1eadt k\u1ebft n\u1ed1i m\u1ea1ng!", 0).show();
                    break;
                }
                Home hm = new Home();
                Home.idtype = BuildConfig.VERSION_NAME;
                Home.page = 1;
                Home.pastVisiblesItems = 0;
                Fragment fragment = new Home();
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(C0336R.id.containerView, fragment);
                ft.commit();
                break;
            case C0336R.id.exit /*2131624287*/:
                if (!isconnect()) {
                    Toast.makeText(this, "B\u1ea1n ph\u1ea3i k\u1ebft n\u1ed1i internet \u0111\u1ec3 tho\u00e1t", 0).show();
                    break;
                }
                logout();
                break;
            case C0336R.id.like /*2131624293*/:
                if (!isconnect()) {
                    Toast.makeText(this, "B\u1ea1n ch\u01b0a k\u1ebft n\u1ed1i internet", 0).show();
                    break;
                }
                if (this.db.countuser() <= 0) {
                    dangnhap();
                    break;
                }
                My_like ml = new My_like();
                My_like.page = 1;
                Fragment mylike = new My_like();
                FragmentTransaction mylik = getSupportFragmentManager().beginTransaction();
                mylik.replace(C0336R.id.containerView, mylike);
                mylik.commit();
                break;
            case C0336R.id.my /*2131624387*/:
                Fragment fsave = new Save();
                FragmentTransaction ftsave = getSupportFragmentManager().beginTransaction();
                ftsave.replace(C0336R.id.containerView, fsave);
                ftsave.commit();
                break;
            case C0336R.id.tg /*2131624388*/:
                Fragment ftg = new About();
                FragmentTransaction ftgtg = getSupportFragmentManager().beginTransaction();
                ftgtg.replace(C0336R.id.containerView, ftg);
                ftgtg.commit();
                break;
            case C0336R.id.nav_share /*2131624389*/:
                Intent sharingIntent = new Intent("android.intent.action.SEND");
                sharingIntent.setType(HTTP.PLAIN_TEXT_TYPE);
                sharingIntent.putExtra("android.intent.extra.SUBJECT", "\u1ee8ng d\u1ee5ng h\u01b0\u1edbng d\u1eabn c\u00e1ch l\u00e0m h\u00e0ng ng\u00e0n m\u00f3n \u0103n ngon");
                sharingIntent.putExtra("android.intent.extra.TEXT", "T\u1ea3i v\u1ec1 m\u00e1y t\u1ea1i \u0111\u00e2y: https://play.google.com/store/apps/details?id=com.app.tuan88291.testapp");
                startActivity(Intent.createChooser(sharingIntent, "Share v\u1edbi :"));
                break;
        }
        ((DrawerLayout) findViewById(C0336R.id.drawer_layout)).closeDrawer(GravityCompat.START);
        return true;
    }

    public void dangnhap() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage((CharSequence) "B\u1ea1n ph\u1ea3i \u0111\u0103ng nh\u1eadp \u0111\u1ec3 th\u1ef1c hi\u1ec7n ch\u1ee9c n\u0103ng n\u00e0y:");
        alertDialogBuilder.setPositiveButton((CharSequence) "\u0110\u0103ng nh\u1eadp", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                arg0.dismiss();
                ContentView.this.startActivity(new Intent(ContentView.this, MainActivity.class));
            }
        });
        alertDialogBuilder.create().show();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == 4 && event.getRepeatCount() == 0) {
            arlet();
        }
        return super.onKeyDown(keyCode, event);
    }

    public void chuyenmuc() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(C0336R.layout.filter_layout);
        dialog.setTitle("Ch\u1ecdn chuy\u00ean m\u1ee5c:");
        dialog.getWindow().setTitleColor(getResources().getColor(C0336R.color.gren));
        dialog.getWindow().setBackgroundDrawableResource(C0336R.color.colorPrimary);
        tim = (EditText) dialog.findViewById(C0336R.id.search);
        this.list = (RecyclerView) dialog.findViewById(C0336R.id.list);
        this.mLayoutManager = new LinearLayoutManager(this);
        new gettype().execute(new String[]{BuildConfig.VERSION_NAME});
        addTextListener();
        this.list.addOnItemTouchListener(new RecyclerItemClickListener(this, this.list, new AnonymousClass14(dialog)));
        dialog.show();
    }

    public void arlet() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage((CharSequence) "B\u1ea1n c\u00f3 mu\u1ed1n tho\u00e1t kh\u1ecfi \u1ee9ng d\u1ee5ng kh\u00f4ng?");
        alertDialogBuilder.setPositiveButton((CharSequence) "C\u00f3", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                ContentView.this.moveTaskToBack(true);
                System.exit(0);
            }
        });
        alertDialogBuilder.setNegativeButton((CharSequence) "Kh\u00f4ng", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialogBuilder.create().show();
    }

    public void logout() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage((CharSequence) "B\u1ea1n c\u00f3 mu\u1ed1n \u0111\u0103ng xu\u1ea5t kh\u00f4ng?");
        alertDialogBuilder.setPositiveButton((CharSequence) "C\u00f3", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                if (AccessToken.getCurrentAccessToken() != null) {
                    LoginManager.getInstance().logOut();
                }
                ContentView.this.db.delete("user");
                ContentView.this.startActivity(new Intent(ContentView.this, MainActivity.class));
            }
        });
        alertDialogBuilder.setNegativeButton((CharSequence) "Kh\u00f4ng", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialogBuilder.create().show();
    }

    public boolean isconnect() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService("connectivity");
        if (VERSION.SDK_INT >= 21) {
            for (Network mNetwork : connectivityManager.getAllNetworks()) {
                if (connectivityManager.getNetworkInfo(mNetwork).getState().equals(State.CONNECTED)) {
                    return true;
                }
            }
        } else if (connectivityManager != null) {
            NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
            if (info != null) {
                for (NetworkInfo anInfo : info) {
                    if (anInfo.getState() == State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
