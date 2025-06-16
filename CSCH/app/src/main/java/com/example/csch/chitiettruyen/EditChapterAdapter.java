package com.example.csch.chitiettruyen;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.widget.ArrayAdapter;

import com.bumptech.glide.Glide;
import com.example.csch.R;

import java.io.File;
import java.util.List;

public class EditChapterAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final List<String> imagePaths;

    public EditChapterAdapter(Context context, List<String> imagePaths) {
        super(context, R.layout.item_comic_image, imagePaths);
        this.context = context;
        this.imagePaths = imagePaths;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Inflate view nếu chưa tồn tại
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_comic_image, parent, false);
        }

        // Lấy ImageView từ layout
        ImageView imageView = convertView.findViewById(R.id.imageViewread);
        String imagePath = imagePaths.get(position);

        // Sử dụng Glide để tải ảnh
        if (imagePath != null) {
            File imgFile = new File(imagePath);

            if (imgFile.exists()) {
                // Đường dẫn là file cục bộ
                Glide.with(context)
                        .load(imgFile)
                        .placeholder(R.drawable.ic_search) // Ảnh tạm khi tải
                        .error(R.drawable.ic_avtar) // Ảnh lỗi nếu không tải được
                        .into(imageView);
            } else if (imagePath.startsWith("http") || imagePath.startsWith("content://")) {
                // Đường dẫn là URL hoặc URI
                Glide.with(context)
                        .load(Uri.parse(imagePath))
                        .placeholder(R.drawable.ic_search)
                        .error(R.drawable.ic_avtar)
                        .into(imageView);
            } else {
                // Nếu không hợp lệ, hiển thị ảnh lỗi
                imageView.setImageResource(R.drawable.ic_avtar);
            }
        } else {
            // Trường hợp đường dẫn null
            imageView.setImageResource(R.drawable.ic_avtar);
        }

        return convertView;
    }
}
