package com.bf.popularmovies.model;

/*
 * @author frielb 
 * Created on 21/02/2018
 * via http://www.jsonschema2pojo.org/
 */

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class TMDBSysConfig {

    @SerializedName("images")
    @Expose
    private TMDBImages images;
    @SerializedName("change_keys")
    @Expose
    private List<String> changeKeys = null;

    public TMDBImages getImages() {
        return images;
    }

    public void setImages(TMDBImages images) {
        this.images = images;
    }

    public List<String> getChangeKeys() {
        return changeKeys;
    }

    public void setChangeKeys(List<String> changeKeys) {
        this.changeKeys = changeKeys;
    }

}
