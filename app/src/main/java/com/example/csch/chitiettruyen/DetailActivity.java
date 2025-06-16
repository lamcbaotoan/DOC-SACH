package com.example.csch.chitiettruyen;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.csch.R;
import com.example.csch.database.DBHelper;

import java.util.ArrayList;

public class DetailActivity extends AppCompatActivity {
    private Button btnReadFromStart, btnReadContinue;
    private ListView lvChapter;
    private TextView tvTitle, tvStatus, tvGroup, tvDescription, tvGenre, tvSeeMore;
    private ImageView imgCover, imv_Follw;
    private DBHelper dbHelper;
    private boolean isDescriptionExpanded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detail);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Ánh xạ các view
        btnReadFromStart = findViewById(R.id.btnReadFromStart);
        btnReadContinue = findViewById(R.id.btnReadContinue);
        lvChapter = findViewById(R.id.lv_Chapte);
        tvTitle = findViewById(R.id.tvTitle);
        tvStatus = findViewById(R.id.tvStatus);
        tvGroup = findViewById(R.id.tvGroup);
        tvDescription = findViewById(R.id.tvDescription);
        tvGenre = findViewById(R.id.tvGenre);
        imgCover = findViewById(R.id.imgCover);
        imv_Follw = findViewById(R.id.imv_Follw);
        tvSeeMore = findViewById(R.id.tvShowMore);


        // Lấy thông tin bookId từ Intent
        String bookId = getIntent().getStringExtra("BOOK_ID");

        // Khởi tạo DBHelper
        dbHelper = new DBHelper(this);

        // Lấy thông tin sách từ cơ sở dữ liệu và hiển thị
        if (bookId != null) {
            Cursor cursor = dbHelper.getBookDetails(bookId);
            if (cursor != null && cursor.moveToFirst()) {
                String title = cursor.getString(cursor.getColumnIndexOrThrow("book_name"));
                String status = cursor.getString(cursor.getColumnIndexOrThrow("status"));
                String group = cursor.getString(cursor.getColumnIndexOrThrow("translation_group"));
                String description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
                String genre = cursor.getString(cursor.getColumnIndexOrThrow("genre"));
                String coverImage = cursor.getString(cursor.getColumnIndexOrThrow("cover"));

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
                        imgCover.setImageResource(R.drawable.bia_truyen); // Ảnh mặc định
                        Toast.makeText(this, "Lỗi hiển thị ảnh bìa", Toast.LENGTH_SHORT).show();
                    }
                }

                cursor.close();
            } else {
                Toast.makeText(this, "Không tìm thấy thông tin truyện", Toast.LENGTH_SHORT).show();
            }
        }

        // Toggle "See more" functionality
        tvSeeMore.setOnClickListener(v -> toggleDescription());

        // Xử lý sự kiện cho các nút
        btnReadFromStart.setOnClickListener(v -> {
            if (bookId != null) {
                Intent intent = new Intent(DetailActivity.this, ReadActivity.class);
                intent.putExtra("BOOK_ID", Integer.parseInt(bookId));
                intent.putExtra("CHAPTER_NUMBER", 1); // Đọc từ chương 1
                startActivity(intent);
            } else {
                Toast.makeText(this, "Không xác định được ID sách", Toast.LENGTH_SHORT).show();
            }
        });

        lvChapter.setOnItemClickListener((parent, view, position, id) -> {
            if (bookId != null) {
                Chapter selectedChapter = (Chapter) parent.getItemAtPosition(position);
                int chapterNumber = selectedChapter.getChapterNumber();

                Intent intent = new Intent(DetailActivity.this, ReadActivity.class);
                intent.putExtra("BOOK_ID", Integer.parseInt(bookId));
                intent.putExtra("CHAPTER_NUMBER", chapterNumber);
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
                    Intent intent = new Intent(DetailActivity.this, ReadActivity.class);
                    intent.putExtra("BOOK_ID", Integer.parseInt(bookId));
                    intent.putExtra("CHAPTER_NUMBER", lastReadChapter);
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "Chưa có lịch sử đọc cho sách này!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Không xác định được ID sách", Toast.LENGTH_SHORT).show();
            }
        });

        // Hiển thị danh sách chương
        displayChapterList(bookId);

        // Lấy userId từ Intent (giả sử userId được truyền qua Intent từ LoginActivity)
        String userId = getIntent().getStringExtra("USER_ID");

        // Kiểm tra trạng thái theo dõi và cập nhật giao diện
        updateFollowStatus(userId, bookId);

        // Xử lý sự kiện nhấn vào imvFollow
        imv_Follw.setOnClickListener(v -> {
            if (userId != null && bookId != null) {
                boolean isFollowed = dbHelper.isBookFollowed(userId, bookId);
                if (isFollowed) {
                    // Nếu đã follow, thì unfollow
                    dbHelper.unfollowBook(userId, bookId);
                    imv_Follw.setColorFilter(getResources().getColor(R.color.black)); // Chuyển sang màu đen
                    showDialog("Đã hủy theo dõi truyện");
                } else {
                    // Nếu chưa follow, thì follow
                    dbHelper.followBook(userId, bookId);
                    imv_Follw.setColorFilter(getResources().getColor(R.color.red)); // Chuyển sang màu đỏ
                    showDialog("Đã theo dõi truyện");
                }
            } else {
                Toast.makeText(this, "Không xác định được thông tin người dùng hoặc sách!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayChapterList(String bookId) {
        ArrayList<Chapter> chapterList = new ArrayList<>();
        ArrayList<String> chapterTitles = dbHelper.getChaptersByBookId(bookId);

        for (int i = 0; i < chapterTitles.size(); i++) {
            chapterList.add(new Chapter(i + 1, chapterTitles.get(i), 0));
        }

        if (!chapterList.isEmpty()) {
            DetailAdapter adapter = new DetailAdapter(this, chapterList);
            lvChapter.setAdapter(adapter);
        } else {
            Toast.makeText(this, "Không có chương nào", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateFollowStatus(String userId, String bookId) {
        if (userId != null && bookId != null) {
            boolean isFollowed = dbHelper.isBookFollowed(userId, bookId);
            if (isFollowed) {
                imv_Follw.setColorFilter(getResources().getColor(R.color.red)); // Màu đỏ nếu đã follow
            } else {
                imv_Follw.setColorFilter(getResources().getColor(R.color.black)); // Màu đen nếu chưa follow
            }
        }
    }

    private void showDialog(String message) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    private void toggleDescription() {
        if (isDescriptionExpanded) {
            tvDescription.setMaxLines(3); // Truncate the description after 3 lines
            tvSeeMore.setText("Xem thêm");
        } else {
            tvDescription.setMaxLines(Integer.MAX_VALUE); // Show the full description
            tvSeeMore.setText("Ẩn bớt");
        }
        isDescriptionExpanded = !isDescriptionExpanded;
    }
}
