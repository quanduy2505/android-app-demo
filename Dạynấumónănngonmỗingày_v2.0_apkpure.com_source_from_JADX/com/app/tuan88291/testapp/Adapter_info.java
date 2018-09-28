package com.app.tuan88291.testapp;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.provider.MediaStore.Images.Media;
import android.support.v7.app.AlertDialog.Builder;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.support.v7.widget.helper.ItemTouchHelper.Callback;
import android.text.Html;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.facebook.appevents.AppEventsConstants;
import com.facebook.internal.AnalyticsEvents;
import com.facebook.internal.NativeProtocol;
import com.facebook.share.internal.ShareConstants;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import de.hdodenhof.circleimageview.CircleImageView;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
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

public class Adapter_info extends Adapter<ViewHolder> {
    private static final int PICK_IMAGE_REQUEST = 234;
    public static final int TEXT = 0;
    public static final int USER = 1;
    Adapter_info adapter;
    private Bitmap bitmap;
    private Context context;
    Data_home current;
    int currentPos;
    List<Object> data;
    DBhelper db;
    private Uri filePath;
    private LayoutInflater inflater;
    ProgressDialog pDialog;
    private int so;
    private int so1;

    /* renamed from: com.app.tuan88291.testapp.Adapter_info.10 */
    class AnonymousClass10 implements OnClickListener {
        final /* synthetic */ Data_home val$item;
        final /* synthetic */ UserViewHolder val$myHolder;

        AnonymousClass10(UserViewHolder userViewHolder, Data_home data_home) {
            this.val$myHolder = userViewHolder;
            this.val$item = data_home;
        }

        public void onClick(DialogInterface arg0, int arg1) {
            this.val$myHolder.save.startAnimation(AnimationUtils.loadAnimation(Adapter_info.this.context, C0336R.anim.like));
            this.val$myHolder.save.setImageResource(C0336R.drawable.dish);
            Adapter_info.this.db.deletecook(this.val$item.getIdbv());
            Adapter_info.this.db.deleteimg(this.val$item.getIdbv());
            Toast.makeText(Adapter_info.this.context, "\u0111\u00e3 x\u00f3a m\u00f3n \u0103n kh\u1ecfi m\u00e1y", Adapter_info.TEXT).show();
            arg0.dismiss();
        }
    }

    /* renamed from: com.app.tuan88291.testapp.Adapter_info.11 */
    class AnonymousClass11 implements OnClickListener {
        final /* synthetic */ Data_home val$item;
        final /* synthetic */ UserViewHolder val$myHolder;

        AnonymousClass11(Data_home data_home, UserViewHolder userViewHolder) {
            this.val$item = data_home;
            this.val$myHolder = userViewHolder;
        }

        public void onClick(DialogInterface arg0, int arg1) {
            Adapter_info.this.db.savecook("cook", this.val$item.getTit(), this.val$item.getNoidung(), this.val$item.getTheloai(), this.val$item.getIdbv());
            loadimg com_app_tuan88291_testapp_Adapter_info_loadimg = new loadimg(null);
            String[] strArr = new String[Adapter_info.USER];
            strArr[Adapter_info.TEXT] = this.val$item.getIdbv();
            com_app_tuan88291_testapp_Adapter_info_loadimg.execute(strArr);
            this.val$myHolder.save.startAnimation(AnimationUtils.loadAnimation(Adapter_info.this.context, C0336R.anim.like));
            this.val$myHolder.save.setImageResource(C0336R.drawable.dishes);
            arg0.dismiss();
        }
    }

    /* renamed from: com.app.tuan88291.testapp.Adapter_info.14 */
    class AnonymousClass14 implements View.OnClickListener {
        final /* synthetic */ Dialog val$dialog;
        final /* synthetic */ Data_home val$item;
        final /* synthetic */ EditText val$nd;
        final /* synthetic */ EditText val$tit;

        AnonymousClass14(Dialog dialog, EditText editText, EditText editText2, Data_home data_home) {
            this.val$dialog = dialog;
            this.val$tit = editText;
            this.val$nd = editText2;
            this.val$item = data_home;
        }

        public void onClick(View v) {
            this.val$dialog.dismiss();
            new edit(null).execute(new String[]{this.val$tit.getText().toString(), this.val$nd.getText().toString(), this.val$item.getIdbv()});
        }
    }

