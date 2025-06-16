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

public class HistoryAdapter extends BaseAdapter {
    private Context context;
    private List<HistoryItem> historyList;

    public HistoryAdapter(Context context, List<HistoryItem> historyList) {
        this.context = context;
        this.historyList = historyList;
    }

    @Override
    public int getCount() {
        return historyList.size();
    }

    @Override
    public Object getItem(int position) {
        return historyList.get(position);
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

        HistoryItem item = historyList.get(position);

        // Set book title
        bookTitleTextView.setText(item.getName());

        // Set image from Base64
        String imageBase64 = item.getImageBase64();
        if (imageBase64 != null && !imageBase64.isEmpty()) {
            try {
                byte[] decodedString = Base64.decode(imageBase64, Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                bookImageView.setImageBitmap(decodedByte);
            } catch (Exception e) {
                e.printStackTrace();
                bookImageView.setImageResource(R.drawable.bia_truyen);
            }
        } else {
            bookImageView.setImageResource(R.drawable.bia_truyen);
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
