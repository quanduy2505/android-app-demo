package com.app.tuan88291.testapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog.Builder;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.facebook.internal.AnalyticsEvents;
import com.facebook.share.internal.ShareConstants;
import java.io.BufferedReader;
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
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import rx.android.BuildConfig;

public class Adapter_grid extends ArrayAdapter<list_grid> {
    Adapter_grid adapter;
    Context context;
    ArrayList<list_grid> data;
    private String idbv;
    int layoutResourceId;
    private String namefile;
    ProgressDialog pDialog;

    /* renamed from: com.app.tuan88291.testapp.Adapter_grid.1 */
    class C02561 implements OnClickListener {
        final /* synthetic */ int val$position;

        C02561(int i) {
            this.val$position = i;
        }

        public void onClick(View v) {
            Adapter_grid.this.arlet(this.val$position);
        }
    }

    /* renamed from: com.app.tuan88291.testapp.Adapter_grid.2 */
    class C02572 implements OnClickListener {
        final /* synthetic */ int val$position;

        C02572(int i) {
            this.val$position = i;
        }

        public void onClick(View v) {
            Adapter_grid.this.xoalink(this.val$position);
        }
    }

    /* renamed from: com.app.tuan88291.testapp.Adapter_grid.3 */
    class C02583 implements OnClickListener {
        final /* synthetic */ int val$position;

        C02583(int i) {
            this.val$position = i;
        }

        public void onClick(View v) {
            Adapter_grid.this.xoalink(this.val$position);
        }
    }

    /* renamed from: com.app.tuan88291.testapp.Adapter_grid.4 */
    class C02594 implements DialogInterface.OnClickListener {
        final /* synthetic */ int val$position;

        C02594(int i) {
            this.val$position = i;
        }

        public void onClick(DialogInterface arg0, int arg1) {
            Adapter_grid.this.namefile = ((list_grid) Adapter_grid.this.data.get(this.val$position)).getTenfile();
            new xoa2(null).execute(new String[]{Adapter_grid.this.namefile});
            Adapter_grid.this.data.remove(this.val$position);
            Adapter_grid.this.adapter.notifyDataSetChanged();
        }
    }

    /* renamed from: com.app.tuan88291.testapp.Adapter_grid.5 */
    class C02605 implements DialogInterface.OnClickListener {
        C02605() {
        }