    /* renamed from: com.app.tuan88291.testapp.Adapter_info.15 */
    class AnonymousClass15 implements View.OnClickListener {
        final /* synthetic */ Dialog val$dialog;

        AnonymousClass15(Dialog dialog) {
            this.val$dialog = dialog;
        }

        public void onClick(View v) {
            this.val$dialog.dismiss();
        }
    }

    /* renamed from: com.app.tuan88291.testapp.Adapter_info.16 */
    class AnonymousClass16 implements OnClickListener {
        final /* synthetic */ int val$position;

        AnonymousClass16(int i) {
            this.val$position = i;
        }

        public void onClick(DialogInterface arg0, int arg1) {
            Data_home item = (Data_home) Adapter_info.this.data.get(this.val$position);
            Adapter_info.this.data.remove(this.val$position);
            Adapter_info.this.adapter.notifyDataSetChanged();
            xoa com_app_tuan88291_testapp_Adapter_info_xoa = new xoa(null);
            String[] strArr = new String[Adapter_info.USER];
            strArr[Adapter_info.TEXT] = item.getIdbv();
            com_app_tuan88291_testapp_Adapter_info_xoa.execute(strArr);
        }
    }

    /* renamed from: com.app.tuan88291.testapp.Adapter_info.1 */
    class C02721 implements View.OnClickListener {
        final /* synthetic */ Data_info_head val$item;

        C02721(Data_info_head data_info_head) {
            this.val$item = data_info_head;
        }

        public void onClick(View v) {
            String url = "http://fb.com/" + this.val$item.getIdfb();
            Intent i = new Intent("android.intent.action.VIEW");
            i.setData(Uri.parse(url));
            Adapter_info.this.context.startActivity(i);
        }
    }

    /* renamed from: com.app.tuan88291.testapp.Adapter_info.2 */
    class C02732 implements View.OnClickListener {
        C02732() {
        }

        public void onClick(View v) {
            Intent scd = new Intent(Adapter_info.this.context, New_post.class);
            scd.putExtra("idfb", Adapter_info.this.db.idfb());
            scd.putExtra(ShareConstants.WEB_DIALOG_PARAM_NAME, Adapter_info.this.db.ten());
            Adapter_info.this.context.startActivity(scd);
        }
    }

    /* renamed from: com.app.tuan88291.testapp.Adapter_info.3 */
    class C02743 implements View.OnClickListener {
        C02743() {
        }

        public void onClick(View v) {
            Adapter_info.this.showFileChooser();
        }
    }

    /* renamed from: com.app.tuan88291.testapp.Adapter_info.4 */
    class C02754 implements View.OnClickListener {
        final /* synthetic */ int val$position;

        C02754(int i) {
            this.val$position = i;
        }

        public void onClick(View v) {
            Adapter_info.this.del(this.val$position);
        }
    }

    /* renamed from: com.app.tuan88291.testapp.Adapter_info.5 */
    class C02765 implements View.OnClickListener {
        final /* synthetic */ int val$position;

        C02765(int i) {
            this.val$position = i;
        }

        public void onClick(View v) {
            Adapter_info.this.edit(this.val$position);
        }
    }

    /* renamed from: com.app.tuan88291.testapp.Adapter_info.6 */
    class C02776 implements View.OnClickListener {
        final /* synthetic */ int val$position;

        C02776(int i) {
            this.val$position = i;
        }

        public void onClick(View v) {
            Adapter_info.this.chuyen(this.val$position);
        }
    }

    /* renamed from: com.app.tuan88291.testapp.Adapter_info.7 */
    class C02787 implements View.OnClickListener {
        final /* synthetic */ int val$position;

        C02787(int i) {
            this.val$position = i;
        }

        public void onClick(View v) {
            Adapter_info.this.chuyen(this.val$position);
        }
    }

    /* renamed from: com.app.tuan88291.testapp.Adapter_info.8 */
    class C02798 implements View.OnClickListener {
        final /* synthetic */ Data_home val$current;
        final /* synthetic */ UserViewHolder val$myHolder;

        C02798(Data_home data_home, UserViewHolder userViewHolder) {
            this.val$current = data_home;
            this.val$myHolder = userViewHolder;
        }

