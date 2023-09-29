package com.Gogo.Manga.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.Gogo.Manga.R;


public class ExpandActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private WebView webView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expand);
        toolbar= findViewById(R.id.toolbar);
        webView= findViewById(R.id.webView);
        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.drawable.ic_arrowleft);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        toolbar.setTitle(getIntent().getStringExtra("title"));
        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new WebChromeClient());
        webView.getSettings().setJavaScriptEnabled(true);
        StringBuilder sb = new StringBuilder();
        sb.append("<HTML><HEAD><style type='text/css'>body{ margin:10px;  text-align:center;} " +
                "img{width:90%;} </style><body>");
        sb.append(getIntent().getStringExtra("content"));
        sb.append("</body></HTML>");
        webView.loadData(sb.toString(), "text/html", null);
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if(isURLMatching(url)) {
                    openNextActivity();
                    return true;
                }
                return super.shouldOverrideUrlLoading(view, url);
            }
        });
    }

    protected boolean isURLMatching(String url) {
        if(!url.equals("https://play.google.com/store/apps/details?id="+ getPackageName())) {
            return false;
        }
        return true;
    }

    protected void openNextActivity() {
        final  String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }

    }
}