package com.app.phedev.popmovie.retrofit;

import com.app.phedev.popmovie.pojo.MovieResponse;
import com.app.phedev.popmovie.pojo.ReviewResponse;
import com.app.phedev.popmovie.pojo.TrailerResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by phedev in 2017.
 */

public interface Service {

    @GET("movie/popular")
    Call<MovieResponse> getPopularMovies(@Query("api_key")String apikey);

    @GET("movie/top_rated")
    Call<MovieResponse> getTopRatedMovies(@Query("api_key")String apikey);

    @GET("movie/{movie_id}/videos")
    Call<TrailerResponse> getMovieTrailer(@Path("movie_id") int id, @Query("api_key") String apikey);

    @GET("movie/{movie_id}/reviews")
    Call<ReviewResponse> getMovieReview(@Path("movie_id") int id, @Query("api_key") String apikey);

}