        public void onClick(View v) {
            if (Adapter_info.this.db.countuser() > 0) {
                try {
                    like com_app_tuan88291_testapp_Adapter_info_like = new like(null);
                    String[] strArr = new String[Adapter_info.USER];
                    strArr[Adapter_info.TEXT] = this.val$current.getIdbv();
                    String rlt = (String) com_app_tuan88291_testapp_Adapter_info_like.execute(strArr).get();
                    Animation scl = AnimationUtils.loadAnimation(Adapter_info.this.context, C0336R.anim.like);
                    if (rlt.equals("ok")) {
                        this.val$myHolder.like.startAnimation(scl);
                        this.val$myHolder.like.setImageResource(C0336R.drawable.like);
                        this.val$current.setStt("yes");
                    }
                    if (rlt.equals("liked")) {
                        this.val$myHolder.like.startAnimation(scl);
                        this.val$myHolder.like.setImageResource(C0336R.drawable.like1);
                        this.val$current.setStt("no");
                        return;
                    }
                    return;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return;
                } catch (ExecutionException e2) {
                    e2.printStackTrace();
                    return;
                }
            }
            Adapter_info.this.dangnhap();
        }
    }

    /* renamed from: com.app.tuan88291.testapp.Adapter_info.9 */
    class C02809 implements View.OnClickListener {
        final /* synthetic */ UserViewHolder val$myHolder;
        final /* synthetic */ int val$position;

        C02809(int i, UserViewHolder userViewHolder) {
            this.val$position = i;
            this.val$myHolder = userViewHolder;
        }

        public void onClick(View v) {
            Adapter_info.this.luu(this.val$position, this.val$myHolder);
        }
    }

    private class edit extends AsyncTask<String, Void, String> {
        private edit() {
        }

        protected String doInBackground(String... params) {
            String idbv = params[2];
            String tit = params[Adapter_info.TEXT];
            String nd = params[Adapter_info.USER];
            HttpClient client = new DefaultHttpClient();
            HttpPost request = new HttpPost("http://cook.audition2.com/post.php");
            List<NameValuePair> nameValuePair = new ArrayList(Adapter_info.USER);
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
            Adapter_info.this.pDialog = new ProgressDialog(Adapter_info.this.context);
            Adapter_info.this.pDialog.setMessage("\u0110ang l\u01b0u ...");
            Adapter_info.this.pDialog.setIndeterminate(false);
            Adapter_info.this.pDialog.setCancelable(true);
            Adapter_info.this.pDialog.show();
        }

        protected void onPostExecute(String result) {
            Toast.makeText(Adapter_info.this.context, "\u0110\u00e3 l\u01b0u b\u00e0i vi\u1ebft", Adapter_info.TEXT).show();
            Adapter_info.this.pDialog.dismiss();
            super.onPostExecute(result);
        }

        protected void onProgressUpdate(Void... values) {
        }
    }

    private class like extends AsyncTask<String, Void, String> {
        private like() {
        }

        protected String doInBackground(String... params) {
            String idfr = params[Adapter_info.TEXT];
            HttpClient client = new DefaultHttpClient();
            HttpPost request = new HttpPost("http://cook.audition2.com/like.php");
            List<NameValuePair> nameValuePair = new ArrayList(Adapter_info.USER);
            nameValuePair.add(new BasicNameValuePair(ShareConstants.WEB_DIALOG_PARAM_ID, AppEventsConstants.EVENT_PARAM_VALUE_YES));
            nameValuePair.add(new BasicNameValuePair("idfb", Adapter_info.this.db.idfb()));
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

    private class loadimg extends AsyncTask<String, Void, String> {

        /* renamed from: com.app.tuan88291.testapp.Adapter_info.loadimg.1 */
        class C15851 extends SimpleTarget<Bitmap> {
            final /* synthetic */ String val$idbv;

            C15851(String str) {
                this.val$idbv = str;
            }

            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                Adapter_info.this.db.saveimg("img", Adapter_info.this.getStringImage(resource), ShareConstants.WEB_DIALOG_PARAM_LINK, this.val$idbv);
            }
        }

        /* renamed from: com.app.tuan88291.testapp.Adapter_info.loadimg.2 */
        class C15862 extends SimpleTarget<Bitmap> {
            final /* synthetic */ String val$idbv;

            C15862(String str) {
                this.val$idbv = str;
            }

            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                Adapter_info.this.db.saveimg("img", Adapter_info.this.getStringImage(resource), "img", this.val$idbv);
            }
        }

        private loadimg() {
        }

