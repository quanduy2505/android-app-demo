package com.app.tuan88291.testapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog.Builder;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import com.facebook.internal.NativeProtocol;
import com.facebook.share.internal.ShareConstants;
import com.firebase.client.core.Constants;
import com.nightonke.boommenu.BoomMenuButton;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout.OnRefreshListener;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import rx.android.BuildConfig;

public class Home extends Fragment {
    static List<Data_home> data;
    private static DBhelper db;
    public static String idtype;
    private static LoadingView ldv;
    private static boolean loading;
    private static Adapter_home mAdapter;
    static LinearLayoutManager mLayoutManager;
    private static RecyclerView mRVFishPrice;
    private static SwipyRefreshLayout mSwipyRefreshLayout;
    public static int page;
    public static int pastVisiblesItems;
    public static Context thiscontext;
    private BoomMenuButton bmb;
    private ContentView ctv;
    private ImageView img;
    int lastVisibleItem;
    private Button nut;
    private int previousTotal;
    int totalItemCount;
    int visibleThreshold;

    /* renamed from: com.app.tuan88291.testapp.Home.3 */
    class C03223 implements OnClickListener {
        C03223() {
        }

        public void onClick(DialogInterface arg0, int arg1) {
            Intent i = new Intent("android.intent.action.VIEW");
            i.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.app.tuan88291.testapp"));
            Home.this.startActivity(i);
            arg0.dismiss();
        }
    }

    /* renamed from: com.app.tuan88291.testapp.Home.4 */
    class C03234 implements OnClickListener {
        C03234() {
        }

