package com.example.csch.admin;



import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
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

public class EditUserActivity extends AppCompatActivity {
    private EditText etUsername, etPassword, etPhone;
    private ImageView imgAvatar, imv_editavtar;
    private Button btnSave;
    private DBHelper dbHelper;
    private int userId;
    private ImageButton btnShowPass;
    public static final int REQUEST_CAMERA = 1;
    public static final int REQUEST_GALLERY = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_user);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.lv_Chapte), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize views
        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        etPhone = findViewById(R.id.et_phone);
        imgAvatar = findViewById(R.id.img_avatar);
        btnSave = findViewById(R.id.btn_save);
        dbHelper = new DBHelper(this);
        btnShowPass = findViewById(R.id.btnShowPass);
        imv_editavtar = findViewById(R.id.imv_editavtar);

        // Receive data from Intent
        Intent intent = getIntent();
        userId = intent.getIntExtra("user_id", -1);
        String username = intent.getStringExtra("username");
        String password = intent.getStringExtra("password");
        String phone = intent.getStringExtra("phone");
        String avatarBase64 = intent.getStringExtra("avatar");

        // Assign data to views
        etUsername.setText(username);
        etPassword.setText(password);
        etPhone.setText(phone);

        if (avatarBase64 != null && !avatarBase64.isEmpty()) {
            Bitmap avatarBitmap = base64ToBitmap(avatarBase64);
            imgAvatar.setImageBitmap(avatarBitmap);
        } else {
            imgAvatar.setImageResource(R.drawable.ic_profile); // Default image
        }

        // Handle show/hide password toggle
        btnShowPass.setOnClickListener(v -> {
            if (etPassword.getTransformationMethod() instanceof PasswordTransformationMethod) {
                etPassword.setTransformationMethod(null); // Show password
                btnShowPass.setImageResource(R.drawable.ic_menu_view); // Update icon
            } else {
                etPassword.setTransformationMethod(new PasswordTransformationMethod()); // Hide password
                btnShowPass.setImageResource(R.drawable.ic_eye_closed); // Update icon
            }
            etPassword.setSelection(etPassword.getText().length()); // Move cursor to end
        });

        // Handle avatar edit button
        imv_editavtar.setOnClickListener(v -> showImageOptionDialog());

        // Save button click listener
        btnSave.setOnClickListener(v -> updateUser());

        // Request permissions
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

    private void updateUser() {
        String updatedUsername = etUsername.getText().toString().trim();
        String updatedPassword = etPassword.getText().toString().trim();
        String updatedPhone = etPhone.getText().toString().trim();

        BitmapDrawable drawable = (BitmapDrawable) imgAvatar.getDrawable();
        Bitmap avatarBitmap = drawable.getBitmap();
        String avatar = bitmapToBase64(avatarBitmap); // Convert image to Base64

        if (updatedUsername.isEmpty() || updatedPassword.isEmpty() || updatedPhone.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!android.util.Patterns.PHONE.matcher(updatedPhone).matches() || updatedPhone.length() != 10) {
            Toast.makeText(this, "Số điện thoại phải hợp lệ và có đúng 10 chữ số", Toast.LENGTH_SHORT).show();
            return;
        }

        if (updatedPassword.length() < 6) {
            Toast.makeText(this, "Mật khẩu phải có ít nhất 6 ký tự", Toast.LENGTH_SHORT).show();
            return;
        }

        int result = dbHelper.updateUser(userId, updatedUsername, updatedPassword, updatedPhone, avatar, "reader");
        if (result > 0) {
            Toast.makeText(this, "Cập nhật người dùng thành công", Toast.LENGTH_SHORT).show();
            finish(); // Return to the previous activity
        } else {
            Toast.makeText(this, "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CAMERA && data != null) {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                imgAvatar.setImageBitmap(resizeBitmap(photo, 120, 120));
            } else if (requestCode == REQUEST_GALLERY && data != null) {
                Uri selectedImage = data.getData();
                try {
                    Bitmap galleryImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                    imgAvatar.setImageBitmap(resizeBitmap(galleryImage, 120, 120));
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
