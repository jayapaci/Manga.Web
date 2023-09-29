package com.Gogo.Manga.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.Gogo.Manga.Activity.LoginActivity;
import com.Gogo.Manga.R;
import com.Gogo.Manga.Utils.BookmarkAdapter;
import com.Gogo.Manga.Utils.Constant;
import com.Gogo.Manga.model.Bookmark;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;


public class FavFragment extends Fragment {
    private View view;

    private DatabaseReference mDatabase;
    private ArrayList<Bookmark> bookmarks = new ArrayList<>( );
    private ImageView imgNedd;
    private TextView txtNeed;
    private RecyclerView recyclerView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_fav, container, false);
        imgNedd = view.findViewById(R.id.imgNedd);
        txtNeed = view.findViewById(R.id.txtNeed);
        recyclerView = view.findViewById(R.id.postList);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));


        if (Constant.getKey(getActivity())!=null)getBookmark();
        else {
            recyclerView.setVisibility(View.GONE);
            txtNeed.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(new Intent(getActivity(), LoginActivity.class));
                    intent.putExtra("isHome", true);
                    startActivity(intent);
                }
            });
        }
        return view ;
    }

    private void getBookmark() {
        mDatabase = FirebaseDatabase.getInstance("https://manga-ko-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference();
        mDatabase.child("movieKoBookmark")
                .child(Constant.getKey(getActivity()))
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        bookmarks.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                            Bookmark bookmark = dataSnapshot.getValue(Bookmark.class);
                            bookmark.setId(dataSnapshot.getKey());
                            bookmarks.add(bookmark);
                        }
                        if (bookmarks.size()==0){
                            imgNedd.setVisibility(View.VISIBLE);
                            txtNeed.setVisibility(View.VISIBLE);
                            txtNeed.setText("No Manga has marked..");
                        }else {
                            imgNedd.setVisibility(View.GONE);
                            txtNeed.setVisibility(View.GONE);
                        }

                        Collections.reverse(bookmarks);
                        recyclerView.setAdapter(new BookmarkAdapter(bookmarks,getActivity()));

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}