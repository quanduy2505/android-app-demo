package com.example.quan.foody;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebView1 extends AppCompatActivity {
WebView webView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview1);
        webView= (WebView) findViewById(R.id.webView);
        Intent intent =getIntent();
        String duonglink = intent.getStringExtra("link");
        webView.loadUrl(duonglink);
        webView.setWebViewClient(new WebViewClient());

    }
}
