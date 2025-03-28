package com.jldubz.gistaviewer.viewmodel;

import android.view.View;

import com.jldubz.gistaviewer.model.Constants;
import com.jldubz.gistaviewer.model.NetworkUtil;
import com.jldubz.gistaviewer.model.data.BasicAuthInterceptor;
import com.jldubz.gistaviewer.model.data.IGitHubService;
import com.jldubz.gistaviewer.model.gists.Gist;
import com.jldubz.gistaviewer.model.gists.GistComment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * ViewModel that handles business logic for a GistActivity
 *
 * @author Jon-Luke West
 * @see com.jldubz.gistaviewer.ui.gists.GistActivity
 */
public class GistViewModel extends ViewModel {

    private MutableLiveData<Gist> mGist;
    private MutableLiveData<List<GistComment>> mComments = new MutableLiveData<>();
    private MutableLiveData<Integer> mProgressBarVisibility = new MutableLiveData<>();
    private MutableLiveData<Integer> mCommentsProgressBarVisibility = new MutableLiveData<>();
    private MutableLiveData<String> mErrorMessage = new MutableLiveData<>();
    private MutableLiveData<Boolean> mIsGistStarred = new MutableLiveData<>();
    private IGitHubService mGitHubService;

    private String mUsername;
    private String mToken;
    private String mGistId;
    private int mGistCommentPrevPage;

    public GistViewModel() {
        super();
        init();
        initAnonService();
    }

    /***
     * Clean and initialize the state of the view and all related counters and flags
     */
    private void init() {

        mProgressBarVisibility.setValue(View.GONE);
        mCommentsProgressBarVisibility.setValue(View.GONE);
        mErrorMessage.setValue(null);
        mIsGistStarred.setValue(false);
        mComments = new MutableLiveData<>();
        mGist = null;
        mUsername = null;
        mToken = null;
    }

    /***
     * Configure a new Retrofit instance for future API calls with no authorization
     */
    private void initAnonService() {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.URL_GITHUB).addConverterFactory(GsonConverterFactory.create()).build();

