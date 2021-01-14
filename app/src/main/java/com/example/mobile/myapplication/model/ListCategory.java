package com.example.mobile.myapplication.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ListCategory {

    @SerializedName("categories")
    @Expose
    private final List<ModelCategory> categories = new ArrayList<>();

    public List<ModelCategory> getCategories() {
        return categories;
    }
}