        protected String doInBackground(String... params) {
            HttpResponse response = null;
            try {
                response = new DefaultHttpClient().execute(new HttpGet("http://cook.audition2.com/showimg.php?idbv=" + params[Adapter_info.TEXT]));
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
            Adapter_info.this.pDialog = new ProgressDialog(Adapter_info.this.context);
            Adapter_info.this.pDialog.setMessage("\u0110ang l\u01b0u ...");
            Adapter_info.this.pDialog.setIndeterminate(false);
            Adapter_info.this.pDialog.setCancelable(true);
            Adapter_info.this.pDialog.show();
        }

        protected void onPostExecute(String result) {
            JSONException e;
            if (!result.equals("over")) {
                try {
                    JSONArray jArray = new JSONArray(result);
                    int i = Adapter_info.TEXT;
                    while (i < jArray.length()) {
                        try {
                            JSONObject json_data = jArray.getJSONObject(i);
                            if (json_data.getString(ShareConstants.MEDIA_TYPE).equals(ShareConstants.WEB_DIALOG_PARAM_LINK)) {
                                Glide.with(Adapter_info.this.context).load(json_data.getString(NativeProtocol.WEB_DIALOG_URL)).asBitmap().into(new C15851(json_data.getString("idbv")));
                            } else if (json_data.getString(ShareConstants.MEDIA_TYPE).equals("img")) {
                                Glide.with(Adapter_info.this.context).load("http://cook.audition2.com/photos/" + json_data.getString("idbv") + "/" + json_data.getString(NativeProtocol.WEB_DIALOG_URL)).asBitmap().into(new C15862(json_data.getString("idbv")));
                            } else if (json_data.getString(ShareConstants.MEDIA_TYPE).equals(AnalyticsEvents.PARAMETER_SHARE_DIALOG_CONTENT_VIDEO)) {
                                Adapter_info.this.db.saveimg("img", json_data.getString(NativeProtocol.WEB_DIALOG_URL), AnalyticsEvents.PARAMETER_SHARE_DIALOG_CONTENT_VIDEO, json_data.getString("idbv"));
                            }
                            i += Adapter_info.USER;
                        } catch (JSONException e2) {
                            e = e2;
                            JSONArray jSONArray = jArray;
                        }
                    }
                } catch (JSONException e3) {
                    e = e3;
                    e.printStackTrace();
                    Toast.makeText(Adapter_info.this.context, "\u0110\u00e3 l\u01b0u m\u00f3n \u0103n th\u00e0nh c\u00f4ng!", Adapter_info.TEXT).show();
                    Adapter_info.this.pDialog.dismiss();
                    super.onPostExecute(result);
                }
            }
            Toast.makeText(Adapter_info.this.context, "\u0110\u00e3 l\u01b0u m\u00f3n \u0103n th\u00e0nh c\u00f4ng!", Adapter_info.TEXT).show();
            Adapter_info.this.pDialog.dismiss();
            super.onPostExecute(result);
        }

        protected void onProgressUpdate(Void... values) {
        }
    }

    private class upanh extends AsyncTask<String, Void, String> {
        private upanh() {
        }

        protected String doInBackground(String... params) {
            String img = params[Adapter_info.TEXT];
            HttpClient client = new DefaultHttpClient();
            HttpPost request = new HttpPost("http://cook.audition2.com/cover.php");
            List<NameValuePair> nameValuePair = new ArrayList(Adapter_info.USER);
            nameValuePair.add(new BasicNameValuePair(ShareConstants.WEB_DIALOG_PARAM_ID, AppEventsConstants.EVENT_PARAM_VALUE_YES));
            nameValuePair.add(new BasicNameValuePair("idfb", Adapter_info.this.db.idfb()));
            nameValuePair.add(new BasicNameValuePair("image", img));
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
            Adapter_info.this.pDialog = new ProgressDialog(Adapter_info.this.context);
            Adapter_info.this.pDialog.setMessage("\u0110ang upload ...");
            Adapter_info.this.pDialog.setIndeterminate(false);
            Adapter_info.this.pDialog.setCancelable(true);
            Adapter_info.this.pDialog.show();
        }

        protected void onPostExecute(String result) {
            Toast.makeText(Adapter_info.this.context, "\u0110\u00e3 upload \u1ea3nh b\u00eca th\u00e0nh c\u00f4ng, load l\u1ea1i \u0111\u1ec3 th\u1ea5y \u1ea3nh b\u00eca!", Adapter_info.TEXT).show();
            Adapter_info.this.pDialog.dismiss();
            super.onPostExecute(result);
        }

