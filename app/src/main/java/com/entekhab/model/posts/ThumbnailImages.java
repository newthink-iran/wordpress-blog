package com.entekhab.model.posts;

import com.google.gson.annotations.Expose;


public class ThumbnailImages {
	
    @Expose
    private Full full;
    
    public Full getFull() {
        return full;
    }
    public void setFull(Full full) {
        this.full = full;
    }
    
}
