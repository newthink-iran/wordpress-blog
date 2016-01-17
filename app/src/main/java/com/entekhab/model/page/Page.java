package com.entekhab.model.page;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.entekhab.model.singlepost.Attachment;
import com.entekhab.model.singlepost.Comment;
import com.entekhab.model.singlepost.Tag;


public class Page {

    @Expose
    private int id;
    @Expose
    private String content;
    @Expose
    private String title;
    @Expose
    private String comment_status;
    @Expose
    private List<Attachment> attachments = new ArrayList<Attachment>();
    @Expose
    private List<Tag> tags = new ArrayList<Tag>();
    @Expose
    private List<Comment> comments = new ArrayList<Comment>();
    

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
    
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

}
