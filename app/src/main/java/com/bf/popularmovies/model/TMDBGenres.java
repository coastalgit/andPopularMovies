package com.bf.popularmovies.model;

/*
 * @author frielb 
 * Created on 23/02/2018
 */

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TMDBGenres {

    @SerializedName("genres")
    @Expose
    private List<TMDBGenre> genres = null;

    public List<TMDBGenre> getGenres() {
        return genres;
    }

    public void setGenres(List<TMDBGenre> genres) {
        this.genres = genres;
    }

}