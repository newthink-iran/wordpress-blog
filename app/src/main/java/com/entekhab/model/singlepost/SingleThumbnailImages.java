package com.entekhab.model.singlepost;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class SingleThumbnailImages {
	
	@SerializedName("full")
    @Expose
    private SingleFull full;

    public SingleFull getFull() {
        return full;
    }
    public void setFull(SingleFull full) {
        this.full = full;
    }
    
}
