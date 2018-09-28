package com.app.tuan88291.testapp;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.facebook.appevents.AppEventsConstants;
import com.facebook.internal.AnalyticsEvents;
import com.facebook.internal.NativeProtocol;
import com.facebook.share.internal.ShareConstants;
import com.gigamole.navigationtabbar.C0597R;
import com.google.android.gms.C0598R;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import de.hdodenhof.circleimageview.CircleImageView;
import devlight.io.library.ntb.NavigationTabBar;
import devlight.io.library.ntb.NavigationTabBar.Model;
import devlight.io.library.ntb.NavigationTabBar.Model.Builder;
import devlight.io.library.ntb.NavigationTabBar.OnTabBarSelectedIndexListener;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import me.wangyuwei.loadingview.C0801R;
import me.wangyuwei.loadingview.LoadingView;
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

public class Detail extends AppCompatActivity implements OnClickListener {
    public static EditText boxsend;
    private Adapter_comment_1 adapter_cmt;
    private AppBarLayout appbar;
    private TextView arl;
    private CircleImageView avatar;
    List<Data_comment_1> data_cmt;
    List<Data_home> data_tl;
    private String date;
    DBhelper db;
    private ImageButton del;
    private ImageButton edit;
    private Gallery gallery;
    private String idbv;
    private String idfb;
    LinearLayoutManager layoutngang;
    LoadingView ldv;
    private String like;
    ArrayList<Data_detail> list;
    private RecyclerView listcm;
    private RecyclerView listcmt;
    private RecyclerView listtl;
    private boolean loading;
    private Adapter_home mAdapter;
    LinearLayoutManager mLayoutManager;
    private String name;
    private TextView namefood;
    private ImageButton next;
    private TextView ngay;
    private String noidung;
    ProgressDialog pDialog;
    private int page;
    int pastVisiblesItems;
    private ProgressBar pro;
    private ImageButton save;
    private ImageButton send;
    private String stt;
    private TextView ten;
    private String tentheloai;
    private TextView tentl;
    private String theloai;
    private ImageButton thich;
    private TextView tieude;
    private String tit;
    private CollapsingToolbarLayout titcus;
    Toolbar toolbar;
    private TextView total;
    private TextView vanban;

    /* renamed from: com.app.tuan88291.testapp.Detail.1 */
    class C03111 implements OnClickListener {
        C03111() {
        }

        public void onClick(View v) {
            Detail.this.finish();
        }
    }

    /* renamed from: com.app.tuan88291.testapp.Detail.2 */
    class C03122 implements OnItemClickListener {
        C03122() {
        }

        public void onItemClick(AdapterView<?> adapterView, View v, int position, long id) {
            Data_detail item = (Data_detail) Detail.this.list.get(position);
            Detail.this.show(item.getUrl(), item.getIdbv(), item.getType(), Detail.this);
        }
    }

    /* renamed from: com.app.tuan88291.testapp.Detail.4 */
    class C03134 implements Runnable {
        final /* synthetic */ NavigationTabBar val$navigationTabBar;

        C03134(NavigationTabBar navigationTabBar) {
            this.val$navigationTabBar = navigationTabBar;
        }

        public void run() {
            View viewPager = Detail.this.findViewById(C0336R.id.vp_horizontal_ntb);
            ((MarginLayoutParams) viewPager.getLayoutParams()).topMargin = (int) (-this.val$navigationTabBar.getBadgeMargin());
            viewPager.requestLayout();
        }
    }

    /* renamed from: com.app.tuan88291.testapp.Detail.6 */
    class C03146 implements Runnable {
        final /* synthetic */ Model val$model;

        C03146(Model model) {
            this.val$model = model;
        }

        public void run() {
            try {
                String total = (String) new totalcmt(null).execute(new String[]{Detail.this.idbv}).get();
                if (this.val$model.isBadgeShowed()) {
                    this.val$model.updateBadgeTitle(total);
                    return;
                }
                this.val$model.setBadgeTitle(total);
                this.val$model.showBadge();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e2) {
                e2.printStackTrace();
            }
        }
    }

    /* renamed from: com.app.tuan88291.testapp.Detail.7 */
    class C03157 implements OnClickListener {
        final /* synthetic */ Dialog val$dialog;
        final /* synthetic */ EditText val$nd;
        final /* synthetic */ EditText val$titt;

        C03157(Dialog dialog, EditText editText, EditText editText2) {
            this.val$dialog = dialog;
            this.val$titt = editText;
            this.val$nd = editText2;
        }

        public void onClick(View v) {
            this.val$dialog.dismiss();
            new edit(null).execute(new String[]{this.val$titt.getText().toString(), this.val$nd.getText().toString(), Detail.this.idbv});
        }
    }

    /* renamed from: com.app.tuan88291.testapp.Detail.8 */
    class C03168 implements OnClickListener {
        final /* synthetic */ Dialog val$dialog;

