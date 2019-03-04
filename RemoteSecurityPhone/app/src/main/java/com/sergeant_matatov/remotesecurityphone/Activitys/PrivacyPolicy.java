package com.sergeant_matatov.remotesecurityphone.Activitys;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;

import com.sergeant_matatov.remotesecurityphone.R;

public class PrivacyPolicy extends AppCompatActivity {

    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_privacy_policy);

        mWebView = (WebView) findViewById(R.id.webView);
        mWebView.getSettings().setJavaScriptEnabled(true);                      // turn on support JavaScript
        mWebView.loadUrl("https://sites.google.com/view/rsp-privacy-policy");   // set url page
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
 //       startActivity(new Intent(PrivacyPolicy.this, MainActivity.class));
    }
}
