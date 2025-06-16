package com.example.csch.followvahistory;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.csch.R;

import java.util.List;

public class FollowAdapter extends BaseAdapter {
    private Context context;
    private List<FollowItem> followList;

    public FollowAdapter(Context context, List<FollowItem> followList) {
        this.context = context;
        this.followList = followList;
    }

    @Override
    public int getCount() {
        return followList.size();
    }

    @Override
    public Object getItem(int position) {
        return followList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_follow_history, parent, false);
        }

        ImageView bookImageView = convertView.findViewById(R.id.bookImageView);
        TextView bookTitleTextView = convertView.findViewById(R.id.BooknameTextView);
        TextView bookChapterTextView = convertView.findViewById(R.id.bookChapterTextView);
        TextView bookTranslatorGroupTextView = convertView.findViewById(R.id.bookTranslatorGroupTextView);
        TextView bookGenreTextView = convertView.findViewById(R.id.bookGenreTextView);

        FollowItem item = followList.get(position);

        // Set book title
        if (item.getName() != null && !item.getName().isEmpty()) {
            bookTitleTextView.setText(item.getName());
        } else {
            bookTitleTextView.setText("Tên không xác định"); // Hiển thị văn bản mặc định nếu tên bị thiếu
        }


        // Set image from Base64 with error handling
        String imageBase64 = item.getImageBase64();
        if (imageBase64 != null && !imageBase64.isEmpty()) {
            try {
                byte[] decodedString = Base64.decode(imageBase64, Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                bookImageView.setImageBitmap(decodedByte);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                bookImageView.setImageResource(R.drawable.bia_truyen); // Sử dụng hình ảnh mặc định
            }
        } else {
            bookImageView.setImageResource(R.drawable.bia_truyen); // Sử dụng hình ảnh mặc định
        }

        // Set chapter text
        bookChapterTextView.setText("Chapter " + item.getLatestChapter() + " | Đọc tiếp " + item.getReadChapter());

        // Set translator group
        bookTranslatorGroupTextView.setText(item.getTranslatorGroup());

        // Set genre
        bookGenreTextView.setText(String.join(" | ", item.getGenres()));

        return convertView;
    }

}
