package com.Gogo.Manga.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.Gogo.Manga.Utils.BookmarkDBHelper;
import com.Gogo.Manga.Utils.Constant;
import com.Gogo.Manga.R;
import com.applovin.mediation.ads.MaxAdView;
import com.applovin.sdk.AppLovinSdk;
import com.applovin.sdk.AppLovinSdkConfiguration;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class ComicActivity extends AppCompatActivity {
    String TAG ="ComicActivityTAG";
    String error= "";
    String finalHtml="";
    ProgressBar progressBar;
    TextView textView;
    Bundle b ;
    int position ;
    Button button , button2;
    WebView webView;
    SharedPreferences sharedpreferences;
    public static final String mypreference = "mypref";
    private RelativeLayout adContainerView;
    private BookmarkDBHelper bookmarkDBHelper;
    String url =null;
    ArrayList array;
    private SwipeRefreshLayout swipeRefreshLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comic);
        AppLovinSdk.getInstance( this ).setMediationProvider( "max" );
        AppLovinSdk.initializeSdk( this, new AppLovinSdk.SdkInitializationListener() {
            @Override
            public void onSdkInitialized(final AppLovinSdkConfiguration configuration)
            {
                ShowMaxBannerAd();

            }
        } );
        bookmarkDBHelper = new BookmarkDBHelper(ComicActivity.this);
        sharedpreferences = getSharedPreferences(mypreference,
                Context.MODE_PRIVATE);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        textView = findViewById(R.id.textView);
        progressBar = findViewById(R.id.progressBar);
        webView = findViewById(R.id.webView);
        progressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.MULTIPLY);
        b =getIntent().getExtras();

          array= b.getStringArrayList("bundle");
        position = getIntent().getIntExtra("position", 0);
     //   new Description().execute();

        button = findViewById(R.id.prevButton);
        int l = array.size()-1;
        if (l==position) {
            button.setVisibility(View.GONE);
        }
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        findViewById(R.id.appBar).setVisibility(View.GONE);
                        position = position+1;
                        progressBar.setVisibility(View.VISIBLE);
                        webView.setVisibility(View.GONE);
                        // setUpWebView();
                     //   new Description().execute();
                        new  Description().execute();
                    }
                },300);
            }
        });

        button2 = findViewById(R.id.nextButton);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 new Handler().postDelayed(new Runnable() {
                     @Override
                     public void run() {
                         findViewById(R.id.appBar).setVisibility(View.GONE);
                         progressBar.setVisibility(View.VISIBLE);
                         webView.setVisibility(View.GONE);
                         position = position-1;
                         // setUpWebView();
                     //    new Description().execute();
                         new  Description().execute();
                     }
                 },300);
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new  Description().execute();
            }
        });
        new  Description().execute();
    }

    public void ShowMaxBannerAd(){
        MaxAdView maxBannerAdView = findViewById(R.id.MaxAdView);
        maxBannerAdView.loadAd();
    }

    private void checkingInterAds() {
        if (Constant.getPageView(ComicActivity.this, getClass().getSimpleName())
                ==Constant.getInterPerClick(ComicActivity.this)) {

            Constant.setPageView(ComicActivity.this, getClass().getSimpleName(), 1);
        }else {
            int pageView = Constant.getPageView(ComicActivity.this, getClass().getSimpleName())+1;
            Constant.setPageView(ComicActivity.this, getClass().getSimpleName(), pageView);

        }

    }

    private class Description extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                url = (String) array.get(position);
                // Connect to the web site
                Document mBlogDocument = Jsoup.connect(url).get();
                // Using Elements to get the Meta data
                Element mElementDataSize = mBlogDocument.getElementById("content");
                Elements elements = mElementDataSize.select("div.bg-cover-faker") .select("div.ch-images");
                finalHtml  = elements.html();

            } catch (IndexOutOfBoundsException | IOException e) {
                Log.i("hasilError", e.toString());
                error = "error";
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            swipeRefreshLayout.setRefreshing(false);
            checkingInterAds();
            if (error.equals("error")) {
                new SweetAlertDialog(ComicActivity.this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Oops...")
                    .setContentText("Something went wrong!")
                    .show();
                Toast.makeText(ComicActivity.this, "Can't Load This Session, Check Your Internet Connection", Toast.LENGTH_SHORT).show();
            } else if (error.equals("")) {
                try {
                    int l = array.size()-1;
                    if (l==position) {
                        button.setVisibility(View.GONE);
                    }else {

                        button.setVisibility(View.VISIBLE);
                    }
                    findViewById(R.id.appBar).setVisibility(View.VISIBLE);
                    Log.i("getChapterImage", String.valueOf(finalHtml));

                    if ( position <= 0 ){
                        button2.setVisibility(View.GONE);
                    } else {
                        button2.setVisibility(View.VISIBLE);
                    }

                    ArrayList array2= b.getStringArrayList("bundle2");
                    textView.setText(array2.get(position).toString());

                    Log.i("arraydata", finalHtml);

                    StringBuilder sb = new StringBuilder();
                    sb.append("<HTML><HEAD><style type='text/css'>body{margin:auto auto;text-align:center;} img{width:100%25; margin-bottom: 10px;} </style><body>");

//                    String[] strings = finalHtml.split(",");
//                    for (String s: strings) {
//                        sb.append("<img src=\" "+s +"\"  >");
//                    }
                    Document document = Jsoup.parse(finalHtml);
                    Elements elements = document.getElementsByTag("img");
                    for (int i = 0; i <elements.size() ; i++) {
                        Log.d(TAG, "onPostExecute: "+elements.attr("src"));
                        sb.append("<img src=\" "+Constant.base_url+elements.get(i).attr("src")+"\"  >");
                    }

                   // sb.append(html);
                    sb.append("</body></HTML>");

                    webView.setVisibility(View.VISIBLE);
                    webView.loadData(sb.toString(), "text/html", null);
                    webView.setWebViewClient(new WebViewClient());
                    webView.getSettings().setJavaScriptEnabled(true);
                    webView.getSettings().setBuiltInZoomControls(true);
                    webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
                    webView.getSettings().setDisplayZoomControls(false);
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


                            if (progress>50){

                               if (bookmarkDBHelper.getReadCount(url)==0){

                                   bookmarkDBHelper.saveRead(textView.getText().toString().trim(),
                                           url);
                               }

                            }

                        }
                    });

                    saveHistory();
                }catch (IndexOutOfBoundsException i) {
                    i.getMessage();
                }

            }
        }
    }

    private void saveHistory() {
        String url_intent = getIntent().getStringExtra("url");
        String title = getIntent().getStringExtra("title");
        String image = getIntent().getStringExtra("image");
        String chapter = textView.getText().toString();
        String url_chapter = url;
        String time =String.valueOf(System.currentTimeMillis());


        Cursor cursor = bookmarkDBHelper.getLastHistory();
        if (cursor.getCount()!=0) {
            cursor.moveToFirst();
            String ch = cursor.getString(4);
            Log.d(TAG, "saveHistory: "+ch);
            if (!ch.equals(url_chapter)) {
                bookmarkDBHelper.saveHistory(url_intent, title, chapter, url_chapter, image, time);
            }
        }else {
            bookmarkDBHelper.saveHistory(url_intent, title, chapter, url_chapter, image, time);

        }


    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("position", position);
        b.putStringArrayList("bundle", b.getStringArrayList("bundle"));
        b.putStringArrayList("bundle2",  b.getStringArrayList("bundle2"));
        intent.putExtras(b);
        intent.putExtra("editTextValue", "value_here");
        setResult(RESULT_OK, intent);
        finish();
        super.onBackPressed();
    }
}
