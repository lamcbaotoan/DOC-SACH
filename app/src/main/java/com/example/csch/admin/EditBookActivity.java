package com.example.csch.admin;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.csch.R;
import com.example.csch.database.DBHelper;

import java.io.ByteArrayOutputStream;

public class EditBookActivity extends AppCompatActivity {

    private EditText etBookName, etAuthor, etGenre, etDescription, etTranslationGroup;
    private Spinner spinnerStatus;
    private Button btnUpdateBook, btnDeleteBook, btnSelectCover;
    private ImageView ivBookCover;
    private DBHelper dbHelper;
    private int bookId;
    private String coverImageBase64 = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_book);

        // Ánh xạ các thành phần giao diện
        etBookName = findViewById(R.id.etBookName);
        etAuthor = findViewById(R.id.etAuthor);
        etGenre = findViewById(R.id.etGenre);
        etDescription = findViewById(R.id.etDescription);
        etTranslationGroup = findViewById(R.id.etTranslationGroup);
        spinnerStatus = findViewById(R.id.spinnerStatus);
        ivBookCover = findViewById(R.id.ivBookCover);
        btnUpdateBook = findViewById(R.id.btnUpdateBook);
        btnDeleteBook = findViewById(R.id.btnDeleteBook);
        btnSelectCover = findViewById(R.id.btnSelectCover);

        dbHelper = new DBHelper(this);
        bookId = getIntent().getIntExtra("BOOK_ID", -1);

        loadBookDetails();

        // Cập nhật sách
        btnUpdateBook.setOnClickListener(v -> updateBook());

        // Xóa sách
        btnDeleteBook.setOnClickListener(v -> deleteBook());

        // Chọn ảnh bìa
        btnSelectCover.setOnClickListener(v -> selectCoverImage());
    }

    private void loadBookDetails() {
        Cursor cursor = null;
        try {
            cursor = dbHelper.getReadableDatabase().query(DBHelper.TABLE_BOOKS, null,
                    DBHelper.COLUMN_BOOK_ID + "=?", new String[]{String.valueOf(bookId)},
                    null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                etBookName.setText(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_BOOK_NAME)));
                etAuthor.setText(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_AUTHOR)));
                etGenre.setText(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_GENRE)));
                etDescription.setText(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_DESCRIPTION)));
                etTranslationGroup.setText(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_TRANSLATION_GROUP)));

                String coverBase64 = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_COVER));
                if (coverBase64 != null && !coverBase64.isEmpty()) {
                    byte[] decodedString = Base64.decode(coverBase64, Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    ivBookCover.setImageBitmap(decodedByte);
                    coverImageBase64 = coverBase64;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Lỗi khi tải thông tin sách!", Toast.LENGTH_SHORT).show();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private void updateBook() {
        String bookName = etBookName.getText().toString().trim();
        String author = etAuthor.getText().toString().trim();
        String genre = etGenre.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String translationGroup = etTranslationGroup.getText().toString().trim();
        String status = spinnerStatus.getSelectedItem().toString();

        if (bookName.isEmpty() || author.isEmpty() || genre.isEmpty() || status.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }

        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_BOOK_NAME, bookName);
        values.put(DBHelper.COLUMN_AUTHOR, author);
        values.put(DBHelper.COLUMN_GENRE, genre);
        values.put(DBHelper.COLUMN_DESCRIPTION, description);
        values.put(DBHelper.COLUMN_TRANSLATION_GROUP, translationGroup);
        values.put(DBHelper.COLUMN_STATUS, status);
        values.put(DBHelper.COLUMN_COVER, coverImageBase64);

        int rowsAffected = dbHelper.getWritableDatabase().update(DBHelper.TABLE_BOOKS, values,
                DBHelper.COLUMN_BOOK_ID + "=?", new String[]{String.valueOf(bookId)});

        if (rowsAffected > 0) {
            Toast.makeText(this, "Cập nhật sách thành công!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Cập nhật thất bại!", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteBook() {
        int rowsDeleted = dbHelper.getWritableDatabase().delete(DBHelper.TABLE_BOOKS,
                DBHelper.COLUMN_BOOK_ID + "=?", new String[]{String.valueOf(bookId)});

        if (rowsDeleted > 0) {
            Toast.makeText(this, "Xóa sách thành công!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Xóa sách thất bại!", Toast.LENGTH_SHORT).show();
        }
    }

    private void selectCoverImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
                ivBookCover.setImageBitmap(bitmap);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] byteArray = baos.toByteArray();
                coverImageBase64 = Base64.encodeToString(byteArray, Base64.DEFAULT);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Lỗi khi chọn ảnh!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
