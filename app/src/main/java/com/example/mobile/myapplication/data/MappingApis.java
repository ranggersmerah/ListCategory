package com.example.mobile.myapplication.data;

import com.example.mobile.myapplication.model.ListCategory;
import retrofit2.Call;
import retrofit2.http.GET;

public interface MappingApis {

    @GET("categories.php")
    Call<ListCategory> getCategory();

}
