
package com.Gogo.Manga.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Title {

    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("$t")
    @Expose
    private String $t;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String get$t() {
        return $t;
    }

    public void set$t(String $t) {
        this.$t = $t;
    }

}
