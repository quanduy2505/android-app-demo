package com.app.tuan88291.testapp;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore.Images.Media;
import android.support.v7.app.AlertDialog.Builder;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.facebook.internal.AnalyticsEvents;
import com.facebook.share.internal.ShareConstants;
import com.firebase.client.core.Constants;
import com.google.android.gms.C0598R;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
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
import rx.android.BuildConfig;

public class New_post extends AppCompatActivity implements OnClickListener {
    private static final int PICK_IMAGE_REQUEST = 234;
    private ImageView avatar;
    private Bitmap bitmap;
    Context cont;
    Adapter_grid customGridAdapter;
    DBhelper db;
    private Uri filePath;
    ArrayList<list_grid> gridArray;
    private GridView gridView;
    private String idbv;
    private String idfb;
    private ImageView img;
    AutoCompleteTextView keytype;
    private String kieu;
    private String name;
    private String namefile;
    private EditText nd;
    private TextView nickname;
    private String noidung;
    ProgressDialog pDialog;
    private ImageButton photo;
    private ImageButton send;
    private String tieude;
    private EditText tit;
    private ImageButton turn;

    /* renamed from: com.app.tuan88291.testapp.New_post.1 */
    class C03271 implements OnClickListener {
        final /* synthetic */ Dialog val$dialog;

        C03271(Dialog dialog) {
            this.val$dialog = dialog;
        }

        public void onClick(View v) {
            New_post.this.showFileChooser();
            this.val$dialog.dismiss();
        }
    }

    /* renamed from: com.app.tuan88291.testapp.New_post.2 */
    class C03282 implements OnClickListener {
        final /* synthetic */ Dialog val$dialog;

        C03282(Dialog dialog) {
            this.val$dialog = dialog;
        }

        public void onClick(View v) {
            New_post.this.inputurl();
            this.val$dialog.dismiss();
        }
    }

    /* renamed from: com.app.tuan88291.testapp.New_post.3 */
    class C03293 implements OnClickListener {
        final /* synthetic */ Dialog val$dialog;

        C03293(Dialog dialog) {
            this.val$dialog = dialog;
        }

        public void onClick(View v) {
            New_post.this.inputvideo();
            this.val$dialog.dismiss();
        }
    }

    /* renamed from: com.app.tuan88291.testapp.New_post.4 */
    class C03304 implements OnClickListener {
        final /* synthetic */ Dialog val$dialog;
        final /* synthetic */ EditText val$url;

        C03304(EditText editText, Dialog dialog) {
            this.val$url = editText;
            this.val$dialog = dialog;
        }

        public void onClick(View v) {
            String link = this.val$url.getText().toString();
            if (link.equals(BuildConfig.VERSION_NAME)) {
                Toast.makeText(New_post.this, "B\u1ea1n ch\u01b0a nh\u1eadp url", 0).show();
            } else if (link.indexOf("http://") < 0 || (link.indexOf(".jpg") < 0 && link.indexOf(".jpeg") < 0 && link.indexOf(".png") < 0 && link.indexOf(".gif") < 0)) {
                Toast.makeText(New_post.this, "Kh\u00f4ng ph\u1ea3i link \u1ea3nh", 0).show();
            } else {
                new linkimg(null).execute(new String[]{link});
                this.val$dialog.dismiss();
            }
        }
    }

    /* renamed from: com.app.tuan88291.testapp.New_post.5 */
    class C03315 implements OnClickListener {
        final /* synthetic */ Dialog val$dialog;

        C03315(Dialog dialog) {
            this.val$dialog = dialog;
        }

        public void onClick(View v) {
            this.val$dialog.dismiss();
        }
    }

    /* renamed from: com.app.tuan88291.testapp.New_post.6 */
    class C03326 implements OnClickListener {
        final /* synthetic */ Dialog val$dialog;
        final /* synthetic */ EditText val$url;

        C03326(EditText editText, Dialog dialog) {
            this.val$url = editText;
            this.val$dialog = dialog;
        }

