package com.example.quan.demojsonkp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
ListView lv;
    ArrayList<SanPham> mang;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lv = (ListView)findViewById(R.id.listViewSanPham);
        mang= new ArrayList<SanPham>();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new docJSON().execute("http://45.58.54.74/phalcon/hoangtu/api//categories/1/receipts");
            }
        });
    }

    class docJSON extends AsyncTask<String, Integer, String>{

        @Override
        protected String doInBackground(String... params) {
            return docNoiDung_Tu_URL(params[0]);
        }

        @Override
        protected void onPostExecute(String s) {

            try {
                JSONObject category = new JSONObject(s);
                JSONArray mangjson = category.getJSONArray("receipts");
                for(int i=0; i<mangjson.length();i++){
                    JSONObject sp = mangjson.getJSONObject(i);
                    mang.add(new SanPham(
                            sp.getString("id"),
                            sp.getString("name"),
                            sp.getString("content"),
                            sp.getString("user_id"),
                            sp.getString("category_id"),
                            sp.getString("nviews"),
                            sp.getString("created_date"),
                            sp.getString("updated_date"),
                            sp.getString("image")
                    ));

                }
                 Toast.makeText(getApplicationContext(),""+ mang.size(), Toast.LENGTH_LONG).show();
                    ListAdapter adapter = new ListAdapter(
                            getApplicationContext(),R.layout.activity_dong_san_pham,mang
                    );
               lv.setAdapter(adapter);
            } catch (JSONException e) {
              //  Toast.makeText(getApplicationContext(),"An lon roi", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
            // Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
        }
    }
    @NonNull
    private String docNoiDung_Tu_URL(String theUrl){
        StringBuilder content = new StringBuilder();
        try    {
            // create a url object
            URL url = new URL(theUrl);

            // create a urlconnection object
            URLConnection urlConnection = url.openConnection();

            // wrap the urlconnection in a bufferedreader
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

            String line;

            // read from the urlconnection via the bufferedreader
            while ((line = bufferedReader.readLine()) != null){
                content.append(line + "\n");
            }
            bufferedReader.close();
        }
        catch(Exception e)    {
            e.printStackTrace();
        }
        return content.toString();
    }
}
