package com.Gogo.Manga.model;

import android.os.Parcel;
import android.os.Parcelable;

public class SongModel implements Parcelable {
    String artis, title,path,lirik, image;
    boolean isVideo;

    public SongModel(){

    }
    public SongModel(String artis, String title, String path, String lirik, String image, boolean isVideo) {
        this.artis = artis;
        this.title = title;
        this.path = path;
        this.lirik = lirik;
        this.image = image;
        this.isVideo = isVideo;
    }

    protected SongModel(Parcel in) {
        artis = in.readString();
        title = in.readString();
        path = in.readString();
        lirik = in.readString();
        image = in.readString();
        isVideo = in.readByte() != 0;
    }

    public static final Creator<SongModel> CREATOR = new Creator<SongModel>() {
        @Override
        public SongModel createFromParcel(Parcel in) {
            return new SongModel(in);
        }

        @Override
        public SongModel[] newArray(int size) {
            return new SongModel[size];
        }
    };

    public String getArtis() {
        return artis;
    }

    public void setArtis(String artis) {
        this.artis = artis;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getLirik() {
        return lirik;
    }

    public void setLirik(String lirik) {
        this.lirik = lirik;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public boolean isVideo() {
        return isVideo;
    }

    public void setVideo(boolean video) {
        isVideo = video;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(artis);
        dest.writeString(title);
        dest.writeString(path);
        dest.writeString(lirik);
        dest.writeString(image);
        dest.writeByte((byte) (isVideo ? 1 : 0));
    }
}
