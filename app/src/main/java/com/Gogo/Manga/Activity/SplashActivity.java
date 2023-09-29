package com.Gogo.Manga.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.Gogo.Manga.BuildConfig;
import com.Gogo.Manga.R;
import com.Gogo.Manga.Utils.Constant;
import com.facebook.ads.AdSettings;
import com.facebook.ads.AudienceNetworkAds;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.firebase.FirebaseApp;
import com.startapp.sdk.adsbase.StartAppAd;
import com.startapp.sdk.adsbase.StartAppSDK;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;


public class SplashActivity extends AppCompatActivity {
    String TAG ="SplashActivityTAG";
    private String html =null;
    private String isError ="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AudienceNetworkAds.initialize(this);
        StartAppAd.disableSplash();
        setContentView(R.layout.activity_splash);
        FirebaseApp.initializeApp(this);
       new Description().execute();
    }

    private class Description extends AsyncTask<Void, Void, Void> {
        String desc;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {

            String url ="https://mangacom-app.blogspot.com/p/update.html";
            try {
                Document mBlogDocument = Jsoup.connect(url).timeout(10 * 1000).get();

                Elements mUpdate = mBlogDocument.getElementsByClass("update");
                html =mUpdate.html();

            } catch (IOException e) {
                e.printStackTrace();

                isError = "error";
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            try {

                Document document = Jsoup.parse(html);
                final Elements elements = document.select("ol").select("li");
                for (int i =0; i<elements.size(); i++) {
                    Log.i("getOl", elements.get(i).text());
                }
                if (elements.get(0).text().equals(BuildConfig.VERSION_NAME)) {
                    switch (splitId(elements.get(1).text())) {
                        case "1":
                            initAdMob(elements);
                            break;
                        case "2":
                            initUnityAds(elements);
                            break;
                        case "3":
                            iniFan(elements);
                            break;
                        case "4":
                            initStartApp(elements);
                            break;
                    }

                } else if (isError.equals("error")) {
                    Toast.makeText(SplashActivity.this, "Can't Load This Session, Check Your Internet Connection", Toast.LENGTH_SHORT).show();
                } else {
                    AlertDialog alertDialog = new AlertDialog.Builder(SplashActivity.this)
                            .setTitle(getString(R.string.app_name))
                            .setMessage(getString(R.string.app_name)+" New version Available")
                            .setPositiveButton("Update", (dialogInterface, i) -> {
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse( elements.get(0).select("a").attr("href")));
                                startActivity(intent);
                                finish();
                                System.exit(0);
                            }).create();
                    alertDialog.show();
                    alertDialog.setCancelable(false);
                    alertDialog.setCanceledOnTouchOutside(false);
                }
            } catch (NullPointerException | IndexOutOfBoundsException |IllegalArgumentException npe) {
                Toast.makeText(SplashActivity.this, "Cant Connect to server please check your internet connection", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void initStartApp(Elements elements) {
        StartAppSDK.setTestAdsEnabled(false);
        StartAppSDK.init(this, splitId(elements.get(1).text()), false);
        setAds(elements.get(1).text(),elements.get(2).text(),elements.get(3).text(),
                elements.get(4).text(),elements.get(5).text(),elements.get(6).text(),
                elements.get(7).text(),    elements.get(8).text(),    elements.get(9).text()
        );
    }

    private void iniFan(Elements elements) {
        AdSettings.setTestMode(false);
        AudienceNetworkAds.initialize(SplashActivity.this);
        setAds(elements.get(1).text(),elements.get(2).text(),elements.get(3).text(),
                elements.get(4).text(),elements.get(5).text(),elements.get(6).text(),
                elements.get(7).text(),    elements.get(8).text(),    elements.get(9).text()
        );
    }

    private void initUnityAds(Elements elements) {
        setAds(elements.get(1).text(),elements.get(2).text(),elements.get(3).text(),
                elements.get(4).text(),elements.get(5).text(),elements.get(6).text(),
                elements.get(7).text(),    elements.get(8).text(),    elements.get(9).text()
        );
    }

    private void initAdMob(Elements elements) {
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {

                setAds(elements.get(1).text(),elements.get(2).text(),elements.get(3).text(),
                        elements.get(4).text(),elements.get(5).text(),elements.get(6).text(),
                        elements.get(7).text(),    elements.get(8).text(),    elements.get(9).text()
                );

            }
        });
    }


    private void setAds(String adsType, String AdsId, String bannerId, String InterId,
                        String InterPerClick, String nativeId, String NativPerItem, String hide, String Alternatif) {
        Constant.setAdsType(SplashActivity.this, Integer.parseInt(splitId(adsType)) );
        Constant.setAdsId(this, splitId(AdsId));
        Constant.setBannerId(this, splitId(bannerId));
        Constant.setInterId(this, splitId(InterId));
        Constant.setInterPerClick(this, Integer.parseInt(splitId(InterPerClick)));
        Constant.setNativeId(this,splitId(nativeId));
        Constant.setNativePerItem(this, Integer.parseInt(splitId(NativPerItem)));

//        Constant.setAdsType(SplashActivity.this, 3);
//        Constant.setAdsId(this,  "YOUR_PLACEMENT_ID");
//        Constant.setBannerId(this, "YOUR_PLACEMENT_ID");
//        Constant.setInterId(this, "YOUR_PLACEMENT_ID");
//        Constant.setInterPerClick(this, 3);
//        Constant.setNativeId(this,"YOUR_PLACEMENT_ID");
//        Constant.setNativePerItem(this, 6);
//
        if (Constant.getAdsType(this)!=0)
        {
            if (getIntent().hasExtra("from")){
                Intent notificationIntent = new Intent(getApplicationContext(), DetailActivity.class);
                notificationIntent.putExtra("link", getIntent().getStringExtra("link"));
                notificationIntent.putExtra("from", "Notif");
                startActivity(notificationIntent);
            }else {

                if (splitId(hide).equals("0")){
                    startActivity(new Intent(this, BlogActivity.class));
                }else {
                    if (splitId(Alternatif).equals("0")) {
                        StartAppSDK.init(SplashActivity.this, Constant.getAdsId(SplashActivity.this), false);
                        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                        startActivity(intent);
                    }else {
                        Intent intent = new Intent(this, AlternatifActivity.class);
                        intent.putExtra("url", splitId(Alternatif));
                        startActivity(intent);
                    }
                }
            }
          
        }
    }

    private String splitId(String id) {
        String balik=null;
        try {
            String[] s = id.split(":");
            balik =  s[1];
            if (balik.startsWith("https")) {
                return "https:"+s[2];
            }
        }catch (IndexOutOfBoundsException | NullPointerException n) {
            n.getMessage();
        }

        return balik.replaceAll("\\s+","");
    }
}