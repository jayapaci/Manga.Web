package com.Gogo.Manga.Activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.Gogo.Manga.R;
import com.Gogo.Manga.Utils.Constant;
import com.Gogo.Manga.adapter.post_adapter;
import com.Gogo.Manga.model.Item;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class UpdateActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    SharedPreferences sharedpreferences;
    public static final String mypreference = "mypref";

    private String TAG ="UpdateActivityTAG";
    ProgressBar progressBar;
    Boolean isScrollig = true;
    int currentItems, totalItems, scrollOutItems;
    int pageNumber = 1;
    GridLayoutManager manager;
    java.util.ArrayList<Item> ArrayList;
    String error = "";
    private post_adapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        sharedpreferences = getSharedPreferences(mypreference,
                Context.MODE_PRIVATE);

        ArrayList = new ArrayList<>();
        progressBar =  findViewById(R.id.progressBar);
        recyclerView = findViewById(R.id.postList);
        manager = new GridLayoutManager(this,3);
        adapter = new post_adapter(this, ArrayList);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        progressBar.getIndeterminateDrawable().setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.MULTIPLY);

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
                    progressBar.setVisibility(View.VISIBLE);
                    new  Description().execute();
                }
            }
        });
        new  Description().execute();
    }

    private class Description extends AsyncTask<Void, Void, String> {
        String desc;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(Void... params) {
            String url = Constant.base_url+ "new-manga/"+pageNumber++  ;
            try {
                // Connect to the web site
                Document mBlogDocument = Jsoup.connect(url).get();
                // Using Elements to get the Meta data
                Element mElement =  mBlogDocument.getElementById("wrapper-inner");

                return mElement.toString();
            } catch (IOException | IndexOutOfBoundsException e) {
                Log.i("hasilError", e.toString());
                error = "error";
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            progressBar.setVisibility(View.GONE);
            if (error.equals("error")) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(UpdateActivity.this, "Can't Load This Session, Check Your Internet Connection", Toast.LENGTH_SHORT).show();
            } else if (error.equals("")) {
                try {
                    progressBar.setVisibility(View.GONE);

                    Document  document = Jsoup.parse(result);
                    Elements mElementDataSize2 = document.select( "div#content")
                            .select("div.dark-segment").select("ul.clearfix").select("li");
                    Log.d(TAG, "onPostExecute: "+mElementDataSize2);
                    for (int i = 0; i< mElementDataSize2.size(); i++){
                        String tittle = mElementDataSize2.get(i).select("div.poster-subject").select("h2").text();
                        String image = mElementDataSize2.get(i).select("img").attr("data-src");
                        String link =mElementDataSize2.get(i).select("div.poster-subject").select("h2").select("a").attr("href");
                        String rate =  mElementDataSize2.get(i).select("div.poster-media")
                                .select("a").select("div.poster-overlay").select("span.rating").text() ;

                        ArrayList.add(new Item(tittle, Constant.base_url+image.replaceAll("-110x150",""),
                                Constant.base_url+link, null,rate,1));


                    }
                    Log.d(TAG, "doInBackground: "+ArrayList.size());
                    adapter.notifyDataSetChanged();

                }catch (NullPointerException n){

                }
            }
        }
    }
}