        C03168(Dialog dialog) {
            this.val$dialog = dialog;
        }

        public void onClick(View v) {
            this.val$dialog.dismiss();
        }
    }

    /* renamed from: com.app.tuan88291.testapp.Detail.9 */
    class C03179 implements DialogInterface.OnClickListener {
        C03179() {
        }

        public void onClick(DialogInterface arg0, int arg1) {
            new xoa(null).execute(new String[]{Detail.this.idbv});
        }
    }

    private class cmt extends AsyncTask<String, Void, String> {
        private cmt() {
        }

        protected String doInBackground(String... params) {
            String ct = params[0];
            HttpClient client = new DefaultHttpClient();
            HttpPost request = new HttpPost("http://cook.audition2.com/cmt.php");
            List<NameValuePair> nameValuePair = new ArrayList(1);
            nameValuePair.add(new BasicNameValuePair(ShareConstants.WEB_DIALOG_PARAM_ID, AppEventsConstants.EVENT_PARAM_VALUE_YES));
            nameValuePair.add(new BasicNameValuePair("idfb", Detail.this.db.idfb()));
            nameValuePair.add(new BasicNameValuePair(ShareConstants.WEB_DIALOG_PARAM_NAME, Detail.this.db.ten()));
            nameValuePair.add(new BasicNameValuePair("text", ct));
            nameValuePair.add(new BasicNameValuePair("idbv", Detail.this.idbv));
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
            Detail.this.pro.setVisibility(0);
        }

        protected void onPostExecute(String result) {
            Detail.this.clearData();
            new loadcmt(null).execute(new String[]{BuildConfig.VERSION_NAME});
            Detail.boxsend.setText(BuildConfig.VERSION_NAME);
            Detail.this.pro.setVisibility(8);
            super.onPostExecute(result);
        }

        protected void onProgressUpdate(Void... values) {
        }
    }

    private class edit extends AsyncTask<String, Void, String> {
        private edit() {
        }

        protected String doInBackground(String... params) {
            String idbv = params[2];
            String tit = params[0];
            String nd = params[1];
            HttpClient client = new DefaultHttpClient();
            HttpPost request = new HttpPost("http://cook.audition2.com/post.php");
            List<NameValuePair> nameValuePair = new ArrayList(1);
            nameValuePair.add(new BasicNameValuePair(ShareConstants.WEB_DIALOG_PARAM_ID, "9"));
            nameValuePair.add(new BasicNameValuePair("idbv", idbv));
            nameValuePair.add(new BasicNameValuePair("tit", tit));
            nameValuePair.add(new BasicNameValuePair("nd", nd));
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
            Detail.this.pDialog = new ProgressDialog(Detail.this);
            Detail.this.pDialog.setMessage("\u0110ang l\u01b0u ...");
            Detail.this.pDialog.setIndeterminate(false);
            Detail.this.pDialog.setCancelable(true);
            Detail.this.pDialog.show();
        }

        protected void onPostExecute(String result) {
            Toast.makeText(Detail.this, "\u0110\u00e3 l\u01b0u b\u00e0i vi\u1ebft, load l\u1ea1i \u0111\u1ec3 xem s\u1ef1 thay \u0111\u1ed5i", 0).show();
            Detail.this.pDialog.dismiss();
            super.onPostExecute(result);
        }

        protected void onProgressUpdate(Void... values) {
        }
    }

    private class like extends AsyncTask<String, Void, String> {
        private like() {
        }

        protected String doInBackground(String... params) {
            String idfr = params[0];
            HttpClient client = new DefaultHttpClient();
            HttpPost request = new HttpPost("http://cook.audition2.com/like.php");
            List<NameValuePair> nameValuePair = new ArrayList(1);
            nameValuePair.add(new BasicNameValuePair(ShareConstants.WEB_DIALOG_PARAM_ID, AppEventsConstants.EVENT_PARAM_VALUE_YES));
            nameValuePair.add(new BasicNameValuePair("idfb", Detail.this.db.idfb()));
            nameValuePair.add(new BasicNameValuePair("idbv", idfr));
            try {
                request.setEntity(new UrlEncodedFormEntity(nameValuePair));
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
        }

        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }

        protected void onProgressUpdate(Void... values) {
        }
    }

    private class loadcmt extends AsyncTask<String, Void, String> {
        private loadcmt() {
        }

