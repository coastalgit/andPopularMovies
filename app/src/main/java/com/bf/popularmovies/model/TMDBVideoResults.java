package com.bf.popularmovies.model;

/*
 * @author frielb 
 * Created on 21/03/2018
 */

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@SuppressWarnings("unused")
public class TMDBVideoResults {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("results")
    @Expose
    private List<TMDBVideo> results = null;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<TMDBVideo> getResults() {
        return results;
    }

    public void setResults(List<TMDBVideo> results) {
        this.results = results;
    }
}

