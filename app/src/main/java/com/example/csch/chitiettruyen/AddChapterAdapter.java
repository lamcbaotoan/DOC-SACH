package com.example.csch.chitiettruyen;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.csch.R;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class AddChapterAdapter extends RecyclerView.Adapter<AddChapterAdapter.ImageViewHolder> {
    private List<Uri> imageUris = new ArrayList<>();

    public void setImages(List<Uri> images) {
        this.imageUris = images;
        notifyDataSetChanged();
    }

    public List<Uri> getImages() {
        return imageUris;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image, parent, false);
        return new ImageViewHolder(view);
    }


    private OnImageClickListener listener;

    public void setOnImageClickListener(OnImageClickListener listener) {
        this.listener = listener;
    }

    public interface OnImageClickListener {
        void onImageClick(int position);
    }
    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        Uri uri = imageUris.get(position);
        Glide.with(holder.imageViewadd.getContext())
                .load(uri)
                .placeholder(R.drawable.imgchapadd)
                .into(holder.imageViewadd);

        holder.imageViewadd.setOnClickListener(v -> {
            if (listener != null) {
                listener.onImageClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return imageUris.size();
    }

    static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewadd;

        ImageViewHolder(View itemView) {
            super(itemView);
            imageViewadd = itemView.findViewById(R.id.imageViewadd);
        }
    }




}
