package com.example.csch.chitiettruyen;

import android.content.Context;
import android.database.Cursor;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.csch.R;
import com.example.csch.database.DBHelper;

public class ChapterListAdapter extends RecyclerView.Adapter<ChapterListAdapter.ViewHolder> {
    private final Context context;
    private final Cursor cursor;
    private final OnChapterClickListener listener;

    // Giao diện callback khi click vào item
    public interface OnChapterClickListener {
        void onChapterClick(int chapterNumber);
    }

    public ChapterListAdapter(Context context, Cursor cursor, OnChapterClickListener listener) {
        this.context = context;
        this.cursor = cursor;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_chapter_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (cursor.moveToPosition(position)) {
            // Lấy thông tin chương từ database
            int chapterNumber = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_CHAPTER_NUMBER));
            String chapterTitle = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_CHAPTER_TITLE));

            holder.tvChapterTitle.setText("Chương " + chapterNumber + ": " + chapterTitle);

            // Xử lý sự kiện khi click vào item
            holder.itemView.setOnClickListener(v -> listener.onChapterClick(chapterNumber));
        }
    }

    @Override
    public int getItemCount() {
        return cursor.getCount();
    }

    // ViewHolder chứa các thành phần của item
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvChapterTitle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvChapterTitle = itemView.findViewById(R.id.tvChapterTitle);
        }
    }

    @Override
    public void onViewRecycled(@NonNull ViewHolder holder) {
        super.onViewRecycled(holder);
    }

    public void closeCursor() {
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
    }
}
