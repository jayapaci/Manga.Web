package com.Gogo.Manga.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.Gogo.Manga.Activity.DetailActivity;
import com.Gogo.Manga.R;
import com.Gogo.Manga.Utils.Constant;
import com.Gogo.Manga.model.Item;
import com.balysv.materialripple.MaterialRippleLayout;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by ASUS on 12/09/2018.
 */

public class post_adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<Item> dataList;
    private final Context context;

    private final int  CONTENT_TYPE=1;
    private final int AD_TYPE=2;
    public post_adapter(Context context, ArrayList<Item> dataList) {
        this.dataList = dataList;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        switch (viewType) {
            case CONTENT_TYPE:
                View viewItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_post, parent, false);
                viewHolder = new ListHolderItem(viewItem);
                break;
            case AD_TYPE:
                View viewAds = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_ads_medium_native, parent, false);
                viewHolder = new ViewHolderAdMob(viewAds);
                break;
        }
        return viewHolder;

//        LayoutInflater layoutInflater = LayoutInflater.from(context);
//        View view = layoutInflater.inflate(R.layout.list_post, parent, false);
//        return new EpisodeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int pos) {
        int position =holder.getAdapterPosition();
        Log.d("getLink", dataList.get(position).getSlug());
        switch (getItemViewType(position)) {
            case CONTENT_TYPE:
                ListHolderItem listHolderItem = (ListHolderItem) holder;
                Log.d( "onBindViewHolder: ",dataList.get(position).getSlug());
                Glide.with(context).load(dataList.get(position).getCover()).into(listHolderItem.imageView);
                listHolderItem.txtNama.setText(dataList.get(position).getTitle());
                listHolderItem.txtRate.setVisibility(View.VISIBLE);
                listHolderItem.txtRate.setText(dataList.get(position).getRating());
                if (dataList.get(position).getChapter()==null) {
                    listHolderItem.txtChap.setVisibility(View.GONE);
                    listHolderItem.txtLabel.setVisibility(View.GONE);
                    listHolderItem.txtRate.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_bookmark_24, 0, 0, 0);

                } else  {
                    if (dataList.get(position).getChapter().equals(dataList.get(position).getTitle())) {
                        listHolderItem.txtChap.setVisibility(View.GONE);
                        listHolderItem.txtLabel.setVisibility(View.GONE);
                    }else {

                        listHolderItem.txtChap.setText(dataList.get(position).getChapter());

                    }
                }


                listHolderItem.ripple.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //   Toast.makeText(context, "you have clicked"+ dataList.get(position).getSlug(), Toast.LENGTH_SHORT).show();
                        new Timer().schedule(new TimerTask() {
                            @Override
                            public void run() {

                                Intent intent = new Intent(context, DetailActivity.class);
                                intent.putExtra("link", dataList.get(position).getSlug());
                                intent.putExtra("title", dataList.get(position).getTitle());
                                intent.putExtra("from", "Main");
                                context.startActivity(intent);
                                //TODO
                            }
                        }, 300);

//
                    }
                });
                break;
            case AD_TYPE:
                ViewHolderAdMob h = (ViewHolderAdMob) holder;
                break;
        }

    }
    @Override
    public int getItemCount() {
        return dataList.size();
    }

    @Override
    public int getItemViewType(int position) {
//        return posts.get(position).getViewType();
//        if ((position+1) % adsPerPage() == 0 && (position+1) != 1) {
//            return AD_TYPE;
//        }
        return CONTENT_TYPE;

    }
    private int adsPerPage(){
        return Constant.getNativePerItem(context)+1;
    }
    public class ListHolderItem extends RecyclerView.ViewHolder{
        private TextView txtNama, txtChap,txtLabel,txtRate;
        private ImageView imageView;
        private MaterialRippleLayout ripple;
        public ListHolderItem(View itemView) {
            super(itemView);
            txtRate =   itemView.findViewById(R.id.txtRate);
            txtLabel =   itemView.findViewById(R.id.txtLabel);
            ripple =   itemView.findViewById(R.id.ripple);
            txtNama = (TextView) itemView.findViewById(R.id.postTitle);
            txtChap = (TextView) itemView.findViewById(R.id.chapter);
            imageView = (ImageView) itemView.findViewById(R.id.postImage);
        }
    }

    private class ViewHolderAdMob extends RecyclerView.ViewHolder {

        public ViewHolderAdMob(@NonNull View itemView) {
            super(itemView);

        }
    }


}