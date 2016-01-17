package com.entekhab.model.singlepost;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class SinglePost {

    @Expose
    private String status;
    @Expose
    private Post post;
    @SerializedName("previous_url")
    @Expose
    private String previousUrl;
    @SerializedName("next_url")
    @Expose
    private String nextUrl;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public String getPreviousUrl() {
        return previousUrl;
    }

    public void setPreviousUrl(String previousUrl) {
        this.previousUrl = previousUrl;
    }

    public String getNextUrl() {
        return nextUrl;
    }

    public void setNextUrl(String nextUrl) {
        this.nextUrl = nextUrl;
    }

}
