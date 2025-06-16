package com.example.csch.chitiettruyen;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.csch.R;
import com.example.csch.database.DBHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class AddChapterActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_PICK_IMAGES = 100;

    private DBHelper dbHelper;
    private EditText edtChapterIndex, edtChapterTitle;
    private RecyclerView lvContent;
    private AddChapterAdapter chapterAdapter; // Đổi từ ChapterAdapter sang AddChapterAdapter
    private Button btnAddChapter, btnAddImage;
    private String bookId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_chapter);

        // Điều chỉnh insets hệ thống
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Lấy thông tin BOOK_ID từ Intent
        bookId = getIntent().getStringExtra("BOOK_ID");

        // Khởi tạo các view
        dbHelper = new DBHelper(this);
        edtChapterIndex = findViewById(R.id.edtChapterIndex);
        edtChapterTitle = findViewById(R.id.edtChapterTitle);
        lvContent = findViewById(R.id.lvContent);
        btnAddChapter = findViewById(R.id.btnAddChapter);
        btnAddImage = findViewById(R.id.btnAddImage);

        // Set up RecyclerView
        chapterAdapter = new AddChapterAdapter(); // Đổi sang AddChapterAdapter
        lvContent.setLayoutManager(new LinearLayoutManager(this));
        lvContent.setAdapter(chapterAdapter);

        // Xử lý sự kiện khi nhấn nút thêm ảnh
        btnAddImage.setOnClickListener(v -> openImagePicker());

        // Xử lý sự kiện khi nhấn nút thêm chương
        btnAddChapter.setOnClickListener(v -> addChapter());

        chapterAdapter.setOnImageClickListener(position -> {
            // Hiển thị Dialog với các lựa chọn
            new AlertDialog.Builder(this)
                    .setTitle("Tùy chọn ảnh")
                    .setMessage("Bạn muốn làm gì?")
                    .setPositiveButton("Sửa ảnh", (dialog, which) -> openImagePickerForEdit(position))
                    .setNegativeButton("Xóa ảnh", (dialog, which) -> {
                        chapterAdapter.getImages().remove(position);
                        chapterAdapter.notifyItemRemoved(position);
                    })
                    .show();
        });

    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(intent, REQUEST_CODE_PICK_IMAGES);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == REQUEST_CODE_PICK_IMAGES) {
                // Thêm ảnh mới
                List<Uri> selectedImages = new ArrayList<>();
                if (data.getClipData() != null) {
                    int count = data.getClipData().getItemCount();
                    for (int i = 0; i < count; i++) {
                        selectedImages.add(data.getClipData().getItemAt(i).getUri());
                    }
                } else if (data.getData() != null) {
                    selectedImages.add(data.getData());
                }
                chapterAdapter.setImages(selectedImages);
            } else if (requestCode >= REQUEST_CODE_PICK_IMAGES) {
                // Sửa ảnh tại vị trí
                int position = requestCode - REQUEST_CODE_PICK_IMAGES;
                Uri newImageUri = data.getData();
                if (newImageUri != null && position >= 0 && position < chapterAdapter.getImages().size()) {
                    // Cập nhật URI ảnh tại vị trí
                    chapterAdapter.getImages().set(position, newImageUri);
                    chapterAdapter.notifyItemChanged(position);
                }
            }
        }
    }


    private void addChapter() {
        try {
            int chapterNumber = Integer.parseInt(edtChapterIndex.getText().toString());
            String chapterTitle = edtChapterTitle.getText().toString();
            List<Uri> selectedImages = chapterAdapter.getImages();

            if (!isValidChapterIndex(bookId, chapterNumber)) {
                Toast.makeText(this, "Số chương không hợp lệ!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (chapterTitle.trim().isEmpty()) {
                Toast.makeText(this, "Tên chương không được để trống!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Lưu ảnh vào bộ nhớ và lấy đường dẫn
            List<String> imagePaths = new ArrayList<>();
            for (Uri imageUri : selectedImages) {
                String imagePath = saveImageToStorage(bookId, chapterNumber, imageUri);
                if (imagePath != null) {
                    imagePaths.add(imagePath);
                }
            }

            // Chuyển danh sách đường dẫn thành chuỗi
            String chapterContent = String.join(";", imagePaths);

            // Lưu vào database
            boolean isInserted = dbHelper.addChapter(bookId, chapterNumber, chapterTitle, chapterContent);
            if (isInserted) {
                Toast.makeText(this, "Lưu chương thành công!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Lưu chương thất bại!", Toast.LENGTH_SHORT).show();
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Vui lòng nhập số chương hợp lệ!", Toast.LENGTH_SHORT).show();
        }
    }






    private boolean isValidChapterIndex(String bookId, int chapterIndex) {
        int chapterNumber = dbHelper.getMaxChapterIndex(bookId);
        return chapterIndex == chapterNumber + 1; // Chỉ được lớn hơn chương hiện tại 1
    }

    private void openImagePickerForEdit(int position) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        // Thêm mã yêu cầu với vị trí ảnh
        startActivityForResult(intent, REQUEST_CODE_PICK_IMAGES + position);
    }

    private String imagesToBase64(List<Uri> imageUris) {
        StringBuilder base64Images = new StringBuilder();
        for (Uri uri : imageUris) {
            String base64 = uriToBase64(uri);
            if (base64 != null) {
                if (base64Images.length() > 0) {
                    base64Images.append(";"); // Ký tự phân tách
                }
                base64Images.append(base64);
            }
        }
        return base64Images.toString();
    }

    private String uriToBase64(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            byte[] bytes = new byte[inputStream.available()];
            inputStream.read(bytes);
            inputStream.close();
            return android.util.Base64.encodeToString(bytes, android.util.Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String imagesToUriString(List<Uri> imageUris) {
        StringBuilder uriString = new StringBuilder();
        for (Uri uri : imageUris) {
            if (uriString.length() > 0) {
                uriString.append(";"); // Ký tự phân tách URI
            }
            uriString.append(uri.toString());
        }
        return uriString.toString();
    }

    private String saveImageToStorage(String bookId, int chapterNumber, Uri imageUri) {
        try {
            // Tạo thư mục lưu ảnh
            File chapterDir = new File(getExternalFilesDir(null), bookId + "/" + chapterNumber);
            if (!chapterDir.exists()) {
                chapterDir.mkdirs(); // Tạo thư mục nếu chưa tồn tại
            }

            // Đọc dữ liệu ảnh từ URI
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            String fileName = "image_" + System.currentTimeMillis() + ".jpg"; // Đặt tên file
            File imageFile = new File(chapterDir, fileName);
            FileOutputStream outputStream = new FileOutputStream(imageFile);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            inputStream.close();
            outputStream.close();

            return imageFile.getAbsolutePath(); // Trả về đường dẫn ảnh
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }



}
