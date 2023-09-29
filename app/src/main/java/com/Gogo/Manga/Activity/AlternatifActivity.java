package com.Gogo.Manga.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.Gogo.Manga.R;
import com.applovin.mediation.ads.MaxAdView;

public class AlternatifActivity extends AppCompatActivity {
    private WebView webView;
    private ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alternatif);
        webView = findViewById(R.id.web_view);
        progressBar = findViewById(R.id.progress_bar);
        MaxAdView maxBannerAdView = findViewById(R.id.MaxAdView);
        maxBannerAdView.loadAd();

        Log.d( "AlternatifActivityonCreate: ",getIntent().getStringExtra("url"));
        webView.loadUrl(getIntent().getStringExtra("url"));
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setBuiltInZoomControls(true);
    //    webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webView.getSettings().setDisplayZoomControls(false);
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith("https://comick.app/user/")) {
                    view.stopLoading();
                    return true;
                }
                if (url.startsWith("https://discord.gg/comick")) {
                    view.stopLoading();
                    return true;
                }
                if (url.startsWith("https://reddit.com/r/ComicK/")) {
                    view.stopLoading();
                    return true;
                }
                if (url.startsWith("https://comick.app/install_app")) {
                    view.stopLoading();
                    return true;
                }
                view.loadUrl(url);
                return false;
            }
        });
        webView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                progressBar.setProgress(progress);
                if (progress == 100) {
                    progressBar.setVisibility(View.GONE);
                    //view.setVisibility(View.VISIBLE);

                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    // view.setVisibility(View.INVISIBLE);
                }

            }
        });
    }


    boolean doubleBackToExitPressedOnce=false;
    public void onBackPressed() {
        if (!webView.getUrl().equals(getIntent().getStringExtra("url"))) {
            webView.goBack();
            return;
        }

        if (doubleBackToExitPressedOnce) {
            Intent homeIntent = new Intent(Intent.ACTION_MAIN);
            homeIntent.addCategory( Intent.CATEGORY_HOME );
            homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(homeIntent);
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Tap Once Again To Close", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }
}