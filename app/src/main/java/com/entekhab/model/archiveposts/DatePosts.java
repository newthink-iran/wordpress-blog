
package com.entekhab.model.archiveposts;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;


public class DatePosts {

    @Expose
    private String status;
    @Expose
    private List<String> permalinks = new ArrayList<String>();
    

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<String> getPermalinks() {
        return permalinks;
    }

    public void setPermalinks(List<String> permalinks) {
        this.permalinks = permalinks;
    }
}
