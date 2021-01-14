package com.example.mobile.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mobile.myapplication.adapter.CategoryAdapter;
import com.example.mobile.myapplication.data.MappingApis;
import com.example.mobile.myapplication.data.NetworkClient;
import com.example.mobile.myapplication.model.ListCategory;
import com.example.mobile.myapplication.model.ModelCategory;
import com.example.mobile.myapplication.utils.AuthImageDownloader;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.example.mobile.myapplication.utils.utils.ErrorMessage;

public class MainActivity extends AppCompatActivity {

    View lytNotFound,lytSomethingWrong;
    TextView tvNotFound,tvSomethingWrong;
    RecyclerView recyclerView;
    ProgressBar pgLoading;
    public List<ModelCategory> listCategory = new ArrayList<>();
    CategoryAdapter mCategory;
    Retrofit retrofit;
    MappingApis mappingApis;
    EditText edSearch;
    boolean isSearch = true;
    ImageView Search;
    TextView Title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PrepareImageLoader();

        retrofit = NetworkClient.getRetrofitClient();
        mappingApis = retrofit.create(MappingApis.class);

        InitView();

        getListCategory();

    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void InitView(){

        Title = findViewById(R.id.title_src_list);
        Search = findViewById(R.id.img_src_list);
        Search.setOnClickListener(view -> {
            if (isSearch){
                Title.setVisibility(View.GONE);
                Search.setImageDrawable(getDrawable(R.drawable.ic_close));
                edSearch.setVisibility(View.VISIBLE);
                isSearch = false;
            }else {
                Title.setVisibility(View.VISIBLE);
                Search.setImageDrawable(getDrawable(R.drawable.ic_search));
                edSearch.setVisibility(View.GONE);
                isSearch = true;
            }
        });

        edSearch = findViewById(R.id.ed_search_list);
        edSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (listCategory!=null&&listCategory.size()!=0) {
                    mCategory.getFilter().filter(charSequence);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        lytNotFound = findViewById(R.id.lyt_not_found);
        tvNotFound = findViewById(R.id.tv_no_data);

        lytSomethingWrong = findViewById(R.id.lyt_something_wrong);
        tvSomethingWrong = findViewById(R.id.tv_something_wrong);

        recyclerView = findViewById(R.id.rc_list);

        pgLoading = findViewById(R.id.pg_list);
    }

    private void Clear(){
        listCategory.clear();
        if (mCategory!=null){
            mCategory.notifyDataSetChanged();
        }
    }

    private void getListCategory(){
        pgLoading.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        lytNotFound.setVisibility(View.GONE);
        lytSomethingWrong.setVisibility(View.GONE);
        Clear();
        Call<ListCategory> call = mappingApis.getCategory();
        call.enqueue(new Callback<ListCategory>() {
            @Override
            public void onResponse(@NonNull Call<ListCategory> call, @NonNull Response<ListCategory> response) {
                if (response.isSuccessful()){
                    assert response.body() != null;
                    listCategory = response.body().getCategories();
                    if (listCategory!=null){
                        if (listCategory.size()!=0) {
                            recyclerView.setVisibility(View.VISIBLE);
                            lytNotFound.setVisibility(View.GONE);
                            final LinearLayoutManager layoutManager = new LinearLayoutManager(MainActivity.this);
                            recyclerView.setLayoutManager(layoutManager);

                            mCategory = new CategoryAdapter(MainActivity.this, listCategory);
                            mCategory.setOnItemClickListener((view, obj, position) -> {
                                Dialog(obj.getStrCategory(),obj.getStrCategoryDescription());
                            });
                            recyclerView.setAdapter(mCategory);
                            if (mCategory!=null){
                                mCategory.notifyDataSetChanged();
                            }
                        }else {
                            recyclerView.setVisibility(View.GONE);
                            lytNotFound.setVisibility(View.VISIBLE);
                            tvNotFound.setText(R.string.no_data_to_show);
                        }
                    }else {
                        recyclerView.setVisibility(View.GONE);
                        lytNotFound.setVisibility(View.VISIBLE);
                        tvNotFound.setText(R.string.no_data_to_show);
                    }
                    lytSomethingWrong.setVisibility(View.GONE);
                }else {
                    recyclerView.setVisibility(View.GONE);
                    lytNotFound.setVisibility(View.GONE);
                    lytSomethingWrong.setVisibility(View.VISIBLE);
                    tvSomethingWrong.setText(ErrorMessage(response.raw().code(),"GET"));
                }
                pgLoading.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(@NonNull Call<ListCategory> call, @NonNull Throwable t) {
                recyclerView.setVisibility(View.GONE);
                lytNotFound.setVisibility(View.GONE);
                lytSomethingWrong.setVisibility(View.VISIBLE);
                tvSomethingWrong.setText(t.getMessage());
                pgLoading.setVisibility(View.GONE);
            }
        });

    }

    private void PrepareImageLoader()
    {
        initImageLoader(this);
    }

    public static void initImageLoader(Context context) {
        @SuppressWarnings("deprecation")
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.mipmap.ic_launcher)
                //.showImageOnFail(R.drawable.ic_imgerrorload).resetViewBeforeLoading()
                //.cacheOnDisc().imageScaleType(ImageScaleType.EXACTLY)
                .cacheOnDisc(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .displayer(new FadeInBitmapDisplayer(300)).build();

        @SuppressWarnings("deprecation")
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                context).threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .discCacheFileNameGenerator(new Md5FileNameGenerator())
                .defaultDisplayImageOptions(options)
                .imageDownloader(new AuthImageDownloader(context, 30000, 30000))
                .tasksProcessingOrder(QueueProcessingType.LIFO).build();

        ImageLoader.getInstance().init(config);
    }

    public void Dialog(String Title, String Message){
        new AlertDialog.Builder(MainActivity.this)
                .setTitle(Title)
                .setMessage(Message)
                .setPositiveButton("Ok", (dialog, which) -> {
                    dialog.dismiss();
                })
                .show();
    }

}