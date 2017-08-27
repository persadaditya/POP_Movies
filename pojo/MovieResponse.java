package com.app.phedev.popmovie.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by phedev in 2017.
 */

public class MovieResponse implements Parcelable{
    @SerializedName("page")
    @Expose
    private int page;
    @SerializedName("results")
    @Expose
    private List<Movie> result = null;
    @SerializedName("total_results")
    @Expose
    private int totalRes;
    @SerializedName("total_pages")
    @Expose
    private int totalPages;

    protected MovieResponse(Parcel in) {
        page = in.readInt();
        result = in.createTypedArrayList(Movie.CREATOR);
        totalRes = in.readInt();
        totalPages = in.readInt();
    }

    public static final Creator<MovieResponse> CREATOR = new Creator<MovieResponse>() {
        @Override
        public MovieResponse createFromParcel(Parcel in) {
            return new MovieResponse(in);
        }

        @Override
        public MovieResponse[] newArray(int size) {
            return new MovieResponse[size];
        }
    };

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public List<Movie> getResult() {
        return result;
    }

    public void setResult(List<Movie> result) {
        this.result = result;
    }

    public int getTotalRes() {
        return totalRes;
    }

    public void setTotalRes(int totalRes) {
        this.totalRes = totalRes;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.page);
        parcel.writeInt(this.totalRes);
        parcel.writeInt(this.totalPages);
        parcel.writeTypedList(this.result);

    }
}
