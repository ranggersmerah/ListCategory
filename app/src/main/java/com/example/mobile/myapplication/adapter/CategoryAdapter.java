package com.example.mobile.myapplication.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobile.myapplication.R;
import com.example.mobile.myapplication.model.ModelCategory;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.CircleBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> implements Filterable{

    private final List<ModelCategory> OriList;
    private List<ModelCategory> FilterList;

    private int lastPosition = -1;
    private final SparseBooleanArray selectedItem;
    private OnItemClickListener monItemClickListener;
    Context ctx;
    private final ItemFilter mFilter = new ItemFilter();

    DisplayImageOptions options;
    ImageLoader imageLoader = ImageLoader.getInstance();
    private ImageLoadingListener imageListener=null;

    public interface  OnItemClickListener{
        void onItemClick(View view, ModelCategory obj, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener){
        this.monItemClickListener = mItemClickListener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imgCategory;
        TextView Name,Desc;
        LinearLayout lyt_parent;

        ViewHolder(View itemView) {
            super(itemView);
            imgCategory = itemView.findViewById(R.id.img_category);
            Name = itemView.findViewById(R.id.name_category);
            Desc = itemView.findViewById(R.id.description_category);
            lyt_parent = itemView.findViewById(R.id.lyt_category);

        }
    }

    @Override
    public Filter getFilter() {
        return mFilter;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_category, parent, false);
        return new ViewHolder(view);
    }

    public CategoryAdapter(Context context, List<ModelCategory> items) {
        ctx = context;
        OriList = items;
        FilterList = items;
        selectedItem = new SparseBooleanArray();
        PrepareImageLoader();
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final ModelCategory modelCategory = FilterList.get(position);

        imageLoader.displayImage(modelCategory.getStrCategoryThumb(),
                holder.imgCategory, options, imageListener);
        holder.Name.setText(modelCategory.getStrCategory());
        holder.Desc.setText(modelCategory.getStrCategoryDescription());

        holder.lyt_parent.setOnClickListener(view -> {
            if (monItemClickListener !=null){
                Animation fadein = new AlphaAnimation(0,1);
                fadein.setDuration(50);
                holder.lyt_parent.startAnimation(fadein);
                monItemClickListener.onItemClick(view, modelCategory,position);
            }
        });
        holder.lyt_parent.setActivated(selectedItem.get(position,false));

        setAnimation(holder.itemView, position);
    }


    // Animation appear with you scroll down only
    private void setAnimation(View viewToAnimate, int position) {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition) {
            ScaleAnimation anim = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, Animation.INFINITE, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            anim.setDuration(new Random().nextInt(1001));//to make duration random number between [0,501)
            viewToAnimate.startAnimation(anim);
            lastPosition = position;
        }
    }

    @Override
    public int getItemCount() {
        return FilterList.size();
    }

    private void PrepareImageLoader(){
        options = new DisplayImageOptions.Builder()
                .showImageOnFail(R.drawable.ic_perm_media)
                .showStubImage(R.drawable.ic_perm_media)
                .showImageForEmptyUri(R.drawable.ic_perm_media)
                .displayer(new CircleBitmapDisplayer())
                .cacheInMemory(true)
                .cacheOnDisk(true).build();

        imageListener = new ImageDisplayListener();
    }
    private static class ImageDisplayListener extends SimpleImageLoadingListener {

        static final List<String> displayedImages = Collections
                .synchronizedList(new LinkedList<String>());

        @Override
        public void onLoadingComplete(String imageUri, View view,
                                      Bitmap loadedImage) {
            if (loadedImage != null) {
                ImageView imageView = (ImageView) view;
                boolean firstDisplay = !displayedImages.contains(imageUri);
                if (firstDisplay) {
                    FadeInBitmapDisplayer.animate(imageView, 250);
                    displayedImages.add(imageUri);
                }
            }
        }
    }

    private class ItemFilter extends Filter{
        @Override
        protected FilterResults performFiltering(CharSequence constraint){
            String query = constraint.toString().toLowerCase();

            FilterResults results = new FilterResults();
            final List<ModelCategory> list = OriList;
            final List<ModelCategory> result_list = new ArrayList<>(list.size());

            for (int i =0; i < list.size(); i++){
                String str_title = list.get(i).getStrCategory();
                if (str_title.toLowerCase().contains(query)){
                    result_list.add(list.get(i));
                }
            }

            results.values = result_list;
            results.count = result_list.size();

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            FilterList = (List<ModelCategory>) results.values;
            notifyDataSetChanged();
        }

    }

}
