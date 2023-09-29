package com.Gogo.Manga.model;

/**
 * Created by ASUS on 02/05/2018.
 */

public class Bookmark {
    private String id;
    private String title;
    private String image;
    private String url;

    public Bookmark(){

    }

    public Bookmark(String id, String title, String image, String url) {
        this.id = id;
        this.title = title;
        this.image = image;
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