        protected String doInBackground(String... params) {
            HttpResponse response = null;
            try {
                response = new DefaultHttpClient().execute(new HttpGet("http://cook.audition2.com/showcmt.php?idbv=" + Detail.this.idbv + "&page=" + Detail.this.page));
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
            Detail.this.pro.setVisibility(0);
        }

        protected void onPostExecute(String result) {
            JSONException e;
            Log.d("lodddddddddd", "page: " + Detail.this.page);
            Log.d("lodddddddddd", "idbvv: " + Detail.this.idbv);
            if (!result.equals("over")) {
                Detail.this.arl.setText(BuildConfig.VERSION_NAME);
                try {
                    JSONArray jArray = new JSONArray(result);
                    int i = 0;
                    while (i < jArray.length()) {
                        try {
                            JSONObject json_data = jArray.getJSONObject(i);
                            Data_comment_1 dth = new Data_comment_1();
                            dth.idbv = json_data.getString("idbv");
                            dth.noidung = json_data.getString("noidung");
                            dth.idfb = json_data.getString("idfb");
                            dth.type = json_data.getString(ShareConstants.MEDIA_TYPE);
                            dth.date = json_data.getString("date");
                            dth.name = json_data.getString(ShareConstants.WEB_DIALOG_PARAM_NAME);
                            dth.stt = json_data.getString("stt");
                            Detail.this.data_cmt.add(dth);
                            i++;
                        } catch (JSONException e2) {
                            e = e2;
                            JSONArray jSONArray = jArray;
                        }
                    }
                    Detail.this.adapter_cmt = new Adapter_comment_1(Detail.this, Detail.this.data_cmt);
                    Detail.this.listcmt.setAdapter(Detail.this.adapter_cmt);
                    Detail.this.listcmt.setLayoutManager(Detail.this.mLayoutManager);
                    Detail.this.loading = true;
                    Detail.this.listcmt.scrollToPosition(Detail.this.pastVisiblesItems);
                } catch (JSONException e3) {
                    e = e3;
                    e.printStackTrace();
                    Detail.this.pro.setVisibility(8);
                    super.onPostExecute(result);
                }
            } else if (Detail.this.data_cmt.size() == 0) {
                Detail.this.arl.setText("Ch\u01b0a c\u00f3 comment n\u00e0o!");
            }
            Detail.this.pro.setVisibility(8);
            super.onPostExecute(result);
        }

        protected void onProgressUpdate(Void... values) {
        }
    }

    private class loadimg extends AsyncTask<String, Void, String> {

        /* renamed from: com.app.tuan88291.testapp.Detail.loadimg.1 */
        class C15891 extends SimpleTarget<Bitmap> {
            final /* synthetic */ String val$idbv;

            C15891(String str) {
                this.val$idbv = str;
            }

            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                Detail.this.db.saveimg("img", Detail.this.getStringImage(resource), ShareConstants.WEB_DIALOG_PARAM_LINK, this.val$idbv);
            }
        }

        /* renamed from: com.app.tuan88291.testapp.Detail.loadimg.2 */
        class C15902 extends SimpleTarget<Bitmap> {
            final /* synthetic */ String val$idbv;

            C15902(String str) {
                this.val$idbv = str;
            }

            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                Detail.this.db.saveimg("img", Detail.this.getStringImage(resource), "img", this.val$idbv);
            }
        }

        private loadimg() {
        }

        protected String doInBackground(String... params) {
            HttpResponse response = null;
            try {
                response = new DefaultHttpClient().execute(new HttpGet("http://cook.audition2.com/showimg.php?idbv=" + params[0]));
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
            Detail.this.pDialog = new ProgressDialog(Detail.this);
            Detail.this.pDialog.setMessage("\u0110ang l\u01b0u ...");
            Detail.this.pDialog.setIndeterminate(false);
            Detail.this.pDialog.setCancelable(true);
            Detail.this.pDialog.show();
        }

        protected void onPostExecute(String result) {
            JSONException e;
            if (!result.equals("over")) {
                try {
                    JSONArray jArray = new JSONArray(result);
                    int i = 0;
                    while (i < jArray.length()) {
                        try {
                            JSONObject json_data = jArray.getJSONObject(i);
                            if (json_data.getString(ShareConstants.MEDIA_TYPE).equals(ShareConstants.WEB_DIALOG_PARAM_LINK)) {
                                Glide.with(Detail.this).load(json_data.getString(NativeProtocol.WEB_DIALOG_URL)).asBitmap().into(new C15891(json_data.getString("idbv")));
                            } else if (json_data.getString(ShareConstants.MEDIA_TYPE).equals("img")) {
                                Glide.with(Detail.this).load("http://cook.audition2.com/photos/" + json_data.getString("idbv") + "/" + json_data.getString(NativeProtocol.WEB_DIALOG_URL)).asBitmap().into(new C15902(json_data.getString("idbv")));
                            } else if (json_data.getString(ShareConstants.MEDIA_TYPE).equals(AnalyticsEvents.PARAMETER_SHARE_DIALOG_CONTENT_VIDEO)) {
                                Detail.this.db.saveimg("img", json_data.getString(NativeProtocol.WEB_DIALOG_URL), AnalyticsEvents.PARAMETER_SHARE_DIALOG_CONTENT_VIDEO, json_data.getString("idbv"));
                            }
                            i++;
                        } catch (JSONException e2) {
                            e = e2;
                            JSONArray jSONArray = jArray;
                        }
                    }
                } catch (JSONException e3) {
                    e = e3;
                    e.printStackTrace();
                    Toast.makeText(Detail.this, "\u0110\u00e3 l\u01b0u m\u00f3n \u0103n th\u00e0nh c\u00f4ng!", 0).show();
                    Detail.this.pDialog.dismiss();
                    super.onPostExecute(result);
                }
            }
            Toast.makeText(Detail.this, "\u0110\u00e3 l\u01b0u m\u00f3n \u0103n th\u00e0nh c\u00f4ng!", 0).show();
            Detail.this.pDialog.dismiss();
            super.onPostExecute(result);
        }

        protected void onProgressUpdate(Void... values) {
        }
    }

