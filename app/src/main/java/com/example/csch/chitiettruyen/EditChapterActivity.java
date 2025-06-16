package com.example.csch.chitiettruyen;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.csch.R;
import com.example.csch.database.DBHelper;

import java.util.ArrayList;

public class EditChapterActivity extends AppCompatActivity {
    private static final int REQUEST_SELECT_IMAGE = 101;

    private TextView tvChap;
    private Button btnCancel, btnSave, btnDeleteChapter, btnAddImage;
    private ListView listViewImages;

    private int bookId;
    private int chapterNumber;
    private ArrayList<String> imageList;
    private EditChapterAdapter imageAdapter;

    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_chapter);

        // Initialize DBHelper
        dbHelper = new DBHelper(this);

        // Initialize UI components
        tvChap = findViewById(R.id.tvChap);
        btnCancel = findViewById(R.id.btnCancel);
        btnSave = findViewById(R.id.btnSave);
        btnDeleteChapter = findViewById(R.id.btnDeleteChapter);
        btnAddImage = findViewById(R.id.btnAddImage);
        listViewImages = findViewById(R.id.listViewImages);

        // Initialize list and adapter
        imageList = new ArrayList<>();
        imageAdapter = new EditChapterAdapter(this, imageList);
        listViewImages.setAdapter(imageAdapter);

        // Get data from Intent
        bookId = getIntent().getIntExtra("BOOK_ID", -1);
        chapterNumber = getIntent().getIntExtra("CHAPTER_NUMBER", 1);

        if (bookId != -1 && chapterNumber > 0) {
            loadChapterContent(bookId, chapterNumber);
        } else {
            Toast.makeText(this, "Dữ liệu truyền vào không hợp lệ!", Toast.LENGTH_SHORT).show();
        }

        tvChap.setOnClickListener(v -> showChapterListDialog());

        // Button listeners
        btnCancel.setOnClickListener(v -> finish());

        btnSave.setOnClickListener(v -> saveChapterContent());

        btnDeleteChapter.setOnClickListener(v -> deleteChapter());

        btnAddImage.setOnClickListener(v -> addImage());

        listViewImages.setOnItemClickListener((parent, view, position, id) -> {
            String selectedImagePath = imageList.get(position);
            showImageOptionsDialog(position, selectedImagePath);
        });
    }

    private void loadChapterContent(int bookId, int chapterNumber) {
        Cursor cursor = dbHelper.getReadableDatabase().query(
                DBHelper.TABLE_BOOK_CONTENT,
                new String[]{DBHelper.COLUMN_CHAPTER_TITLE, DBHelper.COLUMN_CONTENT},
                DBHelper.COLUMN_BOOK_ID + " = ? AND " + DBHelper.COLUMN_CHAPTER_NUMBER + " = ?",
                new String[]{String.valueOf(bookId), String.valueOf(chapterNumber)},
                null, null, null
        );

        if (cursor != null && cursor.moveToFirst()) {
            String chapterTitle = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_CHAPTER_TITLE));
            String contentPaths = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_CONTENT));

            tvChap.setText("Chương " + chapterNumber + ": " + chapterTitle);

            imageList.clear();
            String[] images = contentPaths.split(";");
            for (String path : images) {
                imageList.add(path);
            }
            imageAdapter.notifyDataSetChanged();
            cursor.close();
        } else {
            Toast.makeText(this, "Không tìm thấy chương!", Toast.LENGTH_SHORT).show();
            if (cursor != null) cursor.close();
        }
    }

    private void saveChapterContent() {
        String updatedContent = String.join(";", imageList);

        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_CONTENT, updatedContent);

        int rowsUpdated = dbHelper.getWritableDatabase().update(
                DBHelper.TABLE_BOOK_CONTENT,
                values,
                DBHelper.COLUMN_BOOK_ID + " = ? AND " + DBHelper.COLUMN_CHAPTER_NUMBER + " = ?",
                new String[]{String.valueOf(bookId), String.valueOf(chapterNumber)}
        );

        if (rowsUpdated > 0) {
            Toast.makeText(this, "Lưu thành công!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Lưu thất bại!", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteChapter() {
        int rowsDeleted = dbHelper.getWritableDatabase().delete(
                DBHelper.TABLE_BOOK_CONTENT,
                DBHelper.COLUMN_BOOK_ID + " = ? AND " + DBHelper.COLUMN_CHAPTER_NUMBER + " = ?",
                new String[]{String.valueOf(bookId), String.valueOf(chapterNumber)}
        );

        if (rowsDeleted > 0) {
            Toast.makeText(this, "Xóa chương thành công!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Xóa chương thất bại!", Toast.LENGTH_SHORT).show();
        }
    }

    private void addImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_SELECT_IMAGE);
    }

    private void showImageOptionsDialog(int position, String imagePath) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Tùy chọn ảnh")
                .setItems(new String[]{"Xóa ảnh", "Thay ảnh"}, (dialog, which) -> {
                    switch (which) {
                        case 0: // Delete image
                            imageList.remove(position);
                            imageAdapter.notifyDataSetChanged();
                            break;
                        case 1: // Replace image
                            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(intent, REQUEST_SELECT_IMAGE);
                            break;
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_SELECT_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            if (imageUri != null) {
                String imagePath = getPathFromUri(imageUri);
                if (imagePath != null) {
                    imageList.add(imagePath);
                    imageAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(this, "Không thể lấy đường dẫn ảnh!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private String getPathFromUri(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
            String path = cursor.getString(index);
            cursor.close();
            return path;
        }
        return null;
    }

    private void showChapterListDialog() {
        Cursor chapterCursor = dbHelper.getReadableDatabase().query(
                DBHelper.TABLE_BOOK_CONTENT,
                new String[]{DBHelper.COLUMN_CHAPTER_NUMBER, DBHelper.COLUMN_CHAPTER_TITLE},
                DBHelper.COLUMN_BOOK_ID + " = ?",
                new String[]{String.valueOf(bookId)},
                null, null, DBHelper.COLUMN_CHAPTER_NUMBER
        );

        ArrayList<String> chapterList = new ArrayList<>();
        ArrayList<Integer> chapterNumbers = new ArrayList<>();

        if (chapterCursor != null) {
            while (chapterCursor.moveToNext()) {
                int chapterNumber = chapterCursor.getInt(chapterCursor.getColumnIndexOrThrow(DBHelper.COLUMN_CHAPTER_NUMBER));
                String chapterTitle = chapterCursor.getString(chapterCursor.getColumnIndexOrThrow(DBHelper.COLUMN_CHAPTER_TITLE));
                chapterList.add("Chương " + chapterNumber + ": " + chapterTitle);
                chapterNumbers.add(chapterNumber);
            }
            chapterCursor.close();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Chọn chương")
                .setItems(chapterList.toArray(new String[0]), (dialog, which) -> {
                    chapterNumber = chapterNumbers.get(which);
                    loadChapterContent(bookId, chapterNumber);
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}
