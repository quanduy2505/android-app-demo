package com.app.tuan88291.testapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.support.v7.app.AlertDialog.Builder;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.support.v7.widget.helper.ItemTouchHelper.Callback;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import rx.android.BuildConfig;

public class Adapter_home extends Adapter<ViewHolder> {
    private Context context;
    Data_home current;
    int currentPos;
    List<Data_home> data;
    DBhelper db;
    private LayoutInflater inflater;
    ProgressDialog pDialog;
    private int so;
    private int so1;

    /* renamed from: com.app.tuan88291.testapp.Adapter_home.1 */
    class C02631 implements OnClickListener {
        final /* synthetic */ int val$position;

        C02631(int i) {
            this.val$position = i;
        }

        public void onClick(View v) {
            Adapter_home.this.info(this.val$position);
        }
    }

    /* renamed from: com.app.tuan88291.testapp.Adapter_home.2 */
    class C02642 implements OnClickListener {
        final /* synthetic */ int val$position;

        C02642(int i) {
            this.val$position = i;
        }

        public void onClick(View v) {
            Adapter_home.this.info(this.val$position);
        }
    }

    /* renamed from: com.app.tuan88291.testapp.Adapter_home.3 */
    class C02653 implements OnClickListener {
        final /* synthetic */ int val$position;

        C02653(int i) {
            this.val$position = i;
        }

        public void onClick(View v) {
            Adapter_home.this.chuyen(this.val$position);
        }
    }

    /* renamed from: com.app.tuan88291.testapp.Adapter_home.4 */
    class C02664 implements OnClickListener {
        final /* synthetic */ int val$position;

        C02664(int i) {
            this.val$position = i;
        }

        public void onClick(View v) {
            Adapter_home.this.chuyen(this.val$position);
        }
    }

    /* renamed from: com.app.tuan88291.testapp.Adapter_home.5 */
    class C02675 implements OnClickListener {
        final /* synthetic */ Data_home val$current;
        final /* synthetic */ MyHolder val$myHolder;

        C02675(Data_home data_home, MyHolder myHolder) {
            this.val$current = data_home;
            this.val$myHolder = myHolder;
        }

