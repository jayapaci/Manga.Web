package com.Gogo.Manga.Fragment;

import static com.Gogo.Manga.Utils.Constant.base_url;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.Gogo.Manga.Activity.DetailActivity;
import com.Gogo.Manga.Activity.SearchActivity;
import com.Gogo.Manga.Activity.UpdateActivity;
import com.Gogo.Manga.R;
import com.Gogo.Manga.Utils.Constant;
import com.Gogo.Manga.adapter.GenreAdapter;
import com.Gogo.Manga.adapter.post_adapter;
import com.Gogo.Manga.model.Genre;
import com.Gogo.Manga.model.Item;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.bumptech.glide.Glide;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.NativeAdBase;
import com.facebook.ads.NativeAdListener;
import com.facebook.ads.NativeBannerAd;
import com.facebook.ads.NativeBannerAdView;
import com.google.android.ads.nativetemplates.TemplateView;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageClickListener;
import com.synnapps.carouselview.ImageListener;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cn.pedant.SweetAlert.SweetAlertDialog;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {
    ArrayList<Item> ArrayList;
    String error = "";

    CarouselView carouselView;
    SweetAlertDialog pDialog;
    ArrayList imageCursel = new ArrayList();
    ArrayList linkCursel = new ArrayList();
    ArrayList <String> tittleCursel = new ArrayList();
    String htmlPopular = "";
    TextView textView, textView2,textViewNewManga;
    private String upHtml;
    SharedPreferences sharedpreferences;
    public static final String mypreference = "mypref";
    EditText edit_txt ;
    TransactionDetails purchaseTransactionDetails = null;
    String  TAG ="HomeFragmentTAG";
    private Context context;
    private ImageView imgNedd;
    private RecyclerView recyclerView,recyclerVie_genre;
    public HomeFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the swife for this fragment
        final View view_frag1 = inflater.inflate(R.layout.fragment_home, container, false);
        context = getActivity();
        sharedpreferences = getActivity().getSharedPreferences(mypreference,
                Context.MODE_PRIVATE);
        ArrayList = new ArrayList<>();
        imgNedd = view_frag1.findViewById(R.id.imgNedd);
        edit_txt = (EditText) view_frag1.findViewById(R.id.search_edit);
        textViewNewManga = view_frag1.findViewById(R.id.textViewNewManga);
        recyclerView = view_frag1.findViewById(R.id.recyclerView);
        recyclerVie_genre = view_frag1.findViewById(R.id.recyclerVie_genre);
        ///////////////list Cylle
        imageCursel =  new ArrayList();
        carouselView = (CarouselView) view_frag1.findViewById(R.id.carouselView);

        textView = view_frag1.findViewById(R.id.txtUpdate);


        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {

                        Intent intent = new Intent(getActivity(), UpdateActivity.class);
                        intent.putExtra("key", "update");
                        startActivity(intent);
                    }
                }, 300);
            }
        });


        edit_txt.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    Toast.makeText(getActivity(), "Search "+edit_txt.getText().toString().trim(), Toast.LENGTH_SHORT).show();
                    edit_txt.clearFocus();
                    InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    Intent intent = new Intent(getActivity(), SearchActivity.class);
                    intent.putExtra("keyword",edit_txt.getText().toString().trim());
                    startActivity(intent);
                    return true;
                }
                return false;
            }
        });

        pDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.colorPrimary));
        pDialog.setTitleText("Loading ...");
        pDialog.setCancelable(true);
        pDialog.show();

        setUpNativeAds(view_frag1);
        new  Description().execute();
        return view_frag1;
    }



    private void setUpNativeAds(View view_frag1) {
        if (!hasSubscription()) {

            RelativeLayout relativeAdsNative = view_frag1.findViewById(R.id.relativeAdsNative);
            TemplateView templateView = view_frag1.findViewById(R.id.my_template);
            switch (Constant.getAdsType(getActivity())){
                case 1:
                    templateView.setVisibility(View.VISIBLE);
                    AdLoader adLoader = new AdLoader.Builder(getActivity(), Constant.getNativeId(getActivity()))
                            .forUnifiedNativeAd(new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
                                @Override
                                public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {
                                    templateView.setNativeAd(unifiedNativeAd);

                                }
                            })
                            .build();

                    adLoader.loadAd(new AdRequest.Builder().build());
                    break;
                case 3:
                    NativeBannerAd mNativeBannerAd = new NativeBannerAd(getActivity(), Constant.getNativeId(getActivity()));
                    NativeAdListener nativeAdListener = new NativeAdListener() {
                        @Override
                        public void onMediaDownloaded(Ad ad) {

                        }

                        @Override
                        public void onError(Ad ad, AdError adError) {

                        }

                        @Override
                        public void onAdLoaded(Ad ad) {
                            relativeAdsNative.addView(NativeBannerAdView.render(getActivity(), mNativeBannerAd,
                                    NativeBannerAdView.Type.HEIGHT_100) );
                        }

                        @Override
                        public void onAdClicked(Ad ad) {

                        }

                        @Override
                        public void onLoggingImpression(Ad ad) {

                        }
                    };

                    mNativeBannerAd.loadAd(
                            mNativeBannerAd.buildLoadAdConfig()
                                    .withAdListener(nativeAdListener)
                                    .withMediaCacheFlag(NativeAdBase.MediaCacheFlag.ALL)
                                    .build());
                    break;



            }
        }
    }

    private boolean hasSubscription() {
        if (purchaseTransactionDetails != null) {
            return purchaseTransactionDetails.purchaseInfo != null;
        }
        return false;
    }

    private void initTag(Element gen) {
        List<Genre> list =   getGenre(gen);
        GenreAdapter genreAdapter = new GenreAdapter(requireContext(), list);
        recyclerVie_genre.setAdapter(genreAdapter);
    }


    private class Description extends AsyncTask<Void, Void, Void> {
        String desc;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... params) {
            String url = "https://readm.org/";
            try {
                // Connect to the web site
                Document mBlogDocument = Jsoup.connect(url).get();
                // Using Elements to get the Meta data
                upHtml = mBlogDocument.getElementById("wrapper-inner").html();

            } catch (IOException | IndexOutOfBoundsException e) {
                Log.i("hasilError", e.toString());
                error = "error";
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            pDialog.hide();
            if (error.equals("error")) {
                Toast.makeText(getActivity().getApplicationContext(), "Can't Load This Session, Check Your Internet Connection", Toast.LENGTH_SHORT).show();
            } else if (error.equals("")) {

                Document mBlogDocument = Jsoup.parse(upHtml);

                Element gen = mBlogDocument.getElementById("sidebar-inner");
                initTag(gen);
                Elements popularUpdate = mBlogDocument.select( "div#router-view")
                        .select("div.dark-segment").get(1).select("ul").select("li");
                Log.i("getmElementDataSize2",popularUpdate.html());
//
                Log.i("elementsSlide", popularUpdate.html());
                for (int i = 0; i < 5; i ++){
                    String image = popularUpdate.get(i).select("a").select("div.trailer_simg").select("img").attr("data-src");
                    String judul = popularUpdate.get(i).select("a").select("h6").text();
                    String link = popularUpdate.get(i).select("a").attr("href");
                    linkCursel.add(base_url+link);
                    imageCursel.add(base_url+image.replaceAll("-110x150","") );
                    tittleCursel.add(judul);
                    Log.i("tittleCursel", judul);
                }                // Locate the content attribute

                textViewNewManga.setText(tittleCursel.get(0));
                carouselView.setImageListener(imageListener);
                carouselView.setPageCount(imageCursel.size());
                carouselView.setImageClickListener(new ImageClickListener() {
                    @Override
                    public void onClick(int position) {
                        //TODO
                        Intent intent = new Intent(getActivity(), DetailActivity.class);
                        intent.putExtra("link", base_url+ linkCursel.get(position).toString());
                        intent.putExtra("from", "Main");
                        startActivity(intent);
                        //  Toast.makeText(getActivity(), "You have Cliked " + linkCursel.get(position), Toast.LENGTH_SHORT).show();
                    }
                });

                carouselView.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset,
                                               int positionOffsetPixels) {
                    }

                    @Override
                    public void onPageSelected(int position) {
                        textViewNewManga.setText(tittleCursel.get(position));

                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {

                    }
                });

                setLatestAddManga(upHtml);
            }
        }
    }

    private void setLatestAddManga(String upHtml) {
        ArrayList<Item> ArrayList = new ArrayList<>();
        post_adapter adapter = new post_adapter(requireContext(), ArrayList);
        Document document = Jsoup.parse(upHtml);
        Element element = document.getElementsByClass("dark-segment").get(2);
        Elements ulli = element.select("ul.clearfix").select("li");
        for (Element li    : ulli  ) {
            Log.d(TAG, "setLatestAddManga: "+li.select("div.poster-subject").select("h2").text());
            String tittle = li.select("div.poster-subject").select("h2").text();
            String image = li.select("img").attr("data-src");
            String link =li.select("div.poster-media").select("a").attr("href");
            String rate = li.select("span.rating").text() ;

            ArrayList.add(new Item(tittle, Constant.base_url+image.replaceAll("-110x150",""),
                    Constant.base_url+link, null,rate,1));
        }
        recyclerView.setAdapter(adapter);

    }

    ImageListener imageListener = new ImageListener() {
        @Override
        public void setImageForPosition(int position, ImageView imageView) {
            Glide.with(getActivity()).load(imageCursel.get(position)).into(imageView);
        }

    };

    private List<Genre> getGenre(Element gen){

        List<Genre> genres = new ArrayList<>();
        Elements elements1 = Jsoup.parse(String.valueOf(gen))
                .select("ul.categories").select("li");
        for (int i = 0; i < elements1.size(); i++ ){
            Log.i("loopList", elements1.get(i).html());

            genres.add(new Genre(
                    elements1.get(i).select("a").text(),
                    base_url+elements1.get(i).select("a").attr("href")));

        }
        return genres;
    }


}