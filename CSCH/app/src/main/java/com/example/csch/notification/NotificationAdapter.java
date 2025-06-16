package com.example.csch.notification;



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

public class NotificationAdapter extends BaseAdapter {
    private Context context;
    private List<NotificationItem> notifications;

    public NotificationAdapter(Context context, List<NotificationItem> notifications) {
        this.context = context;
        this.notifications = notifications;
    }

    @Override
    public int getCount() {
        return notifications.size();
    }

    @Override
    public Object getItem(int position) {
        return notifications.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_notification, parent, false);
        }

        ImageView ivBookCover = convertView.findViewById(R.id.ivBookCover);
        TextView tvBookName = convertView.findViewById(R.id.tvBookName);
        TextView tvNewChapter = convertView.findViewById(R.id.tvNewChapter);

        NotificationItem item = notifications.get(position);

        tvBookName.setText(item.getBookName());
        tvNewChapter.setText("Chap mới: " + item.getNewChapter());

        // Hiển thị ảnh từ Base64
        if (item.getCoverBase64() != null) {
            try {
                byte[] decodedString = Base64.decode(item.getCoverBase64(), Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                ivBookCover.setImageBitmap(bitmap);
            } catch (Exception e) {
                ivBookCover.setImageResource(R.drawable.bia_truyen);
            }
        } else {
            ivBookCover.setImageResource(R.drawable.bia_truyen);
        }

        return convertView;
    }
}
