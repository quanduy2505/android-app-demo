package com.example.quan.demojson;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class MainActivity extends Activity {

    TextView txtId;
    TextView txtname;
    TextView txtContent;
    TextView txtUserID;
    TextView txtCategory;
    TextView txtNviews;
    TextView txtCreatedDate;
    TextView txtUpdateDate;

    Button btnShowImage;
    public TextView findTextView(int id)
    {
        return (TextView) findViewById(id);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addControlAndEvents();
    }
    private void addControlAndEvents() {
        txtId=findTextView(R.id.txtId);
        txtname=findTextView(R.id.txtname);
        txtContent=findTextView(R.id.txtContent);
        txtUserID= findTextView(R.id.txtUserId);
        txtCategory= findTextView(R.id.txtCategory);
        txtNviews =findTextView(R.id.txtNviews);
        txtCreatedDate= findTextView(R.id.txtCreatedDate);
        txtUpdateDate=findTextView(R.id.txtUpdateDate);
        btnShowImage=(Button) findTextView(R.id.btnShowImage);
        btnShowImage.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
//Code xem hình ở đây
            }
        });
    }
    @Override
    protected void onResume() {
// TODO Auto-generated method stub
        super.onResume();
        new MyJsonTask().execute("http://graph.facebook.com/duythanhcse");
//new MyJsonTask().execute("http://graph.facebook.com/barackobama");
//new MyJsonTask().execute("http://graph.facebook.com/kyduyenhoahau");
    }
    //Lớp xử lý đa tiến trình:
    public class MyJsonTask extends AsyncTask<String, JSONObject, Void>
    {
        @Override
        protected void onPreExecute() {
// TODO Auto-generated method stub
            super.onPreExecute();
        }
        @Override
        protected Void doInBackground(String... params) {
//Lấy URL truyền vào
            String url=params[0];
            JSONObject jsonObj;
            try {
//đọc và chuyển về JSONObject
                jsonObj = MyJsonReader.readJsonFromUrl(url);
                publishProgress(jsonObj);
            } catch (IOException e) {
// TODO Auto-generated catch block
                e.printStackTrace();
            } catch (JSONException e) {
// TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onProgressUpdate(JSONObject... values) {
            super.onProgressUpdate(values);
//ta cập nhật giao diện ở đây:
            JSONObject jsonObj=values[0];
            try {
//kiểm tra xem có tồn tại thuộc tính id hay không
                if(jsonObj.has("id"))
                    txtId.setText(jsonObj.getString("id"));
                if(jsonObj.has("name"))
                    txtname.setText(jsonObj.getString("name"));
                if(jsonObj.has("content"))
                    txtContent.setText(jsonObj.getString("content"));
                if(jsonObj.has("last_name"))
                    txtLastName.setText(jsonObj.getString("last_name"));
                if(jsonObj.has("link"))
                    txtLink.setText(jsonObj.getString("link"));
                if(jsonObj.has("locale"))
                    txtLocale.setText(jsonObj.getString("locale"));
                if(jsonObj.has("name"))
                    txtName.setText(jsonObj.getString("name"));
                if(jsonObj.has("username"))
                    txtUserName.setText(jsonObj.getString("username"));
            } catch (JSONException e) {
                Toast.makeText(MainActivity.this, e.toString(),
                        Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
        @Override
        protected void onPostExecute(Void result) {
// TODO Auto-generated method stub
            super.onPostExecute(result);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
// Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
// Handle action bar item clicks here. The action bar will
// automatically handle clicks on the Home/Up button, so long
// as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}