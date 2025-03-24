package com.jldubz.gistaviewer.model.data;

import com.jldubz.gistaviewer.model.GitHubUser;
import com.jldubz.gistaviewer.model.gists.Gist;
import com.jldubz.gistaviewer.model.gists.GistComment;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HEAD;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface IGitHubService {
    @GET("/gists/public")
    Call<List<Gist>> getPublicGists(@QueryMap Map<String, Object> queries);

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
    Call<GitHubUser> getLoggedInUser();

    // star gist by id
    @PUT("/gists/{gistId}/star")
    Call<Response> starGistById(@Path("gistId") String gistId);

    @DELETE("/gists/{gistId}/star")
    Call<Response> unstarGistById(@Path("gistId") String gistId);

    @GET("/gists/{gistId}/star")
    Call<Response> getStarGistById(@Path("gistId") String gistId);

    // get gist comment header by id
    @POST("/gists/{gistId}/comments")
    Call<GistComment> createCommentOnGist(@Path("gistId") String gistId, @Body GistComment comment);


    // create comment on gist by id on gist url encode
    @FormUrlEncoded
    @POST("/gists/{gistId}/comments")
    Call<GistComment> createCommentOnGistUrlEncode(@Path("gistId") String gistId, @Field("body") String body);

    // upload Picture to githubuser
    @Multipart
    @POST("/user/pictures")
    Call<GitHubUser> uploadPictureToUser(@Part("picture") RequestBody picture, @Part("description") RequestBody description);
}
