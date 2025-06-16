package com.example.csch.Home;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.csch.R;
import java.util.List;

public class HomeRecyclerViewAdapter extends RecyclerView.Adapter<HomeRecyclerViewAdapter.ViewHolder> {

    private Context context;
    private List<Home> bookList;
    private OnItemClickListener onItemClickListener;

    // Interface để xử lý sự kiện click
    public interface OnItemClickListener {
        void onItemClick(Home book);
    }

    public HomeRecyclerViewAdapter(Context context, List<Home> bookList) {
        this.context = context;
        this.bookList = bookList;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_home, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Home book = bookList.get(position);

        // Gán dữ liệu
        holder.txtTitle.setText(book.getBookTitle());
        holder.txtChap.setText(book.getChapter());

        // Decode Base64 để hiển thị ảnh
        String coverBase64 = book.getCoverImage();
        if (coverBase64 != null && !coverBase64.isEmpty()) {
            holder.imgBookCover.setImageBitmap(base64ToBitmap(coverBase64));
        } else {
            holder.imgBookCover.setImageResource(R.drawable.bia_truyen); // Ảnh mặc định
        }

        // Gọi sự kiện click
        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(book);
            }
        });
    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }

    private Bitmap base64ToBitmap(String base64Str) {
        byte[] decodedBytes = Base64.decode(base64Str, Base64.DEFAULT);
        return android.graphics.BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgBookCover;
        TextView txtTitle, txtChap;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgBookCover = itemView.findViewById(R.id.imgBookCover);
            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtChap = itemView.findViewById(R.id.txtChap);
        }
    }
}
