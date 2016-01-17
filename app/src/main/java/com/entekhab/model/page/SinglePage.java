package com.entekhab.model.page;

import com.google.gson.annotations.Expose;


public class SinglePage {

    @Expose
    private String status;
    @Expose
    private Page page;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Page getPage() {
        return page;
    }

    public void setPage(Page page) {
        this.page = page;
    }

}