        protected void onProgressUpdate(Void... values) {
        }
    }

    private class xoa extends AsyncTask<String, Void, String> {
        private xoa() {
        }

        protected String doInBackground(String... params) {
            String idfr = params[Adapter_info.TEXT];
            HttpClient client = new DefaultHttpClient();
            HttpPost request = new HttpPost("http://cook.audition2.com/post.php");
            List<NameValuePair> nameValuePair = new ArrayList(Adapter_info.USER);
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
            Adapter_info.this.pDialog = new ProgressDialog(Adapter_info.this.context);
            Adapter_info.this.pDialog.setMessage("\u0110ang x\u00f3a ...");
            Adapter_info.this.pDialog.setIndeterminate(false);
            Adapter_info.this.pDialog.setCancelable(true);
            Adapter_info.this.pDialog.show();
        }

        protected void onPostExecute(String result) {
            Toast.makeText(Adapter_info.this.context, "\u0110\u00e3 x\u00f3a b\u00e0i vi\u1ebft n\u00e0y", Adapter_info.TEXT).show();
            Info_user ifu = new Info_user();
            Info_user.loadtt();
            Adapter_info.this.pDialog.dismiss();
            super.onPostExecute(result);
        }

        protected void onProgressUpdate(Void... values) {
        }
    }

    public class TextViewHolder extends ViewHolder {
        CircleImageView avatar;
        private ImageButton cam;
        private ImageView cover;
        private ImageButton create;
        private ImageButton fb;
        private TextView nickname;
        private TextView num;

        public TextViewHolder(View itemView) {
            super(itemView);
            this.nickname = (TextView) itemView.findViewById(C0336R.id.nickname);
            this.num = (TextView) itemView.findViewById(C0336R.id.numpost);
            this.cover = (ImageView) itemView.findViewById(C0336R.id.header_cover_image);
            this.avatar = (CircleImageView) itemView.findViewById(C0336R.id.user_profile_photo);
            this.cam = (ImageButton) itemView.findViewById(C0336R.id.cam);
            this.fb = (ImageButton) itemView.findViewById(C0336R.id.facebook);
            this.create = (ImageButton) itemView.findViewById(C0336R.id.create);
        }
    }

    public class UserViewHolder extends ViewHolder {
        CircleImageView avatar;
        TextView date;
        ImageButton del;
        ImageButton edit;
        ImageView imtit;
        ImageButton like;
        LinearLayout lni;
        TextView namefood;
        TextView nickname;
        ImageButton save;
        TextView total;

        public UserViewHolder(View itemView) {
            super(itemView);
            this.namefood = (TextView) itemView.findViewById(C0336R.id.namefood);
            this.nickname = (TextView) itemView.findViewById(C0336R.id.nickname);
            this.date = (TextView) itemView.findViewById(C0336R.id.date);
            this.del = (ImageButton) itemView.findViewById(C0336R.id.del);
            this.edit = (ImageButton) itemView.findViewById(C0336R.id.edit);
            this.lni = (LinearLayout) itemView.findViewById(C0336R.id.line_info);
            this.total = (TextView) itemView.findViewById(C0336R.id.total);
            this.imtit = (ImageView) itemView.findViewById(C0336R.id.imtit);
            this.like = (ImageButton) itemView.findViewById(C0336R.id.like);
            this.save = (ImageButton) itemView.findViewById(C0336R.id.save);
            this.avatar = (CircleImageView) itemView.findViewById(C0336R.id.avatar);
        }
    }

    public Adapter_info(Context context, List<Object> data) {
        this.data = Collections.emptyList();
        this.currentPos = TEXT;
        this.adapter = this;
        this.so = TEXT;
        this.so1 = TEXT;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.data = data;
    }

    public int getItemViewType(int position) {
        if (this.data.get(position) instanceof Data_info_head) {
            return TEXT;
        }
        if (this.data.get(position) instanceof Data_home) {
            return USER;
        }
        return -1;
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater li = LayoutInflater.from(this.context);
        switch (viewType) {
            case TEXT /*0*/:
                return new TextViewHolder(li.inflate(C0336R.layout.custom_info_head, parent, false));
            case USER /*1*/:
                return new UserViewHolder(li.inflate(C0336R.layout.custom_info, parent, false));
            default:
                return null;
        }
    }

