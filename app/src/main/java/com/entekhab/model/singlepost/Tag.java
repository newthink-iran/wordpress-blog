package com.entekhab.model.singlepost;

import com.google.gson.annotations.Expose;


public class Tag {

  @Expose
  private int id;
  @Expose
  private String title;

  public int getId() {
    return id;
  }

 public void setId(int id) {
   this.id = id;
 }

 public String getTitle() {
   return title;
 }

 public void setTitle(String title) {
   this.title = title;
 }
}
