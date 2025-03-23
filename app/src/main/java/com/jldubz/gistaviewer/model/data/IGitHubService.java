package com.jldubz.gistaviewer.model.data;

import com.jldubz.gistaviewer.model.GitHubUser;
import com.jldubz.gistaviewer.model.gists.Gist;
import com.jldubz.gistaviewer.model.gists.GistComment;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.HEAD;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface IGitHubService {
    @GET("/gists/public")
    Call<List<Gist>> getPublicGists(@Query("page") int pageNum);

    // get your gists
    @GET("/gists")
    Call<List<Gist>> getYourGists(@Query("page") int pageNum);

    // get stared gists
    @GET("/gists/starred")
    Call<List<Gist>> getStarredGists(@Query("page") int pageNum);

    /// get with gist id
    @GET("/gists/{gistId}")
    Call<Gist> getGistById(@Path("gistId") String gistId);

    // get gist comment by id
    @GET("/gists/{gistId}/comments")
    Call<List<GistComment>> getGistCommentsById(@Path("gistId") String gistId, @Query("page") int pageNum);

    // get gist comment header by id
    @HEAD("/gists/{gistId}/comments")
    Call<Void> getGistCommentsHeaderById(@Path("gistId") String gistId);

    // GitHubUser
    @GET("/user")
    Call<GitHubUser> getLoggedInUser(@Header("Authorization") String auth);

}