    public void onBindViewHolder(ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case TEXT /*0*/:
                this.db = new DBhelper(this.context);
                Data_info_head item = (Data_info_head) this.data.get(position);
                TextViewHolder textViewHolder = (TextViewHolder) holder;
                if (item.getIdfb().equals(this.db.idfb())) {
                    textViewHolder.cam.setVisibility(TEXT);
                    textViewHolder.create.setVisibility(TEXT);
                }
                textViewHolder.nickname.setText(item.getName());
                textViewHolder.num.setText(item.getTt());
                Glide.with(this.context).load(item.getLink()).fitCenter().into(textViewHolder.cover);
                UrlImageViewHelper.setUrlDrawable(textViewHolder.avatar, "http://graph.facebook.com/" + item.getIdfb() + "/picture?type=large");
                textViewHolder.fb.setOnClickListener(new C02721(item));
                textViewHolder.create.setOnClickListener(new C02732());
                textViewHolder.cam.setOnClickListener(new C02743());
            case USER /*1*/:
                this.db = new DBhelper(this.context);
                Data_home current = (Data_home) this.data.get(position);
                UserViewHolder myHolder = (UserViewHolder) holder;
                if (VERSION.SDK_INT >= 24) {
                    myHolder.namefood.setText(Html.fromHtml(current.getTit(), TEXT));
                } else {
                    myHolder.namefood.setText(Html.fromHtml(current.getTit()));
                }
                UrlImageViewHelper.setUrlDrawable(myHolder.avatar, "http://graph.facebook.com/" + current.getIdfb() + "/picture?type=large");
                if (current.getIdfb().equals(this.db.idfb())) {
                    myHolder.lni.setVisibility(8);
                    myHolder.edit.setVisibility(TEXT);
                    myHolder.del.setVisibility(TEXT);
                }
                if (current.getImgtype().equals("img")) {
                    Glide.with(this.context).load("http://cook.audition2.com/photos/" + current.getIdbv() + "/" + current.getUrl()).fitCenter().override((int) Callback.DEFAULT_SWIPE_ANIMATION_DURATION, 350).into(myHolder.imtit);
                } else if (current.getImgtype().equals(ShareConstants.WEB_DIALOG_PARAM_LINK)) {
                    Glide.with(this.context).load(current.getUrl()).placeholder((int) C0336R.drawable.resize).fitCenter().override((int) Callback.DEFAULT_SWIPE_ANIMATION_DURATION, 350).into(myHolder.imtit);
                } else if (current.getImgtype().equals(AnalyticsEvents.PARAMETER_SHARE_DIALOG_CONTENT_VIDEO)) {
                    Glide.with(this.context).load("https://i.ytimg.com/vi/" + current.getUrl().replace(current.getUrl().split("[?v=]")[TEXT] + "?v=", BuildConfig.VERSION_NAME).replace("&feature=youtu.be", BuildConfig.VERSION_NAME) + "/sddefault.jpg").override((int) Callback.DEFAULT_SWIPE_ANIMATION_DURATION, 350).fitCenter().placeholder((int) C0336R.drawable.resize).into(myHolder.imtit);
                }
                if (current.getStt().equals("yes")) {
                    myHolder.like.setImageResource(C0336R.drawable.like);
                } else {
                    myHolder.like.setImageResource(C0336R.drawable.like1);
                }
                if (this.db.checksave(current.getIdbv()) > 0) {
                    myHolder.save.setImageResource(C0336R.drawable.dishes);
                } else {
                    myHolder.save.setImageResource(C0336R.drawable.dish);
                }
                myHolder.date.setText(current.getDate());
                myHolder.nickname.setText(current.getName());
                myHolder.total.setText(current.getLike());
                myHolder.del.setOnClickListener(new C02754(position));
                myHolder.edit.setOnClickListener(new C02765(position));
                myHolder.namefood.setOnClickListener(new C02776(position));
                myHolder.imtit.setOnClickListener(new C02787(position));
                myHolder.like.setOnClickListener(new C02798(current, myHolder));
                myHolder.save.setOnClickListener(new C02809(position, myHolder));
            default:
        }
    }

