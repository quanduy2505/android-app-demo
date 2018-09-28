package com.app.tuan88291.testapp;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.facebook.appevents.AppEventsConstants;
import com.facebook.share.internal.ShareConstants;
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

public class Comment extends AppCompatActivity implements OnClickListener {
    public static EditText box;
    private Adapter_comment_1 adapter_cmt;
    List<Data_comment_1> data_cmt;
    DBhelper db;
    private String idbv;
    private RecyclerView listcmt;
    private boolean loading;
    LinearLayoutManager mLayoutManager;
    ProgressDialog pDialog;
    private int page;
    int pastVisiblesItems;
    private ProgressBar pro;
    private ImageButton send;
    private TextView tb;
    private TextView tieude;
    private String title;
    Toolbar toolbar;
    private ImageButton turn;

    private class cmt extends AsyncTask<String, Void, String> {
        private cmt() {
        }

        protected String doInBackground(String... params) {
            String ct = params[0];
            HttpClient client = new DefaultHttpClient();
            HttpPost request = new HttpPost("http://cook.audition2.com/cmt.php");
            List<NameValuePair> nameValuePair = new ArrayList(1);
            nameValuePair.add(new BasicNameValuePair(ShareConstants.WEB_DIALOG_PARAM_ID, AppEventsConstants.EVENT_PARAM_VALUE_YES));
            nameValuePair.add(new BasicNameValuePair("idfb", Comment.this.db.idfb()));
            nameValuePair.add(new BasicNameValuePair(ShareConstants.WEB_DIALOG_PARAM_NAME, Comment.this.db.ten()));
            nameValuePair.add(new BasicNameValuePair("text", ct));
            nameValuePair.add(new BasicNameValuePair("idbv", Comment.this.idbv));
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
            Comment.this.pDialog = new ProgressDialog(Comment.this);
            Comment.this.pDialog.setMessage("\u0110ang g\u1eedi ...");
            Comment.this.pDialog.setIndeterminate(false);
            Comment.this.pDialog.setCancelable(true);
            Comment.this.pDialog.show();
        }

        protected void onPostExecute(String result) {
            new loadcmt(null).execute(new String[]{BuildConfig.VERSION_NAME});
            Comment.box.setText(BuildConfig.VERSION_NAME);
            Comment.this.pDialog.dismiss();
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
                response = new DefaultHttpClient().execute(new HttpGet("http://cook.audition2.com/showcmt.php?idbv=" + Comment.this.idbv + "&page=" + Comment.this.page));
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
            Comment.this.pro.setVisibility(0);
        }

        protected void onPostExecute(String result) {
            JSONException e;
            if (!result.equals("over")) {
                Comment.this.tb.setText(BuildConfig.VERSION_NAME);
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
                            Comment.this.data_cmt.add(dth);
                            i++;
                        } catch (JSONException e2) {
                            e = e2;
                            JSONArray jSONArray = jArray;
                        }
                    }
                    Comment.this.adapter_cmt = new Adapter_comment_1(Comment.this, Comment.this.data_cmt);
                    Comment.this.listcmt.setAdapter(Comment.this.adapter_cmt);
                    Comment.this.listcmt.setLayoutManager(Comment.this.mLayoutManager);
                    Comment.this.loading = true;
                    Comment.this.listcmt.scrollToPosition(Comment.this.pastVisiblesItems);
                } catch (JSONException e3) {
                    e = e3;
                    e.printStackTrace();
                    Comment.this.pro.setVisibility(8);
                    super.onPostExecute(result);
                }
            } else if (Comment.this.data_cmt.size() == 0) {
                Comment.this.tb.setText("Ch\u01b0a c\u00f3 comment n\u00e0o!");
            }
            Comment.this.pro.setVisibility(8);
            super.onPostExecute(result);
        }

        protected void onProgressUpdate(Void... values) {
        }
    }

    /* renamed from: com.app.tuan88291.testapp.Comment.1 */
    class C09511 extends OnScrollListener {
        C09511() {
        }

        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            if (Comment.this.mLayoutManager.findLastCompletelyVisibleItemPosition() == Comment.this.data_cmt.size() - 1 && Comment.this.loading) {
                Comment.this.page = Comment.this.page + 1;
                Comment.this.pastVisiblesItems = Comment.this.mLayoutManager.findFirstVisibleItemPosition();
                new loadcmt(null).execute(new String[]{BuildConfig.VERSION_NAME});
                Comment.this.loading = false;
            }
        }
    }

    public Comment() {
        this.data_cmt = new ArrayList();
        this.pastVisiblesItems = 0;
        this.loading = true;
        this.page = 1;
    }

    @TargetApi(21)
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) C0336R.layout.comment_layout);
        this.db = new DBhelper(this);
        box = (EditText) findViewById(C0336R.id.content);
        this.send = (ImageButton) findViewById(C0336R.id.send);
        this.listcmt = (RecyclerView) findViewById(C0336R.id.listcmt);
        this.pro = (ProgressBar) findViewById(C0336R.id.pro);
        this.tb = (TextView) findViewById(C0336R.id.arl);
        this.mLayoutManager = new LinearLayoutManager(this);
        getSupportActionBar().setTitle(BuildConfig.VERSION_NAME);
        getSupportActionBar().setCustomView(LayoutInflater.from(this).inflate(C0336R.layout.custom_bar_detail, null));
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        Bundle ext = getIntent().getExtras();
        if (ext != null) {
            this.idbv = ext.getString("idbv");
            this.title = ext.getString(ShareConstants.WEB_DIALOG_PARAM_TITLE);
        }
        this.turn = (ImageButton) findViewById(C0336R.id.turnback);
        this.tieude = (TextView) findViewById(C0801R.id.title);
        this.turn.setOnClickListener(this);
        this.send.setOnClickListener(this);
        if (VERSION.SDK_INT >= 24) {
            this.tieude.setText(Html.fromHtml(this.title, 0));
        } else {
            this.tieude.setText(Html.fromHtml(this.title));
        }
        this.tieude.setSelected(true);
        this.listcmt.setNestedScrollingEnabled(false);
        new loadcmt().execute(new String[]{BuildConfig.VERSION_NAME});
        this.listcmt.addOnScrollListener(new C09511());
    }

    public void onClick(View v) {
        if (v == this.turn) {
            finish();
        }
        if (v != this.send) {
            return;
        }
        if (box.getText().toString().equals(BuildConfig.VERSION_NAME)) {
            Toast.makeText(this, "B\u1ea1n ch\u01b0a nh\u1eadp n\u1ed9i dung", 0).show();
            return;
        }
        if (this.data_cmt.size() > 0) {
            clearData();
        }
        this.page = 1;
        new cmt().execute(new String[]{box.getText().toString()});
    }

    public static void setbox(String nd) {
        box.setText(nd);
    }

    public void clearData() {
        this.data_cmt.clear();
        this.adapter_cmt.notifyDataSetChanged();
    }
}
