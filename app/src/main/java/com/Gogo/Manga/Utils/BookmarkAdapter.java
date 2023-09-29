package com.Gogo.Manga.Utils;


import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.Gogo.Manga.R;
import com.Gogo.Manga.Activity.DetailActivity;
import com.Gogo.Manga.model.Bookmark;
import com.balysv.materialripple.MaterialRippleLayout;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class BookmarkAdapter extends RecyclerView.Adapter<BookmarkAdapter.ViewHolder> {
    String TAG ="BookmarkAdapterTAG";
    private List<Bookmark> mArtikelList;
    private Context mContext;


    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView txtNama, txtChap,txtLabel;
        private ImageView imageView;
        public View layout;
        MaterialRippleLayout ripple;
        private ImageButton imgDel;
        public ViewHolder(View v) {
            super(v);
            layout = v;
            imgDel = itemView.findViewById(R.id.imgDel);
            txtLabel = itemView.findViewById(R.id.txtLabel);
            ripple = itemView.findViewById(R.id.ripple);
            txtNama = (TextView) itemView.findViewById(R.id.postTitle);
            txtChap = (TextView) itemView.findViewById(R.id.chapter);
            imageView = (ImageView) itemView.findViewById(R.id.postImage);
        }
    }


    // Provide a suitable constructor (depends on the kind of dataset)
    public BookmarkAdapter(List<Bookmark> myDataset, Context context ) {
        mArtikelList = myDataset;
        mContext = context;
    }

    // Create new views (invoked by the swife manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,
                                         int viewType) {
        // create a new view
        LayoutInflater inflater = LayoutInflater.from(
                parent.getContext());
        View v =
                inflater.inflate(R.layout.list_post, parent, false);
        // set the view's size, margins, paddings and swife parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the swife manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.txtLabel.setVisibility(View.GONE);
        holder.imgDel.setVisibility(View.VISIBLE);
        final Bookmark artikel = mArtikelList.get(position);
        holder.txtNama.setText(artikel.getTitle());
        Log.d(TAG, "onBindViewHolder: "+artikel.getTitle());
        holder.txtChap.setVisibility(View.GONE);
        Glide.with(mContext).load(artikel.getImage()).into(holder.imageView);

        //listen to single view swife click
        holder.ripple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i =  new Intent(mContext, DetailActivity.class);
                i.putExtra("link", artikel.getUrl());
                i.putExtra("from", "BookmarkActivity");
                mContext.startActivity(i);
            }
        });
        holder.imgDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SweetAlertDialog(mContext, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Select One")
                        .setContentText("Do You Want to Delete?")
                        .setConfirmText("Yes")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {

                                deleteList(artikel.getId());
                                sDialog.dismissWithAnimation();
                            }
                        })
                        .setCancelButton("Cancel", new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.dismissWithAnimation();
                            }
                        })
                        .show();
            }
        });


    }

    private DatabaseReference mDatabase;

    private void deleteList(String id) {
        mDatabase = FirebaseDatabase.getInstance("https://manga-ko-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference();
//
        if (Constant.getKey(mContext) != null) {
            mDatabase.child("movieKoBookmark")
                    .child(Constant.getKey(mContext))
                    .child(id).removeValue(new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                    Toast.makeText(mContext, "success delete...", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mArtikelList.size();
    }



}