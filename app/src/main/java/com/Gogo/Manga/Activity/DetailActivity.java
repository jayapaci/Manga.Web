package com.Gogo.Manga.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.Gogo.Manga.R;
import com.Gogo.Manga.Utils.BookmarkDBHelper;
import com.Gogo.Manga.Utils.Constant;
import com.Gogo.Manga.model.Bookmark;
import com.Gogo.Manga.model.Genre;
import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.ads.MaxAdView;
import com.applovin.mediation.ads.MaxInterstitialAd;
import com.applovin.sdk.AppLovinSdk;
import com.applovin.sdk.AppLovinSdkConfiguration;
import com.balysv.materialripple.MaterialRippleLayout;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class DetailActivity extends AppCompatActivity {
    private ImageView imgBack,imageBg;
    private static final String TAG ="DetailActivityTAG" ;
    SharedPreferences sharedpreferences;
    public static final String mypreference = "mypref";
    TextView textView, textView2, textView3, textView4, textView5;
    ImageView imageView;
    String error ="";
    String finalHtml = "";
    private BookmarkDBHelper dbHelper;
    ArrayList <String> listChapter = new ArrayList();
    ArrayList <String>   listLink = new ArrayList();
    private RecyclerView mRecyclerView;
    private ListAdapter mListadapter;
    FloatingActionButton button;
    String getTittle = "";
    String getImage = "";
    private RelativeLayout adContainerView;
    private BookmarkDBHelper bookmarkDBHelper;
    private boolean reverse= false;
    ProgressBar progressBar;
    private MaxInterstitialAd interstitialAd;
    private int retryAttempt;
    private DatabaseReference mDatabase;

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        mDatabase = FirebaseDatabase.getInstance("https://manga-ko-default-rtdb.asia-southeast1.firebasedatabase.app").getReference();

        AppLovinSdk.getInstance( this ).setMediationProvider( "max" );
        AppLovinSdk.initializeSdk( this, new AppLovinSdk.SdkInitializationListener() {
            @Override
            public void onSdkInitialized(final AppLovinSdkConfiguration configuration)
            {
                ShowMaxBannerAd();
                LoadMaxInterstitial();

            }
        } );
        progressBar= findViewById(R.id.progressBar);
        progressBar.getIndeterminateDrawable().setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.MULTIPLY);
        imageBg= findViewById(R.id.imageBg);
        imgBack = findViewById(R.id.imgBack);
        bookmarkDBHelper = new BookmarkDBHelper(DetailActivity.this);
        sharedpreferences = getSharedPreferences(mypreference,
                Context.MODE_PRIVATE);
        adContainerView = findViewById(R.id.bottomBannerView);

        imageView = findViewById(R.id.poster);
        textView = findViewById(R.id.textDeskripsi);
        textView2 = findViewById(R.id.textInfo);
        textView3 = findViewById(R.id.title);
        button = findViewById(R.id.button);
        mRecyclerView = (RecyclerView) findViewById(R.id.postList);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(DetailActivity.this);
        mRecyclerView.setLayoutManager(layoutManager);
        mListadapter = new ListAdapter(listChapter, listLink);
        mRecyclerView.setAdapter(mListadapter);
        mListadapter.notifyDataSetChanged();
        new  Description().execute();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Constant.getKey(DetailActivity.this)!=null){
                    checkHasBookMark();
                }else {
                    Toast.makeText(DetailActivity.this, "You need to login to mark this manga", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(DetailActivity.this, LoginActivity.class);
                    intent.putExtra("isDetail", true);
                    startActivity(intent);
                }

            }
        });

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        findViewById(R.id.txtToChap1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (listLink.size()!=0) {
                            Intent intent = new Intent(DetailActivity.this, ComicActivity.class);
                            Bundle b=new Bundle();
                            if (reverse){
                                intent.putExtra("link", listLink.get(0));
                                intent.putExtra("position", 0);
                            }else {

                                intent.putExtra("link", listLink.get(listLink.size()-1));
                                intent.putExtra("position", listLink.size()-1);
                            }
                            b.putStringArrayList("bundle", listLink);
                            b.putStringArrayList("bundle2", listChapter);
                            intent.putExtra("title", getTittle);
                            intent.putExtra("image", getImage);
                            intent.putExtra("url", getIntent().getStringExtra("link"));
                            intent.putExtras(b);
                            startActivityForResult(intent, 1);

                        }
                    }
                },300);
            }
        });

        findViewById(R.id.textMore).setOnClickListener(new View.OnClickListener() {
            boolean isExpand =false;
            @Override
            public void onClick(View v) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (isExpand) {
                            textView.setMaxLines(4);
                            isExpand=false;
                        }else {
                            textView.setMaxLines(Integer.MAX_VALUE);
                            isExpand=true;
                        }
                    }
                },500);
            }
        });

        findViewById(R.id.imgSort).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (listChapter.size()!=0) {
                            if (reverse) {
                                Collections.reverse(listChapter);
                                Collections.reverse(listLink);
                                mListadapter = new ListAdapter(listChapter, listLink);
                                mRecyclerView.setAdapter(mListadapter);
                                mListadapter.notifyDataSetChanged();
                                reverse=false;
                            }else {

                                Collections.reverse(listChapter);
                                Collections.reverse(listLink);
                                mListadapter = new ListAdapter(listChapter, listLink);
                                mRecyclerView.setAdapter(mListadapter);
                                mListadapter.notifyDataSetChanged();
                                reverse=true;
                            }

                        }
                    }
                },300);
            }
        });

    }

    private void checkHasBookMark() {
        mDatabase.child("movieKoBookmark")
                .child(Constant.getKey(DetailActivity.this) )
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                            Bookmark bookmark = dataSnapshot.getValue(Bookmark.class);
                            if (bookmark.getUrl().equals(getIntent().getStringExtra("link"))) {

                                new SweetAlertDialog(DetailActivity.this, SweetAlertDialog.ERROR_TYPE)
                                        .setTitleText("Failed!")
                                        .setContentText("Manga Already Bookmark!!")
                                        .show();
                                return;
                            }
                        }
                        saveBookmark();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }


    public void ShowMaxBannerAd(){
        MaxAdView maxBannerAdView = findViewById(R.id.MaxAdView);
        maxBannerAdView.loadAd();
    }

    public void LoadMaxInterstitial(){
        interstitialAd = new MaxInterstitialAd( "809294d26c7b97c7", this );
        MaxAdListener maxAdListener = new MaxAdListener() {
            @Override
            public void onAdLoaded(final MaxAd maxAd)
            {
                retryAttempt = 0;
            }
            @Override
            public void onAdLoadFailed(final String adUnitId, final MaxError error)
            {
                retryAttempt++;
                long delayMillis = TimeUnit.SECONDS.toMillis( (long) Math.pow( 2, Math.min( 6, retryAttempt ) ) );
                new Handler().postDelayed(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        interstitialAd.loadAd();
                    }
                }, delayMillis );
            }
            @Override
            public void onAdDisplayFailed(final MaxAd maxAd, final MaxError error)
            {
                interstitialAd.loadAd();
            }
            @Override
            public void onAdDisplayed(final MaxAd maxAd) {}
            @Override
            public void onAdClicked(final MaxAd maxAd) {}
            @Override
            public void onAdHidden(final MaxAd maxAd)
            {
                interstitialAd.loadAd();
            }
        };
        interstitialAd.setListener( maxAdListener );
        interstitialAd.loadAd();

    }

    public void ShowMaxInterstitialAdConnect(){
        if (interstitialAd.isReady())
        {
            interstitialAd.showAd();
        }
    }

    private void checkingInterAds() {
        if (Constant.getPageView(DetailActivity.this, getClass().getSimpleName())
                ==Constant.getInterPerClick(DetailActivity.this)) {
            Constant.setPageView(DetailActivity.this, getClass().getSimpleName(), 1);
        }else {
            int pageView = Constant.getPageView(DetailActivity.this, getClass().getSimpleName())+1;
            Constant.setPageView(DetailActivity.this, getClass().getSimpleName(), pageView);
        }

    }

    private void saveBookmark() {
//        String title = getTittle.trim();
//        String image = getImage.trim();
//        String url = getIntent().getStringExtra("link").trim();
//        dbHelper = new BookmarkDBHelper(this);
//        Bookmark artikel = new Bookmark(title, image, url);
//        dbHelper.saveNewArtikel(DetailActivity.this, artikel);
        Bookmark bookmark = new Bookmark();
        bookmark.setTitle(getTittle );
        bookmark.setImage(getImage );
        bookmark.setUrl(getIntent().getStringExtra("link") );
        String key =  mDatabase.child("movieKoBookmark").push().getKey();
        mDatabase.child("movieKoBookmark")
                .child(Constant.getKey(DetailActivity.this) )
                .child(key).setValue(bookmark)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        new SweetAlertDialog(DetailActivity.this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Failed!")
                                .setContentText("Manga Failed to Boomark")
                                .show();
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        new SweetAlertDialog(DetailActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                                .setTitleText("Good job!")
                                .setContentText("Manga Has Been Added to Boomark")
                                .show();
                    }
                });
    }

    private class Description extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {

            String url= getIntent().getStringExtra("link");
            // String url= "https://readm.org/manga/16956";
            try {
                // Connect to the web site
                Document mBlogDocument = Jsoup.connect(url) .get();
                // Using Elements to get the Meta data
                Elements mElementDataSize = mBlogDocument.getElementById("wrapper-inner").select("div.inner-content");
                finalHtml = mElementDataSize.html();


            } catch (IOException  | RuntimeException e) {
                Log.i("hasilError", e.toString());
                error = "error";
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            try {
                progressBar.setVisibility(View.GONE);
                if (error.equals("error")) {
                    Toast.makeText(DetailActivity.this, "Can't Load This Session, Check Your Internet Connection", Toast.LENGTH_SHORT).show();
                    //   Toast.makeText(DetailActivity.this, "Retrying to Connect..", Toast.LENGTH_SHORT).show();
                    //   new  Description2().execute();


                } else if (error.equals("")) {
                    checkingInterAds();
                    imgBack.setVisibility(View.VISIBLE);
                    findViewById(R.id.nestedScrollView).setVisibility(View.VISIBLE);
                    button.setVisibility(View.VISIBLE);
                    Document document = Jsoup.parse(finalHtml);
                    Elements elements = document.getElementById("router-view").select("div.bg-cover-faker");
                    Log.d(TAG, "onPostExecute: "+elements);
                    String title = elements.select("div.grid").select("a").select("h1").text();
                    getTittle = title;
                    textView3.setText(title);
                    String subtitle = elements.select("div.grid").select("div.sub-title").text();
                    textView2.setText(subtitle);

                    Elements elements1 = elements.select("div.tab-segment").select("div.items").select("div.item");
                    String image = elements1.select("a").select("img").attr("src");
                    getImage = Constant.base_url+image;
                    Glide.with(DetailActivity.this).load(Constant.base_url+image).into(imageView);

                    Elements elements2 = elements1.select("div.content").select("div.series-summary-wrapper");
                    textView.setText("");
                    String sinopsis = elements2.select("span").text();
                    if (!sinopsis.startsWith("Genres")) {
                        textView.setText(sinopsis);
                    }

                    Elements o = elements2.select("div.list").select("div.item").select("a");
                    LinearLayout linear =  findViewById(R.id.linear);
                    for (int x =0; x<o.size();x++){
                        TextView textView = new TextView(DetailActivity.this);
                        textView.setText(o.get(x).text());
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                        params.setMargins(10,10,10,10);
                        textView.setLayoutParams(params);
                        textView.setTextColor(getResources().getColor(R.color.white));
                        textView.setBackground(getResources().getDrawable(R.drawable.bg_list_genre_trans));
                        textView.setPadding(20,20,20,20);
                        textView.setPaddingRelative(20,20,20,20);
                        linear.addView(textView);
                    }

                    //CHAPTER
                    Elements elements3 = elements.select("div.tab-segment").select("div.common-lists").select("div.sixteen")
                            .select("table");
                    for (int i = 0; i < elements3.size(); i++) {
                        String chap = elements3.get(i).select("tr").select("h6").text();
                        listChapter.add(chap);
                        listLink.add(Constant.base_url+elements3.get(i).select("tr").select("h6").select("a").attr("href"));
                        Log.d(TAG, "onPostExecuteCHAPTER: "+chap);
                    }

                    mListadapter.notifyDataSetChanged();
                }
            }catch (IndexOutOfBoundsException i) {
                i.getMessage();
            }
        }
    }


    public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder>
    {
        int row_index = -1;
        int row_index_2 = -1;
        private ArrayList<String> listChapter;
        private ArrayList<String> listLink;

        public ListAdapter(ArrayList<String> listChapter, ArrayList<String> listLink )
        {
            this.listChapter = listChapter;
            this.listLink = listLink;
        }

        public class ViewHolder extends RecyclerView.ViewHolder
        {
            TextView textViewText;
            RelativeLayout row_linearlayout;
            MaterialRippleLayout ripple;
            public ViewHolder(View itemView)
            {
                super(itemView);
                //   this.ripple =  itemView.findViewById(R.id.ripple);
                this.textViewText = (TextView) itemView.findViewById(R.id.txt_chap);
                this.row_linearlayout=(RelativeLayout)itemView.findViewById(R.id.row_linrLayout);
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_chap, parent, false);
            ViewHolder viewHolder = new ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position)
        {

            if (bookmarkDBHelper.getReadCount(listLink.get(position))>0){
                holder.row_linearlayout.setBackgroundResource(R.drawable.bg_label);
                row_index_2=position;
            }else {

                holder.row_linearlayout.setBackgroundResource(R.drawable.bg_list_chap);
            }
            holder.textViewText.setText(listChapter.get(position));


            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    row_index =position;
                    notifyDataSetChanged();

                    if (listLink.get(position).equals("kosong")) {
                        new SweetAlertDialog(DetailActivity.this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Oops...")
                                .setContentText("Something went wrong!")
                                .show();
                    } else {
                        //  Toast.makeText(DetailActivity.this, "ini Link ke " + listLink.get(position), Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(DetailActivity.this, ComicActivity.class);
                        intent.putExtra("title", getTittle);
                        intent.putExtra("image", getImage);
                        intent.putExtra("url", getIntent().getStringExtra("link"));

                        intent.putExtra("link", listLink.get(position));
                        Bundle b=new Bundle();
                        intent.putExtra("position", position);
                        b.putStringArrayList("bundle", listLink);
                        b.putStringArrayList("bundle2", listChapter);
                        intent.putExtras(b);
                        startActivityForResult(intent, 1);
                    }
                }
            });
            if(row_index_2 ==position){
                holder.row_linearlayout.setBackgroundResource(R.drawable.bg_label);
            }
            else
            {
                holder.row_linearlayout.setBackgroundResource(R.drawable.bg_list_chap);
            }
        }

        @Override
        public int getItemCount()
        {
            return listChapter.size();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == RESULT_OK) {
                String strEditText = data.getStringExtra("editTextValue");
                // Toast.makeText(this, strEditText, Toast.LENGTH_SHORT).show();
                mListadapter = new ListAdapter(listChapter, listLink);
                mRecyclerView.setAdapter(mListadapter);
                mListadapter.notifyDataSetChanged();
                refreshChapter(data.getExtras());
            }
        }
    }

    private void refreshChapter(Bundle extras) {
        ArrayList array= extras.getStringArrayList("bundle");

        for (int i =0; i <array.size(); i++){
            Log.i("arratListLink",(String) array.get(i) +  array.get(i));
        }
    }

    @Override
    public void onBackPressed() {
        if (getIntent().getStringExtra("from").equals("Notif")){
            startActivity(new Intent(this, MainActivity.class));
        }else {
            super.onBackPressed();
            ShowMaxInterstitialAdConnect();
        }
    }

    private void initTag() {
        List<Genre> list = new ArrayList<>();
        list = getGenre();
        LinearLayout linear =  findViewById(R.id.linear);
        for (int i =0; i<list.size(); i++) {
            TextView textView = new TextView(DetailActivity.this);
            textView.setText(list.get(i).getLabel());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            params.setMargins(10,10,10,10);
            textView.setLayoutParams(params);
            textView.setTextColor(getResources().getColor(R.color.white));
            textView.setBackground(getResources().getDrawable(R.drawable.bg_list_genre_trans));
            textView.setPadding(20,20,20,20);
            textView.setPaddingRelative(20,20,20,20);
            linear.addView(textView);
        }



    }

    private List<Genre> getGenre (){
        String html = "<div class=\"category\">\n" +
                "                <div class=\"row\">\n" +
                "                                            <div class=\"col-xs-6\">\n" +
                "                            <a href=\"http://mangahere.today/mangas/action\" title=\"Action\">Action</a>\n" +
                "                        </div>\n" +
                "                                            <div class=\"col-xs-6\">\n" +
                "                            <a href=\"http://mangahere.today/mangas/adult\" title=\"Adult\">Adult</a>\n" +
                "                        </div>\n" +
                "                                            <div class=\"col-xs-6\">\n" +
                "                            <a href=\"http://mangahere.today/mangas/adventure\" title=\"Adventure\">Adventure</a>\n" +
                "                        </div>\n" +
                "                                            <div class=\"col-xs-6\">\n" +
                "                            <a href=\"http://mangahere.today/mangas/comedy\" title=\"Comedy\">Comedy</a>\n" +
                "                        </div>\n" +
                "                                            <div class=\"col-xs-6\">\n" +
                "                            <a href=\"http://mangahere.today/mangas/cooking\" title=\"Cooking\">Cooking</a>\n" +
                "                        </div>\n" +
                "                                            <div class=\"col-xs-6\">\n" +
                "                            <a href=\"http://mangahere.today/mangas/doujinshi\" title=\"Doujinshi\">Doujinshi</a>\n" +
                "                        </div>\n" +
                "                                            <div class=\"col-xs-6\">\n" +
                "                            <a href=\"http://mangahere.today/mangas/drama\" title=\"Drama\">Drama</a>\n" +
                "                        </div>\n" +
                "                                            <div class=\"col-xs-6\">\n" +
                "                            <a href=\"http://mangahere.today/mangas/ecchi\" title=\"Ecchi\">Ecchi</a>\n" +
                "                        </div>\n" +
                "                                            <div class=\"col-xs-6\">\n" +
                "                            <a href=\"http://mangahere.today/mangas/fantasy\" title=\"Fantasy\">Fantasy</a>\n" +
                "                        </div>\n" +
                "                                            <div class=\"col-xs-6\">\n" +
                "                            <a href=\"http://mangahere.today/mangas/gender-bender\" title=\"Gender bender\">Gender bender</a>\n" +
                "                        </div>\n" +
                "                                            <div class=\"col-xs-6\">\n" +
                "                            <a href=\"http://mangahere.today/mangas/harem\" title=\"Harem\">Harem</a>\n" +
                "                        </div>\n" +
                "                                            <div class=\"col-xs-6\">\n" +
                "                            <a href=\"http://mangahere.today/mangas/historical\" title=\"Historical\">Historical</a>\n" +
                "                        </div>\n" +
                "                                            <div class=\"col-xs-6\">\n" +
                "                            <a href=\"http://mangahere.today/mangas/horror\" title=\"Horror\">Horror</a>\n" +
                "                        </div>\n" +
                "                                            <div class=\"col-xs-6\">\n" +
                "                            <a href=\"http://mangahere.today/mangas/isekai\" title=\"Isekai\">Isekai</a>\n" +
                "                        </div>\n" +
                "                                            <div class=\"col-xs-6\">\n" +
                "                            <a href=\"http://mangahere.today/mangas/josei\" title=\"Josei\">Josei</a>\n" +
                "                        </div>\n" +
                "                                            <div class=\"col-xs-6\">\n" +
                "                            <a href=\"http://mangahere.today/mangas/manhua\" title=\"Manhua\">Manhua</a>\n" +
                "                        </div>\n" +
                "                                            <div class=\"col-xs-6\">\n" +
                "                            <a href=\"http://mangahere.today/mangas/manhwa\" title=\"Manhwa\">Manhwa</a>\n" +
                "                        </div>\n" +
                "                                            <div class=\"col-xs-6\">\n" +
                "                            <a href=\"http://mangahere.today/mangas/martial-arts\" title=\"Martial arts\">Martial arts</a>\n" +
                "                        </div>\n" +
                "                                            <div class=\"col-xs-6\">\n" +
                "                            <a href=\"http://mangahere.today/mangas/mature\" title=\"Mature\">Mature</a>\n" +
                "                        </div>\n" +
                "                                            <div class=\"col-xs-6\">\n" +
                "                            <a href=\"http://mangahere.today/mangas/mecha\" title=\"Mecha\">Mecha</a>\n" +
                "                        </div>\n" +
                "                                            <div class=\"col-xs-6\">\n" +
                "                            <a href=\"http://mangahere.today/mangas/medical\" title=\"Medical\">Medical</a>\n" +
                "                        </div>\n" +
                "                                            <div class=\"col-xs-6\">\n" +
                "                            <a href=\"http://mangahere.today/mangas/mystery\" title=\"Mystery\">Mystery</a>\n" +
                "                        </div>\n" +
                "                                            <div class=\"col-xs-6\">\n" +
                "                            <a href=\"http://mangahere.today/mangas/one-shot\" title=\"One shot\">One shot</a>\n" +
                "                        </div>\n" +
                "                                            <div class=\"col-xs-6\">\n" +
                "                            <a href=\"http://mangahere.today/mangas/psychological\" title=\"Psychological\">Psychological</a>\n" +
                "                        </div>\n" +
                "                                            <div class=\"col-xs-6\">\n" +
                "                            <a href=\"http://mangahere.today/mangas/romance\" title=\"Romance\">Romance</a>\n" +
                "                        </div>\n" +
                "                                            <div class=\"col-xs-6\">\n" +
                "                            <a href=\"http://mangahere.today/mangas/school-life\" title=\"School life\">School life</a>\n" +
                "                        </div>\n" +
                "                                            <div class=\"col-xs-6\">\n" +
                "                            <a href=\"http://mangahere.today/mangas/sci-fi\" title=\"Sci fi\">Sci fi</a>\n" +
                "                        </div>\n" +
                "                                            <div class=\"col-xs-6\">\n" +
                "                            <a href=\"http://mangahere.today/mangas/seinen\" title=\"Seinen\">Seinen</a>\n" +
                "                        </div>\n" +
                "                                            <div class=\"col-xs-6\">\n" +
                "                            <a href=\"http://mangahere.today/mangas/shoujo\" title=\"Shoujo\">Shoujo</a>\n" +
                "                        </div>\n" +
                "                                            <div class=\"col-xs-6\">\n" +
                "                            <a href=\"http://mangahere.today/mangas/shoujo-ai\" title=\"Shoujo ai\">Shoujo ai</a>\n" +
                "                        </div>\n" +
                "                                            <div class=\"col-xs-6\">\n" +
                "                            <a href=\"http://mangahere.today/mangas/shounen\" title=\"Shounen\">Shounen</a>\n" +
                "                        </div>\n" +
                "                                            <div class=\"col-xs-6\">\n" +
                "                            <a href=\"http://mangahere.today/mangas/shounen-ai\" title=\"Shounen ai\">Shounen ai</a>\n" +
                "                        </div>\n" +
                "                                            <div class=\"col-xs-6\">\n" +
                "                            <a href=\"http://mangahere.today/mangas/slice-of-life\" title=\"Slice of life\">Slice of life</a>\n" +
                "                        </div>\n" +
                "                                            <div class=\"col-xs-6\">\n" +
                "                            <a href=\"http://mangahere.today/mangas/smut\" title=\"Smut\">Smut</a>\n" +
                "                        </div>\n" +
                "                                            <div class=\"col-xs-6\">\n" +
                "                            <a href=\"http://mangahere.today/mangas/sports\" title=\"Sports\">Sports</a>\n" +
                "                        </div>\n" +
                "                                            <div class=\"col-xs-6\">\n" +
                "                            <a href=\"http://mangahere.today/mangas/supernatural\" title=\"Supernatural\">Supernatural</a>\n" +
                "                        </div>\n" +
                "                                            <div class=\"col-xs-6\">\n" +
                "                            <a href=\"http://mangahere.today/mangas/tragedy\" title=\"Tragedy\">Tragedy</a>\n" +
                "                        </div>\n" +
                "                                            <div class=\"col-xs-6\">\n" +
                "                            <a href=\"http://mangahere.today/mangas/webtoons\" title=\"Webtoons\">Webtoons</a>\n" +
                "                        </div>\n" +
                "                                            <div class=\"col-xs-6\">\n" +
                "                            <a href=\"http://mangahere.today/mangas/yaoi\" title=\"Yaoi\">Yaoi</a>\n" +
                "                        </div>\n" +
                "                                            <div class=\"col-xs-6\">\n" +
                "                            <a href=\"http://mangahere.today/mangas/yuri\" title=\"Yuri\">Yuri</a>\n" +
                "                        </div>\n" +
                "                    \n" +
                "                </div>\n" +
                "            </div>";

        List<Genre> genres = new ArrayList<>();
        Elements elements1 = Jsoup.parse(html).select("div.col-xs-6");
        for (int i = 0; i < 5; i++ ){
            Log.i("loopList", elements1.get(i).text());
            genres.add(new Genre(elements1.get(i).text(),
                    elements1.get(i).select("a").attr("href")));

        }
        return genres;
    }
}
