package com.example.csch.admin;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.csch.R;
import com.example.csch.database.DBHelper;

import java.io.ByteArrayOutputStream;

public class AddBookActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText etBookName, etAuthor, etGenre, etDescription, etTranslationGroup;
    private Spinner spStatus;
    private ImageView ivCover;
    private Button btnSelectCover, btnSaveBook;
    private DBHelper dbHelper;
    private String coverBase64 = ""; // Chuỗi chứa dữ liệu bìa sách

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_book);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.lv_Chapte), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Ánh xạ view
        etBookName = findViewById(R.id.etBookName);
        etAuthor = findViewById(R.id.etAuthor);
        etGenre = findViewById(R.id.etGenre);
        spStatus = findViewById(R.id.spStatus);
        etDescription = findViewById(R.id.etDescription);
        etTranslationGroup = findViewById(R.id.etTranslationGroup);
        ivCover = findViewById(R.id.ivCover);
        btnSelectCover = findViewById(R.id.btnSelectCover);
        btnSaveBook = findViewById(R.id.btnSaveBook);

        dbHelper = new DBHelper(this);

        // Xử lý chọn bìa sách
        btnSelectCover.setOnClickListener(v -> selectCover());

        // Lưu sách vào DB
        btnSaveBook.setOnClickListener(v -> saveBook());
    }

    private void selectCover() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                ivCover.setImageBitmap(bitmap);
                coverBase64 = encodeImageToBase64(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Lỗi khi chọn bìa sách!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String encodeImageToBase64(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);
        byte[] imageBytes = outputStream.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }

    private void saveBook() {
        String bookName = etBookName.getText().toString().trim();
        String author = etAuthor.getText().toString().trim();
        String genre = etGenre.getText().toString().trim();
        String status = spStatus.getSelectedItem().toString().trim();
        String description = etDescription.getText().toString().trim();
        String translationGroup = etTranslationGroup.getText().toString().trim();

        if (bookName.isEmpty() || author.isEmpty() || genre.isEmpty() || description.isEmpty() || coverBase64.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }

        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_BOOK_NAME, bookName);
        values.put(DBHelper.COLUMN_AUTHOR, author);
        values.put(DBHelper.COLUMN_GENRE, genre);
        values.put(DBHelper.COLUMN_STATUS, status);
        values.put(DBHelper.COLUMN_DESCRIPTION, description);
        values.put(DBHelper.COLUMN_TRANSLATION_GROUP, translationGroup);
        values.put(DBHelper.COLUMN_COVER, coverBase64);

        long result = dbHelper.getWritableDatabase().insert(DBHelper.TABLE_BOOKS, null, values);
        if (result != -1) {
            Toast.makeText(this, "Thêm sách thành công!", Toast.LENGTH_SHORT).show();
            finish(); // Kết thúc Activity
        } else {
            Toast.makeText(this, "Lỗi khi thêm sách!", Toast.LENGTH_SHORT).show();
        }
    }
}
