package com.example.csch.chitiettruyen;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.csch.R;
import com.example.csch.database.DBHelper;

import java.util.ArrayList;

public class DetailAdminActivity extends AppCompatActivity {
    private Button btnReadFromStart, btnReadContinue;
    private ListView lvChapter;
    private TextView tvTitle, tvStatus, tvGroup, tvDescription, tvGenre, tvShow;
    private ImageView imgCover;
    private DBHelper dbHelper;
    private ImageButton btAddChapter, btEditChapter;
    private boolean isDescriptionExpanded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detail_admin);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Lấy thông tin bookId từ Intent
        String bookId = getIntent().getStringExtra("BOOK_ID");

        // Ánh xạ các view
        btAddChapter = findViewById(R.id.btAddChapter);
        btnReadFromStart = findViewById(R.id.btnReadFromStart);
        btnReadContinue = findViewById(R.id.btnReadContinue);
        lvChapter = findViewById(R.id.lv_Chapte);
        tvTitle = findViewById(R.id.tvTitle);
        tvStatus = findViewById(R.id.tvStatus);
        tvGroup = findViewById(R.id.tvGroup);
        tvDescription = findViewById(R.id.tvDescription);
        tvGenre = findViewById(R.id.tvGenre);
        imgCover = findViewById(R.id.imgCover);
        tvShow = findViewById(R.id.tvShow);
        btEditChapter = findViewById(R.id.btEditChapter);

        // Khởi tạo DBHelper
        dbHelper = new DBHelper(this);

        // Lấy thông tin sách từ cơ sở dữ liệu và hiển thị
        if (bookId != null) {
            Cursor cursor = dbHelper.getBookDetails(bookId);
            if (cursor != null && cursor.moveToFirst()) {
                // Lấy dữ liệu từ Cursor
                String title = cursor.getString(cursor.getColumnIndexOrThrow("book_name"));
                String status = cursor.getString(cursor.getColumnIndexOrThrow("status"));
                String group = cursor.getString(cursor.getColumnIndexOrThrow("translation_group"));
                String description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
                String genre = cursor.getString(cursor.getColumnIndexOrThrow("genre"));
                String coverImage = cursor.getString(cursor.getColumnIndexOrThrow("cover"));


                // Hiển thị thông tin
                tvTitle.setText(title);
                tvStatus.setText(status);
                tvGroup.setText(group);
                tvDescription.setText(description);

                // Update the tvGenre display in the onCreate method
                String[] genreArray = genre.split(",");  // Assuming genres are separated by commas
                StringBuilder genreWithSeparator = new StringBuilder();
                for (int i = 0; i < genreArray.length; i++) {
                    genreWithSeparator.append(genreArray[i].trim());
                    if (i < genreArray.length - 1) {
                        genreWithSeparator.append(" | "); // Add separator between genres
                    }
                }

                tvGenre.setText(genreWithSeparator.toString());
                // Set background color for tvGenre
                tvGenre.setBackgroundColor(getResources().getColor(R.color.light_yellow)); // Set the background color




                // Hiển thị ảnh bìa
                if (coverImage != null) {
                    try {
                        byte[] decodedString = Base64.decode(coverImage, Base64.DEFAULT);
                        Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                        imgCover.setImageBitmap(decodedBitmap);
                    } catch (IllegalArgumentException e) {
                        // Nếu dữ liệu Base64 không hợp lệ
                        imgCover.setImageResource(R.drawable.bia_truyen); // Ảnh mặc định
                        Toast.makeText(this, "Lỗi hiển thị ảnh bìa", Toast.LENGTH_SHORT).show();
                    }
                }

                cursor.close();
            } else {
                Toast.makeText(this, "Không tìm thấy thông tin truyện", Toast.LENGTH_SHORT).show();
            }
        }

        // Xử lý sự kiện cho các nút
        // Truyền BOOK_ID và CHAPTER_NUMBER dưới dạng Integer
        btAddChapter.setOnClickListener(v -> {
            if (bookId != null) {
                Intent intent = new Intent(DetailAdminActivity.this, AddChapterActivity.class);
                intent.putExtra("BOOK_ID", bookId);
                startActivity(intent);
            } else {
                Toast.makeText(DetailAdminActivity.this, "Không xác định được ID sách", Toast.LENGTH_SHORT).show();
            }
        });

        // Truyền BOOK_ID và CHAPTER_NUMBER dưới dạng Integer
        btEditChapter.setOnClickListener(v -> {
            if (bookId != null) {
                Intent intent = new Intent(DetailAdminActivity.this, EditChapterActivity.class);
                intent.putExtra("BOOK_ID", Integer.parseInt(bookId));  // Chuyển BOOK_ID thành Integer
                intent.putExtra("CHAPTER_NUMBER", 1);  // CHAPTER_NUMBER luôn là 1 khi đọc từ đầu
                startActivity(intent);
            } else {
                Toast.makeText(this, "Không xác định được ID sách", Toast.LENGTH_SHORT).show();
            }
        });

        btnReadFromStart.setOnClickListener(v -> {
            if (bookId != null) {
                Intent intent = new Intent(DetailAdminActivity.this, ReadActivity.class);
                intent.putExtra("BOOK_ID", Integer.parseInt(bookId));  // Chuyển BOOK_ID thành Integer
                intent.putExtra("CHAPTER_NUMBER", 1);  // CHAPTER_NUMBER luôn là 1 khi đọc từ đầu
                startActivity(intent);
            } else {
                Toast.makeText(this, "Không xác định được ID sách", Toast.LENGTH_SHORT).show();
            }
        });

        lvChapter.setOnItemClickListener((parent, view, position, id) -> {
            if (bookId != null) {
                Chapter selectedChapter = (Chapter) parent.getItemAtPosition(position);
                int chapterNumber = selectedChapter.getChapterNumber();

                Intent intent = new Intent(DetailAdminActivity.this, ReadActivity.class);
                intent.putExtra("BOOK_ID", Integer.parseInt(bookId));  // Chuyển BOOK_ID thành Integer
                intent.putExtra("CHAPTER_NUMBER", chapterNumber);  // Truyền CHAPTER_NUMBER
                startActivity(intent);
            } else {
                Toast.makeText(this, "Không xác định được ID sách", Toast.LENGTH_SHORT).show();
            }
        });

        btnReadContinue.setOnClickListener(v -> {
            if (bookId != null) {
                String userId = "1"; // Thay bằng ID người dùng thật nếu có hệ thống đăng nhập
                int lastReadChapter = dbHelper.getLastReadChapter(userId, bookId);

                if (lastReadChapter > 0) {
                    // Nếu đã có lịch sử đọc, chuyển sang ReadActivity
                    Intent intent = new Intent(DetailAdminActivity.this, ReadActivity.class);
                    intent.putExtra("BOOK_ID", Integer.parseInt(bookId));  // BOOK_ID
                    intent.putExtra("CHAPTER_NUMBER", lastReadChapter);  // CHAPTER_NUMBER đã đọc lần trước
                    startActivity(intent);
                } else {
                    // Nếu chưa có lịch sử đọc, hiện Dialog
                    Toast.makeText(this, "Chưa có lịch sử đọc cho sách này!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Không xác định được ID sách", Toast.LENGTH_SHORT).show();
            }
        });

        // Handle "See more" toggle
        tvShow.setOnClickListener(v -> toggleDescriptionVisibility());


        // Hiển thị danh sách chương
        displayChapterList(bookId);
    }

    // Lấy danh sách chương từ database và hiển thị trong ListView
    private void displayChapterList(String bookId) {
        ArrayList<Chapter> chapterList = new ArrayList<>();

        // Lấy danh sách chương từ database
        ArrayList<String> chapterTitles = dbHelper.getChaptersByBookId(bookId);
        for (int i = 0; i < chapterTitles.size(); i++) {
            chapterList.add(new Chapter(i + 1, chapterTitles.get(i), 0));
        }

        if (!chapterList.isEmpty()) {
            // Sử dụng DetailAdapter để hiển thị danh sách chương
            DetailAdapter adapter = new DetailAdapter(this, chapterList);
            lvChapter.setAdapter(adapter);
        } else {
            Toast.makeText(this, "Không có chương nào", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Lấy lại thông tin bookId từ Intent
        String bookId = getIntent().getStringExtra("BOOK_ID");

        // Kiểm tra bookId không null và tải lại danh sách chương
        if (bookId != null) {
            displayChapterList(bookId); // Cập nhật danh sách chương
            Toast.makeText(this, "Danh sách chương đã được cập nhật.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Không xác định được ID sách.", Toast.LENGTH_SHORT).show();
        }
    }

    private void toggleDescriptionVisibility() {
        if (isDescriptionExpanded) {
            // Hide the description and change the button text
            tvDescription.setMaxLines(3); // Limit to 3 lines
            tvShow.setText("Xem thêm"); // Change text to "See More"
        } else {
            // Show the full description and change the button text
            tvDescription.setMaxLines(Integer.MAX_VALUE); // Show full text
            tvShow.setText("Thu gọn"); // Change text to "Show Less"
        }

        // Toggle the state
        isDescriptionExpanded = !isDescriptionExpanded;
    }
}