        public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
        }
    }

    /* renamed from: com.app.tuan88291.testapp.Adapter_grid.6 */
    class C02616 implements DialogInterface.OnClickListener {
        final /* synthetic */ int val$position;

        C02616(int i) {
            this.val$position = i;
        }

        public void onClick(DialogInterface arg0, int arg1) {
            Adapter_grid.this.namefile = ((list_grid) Adapter_grid.this.data.get(this.val$position)).getTenfile();
            new xoa(null).execute(new String[]{Adapter_grid.this.namefile});
            Adapter_grid.this.data.remove(this.val$position);
            Adapter_grid.this.adapter.notifyDataSetChanged();
        }
    }

    /* renamed from: com.app.tuan88291.testapp.Adapter_grid.7 */
    class C02627 implements DialogInterface.OnClickListener {
        C02627() {
        }

        public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
        }
    }

    static class RecordHolder {
        ImageView imageItem;
        TextView txtTitle;
        ImageButton xoa;

        RecordHolder() {
        }
    }

    private class xoa2 extends AsyncTask<String, Void, String> {
        private xoa2() {
        }

        protected String doInBackground(String... params) {
            String link = params[0];
            HttpClient client = new DefaultHttpClient();
            HttpPost request = new HttpPost("http://cook.audition2.com/img.php");
            List<NameValuePair> nameValuePair = new ArrayList(1);
            nameValuePair.add(new BasicNameValuePair(ShareConstants.WEB_DIALOG_PARAM_ID, "8"));
            nameValuePair.add(new BasicNameValuePair("idforum", Adapter_grid.this.idbv));
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
            Adapter_grid.this.pDialog = new ProgressDialog(Adapter_grid.this.context);
            Adapter_grid.this.pDialog.setMessage("\u0110ang x\u00f3a ...");
            Adapter_grid.this.pDialog.setIndeterminate(false);
            Adapter_grid.this.pDialog.setCancelable(true);
            Adapter_grid.this.pDialog.show();
        }

        protected void onPostExecute(String result) {
            Toast.makeText(Adapter_grid.this.context, "\u0111\u00e3 x\u00f3a", 0).show();
            Adapter_grid.this.pDialog.dismiss();
            super.onPostExecute(result);
        }

        protected void onProgressUpdate(Void... values) {
        }
    }

    private class xoa extends AsyncTask<String, Void, String> {
        private xoa() {
        }

        protected String doInBackground(String... params) {
            String link = params[0];
            HttpClient client = new DefaultHttpClient();
            HttpPost request = new HttpPost("http://cook.audition2.com/img.php");
            List<NameValuePair> nameValuePair = new ArrayList(1);
            nameValuePair.add(new BasicNameValuePair(ShareConstants.WEB_DIALOG_PARAM_ID, "6"));
            nameValuePair.add(new BasicNameValuePair("idforum", Adapter_grid.this.idbv));
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
            Adapter_grid.this.pDialog = new ProgressDialog(Adapter_grid.this.context);
            Adapter_grid.this.pDialog.setMessage("\u0110ang x\u00f3a ...");
            Adapter_grid.this.pDialog.setIndeterminate(false);
            Adapter_grid.this.pDialog.setCancelable(true);
            Adapter_grid.this.pDialog.show();
        }

        protected void onPostExecute(String result) {
            Toast.makeText(Adapter_grid.this.context, "\u0111\u00e3 x\u00f3a", 0).show();
            Adapter_grid.this.pDialog.dismiss();
            super.onPostExecute(result);
        }

        protected void onProgressUpdate(Void... values) {
        }
    }

    public Adapter_grid(Context context, int layoutResourceId, ArrayList<list_grid> data) {
        super(context, layoutResourceId, data);
        this.data = new ArrayList();
        this.adapter = this;
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        RecordHolder holder;
        View row = convertView;
        if (row == null) {
            row = ((Activity) this.context).getLayoutInflater().inflate(this.layoutResourceId, parent, false);
            holder = new RecordHolder();
            holder.imageItem = (ImageView) row.findViewById(C0336R.id.item_image);
            holder.xoa = (ImageButton) row.findViewById(C0336R.id.xoa);
            row.setTag(holder);
        } else {
            holder = (RecordHolder) row.getTag();
        }
        list_grid item = (list_grid) this.data.get(position);
        this.idbv = item.getIdbv();
        if (item.getType().equals("img")) {
            Glide.with(this.context).load("http://cook.audition2.com/photos/" + item.getIdbv() + "/" + item.getTenfile()).into(holder.imageItem);
            holder.imageItem.setScaleType(ScaleType.CENTER_CROP);
            holder.xoa.setOnClickListener(new C02561(position));
        } else if (item.getType().equals(ShareConstants.WEB_DIALOG_PARAM_LINK)) {
            Glide.with(this.context).load(item.getTenfile()).into(holder.imageItem);
            holder.imageItem.setScaleType(ScaleType.CENTER_CROP);
            holder.xoa.setOnClickListener(new C02572(position));
        } else if (item.getType().equals(AnalyticsEvents.PARAMETER_SHARE_DIALOG_CONTENT_VIDEO)) {
            Glide.with(this.context).load("https://i.ytimg.com/vi/" + item.getTenfile().replace(item.getTenfile().split("[?v=]")[0] + "?v=", BuildConfig.VERSION_NAME).replace("&feature=youtu.be", BuildConfig.VERSION_NAME) + "/sddefault.jpg").into(holder.imageItem);
            holder.imageItem.setScaleType(ScaleType.CENTER_CROP);
            holder.xoa.setOnClickListener(new C02583(position));
        }
        return row;
    }

    public void arlet(int position) {
        Builder alertDialogBuilder = new Builder(this.context);
        alertDialogBuilder.setMessage((CharSequence) "B\u1ea1n c\u00f3 mu\u1ed1n x\u00f3a \u1ea3nh n\u00e0y?");
        alertDialogBuilder.setPositiveButton((CharSequence) "C\u00f3", new C02594(position));
        alertDialogBuilder.setNegativeButton((CharSequence) "Kh\u00f4ng", new C02605());
        alertDialogBuilder.create().show();
    }

    public void xoalink(int position) {
        Builder alertDialogBuilder = new Builder(this.context);
        alertDialogBuilder.setMessage((CharSequence) "B\u1ea1n c\u00f3 mu\u1ed1n x\u00f3a \u1ea3nh n\u00e0y?");
        alertDialogBuilder.setPositiveButton((CharSequence) "C\u00f3", new C02616(position));
        alertDialogBuilder.setNegativeButton((CharSequence) "Kh\u00f4ng", new C02627());
        alertDialogBuilder.create().show();
    }
}
