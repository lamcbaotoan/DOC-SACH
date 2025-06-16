package com.example.csch.Home;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.csch.R;
import java.util.List;

public class HomeAdapter extends BaseAdapter {

    private Context context;
    private List<Home> bookList;

    public HomeAdapter(Context context, List<Home> bookList) {
        this.context = context;
        this.bookList = bookList;
    }

    @Override
    public int getCount() {
        return bookList.size();
    }

    @Override
    public Object getItem(int position) {
        return bookList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_home_user, parent, false);
        }

        Home book = bookList.get(position);

        // Ánh xạ UI
        ImageView imgBookCover = view.findViewById(R.id.imgBookCover);
        TextView tvBookTitle = view.findViewById(R.id.tvBookTitle);
        TextView tvChapter = view.findViewById(R.id.tvChapter);

        // Gán dữ liệu
        tvBookTitle.setText(book.getBookTitle());
        tvChapter.setText(book.getChapter());

        // Decode Base64 và hiển thị ảnh
        String coverBase64 = book.getCoverImage();
        if (coverBase64 != null && !coverBase64.isEmpty()) {
            imgBookCover.setImageBitmap(base64ToBitmap(coverBase64));
        } else {
            imgBookCover.setImageResource(R.drawable.bia_truyen); // Ảnh mặc định
        }

        return view;
    }

    private Bitmap base64ToBitmap(String base64Str) {
        byte[] decodedBytes = Base64.decode(base64Str, Base64.DEFAULT);
        return android.graphics.BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }
}
