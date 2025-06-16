package com.example.csch.timkiem;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.csch.R;
import com.example.csch.database.DBHelper;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {
    private EditText searchEditText;
    private RecyclerView searchRecyclerView;
    private SearchAdapter searchAdapter;
    private DBHelper dbHelper;
    private List<Search> searchList;
    private ImageView imageViewBack;
    private ImageView imageViewfilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search);



        // Áp dụng padding cho insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.lv_Chapte), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        imageViewfilter = findViewById(R.id.imageViewfilter);
        imageViewfilter.setOnClickListener(v -> {
            Intent intent = new Intent(this, FilterActivity.class);
            startActivityForResult(intent, 100);
        });


        imageViewBack = findViewById(R.id.imageViewBack);
        imageViewBack.setOnClickListener(v -> onBackPressed());

        imageViewfilter = findViewById(R.id.imageViewfilter); // Đưa vào onCreate
        imageViewfilter.setOnClickListener(v -> {
            Intent intent = new Intent(this, FilterActivity.class);
            startActivityForResult(intent, 100);
        });

        searchEditText = findViewById(R.id.searchEditText);
        searchRecyclerView = findViewById(R.id.searchRecyclerView);
        searchRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        dbHelper = new DBHelper(this);
        searchList = new ArrayList<>();

        searchAdapter = new SearchAdapter(this, searchList);
        searchRecyclerView.setAdapter(searchAdapter);

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Không cần xử lý trước khi nội dung thay đổi
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Gọi hàm tìm kiếm mỗi khi nội dung thay đổi
                String query = s.toString().trim();
                if (!TextUtils.isEmpty(query)) {
                    searchBooks(query);
                } else {
                    searchList.clear(); // Xóa kết quả khi không nhập gì
                    searchAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Không cần xử lý sau khi nội dung thay đổi
            }
        });

    }


    private void searchBooks(String query) {
        searchList.clear();
        Cursor cursor = dbHelper.getReadableDatabase().rawQuery(
                "SELECT * FROM books WHERE book_name LIKE ? OR translation_group LIKE ?",
                new String[]{"%" + query + "%", "%" + query + "%"});

        if (cursor != null && cursor.moveToFirst()) {
            do {
                String image = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_COVER));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_BOOK_NAME));
                int chapter = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_CHAPTERS));
                String group = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_TRANSLATION_GROUP));
                String genre = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_GENRE));
                int bookID = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_BOOK_ID));
                searchList.add(new Search(image, name, chapter, group, genre, bookID));
            } while (cursor.moveToNext());
            cursor.close();
        }

        searchAdapter.notifyDataSetChanged();

        if (searchList.isEmpty()) {
            Toast.makeText(this, "Không tìm thấy kết quả", Toast.LENGTH_SHORT).show();
        }
    }






    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK) {
            ArrayList<String> selectedGenres = data.getStringArrayListExtra("selectedGenres");
            if (selectedGenres != null) {
                filterBooksByGenres(selectedGenres);
            }
        }
    }

    private void filterBooksByGenres(List<String> genres) {
        searchList.clear();
        String query = "SELECT * FROM books WHERE genre IN (" + TextUtils.join(",", genres) + ")";
        Cursor cursor = dbHelper.getReadableDatabase().rawQuery(query, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                // Lấy thông tin và thêm vào searchList
            } while (cursor.moveToNext());
            cursor.close();
        }

        searchAdapter.notifyDataSetChanged();
        if (searchList.isEmpty()) {
            Toast.makeText(this, "Không tìm thấy kết quả", Toast.LENGTH_SHORT).show();
        }
    }

}
