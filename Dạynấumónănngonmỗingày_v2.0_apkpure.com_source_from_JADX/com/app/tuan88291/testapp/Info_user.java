package com.app.tuan88291.testapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore.Images.Media;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.facebook.appevents.AppEventsConstants;
import com.facebook.internal.NativeProtocol;
import com.facebook.share.internal.ShareConstants;
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

public class Info_user extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 234;
    private static String idfb;
    public static int lastVisibleItem;
    static TextView numpost;
    public static int page;
    public static int pastVisiblesItems;
    public static int totalItemCount;
    public static int visibleThreshold;
    private Adapter_info adapter;
    private Bitmap bitmap;
    List<Object> data;
    DBhelper db;
    private Uri filePath;
    private LoadingView ldv;
    private RecyclerView list;
    private boolean loading;
    LinearLayoutManager mLayoutManager;
    private String name;
    private String namefile;
    ProgressDialog pDialog;
    TextView tieude;
    ImageButton turn;

    /* renamed from: com.app.tuan88291.testapp.Info_user.1 */
    class C03241 implements OnClickListener {
        C03241() {
        }

        public void onClick(View v) {
            Info_user.this.finish();
        }
    }

    private class loadcover extends AsyncTask<String, Void, String> {
        private loadcover() {
        }

        protected String doInBackground(String... params) {
            HttpClient client = new DefaultHttpClient();
            HttpPost request = new HttpPost("http://cook.audition2.com/cover.php");
            List<NameValuePair> nameValuePair = new ArrayList(1);
            nameValuePair.add(new BasicNameValuePair(ShareConstants.WEB_DIALOG_PARAM_ID, "2"));
            nameValuePair.add(new BasicNameValuePair("idfb", Info_user.idfb));
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
        }

        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }

        protected void onProgressUpdate(Void... values) {
        }
    }

    private class loadnd extends AsyncTask<String, Void, String> {
        private loadnd() {
        }

        protected String doInBackground(String... params) {
            HttpResponse response = null;
            try {
                response = new DefaultHttpClient().execute(new HttpGet("http://cook.audition2.com/showndinfo.php?idfb=" + Info_user.idfb + "&myfb=" + Info_user.this.db.idfb() + "&page=" + Info_user.page));
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
            Info_user.this.ldv.start();
        }

        protected void onPostExecute(String result) {
            JSONException e;
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
                            List list = Info_user.this.data;
                            List list2 = list;
                            list2.add(new Data_home(json_data.getString("idbv"), json_data.getString("noidung"), json_data.getString("idfb"), json_data.getString(ShareConstants.MEDIA_TYPE), json_data.getString("date"), json_data.getString(ShareConstants.WEB_DIALOG_PARAM_NAME), json_data.getString("like"), json_data.getString("tit"), json_data.getString(NativeProtocol.WEB_DIALOG_URL), json_data.getString("stt"), json_data.getString("imgtype"), json_data.getString("theloai"), json_data.getString("id_type")));
                            i++;
                        } catch (JSONException e2) {
                            e = e2;
                            JSONArray jSONArray2 = jSONArray;
                        }
                    }
                    Info_user.this.adapter = new Adapter_info(Info_user.this, Info_user.this.data);
                    Info_user.this.list.setAdapter(Info_user.this.adapter);
                    Info_user.this.list.setLayoutManager(Info_user.this.mLayoutManager);
                    Info_user.this.loading = true;
                    Info_user.this.list.scrollToPosition(Info_user.pastVisiblesItems);
                } catch (JSONException e3) {
                    e = e3;
                    e.printStackTrace();
                    Info_user.this.ldv.stop();
                    super.onPostExecute(result);
                }
            }
            Info_user.this.ldv.stop();
            super.onPostExecute(result);
        }

        protected void onProgressUpdate(Void... values) {
        }
    }

    private static class loadtotal extends AsyncTask<String, Void, String> {
        private loadtotal() {
        }

        protected String doInBackground(String... params) {
            HttpClient client = new DefaultHttpClient();
            HttpPost request = new HttpPost("http://cook.audition2.com/cover.php");
            List<NameValuePair> nameValuePair = new ArrayList(1);
            nameValuePair.add(new BasicNameValuePair(ShareConstants.WEB_DIALOG_PARAM_ID, "3"));
            nameValuePair.add(new BasicNameValuePair("idfb", Info_user.idfb));
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
        }

        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }

        protected void onProgressUpdate(Void... values) {
        }
    }

    private class upanh extends AsyncTask<String, Void, String> {
        private upanh() {
        }

        protected String doInBackground(String... params) {
            String img = params[0];
            HttpClient client = new DefaultHttpClient();
            HttpPost request = new HttpPost("http://cook.audition2.com/cover.php");
            List<NameValuePair> nameValuePair = new ArrayList(1);
            nameValuePair.add(new BasicNameValuePair(ShareConstants.WEB_DIALOG_PARAM_ID, AppEventsConstants.EVENT_PARAM_VALUE_YES));
            nameValuePair.add(new BasicNameValuePair("idfb", Info_user.this.db.idfb()));
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
            Info_user.this.pDialog = new ProgressDialog(Info_user.this);
            Info_user.this.pDialog.setMessage("\u0110ang upload ...");
            Info_user.this.pDialog.setIndeterminate(false);
            Info_user.this.pDialog.setCancelable(true);
            Info_user.this.pDialog.show();
        }

        protected void onPostExecute(String result) {
            Toast.makeText(Info_user.this, "\u0110\u00e3 upload \u1ea3nh b\u00eca th\u00e0nh c\u00f4ng, load l\u1ea1i \u0111\u1ec3 th\u1ea5y \u1ea3nh b\u00eca!", 0).show();
            new loadcover(null).execute(new String[]{BuildConfig.VERSION_NAME});
            Info_user.this.pDialog.dismiss();
            super.onPostExecute(result);
        }

        protected void onProgressUpdate(Void... values) {
        }
    }

    /* renamed from: com.app.tuan88291.testapp.Info_user.2 */
    class C09582 extends OnScrollListener {
        C09582() {
        }

        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            Info_user.totalItemCount = Info_user.this.mLayoutManager.getItemCount();
            Info_user.lastVisibleItem = Info_user.this.mLayoutManager.findLastVisibleItemPosition();
            if (Info_user.this.loading && Info_user.totalItemCount <= Info_user.lastVisibleItem + Info_user.visibleThreshold) {
                Info_user.page++;
                Info_user.pastVisiblesItems = Info_user.this.mLayoutManager.findFirstVisibleItemPosition();
                new loadnd(null).execute(new String[]{BuildConfig.VERSION_NAME});
                Info_user.this.loading = false;
                Log.d("lodddddddddd", "page: " + Info_user.page);
            }
        }
    }

    public Info_user() {
        this.data = new ArrayList();
        this.loading = true;
    }

    static {
        page = 1;
        pastVisiblesItems = 0;
        visibleThreshold = 10;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) C0336R.layout.info_layout);
        setResult(-1);
        this.db = new DBhelper(this);
        Bundle ext = getIntent().getExtras();
        if (ext != null) {
            idfb = ext.getString("idfb");
            this.name = ext.getString(ShareConstants.WEB_DIALOG_PARAM_NAME);
        }
        page = 1;
        numpost = (TextView) findViewById(C0336R.id.numpost);
        this.ldv = (LoadingView) findViewById(C0336R.id.loading_view);
        this.list = (RecyclerView) findViewById(C0336R.id.list);
        this.mLayoutManager = new LinearLayoutManager(this, 1, false);
        getSupportActionBar().setTitle(BuildConfig.VERSION_NAME);
        View mCustomView = LayoutInflater.from(this).inflate(C0336R.layout.custom_bar_detail, null);
        getSupportActionBar().setCustomView(mCustomView);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        this.turn = (ImageButton) mCustomView.findViewById(C0336R.id.turnback);
        this.tieude = (TextView) mCustomView.findViewById(C0801R.id.title);
        this.tieude.setText(this.name);
        this.turn.setOnClickListener(new C03241());
        try {
            this.data.add(new Data_info_head((String) new loadcover().execute(new String[]{BuildConfig.VERSION_NAME}).get(), loadtt(), this.name, idfb));
            this.adapter = new Adapter_info(this, this.data);
            this.list.setAdapter(this.adapter);
            this.list.setLayoutManager(this.mLayoutManager);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e2) {
            e2.printStackTrace();
        }
        new loadnd().execute(new String[]{BuildConfig.VERSION_NAME});
        this.list.addOnScrollListener(new C09582());
    }

    public void clearData() {
        if (!this.data.isEmpty()) {
            this.data.clear();
            this.adapter.notifyDataSetChanged();
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.lang.String loadtt() {
        /*
        r2 = 0;
        r1 = new com.app.tuan88291.testapp.Info_user$loadtotal;	 Catch:{ InterruptedException -> 0x001a, ExecutionException -> 0x0020 }
        r3 = 0;
        r1.<init>();	 Catch:{ InterruptedException -> 0x001a, ExecutionException -> 0x0020 }
        r3 = 1;
        r3 = new java.lang.String[r3];	 Catch:{ InterruptedException -> 0x001a, ExecutionException -> 0x0020 }
        r4 = 0;
        r5 = "";
        r3[r4] = r5;	 Catch:{ InterruptedException -> 0x001a, ExecutionException -> 0x0020 }
        r1 = r1.execute(r3);	 Catch:{ InterruptedException -> 0x001a, ExecutionException -> 0x0020 }
        r1 = r1.get();	 Catch:{ InterruptedException -> 0x001a, ExecutionException -> 0x0020 }
        r1 = (java.lang.String) r1;	 Catch:{ InterruptedException -> 0x001a, ExecutionException -> 0x0020 }
    L_0x0019:
        return r1;
    L_0x001a:
        r0 = move-exception;
        r0.printStackTrace();
    L_0x001e:
        r1 = r2;
        goto L_0x0019;
    L_0x0020:
        r0 = move-exception;
        r0.printStackTrace();
        goto L_0x001e;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.app.tuan88291.testapp.Info_user.loadtt():java.lang.String");
    }

    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction("android.intent.action.GET_CONTENT");
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == -1 && data != null && data.getData() != null) {
            this.filePath = data.getData();
            try {
                this.bitmap = Media.getBitmap(getContentResolver(), this.filePath);
                new upanh().execute(new String[]{getStringImage(this.bitmap)});
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(CompressFormat.JPEG, 20, baos);
        return Base64.encodeToString(baos.toByteArray(), 0);
    }
}
