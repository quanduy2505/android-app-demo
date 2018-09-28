package com.app.tuan88291.testapp;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.support.v7.widget.helper.ItemTouchHelper.Callback;
import android.text.Html;
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
import com.bumptech.glide.Glide;
import com.facebook.appevents.AppEventsConstants;
import com.facebook.internal.AnalyticsEvents;
import com.facebook.share.internal.ShareConstants;
import java.io.BufferedReader;
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
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import rx.android.BuildConfig;

public class Adapter_type extends Adapter<ViewHolder> {
    private Context context;
    Data_home current;
    int currentPos;
    List<Data_home> data;
    DBhelper db;
    private LayoutInflater inflater;
    private int so;
    private int so1;

    /* renamed from: com.app.tuan88291.testapp.Adapter_type.1 */
    class C02871 implements OnClickListener {
        final /* synthetic */ int val$position;

        C02871(int i) {
            this.val$position = i;
        }

        public void onClick(View v) {
            Adapter_type.this.chuyen(this.val$position);
        }
    }

    /* renamed from: com.app.tuan88291.testapp.Adapter_type.2 */
    class C02882 implements OnClickListener {
        final /* synthetic */ int val$position;

        C02882(int i) {
            this.val$position = i;
        }

        public void onClick(View v) {
            Adapter_type.this.chuyen(this.val$position);
        }
    }

    /* renamed from: com.app.tuan88291.testapp.Adapter_type.3 */
    class C02893 implements OnClickListener {
        final /* synthetic */ Data_home val$current;
        final /* synthetic */ MyHolder val$myHolder;

        C02893(Data_home data_home, MyHolder myHolder) {
            this.val$current = data_home;
            this.val$myHolder = myHolder;
        }

        public void onClick(View v) {
            Log.d("dbbbbbbb", "From: " + Adapter_type.this.db.idfb() + this.val$current.getIdbv());
            try {
                String rlt = (String) new like(null).execute(new String[]{this.val$current.getIdbv()}).get();
                Animation scl = AnimationUtils.loadAnimation(Adapter_type.this.context, C0336R.anim.like);
                if (rlt.equals("ok")) {
                    this.val$myHolder.like.startAnimation(scl);
                    this.val$myHolder.like.setImageResource(C0336R.drawable.like);
                    this.val$current.setLike(String.valueOf(Integer.parseInt(this.val$current.getLike()) + 1));
                    this.val$current.setStt("yes");
                }
                if (rlt.equals("liked")) {
                    this.val$myHolder.like.startAnimation(scl);
                    this.val$myHolder.like.setImageResource(C0336R.drawable.like1);
                    this.val$current.setStt("no");
                    this.val$current.setLike(String.valueOf(Integer.parseInt(this.val$current.getLike()) - 1));
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e2) {
                e2.printStackTrace();
            }
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
            nameValuePair.add(new BasicNameValuePair("idfb", Adapter_type.this.db.idfb()));
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

    class MyHolder extends ViewHolder {
        ImageView imtit;
        ImageButton like;
        TextView namefood;

        public MyHolder(View itemView) {
            super(itemView);
            this.namefood = (TextView) itemView.findViewById(C0336R.id.namefood);
            this.imtit = (ImageView) itemView.findViewById(C0336R.id.imtit);
            this.like = (ImageButton) itemView.findViewById(C0336R.id.like);
        }
    }

    public Adapter_type(Context context, List<Data_home> data) {
        this.data = Collections.emptyList();
        this.currentPos = 0;
        this.so = 0;
        this.so1 = 0;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.data = data;
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyHolder(this.inflater.inflate(C0336R.layout.custom_type, parent, false));
    }

    public void onBindViewHolder(ViewHolder holder, int position) {
        MyHolder myHolder = (MyHolder) holder;
        Data_home current = (Data_home) this.data.get(position);
        if (VERSION.SDK_INT >= 24) {
            myHolder.namefood.setText(Html.fromHtml(current.getTit(), 0));
        } else {
            myHolder.namefood.setText(Html.fromHtml(current.getTit()));
        }
        myHolder.namefood.setSelected(true);
        this.db = new DBhelper(this.context);
        if (current.getImgtype().equals("img")) {
            Glide.with(this.context).load("http://cook.audition2.com/photos/" + current.getIdbv() + "/" + current.getUrl()).placeholder((int) C0336R.drawable.resize).override((int) Callback.DEFAULT_SWIPE_ANIMATION_DURATION, 350).into(((MyHolder) holder).imtit);
        } else if (current.getImgtype().equals(ShareConstants.WEB_DIALOG_PARAM_LINK)) {
            Glide.with(this.context).load(current.getUrl()).override((int) Callback.DEFAULT_SWIPE_ANIMATION_DURATION, 350).placeholder((int) C0336R.drawable.resize).into(((MyHolder) holder).imtit);
        } else if (current.getImgtype().equals(AnalyticsEvents.PARAMETER_SHARE_DIALOG_CONTENT_VIDEO)) {
            Glide.with(this.context).load("https://i.ytimg.com/vi/" + current.getUrl().replace(current.getUrl().split("[?v=]")[0] + "?v=", BuildConfig.VERSION_NAME).replace("&feature=youtu.be", BuildConfig.VERSION_NAME) + "/sddefault.jpg").override((int) Callback.DEFAULT_SWIPE_ANIMATION_DURATION, 350).fitCenter().placeholder((int) C0336R.drawable.resize).into(((MyHolder) holder).imtit);
        }
        if (current.getStt().equals("yes")) {
            myHolder.like.setImageResource(C0336R.drawable.like);
        } else {
            myHolder.like.setImageResource(C0336R.drawable.like1);
        }
        myHolder.namefood.setOnClickListener(new C02871(position));
        myHolder.imtit.setOnClickListener(new C02882(position));
        myHolder.like.setOnClickListener(new C02893(current, myHolder));
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
