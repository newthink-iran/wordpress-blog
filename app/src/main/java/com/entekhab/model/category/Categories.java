package com.entekhab.model.category;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;


public class Categories {

    @Expose
    private String status;
    @Expose
    private int count;
    @Expose
    private List<Category> categories = new ArrayList<Category>();

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

}
