package com.example.mobile.myapplication.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ModelCategory implements Serializable {

    @SerializedName("idCategory")
    @Expose
    private final String idCategory;

    @SerializedName("strCategory")
    @Expose
    private final String strCategory;

    @SerializedName("strCategoryThumb")
    @Expose
    private final String strCategoryThumb;

    @SerializedName("strCategoryDescription")
    @Expose
    private final String strCategoryDescription;

    public ModelCategory(String idCategory, String strCategory, String strCategoryThumb,
                         String strCategoryDescription) {
        this.idCategory = idCategory;
        this.strCategory = strCategory;
        this.strCategoryThumb = strCategoryThumb;
        this.strCategoryDescription = strCategoryDescription;
    }

    public String getIdCategory() {
        return idCategory;
    }

    public String getStrCategory() {
        return strCategory;
    }

    public String getStrCategoryThumb() {
        return strCategoryThumb;
    }

    public String getStrCategoryDescription() {
        return strCategoryDescription;
    }
}