    private class loadingdk extends AsyncTask<String, Void, String> {
        private loadingdk() {
        }

        protected String doInBackground(String... params) {
            HttpResponse response = null;
            try {
                response = new DefaultHttpClient().execute(new HttpGet("http://cook.audition2.com/showimg.php?idbv=" + Detail.this.idbv));
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
            Detail.this.ldv.start();
        }

        protected void onPostExecute(String result) {
            JSONException e;
            if (!result.equals("over")) {
                try {
                    JSONArray jArray = new JSONArray(result);
                    int i = 0;
                    while (i < jArray.length()) {
                        try {
                            JSONObject json_data = jArray.getJSONObject(i);
                            Detail.this.list.add(new Data_detail(json_data.getString(NativeProtocol.WEB_DIALOG_URL), json_data.getString("idbv"), json_data.getString(ShareConstants.MEDIA_TYPE)));
                            i++;
                        } catch (JSONException e2) {
                            e = e2;
                            JSONArray jSONArray = jArray;
                        }
                    }
                    Detail.this.gallery.setAdapter(new Adapter_detail(Detail.this, C0336R.layout.custom_detail_layout, Detail.this.list));
                } catch (JSONException e3) {
                    e = e3;
                    e.printStackTrace();
                    if (Detail.this.list.size() > 1) {
                        Detail.this.next.setVisibility(0);
                    }
                    Detail.this.ldv.stop();
                    super.onPostExecute(result);
                }
            }
            if (Detail.this.list.size() > 1) {
                Detail.this.next.setVisibility(0);
            }
            Detail.this.ldv.stop();
            super.onPostExecute(result);
        }

        protected void onProgressUpdate(Void... values) {
        }
    }

    private class loadtheloai extends AsyncTask<String, Void, String> {
        private loadtheloai() {
        }

