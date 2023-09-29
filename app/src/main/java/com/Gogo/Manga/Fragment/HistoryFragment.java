package com.Gogo.Manga.Fragment;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.Gogo.Manga.Activity.DetailActivity;
import com.Gogo.Manga.R;
import com.Gogo.Manga.Utils.BookmarkDBHelper;
import com.Gogo.Manga.model.HistoryModel;
import com.bumptech.glide.Glide;
import com.github.marlonlom.utilities.timeago.TimeAgo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HistoryFragment extends Fragment {
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_history, container, false);

        getData();
        return view;
    }

    private void getData() {
        ArrayList<HistoryModel>  arrayList   = new ArrayList<>();
        BookmarkDBHelper bookmarkDBHelper = new BookmarkDBHelper(requireContext());
        Cursor cursor= bookmarkDBHelper.getHistory();
        while (cursor.moveToNext()) {
            int id =  cursor.getInt(0);
            String url = cursor.getString(1);
            String title = cursor.getString(2);
            String chapter = cursor.getString(3);
            String url_chapter = cursor.getString(4);
            String image = cursor.getString(5);
            String time = cursor.getString(6);
            arrayList.add(new HistoryModel(id,url, title, chapter, url_chapter, image, Long.valueOf(time) ));
        }
        Collections.reverse(arrayList);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        RVAdapter adapter = new  RVAdapter(requireContext(),arrayList);
        recyclerView.setAdapter(adapter);
    }

    private class RVAdapter extends RecyclerView.Adapter<RVAdapter.ViewHolder>  {
        private Context context;
        private List<HistoryModel> entries;

        public RVAdapter(Context context, List<HistoryModel> entries) {
            this.context = context;
            this.entries = entries;
        }

        @NonNull
        @Override
        public RVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history, parent, false);
            return new RVAdapter.ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull RVAdapter.ViewHolder holder, int position) {
            HistoryModel historyModel = entries.get(position);
            Glide.with(context).load(historyModel.getImage()).into(holder.image);
            holder.text_chapter.setText(historyModel.getChapter());
            holder.text_title.setText(historyModel.getTitle());
            String text = TimeAgo.using(historyModel.getTime());
            holder.text_time.setText(" "+text);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, DetailActivity.class);
                    intent.putExtra("link", historyModel.getUrl());
                    intent.putExtra("title", historyModel.getTitle());
                    intent.putExtra("from", "Main");
                    context.startActivity(intent);
                }
            });
        }



        @Override
        public int getItemCount() {
            return entries.size();
        }


        public class ViewHolder extends RecyclerView.ViewHolder {
            private TextView text_title,text_chapter,text_time;// init the item view's
            private ImageView image ;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                text_chapter =   itemView.findViewById(R.id.text_chapter);
                text_time =   itemView.findViewById(R.id.text_time);
                text_title =   itemView.findViewById(R.id.text_title);
                image =  itemView.findViewById(R.id.image_view);
            }
        }


    }

}