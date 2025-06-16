package com.example.csch.timkiem;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.csch.R;
import com.example.csch.chitiettruyen.DetailActivity;
import com.example.csch.chitiettruyen.DetailAdminActivity;
import com.example.csch.user.User;

import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {

    private final Context context;
    private final List<Search> searchList;

    public SearchAdapter(Context context, List<Search> searchList) {
        this.context = context;
        this.searchList = searchList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_search, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Search search = searchList.get(position);
        holder.nameTruyen.setText(search.getNameTruyen());
        holder.bookChapter.setText(String.valueOf(search.getBookChapter()));
        holder.translatorGroup.setText(search.getTranslatorGroup());
        holder.genre.setText(search.getGenre());
        holder.bookImage.setImageResource(R.drawable.bia_truyen); // Default image


        // Handle item click event
        holder.itemView.setOnClickListener(v -> {
            // Check user role and userId from SharedPreferences
            SharedPreferences preferences = context.getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
            boolean isAdmin = preferences.getBoolean("isAdmin", false);
            int userId = preferences.getInt("userId", -1); // Default -1 if not found

            // Select appropriate Activity based on user role
            Intent intent;
            if (isAdmin) {
                intent = new Intent(context, DetailAdminActivity.class);
            } else {
                intent = new Intent(context, DetailActivity.class);
            }

            // Pass BOOK_ID and USER_ID to the selected activity
            intent.putExtra("BOOK_ID", String.valueOf(search.getBookID()));
            intent.putExtra("USER_ID", String.valueOf(userId));
            context.startActivity(intent);
        });

        // Assuming the cover Base64 data is stored in search.getCover()
        String avatarBase64 = search.getBookImage(); // Replace with the correct field for the Base64 string
        if (avatarBase64 != null && !avatarBase64.isEmpty()) {
            Bitmap avatarBitmap = base64ToBitmap(avatarBase64);
            holder.bookImage.setImageBitmap(avatarBitmap); // Display the decoded image
        }
/*
        // Handle item click event
        holder.itemView.setOnClickListener(v -> {
            // Check user role from SharedPreferences
            SharedPreferences preferences = context.getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
            boolean isAdmin = preferences.getBoolean("isAdmin", false);

            // Select appropriate Activity based on user role
            Intent intent;
            if (isAdmin) {
                intent = new Intent(context, DetailAdminActivity.class); // Admin activity
            } else {
                intent = new Intent(context, DetailActivity.class); // Regular user activity
            }

            // Pass book details to the selected activity
            intent.putExtra("BOOK_ID",String.valueOf(search.getBookID()));
            context.startActivity(intent);
        });
 */
    }

    @Override
    public int getItemCount() {
        return searchList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView bookImage;
        TextView nameTruyen, bookChapter, translatorGroup, genre;

        public ViewHolder(View itemView) {
            super(itemView);
            bookImage = itemView.findViewById(R.id.bookImageView);
            nameTruyen = itemView.findViewById(R.id.name_truyen);
            bookChapter = itemView.findViewById(R.id.bookChapterTextView);
            translatorGroup = itemView.findViewById(R.id.bookTranslatorGroupTextView);
            genre = itemView.findViewById(R.id.bookGenreTextView);
        }
    }

    // Helper function to decode Base64 string to Bitmap
    private Bitmap base64ToBitmap(String base64Str) {
        byte[] decodedBytes = android.util.Base64.decode(base64Str, android.util.Base64.DEFAULT);
        return android.graphics.BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }
}
