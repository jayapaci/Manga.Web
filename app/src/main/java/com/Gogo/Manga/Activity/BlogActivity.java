package com.Gogo.Manga.Activity;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.Gogo.Manga.adapter.BlogAdapter;
import com.Gogo.Manga.model.Detail;
import com.Gogo.Manga.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

public class BlogActivity extends AppCompatActivity {
    private String TAG ="BlogActivityTAG";
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog);
        recyclerView = findViewById(R.id.recyclerView);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setRefreshing(true);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData();
            }
        });

        getData();
    }

    private void getData() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        GetService getService = getRetrofitInstance().create(GetService.class);
        getService.getAllPost().enqueue(new Callback<Detail>() {
            @Override
            public void onResponse(Call<Detail> call, Response<Detail> response) {
                Log.d(TAG, "onResponse: "+response.body().getFeed().getEntry().size());
                swipeRefreshLayout.setRefreshing(false);
                recyclerView.setAdapter(new BlogAdapter(BlogActivity.this, response.body().getFeed().getEntry()));
            }

            @Override
            public void onFailure(Call<Detail> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                Log.d(TAG, "onFailure: ");
            }
        });
    }

    @Override
    public void onBackPressed() {

    }

    private Retrofit getRetrofitInstance() {
        Retrofit  retrofit = new Retrofit.Builder()
                .baseUrl("https://mangaweb-1.blogspot.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit;
    }

    public interface GetService {
        @GET("/feeds/posts/default?alt=json&&max-results=1000")
        Call<Detail> getAllPost();


    }
}