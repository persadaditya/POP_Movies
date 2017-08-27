package com.app.phedev.popmovie.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by phedev in 2017.
 */

public class Movie implements Parcelable{
    @SerializedName("poster_path")
    @Expose
    private String posterPath;
    @SerializedName("overview")
    @Expose
    private String plot;
    @SerializedName("release_date")
    @Expose
    private String release;
    @SerializedName("genre_ids")
    @Expose
    private List<Integer> genre = new ArrayList<Integer>();
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("original_title")
    @Expose
    private String oriTitle;
    @SerializedName("original_language")
    @Expose
    private String oriLang;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("backdrop_path")
    @Expose
    private String backdrop;
    @SerializedName("vote_average")
    @Expose
    private Double voteAvg;
    @SerializedName("popularity")
    @Expose
    private Double popular;

    public Movie(String posterPath, String plot,String release, List<Integer> genre, Integer id, String oriTitle,
                 String oriLang, String title, String backdrop, Double voteAvg, Double popular){

        this.title = title;
        this.posterPath = posterPath;
        this.plot = plot;
        this.release = release;
        this.genre = genre;
        this.id = id;
        this.oriTitle = oriTitle;
        this.oriLang = oriLang;
        this.backdrop = backdrop;
        this.popular = popular;
        this.voteAvg = voteAvg;
    }
    private String baseImageURL = "https://image.tmdb.org/t/p/w500";



    public Movie(){

    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel source) {
            return new Movie(source);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    public String getPosterPath(){
        return "https://image.tmdb.org/t/p/w500" + posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getPlot() {
        return plot;
    }

    public void setPlot(String plot) {
        this.plot = plot;
    }

    public String getRelease() {
        return release;
    }

    public void setRelease(String release) {
        this.release = release;
    }

    public List<Integer> getGenre() {
        return genre;
    }

    public void setGenre(List<Integer> genre) {
        this.genre = genre;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getOriTitle() {
        return oriTitle;
    }

    public void setOriTitle(String oriTitle) {
        this.oriTitle = oriTitle;
    }

    public String getOriLang() {
        return oriLang;
    }

    public void setOriLang(String oriLang) {
        this.oriLang = oriLang;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBackdrop() {
        return backdrop;
    }

    public void setBackdrop(String backdrop) {
        this.backdrop = backdrop;
    }

    public Double getVoteAvg() {
        return voteAvg;
    }

    public void setVoteAvg(Double voteAvg) {
        this.voteAvg = voteAvg;
    }

    public Double getPopular() {
        return popular;
    }

    public void setPopular(Double popular) {
        this.popular = popular;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.title);
        parcel.writeString(this.posterPath);
        parcel.writeString(this.plot);
        parcel.writeString(this.release);
        parcel.writeString(this.oriTitle);
        parcel.writeString(this.oriLang);
        parcel.writeString(this.backdrop);
        parcel.writeString(this.baseImageURL);
        parcel.writeInt(this.id);
        parcel.writeValue(this.popular);
        parcel.writeValue(this.voteAvg);
    }

    private Movie(Parcel in) {
        this.title = in.readString();
        this.posterPath = in.readString();
        this.plot = in.readString();
        this.release = in.readString();
        this.oriTitle = in.readString();
        this.oriLang = in.readString();
        this.title = in.readString();
        this.backdrop = in.readString();
        this.baseImageURL = in.readString();
        this.id = in.readInt();
        this.popular = in.readDouble();
        this.voteAvg = (Double) in.readValue(Double.class.getClassLoader());

    }

}
