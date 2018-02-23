package com.bf.popularmovies.model;

/*
 * @author frielb 
 * Created on 23/02/2018
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TMDBGenre {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("name")
    @Expose
    private String name;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
