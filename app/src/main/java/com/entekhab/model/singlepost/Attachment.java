
package com.entekhab.model.singlepost;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class Attachment {

    @Expose
    private int id;
    @Expose
    private String url;
    @Expose
    private String title;
    @Expose
    private String description;
    @Expose
    private String caption;
    @Expose
    private int parent;
    @SerializedName("mime_type")
    @Expose
    private String mimeType;
    @Expose
    private Images images;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public int getParent() {
        return parent;
    }

    public void setParent(int parent) {
        this.parent = parent;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public Images getImages() {
        return images;
    }

    public void setImages(Images images) {
        this.images = images;
    }

}
