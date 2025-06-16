package com.example.csch.book;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.csch.R;
import com.example.csch.chitiettruyen.DetailAdminActivity;

import java.util.List;

public class BookAdapter extends ArrayAdapter<Book> {
    private Context context;
    private List<Book> books;
    private BookActionListener listener;

    public interface BookActionListener {
        void onEdit(Book book);
        void onDelete(Book book);
    }

    public BookAdapter(Context context, List<Book> books, BookActionListener listener) {
        super(context, R.layout.item_book_manage, books);
        this.context = context;
        this.books = books;
        this.listener = listener;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_book_manage, parent, false);
        }

        Book book = books.get(position);

        ImageView bookImage = convertView.findViewById(R.id.bookImageView); // Correct reference to bookImage
        TextView nameTextView = convertView.findViewById(R.id.txt_nameTruyen);
        TextView genreTextView = convertView.findViewById(R.id.bookGenreTextView);
        TextView chaptersTextView = convertView.findViewById(R.id.bookChapterTextView);
        TextView translatorGroupTextView = convertView.findViewById(R.id.bookTranslatorGroupTextView);

        ImageButton editButton = convertView.findViewById(R.id.btn_edit);
        ImageButton deleteButton = convertView.findViewById(R.id.btn_delete);

        // Set the book cover image
        String avatarBase64 = book.getCover(); // Assuming 'cover' is a field in Book that holds Base64 string
        if (avatarBase64 != null && !avatarBase64.isEmpty()) {
            Bitmap avatarBitmap = base64ToBitmap(avatarBase64);
            bookImage.setImageBitmap(avatarBitmap); // Display the decoded image
        } else {
            bookImage.setImageResource(R.drawable.bia_truyen); // Set default image if Base64 is empty
        }

        // Set data for the book
        nameTextView.setText(book.getName());
        genreTextView.setText(book.getGenre());
        chaptersTextView.setText(String.valueOf(book.getChapters()));
        translatorGroupTextView.setText(book.getTranslatorGroup());

        // Set up edit action
        editButton.setOnClickListener(v -> listener.onEdit(book));

        // Set up delete action with confirmation
        deleteButton.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Xóa sách")
                    .setMessage("Bạn có chắc chắn muốn xóa sách này không?")
                    .setPositiveButton("Xóa", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            listener.onDelete(book); // Proceed with delete action
                            Toast.makeText(context, "Đã xóa sách", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Hủy", null) // Do nothing if "Hủy" is clicked
                    .show();
        });

        // When clicking on the list item, send the book ID to DetailActivity
        convertView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetailAdminActivity.class);
            intent.putExtra("BOOK_ID",String.valueOf( book.getId()));
            context.startActivity(intent);
        });

        return convertView;
    }

    // Helper function to decode Base64 to Bitmap
    private Bitmap base64ToBitmap(String base64Str) {
        byte[] decodedBytes = android.util.Base64.decode(base64Str, android.util.Base64.DEFAULT);
        return android.graphics.BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }
}
