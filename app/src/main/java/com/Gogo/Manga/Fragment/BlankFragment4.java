package com.Gogo.Manga.Fragment;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.Gogo.Manga.R;
import com.Gogo.Manga.Activity.LoginActivity;
import com.Gogo.Manga.Activity.MainActivity;
import com.Gogo.Manga.Activity.PrivacyActivity;
import com.Gogo.Manga.BuildConfig;
import com.Gogo.Manga.Utils.BookmarkDBHelper;
import com.Gogo.Manga.Utils.Constant;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import cn.pedant.SweetAlert.SweetAlertDialog;


/**
 * A simple {@link Fragment} subclass.
 */
public class BlankFragment4 extends Fragment implements View.OnClickListener {

    Button button, button2, button3, button4;
    private TextView textView, textView2;

    public BlankFragment4() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view_frag1 = inflater.inflate(R.layout.fragment_blank4, container, false);
        TextView textView1 = view_frag1.findViewById(R.id.version_app);
        TextView textView2 = view_frag1.findViewById(R.id.text_name);

        textView1.setText("Version " + BuildConfig.VERSION_NAME);

        view_frag1.findViewById(R.id.clear_history).setOnClickListener(this);
        view_frag1.findViewById(R.id.clear_bookmark).setOnClickListener(this);
        view_frag1.findViewById(R.id.clear_cache).setOnClickListener(this);
        view_frag1.findViewById(R.id.facebook).setOnClickListener(this);
        view_frag1.findViewById(R.id.instagram).setOnClickListener(this);
        view_frag1.findViewById(R.id.privacy_policy).setOnClickListener(this);
        view_frag1.findViewById(R.id.rate_app).setOnClickListener(this);
        view_frag1.findViewById(R.id.other_app).setOnClickListener(this);
        view_frag1.findViewById(R.id.sign_out).setOnClickListener(this);

        if (Constant.getKey(requireContext()) == null) ifNotLogin(view_frag1);
        else textView2.setText(Constant.getName(requireContext()));
        return view_frag1;
    }

    private void ifNotLogin(View view_frag1) {
        view_frag1.findViewById(R.id.sign_out).setVisibility(View.GONE);
        view_frag1.findViewById(R.id.clear_bookmark).setVisibility(View.GONE);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.clear_history:
                deleteHistory();
                break;
            case R.id.clear_bookmark:
                deleteBookmark();
                break;
            case R.id.clear_cache:
                deleteCache();
                break;
            case R.id.facebook:
                openLink("https://www.facebook.com/MangaAnime88/");
                break;
            case R.id.instagram:
                openLink("https://www.instagram.com/dramako_asian/");
                break;
            case R.id.privacy_policy:
                openLayout(PrivacyActivity.class);
                break;
            case R.id.rate_app:
                openLink("https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID);
                break;
            case R.id.other_app:
                String developerLink = "https://play.google.com/store/apps/details?id=com.kdrama.dramako";
                openLink(developerLink);
                break;
            case R.id.sign_out:
                logOut();
                break;
        }
    }

    private void deleteCache() {
        new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Select One")
                .setContentText("Are you sure want to do this?")
                .setConfirmText("Yes")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        deleteHistory();
                        Constant.setKey(requireContext(), null);
                        Constant.setName(getActivity(), null);
                        startActivity(new Intent(requireContext(), LoginActivity.class));
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

    private void deleteHistory() {
        new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Select One")
                .setContentText("Are you sure want to do this?")
                .setConfirmText("Yes")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        BookmarkDBHelper bookmarkDBHelper = new BookmarkDBHelper(requireContext());
                        bookmarkDBHelper.deleteHistory();
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

    private void logOut() {
        new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Select One")
                .setContentText("Are you sure want to do this?")
                .setConfirmText("Yes")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        if (Constant.getKey(getActivity()) != null) {
                            Constant.setName(getActivity(), null);
                            Constant.setKey(getActivity(), null);
                            Toast.makeText(getActivity(), "success logout", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(requireContext(), MainActivity.class));
                        }
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

    private void deleteBookmark() {
        new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Select One")
                .setContentText("Are you sure want to do this?")
                .setConfirmText("Yes")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        DatabaseReference mDatabase = FirebaseDatabase.getInstance("https://manga-ko-default-rtdb.asia-southeast1.firebasedatabase.app")
                                .getReference();
                        mDatabase.child("movieKoBookmark")
                                .child(Constant.getKey(getActivity()))
                                .removeValue(new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                        Toast.makeText(requireContext(), "success delete", Toast.LENGTH_SHORT).show();
                                    }
                                });
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

    private void openLayout(Class<?> cls) {
        Intent i = new Intent(requireContext(), cls);
        startActivity(i);
    }

    private void openLink(String url) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }
}
