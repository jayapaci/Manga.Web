package com.Gogo.Manga.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.Gogo.Manga.Activity.ExpandActivity;
import com.Gogo.Manga.R;
import com.Gogo.Manga.model.Entry;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.List;

public class BlogAdapter extends RecyclerView.Adapter<BlogAdapter.ViewHolder>  {
    private Context context;
    private List<Entry> entries;

    public BlogAdapter(Context context, List<Entry> entries) {
        this.context = context;
        this.entries = entries;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_blog, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.txtTitle.setText(entries.get(position).getTitle().get$t());
        holder.txtDesc.setText(getDesc(entries.get(position).getContent().get$t()));
        Glide.with(context).load(fetchGambar(entries.get(position).getContent().get$t())).into(holder.image);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ExpandActivity.class);
                intent.putExtra("title", entries.get(position).getTitle().get$t());
                intent.putExtra("content", entries.get(position).getContent().get$t());
                context.startActivity(intent);

            }
        });
    }

    private String getDesc(String $t) {
        Document document = Jsoup.parse($t);
        if (document.select("img").size()!=0){
             document.select("img").remove();
            return document.text();
        }
        return document.text();
    }

    @Override
    public int getItemCount() {
        return entries.size();
    }

    private String fetchGambar(String content) {
        Document document = Jsoup.parse(content);
        if (document.select("img").size()!=0){
            return document.select("img").attr("src");
        }
       return "http://anekapaperaindah.id/img/No_image_available.jpg";
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView txtTitle,txtDesc;// init the item view's
        private ImageView image ;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtDesc = (TextView) itemView.findViewById(R.id.txtDesc);
            txtTitle = (TextView) itemView.findViewById(R.id.txtTitle);
            image =  itemView.findViewById(R.id.image);
        }
    }


}