        public void onClick(View v) {
            if (Adapter_home.this.db.countuser() > 0) {
                Log.d("dbbbbbbb", "From: " + Adapter_home.this.db.idfb() + this.val$current.getIdbv());
                try {
                    String rlt = (String) new like(null).execute(new String[]{this.val$current.getIdbv()}).get();
                    Animation scl = AnimationUtils.loadAnimation(Adapter_home.this.context, C0336R.anim.like);
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
            Adapter_home.this.dangnhap();
        }
    }

    /* renamed from: com.app.tuan88291.testapp.Adapter_home.6 */
    class C02686 implements OnClickListener {
        final /* synthetic */ MyHolder val$myHolder;
        final /* synthetic */ int val$position;

        C02686(int i, MyHolder myHolder) {
            this.val$position = i;
            this.val$myHolder = myHolder;
        }

        public void onClick(View v) {
            Adapter_home.this.luu(this.val$position, this.val$myHolder);
        }
    }

    /* renamed from: com.app.tuan88291.testapp.Adapter_home.7 */
    class C02697 implements DialogInterface.OnClickListener {
        C02697() {
        }

        public void onClick(DialogInterface arg0, int arg1) {
            arg0.dismiss();
            Adapter_home.this.context.startActivity(new Intent(Adapter_home.this.context, MainActivity.class));
        }
    }

    /* renamed from: com.app.tuan88291.testapp.Adapter_home.8 */
    class C02708 implements DialogInterface.OnClickListener {
        final /* synthetic */ Data_home val$item;
        final /* synthetic */ MyHolder val$myHolder;

        C02708(MyHolder myHolder, Data_home data_home) {
            this.val$myHolder = myHolder;
            this.val$item = data_home;
        }

        public void onClick(DialogInterface arg0, int arg1) {
            this.val$myHolder.save.startAnimation(AnimationUtils.loadAnimation(Adapter_home.this.context, C0336R.anim.like));
            this.val$myHolder.save.setImageResource(C0336R.drawable.dish);
            Adapter_home.this.db.deletecook(this.val$item.getIdbv());
            Adapter_home.this.db.deleteimg(this.val$item.getIdbv());
            Toast.makeText(Adapter_home.this.context, "\u0111\u00e3 x\u00f3a m\u00f3n \u0103n kh\u1ecfi m\u00e1y", 0).show();
            arg0.dismiss();
        }
    }

    /* renamed from: com.app.tuan88291.testapp.Adapter_home.9 */
    class C02719 implements DialogInterface.OnClickListener {
        final /* synthetic */ Data_home val$item;
        final /* synthetic */ MyHolder val$myHolder;

        C02719(Data_home data_home, MyHolder myHolder) {
            this.val$item = data_home;
            this.val$myHolder = myHolder;
        }

        public void onClick(DialogInterface arg0, int arg1) {
            Adapter_home.this.db.savecook("cook", this.val$item.getTit(), this.val$item.getNoidung(), this.val$item.getTheloai(), this.val$item.getIdbv());
            new loadimg(null).execute(new String[]{this.val$item.getIdbv()});
            this.val$myHolder.save.startAnimation(AnimationUtils.loadAnimation(Adapter_home.this.context, C0336R.anim.like));
            this.val$myHolder.save.setImageResource(C0336R.drawable.dishes);
            arg0.dismiss();
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
            nameValuePair.add(new BasicNameValuePair("idfb", Adapter_home.this.db.idfb()));
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

        /* renamed from: com.app.tuan88291.testapp.Adapter_home.loadimg.1 */
        class C15831 extends SimpleTarget<Bitmap> {
            final /* synthetic */ String val$idbv;

            C15831(String str) {
                this.val$idbv = str;
            }

            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                Adapter_home.this.db.saveimg("img", Adapter_home.this.getStringImage(resource), ShareConstants.WEB_DIALOG_PARAM_LINK, this.val$idbv);
            }
        }

        /* renamed from: com.app.tuan88291.testapp.Adapter_home.loadimg.2 */
        class C15842 extends SimpleTarget<Bitmap> {
            final /* synthetic */ String val$idbv;

            C15842(String str) {
                this.val$idbv = str;
            }

            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                Adapter_home.this.db.saveimg("img", Adapter_home.this.getStringImage(resource), "img", this.val$idbv);
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
            Adapter_home.this.pDialog = new ProgressDialog(Adapter_home.this.context);
            Adapter_home.this.pDialog.setMessage("\u0110ang l\u01b0u ...");
            Adapter_home.this.pDialog.setIndeterminate(false);
            Adapter_home.this.pDialog.setCancelable(true);
            Adapter_home.this.pDialog.show();
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
                                Glide.with(Adapter_home.this.context).load(json_data.getString(NativeProtocol.WEB_DIALOG_URL)).asBitmap().into(new C15831(json_data.getString("idbv")));
                            } else if (json_data.getString(ShareConstants.MEDIA_TYPE).equals("img")) {
                                Glide.with(Adapter_home.this.context).load("http://cook.audition2.com/photos/" + json_data.getString("idbv") + "/" + json_data.getString(NativeProtocol.WEB_DIALOG_URL)).asBitmap().into(new C15842(json_data.getString("idbv")));
                            } else if (json_data.getString(ShareConstants.MEDIA_TYPE).equals(AnalyticsEvents.PARAMETER_SHARE_DIALOG_CONTENT_VIDEO)) {
                                Adapter_home.this.db.saveimg("img", json_data.getString(NativeProtocol.WEB_DIALOG_URL), AnalyticsEvents.PARAMETER_SHARE_DIALOG_CONTENT_VIDEO, json_data.getString("idbv"));
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
                    Toast.makeText(Adapter_home.this.context, "\u0110\u00e3 l\u01b0u m\u00f3n \u0103n th\u00e0nh c\u00f4ng!", 0).show();
                    Adapter_home.this.pDialog.dismiss();
                    super.onPostExecute(result);
                }
            }
            Toast.makeText(Adapter_home.this.context, "\u0110\u00e3 l\u01b0u m\u00f3n \u0103n th\u00e0nh c\u00f4ng!", 0).show();
            Adapter_home.this.pDialog.dismiss();
            super.onPostExecute(result);
        }

        protected void onProgressUpdate(Void... values) {
        }
    }

    class MyHolder extends ViewHolder {
        CircleImageView avatar;
        TextView date;
        ImageView imtit;
        ImageButton like;
        TextView namefood;
        TextView nickname;
        ImageButton save;
        TextView theloai;
        TextView total;

        public MyHolder(View itemView) {
            super(itemView);
            this.namefood = (TextView) itemView.findViewById(C0336R.id.namefood);
            this.nickname = (TextView) itemView.findViewById(C0336R.id.nickname);
            this.date = (TextView) itemView.findViewById(C0336R.id.date);
            this.total = (TextView) itemView.findViewById(C0336R.id.total);
            this.theloai = (TextView) itemView.findViewById(C0336R.id.theloai);
            this.imtit = (ImageView) itemView.findViewById(C0336R.id.imtit);
            this.like = (ImageButton) itemView.findViewById(C0336R.id.like);
            this.save = (ImageButton) itemView.findViewById(C0336R.id.save);
            this.avatar = (CircleImageView) itemView.findViewById(C0336R.id.avatar);
        }
    }

    public Adapter_home(Context context, List<Data_home> data) {
        this.data = Collections.emptyList();
        this.currentPos = 0;
        this.so = 0;
        this.so1 = 0;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.data = data;
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyHolder(this.inflater.inflate(C0336R.layout.custom_home, parent, false));
    }

