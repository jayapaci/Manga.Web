package com.Gogo.Manga.model;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
public class MangaModel {

    @SerializedName("manga")
    @Expose
    private List<Manga> manga = null;
    @SerializedName("success")
    @Expose
    private Integer success;

    public List<Manga> getManga() {
        return manga;
    }

    public void setManga(List<Manga> manga) {
        this.manga = manga;
    }

    public Integer getSuccess() {
        return success;
    }

    public void setSuccess(Integer success) {
        this.success = success;
    }


    public class Manga {

        @SerializedName("title")
        @Expose
        private String title;
        @SerializedName("alternative_title")
        @Expose
        private String alternativeTitle;
        @SerializedName("url")
        @Expose
        private String url;
        @SerializedName("image")
        @Expose
        private String image;
        @SerializedName("tokens")
        @Expose
        private List<String> tokens = null;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getAlternativeTitle() {
            return alternativeTitle;
        }

        public void setAlternativeTitle(String alternativeTitle) {
            this.alternativeTitle = alternativeTitle;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public List<String> getTokens() {
            return tokens;
        }

        public void setTokens(List<String> tokens) {
            this.tokens = tokens;
        }

    }
}
