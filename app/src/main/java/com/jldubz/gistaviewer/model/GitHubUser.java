package com.jldubz.gistaviewer.model;

import androidx.annotation.Keep;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

@Keep
public class GitHubUser {

    /*
    Modified sample data from: https://developer.github.com/v3/users/#get-the-authenticated-user
    {
      "login": "octocat",
      "id": 1,
      "avatar_url": "https://github.com/images/error/octocat_happy.gif",
      "name": "monalisa octocat",
      "company": "GitHub",
      "blog": "https://github.com/blog",
      "location": "San Francisco",
      "email": "octocat@github.com",
      "bio": "There once was...",
      "public_gists": 1,
      "followers": 20,
      "following": 0,
      "created_at": "2008-01-14T04:33:35Z",
      "updated_at": "2008-01-14T04:33:35Z",
      "private_gists": 81,
    }
    */

    private String login;
    private int id;
    private String avatar_url;
    private String name;
    private String company;
    private String blog;
    private String location;
    private String email;
    private String bio;
    private int public_gists;
    private int followers;
    private int following;
    @SerializedName("created_at")
    private Date createdAt;
    @SerializedName("updated_at")
    private Date updatedAt;
    private int private_gists;


    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAvatar_url() {
        return avatar_url;
    }

    public void setAvatar_url(String avatar_url) {
        this.avatar_url = avatar_url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getBlog() {
        return blog;
    }

    public void setBlog(String blog) {
        this.blog = blog;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public int getPublic_gists() {
        return public_gists;
    }

    public void setPublic_gists(int public_gists) {
        this.public_gists = public_gists;
    }

    public int getFollowers() {
        return followers;
    }

    public void setFollowers(int followers) {
        this.followers = followers;
    }

    public int getFollowing() {
        return following;
    }

    public void setFollowing(int following) {
        this.following = following;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public int getPrivate_gists() {
        return private_gists;
    }

    public void setPrivate_gists(int private_gists) {
        this.private_gists = private_gists;
    }
}
