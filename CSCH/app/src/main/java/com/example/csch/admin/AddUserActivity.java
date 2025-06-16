package com.example.csch.admin;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.method.PasswordTransformationMethod;
import android.util.Base64;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.csch.R;
import com.example.csch.database.DBHelper;

import java.io.ByteArrayOutputStream;

public class AddUserActivity extends AppCompatActivity {
    private EditText etUsername, etPassword, etPhone;
    private ImageView imgAvatar, imvEditavt;
    private Button btnSave;
    private DBHelper dbHelper;
    private ImageButton ShowPassword;
    private static final int REQUEST_CAMERA = 1;
    private static final int REQUEST_GALLERY = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_user);

        // Thiết lập giao diện
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.lv_Chapte), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Liên kết giao diện
        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        etPhone = findViewById(R.id.et_phone);
        imgAvatar = findViewById(R.id.img_avatar);
        imvEditavt = findViewById(R.id.imv_editavtar);
        btnSave = findViewById(R.id.btn_save);
        ShowPassword = findViewById(R.id.ShowPassword);
        dbHelper = new DBHelper(this);

        BitmapDrawable drawable = (BitmapDrawable) imgAvatar.getDrawable();
        Bitmap bitmap = drawable.getBitmap();
        String avatar = bitmapToBase64(bitmap);  // Chuyển thành Base64


        // Sự kiện hiển thị mật khẩu
        ShowPassword.setOnClickListener(v -> {
            if (etPassword.getTransformationMethod() instanceof PasswordTransformationMethod) {
                etPassword.setTransformationMethod(null);
                ShowPassword.setImageResource(R.drawable.ic_menu_view);
            } else {
                etPassword.setTransformationMethod(new PasswordTransformationMethod());
                ShowPassword.setImageResource(R.drawable.ic_eye_closed);
            }
            etPassword.setSelection(etPassword.getText().length());
        });

        // Xử lý nút chỉnh sửa ảnh đại diện
        imvEditavt.setOnClickListener(v -> showImageOptionDialog());

        // Xử lý thêm người dùng
        btnSave.setOnClickListener(v -> addUser());

        // Yêu cầu quyền
        requestPermissions();
    }

    private void showImageOptionDialog() {
        String[] options = {"Chụp ảnh mới", "Chọn ảnh từ thư viện"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Chọn ảnh đại diện")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        openCamera();
                    } else if (which == 1) {
                        openGallery();
                    }
                });
        builder.create().show();
    }

    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, REQUEST_CAMERA);
    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, REQUEST_GALLERY);
    }

    private void requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                    checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                    checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(new String[]{
                        Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                }, 100);
            }
        }
    }


    private void addUser() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();

        BitmapDrawable drawable = (BitmapDrawable) imgAvatar.getDrawable();
        Bitmap avatarBitmap = drawable.getBitmap();
        String avatar = bitmapToBase64(avatarBitmap); // Chuyển ảnh thành Base64


        // Kiểm tra thông tin đầu vào
        if (username.isEmpty() || phone.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!phone.matches("\\d{10}")) {
            Toast.makeText(this, "Số điện thoại phải có đúng 10 chữ số!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.length() < 6) {
            Toast.makeText(this, "Mật khẩu phải có ít nhất 6 ký tự!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (isUsernameExists(username)) {
            Toast.makeText(this, "Tên người dùng đã tồn tại!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (isPhoneNumberExists(phone)) {
            Toast.makeText(this, "Số điện thoại đã tồn tại!", Toast.LENGTH_SHORT).show();
            return;
        }

        long result = dbHelper.addUser(username, password, phone, avatar, "reader");
        if (result != -1) {
            Toast.makeText(this, "Tạo mới người dùng thành công", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Tạo mới thất bại", Toast.LENGTH_SHORT).show();
        }
    }



    private boolean isUsernameExists(String username) {
        Cursor cursor = dbHelper.getUserByUsername(username);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.close();
            return true;
        }
        if (cursor != null) cursor.close();
        return false;
    }

    private boolean isPhoneNumberExists(String phoneNumber) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DBHelper.TABLE_USERS + " WHERE " + DBHelper.COLUMN_PHONE + " = ?", new String[]{phoneNumber});
        if (cursor != null && cursor.getCount() > 0) {
            cursor.close();
            return true;
        }
        if (cursor != null) cursor.close();
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CAMERA && data != null) {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                Bitmap resizedPhoto = resizeBitmap(photo, 120, 120); // Kích thước 120 x 120
                imgAvatar.setImageBitmap(resizedPhoto);
            } else if (requestCode == REQUEST_GALLERY && data != null) {
                Uri selectedImage = data.getData();
                try {
                    Bitmap galleryImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                    Bitmap resizedGalleryImage = resizeBitmap(galleryImage, 120, 120); // Kích thước 120 x 120
                    imgAvatar.setImageBitmap(resizedGalleryImage);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        byte[] byteArray = outputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }


    private Bitmap resizeBitmap(Bitmap bitmap, int width, int height) {
        return Bitmap.createScaledBitmap(bitmap, width, height, true);
    }

    private Bitmap base64ToBitmap(String base64Str) {
        byte[] decodedBytes = Base64.decode(base64Str, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }
}