        mGitHubService = retrofit.create(IGitHubService.class);
    }

    /**
     * Set the credentials to use for authorization when communicating with the GitHub API for
     * this Gist.  The Retrofit service instance will be rebuild using these credentials.
     *
     * @param username the GitHub username used for authorization
     * @param token    the private access token associated with the GitHub user
     */
    public void setCredentials(String username, String token) {

        //Check if the provided credentials are empty
        if (username.isEmpty() || token.isEmpty()) {
            return;
        }

        //Store the username and token for later
        mUsername = username;
        mToken = token;

        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(new BasicAuthInterceptor(mUsername, mToken)).build();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.URL_GITHUB).addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        mGitHubService = retrofit.create(IGitHubService.class);

    }

    public LiveData<Integer> getProgressBarVisibility() {
        return mProgressBarVisibility;
    }

    public LiveData<String> getErrorMessage() {
        return mErrorMessage;
    }

    //region Gist

    /**
     * Set the ID of the Gist to use
     *
     * @param mGistId the ID of the Gist
     */
    public void setGistId(String mGistId) {
        this.mGistId = mGistId;
    }

    /**
     * Get an observable instance of the Gist.  This will also load the Gist from the API
     * if one has not been loaded yet.
     *
     * @return an observable Gist
     * @see LiveData
     */
    public LiveData<Gist> getGist() {
        if (mGist == null) {
            mGist = new MutableLiveData<>();
            loadGist();
        }

        return mGist;
    }

    /**
     * Download a Gist from the GitHub API
     */
    private void loadGist() {

        //Make sure that a Gist ID was stored for use
        if (mGistId.isEmpty()) {
            showError(Constants.INVALID_GIST_ID_ERROR);
            return;
        }

        //Show the progress bar
        mProgressBarVisibility.postValue(View.VISIBLE);

        mGitHubService.getGistById(mGistId).enqueue(new Callback<Gist>() {
            @Override
            public void onResponse(Call<Gist> call, Response<Gist> response) {
                mProgressBarVisibility.postValue(View.GONE);

                if (!response.isSuccessful()) {
                    showError(NetworkUtil.onGitHubResponseError(response));
                    return;
                }

                mGist.postValue(response.body());
                loadCommentPageCount();
                getGistStar();
            }

            @Override
            public void onFailure(Call<Gist> call, Throwable t) {
                showError(t.getLocalizedMessage());
            }
        });


    }

    //endregion

    //region Star

    public LiveData<Boolean> getStarredState() {
        return mIsGistStarred;
    }

    /**
     * Called to indicate that the user has clicked the star on the Gist in an attempt to either
     * add or remove it from their list of starred Gists.
     */
    public void starItemClicked() {
        //Make sure that the state of the star has been initialized
        if (mIsGistStarred == null) {
            showError("There was a problem reading the current state of this Gist's star.  Please try again.");
            return;
        }
        if (mIsGistStarred.getValue() == null) {
            mIsGistStarred.postValue(false);
        }

        //Perform the appropriate action based on the state of the star
        if (mIsGistStarred.getValue()) {
            unStarGist();
        } else {
            starGist();
        }
    }

    /**
     * Get the current state of the Gist's star from the GitHub API
     */
    private void getGistStar() {

        //Make sure there is a Gist ID stored for use
        if (mGistId.isEmpty()) {
            return;
        }

        mGitHubService.getStarGistById(mGistId).enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, Response<Response> response) {
                if (response.code() == 404) {
                    mIsGistStarred.postValue(false);
                    return;
                }
                else if (response.code() == 204) {
                    mIsGistStarred.postValue(true);
                    return;
                }

                showError(NetworkUtil.onGitHubResponseError(response));
            }

            @Override
            public void onFailure(Call<Response> call, Throwable t) {
                showError(t.getLocalizedMessage());
            }
        });
    }

    /**
     * Add a star to the Gist so that it shows up in the authorized user's list
     * of starred Gists.
     */
    private void starGist() {

        //Make sure there is a Gist ID stored for use
        if (mGistId.isEmpty()) {
            return;
        }

        mGitHubService.starGistById(mGistId).enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, Response<Response> response) {
                if (response.code() == 204) {
                    mIsGistStarred.postValue(true);
                    return;
                }

                showError(NetworkUtil.onGitHubResponseError(response));

            }

            @Override
            public void onFailure(Call<Response> call, Throwable t) {
                showError(t.getLocalizedMessage());
            }
        });


    }

    /**
     * Remove a star from the Gist so that it no longer shows up in the authorized user's list
     * of starred Gists.
     */
    private void unStarGist() {

        //Make sure there is a Gist ID stored for use
        if (mGistId.isEmpty()) {
            return;
        }

        mGitHubService.unstarGistById(mGistId).enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, Response<Response> response) {
                if (response.code() == 204) {
                    mIsGistStarred.postValue(false);
                    return;
                }

                showError(NetworkUtil.onGitHubResponseError(response));

            }

            @Override
            public void onFailure(Call<Response> call, Throwable t) {
                showError(t.getLocalizedMessage());
            }
        });


    }

    //endregion

    //region Comments

    public LiveData<List<GistComment>> getComments() {
        return mComments;
    }

    /**
     * Download comments for the gist from the GitHub API
     */
    public void loadMoreComments() {

        //Make sure there is a Gist ID stored for use
        if (mGistId.isEmpty()) {
            return;
        }

        //Make sure that there are more comments to load (last -> first)
        if (mGistCommentPrevPage == 0) {
            return;
        }

        //Show the progress bar in the comments section
        mCommentsProgressBarVisibility.postValue(View.VISIBLE);

        mGitHubService.getGistCommentsById(mGistId, mGistCommentPrevPage).enqueue(new Callback<List<GistComment>>() {
            @Override
            public void onResponse(Call<List<GistComment>> call, Response<List<GistComment>> response) {
                mCommentsProgressBarVisibility.postValue(View.GONE);
                if (!response.isSuccessful()) {
                    showError(NetworkUtil.onGitHubResponseError(response));
                    return;
                }

                mGistCommentPrevPage++;
                List<GistComment> currentList = mComments.getValue();
                if (currentList == null) {
                    currentList = new ArrayList<>();
                }

                if (response.body() != null) {
                    List<GistComment> comments = new ArrayList<>(response.body());
                    Collections.reverse(comments);
                    currentList.addAll(comments);
                }
                mComments.postValue(currentList);
            }

            @Override
            public void onFailure(Call<List<GistComment>> call, Throwable t) {
                showError(t.getLocalizedMessage());
            }
        });
    }

    /**
     * Determine if there are more pages that can be loaded for comments
     *
     * @return TRUE if more pages can be loaded, FALSE if not
     */
    public boolean isMoreCommentsAvailable() {
        return mGistCommentPrevPage != 0;
    }

    /**
     * Add a comment to the Gist as the authorized user.  The API will error if there is no
     * authorized user
     *
     * @param comment the comment to post
     */
    public void createComment(String comment) {

        //Make sure there is a Gist ID stored for use
        if (mGistId.isEmpty()) {
            return;
        }

        //Make sure the submitted comment isn't empty
        if (comment.trim().isEmpty()) {
            showError("You cannot create a blank comment");
            return;
        }

        GistComment newComment = new GistComment();
        newComment.setBody(comment);

        mGitHubService.createCommentOnGist(mGistId, newComment).enqueue(new Callback<GistComment>() {
            @Override
            public void onResponse(Call<GistComment> call, Response<GistComment> response) {
                if (!response.isSuccessful()) {
                    showError(NetworkUtil.onGitHubResponseError(response));
                    return;
                }

                List<GistComment> currentList = mComments.getValue();
                if (currentList == null) {
                    currentList = new ArrayList<>();
                }
                List<GistComment> comments = new ArrayList<>();
                comments.add(0, response.body());
                mComments.postValue(comments);

            }

            @Override
            public void onFailure(Call<GistComment> call, Throwable t) {
                showError(t.getLocalizedMessage());
            }
        });


    }

    /**
     * Download the headers for the comment API call to determine how many pages there are to load.
     * Comments cannot be loaded until this is executed first.
     */
    private void loadCommentPageCount() {
        //Make sure there is a Gist ID stored for use
        if (mGistId.isEmpty()) {
            return;
        }

        //Show the progress bar in the comments section
        mCommentsProgressBarVisibility.postValue(View.VISIBLE);

        mGitHubService.getGistCommentsHeaderById(mGistId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (!response.isSuccessful()) {
                    showError(NetworkUtil.onGitHubResponseError(response));
                    return;
                }

                String linkedHeader = response.headers().get("Link");
                if (linkedHeader != null) {
                    mGistCommentPrevPage = getLastPageNum(linkedHeader);
                } else {
                    mGistCommentPrevPage = 0;
                }
                loadMoreComments();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });

    }

    //endregion

    /**
     * Check a link header returned in a call to the GitHub API to see if there is a URL pointing to the last page of content
     * and then get the number of pages from it
     *
     * @param linkHeader the Link header returned by the call to the GitHub API
     * @return the number of pages in the list of data
     */
    private int getLastPageNum(String linkHeader) {
        //check to see if there is a "last" page
        int lastLinkIndex = linkHeader.indexOf("; rel=\"last\"");
        if (lastLinkIndex >= 0) {
            //grab the page number for the "last" link
            int lastPageNumberIndex = linkHeader.lastIndexOf("page=");
            String lastPageNum = linkHeader.substring(lastPageNumberIndex + 5, linkHeader.indexOf(">", lastPageNumberIndex));
            try {
                return Integer.parseInt(lastPageNum);
            } catch (NumberFormatException exception) {
                showError("Couldn't load comments.  Please try again.");
                return 0;
            }

        } else {
            return 0;
        }
    }

    /**
     * Convenience method for showing an error to the user
     *
     * @param message the message to show to the user
     */
    private void showError(String message) {
        mProgressBarVisibility.postValue(View.GONE);
        mCommentsProgressBarVisibility.postValue(View.GONE);
        mErrorMessage.postValue(message);
    }
}