        protected String doInBackground(String... params) {
            HttpResponse response = null;
            try {
                response = new DefaultHttpClient().execute(new HttpGet("http://cook.audition2.com/theloai.php?tl=" + params[0] + "&idfb=" + Detail.this.db.idfb()));
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
            Detail.this.clearDatatl();
            if (!result.equals("over")) {
                try {
                    JSONArray jSONArray = new JSONArray(result);
                    int i = 0;
                    while (true) {
                        try {
                            if (i >= jSONArray.length()) {
                                break;
                            }
                            JSONObject json_data = jSONArray.getJSONObject(i);
                            List list = Detail.this.data_tl;
                            List list2 = list;
                            list2.add(new Data_home(json_data.getString("idbv"), json_data.getString("noidung"), json_data.getString("idfb"), json_data.getString(ShareConstants.MEDIA_TYPE), json_data.getString("date"), json_data.getString(ShareConstants.WEB_DIALOG_PARAM_NAME), json_data.getString("like"), json_data.getString("tit"), json_data.getString(NativeProtocol.WEB_DIALOG_URL), json_data.getString("stt"), json_data.getString("imgtype"), json_data.getString("theloai"), json_data.getString("id_type")));
                            i++;
                        } catch (JSONException e2) {
                            e = e2;
                            JSONArray jSONArray2 = jSONArray;
                        }
                    }
                    Detail.this.mAdapter = new Adapter_home(Detail.this, Detail.this.data_tl);
                    Detail.this.listcm.setAdapter(Detail.this.mAdapter);
                    Detail.this.listcm.setLayoutManager(Detail.this.layoutngang);
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

    private class totalcmt extends AsyncTask<String, Void, String> {
        private totalcmt() {
        }

        protected String doInBackground(String... params) {
            String idfr = params[0];
            HttpClient client = new DefaultHttpClient();
            HttpPost request = new HttpPost("http://cook.audition2.com/like.php");
            List<NameValuePair> nameValuePair = new ArrayList(1);
            nameValuePair.add(new BasicNameValuePair(ShareConstants.WEB_DIALOG_PARAM_ID, "2"));
            nameValuePair.add(new BasicNameValuePair("idbv", idfr));
            try {
                request.setEntity(new UrlEncodedFormEntity(nameValuePair));
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
        }

        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }

        protected void onProgressUpdate(Void... values) {
        }
    }

    private class xoa extends AsyncTask<String, Void, String> {
        private xoa() {
        }

        protected String doInBackground(String... params) {
            String idfr = params[0];
            HttpClient client = new DefaultHttpClient();
            HttpPost request = new HttpPost("http://cook.audition2.com/post.php");
            List<NameValuePair> nameValuePair = new ArrayList(1);
            nameValuePair.add(new BasicNameValuePair(ShareConstants.WEB_DIALOG_PARAM_ID, "8"));
            nameValuePair.add(new BasicNameValuePair("idbv", idfr));
            try {
                request.setEntity(new UrlEncodedFormEntity(nameValuePair));
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
            Detail.this.pDialog = new ProgressDialog(Detail.this);
            Detail.this.pDialog.setMessage("\u0110ang x\u00f3a ...");
            Detail.this.pDialog.setIndeterminate(false);
            Detail.this.pDialog.setCancelable(true);
            Detail.this.pDialog.show();
        }

        protected void onPostExecute(String result) {
            Toast.makeText(Detail.this, "\u0110\u00e3 x\u00f3a b\u00e0i vi\u1ebft n\u00e0y", 0).show();
            Intent scd = new Intent(Detail.this, ContentView.class);
            Home hm = new Home();
            Home.idtype = BuildConfig.VERSION_NAME;
            Home.page = 1;
            Home.pastVisiblesItems = 0;
            Detail.this.startActivity(scd);
            Detail.this.pDialog.dismiss();
            super.onPostExecute(result);
        }

        protected void onProgressUpdate(Void... values) {
        }
    }

    /* renamed from: com.app.tuan88291.testapp.Detail.3 */
    class C09543 extends PagerAdapter {

        /* renamed from: com.app.tuan88291.testapp.Detail.3.1 */
        class C09531 extends OnScrollListener {
            C09531() {
            }

            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (Detail.this.mLayoutManager.findLastCompletelyVisibleItemPosition() == Detail.this.data_cmt.size() - 1 && Detail.this.loading) {
                    Detail.this.page = Detail.this.page + 1;
                    Detail.this.pastVisiblesItems = Detail.this.mLayoutManager.findFirstVisibleItemPosition();
                    new loadcmt(null).execute(new String[]{BuildConfig.VERSION_NAME});
                    Detail.this.loading = false;
                }
            }
        }

        C09543() {
        }

        public int getCount() {
            return 3;
        }

        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }

        public void destroyItem(View container, int position, Object object) {
            ((ViewPager) container).removeView((View) object);
        }

        public Object instantiateItem(ViewGroup container, int position) {
            View view;
            if (position == 0) {
                view = LayoutInflater.from(Detail.this.getBaseContext()).inflate(C0336R.layout.bar1, null, false);
                Detail.this.ten = (TextView) view.findViewById(C0336R.id.nickname);
                Detail.this.tentl = (TextView) view.findViewById(C0336R.id.theloai);
                Detail.this.vanban = (TextView) view.findViewById(C0336R.id.noidung);
                Detail.this.ngay = (TextView) view.findViewById(C0336R.id.date);
                Detail.this.namefood = (TextView) view.findViewById(C0336R.id.namefood);
                Detail.this.edit = (ImageButton) view.findViewById(C0336R.id.edit);
                Detail.this.del = (ImageButton) view.findViewById(C0336R.id.del);
                Detail.this.avatar = (CircleImageView) view.findViewById(C0336R.id.avatar);
                Detail.this.total = (TextView) view.findViewById(C0336R.id.total);
                UrlImageViewHelper.setUrlDrawable(Detail.this.avatar, "http://graph.facebook.com/" + Detail.this.idfb + "/picture?type=large");
                if (Detail.this.db.countuser() > 0 && Detail.this.db.idfb().equals(Detail.this.idfb)) {
                    Detail.this.edit.setVisibility(0);
                    Detail.this.del.setVisibility(0);
                }
                Detail.this.ten.setText(Detail.this.name);
                Detail.this.tentl.setText(Detail.this.tentheloai);
                if (VERSION.SDK_INT >= 24) {
                    Detail.this.vanban.setText(Html.fromHtml(Detail.this.noidung, 0));
                } else {
                    Detail.this.vanban.setText(Html.fromHtml(Detail.this.noidung));
                }
                Detail.this.ngay.setText(Detail.this.date);
                if (VERSION.SDK_INT >= 24) {
                    Detail.this.namefood.setText(Html.fromHtml(Detail.this.tit, 0));
                } else {
                    Detail.this.namefood.setText(Html.fromHtml(Detail.this.tit));
                }
                Detail.this.total.setText(Detail.this.like);
                Detail.this.edit.setOnClickListener(Detail.this);
                Detail.this.del.setOnClickListener(Detail.this);
                Detail.this.ten.setOnClickListener(Detail.this);
                Detail.this.avatar.setOnClickListener(Detail.this);
                container.addView(view);
                return view;
            } else if (position == 1) {
                view = LayoutInflater.from(Detail.this.getBaseContext()).inflate(C0336R.layout.bar2, null, false);
                Detail.boxsend = (EditText) view.findViewById(C0336R.id.content);
                Detail.this.send = (ImageButton) view.findViewById(C0336R.id.send);
                Detail.this.arl = (TextView) view.findViewById(C0336R.id.arl);
                Detail.this.listcmt = (RecyclerView) view.findViewById(C0336R.id.listcmt);
                Detail.this.pro = (ProgressBar) view.findViewById(C0336R.id.pro);
                Detail.this.mLayoutManager = new LinearLayoutManager(Detail.this);
                if (Detail.this.db.countuser() <= 0) {
                    Detail.boxsend.setVisibility(8);
                    Detail.this.send.setVisibility(8);
                }
                new loadcmt(null).execute(new String[]{BuildConfig.VERSION_NAME});
                Detail.this.send.setOnClickListener(Detail.this);
                Detail.this.listcmt.addOnScrollListener(new C09531());
                container.addView(view);
                return view;
            } else if (position != 2) {
                return null;
            } else {
                view = LayoutInflater.from(Detail.this.getBaseContext()).inflate(C0336R.layout.bar3, null, false);
                Detail.this.listcm = (RecyclerView) view.findViewById(C0336R.id.listcm);
                Detail.this.layoutngang = new LinearLayoutManager(Detail.this);
                new loadtheloai(null).execute(new String[]{Detail.this.theloai});
                container.addView(view);
                return view;
            }
        }
    }

    /* renamed from: com.app.tuan88291.testapp.Detail.5 */
    class C09555 implements OnTabBarSelectedIndexListener {
        C09555() {
        }

        public void onStartTabSelected(Model model, int index) {
        }

        public void onEndTabSelected(Model model, int index) {
            model.hideBadge();
        }
    }

    /* renamed from: com.app.tuan88291.testapp.Detail.14 */
    class AnonymousClass14 extends SimpleTarget<Bitmap> {
        final /* synthetic */ SubsamplingScaleImageView val$image;

        AnonymousClass14(SubsamplingScaleImageView subsamplingScaleImageView) {
            this.val$image = subsamplingScaleImageView;
        }

        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
            this.val$image.setImage(ImageSource.bitmap(resource));
        }
    }

    /* renamed from: com.app.tuan88291.testapp.Detail.15 */
    class AnonymousClass15 extends SimpleTarget<Bitmap> {
        final /* synthetic */ SubsamplingScaleImageView val$image;

        AnonymousClass15(SubsamplingScaleImageView subsamplingScaleImageView) {
            this.val$image = subsamplingScaleImageView;
        }

        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
            this.val$image.setImage(ImageSource.bitmap(resource));
        }
    }

    public Detail() {
        this.list = new ArrayList();
        this.data_tl = new ArrayList();
        this.data_cmt = new ArrayList();
        this.pastVisiblesItems = 0;
        this.loading = true;
        this.page = 1;
    }

    @TargetApi(21)
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(1);
        super.onCreate(savedInstanceState);
        setContentView((int) C0336R.layout.detail_layout);
        this.db = new DBhelper(this);
        this.ldv = (LoadingView) findViewById(C0336R.id.loading_view);
        this.gallery = (Gallery) findViewById(C0336R.id.gallery);
        this.gallery.setSpacing(10);
        this.next = (ImageButton) findViewById(C0336R.id.next);
        this.thich = (ImageButton) findViewById(C0336R.id.like);
        this.save = (ImageButton) findViewById(C0336R.id.save);
        initUI();
        this.titcus = (CollapsingToolbarLayout) findViewById(C0336R.id.collapsingToolbar);
        this.appbar = (AppBarLayout) findViewById(C0336R.id.appBar);
        this.thich.setOnClickListener(this);
        this.save.setOnClickListener(this);
        this.toolbar = (Toolbar) findViewById(C0598R.id.toolbar);
        this.toolbar.setNavigationIcon((int) C0336R.drawable.back);
        this.toolbar.setNavigationOnClickListener(new C03111());
        Bundle ext = getIntent().getExtras();
        if (ext != null) {
            this.idfb = ext.getString("idfb");
            this.idbv = ext.getString("idbv");
            this.name = ext.getString(ShareConstants.WEB_DIALOG_PARAM_NAME);
            this.noidung = ext.getString("noidung");
            this.date = ext.getString("date");
            this.like = ext.getString("like");
            this.tit = ext.getString("tit");
            this.stt = ext.getString("stt");
            this.tentheloai = ext.getString("tentheloai");
            this.theloai = ext.getString("theloai");
        }
        if (this.stt.equals("yes")) {
            this.thich.setImageResource(C0336R.drawable.like);
        }
        if (this.db.checksave(this.idbv) > 0) {
            this.save.setImageResource(C0336R.drawable.dishes);
        } else {
            this.save.setImageResource(C0336R.drawable.dish);
        }
        try {
            if (((String) new loadingdk().execute(new String[]{BuildConfig.VERSION_NAME}).get()).equals("over")) {
                this.gallery.setBackgroundResource(C0336R.drawable.bgmeal);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e2) {
            e2.printStackTrace();
        }
        this.appbar.setVisibility(0);
        if (VERSION.SDK_INT >= 24) {
            this.titcus.setTitle(Html.fromHtml(this.tit, 0));
        } else {
            this.titcus.setTitle(Html.fromHtml(this.tit));
        }
        this.titcus.setSelected(true);
        Typeface face = Typeface.createFromAsset(getAssets(), "font/to.otf");
        this.titcus.setCollapsedTitleTypeface(face);
        this.titcus.setExpandedTitleTypeface(face);
        this.gallery.setOnItemClickListener(new C03122());
    }

    private void initUI() {
        ViewPager viewPager = (ViewPager) findViewById(C0336R.id.vp_horizontal_ntb);
        viewPager.setAdapter(new C09543());
        String[] colors = getResources().getStringArray(C0597R.array.default_preview);
        NavigationTabBar navigationTabBar = (NavigationTabBar) findViewById(C0336R.id.ntb_horizontal);
        ArrayList<Model> models = new ArrayList();
        models.add(new Builder(getResources().getDrawable(C0336R.drawable.help), Color.parseColor(colors[3])).title("H\u01b0\u1edbng d\u1eabn").build());
        models.add(new Builder(getResources().getDrawable(C0336R.drawable.chat), Color.parseColor(colors[3])).title("B\u00ecnh lu\u1eadn").build());
        models.add(new Builder(getResources().getDrawable(C0336R.drawable.same), Color.parseColor(colors[3])).title("C\u00f9ng th\u1ec3 lo\u1ea1i").build());
        navigationTabBar.setModels(models);
        navigationTabBar.setViewPager(viewPager, 0);
        navigationTabBar.post(new C03134(navigationTabBar));
        navigationTabBar.setOnTabBarSelectedIndexListener(new C09555());
        navigationTabBar.postDelayed(new C03146((Model) navigationTabBar.getModels().get(1)), 100);
    }

    public void editbv() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(C0336R.layout.custom_edit);
        dialog.setTitle("S\u1eeda b\u00e0i vi\u1ebft:");
        dialog.getWindow().setTitleColor(getResources().getColor(C0336R.color.gren));
        dialog.getWindow().setBackgroundDrawableResource(C0336R.color.colorPrimary);
        ImageButton exit = (ImageButton) dialog.findViewById(C0336R.id.exit);
        ImageButton send = (ImageButton) dialog.findViewById(C0336R.id.send);
        EditText titt = (EditText) dialog.findViewById(C0336R.id.tit);
        EditText nd = (EditText) dialog.findViewById(C0336R.id.nd);
        titt.requestFocus();
        titt.setText(this.tit);
        nd.setText(this.noidung);
        send.setOnClickListener(new C03157(dialog, titt, nd));
        exit.setOnClickListener(new C03168(dialog));
        dialog.show();
    }

