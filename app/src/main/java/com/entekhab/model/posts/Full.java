package com.entekhab.model.posts;


import com.google.gson.annotations.Expose;


public class Full {

    @Expose
    private String url;
    @Expose
    private int width;
    @Expose
    private int height;

    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }

    public int getWidth() {
        return width;
    }
    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }
    public void setHeight(int height) {
        this.height = height;
    }

}
