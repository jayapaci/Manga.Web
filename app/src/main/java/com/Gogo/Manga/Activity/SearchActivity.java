package com.Gogo.Manga.Activity;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.Gogo.Manga.R;
import com.Gogo.Manga.Utils.Constant;
import com.Gogo.Manga.adapter.post_adapter;
import com.Gogo.Manga.model.Item;
import com.Gogo.Manga.model.MangaModel;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;

public class SearchActivity extends AppCompatActivity {
    String TAG ="SearchActivityTAG";
    RecyclerView recyclerView;
    GridLayoutManager manager;
    private post_adapter adapter;
    java.util.ArrayList<Item> ArrayList;
    String error = "";
    ProgressBar progressBar;
    Integer pageNumber = 1;
    Boolean isScrollig = true;
    int currentItems, totalItems, scrollOutItems;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ArrayList = new ArrayList<>();
        progressBar = findViewById(R.id.progressBar);
        recyclerView = findViewById(R.id.postList);
        adapter = new post_adapter(SearchActivity.this, ArrayList);
        manager = new GridLayoutManager(SearchActivity.this, 3);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch (adapter.getItemViewType(position)){
                    case 1:
                        return 1;
                    case 2:
                        return manager.getSpanCount();
                    default:
                        return -1;

                }
            }
        });
        adapter.notifyDataSetChanged();

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL)
                {
                    isScrollig = true;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                currentItems = manager.getChildCount();
                totalItems = manager.getItemCount();
                scrollOutItems = manager.findFirstVisibleItemPosition();

                if(isScrollig && (currentItems + scrollOutItems == totalItems))
                {
                    isScrollig = false;
               //     progressBar.setVisibility(View.VISIBLE);
                 }
            }
        });
         progressBar.getIndeterminateDrawable().setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.MULTIPLY);
        getData();
    }
    private void getData() {
        GetService getService = getRetrofitInstance().create( GetService.class);
        getService.getAllPost(getIntent().getStringExtra("keyword"),
                "XMLHttpRequest").enqueue(new Callback<MangaModel>() {
            @Override
            public void onResponse(Call<MangaModel> call, Response<MangaModel> response) {
                progressBar.setVisibility(View.GONE);
                if (response.code()==200){

                    for (int i = 0; i < response.body().getManga().size(); i++) {
                        String tittle = response.body().getManga().get(i).getTitle();
                        String image = response.body().getManga().get(i).getImage();
                        String link = response.body().getManga().get(i).getUrl();
                        ArrayList.add(new Item(tittle,Constant.base_url+ image, Constant.base_url+ link, tittle,"",1));
                    }
                    adapter.notifyDataSetChanged();
                    Log.d(TAG, "onResponse: "+response.body().getSuccess());
                }

            }

            @Override
            public void onFailure(Call<MangaModel> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Log.d(TAG, "onFailure: ");
            }
        });
    }
    private Retrofit getRetrofitInstance() {
        Retrofit  retrofit = new Retrofit.Builder()
                .baseUrl("https://readm.org/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit;
    }

    public interface GetService {
        @FormUrlEncoded
        @POST("service/search")
        Call<MangaModel> getAllPost(
                @Field("phrase") String phrase,
                @Header("x-requested-with") String req );

    }

    class ParsePageTask extends AsyncTask<String, Void, String> {
        protected String doInBackground(String... urls) {
            try {
                Document doc = Jsoup.connect(urls[0]).get();
                Elements link =  doc.getElementsByClass("body-site").select("div.container-main") ;
                return link.toString();
            } catch (Exception ignored) {
            }

            return "";
        }

        protected void onPostExecute(String result) {
            progressBar.setVisibility(View.GONE);
            Log.i("getHtml", result);
            try {

                Document document = Jsoup.parse(result);
                //get last update
                Elements mElementDataSize2 = document.select("div.container-main-left")
                        .select("div.panel-search-story").select("div.search-story-item");
                for (int i = 0; i < mElementDataSize2.size(); i++) {
                    String tittle = mElementDataSize2.get(i).select("div.item-right").select("h3").text();
                    String image = mElementDataSize2.get(i).select("a.item-img").select("img").attr("src");

                    Log.i("elementsImg",  image);
                    String link = mElementDataSize2.get(i).select("a").attr("href");

                    String rate = mElementDataSize2.get(i).select("div.genres-item-rate").text() ;
                    ArrayList.add(new Item(tittle, image, link, tittle,rate,1));
                }

                adapter.notifyDataSetChanged();
            } catch (NullPointerException | IndexOutOfBoundsException n) {
                n.getMessage();
            }

        }
    }
}