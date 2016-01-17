package com.entekhab.model.singlepost;

import com.google.gson.annotations.Expose;


public class Comment {

    @Expose
    private int id;
    @Expose
    private String name;
    @Expose
    private String date;
    @Expose
    private String content;
    @Expose
    private int parent;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getParent() {
        return parent;
    }

    public void setParent(int parent) {
        this.parent = parent;
    }
}