        public void onClick(View v) {
            String link = this.val$url.getText().toString();
            if (link.equals(BuildConfig.VERSION_NAME)) {
                Toast.makeText(New_post.this, "B\u1ea1n ch\u01b0a nh\u1eadp url", 0).show();
            } else if (link.indexOf("https://") < 0 || (link.indexOf("youtube.com") < 0 && link.indexOf("v=") < 0)) {
                Toast.makeText(New_post.this, "Kh\u00f4ng ph\u1ea3i link youtube", 0).show();
            } else {
                new video(null).execute(new String[]{link});
                this.val$dialog.dismiss();
            }
        }
    }

    /* renamed from: com.app.tuan88291.testapp.New_post.7 */
    class C03337 implements OnClickListener {
        final /* synthetic */ Dialog val$dialog;

        C03337(Dialog dialog) {
            this.val$dialog = dialog;
        }

        public void onClick(View v) {
            this.val$dialog.dismiss();
        }
    }

    /* renamed from: com.app.tuan88291.testapp.New_post.8 */
    class C03348 implements DialogInterface.OnClickListener {
        C03348() {
        }

        public void onClick(DialogInterface arg0, int arg1) {
            new xoa(null).execute(new String[]{BuildConfig.VERSION_NAME});
        }
    }

    /* renamed from: com.app.tuan88291.testapp.New_post.9 */
    class C03359 implements DialogInterface.OnClickListener {
        C03359() {
        }

