package com.example.csch.admin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.csch.Home.Home;
import com.example.csch.R;
import com.example.csch.book.Book;
import com.example.csch.book.BookAdapter;
import com.example.csch.chitiettruyen.DetailAdminActivity;
import com.example.csch.database.DBHelper;
import com.example.csch.timkiem.SearchActivity;

import java.util.ArrayList;

public class ManageBookActivity extends AppCompatActivity {
    private ListView lvBooks;
    private DBHelper dbHelper;
    private EditText etSearch;
    private Button btnAddBook;
    private ArrayList<Book> bookList;
    private BookAdapter bookAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_book);

        lvBooks = findViewById(R.id.lv_book);
        etSearch = findViewById(R.id.etSearch_manage_book);
        btnAddBook = findViewById(R.id.btn_add_book);

        dbHelper = new DBHelper(this);

        // Load books
        loadBooks("");

        btnAddBook.setOnClickListener(v -> {
            Intent intent = new Intent(ManageBookActivity.this, AddBookActivity.class);
            startActivity(intent);
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            private Handler handler = new Handler();
            private Runnable searchRunnable;

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                handler.removeCallbacks(searchRunnable);
                searchRunnable = () -> loadBooks(s.toString());
                handler.postDelayed(searchRunnable, 500); // Trễ 500ms
            }

            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadBooks(etSearch.getText().toString().trim());
        Toast.makeText(this, "Danh sách truyện đã được cập nhật.", Toast.LENGTH_SHORT).show();
    }

    private void loadBooks(String query) {
        bookList = new ArrayList<>();

        String sqlQuery = "SELECT * FROM " + DBHelper.TABLE_BOOKS;
        String[] selectionArgs = null;

        if (!query.isEmpty()) {
            sqlQuery += " WHERE " + DBHelper.COLUMN_BOOK_NAME + " LIKE ?";
            selectionArgs = new String[]{"%" + query + "%"};
        }

        Cursor cursor = dbHelper.getReadableDatabase().rawQuery(sqlQuery, selectionArgs);
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_BOOK_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_BOOK_NAME));
                String genre = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_GENRE));
                int chapters = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_CHAPTERS));
                String translatorGroup = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_TRANSLATION_GROUP));
                String cover = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_COVER));
                if (cover == null || cover.isEmpty()) {
                    cover = "default_cover"; // Đặt giá trị mặc định nếu `cover` rỗng hoặc null
                }


                bookList.add(new Book(id, name, genre, chapters, translatorGroup, cover));
            } while (cursor.moveToNext());
        }
        cursor.close();

        bookAdapter = new BookAdapter(this, bookList, new BookAdapter.BookActionListener() {
            @Override
            public void onEdit(Book book) {
                Intent intent = new Intent(ManageBookActivity.this, EditBookActivity.class);
                intent.putExtra("BOOK_ID", book.getId());
                startActivity(intent);
            }

            @Override
            public void onDelete(Book book) {
                deleteBook(book.getId());
            }
        });
        lvBooks.setAdapter(bookAdapter);
    }

    private void deleteBook(int bookId) {
        int rows = dbHelper.getWritableDatabase().delete(DBHelper.TABLE_BOOKS, DBHelper.COLUMN_BOOK_ID + " = ?", new String[]{String.valueOf(bookId)});
        if (rows > 0) {
            Toast.makeText(this, "Sách đã bị xóa.", Toast.LENGTH_SHORT).show();
            loadBooks(etSearch.getText().toString());
        }
    }


}
