package com.Gogo.Manga.Fragment;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.Gogo.Manga.R;
import com.Gogo.Manga.Utils.Constant;
import com.Gogo.Manga.adapter.post_adapter;
import com.Gogo.Manga.model.Item;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class BlankFragment3 extends Fragment {
    String TAG ="BlankFragment3TAG";
    GridLayoutManager manager;
    private    RecyclerView recyclerView;
    private post_adapter adapter;
    ArrayList<Item> ArrayList;
    ProgressBar progressBar;
    Boolean isScrollig = true;
    int currentItems, totalItems, scrollOutItems;
    int pageNumber = 1;
    SharedPreferences sharedpreferences;
    public static final String mypreference = "mypref";
    String error = "";
    public BlankFragment3() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_blank3, container, false);

        sharedpreferences = getActivity().getSharedPreferences(mypreference,
                Context.MODE_PRIVATE);
        ArrayList = new ArrayList<>();
        progressBar = view.findViewById(R.id.progressBar);
        progressBar.getIndeterminateDrawable().setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.MULTIPLY);
        recyclerView = view.findViewById(R.id.postList);
        adapter = new post_adapter(getActivity(), ArrayList);
        manager = new GridLayoutManager(getActivity(),3);
//        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
//            @Override
//            public int getSpanSize(int position) {
//                switch (adapter.getItemViewType(position)){
//                    case 1:
//                        return 1;
//                    case 2:
//                        return manager.getSpanCount();
//                    default:
//                        return -1;
//
//                }
//            }
//        });

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
                    new Description().execute();
                }
            }
        });

        new Description().execute();
        return view;
    }


    private class Description extends AsyncTask<Void, Void, String> {
        String desc;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(Void... params) {
            String url = Constant.base_url+ "latest-releases/"+pageNumber++  ;
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
                Toast.makeText(getActivity(), "Can't Load This Session, Check Your Internet Connection", Toast.LENGTH_SHORT).show();
            } else if (error.equals("")) {
                try {
                    progressBar.setVisibility(View.GONE);

                    Document  document = Jsoup.parse(result);
                    Elements mElementDataSize2 = document.select( "div#content")
                            .select("div.dark-segment").select("ul.latest-updates").select("li.segment-poster-sm");
                    Log.d(TAG, "onPostExecute: "+mElementDataSize2);
                    for (int i = 0; i< mElementDataSize2.size(); i++){
                        String tittle = mElementDataSize2.get(i).select("div.poster-subject").select("h2").text();
                        String image = mElementDataSize2.get(i).select("img").attr("data-src");
                        String link =mElementDataSize2.get(i).select("div.poster-subject").select("h2").select("a").attr("href");
                        String rate =  mElementDataSize2.get(i).select("span").text() ;

                        String chap = mElementDataSize2.get(i).select("div.poster-subject").
                                select("ul.chapters").select("li")  .first()  .text();

                        ArrayList.add(new Item(tittle, Constant.base_url+image.replaceAll("-110x150",""),
                                Constant.base_url+link, "Chapter "+chap,rate,1));


                    }
                    Log.d(TAG, "doInBackground: "+ArrayList.size());
                    adapter.notifyDataSetChanged();

                }catch (NullPointerException n){

                }
            }
        }
    }
}