        public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
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
                try {
                    JSONArray jArray = new JSONArray(result);
                    try {
                        String[] str = new String[jArray.length()];
                        for (int i = 0; i < jArray.length(); i++) {
                            str[i] = jArray.getJSONObject(i).getString(ShareConstants.WEB_DIALOG_PARAM_NAME);
                        }
                        ArrayAdapter<String> adp = new ArrayAdapter(New_post.this.getBaseContext(), 17367050, str);
                        adp.setDropDownViewResource(17367046);
                        New_post.this.keytype.setAdapter(adp);
                    } catch (JSONException e2) {
                        e = e2;
                        JSONArray jSONArray = jArray;
                        e.printStackTrace();
                        super.onPostExecute(result);
                    }
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

    private class inimg extends AsyncTask<String, Void, String> {
        private inimg() {
        }

        protected String doInBackground(String... params) {
            String anh = params[0];
            HttpClient client = new DefaultHttpClient();
            HttpPost request = new HttpPost("http://cook.audition2.com/img.php");
            List<NameValuePair> nameValuePair = new ArrayList(1);
            nameValuePair.add(new BasicNameValuePair(ShareConstants.WEB_DIALOG_PARAM_ID, Constants.WIRE_PROTOCOL_VERSION));
            nameValuePair.add(new BasicNameValuePair("idforum", New_post.this.idbv));
            nameValuePair.add(new BasicNameValuePair("img", anh));
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
            New_post.this.pDialog = new ProgressDialog(New_post.this);
            New_post.this.pDialog.setMessage("\u0110ang upload ...");
            New_post.this.pDialog.setIndeterminate(false);
            New_post.this.pDialog.setCancelable(true);
            New_post.this.pDialog.show();
        }

        protected void onPostExecute(String result) {
            New_post.this.gridArray.add(new list_grid(New_post.this.idbv, result, "img"));
            New_post.this.customGridAdapter.notifyDataSetChanged();
            New_post.this.gridView.setAdapter(New_post.this.customGridAdapter);
            New_post.this.pDialog.dismiss();
            super.onPostExecute(result);
        }

        protected void onProgressUpdate(Void... values) {
        }
    }

    private class linkimg extends AsyncTask<String, Void, String> {
        private linkimg() {
        }

        protected String doInBackground(String... params) {
            String link = params[0];
            HttpClient client = new DefaultHttpClient();
            HttpPost request = new HttpPost("http://cook.audition2.com/img.php");
            List<NameValuePair> nameValuePair = new ArrayList(1);
            nameValuePair.add(new BasicNameValuePair(ShareConstants.WEB_DIALOG_PARAM_ID, "7"));
            nameValuePair.add(new BasicNameValuePair("idforum", New_post.this.idbv));
            nameValuePair.add(new BasicNameValuePair("tenfile", link));
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
            New_post.this.pDialog = new ProgressDialog(New_post.this);
            New_post.this.pDialog.setMessage("\u0110ang upload ...");
            New_post.this.pDialog.setIndeterminate(false);
            New_post.this.pDialog.setCancelable(true);
            New_post.this.pDialog.show();
        }

        protected void onPostExecute(String result) {
            New_post.this.gridArray.add(new list_grid(New_post.this.idbv, result, ShareConstants.WEB_DIALOG_PARAM_LINK));
            New_post.this.customGridAdapter.notifyDataSetChanged();
            New_post.this.gridView.setAdapter(New_post.this.customGridAdapter);
            super.onPostExecute(result);
            New_post.this.pDialog.dismiss();
        }

        protected void onProgressUpdate(Void... values) {
        }
    }

    private class loadingdk extends AsyncTask<String, Void, String> {
        private loadingdk() {
        }

        protected String doInBackground(String... params) {
            HttpClient client = new DefaultHttpClient();
            HttpPost request = new HttpPost("http://cook.audition2.com/post.php");
            List<NameValuePair> nameValuePair = new ArrayList(1);
            nameValuePair.add(new BasicNameValuePair(ShareConstants.WEB_DIALOG_PARAM_ID, Constants.WIRE_PROTOCOL_VERSION));
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
            New_post.this.pDialog = new ProgressDialog(New_post.this);
            New_post.this.pDialog.setMessage("\u0110ang load ...");
            New_post.this.pDialog.setIndeterminate(false);
            New_post.this.pDialog.setCancelable(true);
            New_post.this.pDialog.show();
        }

        protected void onPostExecute(String result) {
            New_post.this.idbv = result;
            New_post.this.pDialog.dismiss();
            super.onPostExecute(result);
        }

        protected void onProgressUpdate(Void... values) {
        }
    }

    private class upbai extends AsyncTask<String, Void, String> {
        private upbai() {
        }

        protected String doInBackground(String... params) {
            HttpClient client = new DefaultHttpClient();
            HttpPost request = new HttpPost("http://cook.audition2.com/post.php");
            List<NameValuePair> nameValuePair = new ArrayList(1);
            nameValuePair.add(new BasicNameValuePair(ShareConstants.WEB_DIALOG_PARAM_ID, "7"));
            nameValuePair.add(new BasicNameValuePair("idbv", New_post.this.idbv));
            nameValuePair.add(new BasicNameValuePair(ShareConstants.WEB_DIALOG_PARAM_NAME, New_post.this.db.ten()));
            nameValuePair.add(new BasicNameValuePair("idfb", New_post.this.db.idfb()));
            nameValuePair.add(new BasicNameValuePair("nd", New_post.this.noidung));
            nameValuePair.add(new BasicNameValuePair("tit", New_post.this.tieude));
            nameValuePair.add(new BasicNameValuePair(ShareConstants.MEDIA_TYPE, New_post.this.kieu));
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
            New_post.this.pDialog = new ProgressDialog(New_post.this);
            New_post.this.pDialog.setMessage("\u0110ang g\u1eedi ...");
            New_post.this.pDialog.setIndeterminate(false);
            New_post.this.pDialog.setCancelable(true);
            New_post.this.pDialog.show();
        }

        protected void onPostExecute(String result) {
            Toast.makeText(New_post.this, "\u0110\u00e3 g\u1eedi b\u00e0i th\u00e0nh c\u00f4ng, b\u00e0i c\u1ee7a b\u1ea1n s\u1ebd \u0111\u01b0\u1ee3c ki\u1ec3m duy\u1ec7t tr\u01b0\u1edbc khi hi\u1ec7n l\u00ean", 0).show();
            Intent scd = new Intent(New_post.this, ContentView.class);
            Home hm = new Home();
            Home.idtype = BuildConfig.VERSION_NAME;
            Home.page = 1;
            Home.pastVisiblesItems = 0;
            New_post.this.startActivity(scd);
            New_post.this.pDialog.dismiss();
            super.onPostExecute(result);
        }

        protected void onProgressUpdate(Void... values) {
        }
    }

    private class video extends AsyncTask<String, Void, String> {
        private video() {
        }

        protected String doInBackground(String... params) {
            String link = params[0];
            HttpClient client = new DefaultHttpClient();
            HttpPost request = new HttpPost("http://cook.audition2.com/img.php");
            List<NameValuePair> nameValuePair = new ArrayList(1);
            nameValuePair.add(new BasicNameValuePair(ShareConstants.WEB_DIALOG_PARAM_ID, "9"));
            nameValuePair.add(new BasicNameValuePair("idforum", New_post.this.idbv));
            nameValuePair.add(new BasicNameValuePair("tenfile", link));
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
            New_post.this.pDialog = new ProgressDialog(New_post.this);
            New_post.this.pDialog.setMessage("\u0110ang upload ...");
            New_post.this.pDialog.setIndeterminate(false);
            New_post.this.pDialog.setCancelable(true);
            New_post.this.pDialog.show();
        }

        protected void onPostExecute(String result) {
            New_post.this.gridArray.add(new list_grid(New_post.this.idbv, result, AnalyticsEvents.PARAMETER_SHARE_DIALOG_CONTENT_VIDEO));
            New_post.this.customGridAdapter.notifyDataSetChanged();
            New_post.this.gridView.setAdapter(New_post.this.customGridAdapter);
            super.onPostExecute(result);
            New_post.this.pDialog.dismiss();
        }

        protected void onProgressUpdate(Void... values) {
        }
    }

    private class xoa extends AsyncTask<String, Void, String> {
        private xoa() {
        }

        protected String doInBackground(String... params) {
            HttpClient client = new DefaultHttpClient();
            HttpPost request = new HttpPost("http://cook.audition2.com/post.php");
            List<NameValuePair> nameValuePair = new ArrayList(1);
            nameValuePair.add(new BasicNameValuePair(ShareConstants.WEB_DIALOG_PARAM_ID, "6"));
            nameValuePair.add(new BasicNameValuePair("idbv", New_post.this.idbv));
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
            New_post.this.pDialog = new ProgressDialog(New_post.this);
            New_post.this.pDialog.setMessage("\u0110ang h\u1ee7y ...");
            New_post.this.pDialog.setIndeterminate(false);
            New_post.this.pDialog.setCancelable(true);
            New_post.this.pDialog.show();
        }

        protected void onPostExecute(String result) {
            New_post.this.finish();
            New_post.this.pDialog.dismiss();
            super.onPostExecute(result);
        }

        protected void onProgressUpdate(Void... values) {
        }
    }

    public New_post() {
        this.gridArray = new ArrayList();
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) C0336R.layout.new_layout);
        getSupportActionBar().setTitle(BuildConfig.VERSION_NAME);
        getSupportActionBar().setCustomView(LayoutInflater.from(this).inflate(C0336R.layout.custom_bar, null));
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        this.db = new DBhelper(this);
        Bundle ext = getIntent().getExtras();
        if (ext != null) {
            this.idfb = ext.getString("idfb");
            this.name = ext.getString(ShareConstants.WEB_DIALOG_PARAM_NAME);
        }
        this.nickname = (TextView) findViewById(C0336R.id.nickname);
        this.avatar = (ImageView) findViewById(C0336R.id.avatar);
        this.keytype = (AutoCompleteTextView) findViewById(C0336R.id.typekey);
        this.keytype.setThreshold(1);
        new gettype().execute(new String[]{BuildConfig.VERSION_NAME});
        this.nickname.setText(this.name);
        UrlImageViewHelper.setUrlDrawable(this.avatar, "http://graph.facebook.com/" + this.idfb + "/picture?type=large");
        this.turn = (ImageButton) findViewById(C0336R.id.turnback);
        this.send = (ImageButton) findViewById(C0336R.id.send);
        this.photo = (ImageButton) findViewById(C0336R.id.photo);
        this.tit = (EditText) findViewById(C0336R.id.tit);
        this.nd = (EditText) findViewById(C0336R.id.nd);
        this.turn.setOnClickListener(this);
        this.send.setOnClickListener(this);
        this.photo.setOnClickListener(this);
        this.gridView = (GridView) findViewById(C0336R.id.grid);
        this.customGridAdapter = new Adapter_grid(this, C0336R.layout.custom_grid, this.gridArray);
        this.tit.requestFocus();
        new loadingdk().execute(new String[]{BuildConfig.VERSION_NAME});
    }