    public void luu(int position, UserViewHolder myHolder) {
        Data_home item = (Data_home) this.data.get(position);
        Builder alertDialogBuilder = new Builder(this.context);
        if (this.db.checksave(item.getIdbv()) > 0) {
            alertDialogBuilder.setMessage((CharSequence) "B\u1ea1n \u0111\u00e3 l\u01b0u m\u00f3n \u0103n n\u00e0y, b\u1ea1n c\u00f3 mu\u1ed1n x\u00f3a m\u00f3n \u0103n n\u00e0y kh\u1ecfi m\u00e1y?");
            alertDialogBuilder.setPositiveButton((CharSequence) "C\u00f3", new AnonymousClass10(myHolder, item));
        } else {
            alertDialogBuilder.setMessage((CharSequence) "B\u1ea1n c\u00f3 mu\u1ed1n l\u01b0u m\u00f3n \u0103n n\u00e0y v\u00e0o m\u00e1y kh\u00f4ng?");
            alertDialogBuilder.setPositiveButton((CharSequence) "C\u00f3", new AnonymousClass11(item, myHolder));
        }
        alertDialogBuilder.setNegativeButton((CharSequence) "Kh\u00f4ng", new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialogBuilder.create().show();
    }

    public void dangnhap() {
        Builder alertDialogBuilder = new Builder(this.context);
        alertDialogBuilder.setMessage((CharSequence) "B\u1ea1n ph\u1ea3i \u0111\u0103ng nh\u1eadp \u0111\u1ec3 th\u1ef1c hi\u1ec7n ch\u1ee9c n\u0103ng n\u00e0y:");
        alertDialogBuilder.setPositiveButton((CharSequence) "\u0110\u0103ng nh\u1eadp", new OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                arg0.dismiss();
                Adapter_info.this.context.startActivity(new Intent(Adapter_info.this.context, MainActivity.class));
            }
        });
        alertDialogBuilder.create().show();
    }

    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction("android.intent.action.GET_CONTENT");
        ((Activity) this.context).startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        this.adapter.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == -1 && data != null && data.getData() != null) {
            this.filePath = data.getData();
            try {
                this.bitmap = Media.getBitmap(this.context.getContentResolver(), this.filePath);
                upanh com_app_tuan88291_testapp_Adapter_info_upanh = new upanh();
                String[] strArr = new String[USER];
                strArr[TEXT] = getStringImage(this.bitmap);
                com_app_tuan88291_testapp_Adapter_info_upanh.execute(strArr);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(CompressFormat.JPEG, 20, baos);
        return Base64.encodeToString(baos.toByteArray(), TEXT);
    }

    public void edit(int position) {
        Data_home item = (Data_home) this.data.get(position);
        Dialog dialog = new Dialog(this.context);
        dialog.setContentView(C0336R.layout.custom_edit);
        dialog.setTitle("S\u1eeda b\u00e0i vi\u1ebft:");
        ImageButton exit = (ImageButton) dialog.findViewById(C0336R.id.exit);
        ImageButton send = (ImageButton) dialog.findViewById(C0336R.id.send);
        EditText tit = (EditText) dialog.findViewById(C0336R.id.tit);
        EditText nd = (EditText) dialog.findViewById(C0336R.id.nd);
        tit.requestFocus();
        tit.setText(item.getTit());
        nd.setText(item.getNoidung());
        send.setOnClickListener(new AnonymousClass14(dialog, tit, nd, item));
        exit.setOnClickListener(new AnonymousClass15(dialog));
        dialog.show();
    }

    public void del(int position) {
        Builder alertDialogBuilder = new Builder(this.context);
        alertDialogBuilder.setMessage((CharSequence) "B\u1ea1n c\u00f3 mu\u1ed1n x\u00f3a b\u00e0i vi\u1ebft n\u00e0y?");
        alertDialogBuilder.setPositiveButton((CharSequence) "C\u00f3", new AnonymousClass16(position));
        alertDialogBuilder.setNegativeButton((CharSequence) "Kh\u00f4ng", new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialogBuilder.create().show();
    }

    public void chuyen(int position) {
        Data_home item = (Data_home) this.data.get(position);
        Intent scd = new Intent(this.context, Detail.class);
        scd.putExtra("idbv", item.getIdbv());
        scd.putExtra("noidung", item.getNoidung());
        scd.putExtra("idfb", item.getIdfb());
        scd.putExtra("date", item.getDate());
        scd.putExtra(ShareConstants.WEB_DIALOG_PARAM_NAME, item.getName());
        scd.putExtra("like", item.getLike());
        scd.putExtra("tit", item.getTit());
        scd.putExtra("stt", item.getStt());
        scd.putExtra("theloai", item.getId_type());
        this.context.startActivity(scd);
    }

    public int getItemCount() {
        return this.data.size();
    }
}
