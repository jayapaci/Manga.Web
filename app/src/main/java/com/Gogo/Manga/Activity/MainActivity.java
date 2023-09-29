package com.Gogo.Manga.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.Gogo.Manga.R;
import com.Gogo.Manga.Fragment.BlankFragment3;
import com.Gogo.Manga.Fragment.BlankFragment4;
import com.Gogo.Manga.Fragment.FavFragment;
import com.Gogo.Manga.Fragment.HistoryFragment;
import com.Gogo.Manga.Fragment.HomeFragment;
import com.Gogo.Manga.adapter.ViewPagerAdapter;
import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdViewAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.ads.MaxAdView;
import com.applovin.sdk.AppLovinSdk;
import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;

import cn.pedant.SweetAlert.SweetAlertDialog;
import me.ibrahimsn.lib.OnItemSelectedListener;
import me.ibrahimsn.lib.SmoothBottomBar;

public class MainActivity extends AppCompatActivity {
    String TAG ="MainActivityTAG";
    private ViewPager viewPager;
    // private MyPagerAdapter myPagerAdapter;
    MenuItem prevMenuItem;
    HomeFragment homeFragment;
    BlankFragment3 blankFragment3;
    BlankFragment4 blankFragment4;
    SharedPreferences sharedpreferences;
    public static final String mypreference = "mypref";
    SharedPreferences.Editor editor;
    private RelativeLayout adContainerView;
    private MeowBottomNavigation nav_view;
    private SmoothBottomBar bottomBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AppLovinSdk.getInstance( this ).setMediationProvider( "max" );
        AppLovinSdk.initializeSdk( this, configuration -> {
            Log.d(TAG, "onSdkInitialized: ");
            ShowMaxBannerAd();

        });
        ShowMaxBannerAd();
        sharedpreferences = getSharedPreferences(mypreference,
                Context.MODE_PRIVATE);
          editor = sharedpreferences.edit();

        bottomBar = findViewById(R.id.bottomBar);
        viewPager = (ViewPager) findViewById(R.id.container);
//        myPagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
//        mViewPager.setAdapter(myPagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                bottomBar.setItemActiveIndex(position);
//                nav_view.show(position,true);
                if (prevMenuItem != null) {
                    prevMenuItem.setChecked(false);
                }
                else
                {
                  //  bottomNavigationView.getMenu().getItem(0).setChecked(false);
                }
                Log.d("page", "onPageSelected: "+position);


            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        setupViewPager(viewPager);
        subscribeToPushService();
        setUpNav();
    }

    private void setUpNav() {
        bottomBar.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public boolean onItemSelect(int pos) {
                viewPager.setCurrentItem(pos);
                return false;
            }
        });

    }

    public void ShowMaxBannerAd(){
        MaxAdView maxBannerAdView = findViewById(R.id.MaxAdView);
        maxBannerAdView.loadAd();
        maxBannerAdView.setListener(new MaxAdViewAdListener() {
            @Override
            public void onAdExpanded(MaxAd ad) {

            }

            @Override
            public void onAdCollapsed(MaxAd ad) {

            }

            @Override
            public void onAdLoaded(MaxAd ad) {

            }

            @Override
            public void onAdDisplayed(MaxAd ad) {

            }

            @Override
            public void onAdHidden(MaxAd ad) {

            }

            @Override
            public void onAdClicked(MaxAd ad) {

            }

            @Override
            public void onAdLoadFailed(String adUnitId, MaxError error) {ShowMaxBannerAd();
                Log.d(TAG, "onAdLoadFailed: "+error.getCode() +error.getMessage());
            }

            @Override
            public void onAdDisplayFailed(MaxAd ad, MaxError error) {ShowMaxBannerAd();
                Log.d(TAG, "onAdDisplayFailed: "+error.getCode() +error.getMessage());
            }
        });
    }


    private void subscribeToPushService() {
        try {

            FirebaseApp.initializeApp(this);
            FirebaseMessaging.getInstance().subscribeToTopic(getPackageName());

            Log.d("AndroidBash", "Subscribed");
        }catch (IllegalArgumentException i) {
            i.getMessage();
        }
    }


    private void showDialog() {
        new SweetAlertDialog(MainActivity.this, SweetAlertDialog.NORMAL_TYPE)
                .setTitleText("Do You Enjoy?")
                .setContentText("Please Rate This App")
                .setConfirmText("Yes")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        Intent intent = new Intent( Intent.ACTION_VIEW);
                        intent.setData(Uri.parse("https://play.google.com/store/apps/details?id="+getPackageName()));
                        startActivity(intent);
                        sDialog.dismissWithAnimation();
                    }
                })
                .setCancelButton("Cancel", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                    }
                })
                .show();
    }




    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        homeFragment=new HomeFragment();
        blankFragment3=new BlankFragment3();
        blankFragment4=new BlankFragment4();

        adapter.addFragment(homeFragment);
        adapter.addFragment(blankFragment3);
        adapter.addFragment(new FavFragment());
        adapter.addFragment(new HistoryFragment());
        adapter.addFragment(blankFragment4);
        viewPager.setAdapter(adapter);
    }
    boolean doubleBackToExitPressedOnce=false;
    public void onBackPressed() {
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
