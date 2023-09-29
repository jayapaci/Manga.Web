package com.Gogo.Manga.model;

public class HistoryModel {

    private int id;
    private String url, title, chapter, url_chapter, image ;
    private long time;

    public HistoryModel(int id, String url, String title, String chapter, String url_chapter, String image, long time) {
        this.id = id;
        this.url = url;
        this.title = title;
        this.chapter = chapter;
        this.url_chapter = url_chapter;
        this.image = image;
        this.time = time;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getChapter() {
        return chapter;
    }

    public void setChapter(String chapter) {
        this.chapter = chapter;
    }

    public String getUrl_chapter() {
        return url_chapter;
    }

    public void setUrl_chapter(String url_chapter) {
        this.url_chapter = url_chapter;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
