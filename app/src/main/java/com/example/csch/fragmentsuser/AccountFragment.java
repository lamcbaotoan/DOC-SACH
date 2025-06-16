package com.example.csch.fragmentsuser;

import static android.content.Context.MODE_PRIVATE;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.csch.R;
import com.example.csch.database.DBHelper;
import com.example.csch.edituser.EditActivity;
import com.example.csch.login_register.LoginActivity;
import com.google.android.material.imageview.ShapeableImageView;

public class AccountFragment extends Fragment {

    private TextView userName, userPhone, userRole;
    private ShapeableImageView profileImage;
    private Button btnLogout,manageInfoButton;
    private DBHelper dbHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        // Ánh xạ các thành phần UI
        profileImage = view.findViewById(R.id.imgAvatar);
        userName = view.findViewById(R.id.txtUsername);
        userPhone = view.findViewById(R.id.txtPhoneNumber);
        userRole = view.findViewById(R.id.txtRole);
        btnLogout = view.findViewById(R.id.btn_logout);
        manageInfoButton = view.findViewById(R.id.manageInfoButton);


        dbHelper = new DBHelper(getContext());

        // Lấy thông tin từ Bundle được truyền từ MainActivity
        Bundle bundle = getArguments();
        if (bundle != null) {
            String username = bundle.getString("username");
            if (username != null) {
                displayUserInfo(username);
            }
        }


        // Lấy userId từ SharedPreferences
        SharedPreferences preferences = requireContext().getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        int userId = preferences.getInt("userId", -1); // Giá trị mặc định -1 nếu không tìm thấy


        // Xử lý sự kiện khi người dùng nhấn nút "Quản lý thông tin"
        manageInfoButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), EditActivity.class);
            intent.putExtra("userId", userId); // Truyền userId qua Intent
            startActivity(intent);
        });




        // Xử lý đăng xuất
        btnLogout.setOnClickListener(v -> handleLogout());

        return view;
    }

    // Hiển thị thông tin người dùng
    private void displayUserInfo(String username) {
        Cursor cursor = dbHelper.getUserByUsername(username);
        if (cursor != null && cursor.moveToFirst()) {
            int phoneIndex = cursor.getColumnIndex(DBHelper.COLUMN_PHONE);
            int roleIndex = cursor.getColumnIndex(DBHelper.COLUMN_ROLE);
            int avatarIndex = cursor.getColumnIndex(DBHelper.COLUMN_AVATAR);

            String phone = (phoneIndex >= 0) ? cursor.getString(phoneIndex) : null;
            String role = (roleIndex >= 0) ? cursor.getString(roleIndex) : null;
            String avatarBase64 = (avatarIndex >= 0) ? cursor.getString(avatarIndex) : null;

            userName.setText(username);
            userPhone.setText(phone != null ? phone : "Không có số điện thoại");
            userRole.setText(role != null ? role : "Người dùng");

            // Display avatar
            if (avatarBase64 != null && !avatarBase64.isEmpty()) {
                Bitmap avatarBitmap = base64ToBitmap(avatarBase64);
                profileImage.setImageBitmap(avatarBitmap);
            } else {
                profileImage.setImageResource(R.drawable.ic_profile); // Default avatar
            }

            cursor.close();
        } else {
            userName.setText("Khách");
            userPhone.setText("Không có số điện thoại");
            userRole.setText("Người dùng");
            profileImage.setImageResource(R.drawable.ic_profile); // Default avatar
        }
    }



    // Xử lý đăng xuất
    private void handleLogout() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Đăng xuất")
                .setMessage("Bạn có chắc chắn muốn đăng xuất?")
                .setPositiveButton("Có", (dialog, which) -> {
                    SharedPreferences preferences = requireActivity().getSharedPreferences("LoginPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.clear(); // Xóa toàn bộ thông tin đăng nhập
                    editor.apply();

                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                    requireActivity().finish();
                })
                .setNegativeButton("Không", null)
                .show();
    }

    private Bitmap base64ToBitmap(String base64Str) {
        byte[] decodedBytes = android.util.Base64.decode(base64Str, android.util.Base64.DEFAULT);
        return android.graphics.BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

}

