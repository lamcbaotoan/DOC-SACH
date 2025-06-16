package com.example.csch.chitiettruyen;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide; // Nếu chọn Glide
import com.example.csch.R;
import com.squareup.picasso.Picasso; // Nếu chọn Picasso

import java.io.File;
import java.util.List;

public class ComicImageAdapter extends RecyclerView.Adapter<ComicImageAdapter.ComicViewHolder> {
    private final Context context;
    private final List<String> imagePaths; // Danh sách đường dẫn ảnh

    public ComicImageAdapter(Context context, List<String> imagePaths) {
        this.context = context;
        this.imagePaths = imagePaths;
    }

    // ViewHolder class
    public static class ComicViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ComicViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageViewread); // ID trong layout item_comic_image.xml
        }
    }

    @NonNull
    @Override
    public ComicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_comic_image, parent, false);
        return new ComicViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ComicViewHolder holder, int position) {
        String imagePath = imagePaths.get(position); // Đường dẫn ảnh


        // Load hình ảnh
        Glide.with(context)
                .load(imagePath)
                .placeholder(R.drawable.ic_search)
                .error(R.drawable.ic_avtar)
                .into(holder.imageView);

        // Đặt chiều rộng bằng chiều rộng màn hình
        int screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        holder.imageView.getLayoutParams().width = screenWidth;

        // Layout lại sau khi điều chỉnh
        holder.imageView.requestLayout();

        // Kiểm tra xem đường dẫn ảnh là URI hay đường dẫn file
        File imgFile = new File(imagePath);

        if (imgFile.exists()) {
            // Nếu đường dẫn là tệp, sử dụng Glide hoặc Picasso để tải ảnh
            Glide.with(context)
                    .load(imgFile) // Hoặc Picasso.get().load(imgFile) nếu dùng Picasso
                    .placeholder(R.drawable.ic_search) // Ảnh hiển thị trong lúc tải
                    .error(R.drawable.ic_avtar) // Ảnh hiển thị nếu lỗi
                    .into(holder.imageView);
        } else if (imagePath.startsWith("http") || imagePath.startsWith("content://")) {
            // Nếu đường dẫn là URL hoặc URI, tải trực tiếp
            Glide.with(context)
                    .load(Uri.parse(imagePath)) // Hoặc Picasso.get().load(Uri.parse(imagePath))
                    .placeholder(R.drawable.ic_search)
                    .error(R.drawable.ic_avtar)
                    .into(holder.imageView);
        } else {
            // Nếu không hợp lệ, hiển thị ảnh lỗi
            holder.imageView.setImageResource(R.drawable.ic_avtar);
        }
    }

    @Override
    public int getItemCount() {
        return imagePaths.size();
    }
}
