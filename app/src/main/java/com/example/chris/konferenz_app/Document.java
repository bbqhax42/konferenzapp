package com.example.chris.konferenz_app;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Chris on 01.06.2017.
 */

public class Document {
    int id;
    private String title;

    public Document() {
    }

    public Document(int id, String title) {
        this.id = id;
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
