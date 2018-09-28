package com.app.tuan88291.testapp;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.facebook.internal.NativeProtocol;
import com.facebook.share.internal.ShareConstants;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import me.wangyuwei.loadingview.LoadingView;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import rx.android.BuildConfig;

public class My_like extends Fragment {
    static List<Data_home> data;
    private static DBhelper db;
    private static LoadingView ldv;
    private static boolean loading;
    private static Adapter_home mAdapter;
    static LinearLayoutManager mLayoutManager;
    private static RecyclerView mRVFishPrice;
    public static int page;
    public static int pastVisiblesItems;
    public static Context thiscontext;
    private ContentView ctv;
    int lastVisibleItem;
    int totalItemCount;
    int visibleThreshold;

    private static class loadingdk extends AsyncTask<String, Void, String> {
        private loadingdk() {
        }

        protected String doInBackground(String... params) {
            String pg = String.valueOf(My_like.page);
            String ty = params[0];
            HttpResponse response = null;
            try {
                response = new DefaultHttpClient().execute(new HttpGet("http://cook.audition2.com/yeuthich.php?page=" + pg + "&myfb=" + My_like.db.idfb()));
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
            My_like.ldv.start();
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
                            List list = My_like.data;
                            List list2 = list;
                            list2.add(new Data_home(json_data.getString("idbv"), json_data.getString("noidung"), json_data.getString("idfb"), json_data.getString(ShareConstants.MEDIA_TYPE), json_data.getString("date"), json_data.getString(ShareConstants.WEB_DIALOG_PARAM_NAME), json_data.getString("like"), json_data.getString("tit"), json_data.getString(NativeProtocol.WEB_DIALOG_URL), json_data.getString("stt"), json_data.getString("imgtype"), json_data.getString("theloai"), json_data.getString("idtheloai")));
                            i++;
                        } catch (JSONException e2) {
                            e = e2;
                            JSONArray jSONArray2 = jSONArray;
                        }
                    }
                    My_like.mAdapter = new Adapter_home(My_like.thiscontext, My_like.data);
                    My_like.mRVFishPrice.setAdapter(My_like.mAdapter);
                    My_like.mRVFishPrice.setLayoutManager(My_like.mLayoutManager);
                    My_like.loading = true;
                    My_like.mRVFishPrice.scrollToPosition(My_like.pastVisiblesItems);
                } catch (JSONException e3) {
                    e = e3;
                    e.printStackTrace();
                    My_like.ldv.stop();
                    super.onPostExecute(result);
                }
            }
            My_like.ldv.stop();
            super.onPostExecute(result);
        }

        protected void onProgressUpdate(Void... values) {
        }
    }

    /* renamed from: com.app.tuan88291.testapp.My_like.1 */
    class C09641 extends OnScrollListener {
        C09641() {
        }

        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            if (dy > 0) {
                My_like.this.ctv;
                ContentView.setmt();
                My_like.this.ctv;
                ContentView.setgone(0);
            } else {
                My_like.this.ctv;
                ContentView.setgone(1);
            }
            My_like.this.totalItemCount = My_like.mLayoutManager.getItemCount();
            My_like.this.lastVisibleItem = My_like.mLayoutManager.findLastVisibleItemPosition();
            if (My_like.loading && My_like.this.totalItemCount <= My_like.this.lastVisibleItem + My_like.this.visibleThreshold) {
                Log.d("lodddddddddd", "page: " + My_like.page);
                My_like.page++;
                My_like.pastVisiblesItems = My_like.mLayoutManager.findFirstVisibleItemPosition();
                new loadingdk().execute(new String[]{BuildConfig.VERSION_NAME});
                My_like.loading = false;
            }
        }
    }

    public My_like() {
        this.visibleThreshold = 10;
    }

    static {
        data = new ArrayList();
        loading = true;
        pastVisiblesItems = 0;
        page = 1;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootview = inflater.inflate(C0336R.layout.my_like_info, null);
        thiscontext = container.getContext();
        mRVFishPrice = (RecyclerView) rootview.findViewById(C0336R.id.fishPriceList);
        mLayoutManager = new LinearLayoutManager(thiscontext);
        db = new DBhelper(thiscontext);
        ldv = (LoadingView) rootview.findViewById(C0336R.id.loading_view);
        this.ctv = new ContentView();
        ContentView contentView = this.ctv;
        ContentView.hidenall();
        contentView = this.ctv;
        ContentView.setmt();
        contentView = this.ctv;
        ContentView.puttitle("M\u00f3n \u0103n \u0111\u00e3 th\u00edch");
        clearData();
        Log.d("lodddddddddd", "page: " + page);
        new loadingdk().execute(new String[]{BuildConfig.VERSION_NAME});
        mRVFishPrice.addOnScrollListener(new C09641());
        return rootview;
    }

    public static void clearData() {
        if (!data.isEmpty()) {
            data.clear();
            mAdapter.notifyDataSetChanged();
        }
    }
}
