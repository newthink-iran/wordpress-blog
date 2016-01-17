package com.entekhab.model.posts;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class Posts {

    @Expose
    private int id;
    @Expose
    private String url;
    @Expose
    private String title;
    @Expose
    private String type;
    @Expose
    private String date;
    @Expose
    private Author author;
    @SerializedName("comment_count")
    @Expose
    private int commentCount;
    @SerializedName("comment_status")
    @Expose
    private String commentStatus;
    @Expose
    private Object thumbnail;
    @SerializedName("thumbnail_size")
    @Expose
    private String thumbnailSize;
    @SerializedName("thumbnail_images")
    @Expose
    private ThumbnailImages thumbnailImages;

    
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }
    
    public String getCommentStatus() {
        return commentStatus;
    }

    public void setCommentStatus(String commentStatus) {
        this.commentStatus = commentStatus;
    }

    public Object getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(Object thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getThumbnailSize() {
        return thumbnailSize;
    }

    public void setThumbnailSize(String thumbnailSize) {
        this.thumbnailSize = thumbnailSize;
    }

    public ThumbnailImages getThumbnailImages() {
        return thumbnailImages;
    }

    public void setThumbnailImages(ThumbnailImages thumbnailImages) {
        this.thumbnailImages = thumbnailImages;
    }

}
