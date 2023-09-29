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

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;

public class GenreActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    GridLayoutManager manager;
    private post_adapter adapter;
    ArrayList<Item> ArrayList;
    String error = "";
    ProgressBar progressBar;
    Integer pageNumber = 1;
    int currentItems, totalItems, scrollOutItems;
    Boolean isScrollig = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_genre);
        ArrayList = new ArrayList<>();
        progressBar = findViewById(R.id.progressBar);
        recyclerView = findViewById(R.id.postList);
        adapter = new post_adapter(GenreActivity.this, ArrayList);
        manager = new GridLayoutManager(GenreActivity.this, 3);
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
                     new ParsePageTask().execute(getIntent().getStringExtra("link")+"/watch/"+pageNumber++);
                    progressBar.setVisibility(View.VISIBLE);
                    // new Description().execute();
                }
            }
        });

        new ParsePageTask().execute(getIntent().getStringExtra("link")+"/watch/"+pageNumber++);

    }


    class ParsePageTask extends AsyncTask<String, Void, String> {
        protected String doInBackground(String... urls) {
            try {
                Document doc = Jsoup.connect(urls[0]).get();
                Elements link = doc.getElementsByClass("inner-content container").select("div#router-view");
                return link.toString();
            } catch (Exception ignored) {
            }

            return "";
        }
        protected void onPostExecute(String result) {
            Document mBlogDocument = Jsoup.parse(result);
            Elements mElement = mBlogDocument.getElementsByClass("bg-cover-faker") ;
            Elements mElementDataSize2 = mElement.select("div#discover-response")
                    .select("ul.filter-results").select("li");

            Log.i("mElementDataSize2",   mElementDataSize2.html());
            for (int i = 0; i< mElementDataSize2.size(); i++){
                String tittle = mElementDataSize2.get(i).select("div.poster-with-subject")
                        .select("div.subject").select("h2").text();
                String image = mElementDataSize2.get(i).select("div.poster-with-subject")
                        .select("a").select("img").attr("data-src");

                String link =  mElementDataSize2.get(i).select("div.poster-with-subject")
                        .select("a").first().attr("href");
                String rate = mElementDataSize2.get(i).select("em.genres-item-rate").text() ;

                try {
                    String chap = mElementDataSize2.get(i).select("div.genres-item-info").select("a.genres-item-chap")
                           .text();
                    ArrayList.add(new Item(tittle, Constant.base_url+image.replaceAll("-110x150",""),Constant.base_url+ link, tittle,rate,1));
                } catch (NullPointerException n) {

                    ArrayList.add(new Item(tittle, Constant.base_url+image, Constant.base_url+ link, tittle,rate,1));
                }

            }
            progressBar.setVisibility(View.GONE);
            adapter.notifyDataSetChanged();
            // Locate the content a
        }
    }
}