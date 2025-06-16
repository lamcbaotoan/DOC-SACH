package com.example.csch.chitiettruyen;

import static com.example.csch.database.DBHelper.COLUMN_BOOK_ID;
import static com.example.csch.database.DBHelper.COLUMN_READ_CHAPTERS;
import static com.example.csch.database.DBHelper.COLUMN_USER_ID;
import static com.example.csch.database.DBHelper.TABLE_READING_HISTORY;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Base64;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.csch.R;
import com.example.csch.database.DBHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ReadActivity extends AppCompatActivity {
    private int bookId;
    private int chapterNumber;
    private TextView tvChapterTitle;
    private RecyclerView lvComicImages;
    private ImageView imvPrev, imvNext, imvListchap, imvBack;
    private LinearLayout linearLayoutHeader;
    private View btnNavigation; // Các nút điều hướng khác


    private DBHelper dbHelper;
    private ComicImageAdapter adapter;
    private List<String> imageList; // Danh sách ảnh đã giải mã từ Base64
    private PopupWindow chapterPopup; // Đảm bảo khai báo một instance duy nhất

    private GestureDetector gestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read);  // Chỉ gọi setContentView một lần duy nhất

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        dbHelper = new DBHelper(this);
        updateLastReadChapter("1", String.valueOf(bookId), chapterNumber);


        // Initialize views
        tvChapterTitle = findViewById(R.id.tvChapterTitle);
        lvComicImages = findViewById(R.id.lvComicImages);
        imvPrev = findViewById(R.id.imvPrev);
        imvNext = findViewById(R.id.imvNext);
        imvListchap = findViewById(R.id.imvListchap);
        imvBack = findViewById(R.id.imvBack);  // Initialize the missing ImageView
        linearLayoutHeader = findViewById(R.id.linearLayoutHeader);
        btnNavigation = findViewById(R.id.btnNavigation);

        dbHelper = new DBHelper(this);
        imageList = new ArrayList<>();
        adapter = new ComicImageAdapter(this, imageList);
        lvComicImages.setLayoutManager(new LinearLayoutManager(this));
        lvComicImages.setAdapter(adapter);

        // Nhận dữ liệu Intent
        bookId = getIntent().getIntExtra("BOOK_ID", -1);
        chapterNumber = getIntent().getIntExtra("CHAPTER_NUMBER", 1);

        if (bookId != -1 && chapterNumber > 0) {
            loadChapterContent(bookId, chapterNumber);
        } else {
            Toast.makeText(this, "Dữ liệu truyền vào không hợp lệ!", Toast.LENGTH_SHORT).show();
        }

        // Khởi tạo GestureDetector cho các sự kiện hai lần nhấn và cuộn
        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                toggleVisibility();
                return super.onDoubleTap(e);
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                if (distanceY > 0) {
                    hideUIElements();
                } else if (distanceY < 0) {
                    showUIElements();
                }
                return super.onScroll(e1, e2, distanceX, distanceY);
            }
        });

        lvComicImages.setOnTouchListener((v, event) -> {
            gestureDetector.onTouchEvent(event); // Kích hoạt GestureDetector
            return false; // Để RecyclerView vẫn xử lý sự kiện cuộn
        });


        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                toggleVisibility();
                return true; // Xử lý hoàn tất sự kiện
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                // Lướt lên -> Ẩn header và navigation
                if (distanceY > 10) { // Kiểm tra giá trị khoảng cách cuộn
                    hideUIElements();
                }
                // Lướt xuống -> Hiện header và navigation
                else if (distanceY < -10) {
                    showUIElements();
                }
                return super.onScroll(e1, e2, distanceX, distanceY);
            }
        });




        // Các sự kiện cho nút quay lại
        imvBack.setOnClickListener(v -> finish());

        // Sự kiện cho các nút điều hướng chương
        imvPrev.setOnClickListener(v -> {
            if (chapterNumber > 1) {
                chapterNumber--;
                loadChapterContent(bookId, chapterNumber);
            } else {
                Toast.makeText(this, "Đây là chương đầu tiên!", Toast.LENGTH_SHORT).show();
            }
        });

        imvNext.setOnClickListener(v -> {
            chapterNumber++;
            if (!loadChapterContent(bookId, chapterNumber)) {
                chapterNumber--; // Nếu chương tiếp không tồn tại, quay lại chương hiện tại
            }
        });

        imvListchap.setOnClickListener(v -> showChapterList());
    }

    private boolean loadChapterContent(int bookId, int chapterNumber) {
        Cursor chapterCursor = dbHelper.getReadableDatabase().query(
                DBHelper.TABLE_BOOK_CONTENT,
                new String[]{DBHelper.COLUMN_CHAPTER_TITLE, DBHelper.COLUMN_CONTENT},
                COLUMN_BOOK_ID + " = ? AND " + DBHelper.COLUMN_CHAPTER_NUMBER + " = ?",
                new String[]{String.valueOf(bookId), String.valueOf(chapterNumber)},
                null, null, null
        );

        if (chapterCursor != null && chapterCursor.moveToFirst()) {
            String chapterTitle = chapterCursor.getString(chapterCursor.getColumnIndexOrThrow(DBHelper.COLUMN_CHAPTER_TITLE));
            String contentPaths = chapterCursor.getString(chapterCursor.getColumnIndexOrThrow(DBHelper.COLUMN_CONTENT));

            tvChapterTitle.setText("Chương " + chapterNumber + ": " + chapterTitle);

            imageList.clear();
            String[] images = contentPaths.split(";");
            for (String path : images) {
                File imgFile = new File(path);
                if (imgFile.exists()) {
                    imageList.add(imgFile.getAbsolutePath());
                }
            }

            adapter.notifyDataSetChanged();
            chapterCursor.close();
            return true;
        } else {
            if (chapterCursor != null) chapterCursor.close();
            Toast.makeText(this, "Không tìm thấy chương!", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private void showChapterList() {
        // Inflate layout cho popup
        View popupView = LayoutInflater.from(this).inflate(R.layout.chapter_list_popup, null);
        RecyclerView rvChapters = popupView.findViewById(R.id.rvChapters);

        // Lấy danh sách chương từ database
        Cursor chapterCursor = dbHelper.getReadableDatabase().query(
                DBHelper.TABLE_BOOK_CONTENT,
                new String[]{DBHelper.COLUMN_CHAPTER_NUMBER, DBHelper.COLUMN_CHAPTER_TITLE},
                COLUMN_BOOK_ID + " = ?",
                new String[]{String.valueOf(bookId)},
                null, null, DBHelper.COLUMN_CHAPTER_NUMBER
        );

        // Khởi tạo adapter
        ChapterListAdapter chapterListAdapter = new ChapterListAdapter(this, chapterCursor, selectedChapterNumber -> {
            loadChapterContent(bookId, selectedChapterNumber); // Tải nội dung chương được chọn
            chapterPopup.dismiss(); // Đóng PopupWindow
        });

        // Thiết lập RecyclerView
        rvChapters.setLayoutManager(new LinearLayoutManager(this));
        rvChapters.setAdapter(chapterListAdapter);

        // Tạo PopupWindow
        chapterPopup = new PopupWindow(popupView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        chapterPopup.setAnimationStyle(R.style.PopupAnimation); // Thêm animation nếu cần
        chapterPopup.showAtLocation(findViewById(android.R.id.content), Gravity.BOTTOM, 0, 0);

        // Đảm bảo Cursor được đóng khi PopupWindow bị tắt
        chapterPopup.setOnDismissListener(() -> {
            if (chapterCursor != null && !chapterCursor.isClosed()) {
                chapterCursor.close();
            }
        });
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    private void toggleVisibility() {
        if (linearLayoutHeader.getVisibility() == View.VISIBLE) {
            hideUIElements();
        } else {
            showUIElements();
        }
    }

    private void hideUIElements() {
        linearLayoutHeader.setVisibility(View.GONE);
        btnNavigation.setVisibility(View.GONE);
    }

    private void showUIElements() {
        linearLayoutHeader.setVisibility(View.VISIBLE);
        btnNavigation.setVisibility(View.VISIBLE);
    }

    public void updateLastReadChapter(String userId, String bookId, int chapterNumber) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID, userId);
        values.put(COLUMN_BOOK_ID, bookId);
        values.put(COLUMN_READ_CHAPTERS, chapterNumber);

        // Cập nhật bản ghi nếu đã tồn tại, thêm mới nếu chưa có
        int rows = db.update(TABLE_READING_HISTORY, values,
                COLUMN_USER_ID + " = ? AND " + COLUMN_BOOK_ID + " = ?",
                new String[]{userId, bookId});

        if (rows == 0) {
            db.insert(TABLE_READING_HISTORY, null, values);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveLastReadChapter(); // Lưu chương hiện tại khi activity bị tạm dừng
    }

    private void saveLastReadChapter() {
        String userId = "1"; // Thay bằng cách lấy ID người dùng hiện tại nếu có cơ chế user login
        updateLastReadChapter(userId, String.valueOf(bookId), chapterNumber);
    }





}
