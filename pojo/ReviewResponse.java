package com.app.phedev.popmovie.pojo;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by phedev in 2017.
 */

public class ReviewResponse {

    @SerializedName("id")
    private int id_trailer;
    @SerializedName("results")
    private List<Review> results;
    @SerializedName("page")
    private int page_review;

    public int getId_trailer() {
        return id_trailer;
    }

    public void setId_trailer(int id_trailer) {
        this.id_trailer = id_trailer;
    }

    public List<Review> getResults() {
        return results;
    }

    public void setResults(List<Review> results) {
        this.results = results;
    }

    public int getPage_review() {
        return page_review;
    }

    public void setPage_review(int page_review) {
        this.page_review = page_review;
    }
}