    public void onClick(View v) {
        if (v == this.turn) {
            arlet();
        }
        if (v == this.send) {
            if (this.nd.getText().toString().equals(BuildConfig.VERSION_NAME) || this.tit.getText().toString().equals(BuildConfig.VERSION_NAME)) {
                Toast.makeText(this, "Vui l\u00f2ng nh\u1eadp \u0111\u1ea7y \u0111\u1ee7 th\u00f4ng tin", 0).show();
            } else {
                sendpost();
            }
        }
        if (v == this.photo) {
            show();
        }
    }

    public void show() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(C0336R.layout.arlet);
        dialog.setTitle("Ch\u1ecdn t\u00e1c v\u1ee5:");
        Button url = (Button) dialog.findViewById(C0598R.id.url);
        Button video = (Button) dialog.findViewById(C0336R.id.video);
        ((Button) dialog.findViewById(C0336R.id.storage)).setOnClickListener(new C03271(dialog));
        url.setOnClickListener(new C03282(dialog));
        video.setOnClickListener(new C03293(dialog));
        dialog.show();
    }

    public void inputurl() {
        Dialog dialog = new Dialog(this);
        dialog.getWindow().requestFeature(1);
        dialog.setContentView(C0336R.layout.custom_input_url);
        Button exit = (Button) dialog.findViewById(C0336R.id.exit);
        ((Button) dialog.findViewById(C0336R.id.upload)).setOnClickListener(new C03304((EditText) dialog.findViewById(C0598R.id.url), dialog));
        exit.setOnClickListener(new C03315(dialog));
        dialog.show();
    }

    public void inputvideo() {
        Dialog dialog = new Dialog(this);
        dialog.getWindow().requestFeature(1);
        dialog.setContentView(C0336R.layout.custom_input_url);
        Button upload = (Button) dialog.findViewById(C0336R.id.upload);
        Button exit = (Button) dialog.findViewById(C0336R.id.exit);
        EditText url = (EditText) dialog.findViewById(C0598R.id.url);
        ((TextView) dialog.findViewById(C0336R.id.tieude)).setText("Nh\u1eadp url c\u1ee7a video:");
        url.setHint("https://www.youtube.com/watch?v=FNkYaTCyF6w");
        upload.setOnClickListener(new C03326(url, dialog));
        exit.setOnClickListener(new C03337(dialog));
        dialog.show();
    }

    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(CompressFormat.JPEG, 17, baos);
        return Base64.encodeToString(baos.toByteArray(), 0);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == -1 && data != null && data.getData() != null) {
            this.filePath = data.getData();
            try {
                this.bitmap = Media.getBitmap(getContentResolver(), this.filePath);
                new inimg().execute(new String[]{getStringImage(this.bitmap)});
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction("android.intent.action.GET_CONTENT");
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == 4 && event.getRepeatCount() == 0) {
            arlet();
        }
        return super.onKeyDown(keyCode, event);
    }

    public void arlet() {
        Builder alertDialogBuilder = new Builder(this);
        alertDialogBuilder.setMessage((CharSequence) "B\u1ea1n c\u00f3 mu\u1ed1n h\u1ee7y b\u00e0i vi\u1ebft?");
        alertDialogBuilder.setPositiveButton((CharSequence) "C\u00f3", new C03348());
        alertDialogBuilder.setNegativeButton((CharSequence) "Kh\u00f4ng", new C03359());
        alertDialogBuilder.create().show();
    }

    public void sendpost() {
        Builder alertDialogBuilder = new Builder(this);
        alertDialogBuilder.setMessage((CharSequence) "B\u1ea1n c\u00f3 mu\u1ed1n g\u1eedi b\u00e0i vi\u1ebft n\u00e0y?");
        alertDialogBuilder.setPositiveButton((CharSequence) "C\u00f3", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                New_post.this.tieude = New_post.this.tit.getText().toString();
                New_post.this.noidung = New_post.this.nd.getText().toString();
                New_post.this.kieu = New_post.this.keytype.getText().toString();
                new upbai(null).execute(new String[]{BuildConfig.VERSION_NAME});
            }
        });
        alertDialogBuilder.setNegativeButton((CharSequence) "Kh\u00f4ng", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialogBuilder.create().show();
    }
}