    public void delete() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage((CharSequence) "B\u1ea1n c\u00f3 mu\u1ed1n x\u00f3a b\u00e0i vi\u1ebft n\u00e0y?");
        alertDialogBuilder.setPositiveButton((CharSequence) "C\u00f3", new C03179());
        alertDialogBuilder.setNegativeButton((CharSequence) "Kh\u00f4ng", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialogBuilder.create().show();
    }

    public void onClick(View v) {
        if (v == this.avatar || v == this.ten) {
            info();
        }
        if (v == this.edit) {
            editbv();
        }
        if (v == this.del) {
            delete();
        }
        if (v == this.save) {
            luu();
        }
        if (v == this.thich) {
            if (this.db.countuser() > 0) {
                try {
                    String rlt = (String) new like().execute(new String[]{this.idbv}).get();
                    Animation scl = AnimationUtils.loadAnimation(this, C0336R.anim.like);
                    if (rlt.equals("ok")) {
                        this.thich.startAnimation(scl);
                        this.thich.setImageResource(C0336R.drawable.like);
                    }
                    if (rlt.equals("liked")) {
                        this.thich.startAnimation(scl);
                        this.thich.setImageResource(C0336R.drawable.like1);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e2) {
                    e2.printStackTrace();
                }
            } else {
                dangnhap();
            }
        }
        if (v != this.send) {
            return;
        }
        if (boxsend.getText().toString().equals(BuildConfig.VERSION_NAME)) {
            Toast.makeText(this, "B\u1ea1n ch\u01b0a nh\u1eadp n\u1ed9i dung", 0).show();
            return;
        }
        hideSoftKeyboard(this);
        this.page = 1;
        new cmt().execute(new String[]{boxsend.getText().toString()});
    }

    public void luu() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        if (this.db.checksave(this.idbv) > 0) {
            alertDialogBuilder.setMessage((CharSequence) "B\u1ea1n \u0111\u00e3 l\u01b0u m\u00f3n \u0103n n\u00e0y, b\u1ea1n c\u00f3 mu\u1ed1n x\u00f3a m\u00f3n \u0103n n\u00e0y kh\u1ecfi m\u00e1y?");
            alertDialogBuilder.setPositiveButton((CharSequence) "C\u00f3", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface arg0, int arg1) {
                    Detail.this.save.startAnimation(AnimationUtils.loadAnimation(Detail.this, C0336R.anim.like));
                    Detail.this.save.setImageResource(C0336R.drawable.dish);
                    Detail.this.db.deletecook(Detail.this.idbv);
                    Detail.this.db.deleteimg(Detail.this.idbv);
                    Toast.makeText(Detail.this, "\u0111\u00e3 x\u00f3a m\u00f3n \u0103n kh\u1ecfi m\u00e1y", 0).show();
                    arg0.dismiss();
                }
            });
        } else {
            alertDialogBuilder.setMessage((CharSequence) "B\u1ea1n c\u00f3 mu\u1ed1n l\u01b0u m\u00f3n \u0103n n\u00e0y v\u00e0o m\u00e1y kh\u00f4ng?");
            alertDialogBuilder.setPositiveButton((CharSequence) "C\u00f3", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface arg0, int arg1) {
                    Detail.this.db.savecook("cook", Detail.this.tit, Detail.this.noidung, Detail.this.tentheloai, Detail.this.idbv);
                    new loadimg(null).execute(new String[]{Detail.this.idbv});
                    Detail.this.save.startAnimation(AnimationUtils.loadAnimation(Detail.this, C0336R.anim.like));
                    Detail.this.save.setImageResource(C0336R.drawable.dishes);
                    arg0.dismiss();
                }
            });
        }
        alertDialogBuilder.setNegativeButton((CharSequence) "Kh\u00f4ng", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialogBuilder.create().show();
    }

    public static void hideSoftKeyboard(Activity activity) {
        ((InputMethodManager) activity.getSystemService("input_method")).hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

    public void info() {
        Intent scd = new Intent(this, Info_user.class);
        scd.putExtra("idfb", this.idfb);
        scd.putExtra(ShareConstants.WEB_DIALOG_PARAM_NAME, this.name);
        startActivity(scd);
    }

    public static void setbox(String nd) {
        boxsend.setText(nd);
    }

    public void clearDatatl() {
        if (!this.data_tl.isEmpty()) {
            this.data_tl.clear();
            this.mAdapter.notifyDataSetChanged();
        }
    }

    public void clearData() {
        if (!this.data_cmt.isEmpty()) {
            this.data_cmt.clear();
            this.adapter_cmt.notifyDataSetChanged();
        }
    }

    public void show(String url, String idbv, String type, Context ct) {
        Dialog dialog = new Dialog(ct);
        dialog.getWindow().requestFeature(1);
        dialog.setContentView(C0336R.layout.show);
        SubsamplingScaleImageView image = (SubsamplingScaleImageView) dialog.findViewById(C0801R.id.image);
        WebView web = (WebView) dialog.findViewById(C0336R.id.web);
        if (type.equals("img")) {
            Glide.with((FragmentActivity) this).load("http://cook.audition2.com/photos/" + idbv + "/" + url).asBitmap().into(new AnonymousClass14(image));
        } else if (type.equals(ShareConstants.WEB_DIALOG_PARAM_LINK)) {
            Glide.with((FragmentActivity) this).load(url).asBitmap().into(new AnonymousClass15(image));
        } else if (type.equals(AnalyticsEvents.PARAMETER_SHARE_DIALOG_CONTENT_VIDEO)) {
            image.setVisibility(8);
            web.setVisibility(0);
            web.getSettings().setJavaScriptEnabled(true);
            web.loadUrl("https://www.youtube.com/embed/" + url.replace(url.split("[?v=]")[0] + "?v=", BuildConfig.VERSION_NAME).replace("&feature=youtu.be", BuildConfig.VERSION_NAME));
        }
        dialog.show();
    }

    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(CompressFormat.JPEG, 20, baos);
        return Base64.encodeToString(baos.toByteArray(), 0);
    }

    public void dangnhap() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage((CharSequence) "B\u1ea1n ph\u1ea3i \u0111\u0103ng nh\u1eadp \u0111\u1ec3 th\u1ef1c hi\u1ec7n ch\u1ee9c n\u0103ng n\u00e0y:");
        alertDialogBuilder.setPositiveButton((CharSequence) "\u0110\u0103ng nh\u1eadp", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                arg0.dismiss();
                Detail.this.startActivity(new Intent(Detail.this, MainActivity.class));
            }
        });
        alertDialogBuilder.create().show();
    }
}
