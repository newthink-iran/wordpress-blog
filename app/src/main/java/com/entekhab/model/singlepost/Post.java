package com.entekhab.model.singlepost;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class Post {

    @Expose
    private int id;
    @Expose
    private String content;
    @Expose
    private String date;
    @Expose
    private String comment_status;
    @Expose
    private List<Attachment> attachments = new ArrayList<Attachment>();
    @Expose
    private List<Tag> tags = new ArrayList<Tag>();
    @Expose
    private List<Comment> comments = new ArrayList<Comment>();
    @SerializedName("categories")
    @Expose
    private List<PostCategory> postCategories = new ArrayList<PostCategory>();
    @SerializedName("thumbnail_images")
    @Expose
    private SingleThumbnailImages thumbnailImages;
    

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    
    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }
    
    public String getCommentStatus() {
        return comment_status;
    }
    public void setCommentStatus(String comment_status) {
        this.comment_status = comment_status;
    }
    
    public List<Attachment> getAttachments() {
        return attachments;
    }
    public void setAttachments(List<Attachment> attachments) {
        this.attachments = attachments;
    }
    
    public List<Comment> getComments() {
        return comments;
    }
    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }
    
    public List<Tag> getTags() {
        return tags;
    }
    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }
    
    public List<PostCategory> getCategories() {
        return postCategories;
    }
    public void setCategories(List<PostCategory> postCategories) {
        this.postCategories = postCategories;
    }
    
    public SingleThumbnailImages getThumbnailImages() {
        return thumbnailImages;
    }
    public void setThumbnailImages(SingleThumbnailImages thumbnailImages) {
        this.thumbnailImages = thumbnailImages;
    }
}
