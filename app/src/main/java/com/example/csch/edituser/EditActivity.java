package com.example.csch.edituser;


import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.method.PasswordTransformationMethod;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.csch.R;
import com.example.csch.database.DBHelper;

import android.database.Cursor;

import java.io.ByteArrayOutputStream;

public class EditActivity extends AppCompatActivity {

    private EditText edtUsername, edtPhone;
    private ImageView imgAvatar, imgEditAvatar;
    private Button btnSave, btnChangePassword;
    private DBHelper dbHelper;
    private int userId;
    private static final int REQUEST_CAMERA = 1;
    private static final int REQUEST_GALLERY = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        if (checkSelfPermission(android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.CAMERA}, REQUEST_CAMERA);
        }


        // Ánh xạ UI
        edtUsername = findViewById(R.id.edtUsername);
        edtPhone = findViewById(R.id.edtPhone);
        imgAvatar = findViewById(R.id.imgAvatar);
        imgEditAvatar = findViewById(R.id.imgEditAvatar);
        btnSave = findViewById(R.id.btnSave);
        btnChangePassword = findViewById(R.id.btnChangePassword);

        dbHelper = new DBHelper(this);

        // Lấy userId từ Intent
        userId = getIntent().getIntExtra("userId", -1);
        if (userId == -1) {
            Toast.makeText(this, "Không tìm thấy thông tin người dùng!", Toast.LENGTH_SHORT).show();
            finish();
        }

        loadUserInfo();

        // Xử lý sự kiện cập nhật ảnh đại diện
        imgEditAvatar.setOnClickListener(v -> showImageOptionDialog());

        // Xử lý sự kiện lưu thông tin
        btnSave.setOnClickListener(v -> updateUserInfo());

        // Xử lý sự kiện thay đổi mật khẩu
        btnChangePassword.setOnClickListener(v -> showChangePasswordDialog());
    }

    private void loadUserInfo() {
        Cursor cursor = dbHelper.getUserById(userId);
        if (cursor != null && cursor.moveToFirst()) {
            edtUsername.setText(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_USERNAME)));
            edtPhone.setText(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_PHONE)));

            String avatarBase64 = cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_AVATAR));
            if (avatarBase64 != null && !avatarBase64.isEmpty()) {
                imgAvatar.setImageBitmap(base64ToBitmap(avatarBase64));
            } else {
                imgAvatar.setImageResource(R.drawable.ic_profile); // Ảnh mặc định
            }
            cursor.close();
        }
    }

    private void updateUserInfo() {
        String username = edtUsername.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();

        BitmapDrawable drawable = (BitmapDrawable) imgAvatar.getDrawable();
        String avatarBase64 = "";
        if (drawable != null) {
            Bitmap avatarBitmap = drawable.getBitmap();
            avatarBase64 = bitmapToBase64(avatarBitmap);
        }

        if (username.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        if (phone.length() != 10) {
            Toast.makeText(this, "Số điện thoại phải có 10 chữ số", Toast.LENGTH_SHORT).show();
            return;
        }

        int result = dbHelper.updateUserInfo(userId, username, phone, avatarBase64);
        if (result > 0) {
            Toast.makeText(this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Cập nhật thất bại!", Toast.LENGTH_SHORT).show();
        }
    }

    private void showChangePasswordDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_change_password, null);

        // Ánh xạ các View
        EditText edtOldPassword = dialogView.findViewById(R.id.edtOldPassword);
        EditText edtNewPassword = dialogView.findViewById(R.id.edtNewPassword);
        EditText edtConfirmPassword = dialogView.findViewById(R.id.edtConfirmPassword);
        ImageButton btnShowOldPassword = dialogView.findViewById(R.id.btnShowOldPassword);
        ImageButton btnShowNewPassword = dialogView.findViewById(R.id.btnShowNewPassword);
        ImageButton btnShowConfirmPassword = dialogView.findViewById(R.id.btnShowConfirmPassword);
        Button btnConfirmChange = dialogView.findViewById(R.id.btnConfirmChange);

        // Xử lý hiện/ẩn mật khẩu cho trường mật khẩu cũ
        btnShowOldPassword.setOnClickListener(v -> {
            if (edtOldPassword.getTransformationMethod() instanceof PasswordTransformationMethod) {
                edtOldPassword.setTransformationMethod(null); // Hiện mật khẩu
                btnShowOldPassword.setImageResource(R.drawable.ic_eye_closed); // Cập nhật icon
            } else {
                edtOldPassword.setTransformationMethod(new PasswordTransformationMethod()); // Ẩn mật khẩu
                btnShowOldPassword.setImageResource(R.drawable.ic_menu_view); // Cập nhật icon
            }
            edtOldPassword.setSelection(edtOldPassword.getText().length()); // Di chuyển con trỏ về cuối
        });

        // Xử lý hiện/ẩn mật khẩu cho trường mật khẩu mới
        btnShowNewPassword.setOnClickListener(v -> {
            if (edtNewPassword.getTransformationMethod() instanceof PasswordTransformationMethod) {
                edtNewPassword.setTransformationMethod(null); // Hiện mật khẩu
                btnShowNewPassword.setImageResource(R.drawable.ic_eye_closed); // Cập nhật icon
            } else {
                edtNewPassword.setTransformationMethod(new PasswordTransformationMethod()); // Ẩn mật khẩu
                btnShowNewPassword.setImageResource(R.drawable.ic_menu_view); // Cập nhật icon
            }
            edtNewPassword.setSelection(edtNewPassword.getText().length()); // Di chuyển con trỏ về cuối
        });

        // Xử lý hiện/ẩn mật khẩu cho trường xác nhận mật khẩu
        btnShowConfirmPassword.setOnClickListener(v -> {
            if (edtConfirmPassword.getTransformationMethod() instanceof PasswordTransformationMethod) {
                edtConfirmPassword.setTransformationMethod(null); // Hiện mật khẩu
                btnShowConfirmPassword.setImageResource(R.drawable.ic_eye_closed); // Cập nhật icon
            } else {
                edtConfirmPassword.setTransformationMethod(new PasswordTransformationMethod()); // Ẩn mật khẩu
                btnShowConfirmPassword.setImageResource(R.drawable.ic_menu_view); // Cập nhật icon
            }
            edtConfirmPassword.setSelection(edtConfirmPassword.getText().length()); // Di chuyển con trỏ về cuối
        });

        // Tạo và hiển thị Dialog
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .create();

        // Xử lý sự kiện khi nhấn nút "Lưu"
        btnConfirmChange.setOnClickListener(v -> {
            String oldPassword = edtOldPassword.getText().toString().trim();
            String newPassword = edtNewPassword.getText().toString().trim();
            String confirmPassword = edtConfirmPassword.getText().toString().trim();

            if (oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!newPassword.equals(confirmPassword)) {
                Toast.makeText(this, "Mật khẩu xác nhận không khớp", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!dbHelper.validateUserById(userId, oldPassword)) {
                Toast.makeText(this, "Mật khẩu cũ không chính xác", Toast.LENGTH_SHORT).show();
                return;
            }

            dbHelper.updatePassword(userId, newPassword);
            Toast.makeText(this, "Đổi mật khẩu thành công!", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        dialog.show();
    }




    private void showImageOptionDialog() {
        String[] options = {"Chụp ảnh mới", "Chọn ảnh từ thư viện"};
        new AlertDialog.Builder(this)
                .setTitle("Cập nhật ảnh đại diện")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(cameraIntent, REQUEST_CAMERA);
                    } else if (which == 1) {
                        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(galleryIntent, REQUEST_GALLERY);
                    }
                })
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CAMERA && data != null && data.getExtras() != null) {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                if (photo != null) {
                    imgAvatar.setImageBitmap(resizeBitmap(photo, 120, 120));
                }
            } else if (requestCode == REQUEST_GALLERY && data != null) {
                try {
                    Bitmap galleryImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
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
        return Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT);
    }

    private Bitmap base64ToBitmap(String base64Str) {
        byte[] decodedBytes = Base64.decode(base64Str, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    private Bitmap resizeBitmap(Bitmap originalBitmap, int width, int height) {
        return Bitmap.createScaledBitmap(originalBitmap, width, height, true);
    }

}