    public void onBindViewHolder(ViewHolder holder, int position) {
        MyHolder myHolder = (MyHolder) holder;
        Data_home current = (Data_home) this.data.get(position);
        if (VERSION.SDK_INT >= 24) {
            myHolder.namefood.setText(Html.fromHtml(current.getTit(), 0));
        } else {
            myHolder.namefood.setText(Html.fromHtml(current.getTit()));
        }
        UrlImageViewHelper.setUrlDrawable(((MyHolder) holder).avatar, "http://graph.facebook.com/" + current.getIdfb() + "/picture?type=large");
        this.db = new DBhelper(this.context);
        if (current.getImgtype().equals("img")) {
            if (current.getUrl().equals("null")) {
                ((MyHolder) holder).imtit.setImageResource(C0336R.drawable.resize);
            } else {
                Glide.with(this.context).load("http://cook.audition2.com/photos/" + current.getIdbv() + "/" + current.getUrl()).override((int) Callback.DEFAULT_SWIPE_ANIMATION_DURATION, 350).fitCenter().into(((MyHolder) holder).imtit);
            }
        } else if (current.getImgtype().equals(ShareConstants.WEB_DIALOG_PARAM_LINK)) {
            Glide.with(this.context).load(current.getUrl()).override((int) Callback.DEFAULT_SWIPE_ANIMATION_DURATION, 350).fitCenter().placeholder((int) C0336R.drawable.resize).into(((MyHolder) holder).imtit);
        } else if (current.getImgtype().equals(AnalyticsEvents.PARAMETER_SHARE_DIALOG_CONTENT_VIDEO)) {
            Glide.with(this.context).load("https://i.ytimg.com/vi/" + current.getUrl().replace(current.getUrl().split("[?v=]")[0] + "?v=", BuildConfig.VERSION_NAME).replace("&feature=youtu.be", BuildConfig.VERSION_NAME) + "/sddefault.jpg").override((int) Callback.DEFAULT_SWIPE_ANIMATION_DURATION, 350).fitCenter().placeholder((int) C0336R.drawable.resize).into(((MyHolder) holder).imtit);
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
        myHolder.theloai.setText(current.getTheloai());
        myHolder.nickname.setOnClickListener(new C02631(position));
        myHolder.avatar.setOnClickListener(new C02642(position));
        myHolder.namefood.setOnClickListener(new C02653(position));
        myHolder.imtit.setOnClickListener(new C02664(position));
        myHolder.like.setOnClickListener(new C02675(current, myHolder));
        myHolder.save.setOnClickListener(new C02686(position, myHolder));
    }

    public void dangnhap() {
        Builder alertDialogBuilder = new Builder(this.context);
        alertDialogBuilder.setMessage((CharSequence) "B\u1ea1n ph\u1ea3i \u0111\u0103ng nh\u1eadp \u0111\u1ec3 th\u1ef1c hi\u1ec7n ch\u1ee9c n\u0103ng n\u00e0y:");
        alertDialogBuilder.setPositiveButton((CharSequence) "\u0110\u0103ng nh\u1eadp", new C02697());
        alertDialogBuilder.create().show();
    }

    public void luu(int position, MyHolder myHolder) {
        Data_home item = (Data_home) this.data.get(position);
        Builder alertDialogBuilder = new Builder(this.context);
        if (this.db.checksave(item.getIdbv()) > 0) {
            alertDialogBuilder.setMessage((CharSequence) "B\u1ea1n \u0111\u00e3 l\u01b0u m\u00f3n \u0103n n\u00e0y, b\u1ea1n c\u00f3 mu\u1ed1n x\u00f3a m\u00f3n \u0103n n\u00e0y kh\u1ecfi m\u00e1y?");
            alertDialogBuilder.setPositiveButton((CharSequence) "C\u00f3", new C02708(myHolder, item));
        } else {
            alertDialogBuilder.setMessage((CharSequence) "B\u1ea1n c\u00f3 mu\u1ed1n l\u01b0u m\u00f3n \u0103n n\u00e0y v\u00e0o m\u00e1y kh\u00f4ng?");
            alertDialogBuilder.setPositiveButton((CharSequence) "C\u00f3", new C02719(item, myHolder));
        }
        alertDialogBuilder.setNegativeButton((CharSequence) "Kh\u00f4ng", new DialogInterface.OnClickListener() {
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
        scd.putExtra("tentheloai", item.getTheloai());
        this.context.startActivity(scd);
    }

    public void info(int position) {
        Data_home item = (Data_home) this.data.get(position);
        Intent scd = new Intent(this.context, Info_user.class);
        scd.putExtra("idfb", item.getIdfb());
        scd.putExtra(ShareConstants.WEB_DIALOG_PARAM_NAME, item.getName());
        this.context.startActivity(scd);
    }

    public int getItemCount() {
        return this.data.size();
    }

    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(CompressFormat.JPEG, 20, baos);
        return Base64.encodeToString(baos.toByteArray(), 0);
    }
}
