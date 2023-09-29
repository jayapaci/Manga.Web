package com.Gogo.Manga.model;

/**
 * Created by ASUS on 15/10/2018.
 */

public class Item {
    String title, cover, slug, chapter, rating;
    int viewType;

    public Item(String title, String cover, String slug, String chapter, String rating, int viewType) {
        this.title = title;
        this.cover = cover;
        this.slug = slug;
        this.chapter = chapter;
        this.rating = rating;
        this.viewType = viewType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getChapter() {
        return chapter;
    }

    public void setChapter(String chapter) {
        this.chapter = chapter;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public int getViewType() {
        return viewType;
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }
}