        public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
        }
    }

    private static class loadingdk extends AsyncTask<String, Void, String> {
        private loadingdk() {
        }

        protected String doInBackground(String... params) {
            String pg = String.valueOf(Home.page);
            HttpResponse response = null;
            try {
                response = new DefaultHttpClient().execute(new HttpGet("http://cook.audition2.com/forum.php?page=" + pg + "&myfb=" + Home.db.idfb() + "&type=" + params[0]));
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
            Home.ldv.start();
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
                            List list = Home.data;
                            List list2 = list;
                            list2.add(new Data_home(json_data.getString("idbv"), json_data.getString("noidung"), json_data.getString("idfb"), json_data.getString(ShareConstants.MEDIA_TYPE), json_data.getString("date"), json_data.getString(ShareConstants.WEB_DIALOG_PARAM_NAME), json_data.getString("like"), json_data.getString("tit"), json_data.getString(NativeProtocol.WEB_DIALOG_URL), json_data.getString("stt"), json_data.getString("imgtype"), json_data.getString("theloai"), json_data.getString("idtheloai")));
                            i++;
                        } catch (JSONException e2) {
                            e = e2;
                            JSONArray jSONArray2 = jSONArray;
                        }
                    }
                    Home.mAdapter = new Adapter_home(Home.thiscontext, Home.data);
                    Home.mRVFishPrice.setAdapter(Home.mAdapter);
                    Home.mRVFishPrice.setLayoutManager(Home.mLayoutManager);
                    Home.loading = true;
                    Home.mRVFishPrice.scrollToPosition(Home.pastVisiblesItems);
                } catch (JSONException e3) {
                    e = e3;
                    e.printStackTrace();
                    Home.ldv.stop();
                    super.onPostExecute(result);
                }
            }
            Home.ldv.stop();
            super.onPostExecute(result);
        }

        protected void onProgressUpdate(Void... values) {
        }
    }

    private class update extends AsyncTask<String, Void, String> {
        private update() {
        }

        protected String doInBackground(String... params) {
            HttpClient client = new DefaultHttpClient();
            HttpPost request = new HttpPost("http://cook.audition2.com/update.php");
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
        }

        protected void onPostExecute(String result) {
            if (result.equals("new1")) {
                Home.this.upg();
            }
            super.onPostExecute(result);
        }

        protected void onProgressUpdate(Void... values) {
        }
    }

    /* renamed from: com.app.tuan88291.testapp.Home.1 */
    class C09561 extends OnScrollListener {
        C09561() {
        }

        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            if (dy > 0) {
                Home.this.ctv;
                ContentView.setmt();
                Home.this.ctv;
                ContentView.setgone(0);
            } else {
                Home.this.ctv;
                ContentView.setgone(1);
            }
            Home.this.totalItemCount = Home.mLayoutManager.getItemCount();
            Home.this.lastVisibleItem = Home.mLayoutManager.findLastVisibleItemPosition();
            if (Home.loading && Home.this.totalItemCount <= Home.this.lastVisibleItem + Home.this.visibleThreshold) {
                Log.d("lodddddddddd", "page: " + Home.page);
                Home.page++;
                Home.pastVisiblesItems = Home.mLayoutManager.findFirstVisibleItemPosition();
                new loadingdk().execute(new String[]{Home.idtype});
                Home.loading = false;
            }
        }
    }

    /* renamed from: com.app.tuan88291.testapp.Home.2 */
    class C09572 implements OnRefreshListener {

        /* renamed from: com.app.tuan88291.testapp.Home.2.1 */
        class C03211 implements Runnable {

            /* renamed from: com.app.tuan88291.testapp.Home.2.1.1 */
            class C03201 implements Runnable {
                C03201() {
                }

                public void run() {
                    Home.mSwipyRefreshLayout.setRefreshing(false);
                }
            }

            C03211() {
            }

            public void run() {
                Home.this.getActivity().runOnUiThread(new C03201());
            }
        }

        C09572() {
        }

        public void onRefresh(SwipyRefreshLayoutDirection direction) {
            Home.clearData();
            Home.this.ctv;
            ContentView.puttitle("Scook");
            Home.idtype = BuildConfig.VERSION_NAME;
            Home.page = 1;
            new loadingdk().execute(new String[]{Home.idtype});
            new Handler().postDelayed(new C03211(), 1000);
        }
    }

    public Home() {
        this.visibleThreshold = 10;
        this.previousTotal = 0;
    }

    static {
        data = new ArrayList();
        loading = true;
        pastVisiblesItems = 0;
        idtype = BuildConfig.VERSION_NAME;
        page = 1;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootview = inflater.inflate(C0336R.layout.home_layout, null);
        thiscontext = container.getContext();
        mRVFishPrice = (RecyclerView) rootview.findViewById(C0336R.id.fishPriceList);
        mLayoutManager = new LinearLayoutManager(thiscontext);
        db = new DBhelper(thiscontext);
        mSwipyRefreshLayout = (SwipyRefreshLayout) rootview.findViewById(C0336R.id.swipyrefreshlayout);
        ldv = (LoadingView) rootview.findViewById(C0336R.id.loading_view);
        this.ctv = new ContentView();
        ContentView contentView = this.ctv;
        ContentView.puttitle("Scook");
        contentView = this.ctv;
        ContentView.hidenall();
        contentView = this.ctv;
        ContentView.setmt();
        clearData();
        new loadingdk().execute(new String[]{idtype});
        new update().execute(new String[]{BuildConfig.VERSION_NAME});
        mRVFishPrice.addOnScrollListener(new C09561());
        mSwipyRefreshLayout.setOnRefreshListener(new C09572());
        return rootview;
    }

    public static void clearData() {
        if (!data.isEmpty()) {
            data.clear();
            mAdapter.notifyDataSetChanged();
        }
    }

    public static void gettype() {
        clearData();
        page = 1;
        new loadingdk().execute(new String[]{idtype});
    }

    public void upg() {
        Builder alertDialogBuilder = new Builder(thiscontext);
        alertDialogBuilder.setMessage((CharSequence) "\u0110\u00e3 c\u00f3 b\u1ea3n c\u1eadp nh\u1eadt m\u1edbi, b\u1ea1n c\u00f3 mu\u1ed1n c\u1eadp nh\u1eadt kh\u00f4ng?");
        alertDialogBuilder.setPositiveButton((CharSequence) "C\u00f3", new C03223());
        alertDialogBuilder.setNegativeButton((CharSequence) "Kh\u00f4ng", new C03234());
        alertDialogBuilder.create().show();
    }
}
